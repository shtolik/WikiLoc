
package mobi.stolicus.wikiloc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * generated with http://www.jsonschema2pojo.org/
 */
public class WikiArticlesResponse {

	@SerializedName("batchcomplete")
	@Expose
	public Boolean batchcomplete;

	@SerializedName("continue")
	@Expose
	public Continue _continue;

	@SerializedName("query")
	@Expose
	public GeosearchQuery query;

}
