package examples;

import com.stirante.lolclient.ClientApi;
import generated.LolChatUserResource;

import java.io.IOException;

@SuppressWarnings("deprecation")
public class LegacyModeExample {

    /**
     * Simple example, which shows how to use API with legacy mode
     */
    public static void main(String[] args) throws IOException {
        //Set legacy mode
        ClientApi.setLegacyMode(true);
        //Initialize API
        ClientApi api = new ClientApi();
        //Get current user chat info
        LolChatUserResource user = api.executeGet("/lol-chat/v1/me", LolChatUserResource.class);
        //Print status message
        System.out.println(user.statusMessage);
        api.stop();
    }
}
