package generated;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class SignedRankedDashboardDTO {

	public LeaguesLcdsLeagueRank highestPreviousSeasonRank;
	public LeaguesLcdsLeagueTier highestPreviousSeasonTier;
	public String jwt;
	public List<RankedDashboardQueueDTO> queues;

}