package examples;

import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientConnectionListener;
import generated.LolChatUserResource;

public class DirectAccessExample {

    /**
     * Simple example, which shows how to access API directly
     */
    public static void main(String[] args) {
        //Initialize API
        ClientApi api = new ClientApi();
        //Add listener, which will notify us about client connection available
        api.addClientConnectionListener(new ClientConnectionListener() {
            @Override
            public void onClientConnected() {
                try {
                    //Get current user chat info
                    LolChatUserResource user = api.executeGet("/lol-chat/v1/me", LolChatUserResource.class);
                    //Print status message
                    System.out.println(user.statusMessage);
                    api.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClientDisconnected() {

            }
        });
    }
}
