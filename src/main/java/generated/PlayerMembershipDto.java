package generated;

import java.util.Map;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PlayerMembershipDto {

	public List<ClubDto> activeClubs;
	public ClubsServerConfigDto clubsServerConfig;
	public List<MembershipInviteDto> pendingInvites;
	public PlayerInfoDto player;
	public PlayerMembershipPreferencesDto playerClubPreferences;
	public List<ClubReferenceDto> removedClubs;
	public String resourceUri;
	public List<ClubReferenceDto> revokedInviteClubs;
	public String secureClubPresenceInfoString;

}