package generated;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class LcdsGameDTO {

	public String gameMode;
	public List<String> gameMutators;
	public String gameState;
	public String gameType;
	public Integer gameTypeConfigId;
	public Long id;
	public Integer mapId;
	public Integer maxNumPlayers;
	public String name;
	public List<LcdsPlayerParticipant> observers;
	public Long optimisticLock;
	public LcdsPlayerParticipant ownerSummary;
	public String passbackDataPacket;
	public String passbackUrl;
	public List<String> practiceGameRewardsDisabledReasons;
	public String roomName;
	public String roomPassword;
	public String spectatorsAllowed;
	public List<LcdsPlayerParticipant> teamOne;
	public List<LcdsPlayerParticipant> teamTwo;

}