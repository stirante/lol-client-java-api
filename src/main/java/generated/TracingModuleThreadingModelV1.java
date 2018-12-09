package generated;

import com.google.gson.annotations.SerializedName;

public enum TracingModuleThreadingModelV1 {

	@SerializedName("kNone")
	KNONE,
	@SerializedName("kMainThread")
	KMAINTHREAD,
	@SerializedName("kDedicated")
	KDEDICATED,
	@SerializedName("kSequential")
	KSEQUENTIAL,
	@SerializedName("kConcurrent")
	KCONCURRENT,
	@SerializedName("kParallel")
	KPARALLEL

}