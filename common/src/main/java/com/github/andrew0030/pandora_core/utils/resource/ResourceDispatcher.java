package com.github.andrew0030.pandora_core.utils.resource;

import io.netty.util.concurrent.CompleteFuture;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ResourceDispatcher {
    final PreparableReloadListener.PreparationBarrier barrier;
    final ProfilerFiller prepareProfiler;
    final ProfilerFiller applyProfiler;
    final Executor prepareExecutor;
    final Executor applyExecutor;
    List<CompletedDispatch> dispatches = new ArrayList<>();

    public ResourceDispatcher(PreparableReloadListener.PreparationBarrier barrier, ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        this.barrier = barrier;
        this.prepareProfiler = prepareProfiler;
        this.applyProfiler = applyProfiler;
        this.prepareExecutor = prepareExecutor;
        this.applyExecutor = applyExecutor;
    }

    public CompletableFuture<Void> future() {
        if (dispatches.isEmpty()) return CompletableFuture.completedFuture(null);
        ArrayList<CompletableFuture<?>> futures = new ArrayList<>();
        for (CompletedDispatch dispatch : dispatches) futures.add(dispatch.future);
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{}));
    }

    public final class CompletedDispatch<T> {
        CompletableFuture<T> future;

        public CompletedDispatch(CompletableFuture<T> future) {
            this.future = future;
        }

        public T get() throws InterruptedException, ExecutionException {
            return future.join();
        }
    }

    public final class SubDispatch<T> {
        CompletableFuture<T> stem;

        public SubDispatch(CompletableFuture<T> stem) {
            this.stem = stem;
        }

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

        public <V> CompletedDispatch<V> apply(String section, Function<T, V> r) {
            CompletedDispatch<V> dispatch = new CompletedDispatch<>(
                    stem.thenComposeAsync((v) -> {
                        applyProfiler.push(section);
                        V a = r.apply(v);
                        applyProfiler.pop();
                        return CompletableFuture.completedStage(a);
                    }, applyExecutor)
            );
            dispatches.add(dispatch);
            return dispatch;
        }

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

    public void apply(String section, Runnable r) {
        dispatches.add(new CompletedDispatch<>(CompletableFuture.runAsync(() -> {
            applyProfiler.push(section);
            r.run();
            applyProfiler.pop();
        }, applyExecutor)));
    }

    public <T> SubDispatch<T> prepare(String section, Supplier<T> r) {
        return new SubDispatch<>(CompletableFuture.supplyAsync(() -> {
            prepareProfiler.push(section);
            T t = r.get();
            prepareProfiler.pop();
            return t;
        }, prepareExecutor));
    }

    public SubDispatch<Void> prepare(String section, Runnable r) {
        return new SubDispatch<>(CompletableFuture.runAsync(() -> {
            prepareProfiler.push(section);
            r.run();
            prepareProfiler.pop();
        }, prepareExecutor));
    }
}
