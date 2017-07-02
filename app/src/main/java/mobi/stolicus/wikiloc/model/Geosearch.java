
package mobi.stolicus.wikiloc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geosearch {

	@SerializedName("pageid")
	@Expose
	public Integer pageid;
	@SerializedName("ns")
	@Expose
	public Integer ns;
	@SerializedName("title")
	@Expose
	public String title;
	@SerializedName("lat")
	@Expose
	public Float lat;
	@SerializedName("lon")
	@Expose
	public Float lon;
	@SerializedName("dist")
	@Expose
	public Float dist;
	@SerializedName("primary")
	@Expose
	public String primary;

}
