package com.stirante.lolclient;

import java.io.IOException;

public abstract class ProcessWatcher {

    private static ProcessWatcher instance;
    private static boolean initialized = false;

    private static void init() {
        register(new PSProcessWatcher());
        register(new WMICProcessWatcher());
        register(new PowershellProcessWatcher());
        initialized = true;
    }

    public static void register(ProcessWatcher processWatcher) {
        if (processWatcher.isApplicable() &&
                (instance == null || instance.getPriority() > processWatcher.getPriority())) {
            if (instance != null) {
                instance.stop();
            }
            instance = processWatcher;
        }
        else {
            processWatcher.stop();
        }
    }

    public abstract String getInstallDirectory() throws IOException;

    public abstract boolean isApplicable();

    public abstract int getPriority();

    public abstract void stop();

    public static ProcessWatcher getInstance() {
        if (!initialized) {
            init();
        }
        return instance;
    }

}
