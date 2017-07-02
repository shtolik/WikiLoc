
package mobi.stolicus.wikiloc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeosearchQuery {

	@SerializedName("geosearch")
	@Expose
	public List<Geosearch> geosearch = null;

}
