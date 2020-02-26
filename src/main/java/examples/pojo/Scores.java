
package examples.pojo;

import com.google.gson.annotations.Expose;

public class Scores {

    @Expose
    private Long assists;
    @Expose
    private Long creepScore;
    @Expose
    private Long deaths;
    @Expose
    private Long kills;
    @Expose
    private Double wardScore;

    public Long getAssists() {
        return assists;
    }

    public void setAssists(Long assists) {
        this.assists = assists;
    }

    public Long getCreepScore() {
        return creepScore;
    }

    public void setCreepScore(Long creepScore) {
        this.creepScore = creepScore;
    }

    public Long getDeaths() {
        return deaths;
    }

    public void setDeaths(Long deaths) {
        this.deaths = deaths;
    }

    public Long getKills() {
        return kills;
    }

    public void setKills(Long kills) {
        this.kills = kills;
    }

    public Double getWardScore() {
        return wardScore;
    }

    public void setWardScore(Double wardScore) {
        this.wardScore = wardScore;
    }

}
