package generated;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class LolLobbyLobbyGameConfigDto {

	public List<Integer> allowablePremadeSizes;
	public String customLobbyName;
	public String customMutatorName;
	public List<String> customRewardsDisabledReasons;
	public LolLobbyQueueCustomGameSpectatorPolicy customSpectatorPolicy;
	public List<LolLobbyLobbyParticipantDto> customSpectators;
	public List<LolLobbyLobbyParticipantDto> customTeam100;
	public List<LolLobbyLobbyParticipantDto> customTeam200;
	public String gameMode;
	public String gameMutator;
	public Boolean isCustom;
	public Boolean isLobbyFull;
	public Boolean isTeamBuilderManaged;
	public Integer mapId;
	public Integer maxHumanPlayers;
	public Integer maxLobbySize;
	public Integer maxTeamSize;
	public String pickType;
	public Boolean premadeSizeAllowed;
	public Integer queueId;
	public Boolean showPositionSelector;

}