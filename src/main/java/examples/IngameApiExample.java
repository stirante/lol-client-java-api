package examples;

import com.google.gson.GsonBuilder;
import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientConnectionListener;
import examples.pojo.GameData;
import generated.LolGameflowGameflowPhase;

public class IngameApiExample {

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
                    LolGameflowGameflowPhase phase =
                            api.executeGet("/lol-gameflow/v1/gameflow-phase", LolGameflowGameflowPhase.class).getResponseObject();
                    if (phase != LolGameflowGameflowPhase.INPROGRESS) {
                        System.out.println("You're not in game! Run this example while the game is in progress.");
                        api.stop();
                        return;
                    }
                    //Get all data about current game
                    //Almost all those models are actually not generated, because current API documentation
                    //lacks schema for an automated model generation
                    GameData data = api.executeLiveGet("/liveclientdata/allgamedata", GameData.class).getResponseObject();
                    //Print data
                    System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(data));
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
