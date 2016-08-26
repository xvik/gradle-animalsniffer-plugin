package ru.vyarus.gradle.plugin.animalsniffer

import org.apache.tools.ant.BuildException
import org.apache.tools.ant.BuildListener
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.project.IsolatedAntBuilder
import org.gradle.api.reporting.Report
import org.gradle.api.reporting.Reporting
import org.gradle.api.tasks.*
import org.gradle.internal.classpath.ClassPath
import org.gradle.internal.classpath.DefaultClassPath
import org.gradle.internal.reflect.Instantiator
import ru.vyarus.gradle.plugin.animalsniffer.report.AnimalSnifferReports
import ru.vyarus.gradle.plugin.animalsniffer.report.AnimalSnifferReportsImpl
import ru.vyarus.gradle.plugin.animalsniffer.report.ReportCollector

import javax.inject.Inject
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

/**
 * AnimalSniffer task is created for each registered source set.
 * <p>
 * Task is SourceTask, but with root in compile classes dir of current source set. This means that include/exclude
 * may be used, but they wil be applied to compiled classes and not sources.
 *
 * @author Vyacheslav Rusakov
 * @since 14.12.2015
 */
@SuppressWarnings('UnnecessaryGetter')
class AnimalSniffer extends SourceTask implements VerificationTask, Reporting<AnimalSnifferReports> {

    /**
     * The class path containing the Animal Sniffer library to be used.
     */
    @InputFiles
    FileCollection animalsnifferClasspath

    /**
     * Signature files used for checks.
     */
    @InputFiles
    FileCollection animalsnifferSignatures

    /**
     * Classpath used for compilation
     */
    @InputFiles
    FileCollection classpath

    /**
     * Classes, required only for task execution after actual compilation
     */
    @SkipWhenEmpty
    @InputFiles
    FileCollection classes

    /**
     * Source directories
     */
    @Input
    SourceDirectorySet sourcesDirs

    /**
     * Annotation class name to avoid check
     */
    @Input
    @Optional
    String annotation

    /**
     * Whether or not the build should break when the verifications performed by this task fail.
     */
    @Console
    boolean ignoreFailures

    @Nested
    private final AnimalSnifferReportsImpl reports

    @SuppressWarnings('ThisReferenceEscapesConstructor')
    AnimalSniffer() {
        reports = instantiator.newInstance(AnimalSnifferReportsImpl, this)
    }

    @Inject
    Instantiator getInstantiator() {
        throw new UnsupportedOperationException()
    }

    @Inject
    IsolatedAntBuilder getAntBuilder() {
        throw new UnsupportedOperationException()
    }

    @TaskAction
    @SuppressWarnings('CatchException')
    void run() {
        ClassPath animalsnifferClasspath = new DefaultClassPath(getAnimalsnifferClasspath())
        antBuilder.withClasspath(animalsnifferClasspath.asFiles).execute { a ->
            ant.taskdef(name: 'animalsniffer', classname: 'org.codehaus.mojo.animal_sniffer.ant.CheckSignatureTask')
            ReportCollector collector = new ReportCollector(getSourcesDirs().srcDirs)
            replaceBuildListener(project, collector)
            getAnimalsnifferSignatures().each { signature ->
                try {
                    ant.animalsniffer(signature: signature.absolutePath, classpath: getClasspath().asPath) {
                        path(path: getSource().asPath)
                        getSourcesDirs().srcDirs.each {
                            sourcepath(path: it.absoluteFile)
                        }
                        annotation(className: 'org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement')
                        if (getAnnotation()) {
                            annotation(className: getAnnotation())
                        }
                    }
                } catch (Exception ex) {
                    // rethrow not expected ant exceptions
                    if (ex.class.name != BuildException.name) {
                        throw ex
                    }
                }
            }
            if (collector.errorsCnt() > 0) {
                String message = "${collector.errorsCnt()} AnimalSniffer violations were found " +
                        "in ${collector.filesCnt()} files."
                Report report = reports.firstEnabled
                if (report) {
                    collector.writeToFile(report.destination)

                    String reportUrl = "file:///${report.destination.canonicalPath.replaceAll('\\\\', '/')}"
                    message += " See the report at: $reportUrl"
                }
                if (getIgnoreFailures()) {
                    logger.error(message + '\n')
                    collector.writeToConsole(logger)
                } else {
                    logger.error('')
                    collector.writeToConsole(logger)
                    throw new GradleException(message)
                }
            }
        }
    }

    /**
     * Returns the reports to be generated by this task.
     */
    @Nested
    @Override
    AnimalSnifferReports getReports() {
        return reports
    }

    @Override
    @SuppressWarnings('ConfusingMethodName')
    AnimalSnifferReports reports(Closure closure) {
        reports.configure(closure)
    }

    @Override
    @SuppressWarnings('ConfusingMethodName')
    AnimalSnifferReports reports(Action<? super AnimalSnifferReports> action) {
        action.execute(reports)
        return reports
    }

    /**
     * Due to many classloaders used by AntBuilder, have to avoid ant classes in custom listener.
     * To accomplish this jdk proxy used.
     *
     * @param project ant project
     * @param handler proxy handler
     */
    static void replaceBuildListener(Object project, InvocationHandler handler) {
        // cleanup default gradle listener listener to avoid console output
        project.buildListeners.each { project.removeBuildListener(it) }
        ClassLoader cl = project.class.classLoader
        Object listener = Proxy.newProxyInstance(cl, [cl.loadClass(BuildListener.name)] as Class[], handler)
        project.addBuildListener(listener)
    }
}
