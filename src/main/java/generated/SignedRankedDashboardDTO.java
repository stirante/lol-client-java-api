package generated;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class SignedRankedDashboardDTO {

	public List<String> earnedRegaliaRewardIds;
	public String highestPreviousSeasonRank;
	public String highestPreviousSeasonTier;
	public String jwt;
	public List<RankedDashboardQueueDTO> queues;
	public List<SeasonSplitDTO> splits;
	public SplitsRewardProgressDTO splitsProgress;

}