package ru.vyarus.gradle.plugin.animalsniffer.report.internal

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.*
import org.gradle.api.provider.Provider
import org.gradle.api.reporting.Report
import org.gradle.api.reporting.ReportContainer
import org.gradle.api.specs.Spec

/**
 * {@link ReportContainer} implementation (default gradle implementation is in internal package and could change at
 * any time). Required for unified reporting configuration (same as bundled quality plugins) and compatibility
 * with dashboard plugin.
 * <p>
 * Gradle provides nice reporting interfaces, but hides their implementation (and changes it in backwards incompatible
 * way quite often). See <a href="https://github.com/gradle/gradle/issues/7063">issue</a> for official position.
 * As a consequence, the only way to preserve backwards compatibility is to implement reporting from scratch.
 *
 * @author Vyacheslav Rusakov (modifications)
 * @author Baron Roberts (initial implementation)
 * @since 17.11.2024
 * @see <a href="https://github.com/gradle/gradle/issues/7063#issuecomment-1970527749">source</a>
 */
@SuppressWarnings('MethodCount')
@CompileStatic(TypeCheckingMode.SKIP)
class Reports<T extends Report> implements ReportContainer<T> {

    private final NamedDomainObjectSet<T> reports
    private final NamedDomainObjectSet<T> enabled

    Reports(final Project project, final Class<T> clazz) {
        this.reports = project.objects.namedDomainObjectSet(clazz)
        this.enabled = this.reports.matching(report -> (report as Report).required.get())
    }

    @Override
    NamedDomainObjectSet<T> getEnabled() {
        return this.enabled
    }

    @Override
    boolean add(final T report) {
        throw new ImmutableViolationException()
    }

    @Override
    boolean addAll(final Collection<? extends T> reps) {
        throw new ImmutableViolationException()
    }

    @Override
    void addLater(final Provider<? extends T> provider) {
        throw new ImmutableViolationException()
    }

    @Override
    void addAllLater(final Provider<? extends Iterable<T>> provider) {
        throw new ImmutableViolationException()
    }

    @Override
    boolean remove(final Object report) {
        throw new ImmutableViolationException()
    }

    @Override
    boolean removeAll(final Collection<?> reps) {
        throw new ImmutableViolationException()
    }

    @Override
    boolean retainAll(final Collection<?> reps) {
        throw new ImmutableViolationException()
    }

    @Override
    void clear() {
        throw new ImmutableViolationException()
    }

    @Override
    boolean containsAll(final Collection<?> reps) {
        return this.reports.containsAll(reps)
    }

    @Override
    Namer<T> getNamer() {
        return Report::getName
    }

    @Override
    SortedMap<String, T> getAsMap() {
        return this.reports.asMap
    }

    @Override
    SortedSet<String> getNames() {
        return this.reports.names
    }

    @Override
    T findByName(final String name) {
        return this.reports.findByName(name)
    }

    @Override
    T getByName(final String name) throws UnknownDomainObjectException {
        return this.reports.getByName(name)
    }

    @Override
    T getByName(final String name, final Closure configureClosure) throws UnknownDomainObjectException {
        return this.reports.getByName(name, configureClosure)
    }

    @Override
    T getByName(final String name, final Action<? super T> configureAction) throws UnknownDomainObjectException {
        return this.reports.getByName(name, configureAction)
    }

    @Override
    @SuppressWarnings('ExplicitCallToGetAtMethod')
    T getAt(final String name) throws UnknownDomainObjectException {
        return this.reports.getAt(name)
    }

    @Override
    Rule addRule(final Rule rule) {
        return this.reports.addRule(rule)
    }

    @Override
    Rule addRule(final String description, final Closure ruleAction) {
        return this.reports.addRule(description, ruleAction)
    }

    @Override
    Rule addRule(final String description, final Action<String> ruleAction) {
        return this.reports.addRule(description, ruleAction)
    }

    @Override
    List<Rule> getRules() {
        return this.reports.rules
    }

    @Override
    int size() {
        return this.reports.size()
    }

    @Override
    boolean isEmpty() {
        return this.reports.empty
    }

    @Override
    boolean contains(final Object report) {
        return this.reports.contains(report)
    }

    @Override
    Iterator<T> iterator() {
        return this.reports.iterator()
    }

    @Override
    Object[] toArray() {
        return this.reports.toArray()
    }

    @Override
    @SuppressWarnings('unchecked')
    <T1> T1[] toArray(final T1[] arr) {
        return (T1[]) this.reports.toArray((T[]) arr)
    }

    @Override
    Map<String, T> getEnabledReports() {
        return this.enabled.asMap
    }

    @Override
    <S extends T> NamedDomainObjectSet<S> withType(final Class<S> type) {
        return this.reports.withType(type)
    }

    @Override
    <S extends T> DomainObjectCollection<S> withType(final Class<S> type, final Action<? super S> configureAction) {
        return this.reports.withType(type, configureAction)
    }

    @Override
    <S extends T> DomainObjectCollection<S> withType(final Class<S> type, final Closure configureClosure) {
        return this.reports.withType(type, configureClosure)
    }

    @Override
    NamedDomainObjectSet<T> matching(final Spec<? super T> spec) {
        return this.reports.matching(spec)
    }

    @Override
    NamedDomainObjectSet<T> matching(final Closure spec) {
        return this.reports.matching(spec)
    }

    @Override
    Action<? super T> whenObjectAdded(final Action<? super T> action) {
        return this.reports.whenObjectAdded(action)
    }

    @Override
    void whenObjectAdded(final Closure action) {
        this.reports.whenObjectAdded(action)
    }

    @Override
    Action<? super T> whenObjectRemoved(final Action<? super T> action) {
        return this.reports.whenObjectRemoved(action)
    }

    @Override
    void whenObjectRemoved(final Closure action) {
        this.reports.whenObjectRemoved(action)
    }

    @Override
    void all(final Action<? super T> action) {
        this.reports.all(action)
    }

    @Override
    void all(final Closure action) {
        this.reports.all(action)
    }

    @Override
    void configureEach(final Action<? super T> action) {
        this.reports.configureEach(action)
    }

    @Override
    NamedDomainObjectProvider<T> named(final String name) throws UnknownDomainObjectException {
        return this.reports.named(name)
    }

    @Override
    NamedDomainObjectProvider<T> named(final String name, final Action<? super T> configurationAction)
            throws UnknownDomainObjectException {
        return this.reports.named(name, configurationAction)
    }

    @Override
    <S extends T> NamedDomainObjectProvider<S> named(final String name, final Class<S> type)
            throws UnknownDomainObjectException {
        return this.reports.named(name, type)
    }

    @Override
    <S extends T> NamedDomainObjectProvider<S> named(final String name, final Class<S> type,
                                                     final Action<? super S> configurationAction)
            throws UnknownDomainObjectException {
        return this.reports.named(name, type, configurationAction)
    }

    @Override
    NamedDomainObjectSet<T> named(Spec<String> nameFilter) {
        return this.reports.named(nameFilter)
    }

    @Override
    NamedDomainObjectCollectionSchema getCollectionSchema() {
        return this.reports.collectionSchema
    }

    @Override
    Set<T> findAll(final Closure spec) {
        return this.reports.findAll(spec)
    }

    @Override
    ReportContainer<T> configure(final Closure closure) {
        final Closure cl = (Closure) closure.clone()
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl.delegate = this
        cl.call(this)
        return this
    }

    protected boolean addReport(final T report) {
        return this.reports.add(report)
    }
}
