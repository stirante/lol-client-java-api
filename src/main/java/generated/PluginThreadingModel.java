package generated;

import com.google.gson.annotations.SerializedName;

public enum PluginThreadingModel {

	@SerializedName("dedicated")
	DEDICATED,
	@SerializedName("sequential")
	SEQUENTIAL,
	@SerializedName("concurrent")
	CONCURRENT,
	@SerializedName("parallel")
	PARALLEL

}