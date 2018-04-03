package generated;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PlayerClubMembership {

	public List<PlayerClub> activeClubs;
	public ClubsConfig clubsServerConfig;
	public ClubPlayer info;
	public List<ClubInvite> pendingInvites;
	public ClubPreferences preferences;
	public List<Club> removedClubs;
	public List<Club> revokedInviteClubs;
	public String secureClubPresenceInfoString;

}