
package examples.pojo;

import com.google.gson.annotations.Expose;
import generated.live.TeamID;

import java.util.List;

public class Player {

    @Expose
    private String championName;
    @Expose
    private Boolean isBot;
    @Expose
    private Boolean isDead;
    @Expose
    private List<Item> items;
    @Expose
    private Long level;
    @Expose
    private String position;
    @Expose
    private String rawChampionName;
    @Expose
    private Double respawnTimer;
    @Expose
    private Runes runes;
    @Expose
    private Scores scores;
    @Expose
    private Long skinID;
    @Expose
    private String summonerName;
    @Expose
    private SummonerSpells summonerSpells;
    @Expose
    private TeamID team;

    public String getChampionName() {
        return championName;
    }

    public void setChampionName(String championName) {
        this.championName = championName;
    }

    public Boolean getIsBot() {
        return isBot;
    }

    public void setIsBot(Boolean isBot) {
        this.isBot = isBot;
    }

    public Boolean getIsDead() {
        return isDead;
    }

    public void setIsDead(Boolean isDead) {
        this.isDead = isDead;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRawChampionName() {
        return rawChampionName;
    }

    public void setRawChampionName(String rawChampionName) {
        this.rawChampionName = rawChampionName;
    }

    public Double getRespawnTimer() {
        return respawnTimer;
    }

    public void setRespawnTimer(Double respawnTimer) {
        this.respawnTimer = respawnTimer;
    }

    public Runes getRunes() {
        return runes;
    }

    public void setRunes(Runes runes) {
        this.runes = runes;
    }

    public Scores getScores() {
        return scores;
    }

    public void setScores(Scores scores) {
        this.scores = scores;
    }

    public Long getSkinID() {
        return skinID;
    }

    public void setSkinID(Long skinID) {
        this.skinID = skinID;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

    public SummonerSpells getSummonerSpells() {
        return summonerSpells;
    }

    public void setSummonerSpells(SummonerSpells summonerSpells) {
        this.summonerSpells = summonerSpells;
    }

    public TeamID getTeam() {
        return team;
    }

    public void setTeam(TeamID team) {
        this.team = team;
    }

}
