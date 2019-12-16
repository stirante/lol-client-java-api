package generated;

import com.google.gson.annotations.SerializedName;

public enum ExternalPluginsAvailability {

	@SerializedName("NotAvailable")
	NOTAVAILABLE,
	@SerializedName("Preparing")
	PREPARING,
	@SerializedName("Connected")
	CONNECTED,
	@SerializedName("Recovering")
	RECOVERING,
	@SerializedName("Error")
	ERROR

}