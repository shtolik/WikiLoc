package mobi.stolicus.wikiloc.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import mobi.stolicus.wikiloc.model.WikiArticlesResponse;
import mobi.stolicus.wikiloc.model.WikiImagesResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface WikiApi {
	String BASE_URL = "https://en.wikipedia.org/w/";
	String COMMON_PART = "api.php?action=query&";
	int GSLIMIT = 50;
	int GSRADIUS = 10000;
	String FORMAT_VERSION_2 = "2";
	int IMLIMIT = 50;

	/**
	 * defines and pass parameter to get articles based on coordinates to call retrofit with
	 * e.g. https://en.wikipedia.org/w/api.php?action=query&list=geosearch&gsradius=10000&gscoord=37.786971|-122.399677&gslimit=50&format=json
	 *
	 * @param coordinates user coordinates
	 * @return observable which will receive response with articles
	 */
	@GET(BASE_URL + COMMON_PART + "list=geosearch&gsradius=" + GSRADIUS + "&gslimit=" + GSLIMIT + "&format=json" + "&formatversion=" + FORMAT_VERSION_2)
	Observable<WikiArticlesResponse> getArticles(@NonNull @Query("gscoord") final String coordinates);

	/**
	 * defines and pass parameter to get articles based on coordinates to call retrofit with
	 * e.g. https://en.wikipedia.org/w/api.php?action=query&list=geosearch&gsradius=10000&gscoord=37.786971|-122.399677&gslimit=50&format=json
	 *
	 * @param coordinates user coordinates
	 * @param _continue continue type(?) e.g. "||"
	 * @param imcontinue place from were to continue, e.g. "105343|Kauppakeskus_Myyrmannin_etupiha.jpg"
	 * @return observable which will receive response with articles
	 */
	@GET(BASE_URL + COMMON_PART + "list=geosearch&gsradius=" + GSRADIUS + "&gslimit=" + GSLIMIT + "&format=json" + "&formatversion=" + FORMAT_VERSION_2)
	Observable<WikiArticlesResponse> getArticlesContinue(@NonNull @Query("gscoord") final String coordinates, @Nullable @Query("continue") final String _continue, @Nullable @Query("imcontinue") final String imcontinue);

	/**
	 * defines and pass parameter to get images from articles ids call retrofit with
	 * e.g. https://en.wikipedia.org/w/api.php?action=query&prop=images&pageids=18618509&format=json
	 * <p>
	 * using FORMAT_VERSION_2, because it changes action=query's "pages" to be an array, instead of an object with page ids as keys that can be difficult to iterate.
	 *
	 * @param ids article page ids
	 * @return observable which will receive response with images
	 */
	@GET(BASE_URL + COMMON_PART + "prop=images&format=json" + "&formatversion=" + FORMAT_VERSION_2)
	Observable<WikiImagesResponse> getImages(@NonNull @Query("pageids") final String ids);

	/**
	 * defines and pass parameter to get images from articles ids call retrofit with
	 * e.g. https://en.wikipedia.org/w/api.php?action=query&prop=images&pageids=18618509&format=json
	 * <p>
	 * using FORMAT_VERSION_2, because it changes action=query's "pages" to be an array, instead of an object with page ids as keys that can be difficult to iterate.
	 *
	 * @param ids article page ids
	 * @param _continue continue type(?) e.g. "||"
	 * @param imcontinue place from were to continue, e.g. "105343|Kauppakeskus_Myyrmannin_etupiha.jpg"
	 * @return observable which will receive response with images
	 */
	@GET(BASE_URL + COMMON_PART + "prop=images&format=json" + "&formatversion=" + FORMAT_VERSION_2 + "&imlimit=" + IMLIMIT)
//	Observable<WikiImagesResponse> getImagesContinue(@NonNull @Query("pageids") final String ids, @Nullable @Query("imcontinue") final String imcontinue);
	Observable<WikiImagesResponse> getImagesContinue(@NonNull @Query("pageids") final String ids, @Nullable @Query("continue") final String _continue, @Nullable @Query("imcontinue") final String imcontinue);

}
