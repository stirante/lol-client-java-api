package generated;

import java.util.List;

public class MultiLeagueEntryChangeEventDTO {

	public Long gameId;
	public LeagueEntryChangeEventDTOV2 mainChangeEventDTO;
	public String participantId;
	public Integer previousSplitPointsRequired;
	public Integer provisionalGameThreshold;
	public String shardId;
	public Object splitPointBreakdown;
	public Integer splitPointsBeforeGame;
	public Integer splitPointsGained;
	public Integer splitPointsRequired;
	public List<SplitRewardGrantDTO> splitRewardGrants;
	public Integer upcomingSplitPointsRequired;
	public List<SplitRewardGrantDTO> upcomingSplitRewards;

}