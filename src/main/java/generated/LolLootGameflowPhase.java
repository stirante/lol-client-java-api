package generated;

import com.google.gson.annotations.SerializedName;

public enum LolLootGameflowPhase {

	@SerializedName("None")
	NONE,
	@SerializedName("Lobby")
	LOBBY,
	@SerializedName("Matchmaking")
	MATCHMAKING,
	@SerializedName("ReadyCheck")
	READYCHECK,
	@SerializedName("ChampSelect")
	CHAMPSELECT,
	@SerializedName("GameStart")
	GAMESTART,
	@SerializedName("FailedToLaunch")
	FAILEDTOLAUNCH,
	@SerializedName("InProgress")
	INPROGRESS,
	@SerializedName("Reconnect")
	RECONNECT,
	@SerializedName("WaitingForStats")
	WAITINGFORSTATS,
	@SerializedName("PreEndOfGame")
	PREENDOFGAME,
	@SerializedName("EndOfGame")
	ENDOFGAME,
	@SerializedName("TerminatedInError")
	TERMINATEDINERROR

}