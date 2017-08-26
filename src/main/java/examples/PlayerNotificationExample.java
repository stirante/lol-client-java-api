package examples;

import generated.PlayerNotificationResource;
import lolclient.ClientApi;

import java.io.IOException;
import java.util.HashMap;

public class PlayerNotificationExample {

    public static void main(String[] args) throws IOException {
        ClientApi api = new ClientApi();
        PlayerNotificationResource n = new PlayerNotificationResource();
        n.backgroundUrl = "images/esports_toast_background.jpg";
        n.critical = false;
        n.dismissible = true;
        n.iconUrl = "fe/lol-player-notifications/images/esports.png";
        n.type = "default";
        n.data = new HashMap<>();
        n.titleKey = "esports";
        n.detailKey = "esports_toast_watch_game_between";
        n.type = "game";
        n.source = "esports";
        n.created = "2017-08-25T17:24:19Z";
        n.data.put("matchId", "d4c278e2-10fa-4990-a669-5dfb17ca1ded");
        n.data.put("matchUrl", "http://watch.lolesports.com/en_GB/eulcs1/en?utm_source=eune_pvp_client&utm_medium=referral&utm_campaign=match_notifications");
        n.data.put("teamA", "FC Schalke 04");
        n.data.put("teamALogoUrl", "https://am-a.akamaihd.net/image/?f=https://lolstatic-a.akamaihd.net/esports-assets/production/team/fc-schalke-04-h992ofkj.png");
        n.data.put("teamB", "Ninjas in Pyjamas");
        n.data.put("teamBLogoUrl", "https://am-a.akamaihd.net/image/?f=https://lolstatic-a.akamaihd.net/esports-assets/production/team/ninjas-in-pyjamas-588ue1or.png");
        n.data.put("tournamentDescription", "EU 2017 Summer Split");
        n = api.addPlayerNotification(n);
        System.out.println(n.id);
    }

}

//example notifications

/*
  {
    "backgroundUrl": "images/esports_toast_background.jpg",
    "created": "2017-08-25T17:24:19Z",
    "critical": false,
    "data": {
      "matchId": "d4c278e2-10fa-4990-a669-5dfb17ca1ded",
      "matchUrl": "http://watch.lolesports.com/en_GB/eulcs1/en?utm_source=eune_pvp_client&utm_medium=referral&utm_campaign=match_notifications",
      "teamA": "FC Schalke 04",
      "teamALogoUrl": "https://am-a.akamaihd.net/image/?f=https://lolstatic-a.akamaihd.net/esports-assets/production/team/fc-schalke-04-h992ofkj.png",
      "teamB": "Ninjas in Pyjamas",
      "teamBLogoUrl": "https://am-a.akamaihd.net/image/?f=https://lolstatic-a.akamaihd.net/esports-assets/production/team/ninjas-in-pyjamas-588ue1or.png",
      "tournamentDescription": "EU 2017 Summer Split"
    },
    "detailKey": "esports_toast_watch_game_between",
    "dismissible": true,
    "expires": "",
    "iconUrl": "fe/lol-player-notifications/images/esports.png",
    "id": 9,
    "source": "esports",
    "state": "unread",
    "titleKey": "esports",
    "type": "game"
  }
 */

/*
  {
    "backgroundUrl": "",
    "created": "2017-08-26T08:28:30Z",
    "critical": true,
    "data": {
      "endDate": "24/08/2017",
      "redeemDate": "11/09/2017",
      "startDate": "10/08/2017"
    },
    "detailKey": "loot_adventure_arcade_third_event_notification_description",
    "dismissible": true,
    "expires": "",
    "iconUrl": "fe/lol-player-notifications/images/notifications_button_icon.png",
    "id": 0,
    "source": "lootAdventure",
    "state": "unread",
    "titleKey": "loot_adventure_arcade_event_notification_title",
    "type": "event"
  }
 */