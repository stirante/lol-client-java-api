package examples;

import generated.LolChampionsCollectionsChampion;
import generated.LolChampionsCollectionsChampionSkin;
import generated.LolSummonerSummoner;
import com.stirante.lolclient.ClientApi;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SkinListExample {

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd-MM-yyyy");

    /**
     * Simple example, which show all owned champions and skins (with purchase date)
     */
    public static void main(String[] args) throws IOException {
        //Initialize API
        ClientApi api = new ClientApi();
        //Check if user is logged in
        if (!api.isAuthorized()) {
            System.out.println("Not logged in!");
            return;
        }
        //Get current summoner
        LolSummonerSummoner summoner = api.getCurrentSummoner();
        //Get champion collection of summoner
        LolChampionsCollectionsChampion[] champions = api.getChampions(summoner.summonerId);
        for (LolChampionsCollectionsChampion champion : champions) {
            if (champion.ownership.owned) {
                System.out.println(champion.name + " purchased on " + FORMATTER.format(new Date(champion.ownership.rental.purchaseDate)));
                for (LolChampionsCollectionsChampionSkin skin : champion.skins) {
                    if (!skin.isBase && skin.ownership.owned) {
                        System.out.println("\t" + skin.name + " purchased on " + FORMATTER.format(new Date(skin.ownership.rental.purchaseDate)));
                    }
                }
            }
        }
    }
}
