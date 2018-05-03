package generated;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class RankedScoutingDTO {

	public Long playerId;
	public List<ChampionMasteryPublicDTO> topMasteries;
	public List<ChampionScoutingDTO> topSeasonChampions;
	public Long totalMasteryScore;

}