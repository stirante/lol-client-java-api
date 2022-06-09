package com.stirante.lolclient;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.W32APIOptions;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Win32ProcessWatcher extends ProcessWatcher {

    private CompletableFuture<Boolean> applicable;

    @Override
    public CompletableFuture<String> getInstallDirectory() throws IOException {
        final CompletableFuture<String> target = new CompletableFuture<>();
        int processId = getProcessId("LeagueClientUx.exe");
        if (processId == -1) {
            target.complete(null);
            return target;
        }
        String path = getProcessPath(processId);
        if (path == null) {
            target.complete(null);
            return target;
        }
        target.complete(path);
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
            int pid = getProcessId("java.exe");
            if (pid == -1) {
                applicable.complete(false);
                return applicable;
            }
            String processPath = getProcessPath(pid);
            if (processPath == null) {
                applicable.complete(false);
                return applicable;
            }
            applicable.complete(true);
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
    }

    private static int getProcessId(String executable) {
        Tlhelp32.PROCESSENTRY32.ByReference processEntry = new Tlhelp32.PROCESSENTRY32.ByReference();

        WinNT.HANDLE snapshot =
                Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0));
        try {
            while (Kernel32.INSTANCE.Process32Next(snapshot, processEntry)) {
                if (Native.toString(processEntry.szExeFile).equals(executable)) {
                    return processEntry.th32ProcessID.intValue();
                }
            }
        } finally {
            Kernel32.INSTANCE.CloseHandle(snapshot);
        }
        return -1;
    }

    private static String getProcessPath(int pid) {
        Tlhelp32.MODULEENTRY32W.ByReference processEntry = new Tlhelp32.MODULEENTRY32W.ByReference();

        WinNT.HANDLE snapshot =
                Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, new WinDef.DWORD(pid));
        try {
            if (Kernel32.INSTANCE.Module32FirstW(snapshot, processEntry)) {
                return Native.toString(processEntry.szExePath);
            }
        } finally {
            Kernel32.INSTANCE.CloseHandle(snapshot);
        }
        return null;
    }


}
