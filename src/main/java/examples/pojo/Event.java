
package examples.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Event {

    @SerializedName("EventID")
    private Long eventID;
    @SerializedName("EventName")
    private EventName eventName;
    @SerializedName("EventTime")
    private Double eventTime;
    @SerializedName("KillerName")
    private String killerName;
    @SerializedName("TurretKilled")
    private String turretKilled;
    @SerializedName("InhibKilled")
    private String inhibKilled;
    @SerializedName("VictimName")
    private String victimName;
    @SerializedName("DragonType")
    private String dragonType;
    @SerializedName("Acer")
    private String acer;
    @SerializedName("AcingTeam")
    private String acingTeam;
    @SerializedName("InhibRespawned")
    private String inhibRespawned;
    @SerializedName("InhibRespawningSoon")
    private String inhibRespawningSoon;
    @SerializedName("Recipient")
    private String recipient;
    @SerializedName("KillStreak")
    private Long killStreak;
    @SerializedName("Stolen")
    private Boolean stolen;
    @SerializedName("Assisters")
    private List<String> assisters;

    public Long getEventID() {
        return eventID;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }

    public EventName getEventName() {
        return eventName;
    }

    public void setEventName(EventName eventName) {
        this.eventName = eventName;
    }

    public Double getEventTime() {
        return eventTime;
    }

    public void setEventTime(Double eventTime) {
        this.eventTime = eventTime;
    }

}
