package generated;

import java.util.Map;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class TournamentPlayerInfoDTO {

	public PlayerDTO player;
	public List<PlayerReward> playerRewards;
	public TournamentRewardConfigDTO seasonRewards;
	public Integer seasonVp;
	public List<ThemeVp> themeVps;
	public Long time;
	public List<TournamentInfoDTO> tournamentInfo;

}