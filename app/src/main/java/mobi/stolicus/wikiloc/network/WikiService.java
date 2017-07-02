package mobi.stolicus.wikiloc.network;

import android.location.Location;
import android.support.annotation.NonNull;

import java.util.Iterator;

import javax.annotation.Nullable;

import mobi.stolicus.wikiloc.model.Continue;
import mobi.stolicus.wikiloc.model.WikiArticlesResponse;
import mobi.stolicus.wikiloc.model.WikiImagesResponse;
import mobi.stolicus.wikiloc.support.QueryHelper;
import rx.Observable;

public class WikiService {

	private WikiApi mWikiApi;

	public WikiService(WikiApi wikiApi) {
		mWikiApi = wikiApi;
	}

	/**
	 * prepares request for passing to retrofit
	 * //	gscoord=37.786971|-122.399677
	 *
	 * @param coordinate         last coordinates
	 * @param continueInResponse continue from last response, if not all data was yet sent
	 * @return observable which will receive response with articles
	 */
	public Observable<WikiArticlesResponse> getArticles(@NonNull final Location coordinate, @Nullable Continue continueInResponse) {
		if (continueInResponse != null) {
			return mWikiApi.getArticlesContinue(QueryHelper.getGscoordFromLocation(coordinate), continueInResponse._continue, continueInResponse.imcontinue);
		} else {
			return mWikiApi.getArticles(QueryHelper.getGscoordFromLocation(coordinate));
		}

	}

	/**
	 * prepares request for passing to retrofit
	 *
	 * @param pageids            pages ids
	 * @param continueInResponse continue from last response, if not all data was yet sent
	 * @return observable which will receive response with images
	 */
	public Observable<WikiImagesResponse> getImages(@NonNull final Iterator<Integer> pageids, @Nullable Continue continueInResponse) {
		if (continueInResponse != null) {
			return mWikiApi.getImagesContinue(QueryHelper.getPageIdsFromList(pageids), continueInResponse._continue, continueInResponse.imcontinue);
		}
		return mWikiApi.getImages(QueryHelper.getPageIdsFromList(pageids));
	}
}
