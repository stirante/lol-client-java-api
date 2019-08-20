package examples;

import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientConnectionListener;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AssetsExample {

    /**
     * Simple example, which shows how to access assets from LCU API
     */
    public static void main(String[] args) {
        //Initialize API
        ClientApi api = new ClientApi();
        //Add listener, which will notify us about client connection available
        api.addClientConnectionListener(new ClientConnectionListener() {
            @Override
            public void onClientConnected() {
                try {
                    //Get "v1/champion-splashes/1/1000.jpg" (Annie's centered splash art) from plugin "lol-game-data"
                    //Result will be this: http://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champion-splashes/2/2000.jpg
                    InputStream input = api.getAsset("lol-game-data", "v1/champion-splashes/1/1000.jpg");
                    //Save file locally
                    Files.copy(input, new File("Annie.jpg").toPath(), StandardCopyOption.REPLACE_EXISTING);
                    //Close stream
                    input.close();
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
