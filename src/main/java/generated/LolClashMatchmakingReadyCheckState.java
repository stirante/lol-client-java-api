package generated;

import com.google.gson.annotations.SerializedName;

public enum LolClashMatchmakingReadyCheckState {

	@SerializedName("Invalid")
	INVALID,
	@SerializedName("InProgress")
	INPROGRESS,
	@SerializedName("EveryoneReady")
	EVERYONEREADY,
	@SerializedName("StrangerNotReady")
	STRANGERNOTREADY,
	@SerializedName("PartyNotReady")
	PARTYNOTREADY,
	@SerializedName("Error")
	ERROR

}