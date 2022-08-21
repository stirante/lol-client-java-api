package com.stirante.lolclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class PSProcessWatcher extends ProcessWatcher {

    private static final Logger logger = LoggerFactory.getLogger(PSProcessWatcher.class);

    @Override
    public CompletableFuture<String> getInstallDirectory() throws IOException {
        String target = "";
        //Get all processes command line
        Process process =
                Runtime.getRuntime().exec("ps x -o args | grep 'LeagueClientUx'");
        InputStream in = process.getInputStream();
        Scanner sc = new Scanner(in);
        while (sc.hasNextLine()) {
            String s = sc.nextLine();
            //executable has to be LeagueClientUx and must contain in arguments install-directory
            if (s.contains("LeagueClientUx") && s.contains("--install-directory=")) {
                target = s;
                break;
            }
        }
        in.close();
        process.destroy();
        if (target.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.completedFuture(target);
    }

    @Override
    public CompletableFuture<Boolean> isApplicable() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            logger.debug("ProcessWatcher is not applicable - Windows");
            return CompletableFuture.completedFuture(false);
        }
        try {
            Runtime.getRuntime().exec("ps");
            return CompletableFuture.completedFuture(true);

        } catch (Exception e) {
            logger.debug("ProcessWatcher is not applicable - ps failed", e);
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public void stop() {

    }

}
