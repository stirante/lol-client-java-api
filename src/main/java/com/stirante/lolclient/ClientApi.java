package com.stirante.lolclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.stirante.lolclient.utils.SSLUtil;
import generated.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

public class ClientApi {

    private static final Pattern INSTALL_DIR =
            Pattern.compile(".+\"--install-directory=([()a-zA-Z_0-9- :.\\\\/]+)\".+");
    private static final Gson GSON = new GsonBuilder().create();
    private static final int LIVE_PORT = 2999;
    /**
     * Enabled 'legacy' mode
     */
    private static final AtomicBoolean legacyMode = new AtomicBoolean(false);
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
     * Request config
     */
    private final RequestConfig requestConfig;
    /**
     * HTTP client
     */
    private final CloseableHttpClient client;
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

    private static CloseableHttpClient createHttpClient() throws Exception {
        return HttpClients.custom()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(SSLUtil.getSocketFactory(), (HostnameVerifier) null))
                .build();
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
        this.requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(readTimeout)
                .build();
        try {
            this.client = createHttpClient();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        start();
    }

    /**
     * @return a {@code java.lang.String} that points to the League of Legends client directory
     */
    public String getClientPath() {
        return clientPath;
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
            File lockfile = new File(new File(clientPath), "lockfile");
            startFileWatcher(lockfile.getAbsolutePath());
            if (lockfile.exists()) {
                setupApiWithLockfile();
            }
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
        ProcessWatcher.getInstance().stop();
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
            String target = ProcessWatcher.getInstance().getInstallDirectory();
            if (target == null) {
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
    private static String readFile(String path) {
        try {
            Scanner scanner = new Scanner(new InputStreamReader(new FileInputStream(path)));
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                if (!sb.toString().isEmpty()) {
                    sb.append("\n");
                }
                sb.append(scanner.nextLine());
            }
            scanner.close();
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reads {@code java.io.InputStream} content into String
     * <p>
     * From https://stackoverflow.com/a/5445161/6459649
     *
     * @param in InputStream
     * @return Text contents of {@code java.io.InputStream}
     */
    private static String dumpStream(InputStream in) {
        java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private String dumpHttpRequest(HttpRequestBase conn) throws IOException {
        try (CloseableHttpResponse response = client.execute(conn)) {
            boolean b = response.getStatusLine().getStatusCode() == 200;
            if (!b) {
                return null;
            }
            String str = dumpStream(response.getEntity().getContent());
            EntityUtils.consume(response.getEntity());
            return str;
        }
    }

    public InputStream getAsset(String plugin, String path) throws IOException {
        return executeBinaryGet(plugin + "/assets/" + URLEncoder.encode(path, StandardCharsets.UTF_8.name()));
    }

    public boolean isAuthorized() throws IOException {
        try {
            return executeGet("/lol-summoner/v1/current-summoner", LolSummonerSummoner.class).getResponseObject().accountId > 0;
        } catch (FileNotFoundException | NullPointerException e) {
            return false;
        }
    }

    public String getSwaggerJson() throws IOException {
        return dumpHttpRequest(getConnection("/swagger/v2/swagger.json", port, new HttpGet()));
    }

    public String getOpenapiJson() throws IOException {
        return dumpHttpRequest(getConnection("/swagger/v3/openapi.json", port, new HttpGet()));
    }

    public String getLiveSwaggerJson() throws IOException {
        return dumpHttpRequest(getConnection("/swagger/v2/swagger.json", LIVE_PORT, new HttpGet()));
    }

    public String getLiveOpenapiJson() throws IOException {
        return dumpHttpRequest(getConnection("/swagger/v3/openapi.json", LIVE_PORT, new HttpGet()));
    }

    public <T> ApiResponse<T> executeGet(String path, Class<T> clz) throws IOException {
        HttpGet conn = getConnection(path, port, new HttpGet());
        return getResponse(clz, conn);
    }

    public <T> ApiResponse<T> executeLiveGet(String path, Class<T> clz) throws IOException {
        HttpGet conn = getConnection(path, LIVE_PORT, new HttpGet());
        return getResponse(clz, conn);
    }

    public InputStream executeBinaryGet(String path) throws IOException {
        HttpGet conn = getConnection(path, port, new HttpGet());
        CloseableHttpResponse response = client.execute(conn);
        boolean b = response.getStatusLine().getStatusCode() == 200;
        if (!b) {
            EntityUtils.consume(response.getEntity());
            return null;
        }
        return response.getEntity().getContent();
    }

    public <T> ApiResponse<T> executeGet(String path, Class<T> clz, String... queryParams) throws IOException {
        HttpGet conn = getConnection(path, port, new HttpGet(), queryParams);
        return getResponse(clz, conn);
    }

    public ApiResponse<Void> executePut(String path, Object jsonObject) throws IOException {
        HttpPut conn = getConnection(path, port, new HttpPut());
        addJsonBody(jsonObject, conn);
        return getResponse(conn);
    }

    public <T> ApiResponse<T> executePost(String path, Object jsonObject, Class<T> clz) throws IOException {
        HttpPost conn = getConnection(path, port, new HttpPost());
        addJsonBody(jsonObject, conn);
        return getResponse(clz, conn);
    }

    public <T> ApiResponse<T> executePost(String path, Class<T> clz) throws IOException {
        HttpPost conn = getConnection(path, port, new HttpPost());
        return getResponse(clz, conn);
    }

    public ApiResponse<Void> executePost(String path, Object jsonObject) throws IOException {
        HttpPost conn = getConnection(path, port, new HttpPost());
        addJsonBody(jsonObject, conn);
        return getResponse(conn);
    }

    public ApiResponse<Void> executePatch(String path, Object jsonObject) throws IOException {
        HttpPatch conn = getConnection(path, port, new HttpPatch());
        addJsonBody(jsonObject, conn);
        return getResponse(conn);
    }

    public ApiResponse<Void> executePost(String path) throws IOException {
        return getResponse(getConnection(path, port, new HttpPost()));
    }

    public ApiResponse<Void> executeDelete(String path) throws IOException {
        return getResponse(getConnection(path, port, new HttpDelete()));
    }

    private <T extends HttpEntityEnclosingRequestBase> void addJsonBody(Object jsonObject, T method) {
        method.setEntity(
                EntityBuilder.create()
                        .setText(GSON.toJson(jsonObject))
                        .setContentType(ContentType.APPLICATION_JSON)
                        .build()
        );
    }

    private ApiResponse<Void> getResponse(HttpRequestBase method) throws IOException {
        try (CloseableHttpResponse response = client.execute(method)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String rawResponse = null;
            if (response.getEntity() != null && response.getEntity().getContentLength() > 0) {
                InputStream in = response.getEntity().getContent();
                rawResponse = dumpStream(in);
            }
            EntityUtils.consume(response.getEntity());
            return new ApiResponse<>(null, rawResponse, statusCode);
        }
    }

    private <T> ApiResponse<T> getResponse(Class<T> clz, HttpRequestBase method) throws IOException {
        try (CloseableHttpResponse response = client.execute(method)) {
            int statusCode = response.getStatusLine().getStatusCode();
            T t = null;
            String rawResponse = null;
            if (response.getEntity().getContentLength() > 0) {
                InputStream in = response.getEntity().getContent();
                rawResponse = dumpStream(in);
                if (statusCode / 200 == 1 && rawResponse != null && !rawResponse.isEmpty() && clz != Void.class) {
                    t = GSON.fromJson(rawResponse, clz);
                }
            }
            EntityUtils.consume(response.getEntity());
            return new ApiResponse<>(t, rawResponse, statusCode);
        }
    }

    /**
     * Prepares Http request with proper URL and authorization.
     * Simple usage: getConnection("/endpoint", new HttpGet(), "key1", "value1", key2", "value2")
     *
     * @param endpoint    endpoint
     * @param method      Base request
     * @param queryParams Pairs of get parameters. Must be divisible by 2.
     */
    private <T extends HttpRequestBase> T getConnection(String endpoint, int port, T method, String... queryParams) {
        if (!connected.get()) {
            throw new IllegalStateException("API not connected!");
        }
        try {
            StringBuilder sb = new StringBuilder("https://127.0.0.1:").append(port);
            if (!endpoint.startsWith("/")) {
                sb.append("/");
            }
            sb.append(endpoint);
            boolean addedParams = false;
            for (int i = 0; i < queryParams.length; i += 2) {
                if (!addedParams) {
                    sb.append("?");
                    addedParams = true;
                }
                else {
                    sb.append("&");
                }
                sb.append(queryParams[i]).append("=").append(queryParams[i + 1]);
            }
            URI uri = new URI(sb.toString());
            method.setURI(uri);
            method.addHeader("Authorization", "Basic " + token);
            method.addHeader("Accept", "*/*");
            method.setConfig(requestConfig);
            return method;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid endpoint!", e);
        }
    }

    /**
     * @deprecated Uses disallowed endpoint. Will be removed in future versions
     */
    @Deprecated
    public LolRsoAuthAuthorization getAuth() throws IOException {
        return executeGet("/rso-auth/v1/authorization", LolRsoAuthAuthorization.class).getResponseObject();
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public JsonObject getLiveGameData() throws IOException {
        return executeGet("/liveclientdata/allgamedata", JsonObject.class).getResponseObject();
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public LolSummonerSummoner getCurrentSummoner() throws IOException {
        return executeGet("/lol-summoner/v1/current-summoner", LolSummonerSummoner.class).getResponseObject();
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public PlayerNotificationsPlayerNotificationResource[] getPlayerNotifications() throws IOException {
        return executeGet("/player-notifications/v1/notifications", PlayerNotificationsPlayerNotificationResource[].class).getResponseObject();
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public PlayerNotificationsPlayerNotificationResource addPlayerNotification(PlayerNotificationsPlayerNotificationResource notification) throws IOException {
        return executePost("/player-notifications/v1/notifications", notification, PlayerNotificationsPlayerNotificationResource.class).getResponseObject();
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public LolChampionsCollectionsChampion[] getChampions(long summonerId) throws IOException {
        return executeGet(
                "/lol-champions/v1/inventories/" + summonerId + "/champions", LolChampionsCollectionsChampion[].class).getResponseObject();
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public RegionLocale getRegionLocale() throws IOException {
        return executeGet("/riotclient/region-locale", RegionLocale.class).getResponseObject();
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public LolStoreWallet getWallet() throws IOException {
        return executeGet("/lol-store/v1/wallet", LolStoreWallet.class).getResponseObject();
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public boolean setRegionLocale(RegionLocale locale) throws IOException {
        return executePut("/riotclient/region-locale", locale).isOk();
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public String[] getProducts() throws IOException {
        return executeGet("/patcher/v1/products", String[].class).getResponseObject();
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public PatcherProductState requestCorruptionCheck(String product) throws IOException {
        return executePost("/patcher/v1/products/" + product + "/detect-corruption-request", PatcherProductState.class).getResponseObject();
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public boolean requestPartialRepair(String product) throws IOException {
        return executePost("/patcher/v1/products/" + product + "/partial-repair-request").isOk();
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public boolean requestFullRepair(String product) throws IOException {
        return executePost("/patcher/v1/products/" + product + "/full-repair-request").isOk();
    }

    /**
     * @deprecated Will be removed someday. It should be moved and organized.
     */
    @Deprecated
    public LolChatSessionResource getChatSession() throws IOException {
        return executeGet("/lol-chat/v1/session", LolChatSessionResource.class).getResponseObject();
    }

    public static String generateDebugLog() {
        StringBuilder sb = new StringBuilder();
        sb.append("Created at ")
                .append(SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.FULL, SimpleDateFormat.FULL, Locale.ENGLISH).format(new Date()))
                .append("\n");
        try {
            String target = null;
            boolean found = false;
            Process process =
                    Runtime.getRuntime().exec("WMIC PROCESS WHERE name='LeagueClientUx.exe' GET commandline");
            InputStream in = process.getInputStream();
            Scanner sc = new Scanner(in);
            while (sc.hasNextLine()) {
                String s = sc.nextLine();
                sb.append(s).append("\n");
                if (s.contains("LeagueClientUx.exe") && s.contains("--install-directory=")) {
                    sb.append("Found correct process\n");
                    found = true;
                    target = s;
                    break;
                }
            }
            in.close();
            process.destroy();
            if (!found) {
                process =
                        Runtime.getRuntime().exec("WMIC PROCESS GET name,commandline /format:csv");
                in = process.getInputStream();
                sc = new Scanner(in);
                while (sc.hasNextLine()) {
                    String s = sc.nextLine();
                    sb.append(s).append("\n");
                }
                in.close();
                process.destroy();
            }
            else {
                Matcher matcher = INSTALL_DIR.matcher(target);
                if (matcher.find()) {
                    String clientPath = new File(matcher.group(1)).getAbsolutePath();
                    String path = new File(new File(clientPath), "lockfile").getAbsolutePath();
                    String lockfile = readFile(path);
                    if (lockfile == null) {
                        sb.append("Lockfile not found!\n");
                    }
                    else {
                        sb.append("Lockfile found: ");
                        sb.append(lockfile).append("\n");
                        String[] split = lockfile.split(":");
                        String password = split[3];
                        String token = new String(Base64.getEncoder().encode(("riot:" + password).getBytes()));
                        int port = Integer.parseInt(split[2]);
                        sb.append("Token: ").append(token).append("\n");
                        sb.append("Port: ").append(port).append("\n");
                        sb.append("Executing test request\n");
                        CloseableHttpClient client = createHttpClient();
                        HttpGet method = new HttpGet();
                        method.setURI(new URI("https://127.0.0.1:" + port + "/system/v1/builds"));
                        method.addHeader("Authorization", "Basic " + token);
                        method.addHeader("Accept", "*/*");
                        try (CloseableHttpResponse response = client.execute(method)) {
                            boolean b = response.getStatusLine().getStatusCode() == 200;
                            if (!b) {
                                sb.append("Status code: ")
                                        .append(response.getStatusLine().getStatusCode())
                                        .append("\n");
                            }
                            else {
                                String t = dumpStream(response.getEntity().getContent());
                                EntityUtils.consume(response.getEntity());
                                sb.append("Response: ").append(t).append("\n");
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream stream = new PrintStream(baos);
            t.printStackTrace(stream);
            sb.append(baos.toString()).append("\n");
        }

        return sb.toString();
    }

}
