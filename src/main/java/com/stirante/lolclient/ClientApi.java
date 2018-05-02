package com.stirante.lolclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import generated.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientApi {

    private static final Pattern INSTALL_DIR = Pattern.compile(".+\"--install-directory=([a-zA-Z_0-9- :.\\\\/]+)\".+");
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

    public ClientApi() {
        this(5000, 5000);
    }

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
                //executable has to be LeagueClientUx.exe and must containt in arguments remoting-auth-token
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
                String path = matcher.group(1) + "lockfile";
                String lockfile = readFile(path);
                if (lockfile == null) throw new IllegalStateException("Couldn't find lockfile! Check if League of Legends client properly launched.");
                String[] split = lockfile.split(":");
                token = new String(Base64.getEncoder().encode(("riot:" + split[3]).getBytes()));
                port = Integer.parseInt(matcher1.group(1));
            } else {
                throw new IllegalStateException("Couldn't find port or token!");
            }
            if (token.isEmpty() || port == 0) {
                throw new IllegalStateException("Couldn't parse port or token!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFile(String path) {
        try {
            Scanner scanner = new Scanner(new InputStreamReader(new FileInputStream(path)));
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                if (!sb.toString().isEmpty()) sb.append("\n");
                sb.append(scanner.nextLine());
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public RsoAuthAuthorization getAuth() throws IOException {
        return executeGet("/rso-auth/v1/authorization", RsoAuthAuthorization.class);
    }

    public boolean isAuthorized() throws IOException {
        try {
            return getAuth().currentAccountId > 0;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public LolSummonerSummoner getCurrentSummoner() throws IOException {
        return executeGet("/lol-summoner/v1/current-summoner", LolSummonerSummoner.class);
    }

    public String getSwaggerJson() throws IOException {
        HttpURLConnection conn = getConnection("/v2/swagger.json", "GET");
        conn.connect();
        InputStream in = conn.getInputStream();
        return dumpStream(in);
    }

    public String getOpenapiJson() throws IOException {
        HttpURLConnection conn = getConnection("/v3/openapi.json", "GET");
        conn.connect();
        InputStream in = conn.getInputStream();
        return dumpStream(in);
    }

    public PlayerNotificationResource[] getPlayerNotifications() throws IOException {
        return executeGet("/player-notifications/v1/notifications", PlayerNotificationResource[].class);
    }

    public PlayerNotificationResource addPlayerNotification(PlayerNotificationResource notification) throws IOException {
        return executePost("/player-notifications/v1/notifications", notification, PlayerNotificationResource.class);
    }

    private String dumpStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        Scanner sc = new Scanner(in);
        while (sc.hasNextLine()) {
            sb.append(sc.nextLine()).append("\n");
        }
        in.close();
        return sb.toString();
    }

    public LolChampionsCollectionsChampion[] getChampions(long summonerId) throws IOException {
        return executeGet("/lol-champions/v1/inventories/" + summonerId + "/champions", LolChampionsCollectionsChampion[].class);
    }

    public RegionLocale getRegionLocale() throws IOException {
        return executeGet("/riotclient/region-locale", RegionLocale.class);
    }

    public LolStoreWallet getWallet() throws IOException {
        return executeGet("/lol-store/v1/wallet", LolStoreWallet.class);
    }

    public boolean setRegionLocale(RegionLocale locale) throws IOException {
        return executePut("/riotclient/region-locale", locale);
    }

    public String[] getProducts() throws IOException {
        return executeGet("/patcher/v1/products", String[].class);
    }

    public PatcherProductState requestCorruptionCheck(String product) throws IOException {
        return executePost("/patcher/v1/products/" + product + "/detect-corruption-request", PatcherProductState.class);
    }

    public boolean requestPartialRepair(String product) throws IOException {
        return executePost("/patcher/v1/products/" + product + "/partial-repair-request");
    }

    public boolean requestFullRepair(String product) throws IOException {
        return executePost("/patcher/v1/products/" + product + "/full-repair-request");
    }

    public LolChatSessionResource getChatSession() throws IOException {
        return executeGet("/lol-chat/v1/session", LolChatSessionResource.class);
    }

    public <T> T executeGet(String path, Class<T> clz) throws IOException {
        HttpURLConnection conn = getConnection(path, "GET");
        conn.connect();
        InputStream in = conn.getInputStream();
        T result = GSON.fromJson(new InputStreamReader(in), clz);
        in.close();
        conn.getOutputStream().close();
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
        conn.getOutputStream().close();
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

    public boolean executePost(String path) throws IOException {
        HttpURLConnection conn = getConnection(path, "POST");
        conn.connect();
        boolean b = conn.getResponseCode() == 204;
        conn.getInputStream().close();
        conn.getOutputStream().close();
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

}
