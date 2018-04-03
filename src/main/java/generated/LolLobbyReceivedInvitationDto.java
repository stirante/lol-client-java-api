package generated;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class LolLobbyReceivedInvitationDto {

	public Boolean canAcceptInvitation;
	public Long fromSummonerId;
	public String fromSummonerName;
	public LolLobbyReceivedInvitationGameConfigDto gameConfig;
	public String invitationId;
	public List<LolLobbyEligibilityRestriction> restrictions;
	public LolLobbyLobbyInvitationState state;
	public String timestamp;

}