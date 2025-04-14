package com.github.andrew0030.pandora_core.utils.resource;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A utility class abstracting away CompletableFutures for resource management
 */
public class ResourceDispatcher {
    final PreparableReloadListener.PreparationBarrier barrier;
    final ProfilerFiller prepareProfiler;
    final ProfilerFiller applyProfiler;
    final Executor prepareExecutor;
    final Executor applyExecutor;
    List<CompletedDispatch> dispatches = new ArrayList<>();

    @ApiStatus.Internal
    public ResourceDispatcher(PreparableReloadListener.PreparationBarrier barrier, ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        this.barrier = barrier;
        this.prepareProfiler = prepareProfiler;
        this.applyProfiler = applyProfiler;
        this.prepareExecutor = prepareExecutor;
        this.applyExecutor = applyExecutor;
    }

    /**
     * Unifies the dispatches into a single future
     *
     * @return A single completable future representing all dispatches
     */
    @ApiStatus.Internal
    public CompletableFuture<Void> future() {
        if (dispatches.isEmpty()) return CompletableFuture.completedFuture(null);
        ArrayList<CompletableFuture<?>> futures = new ArrayList<>();
        for (CompletedDispatch dispatch : dispatches) futures.add(dispatch.future);
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{}));
    }

    /**
     * An object representing a completed dispatch
     */
    public final class CompletedDispatch<T> {
        CompletableFuture<T> future;

        public CompletedDispatch(CompletableFuture<T> future) {
            this.future = future;
        }

//        public T get() throws InterruptedException, ExecutionException {
//            return future.join();
//        }
    }

    /**
     * An intermediate object for constructing chains of preparation actions
     */
    public final class SubDispatch<T> {
        CompletableFuture<T> stem;

        public SubDispatch(CompletableFuture<T> stem) {
            this.stem = stem;
        }

        /**
         * Runs the provided runnable on thread after resource loading
         * GL resources can be managed in this
         *
         * @param section a name for a profiler section to create
         * @param r       the action to run to apply resources
         */
        public void apply(String section, Consumer<T> r) {
            dispatches.add(new CompletedDispatch<>(
                    stem.thenComposeAsync((v) -> {
                        applyProfiler.push(section);
                        r.accept(v);
                        applyProfiler.pop();
                        return CompletableFuture.completedStage(null);
                    }, applyExecutor)
            ));
        }

        /**
         * Runs the provided runnable on thread after resource loading
         * GL resources can be managed in this
         *
         * @param section a name for a profiler section to create
         * @param r       a function mapping the result to another object
         *                this is currently pointless
         *                <p>
         *                intellij formats this dumbly, so adding the letter "a" to prevent that
         *                a TODO: figure out a way to make a completed dispatch's result acquirable
         *                another TODO: test that, because I changed this last second
         */
        public <V> CompletedDispatch<V> complete(String section, Function<T, V> r) {
            CompletedDispatch<V> dispatch = new CompletedDispatch<>(
                    stem.thenComposeAsync((v) -> {
                        prepareProfiler.push(section);
                        V a = r.apply(v);
                        prepareProfiler.pop();
                        return CompletableFuture.completedStage(a);
                    }, prepareExecutor)
            );
            dispatches.add(dispatch);
            return dispatch;
        }

        /**
         * Computes the provided function off-thread
         * GL resource management must be done on thread, in an apply
         *
         * @param section a name for a profiler section to create
         * @param r       a function mapping the result of the previous preparation action to a new object
         * @return a SubDispatch to chain more preparation and application actions off of
         */
        public <V> SubDispatch<V> prepare(String section, Function<T, V> r) {
            return new SubDispatch<>(
                    stem.thenComposeAsync((v) -> {
                        prepareProfiler.push(section);
                        V a = r.apply(v);
                        prepareProfiler.pop();
                        return CompletableFuture.completedStage(a);
                    }, prepareExecutor)
            );
        }

        /**
         * Computes the provided consumer off-thread
         * GL resource management must be done on thread, in an apply
         *
         * @param section a name for a profiler section to create
         * @param r       a consumer taking the result of the previous preparation action and performing more actions
         * @return a SubDispatch to chain more preparation and application actions off of
         */
        public SubDispatch<Void> prepare(String section, Consumer<T> r) {
            return new SubDispatch<>(
                    stem.thenAcceptAsync((v) -> {
                        prepareProfiler.push(section);
                        r.accept(v);
                        prepareProfiler.pop();
                    }, prepareExecutor)
            );
        }

        public SubDispatch<T> barrier() {
            stem = stem.thenCompose(barrier::wait);
            return this;
        }
    }

    /**
     * Runs the provided runnable on thread after resource loading
     * GL resources can be managed in this
     *
     * @param section a name for a profiler section to create
     * @param r       the action to run to apply resources
     */
    public void apply(String section, Runnable r) {
        dispatches.add(new CompletedDispatch<>(CompletableFuture
                .runAsync(() -> {
                })
                .thenCompose(barrier::wait)
                .thenRunAsync(() -> {
                    applyProfiler.push(section);
                    r.run();
                    applyProfiler.pop();
                }, applyExecutor)
        ));
    }

    /**
     * Computes the provided supplier off-thread
     * GL resource management must be done on thread, in an apply
     *
     * @param section a name for a profiler section to create
     * @param r       a supplier providing an object to pass to the next element in the chain
     * @return a SubDispatch to chain more preparation and application actions off of
     */
    public <T> SubDispatch<T> prepare(String section, Supplier<T> r) {
        return new SubDispatch<>(CompletableFuture.supplyAsync(() -> {
            prepareProfiler.push(section);
            T t = r.get();
            prepareProfiler.pop();
            return t;
        }, prepareExecutor));
    }

    /**
     * Runs the provided runnable off-thread
     * GL resource management must be done on thread, in an apply
     *
     * @param section a name for a profiler section to create
     * @param r       the runnable to run for resource preparation
     * @return a SubDispatch to chain more preparation and application actions off of
     */
    public SubDispatch<Void> prepare(String section, Runnable r) {
        return new SubDispatch<>(CompletableFuture.runAsync(() -> {
            prepareProfiler.push(section);
            r.run();
            prepareProfiler.pop();
        }, prepareExecutor));
    }
}
