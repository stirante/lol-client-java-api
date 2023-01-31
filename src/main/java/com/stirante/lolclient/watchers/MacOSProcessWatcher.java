package com.stirante.lolclient.watchers;

import com.stirante.lolclient.ProcessWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacOSProcessWatcher extends ProcessWatcher {

    private static final String MAC_OS_X = "Mac OS X";
    private static final Pattern INSTALL_DIRECTORY_PATTERN = Pattern.compile("(--install-directory=)([a-zA-Z./\\s]*)", Pattern.CASE_INSENSITIVE);
    private static final String INSTALL_DIRECTORY = "--install-directory=";
    private static final String LEAGUE_CLIENT_UX_PROCESS_NAME = "LeagueClientUx";
    private static final String LEAGUE_CLIENT_EXECUTABLE_POSTFIX = "/LeagueClient.app";
    private static final String[] LOOKUP_COMMANDS = new String[]{"/bin/sh", "-c", "ps x -o args | grep 'LeagueClientUx'"};
    private static final Logger logger = LoggerFactory.getLogger(MacOSProcessWatcher.class);

    /**
     * This method is used to get the installation directory of the League of Legends client.
     * It also contains the path of the executable.
     *
     * @return The installation directory of the League of Legends client.
     * @throws IOException If the process fails to execute.
     */
    @Override
    public CompletableFuture<String> getInstallDirectory() throws IOException {
        Process process = Runtime.getRuntime().exec(LOOKUP_COMMANDS);
        InputStream processInputStream = process.getInputStream();
        Scanner scanner = new Scanner(processInputStream);

        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();
            if (nextLine.contains(LEAGUE_CLIENT_UX_PROCESS_NAME) && nextLine.contains(INSTALL_DIRECTORY)) {
                Matcher installDirectoryPatternMatcher = INSTALL_DIRECTORY_PATTERN.matcher(nextLine);
                if (installDirectoryPatternMatcher.find()) {
                    return CompletableFuture.completedFuture(installDirectoryPatternMatcher.group(2).trim() + LEAGUE_CLIENT_EXECUTABLE_POSTFIX);
                }
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Boolean> isApplicable() {
        if (System.getProperty("os.name").contains(MAC_OS_X)) {
            logger.debug("MacOSProcessWatcher is applicable for the current system.");
            return CompletableFuture.completedFuture(true);
        }
        logger.debug("MacOSProcessWatcher is not applicable for the current system.");
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public void stop() {

    }
}
