package generated;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class LolClashTournament {

	public Boolean allowRosterCreation;
	public String bracketSize;
	public List<Integer> buyInOptions;
	public List<Integer> buyInOptionsPremium;
	public Long endTimeMs;
	public Integer entryFee;
	public Long id;
	public Boolean isHonorRestrictionEnabled;
	public Boolean isRankedRestrictionEnabled;
	public Boolean isSmsRestrictionEnabled;
	public Boolean lastThemeOfSeason;
	public Integer maxSubstitutes;
	public String nameLocKey;
	public String nameLocKeySecondary;
	public List<LolClashTournamentPhase> phases;
	public List<ClashRewardConfigClient> rewardConfig;
	public Long rosterCreateDeadline;
	public Integer rosterSize;
	public Long scoutingDurationMs;
	public Long startTimeMs;
	public Integer themeId;
	public List<TierCheckinDelay> tierCheckinTimes;

}