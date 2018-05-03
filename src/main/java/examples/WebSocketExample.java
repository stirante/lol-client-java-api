package examples;

import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientWebSocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WebSocketExample {

    /**
     * Simple example showing how to receive websocket events from client
     */
    public static void main(String[] args) throws Exception {
        //Initialize API
        ClientApi api = new ClientApi();
        //open web socket
        ClientWebSocket socket = api.openWebSocket();
        //add event handler, which prints every received event
        socket.setSocketListener(System.out::println);
        //close socket when user enters something into console
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.readLine();
        socket.close();
    }

}
