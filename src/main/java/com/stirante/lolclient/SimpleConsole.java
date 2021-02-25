package com.stirante.lolclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

class SimpleConsole extends Thread {
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ReentrantLock LISTENERS_LOCK = new ReentrantLock();

    private final String executable;
    private final List<Function<String, Boolean>> listeners = new ArrayList<>();
    private Process process;
    private Scanner cmdOutput;
    private PrintWriter cmdInput;

    public SimpleConsole(String executable) {
        this.executable = executable;
        try {
            process = new ProcessBuilder(executable).start();
            cmdOutput = new Scanner(process.getInputStream());
            cmdInput = new PrintWriter(process.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to spawn " + executable + "!");
        }
    }

    @Override
    public void run() {
        while (running.get()) {
            while (running.get() && process.isAlive() && cmdOutput.hasNextLine()) {
                String s = cmdOutput.nextLine();
                LISTENERS_LOCK.lock();
                listeners.removeIf(stringBooleanFunction -> stringBooleanFunction.apply(s));
                LISTENERS_LOCK.unlock();
            }
            if (!process.isAlive() && running.get()) {
                try {
                    process = new ProcessBuilder(executable).start();
                    cmdOutput = new Scanner(process.getInputStream());
                    cmdInput = new PrintWriter(process.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to spawn " + executable + "!");
                }
            }
        }
    }

    public void addOutputListener(Function<String, Boolean> listener) {
        LISTENERS_LOCK.lock();
        listeners.add(listener);
        LISTENERS_LOCK.unlock();
    }

    public void writeCommand(String s) {
        cmdInput.println(s);
        cmdInput.flush();
    }

    public void close() {
        running.set(false);
        cmdInput.close();
        cmdOutput.close();
        process.destroyForcibly();
    }

}
