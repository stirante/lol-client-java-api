package com.stirante.lolclient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PowershellProcessWatcher extends ProcessWatcher {

    public static final String EXECUTABLE = "powershell.exe";
    public static final String COMMAND =
            "(Get-CimInstance -ClassName win32_process -Filter \"name like 'LeagueClientUx.exe'\").CommandLine;echo --end-marker--";
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
        if (Stream.of(System.getenv("PATH").split(Pattern.quote(File.pathSeparator)))
                .map(Paths::get)
                .noneMatch(path -> Files.exists(path.resolve("powershell.exe")))) {
            applicable.complete(false);
            return applicable;
        }
        try {
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
            thread.writeCommand("Get-CimInstance -ClassName win32_process -Filter \"name like 'java%'\";echo --end-marker--");
            return applicable;
        } catch (Exception e) {
            applicable.complete(false);
            return applicable;
        }
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public void stop() {
        thread.close();
        thread = null;
    }

}
