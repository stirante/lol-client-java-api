
package examples.pojo;

import com.google.gson.annotations.Expose;

public class ActivePlayer {

    @Expose
    private Abilities abilities;
    @Expose
    private ChampionStats championStats;
    @Expose
    private Double currentGold;
    @Expose
    private FullRunes fullRunes;
    @Expose
    private Long level;
    @Expose
    private String summonerName;

    public Abilities getAbilities() {
        return abilities;
    }

    public void setAbilities(Abilities abilities) {
        this.abilities = abilities;
    }

    public ChampionStats getChampionStats() {
        return championStats;
    }

    public void setChampionStats(ChampionStats championStats) {
        this.championStats = championStats;
    }

    public Double getCurrentGold() {
        return currentGold;
    }

    public void setCurrentGold(Double currentGold) {
        this.currentGold = currentGold;
    }

    public FullRunes getFullRunes() {
        return fullRunes;
    }

    public void setFullRunes(FullRunes fullRunes) {
        this.fullRunes = fullRunes;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

}
