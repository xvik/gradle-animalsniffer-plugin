package ru.vyarus.gradle.plugin.animalsniffer.debug.util

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Buildable
import org.gradle.api.Task
import org.gradle.api.internal.file.collections.ProviderBackedFileCollection
import org.gradle.api.internal.provider.ProviderResolutionStrategy
import org.gradle.api.internal.tasks.AbstractTaskDependencyResolveContext
import org.gradle.api.internal.tasks.TaskDependencyContainer
import org.gradle.api.internal.tasks.TaskDependencyResolveException
import org.gradle.api.internal.tasks.WorkDependencyResolver
import org.gradle.internal.graph.CachingDirectedGraphWalker
import org.gradle.internal.graph.DirectedGraph

import javax.annotation.Nullable
import java.lang.reflect.Field

import static java.lang.String.format

/**
 * Fixed implementation for {@code CachingTaskDependencyResolveContext}, avoiding throwing exceptions
 * (collect as much data as we can, no matter if something fails).
 *
 * @author Vyacheslav Rusakov
 * @since 04.12.2024
 */
@CompileStatic
class DependencyResolutionContext<T> extends AbstractTaskDependencyResolveContext {
    private final Deque<Object> queue = new ArrayDeque<Object>()
    private final CachingDirectedGraphWalker<Object, T> walker
    private Task task

    DependencyResolutionContext(Collection<? extends WorkDependencyResolver<T>> workResolvers) {
        this.walker = new CachingDirectedGraphWalker<>(new TaskGraphImpl(workResolvers))
    }

    @SuppressWarnings('CatchException')
    Set<T> getDependencies(@Nullable Task task, Object dependencies) {
        this.task = task
        try {
            walker.add(dependencies)
            return walker.findValues()
        } catch (Exception e) {
            throw new TaskDependencyResolveException(format('Could not determine the dependencies of %s.', task), e)
        } finally {
            queue.clear()
            this.task = null
        }
    }

    @Override
    @Nullable
    Task getTask() {
        return task
    }

    @Override
    void add(Object dependency) {
        if (dependency == TaskDependencyContainer.EMPTY) {
            // Ignore things we know are empty
            return
        }
        queue.add(dependency)
    }

    private class TaskGraphImpl implements DirectedGraph<Object, T> {
        private final Collection<? extends WorkDependencyResolver<T>> workResolvers

        TaskGraphImpl(Collection<? extends WorkDependencyResolver<T>> workResolvers) {
            this.workResolvers = workResolvers
        }

        @Override
        @SuppressWarnings('Instanceof')
        void getNodeValues(Object node,
                           final Collection<? super T> values,
                           Collection<? super Object> connectedNodes) {
            if (node instanceof TaskDependencyContainer) {
                TaskDependencyContainer taskDependency = (TaskDependencyContainer) node
                queue.clear()

                // hack to continue on provider without value ---------- THE ONLY CHANGE
                if (taskDependency instanceof ProviderBackedFileCollection) {
                    Field strategy = ProviderBackedFileCollection.getDeclaredField('providerResolutionStrategy')
                    strategy.accessible = true
                    strategy.set(taskDependency, ProviderResolutionStrategy.ALLOW_ABSENT)
                }

                taskDependency.visitDependencies(DependencyResolutionContext.this)
                connectedNodes.addAll(queue)
                return
            }
            if (node instanceof Buildable) {
                Buildable buildable = (Buildable) node
                connectedNodes.add(buildable.buildDependencies)
                return
            }
            for (WorkDependencyResolver<T> workResolver : workResolvers) {
                if (workResolver.resolve(task, node, new Action<T>() {
                    @Override
                    void execute(T t) {
                        values.add(t)
                    }
                })) {
                    return
                }
            }
            throw new IllegalArgumentException(
                    format('Cannot resolve object of unknown type %s to a Task.', node.class.simpleName)
            )
        }
    }
}
