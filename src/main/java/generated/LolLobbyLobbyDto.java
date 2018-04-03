package generated;

import java.util.Map;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class LolLobbyLobbyDto {

	public Boolean canStartActivity;
	public String chatRoomId;
	public String chatRoomKey;
	public LolLobbyLobbyGameConfigDto gameConfig;
	public List<LolLobbyLobbyInvitationDto> invitations;
	public LolLobbyLobbyParticipantDto localMember;
	public List<LolLobbyLobbyParticipantDto> members;
	public String partyId;
	public String partyType;
	public List<LolLobbyEligibilityRestriction> restrictions;

}