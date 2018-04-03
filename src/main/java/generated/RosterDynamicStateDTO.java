package generated;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class RosterDynamicStateDTO {

	public List<PhaseInMember> members;
	public List<Long> phaseCheckinStates;
	public List<PhaseRosterSubDTO> phaseRosterSubs;
	public Long rosterId;
	public List<TicketOfferDTO> ticketOffers;
	public Long tournamentId;
	public Integer version;
	public RosterWithdraw withdraw;

}