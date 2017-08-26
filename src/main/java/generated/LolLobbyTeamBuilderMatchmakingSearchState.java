package generated;

import com.google.gson.annotations.SerializedName;

public enum LolLobbyTeamBuilderMatchmakingSearchState {

	@SerializedName("Invalid")
	INVALID,
	@SerializedName("AbandonedLowPriorityQueue")
	ABANDONEDLOWPRIORITYQUEUE,
	@SerializedName("Canceled")
	CANCELED,
	@SerializedName("Searching")
	SEARCHING,
	@SerializedName("Found")
	FOUND,
	@SerializedName("Error")
	ERROR,
	@SerializedName("ServiceError")
	SERVICEERROR,
	@SerializedName("ServiceShutdown")
	SERVICESHUTDOWN

}