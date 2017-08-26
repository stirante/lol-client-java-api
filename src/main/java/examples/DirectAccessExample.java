package examples;

import generated.LolChatUserResource;
import com.stirante.lolclient.ClientApi;

import java.io.IOException;

public class DirectAccessExample {

    /**
     * Simple example, which shows how to access API directly
     */
    public static void main(String[] args) throws IOException {
        //Initialize API
        ClientApi api = new ClientApi();
        //Get current user chat info
        LolChatUserResource user = api.executeGet("/lol-chat/v1/me", LolChatUserResource.class);
        //Print status message
        System.out.println(user.statusMessage);
    }
}
