package ru.vyarus.gradle.plugin.animalsniffer.worker

import groovy.transform.CompileStatic
import org.codehaus.mojo.animal_sniffer.SignatureBuilder
import org.gradle.workers.WorkAction
import org.slf4j.LoggerFactory

/**
 * Almost copies original animalsniffer ant task (BuildSignatureTask) logic.
 * <p>
 * Builds signature file. No file indicates error (actual error would be logged).
 *
 * @author Vyacheslav Rusakov
 * @since 11.01.2025
 */
@CompileStatic
@SuppressWarnings(['AbstractClassWithoutAbstractMethod', 'CatchException'])
abstract class BuildWorker implements WorkAction<BuildParameters> {
    private final DirectLogger logger = new DirectLogger(LoggerFactory.getLogger(BuildWorker))

    @Override
    void execute() {
        // important because no file will indicate error in signature building
        deleteOut()
        try {
            if (!parameters.path.present) {
                throw new IllegalStateException('Required path not set')
            }

            List<FileInputStream> inStreams = parameters.signatures.get()
                    .collect { new FileInputStream(it) }

            SignatureBuilder builder = new SignatureBuilder(
                    inStreams as InputStream[], new FileOutputStream(parameters.output.get()), logger)
            parameters.include.get().each { builder.addInclude(it) }
            parameters.exclude.get().each { builder.addExclude(it) }

            parameters.path.get().each {
                if (it.exists()) {
                    builder.process(it)
                }
            }

            builder.close()
        } catch (Exception ex) {
            logger.error('Failed to build signature', ex)
            // delete file to indicate error
            deleteOut()
        }
    }

    private void deleteOut() {
        if (parameters.output.get().exists()) {
            parameters.output.get().delete()
        }
    }
}
