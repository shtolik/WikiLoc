
package mobi.stolicus.wikiloc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Page {

	@SerializedName("pageid")
	@Expose
	public Integer pageid;
	@SerializedName("ns")
	@Expose
	public Integer ns;
	@SerializedName("title")
	@Expose
	public String title;
	@SerializedName("images")
	@Expose
	public List<Image> images = null;

}
