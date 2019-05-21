package examples;

import com.google.gson.Gson;
import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientWebSocket;
import generated.LolChampSelectChampSelectPlayerSelection;
import generated.LolChampSelectChampSelectSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class InstaLockExample {

    public static void main(String[] args) throws Exception {
        //Initialize API
        ClientApi api = new ClientApi();
        //save current summoner id
        final Long id = api.getCurrentSummoner().summonerId;
        //open web socket
        ClientWebSocket socket = api.openWebSocket();
        //add event handler
        socket.setSocketListener(new ClientWebSocket.SocketListener() {
            @Override
            public void onEvent(ClientWebSocket.Event event) {
                //if current champion selection session is updated
                if (event.getUri().equalsIgnoreCase("/lol-champ-select/v1/session")) {
                    LolChampSelectChampSelectSession session = (LolChampSelectChampSelectSession) event.getData();
                    System.out.println(new Gson().toJson(session));
                    //we need to find summoner's cell id
                    LolChampSelectChampSelectPlayerSelection self =
                            session.myTeam.stream()
                                    .filter(player -> player.summonerId.equals(id))
                                    .findFirst()
                                    .orElse(null);
                    //should never be null, but i'll check just in case
                    if (self != null) {
                        for (Object actions : session.actions) {
                            for (Object action : ((List) actions)) {
                                Map<String, Object> a = (Map<String, Object>) action;
                                //no idea why, but cell id gets recognized here as Double
                                if (((Double) a.get("actorCellId")).intValue() == self.cellId.intValue() &&
                                        a.get("type").equals("pick") && a.get("completed").equals(false)) {
                                    a.put("championId", 157);//yasuo, lol 157
                                    a.put("completed", true);//lock it
                                    //patch it
                                    try {
                                        //this will throw errors if you will try to lock champion too soon
                                        //it shouldn't ban champions, when it's in ban phase, but I have no idea
                                        //how it will behave in case of draft or something like that.
                                        api.executePatch("/lol-champ-select/v1/session/actions/" +
                                                ((Double) a.get("id")).intValue(), a);
                                        return;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }

                }
            }

            @Override
            public void onClose(int code, String reason) {
            }
        });
        //close socket when user enters something into console
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.readLine();
        socket.close();
    }

}
