package generated;

import com.google.gson.annotations.SerializedName;

public enum PatchErrorCode {

	@SerializedName("Unspecified")
	UNSPECIFIED,
	@SerializedName("Cancelled")
	CANCELLED,
	@SerializedName("NotEnoughDiskSpace")
	NOTENOUGHDISKSPACE,
	@SerializedName("AccessDenied")
	ACCESSDENIED,
	@SerializedName("FileNotFound")
	FILENOTFOUND,
	@SerializedName("DownloaderConnection")
	DOWNLOADERCONNECTION,
	@SerializedName("DownloaderClientError")
	DOWNLOADERCLIENTERROR,
	@SerializedName("DownloaderServerError")
	DOWNLOADERSERVERERROR,
	@SerializedName("DownloaderAuthorization")
	DOWNLOADERAUTHORIZATION,
	@SerializedName("DownloadedCorruptData")
	DOWNLOADEDCORRUPTDATA,
	@SerializedName("CorruptData")
	CORRUPTDATA,
	@SerializedName("AddressResolutionFailed")
	ADDRESSRESOLUTIONFAILED

}