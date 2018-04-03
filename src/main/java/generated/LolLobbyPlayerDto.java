package generated;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class LolLobbyPlayerDto {

	public Long accountId;
	public String accountToken;
	public Long createdAt;
	public LolLobbyPartyDto currentParty;
	public Long eligibilityHash;
	public String inventoryToken;
	public String leaguesToken;
	public List<LolLobbyPartyMemberDto> parties;
	public String platformId;
	public String puuid;
	public Long serverUtcMillis;
	public Long summonerId;
	public String summonerToken;
	public Long version;

}