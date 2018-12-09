package generated;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class RankedDashboardQueueDTO {

	public List<LeaguesLcdsLeagueListDTO> leagues;
	public List<RankedPositionInfoDTO> positionInfos;
	public Boolean positionRanks;
	public String previousSeasonRank;
	public String previousSeasonTier;
	public Integer provisionalGameThreshold;
	public String queue;

}