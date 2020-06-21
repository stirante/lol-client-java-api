package com.stirante.lolclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
            ClientApi api = new ClientApi();
            String finalMethod = method;
            String finalPath = path;
            api.addClientConnectionListener(new ClientConnectionListener() {
                @Override
                public void onClientConnected() {
                    if (finalMethod.equalsIgnoreCase("GET")) {
                        try {
                            System.out.println(api.executeGet(finalPath, Object.class).getRawResponse());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (finalMethod.equalsIgnoreCase("POST")) {
                        try {
                            System.out.println(api.executePost(finalPath, Object.class).getRawResponse());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    api.stop();
                }

                @Override
                public void onClientDisconnected() {

                }
            });
        }
    }

    private static void showUsage() {
        System.out.println("Simple CLI for LoL Client API");
        System.out.println("Parameters:");
        System.out.println("\t-p\tAPI Path");
        System.out.println("\t-m\tRequest method (right now only GET and POST)");
    }

}
