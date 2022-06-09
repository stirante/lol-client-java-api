package com.stirante.lolclient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class WMICProcessWatcher extends ProcessWatcher {

    public static final String EXECUTABLE = "cmd.exe";
    public static final String COMMAND = "WMIC PROCESS WHERE name='LeagueClientUx.exe' GET commandline & echo --end-marker--";
    private SimpleConsole thread;
    private CompletableFuture<Boolean> applicable;

    @Override
    public CompletableFuture<String> getInstallDirectory() throws IOException {
        if (thread == null) {
            thread = new SimpleConsole(EXECUTABLE);
            thread.start();
        }
        final CompletableFuture<String> target = new CompletableFuture<>();
        thread.addOutputListener(s -> {
            if (s.contains("LeagueClientUx.exe") && s.contains("--install-directory=")) {
                target.complete(s);
                return true;
            }
            if (s.equals("--end-marker--")) {
                target.complete(null);
                return true;
            }
            return false;
        });
        //Get all processes command line
        thread.writeCommand(COMMAND);
        return target;
    }

    @Override
    public CompletableFuture<Boolean> isApplicable() {
        if (applicable != null) {
            return applicable;
        }
        applicable = new CompletableFuture<>();
        if (!System.getProperty("os.name").startsWith("Windows")) {
            applicable.complete(false);
            return applicable;
        }
        try {
            Process process =
                    Runtime.getRuntime().exec("WMIC PROCESS WHERE name='LeagueClientUx.exe' GET commandline");
            process.waitFor(10, TimeUnit.SECONDS);
            if (process.exitValue() != 0) {
                applicable.complete(false);
                return applicable;
            }
            process.destroy();
            if (thread == null) {
                thread = new SimpleConsole(EXECUTABLE);
                thread.start();
            }
            AtomicInteger counter = new AtomicInteger(0);
            thread.addOutputListener(s -> {
                if (s.contains("ProcessId")) {
                    applicable.complete(true);
                    return true;
                }
                if (s.equals("--end-marker--")) {
                    applicable.complete(false);
                    return true;
                }
                if (counter.incrementAndGet() > 10) {
                    applicable.complete(false);
                }
                return false;
            });
            thread.writeCommand("WMIC PROCESS WHERE name='java.exe' GET ProcessId & echo --end-marker--");
            return applicable;
        } catch (Exception e) {
            applicable.complete(false);
            return applicable;
        }
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public void stop() {
        thread.close();
        thread = null;
    }


}
