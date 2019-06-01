package examples;

import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientConnectionListener;
import com.stirante.lolclient.ClientWebSocket;
import generated.VoiceChatParticipantResource;
import generated.VoiceChatSessionResource;
import generated.VoiceChatUpdateParticipantResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class VoiceVolumeExample {

    private static ClientWebSocket socket;

    public static void main(String[] args) throws Exception {
        ClientApi api = new ClientApi();
        //Listen for client connection
        api.addClientConnectionListener(new ClientConnectionListener() {
            @Override
            public void onClientConnected() {
                try {
                    //Open websocket
                    socket = api.openWebSocket();
                    socket.setSocketListener(new ClientWebSocket.SocketListener() {
                        @Override
                        public void onEvent(ClientWebSocket.Event event) {
                            //If we receive update for voice chat session, we proceed to checking all participants
                            // volume
                            if (event.getEventType().equalsIgnoreCase("update") &&
                                    event.getUri().equalsIgnoreCase("/voice-chat" +
                                            "/v2/sessions")) {
                                VoiceChatSessionResource[] res = (VoiceChatSessionResource[]) event.getData();
                                //Loop through all sessions
                                for (VoiceChatSessionResource re : res) {
                                    //Loop through all session's participants
                                    for (VoiceChatParticipantResource participant : re.participants) {
                                        //If volume isn't 50, we send an update to this participant
                                        if (participant.volume != 50) {
                                            try {
                                                api.executePut(
                                                        "/lol-premade-voice/v1/participants/" + participant.id + "/volume",
                                                        50);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
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
                socket.close();
            }
        });
        //close socket when user enters something into console
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.readLine();
        socket.close();
        api.stop();
    }
}
