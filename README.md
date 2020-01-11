lol-client-java-api
----------
[![Build Status](https://travis-ci.org/stirante/lol-client-java-api.svg?branch=master)](https://travis-ci.org/stirante/lol-client-java-api) [![lol-client-java-api](https://jitpack.io/v/stirante/lol-client-java-api.svg)](https://jitpack.io/#stirante/lol-client-java-api)
----------

Simple library which provides access to internal League of Legends Client API.

## Disclaimer
This product is not endorsed, certified or otherwise approved in any way by Riot Games, Inc. or any of its affiliates.

## Requirements

**lol-client-java-api** requires at least Java 8 and works only on Windows.

## Setup

This project is available on [Jitpack](https://jitpack.io/#stirante/lol-client-java-api/1.1.7)

### Gradle

Add Jitpack to your root build.gradle at the end of repositories:

```java
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Add the project as a dependency:

```java
dependencies {
	compile 'com.github.stirante:lol-client-java-api:1.1.7'
}
```

### Maven

Add Jitpack as a repository:

```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
```

Add the project as a dependency:

```xml
<dependency>
    <groupId>com.github.stirante</groupId>
    <artifactId>lol-client-java-api</artifactId>
    <version>1.1.7</version>
</dependency>
```

## Usage

This library depends on League of Legends client and requires it to be open while using this API.

```java
package examples;

import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientConnectionListener;
import generated.LolChampionsCollectionsChampion;
import generated.LolChampionsCollectionsChampionSkin;
import generated.LolSummonerSummoner;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SkinListExample {

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd-MM-yyyy");

    /**
     * Simple example, which show all owned champions and skins (with purchase date)
     */
    public static void main(String[] args) {
        //Initialize API
        ClientApi api = new ClientApi();
        api.addClientConnectionListener(new ClientConnectionListener() {
            @Override
            public void onClientConnected() {
                try {
                    //Check if user is logged in
                    if (!api.isAuthorized()) {
                        System.out.println("Not logged in!");
                        return;
                    }
                    //Get current summoner
                    LolSummonerSummoner summoner = api.executeGet("/lol-summoner/v1/current-summoner", LolSummonerSummoner.class);
                    //Get champion collection of summoner
                    LolChampionsCollectionsChampion[] champions = api.executeGet(
                            "/lol-champions/v1/inventories/" + summoner.summonerId + "/champions",
                            LolChampionsCollectionsChampion[].class);
                    for (LolChampionsCollectionsChampion champion : champions) {
                        if (champion.ownership.owned) {
                            System.out.println(champion.name + " purchased on " +
                                    FORMATTER.format(new Date(champion.ownership.rental.purchaseDate)));
                            for (LolChampionsCollectionsChampionSkin skin : champion.skins) {
                                if (!skin.isBase && skin.ownership.owned) {
                                    System.out.println("\t" + skin.name + " purchased on " +
                                            FORMATTER.format(new Date(skin.ownership.rental.purchaseDate)));
                                }
                            }
                        }
                    }
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
```

This library is still under development and lacks many features. Right now to access them, use these methods.

```java
package examples;

import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientConnectionListener;
import generated.LolChatUserResource;

import java.io.IOException;

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

```

All possible paths can be found in ```api.getSwaggerJson()``` or ```api.getOpenapiJson()```. (In order to enable this, you need to add ```enable_swagger: true``` to ```Riot Games\League of Legends\RADS\projects\league_client\releases\<latest version>\deploy\system.yaml```)

All classes in ```generated``` package were generated from OpenAPI JSON. To regenerate all classes, run League of 
Legends client with access to swagger and run
```
mvn clean compile exec:java
```

All examples are in ```examples``` package.

Library contains very simple command line interface which can be used like this
```
java -jar lol-client-java-api.jar -p PATH -m METHOD
```
Example:
```
java -jar lol-client-java-api.jar -p rso-auth/v1/authorization -m GET
```

Library also allows for listening to events from League of Legends client
```java
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
```

## Contributing
All contributions are appreciated.
If you would like to contribute to this project, please send a pull request.

## Contact
Have a suggestion, complaint, or question? Open an [issue](https://github.com/stirante/lol-client-java-api/issues).
