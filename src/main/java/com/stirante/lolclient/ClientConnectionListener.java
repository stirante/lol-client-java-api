package com.stirante.lolclient;

public interface ClientConnectionListener {

    /**
     * Called when client is connected. Is also called, when adding listener to connected API.
     */
    void onClientConnected();

    /**
     * Called when client is disconnected.
     */
    void onClientDisconnected();

}
