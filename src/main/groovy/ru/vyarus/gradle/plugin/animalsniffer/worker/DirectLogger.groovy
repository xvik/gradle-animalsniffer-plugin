package ru.vyarus.gradle.plugin.animalsniffer.worker

import groovy.transform.CompileStatic
import org.codehaus.mojo.animal_sniffer.logging.Logger

/**
 * Slf4j animalsniffer logger implementation.
 *
 * @author Vyacheslav Rusakov
 * @since 11.01.2025
 */
@CompileStatic
class DirectLogger implements Logger {
    private final org.slf4j.Logger logger

    DirectLogger(org.slf4j.Logger logger) {
        this.logger = logger
    }

    @Override
    void info(String s) {
        logger.info(s)
    }

    @Override
    void info(String s, Throwable throwable) {
        logger.info(s, throwable)
    }

    @Override
    void debug(String s) {
        logger.debug(s)
    }

    @Override
    void debug(String s, Throwable throwable) {
        logger.debug(s, throwable)
    }

    @Override
    void warn(String s) {
        logger.warn(s)
    }

    @Override
    void warn(String s, Throwable throwable) {
        logger.warn(s, throwable)
    }

    @Override
    void error(String s) {
        logger.error(s)
    }

    @Override
    void error(String s, Throwable throwable) {
        logger.error(s, throwable)
    }
}
