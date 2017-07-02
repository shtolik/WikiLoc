
package mobi.stolicus.wikiloc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * generated from json reply with with http://www.jsonschema2pojo.org/
 */
public class WikiImagesResponse {

	@SerializedName("batchcomplete")
	@Expose
	public Boolean batchcomplete;

	@SerializedName("continue")
	@Expose
	public Continue _continue;

	@SerializedName("query")
	@Expose
	public PagesQuery query;

}
