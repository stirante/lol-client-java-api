package com.stirante.lolclient.utils;

import com.stirante.lolclient.ClientApi;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;

public class SSLUtil {

    private static SSLSocketFactory instance;

    public static SSLSocketFactory getSocketFactory() throws Exception {
        if (instance == null) {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(ClientApi.class.getResourceAsStream("/riotgames.jks"), "nopass".toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tmf.getTrustManagers(), null);
            instance = ctx.getSocketFactory();
        }
        return instance;
    }

}
