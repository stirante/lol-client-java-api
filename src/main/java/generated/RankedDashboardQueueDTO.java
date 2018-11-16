package generated;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class RankedDashboardQueueDTO {

	public List<LeaguesLcdsLeagueListDTO> leagues;
	public List<RankedPositionInfoDTO> positionInfos;
	public Boolean positionRanks;
	public LeaguesLcdsLeagueRank previousSeasonRank;
	public LeaguesLcdsLeagueTier previousSeasonTier;
	public LeaguesLcdsQueueType queue;

}