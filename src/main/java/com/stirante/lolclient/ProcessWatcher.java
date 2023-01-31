package com.stirante.lolclient;

import com.stirante.lolclient.watchers.MacOSProcessWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public abstract class ProcessWatcher {

    private static final Logger logger = LoggerFactory.getLogger(ProcessWatcher.class);

    private static ProcessWatcher instance;
    private static CompletableFuture<Boolean> initialized;

    private static void init() {
        initialized = new CompletableFuture<>();
        register(new Win32ProcessWatcher());
        register(new PSProcessWatcher());
        register(new WMICProcessWatcher());
        register(new PowershellProcessWatcher());
        register(new MacOSProcessWatcher());
    }

    public static void register(ProcessWatcher processWatcher) {
        processWatcher.isApplicable().whenComplete((applicable, throwable) -> {
            if (applicable && throwable == null &&
                    (instance == null || instance.getPriority() > processWatcher.getPriority())) {
                logger.debug("ProcessWatcher " + processWatcher.getClass().getSimpleName() + " is applicable");
                if (instance != null) {
                    logger.debug("ProcessWatcher " + processWatcher.getClass().getSimpleName() + " is applicable and higher priority than " + instance.getClass().getSimpleName() + " - replacing");
                    instance.stop();
                }
                instance = processWatcher;
                if (!initialized.isDone()) {
                    initialized.complete(true);
                }
            }
            else {
                if (instance != null && applicable) {
                    logger.debug("ProcessWatcher " + processWatcher.getClass().getSimpleName() + " is applicable, but lower priority than " + instance.getClass().getSimpleName() + " - ignoring");
                } else {
                    logger.debug("ProcessWatcher " + processWatcher.getClass().getSimpleName() + " is not applicable");
                }
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
