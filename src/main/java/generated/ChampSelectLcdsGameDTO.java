package generated;

import java.util.Map;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ChampSelectLcdsGameDTO {

	public List<BannedChampion> bannedChampions;
	public List<String> gameMutators;
	public String gameState;
	public Long id;
	public String name;
	public Long optimisticLock;
	public Integer pickTurn;
	public List<ChampSelectLcdsPlayerChampionSelectionDTO> playerChampionSelections;
	public String queueTypeName;
	public String roomName;
	public String roomPassword;
	public Long spectatorDelay;
	public String statusOfParticipants;
	public List<Map<String, Object>> teamOne;
	public List<Map<String, Object>> teamTwo;

}