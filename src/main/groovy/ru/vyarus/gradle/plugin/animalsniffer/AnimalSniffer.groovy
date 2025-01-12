package ru.vyarus.gradle.plugin.animalsniffer

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.model.ObjectFactory
import org.gradle.api.reporting.Report
import org.gradle.api.reporting.Reporting
import org.gradle.api.tasks.*
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.internal.ClosureBackedAction
import org.gradle.workers.WorkQueue
import org.gradle.workers.WorkerExecutor
import ru.vyarus.gradle.plugin.animalsniffer.debug.PrintAnimalsnifferTasksTask
import ru.vyarus.gradle.plugin.animalsniffer.report.AnimalSnifferReports
import ru.vyarus.gradle.plugin.animalsniffer.report.AnimalSnifferReportsImpl
import ru.vyarus.gradle.plugin.animalsniffer.report.ReportBuilder
import ru.vyarus.gradle.plugin.animalsniffer.util.TargetType
import ru.vyarus.gradle.plugin.animalsniffer.worker.CheckWorker

import javax.inject.Inject

/**
 * AnimalSniffer task is created for each registered source set.
 * <p>
 * Task is SourceTask, but with root in compile classes dir of current source set. This means that include/exclude
 * may be used, but they will be applied to compiled classes and not sources.
 *
 * @author Vyacheslav Rusakov
 * @since 14.12.2015
 */
@SuppressWarnings(['UnnecessaryGetter', 'Println'])
@CompileStatic
@CacheableTask
abstract class AnimalSniffer extends SourceTask implements VerificationTask, Reporting<AnimalSnifferReports> {

    private static final String NL = '\n'
    private final String projectRoot

    /**
     * Type of task source (source set, multiplatform, android). Added to be able to easily disable tasks by type.
     */
    @Internal
    TargetType targetType

    /**
     * Target name: source set name, kotlin platform with compilation or android variant name. Used to simplify
     * appliance to check task.
     */
    @Internal
    String targetName

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
     * Classpath used for compilation (if not included in signature)
     */
    @CompileClasspath
    @InputFiles
    @Optional
    FileCollection classpath

    /**
     * Source directories
     */
    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    FileCollection sourcesDirs

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
     * Configuration debug output
     */
    @Console
    boolean debug

    @Internal
    // for debug print task because getSources would return anything only for non-empty dirs
    private final List<Object> classesDirs = []

    private final AnimalSnifferReportsImpl reports

    @SuppressWarnings(['ThisReferenceEscapesConstructor', 'AbstractClassWithPublicConstructor'])
    AnimalSniffer() {
        reports = instantiator.newInstance(AnimalSnifferReportsImpl, this, getObjectFactory())
        projectRoot = project.rootDir.canonicalPath
    }

    @Inject
    abstract Instantiator getInstantiator()

    @Inject
    abstract ObjectFactory getObjectFactory()

    @Inject
    abstract WorkerExecutor getWorkerExecutor()

    @TaskAction
    @CompileStatic(TypeCheckingMode.SKIP)
    void run() {
        List<File> sortedPath = preparePath(getSource())
        if (getDebug()) {
            printTaskConfig(sortedPath)
        }
        Set<File> sourceDirs = getSourcesDirs().getFiles()
        String annotation = getAnnotation()
        FileCollection signatures = getAnimalsnifferSignatures()
        Set<File> classpathFiles = getClasspath().asFileTree.files
        List<String> ignoreClasses = getIgnoreClasses()
        boolean quiet = isIgnoreFailures()

        WorkQueue workQueue = workerExecutor.classLoaderIsolation {
            it.classpath.from(getAnimalsnifferClasspath())
        }

        // report file used for errors list exchange between task and worker
        File errors = reports.text.outputLocation.get().asFile
        workQueue.submit(CheckWorker) { params ->
            params.signatures.addAll(signatures)
            params.classpath.addAll(classpathFiles)
            params.classes.addAll(sortedPath)
            params.sourceDirs.addAll(sourceDirs)
            params.annotations.add('org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement')
            if (annotation) {
                params.annotations.add(annotation)
            }
            params.ignored.addAll(ignoreClasses)
            params.ignoreErrors.set(quiet)

            // always create report file because its the only way to know if errors were found
            params.reportOutput.set(errors)
        }

        workQueue.await()

        // if file exists then violations were found (or check process failed)
        if (errors.exists()) {
            // read errors from file
            ReportBuilder collector = new ReportBuilder(errors, getAnimalsnifferSignatures().size() > 1)
            // remove intermediate file - correct report would be created, if required
            errors.delete()

            processErrors(collector)
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
    FileTree getSource() {
        return super.getSource()
    }

    @Override
    void setSource(FileTree source) {
        classesDirs.add(source)
        super.setSource(source)
    }

    @Override
    void setSource(Object source) {
        classesDirs.add(source)
        super.setSource(source)
    }

    @Override
    SourceTask source(Object... sources) {
        classesDirs.add(sources)
        return super.source(sources)
    }

    Set<File> getClassesDirs() {
        return new TreeSet<File>(objectFactory.fileCollection().from(classesDirs).files)
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    void processErrors(ReportBuilder collector) {
        if (collector.errorsCnt() > 0) {
            String message = "${collector.errorsCnt()} AnimalSniffer violations were found " +
                    "in ${collector.filesCnt()} files."
            Report report = reports.text
            if (report && report.required.get()) {
                File target = report.outputLocation.get().asFile
                collector.writeToFile(target)

                String reportUrl = "file:///${target.canonicalPath.replaceAll('\\\\', '/')}"
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

    void printTaskConfig(List<File> path) {
        String rootDir = "${projectRoot}${File.separator}"
        StringBuilder res = new StringBuilder()
                .append('\n\tsignatures:\n')
                .append(getAnimalsnifferSignatures().files
                        .collect { "\t\t$it.name" }.join(NL))
                .append(NL)
                .append('\n\tsources:\n')
                .append(getSourcesDirs().getFiles().sort()
                        .collect { "\t\t${it.canonicalPath.replace(rootDir, '')}" }.join(NL))
                .append(NL)
                .append('\n\tfiles:\n')
                .append(path
                        .collect { "\t\t${it.toString().replace(rootDir, '')}" }.join(NL))
                .append(NL)

        if (!this.getIgnoreClasses().empty) {
            res.append('\n\tignored:\n')
                    .append(this.getIgnoreClasses().collect { "\t\t$it" }.join(NL))
                    .append(NL)
        }

        res.append("\n\n*use [$PrintAnimalsnifferTasksTask.NAME] task to print all tasks info\n")

        println res.toString()
    }

    @SuppressWarnings(['Indentation', 'UnnecessaryCollectCall', 'UnnecessarySubstring'])
    private static List<File> preparePath(FileTree source) {
        int clsExtSize = '.class'.length()
        String innerIndicator = '$'
        return source
                .toSorted { a, b ->
                    String n1 = a
                    String n2 = b
                    if (n1.contains(innerIndicator) || n2.contains(innerIndicator)) {
                        String a1 = n1.substring(0, n1.length() - clsExtSize) // - .class
                        String b1 = n2.substring(0, n2.length() - clsExtSize)
                        // the trick is to compare names without extension, so inner class would
                        // become longer and go last automatically;
                        // compare: Some.class < Some$1.class, but Some > Some$1
                        return a1 <=> b1
                    }
                    return n1 <=> n2
                }
    }
}
