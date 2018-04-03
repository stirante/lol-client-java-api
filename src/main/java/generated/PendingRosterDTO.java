package generated;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PendingRosterDTO {

	public Long captainId;
	public String invitationId;
	public List<FailedInvite> inviteFaileds;
	public List<PendingRosterInviteeDTO> invitees;
	public Integer logo;
	public Integer logoColor;
	public List<PendingRosterMemberDTO> members;
	public String name;
	public List<RewardLogo> rewardLogos;
	public String shortName;
	public List<TicketOfferDTO> ticketOffers;
	public Integer tier;
	public Long tournamentId;
	public Integer version;

}