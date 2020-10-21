package com.stirante.lolclient;

import java.io.IOException;

public class PowershellProcessWatcher extends ProcessWatcher {

    public static final String EXECUTABLE = "powershell.exe";
    public static final String COMMAND =
            "(Get-CimInstance -ClassName win32_process -Filter \"name like 'LeagueClientUx.exe'\").CommandLine";
    private SimpleConsole thread;

    @Override
    public String getInstallDirectory() throws IOException {
        if (thread == null) {
            thread = new SimpleConsole(EXECUTABLE);
            thread.start();
        }
        String target = "";
        //Get all processes command line
        thread.writeCommand(COMMAND);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String s : thread.getCommandOutput()) {
            //executable has to be LeagueClientUx.exe and must contain in arguments install-directory
            if (s.contains("LeagueClientUx.exe") && s.contains("--install-directory=")) {
                target = s;
                break;
            }
        }
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
            if (thread == null) {
                thread = new SimpleConsole(EXECUTABLE);
                thread.start();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public void stop() {
        thread.close();
    }

}
