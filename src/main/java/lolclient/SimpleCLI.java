package lolclient;

public class SimpleCLI {

    public static void main(String[] args) {
        if (args.length == 0) {
            showUsage();
        } else {
            for (String arg : args) {
                System.out.println(arg);
            }
        }
    }

    private static void showUsage() {

    }

}
