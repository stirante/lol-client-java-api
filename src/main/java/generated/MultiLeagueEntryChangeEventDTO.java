package generated;

import java.util.List;

public class MultiLeagueEntryChangeEventDTO {

	public Long gameId;
	public LeagueEntryChangeEventDTOV2 mainChangeEventDTO;
	public String multiLeagueChangeEventJwt;
	public String participantId;
	public Boolean positionRanks;
	public Integer previousSplitPointsRequired;
	public Integer provisionalGameThreshold;
	public String queueType;
	public Object relatedChangeEventDTOs;
	public String shardId;
	public Object splitPointBreakdown;
	public Integer splitPointsBeforeGame;
	public Integer splitPointsGained;
	public Integer splitPointsRequired;
	public List<SplitRewardGrantDTO> splitRewardGrants;
	public Integer upcomingSplitPointsRequired;
	public List<SplitRewardGrantDTO> upcomingSplitRewards;

}