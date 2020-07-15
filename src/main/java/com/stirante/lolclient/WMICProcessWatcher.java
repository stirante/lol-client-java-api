package com.stirante.lolclient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class WMICProcessWatcher extends ProcessWatcher {

    @Override
    public String getInstallDirectory() throws IOException {
        String target = "";
        //Get all processes command line
        Process process =
                Runtime.getRuntime().exec("WMIC PROCESS WHERE name='LeagueClientUx.exe' GET commandline");
        InputStream in = process.getInputStream();
        Scanner sc = new Scanner(in);
        while (sc.hasNextLine()) {
            String s = sc.nextLine();
            //executable has to be LeagueClientUx.exe and must contain in arguments install-directory
            if (s.contains("LeagueClientUx.exe") && s.contains("--install-directory=")) {
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
        if (!System.getProperty("os.name").startsWith("Windows")) {
            return false;
        }
        try {
            Runtime.getRuntime().exec("wmic");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int getPriority() {
        return 2;
    }

}
