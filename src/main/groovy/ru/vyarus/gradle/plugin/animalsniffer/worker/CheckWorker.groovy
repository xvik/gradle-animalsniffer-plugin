package ru.vyarus.gradle.plugin.animalsniffer.worker

import groovy.transform.CompileStatic
import org.codehaus.mojo.animal_sniffer.ClassListBuilder
import org.codehaus.mojo.animal_sniffer.SignatureChecker
import org.codehaus.mojo.animal_sniffer.logging.Logger
import org.gradle.workers.WorkAction
import org.slf4j.LoggerFactory

/**
 * Almost copies original animalsniffer ant task (CheckSignatureTask) logic.
 * <p>
 * Runs animalsniffer check and saves all found violation into CSV file. In case of internal error, empty file would
 * be created.
 *
 * @author Vyacheslav Rusakov
 * @since 10.01.2025
 */
@CompileStatic
@SuppressWarnings(['AbstractClassWithoutAbstractMethod', 'CatchException'])
abstract class CheckWorker implements WorkAction<CheckParameters> {
    private final DirectLogger logger = new DirectLogger(LoggerFactory.getLogger(CheckWorker))

    @Override
    void execute() {
        final CheckReportCollector collector = new CheckReportCollector(parameters.sourceDirs.get())
        deleteReport()
        try {
            Set<String> ignorePackages = buildPackageList(logger)
            if (parameters.ignored.present) {
                parameters.ignored.get().each {
                    ignorePackages.add(it.replace('.', '/'))
                }
            }

            for (File signature : parameters.signatures.get()) {
                collector.signature = signature.name

                final SignatureChecker signatureChecker = new SignatureChecker(
                        new FileInputStream(signature), ignorePackages, new LoggerInterceptor(logger, collector))

                signatureChecker.sourcePath = parameters.sourceDirs.get() as List
                signatureChecker.annotationTypes = parameters.annotations.get()

                parameters.classes.get().each {
                    signatureChecker.process(it)
                }

                // save all errors to file to indicate violations
                if (signatureChecker.signatureBroken) {
                    collector.save(parameters.reportOutput.get())
                }
            }
        } catch (Exception ex) {
            logger.error('Failed to check signatures', ex)
            // write all known errors or just empty file to indicate error
            collector.save(parameters.reportOutput.get())
        }
    }

    private void deleteReport() {
        if (parameters.reportOutput.get().exists()) {
            parameters.reportOutput.get().delete()
        }
    }

    private Set<String> buildPackageList(Logger logger) throws IOException {
        ClassListBuilder plb = new ClassListBuilder(logger)
        parameters.classes.get().each { plb.process(it) }
        parameters.classpath.get().each { plb.process(it) }
        return plb.packages
    }

    private static class LoggerInterceptor implements Logger {
        Logger delegate
        CheckReportCollector collector

        LoggerInterceptor(Logger delegate, CheckReportCollector collector) {
            this.delegate = delegate
            this.collector = collector
        }

        @Override
        void info(String s) {
            delegate.info(s)
        }

        @Override
        void info(String s, Throwable throwable) {
            delegate.info(s, throwable)
        }

        @Override
        void debug(String s) {
            delegate.debug(s)
        }

        @Override
        void debug(String s, Throwable throwable) {
            delegate.debug(s, throwable)
        }

        @Override
        void warn(String s) {
            delegate.warn(s)
        }

        @Override
        void warn(String s, Throwable throwable) {
            delegate.warn(s, throwable)
        }

        // actually, only error used by signature checker (other levels implemented just in case)
        @Override
        void error(String s) {
            collector.add(s)
        }

        @Override
        void error(String s, Throwable throwable) {
            collector.add(s)
        }
    }
}
