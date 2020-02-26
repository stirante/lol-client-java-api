package examples.pojo;

import com.google.gson.annotations.SerializedName;

public enum EventName {

    @SerializedName("BaronKill")
    BARON_KILLED,
    @SerializedName("ChampionKill")
    CHAMPION_KILL,
    @SerializedName("DragonKill")
    DRAGON_KILLED,
    @SerializedName("FirstBlood")
    FIRST_BLOOD,
    @SerializedName("FirstBrick")
    FIRST_TOWER,
    @SerializedName("GameEnd")
    GAME_END,
    @SerializedName("GameStart")
    GAME_START,
    @SerializedName("HeraldKill")
    HERALD_KILLED,
    @SerializedName("InhibKilled")
    INHIBITOR_KILLED,
    @SerializedName("InhibRespawned")
    INHIBITOR_RESPAWNED,
    @SerializedName("InhibRespawningSoon")
    INHIBITOR_RESPAWNING_SOON,
    @SerializedName("MinionsSpawning")
    MINIONS_SPAWNING,
    @SerializedName("Multikill")
    MULTIKILL,
    @SerializedName("Ace")
    TEAM_ACE,
    @SerializedName("TurretKilled")
    TOWER_KILLED,

}