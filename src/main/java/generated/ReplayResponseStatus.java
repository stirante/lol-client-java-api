package generated;

import com.google.gson.annotations.SerializedName;

public enum ReplayResponseStatus {

	@SerializedName("OK")
	OK,
	@SerializedName("NOT_FOUND")
	NOT_FOUND,
	@SerializedName("PENDING")
	PENDING,
	@SerializedName("LOST")
	LOST,
	@SerializedName("EXPIRED")
	EXPIRED,
	@SerializedName("BAD_REQUEST")
	BAD_REQUEST,
	@SerializedName("INTERNAL_SERVER_ERROR")
	INTERNAL_SERVER_ERROR,
	@SerializedName("UNAUTHORIZED")
	UNAUTHORIZED,
	@SerializedName("FORBIDDEN")
	FORBIDDEN,
	@SerializedName("NOT_IMPLEMENTED")
	NOT_IMPLEMENTED

}