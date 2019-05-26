package examples;

import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientConnectionListener;
import com.stirante.lolclient.ClientWebSocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WebSocketExample {

    private static ClientWebSocket socket;

    /**
     * Simple example showing how to receive websocket events from client
     */
    public static void main(String[] args) throws Exception {
        //Initialize API
        ClientApi api = new ClientApi();
        api.addClientConnectionListener(new ClientConnectionListener() {
            @Override
            public void onClientConnected() {
                System.out.println("Client connected");
                try {
                    //open web socket
                    socket = api.openWebSocket();
                    //add event handler, which prints every received event
                    socket.setSocketListener(new ClientWebSocket.SocketListener() {
                        @Override
                        public void onEvent(ClientWebSocket.Event event) {
                            System.out.println(event);
                        }

                        @Override
                        public void onClose(int code, String reason) {
                            System.out.println("Socket closed, reason: " + reason);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClientDisconnected() {
                System.out.println("Client disconnected");
                socket.close();
            }
        });
        //close socket when user enters something into console
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.readLine();
        api.stop();
        socket.close();
    }

}
