package com.stirante.lolclient;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public abstract class ProcessWatcher {

    private static final Set<ProcessWatcher> registered = new HashSet<>();
    private static boolean initialized = false;

    private static void init() {
        register(new PSProcessWatcher());
        register(new WMICProcessWatcher());
        register(new PowershellProcessWatcher());
        initialized = true;

    }

    public static void register(ProcessWatcher processWatcher) {
        registered.add(processWatcher);
    }

    public abstract String getInstallDirectory() throws IOException;

    public abstract boolean isApplicable();

    public abstract int getPriority();

    public static ProcessWatcher getInstance() {
        if (!initialized) {
            init();
        }
        return registered.stream()
                .sorted(Comparator.comparingInt(ProcessWatcher::getPriority))
                .filter(ProcessWatcher::isApplicable)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Applicable ProcessWatcher not found!"));
    }

}
