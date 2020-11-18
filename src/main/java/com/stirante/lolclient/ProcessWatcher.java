package com.stirante.lolclient;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public abstract class ProcessWatcher {

    private static ProcessWatcher instance;
    private static CompletableFuture<Boolean> initialized;

    private static void init() {
        initialized = new CompletableFuture<>();
        register(new PSProcessWatcher());
        register(new WMICProcessWatcher());
        register(new PowershellProcessWatcher());
    }

    public static void register(ProcessWatcher processWatcher) {
        processWatcher.isApplicable().whenComplete((applicable, throwable) -> {
            if (applicable && throwable == null &&
                    (instance == null || instance.getPriority() > processWatcher.getPriority())) {
                if (instance != null) {
                    instance.stop();
                }
                instance = processWatcher;
                if (!initialized.isDone()) {
                    initialized.complete(true);
                }
            }
            else {
                processWatcher.stop();
            }
        });
    }

    public abstract CompletableFuture<String> getInstallDirectory() throws IOException;

    public abstract CompletableFuture<Boolean> isApplicable();

    public abstract int getPriority();

    public abstract void stop();

    public static ProcessWatcher getInstance() {
        if (initialized == null) {
            init();
        }
        if (!initialized.isDone()) {
            initialized.join();
        }
        return instance;
    }

}
