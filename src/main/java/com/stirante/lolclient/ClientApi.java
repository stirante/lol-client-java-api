package com.stirante.lolclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import generated.*;

import javax.net.ssl.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

public class ClientApi {

    private static final List<String> ALLOWED_ENDPOINTS = Arrays.asList(
            "lol-active-boosts",
            "lol-banners",
            "lol-career-stats",
            "lol-champ-select",
            "lol-champ-select-legacy",
            "lol-clubs",
            "lol-clubs-public",
            "lol-collections",
            "lol-end-of-game",
            "lol-featured-modes",
            "lol-game-client-chat",
            "lol-game-queues",
            "lol-game-settings",
            "lol-gameflow",
            "lol-highlights",
            "lol-honor-v2",
            "lol-loadouts",
            "lol-lobby",
            "lol-lobby-team-builder",
            "lol-1oot",
            "lol-loyalty",
            "lol-maps",
            "lol-matchmaking",
            "lol-missions",
            "lol-npe-rewards",
            "lol-npe-tutorial-path",
            "lol-patch",
            "lol-perks",
            "lol-pft",
            "lol-platform-config",
            "lol-player-behavior",
            "lol-player-level-up",
            "lol-summoner",
            "lol-player-messaging",
            "lol-player-preferences",
            "lol-pre-end-of-game",
            "lol-premade-voice",
            "lol-purchase-widget",
            "lol-queue-eligibility",
            "lol-ranked",
            "lol-recommendations",
            "lol-regalia",
            "lol-replays",
            "lol-service-status",
            "lol-settings",
            "lol-simple-dialog-messages",
            "lol-spectator",
            "lol-suggested-players",
            "lol-trophies"
    );
    private static final Pattern INSTALL_DIR =
            Pattern.compile(".+\"--install-directory=([()a-zA-Z_0-9- :.\\\\/]+)\".+");
    private static final Gson GSON = new GsonBuilder().create();
    /**
     * Enabled 'legacy' mode
     */
    private static final AtomicBoolean legacyMode = new AtomicBoolean(false);
    /**
     * Disables warnings about using disallowed endpoint
     */
    private static final AtomicBoolean disableEndpointWarnings = new AtomicBoolean(false);
    /**
     * Prints out all responses from client to System.out
     */
    private static final AtomicBoolean printResponse = new AtomicBoolean(false);

    /**
     * If enabled, makes it possible to use the library the way it was prior to version 1.1.0.
     * It's not guaranteed to work in future versions. In case new features would break legacy mode, I will
     * prioritize new features over this.
     */
    @Deprecated
    public static void setLegacyMode(boolean legacyMode) {
        ClientApi.legacyMode.set(legacyMode);
    }

    /**
     * Disables warnings about using disallowed endpoint
     */
    public static void setDisableEndpointWarnings(boolean disableEndpointWarnings) {
        ClientApi.disableEndpointWarnings.set(disableEndpointWarnings);
    }

    /**
     * Prints out all responses from client to System.out
     */
    public static void setPrintResponse(boolean printResponse) {
        ClientApi.printResponse.set(printResponse);
    }

    /**
     * Auth token used for requests
     */
    private String token = "";
    /**
     * Local rest api port
     */
    private int port = 0;
    /**
     * Path to the League of Legends client directory
     */
    private String clientPath;
    /**
     * Connect timeout
     */
    private int connectTimeout;
    /**
     * Read timeout
     */
    private int readTimeout;
    /**
     * Is api connected
     */
    private final AtomicBoolean connected = new AtomicBoolean(false);
    /**
     * Is file watcher started
     */
    private final AtomicBoolean fileWatcherStarted = new AtomicBoolean(false);
    /**
     * Is process watcher started
     */
    private final AtomicBoolean processWatcherStarted = new AtomicBoolean(false);
    /**
     * List of event listeners
     */
    private final Set<ClientConnectionListener> clientListeners = new HashSet<>();

    /*
     * Localhost needs HTTPS, but doesn't provide valid SSL
     */
    static {
        try {
            ignoreSSL();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        allowMethods("PATCH");
    }

    private static void ignoreSSL() throws KeyManagementException, NoSuchAlgorithmException {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }

                }
        };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    /**
     * Great workaround for java rejecting to send http requests with custom methods
     * From https://stackoverflow.com/a/46323891/6459649
     *
     * @param methods methods to allow
     */
    private static void allowMethods(String... methods) {
        try {
            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

            methodsField.setAccessible(true);

            String[] oldMethods = (String[]) methodsField.get(null);
            Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
            methodsSet.addAll(Arrays.asList(methods));
            String[] newMethods = methodsSet.toArray(new String[0]);

            methodsField.set(null/*static field*/, newMethods);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Creates and starts the League of Legends client API
     */
    public ClientApi() {
        this(null);
    }

    /**
     * Creates and starts the League of Legends client API
     *
     * @param clientPath Path to the League of Legends client directory
     */
    public ClientApi(String clientPath) {
        this(clientPath, 5000, 5000);
    }

    /**
     * Creates and starts the League of Legends client API
     *
     * @param clientPath     a {@code java.lang.String} that points to the League of Legends client directory
     * @param connectTimeout an {@code int} that specifies the connect timeout value in milliseconds
     * @param readTimeout    an {@code int} that specifies the timeout value to be used in milliseconds
     */
    public ClientApi(String clientPath, int connectTimeout, int readTimeout) {
        this.clientPath = clientPath;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        start();
    }

    /**
     * Adds client connection listener
     */
    public void addClientConnectionListener(ClientConnectionListener listener) {
        clientListeners.add(listener);
        if (isConnected()) {
            try {
                listener.onClientConnected();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    /**
     * Removes a client connection listener
     */
    public void removeClientConnectionListener(ClientConnectionListener listener) {
        clientListeners.remove(listener);
    }

    /**
     * Starts the connection with League of Legends client
     */
    public void start() {
        if (legacyMode.get()) {
            checkClientProcess();
            if (!isConnected()) {
                throw new IllegalStateException("LoL client not found!");
            }
        }
        if (clientPath == null) {
            startProcessWatcher();
        }
        else {
            startFileWatcher(new File(new File(clientPath), "lockfile").getAbsolutePath());
        }
    }

    private void setupApiWithLockfile() {
        String path = new File(new File(clientPath), "lockfile").getAbsolutePath();
        String lockfile = readFile(path);
        if (lockfile == null) {
            throw new IllegalStateException("Couldn't find lockfile! Check if League of Legends client properly launched.");
        }
        String[] split = lockfile.split(":");
        String password = split[3];
        token = new String(Base64.getEncoder().encode(("riot:" + password).getBytes()));
        port = Integer.parseInt(split[2]);
        connected.set(true);
        startFileWatcher(path);
        for (ClientConnectionListener listener : clientListeners) {
            try {
                listener.onClientConnected();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    /**
     * Stops watcher services.
     */
    public void stop() {
        fileWatcherStarted.set(false);
        processWatcherStarted.set(false);
        if (isConnected()) {
            connected.set(false);
            for (ClientConnectionListener listener : clientListeners) {
                try {
                    listener.onClientDisconnected();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    /**
     * Checks process list for LoL client instance
     *
     * @return true, if process has been found
     */
    private boolean checkClientProcess() {
        try {
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
                return false;
            }
            Matcher matcher = INSTALL_DIR.matcher(target);
            if (matcher.find()) {
                clientPath = new File(matcher.group(1)).getAbsolutePath();
                setupApiWithLockfile();
                processWatcherStarted.set(false);
                return true;
            }
            else {
                throw new IllegalStateException("Couldn't find port or token in lockfile!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Starts service, which listens for creation of League of Legends client process
     */
    private void startProcessWatcher() {
        if (processWatcherStarted.get() || legacyMode.get()) {
            return;
        }
        new Thread(() -> {
            processWatcherStarted.set(true);
            while (processWatcherStarted.get()) {
                if (!checkClientProcess()) {
                    //slow down a bit
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * Start file watcher, which listens for changes in lockfile
     *
     * @param lockfilePath absolute path to the lockfile
     */
    private void startFileWatcher(String lockfilePath) {
        if (fileWatcherStarted.get() || legacyMode.get()) {
            return;
        }
        new Thread(() -> {
            try {
                WatchService watcher = FileSystems.getDefault().newWatchService();
                File file = new File(lockfilePath);
                WatchKey key = file.getParentFile().toPath().register(watcher,
                        ENTRY_CREATE,
                        ENTRY_DELETE);
                fileWatcherStarted.set(true);
                while (fileWatcherStarted.get()) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (!event.context().toString().equals(file.getName())) {
                            continue;
                        }
                        if (isConnected()) {
                            connected.set(false);
                            for (ClientConnectionListener listener : clientListeners) {
                                try {
                                    listener.onClientDisconnected();
                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }
                            }
                        }
                        if (event.kind() != ENTRY_DELETE) {
                            setupApiWithLockfile();
                        }
                    }
                    //slow down a bit
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Is api connected to the League of Legends client
     */
    public boolean isConnected() {
        return connected.get();
    }

    /**
     * Creates and opens a websocket for receiving LoL client events
     *
     * @return connected websocket
     */
    public ClientWebSocket openWebSocket() throws Exception {
        if (!connected.get()) {
            throw new IllegalStateException("API not connected!");
        }
        return new ClientWebSocket(token, port);
    }

    /**
     * Simple method for reading text file into string
     *
     * @param path path to the file
     * @return text contents of the file
     */
    private String readFile(String path) {
        try {
            Scanner scanner = new Scanner(new InputStreamReader(new FileInputStream(path)));
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                if (!sb.toString().isEmpty()) {
                    sb.append("\n");
                }
                sb.append(scanner.nextLine());
            }
            //wow, I can't believe I forgot about closing this stream
            scanner.close();
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reads {@code java.io.InputStream} content into String
     *
     * From https://stackoverflow.com/a/5445161/6459649
     *
     * @param in InputStream
     * @return Text contents of {@code java.io.InputStream}
     */
    private String dumpStream(InputStream in) {
        java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * Decodes input stream into object
     */
    private <T> T decodeResponse(InputStream in, Class<T> clz) throws IOException {
        if (printResponse.get()) {
            String s = dumpStream(in);
            in.close();
            System.out.println(s);
            return GSON.fromJson(s, clz);
        } else {
            T result = GSON.fromJson(new InputStreamReader(in), clz);
            in.close();
            return result;
        }
    }

    public boolean isAuthorized() throws IOException {
        try {
            return executeGet("/lol-summoner/v1/current-summoner", LolSummonerSummoner.class).accountId > 0;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public String getSwaggerJson() throws IOException {
        HttpURLConnection conn = getConnection("/swagger/v2/swagger.json", "GET");
        conn.connect();
        InputStream in = conn.getInputStream();
        return dumpStream(in);
    }

    public String getOpenapiJson() throws IOException {
        HttpURLConnection conn = getConnection("/swagger/v3/openapi.json", "GET");
        conn.connect();
        InputStream in = conn.getInputStream();
        return dumpStream(in);
    }

    public <T> T executeGet(String path, Class<T> clz) throws IOException {
        HttpURLConnection conn = getConnection(path, "GET");
        conn.connect();
        T result = decodeResponse(conn.getInputStream(), clz);
        conn.disconnect();
        return result;
    }

    public boolean executePut(String path, Object jsonObject) throws IOException {
        HttpURLConnection conn = getConnection(path, "PUT");
        conn.setDoOutput(true);
        conn.connect();
        writeJson(conn, jsonObject);
        boolean b = conn.getResponseCode() == 204;
        conn.getInputStream().close();
        conn.disconnect();
        return b;
    }

    public <T> T executePost(String path, Object jsonObject, Class<T> clz) throws IOException {
        HttpURLConnection conn = getConnection(path, "POST");
        conn.setDoOutput(true);
        conn.connect();
        writeJson(conn, jsonObject);
        T result = decodeResponse(conn.getInputStream(), clz);
        conn.disconnect();
        return result;
    }

    public <T> T executePost(String path, Class<T> clz) throws IOException {
        HttpURLConnection conn = getConnection(path, "POST");
        conn.connect();
        T result = decodeResponse(conn.getInputStream(), clz);
        conn.disconnect();
        return result;
    }

    public boolean executePost(String path, Object jsonObject) throws IOException {
        HttpURLConnection conn = getConnection(path, "POST");
        conn.setDoOutput(true);
        conn.connect();
        writeJson(conn, jsonObject);
        boolean b = conn.getResponseCode() == 204;
        conn.getInputStream().close();
        conn.disconnect();
        return b;
    }

    public boolean executePatch(String path, Object jsonObject) throws IOException {
        HttpURLConnection conn = getConnection(path, "PATCH");
        conn.setDoOutput(true);
        conn.connect();
        writeJson(conn, jsonObject);
        boolean b = conn.getResponseCode() == 204;
        conn.getInputStream().close();
        conn.disconnect();
        return b;
    }

    public boolean executePost(String path) throws IOException {
        HttpURLConnection conn = getConnection(path, "POST");
        conn.connect();
        boolean b = conn.getResponseCode() == 204;
        conn.getInputStream().close();
        conn.disconnect();
        return b;
    }

    public boolean executeDelete(String path) throws IOException {
        HttpURLConnection conn = getConnection(path, "DELETE");
        conn.connect();
        boolean b = conn.getResponseCode() == 204;
        conn.getInputStream().close();
        conn.disconnect();
        return b;
    }

    private HttpURLConnection getConnection(String endpoint, String method) throws IOException {
        if (!connected.get()) {
            throw new IllegalStateException("API not connected!");
        }
        URL url = new URL("https", "127.0.0.1", port, endpoint);
        if (!disableEndpointWarnings.get()) {
            String path = url.getPath().substring(1, url.getPath().substring(1).indexOf('/') + 1);
            if (!ALLOWED_ENDPOINTS.contains(path)) {
                System.err.println("Using endpoint \"" + path + "\" which is not in the list of allowed endpoints!");
                System.err.println(
                        "If you're seeing this message while using allowed endpoint, consider opening an issue" +
                                " or pull request.");
            }
        }
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.addRequestProperty("Authorization", "Basic " + token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestMethod(method);
        conn.setReadTimeout(readTimeout);
        conn.setConnectTimeout(connectTimeout);
        return conn;
    }

    private void writeJson(HttpURLConnection conn, Object obj) throws IOException {
        OutputStream out = conn.getOutputStream();
        new DataOutputStream(out).write(GSON.toJson(obj).getBytes());
        out.flush();
        out.close();
    }

    /**
     * @deprecated Uses disallowed endpoint. Will be removed in future versions
     */
    @Deprecated
    public LolRsoAuthAuthorization getAuth() throws IOException {
        return executeGet("/rso-auth/v1/authorization", LolRsoAuthAuthorization.class);
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public LolSummonerSummoner getCurrentSummoner() throws IOException {
        return executeGet("/lol-summoner/v1/current-summoner", LolSummonerSummoner.class);
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public PlayerNotificationsPlayerNotificationResource[] getPlayerNotifications() throws IOException {
        return executeGet("/player-notifications/v1/notifications", PlayerNotificationsPlayerNotificationResource[].class);
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public PlayerNotificationsPlayerNotificationResource addPlayerNotification(PlayerNotificationsPlayerNotificationResource notification) throws IOException {
        return executePost("/player-notifications/v1/notifications", notification, PlayerNotificationsPlayerNotificationResource.class);
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public LolChampionsCollectionsChampion[] getChampions(long summonerId) throws IOException {
        return executeGet(
                "/lol-champions/v1/inventories/" + summonerId + "/champions", LolChampionsCollectionsChampion[].class);
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public RegionLocale getRegionLocale() throws IOException {
        return executeGet("/riotclient/region-locale", RegionLocale.class);
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public LolStoreWallet getWallet() throws IOException {
        return executeGet("/lol-store/v1/wallet", LolStoreWallet.class);
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public boolean setRegionLocale(RegionLocale locale) throws IOException {
        return executePut("/riotclient/region-locale", locale);
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public String[] getProducts() throws IOException {
        return executeGet("/patcher/v1/products", String[].class);
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public PatcherProductState requestCorruptionCheck(String product) throws IOException {
        return executePost("/patcher/v1/products/" + product + "/detect-corruption-request", PatcherProductState.class);
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public boolean requestPartialRepair(String product) throws IOException {
        return executePost("/patcher/v1/products/" + product + "/partial-repair-request");
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public boolean requestFullRepair(String product) throws IOException {
        return executePost("/patcher/v1/products/" + product + "/full-repair-request");
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public LolChatSessionResource getChatSession() throws IOException {
        return executeGet("/lol-chat/v1/session", LolChatSessionResource.class);
    }

}
