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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientApi {

    private static final Pattern INSTALL_DIR =
            Pattern.compile(".+\"--install-directory=([()a-zA-Z_0-9- :.\\\\/]+)\".+");
    private static final Pattern PORT = Pattern.compile(".+--app-port=([0-9]+).+");
    private static final Gson GSON = new GsonBuilder().create();

    /**
     * Auth token used for requests
     */
    private String token = "";
    /**
     * Local rest api port
     */
    private int port = 0;
    /**
     * Connect timeout
     */
    private int connectTimeout;
    /**
     * Read timeout
     */
    private int readTimeout;

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

    public ClientApi() {
        this(5000, 5000);
    }

    /**
     * @param connectTimeout an {@code int} that specifies the connect timeout value in milliseconds
     * @param readTimeout an {@code int} that specifies the timeout value to be used in milliseconds
     */
    public ClientApi(int connectTimeout, int readTimeout) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        try {
            String target = "";
            //Get all processes command line
            Process process = Runtime.getRuntime().exec("wmic process get CommandLine");
            InputStream in = process.getInputStream();
            Scanner sc = new Scanner(in);
            while (sc.hasNextLine()) {
                String s = sc.nextLine();
                //executable has to be LeagueClientUx.exe and must contain in arguments remoting-auth-token
                if (s.contains("LeagueClientUx.exe") && s.contains("--install-directory=")) {
                    target = s;
                    break;
                }
            }
            in.close();
            process.destroy();
            if (target.isEmpty()) {
                throw new IllegalStateException("Couldn't find League of Legends process!");
            }
            Matcher matcher = INSTALL_DIR.matcher(target);
            Matcher matcher1 = PORT.matcher(target);
            if (matcher.find() && matcher1.find()) {
                //Base64("user:password")
                //This should fix a bug, where path would not end with '/'
                String path = new File(new File(matcher.group(1)), "lockfile").getAbsolutePath();
                String lockfile = readFile(path);
                if (lockfile == null) {
                    throw new IllegalStateException("Couldn't find lockfile! Check if League of Legends client properly launched.");
                }
                String[] split = lockfile.split(":");
                String password = split[3];
                token = new String(Base64.getEncoder().encode(("riot:" + password).getBytes()));
                port = Integer.parseInt(matcher1.group(1));
            }
            else {
                throw new IllegalStateException("Couldn't find port or token!");
            }
            if (token.isEmpty() || port == 0) {
                throw new IllegalStateException("Couldn't parse port or token!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates and opens a websocket for receiving LoL client events
     * @return connected websocket
     */
    public ClientWebSocket openWebSocket() throws Exception {
        return new ClientWebSocket(token, port);
    }

    /**
     * Simple method for reading text file into string
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
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reads {@code java.io.InputStream} content into String
     * @param in InputStream
     * @return Text contents of {@code java.io.InputStream}
     */
    private String dumpStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        Scanner sc = new Scanner(in);
        while (sc.hasNextLine()) {
            sb.append(sc.nextLine()).append("\n");
        }
        in.close();
        return sb.toString();
    }

    public LolRsoAuthAuthorization getAuth() throws IOException {
        return executeGet("/rso-auth/v1/authorization", LolRsoAuthAuthorization.class);
    }

    public boolean isAuthorized() throws IOException {
        try {
            return getAuth().currentAccountId > 0;
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
        InputStream in = conn.getInputStream();
        T result = GSON.fromJson(new InputStreamReader(in), clz);
        in.close();
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
        InputStream in = conn.getInputStream();
        T result = GSON.fromJson(new InputStreamReader(in), clz);
        in.close();
        conn.disconnect();
        return result;
    }

    public <T> T executePost(String path, Class<T> clz) throws IOException {
        HttpURLConnection conn = getConnection(path, "POST");
        conn.connect();
        InputStream in = conn.getInputStream();
        T result = GSON.fromJson(new InputStreamReader(in), clz);
        in.close();
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
        URL url = new URL("https", "127.0.0.1", port, endpoint);
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
