package generated;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class MultiLeagueEntryChangeEventDTO {

	public Long gameId;
	public LeagueEntryChangeEventDTOV2 mainChangeEventDTO;
	public String multiLeagueChangeEventJwt;
	public String participantId;
	public Boolean positionRanks;
	public Integer provisionalGameThreshold;
	public String queueType;
	public Object relatedChangeEventDTOs;
	public String shardId;
	public Integer splitPointsGained;
	public List<SplitRewardGrantDTO> splitRewardGrants;

}