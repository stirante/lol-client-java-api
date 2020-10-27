package com.stirante.lolclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

class SimpleConsole extends Thread {
    private final AtomicBoolean running = new AtomicBoolean(true);

    private final Queue<String> output = new LinkedBlockingQueue<>();
    private final String executable;
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
            while (running.get() && cmdOutput.hasNextLine()) {
                String s = cmdOutput.nextLine();
                output.add(s);
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

    public Queue<String> getCommandOutput() {
        return output;
    }

}
