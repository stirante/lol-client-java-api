package generated;

import java.util.Map;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PluginResource {

	public String app;
	public List<String> assetBundleNames;
	public List<PluginResourceContract> dependencies;
	public String dynLibFileName;
	public String dynLibPath;
	public String externalUri;
	public String feature;
	public String fullName;
	public List<PluginResourceContract> implementedContracts;
	public Boolean isDynamicLibraryInited;
	public Boolean isDynamicLibraryLoaded;
	public Map<String, Object> mountedAssetBundles;
	public Integer orderDynamicLibraryInited;
	public Integer orderDynamicLibraryLoaded;
	public Integer orderWADFileMounted;
	public String pluginInfoApiSemVer;
	public String shortName;
	public Boolean standalone;
	public String subtype;
	public String supertype;
	public String threadingModel;
	public String version;

}