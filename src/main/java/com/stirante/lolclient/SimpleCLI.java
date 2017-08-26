package com.stirante.lolclient;

import java.io.IOException;

public class SimpleCLI {

    public static void main(String[] args) {
        if (args.length == 0) {
            showUsage();
        } else {
            String path = "";
            String method = "GET";
            String lastParam = "";
            for (String arg : args) {
                if (lastParam.isEmpty() && arg.charAt(0) != '-') {
                    showUsage();
                } else if (!lastParam.isEmpty() && arg.charAt(0) == '-') {
                    showUsage();
                } else if (!lastParam.isEmpty()) {
                    if (lastParam.equals("-p")) path = "/" + arg;
                    else if (lastParam.equals("-m")) method = arg;
                    lastParam = "";
                } else {
                    lastParam = arg;
                }
            }
            if (method.equalsIgnoreCase("GET")) {
                ClientApi api = new ClientApi();
                try {
                    Object o = api.executeGet(path, Object.class);
                    System.out.println(o);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (method.equalsIgnoreCase("POST")) {
                ClientApi api = new ClientApi();
                try {
                    Object o = api.executePost(path, Object.class);
                    System.out.println(o);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void showUsage() {
        System.out.println("Simple CLI for LoL Client API");
        System.out.println("Parameters:");
        System.out.println("\t-p\tAPI Path");
        System.out.println("\t-m\tRequest method (right now only GET and POST)");
    }

}
