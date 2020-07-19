package com.stirante.lolclient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class PSProcessWatcher extends ProcessWatcher {

    @Override
    public String getInstallDirectory() throws IOException {
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
            return null;
        }
        return target;
    }

    @Override
    public boolean isApplicable() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return false;
        }
        try {
            Runtime.getRuntime().exec("ps");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
