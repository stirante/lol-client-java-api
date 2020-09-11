package generated;

import java.util.List;

public class LolLobbyPartyDto {

	public LolLobbyQueueRestrictionDto activeRestrictions;
	public Boolean activityLocked;
	public Long activityResumeUtcMillis;
	public Long activityStartedUtcMillis;
	public LolLobbyPartyChatDto chat;
	public Long eligibilityHash;
	public List<LolLobbyGatekeeperRestrictionDto> eligibilityRestrictions;
	public List<LolLobbyGatekeeperRestrictionDto> eligibilityWarnings;
	public LolLobbyGameModeDto gameMode;
	public Integer maxPartySize;
	public String partyId;
	public String partyType;
	public String platformId;
	public List<LolLobbyPartyMemberDto> players;
	public Long version;

}