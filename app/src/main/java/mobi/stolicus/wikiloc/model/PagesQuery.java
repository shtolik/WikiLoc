
package mobi.stolicus.wikiloc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PagesQuery {

	@SerializedName("pages")
	@Expose
	public List<Page> pages = null;

}
