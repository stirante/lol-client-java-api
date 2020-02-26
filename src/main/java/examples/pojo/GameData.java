
package examples.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;

public class GameData {

    @Expose
    private ActivePlayer activePlayer;
    @Expose
    private List<Player> players;
    @Expose
    private Events events;
    @Expose
    private GameData gameData;
    @Expose
    private String gameMode;
    @Expose
    private Double gameTime;
    @Expose
    private String mapName;
    @Expose
    private Long mapNumber;
    @Expose
    private String mapTerrain;

    public ActivePlayer getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(ActivePlayer activePlayer) {
        this.activePlayer = activePlayer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Events getEvents() {
        return events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }

    public GameData getGameData() {
        return gameData;
    }

    public void setGameData(GameData gameData) {
        this.gameData = gameData;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public Double getGameTime() {
        return gameTime;
    }

    public void setGameTime(Double gameTime) {
        this.gameTime = gameTime;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public Long getMapNumber() {
        return mapNumber;
    }

    public void setMapNumber(Long mapNumber) {
        this.mapNumber = mapNumber;
    }

    public String getMapTerrain() {
        return mapTerrain;
    }

    public void setMapTerrain(String mapTerrain) {
        this.mapTerrain = mapTerrain;
    }

}
