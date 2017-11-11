package generated;

import java.util.Map;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class LolPerksChampSelectSession {

	public List<Map<String, Object>> actions;
	public LolPerksChampSelectBannedChampions bans;
	public LolPerksChampSelectChatRoomDetails chatDetails;
	public Boolean isSpectating;
	public Long localPlayerCellId;
	public List<LolPerksChampSelectPlayerSelection> myTeam;
	public List<LolPerksChampSelectPlayerSelection> theirTeam;
	public LolPerksChampSelectTimer timer;
	public List<LolPerksChampSelectTradeContract> trades;

}