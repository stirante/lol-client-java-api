package generated.live;

import com.google.gson.annotations.SerializedName;

public enum BindingAsyncState {

	@SerializedName("None")
	NONE,
	@SerializedName("Running")
	RUNNING,
	@SerializedName("Cancelling")
	CANCELLING,
	@SerializedName("Cancelled")
	CANCELLED,
	@SerializedName("Succeeded")
	SUCCEEDED,
	@SerializedName("Failed")
	FAILED

}