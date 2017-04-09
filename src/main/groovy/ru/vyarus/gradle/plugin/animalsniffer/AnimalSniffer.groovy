package ru.vyarus.gradle.plugin.animalsniffer

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.apache.tools.ant.BuildException
import org.apache.tools.ant.BuildListener
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.ClosureBackedAction
import org.gradle.api.internal.project.IsolatedAntBuilder
import org.gradle.api.reporting.Report
import org.gradle.api.reporting.Reporting
import org.gradle.api.tasks.*
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import org.gradle.api.tasks.incremental.InputFileDetails
import org.gradle.internal.classpath.ClassPath
import org.gradle.internal.classpath.DefaultClassPath
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.GUtil
import ru.vyarus.gradle.plugin.animalsniffer.report.AnimalSnifferReports
import ru.vyarus.gradle.plugin.animalsniffer.report.AnimalSnifferReportsImpl
import ru.vyarus.gradle.plugin.animalsniffer.report.ReportCollector
import ru.vyarus.gradle.plugin.animalsniffer.util.FormatUtils

import javax.inject.Inject
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
@CompileStatic
@CacheableTask
class AnimalSniffer extends SourceTask implements VerificationTask, Reporting<AnimalSnifferReports> {

    /**
     * The class path containing the Animal Sniffer library to be used.
     */
    @Classpath
    @InputFiles
    FileCollection animalsnifferClasspath

    /**
     * Signature files used for checks.
     */
    @SkipWhenEmpty
    @Classpath
    @InputFiles
    FileCollection animalsnifferSignatures

    /**
     * Classpath used for compilation
     */
    @CompileClasspath
    @InputFiles
    FileCollection classpath

    /**
     * Source directories
     */
    @Input
    SourceDirectorySet sourcesDirs

    /**
     * Compiled classes directory
     */
    @Input
    File classesDir

    /**
     * Annotation class name to avoid check
     */
    @Input
    @Optional
    String annotation

    /**
     * Extra allowed classes, not mentioned in signature, but allowed for usage.
     * Most commonly, when some classes target newer jdk version.
     * <p>
     * See <a href="http://www.mojohaus.org/animal-sniffer/animal-sniffer-ant-tasks/examples/checking-signatures.html
     * #Ignoring_classes_not_in_the_signature">
     * docs</a> for more info.
     */
    @Input
    @Optional
    Iterable<String> ignoreClasses = []

    /**
     * Whether or not the build should break when the verifications performed by this task fail.
     */
    @Console
    boolean ignoreFailures

    /**
     * Enables incremental check mode: check only changed files after first run.
     */
    @Input
    @Optional
    boolean incremental

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
    @CompileStatic(TypeCheckingMode.SKIP)
    void run(IncrementalTaskInputs inputs) {
        checkIncrementalConditions()
        Iterable<File> filesToCheck = getFilesToCheck(inputs)
        String targetPath = GUtil.asPath(filesToCheck)
        Iterable<String> ignored = getIgnores(inputs, filesToCheck)
        ClassPath animalsnifferClasspath = new DefaultClassPath(getAnimalsnifferClasspath())
        antBuilder.withClasspath(animalsnifferClasspath.asFiles).execute { a ->
            ant.taskdef(name: 'animalsniffer', classname: 'org.codehaus.mojo.animal_sniffer.ant.CheckSignatureTask')
            ReportCollector collector = new ReportCollector(getSourcesDirs().srcDirs)
            replaceBuildListener(project, collector)
            getAnimalsnifferSignatures().each { signature ->
                try {
                    ant.animalsniffer(signature: signature.absolutePath, classpath: getClasspath().asPath) {
                        path(path: targetPath)
                        getSourcesDirs().srcDirs.each {
                            sourcepath(path: it.absoluteFile)
                        }
                        annotation(className: 'org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement')
                        if (getAnnotation()) {
                            annotation(className: getAnnotation())
                        }
                        ignored.each { ignore(className: it) }
                    }
                } catch (Exception ex) {
                    // rethrow not expected ant exceptions
                    if (ex.class.name != BuildException.name) {
                        throw ex
                    }
                }
            }
            processErrors(collector)
            // it should be useless as completely custom ant used for execution, but just in case
            recoverOriginalListener(project, collector.originalListener)
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
    AnimalSnifferReports reports(
            @DelegatesTo(value = AnimalSnifferReports, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        reports(new ClosureBackedAction<AnimalSnifferReports>(closure))
    }

    @Override
    @SuppressWarnings('ConfusingMethodName')
    AnimalSnifferReports reports(Action<? super AnimalSnifferReports> action) {
        action.execute(reports)
        return reports
    }

    @Override
    @PathSensitive(PathSensitivity.RELATIVE)
    @InputFiles
    FileTree getSource() {
        return super.getSource()
    }

    /**
     *
     * @param inputs incremental inputs
     * @return files to check: changed or all files
     */
    private Iterable<File> getFilesToCheck(IncrementalTaskInputs inputs) {
        if (getIncremental() && inputs.incremental) {
            // collect only changed files
            List<File> files = []
            inputs.outOfDate {
                // use source as filter for other inputs
                if (source.contains(it.file)) {
                    files << it.file
                }
            } as Action<InputFileDetails>
            logger.info "[animalsniffer] Incremental mode: checking only ${files.size()} changed files"
            return files
        }
        return source.files
    }

    /**
     * With ignoreFailures gradle will finish incremental task and you will never see
     * these errors anymore (if these files will not be modified). Even clean will not bring back the errors.
     * Only task fail grants errors consistency.
     */
    private void checkIncrementalConditions() {
        if (getIgnoreFailures() && getIncremental()) {
            throw new GradleException(
                    "Animalsniffer can't run in incremental mode when 'ignoreFailures' is enabled " +
                            'because some errors may be missed in this case')
        }
    }

    /**
     * In incremental mode, all remaining classes must be added to ignored because animalsniffer
     * will treat unknown project classes as errors.
     * @param inputs incremental inputs
     * @param files checked files (only changed or all files)
     * @return full list of classes to ignore, including user configured ignores and missed classes in
     * incremental mode
     */
    private Iterable<String> getIgnores(IncrementalTaskInputs inputs, Iterable<File> files) {
        if (getIncremental() && inputs.incremental) {
            // all not changed files must be excluded from errors, because these types are out of signature
            List<String> res = [] + getIgnoreClasses()
            String root = classesDir.canonicalPath
            (getSource() - files).each {
                String clazz = FormatUtils.toClass(it.canonicalPath, root)
                clazz = clazz[0..clazz.lastIndexOf('.') - 1]
                res.add(clazz)

            }
            return res
        }
        return getIgnoreClasses()
    }

    void processErrors(ReportCollector collector) {
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
                String nl = String.format('%n')
                logger.error(nl + message + nl)
                collector.writeToConsole(logger)
            } else {
                logger.error('')
                collector.writeToConsole(logger)
                throw new GradleException(message)
            }
        }
    }

    /**
     * Due to many classloaders used by AntBuilder, have to avoid ant classes in custom listener.
     * Use jdk proxy to register custom listener.
     *
     * @param project ant project
     * @param handler proxy handler
     */
    @CompileStatic(TypeCheckingMode.SKIP)
    static void replaceBuildListener(Object project, ReportCollector handler) {
        // use original to redirect other ant tasks output (not expected, but just in case)
        handler.originalListener = project.buildListeners.first()
        // cleanup default gradle listener listener to avoid console output
        project.buildListeners.each { project.removeBuildListener(it) }
        ClassLoader cl = project.class.classLoader
        Object listener = Proxy.newProxyInstance(cl, [cl.loadClass(BuildListener.name)] as Class[], handler)
        project.addBuildListener(listener)
    }

    /**
     * Recover original listener after execution.
     *
     * @param project ant project
     * @param original original listener
     */
    @CompileStatic(TypeCheckingMode.SKIP)
    static void recoverOriginalListener(Object project, Object original) {
        if (original != null) {
            project.buildListeners.each { project.removeBuildListener(it) }
            project.addBuildListener(original)
        }
    }
}
