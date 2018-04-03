package generated;

import com.google.gson.annotations.SerializedName;

public enum SubState {

	@SerializedName("SUGGESTED")
	SUGGESTED,
	@SerializedName("PENDING")
	PENDING,
	@SerializedName("DECLINED")
	DECLINED,
	@SerializedName("REVOKED")
	REVOKED,
	@SerializedName("NOT_READY")
	NOT_READY,
	@SerializedName("FORCED_NOT_READY")
	FORCED_NOT_READY,
	@SerializedName("READY")
	READY

}