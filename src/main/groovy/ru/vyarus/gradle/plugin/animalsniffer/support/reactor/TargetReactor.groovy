package ru.vyarus.gradle.plugin.animalsniffer.support.reactor

import org.gradle.api.Action

/**
 * Reactor encapsulates various objects resolution (like source sets, android variants, kotlin platforms).
 *
 * @author Vyacheslav Rusakov
 * @since 18.12.2024
 */
interface TargetReactor<T> {

    /**
     * Resolve objects. Note that all implementations use {@link .all()} calls in order to resolve not only existing
     * but also newly added objects. This means that some action calls might be delayed in time (as soon as new objects
     * appear).
     *
     * @param action action to call (resolved object reaction)
     */
    void onTarget(Action<T> action)
}
