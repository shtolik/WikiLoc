package mobi.stolicus.wikiloc.support;

import android.location.Location;
import android.support.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;

/**
 * Utility helper class for queries to wiki api
 * Created by shtolik on 01.07.2017.
 */

public class QueryHelper {

	private QueryHelper() {
		throw new IllegalAccessError("Utility helper class with only static methods and constants");
	}

	/**
	 * Converting location to string in format understood by wikiapi in gscoord query
	 * e.g. gscoord=37.786971|-122.399677
	 *
	 * @param location location
	 * @return string formatted for wiki gscoord query
	 */
	public static String getGscoordFromLocation(@NonNull final Location location) {
		return location.getLatitude() + "|" + location.getLongitude();
	}

	/**
	 * Preparing page ids in format understood by wikiapi in pageids query
	 *
	 * @param pageids list of page ids
	 * @return string of pages ids
	 */
	public static String getPageIdsFromList(Iterator<Integer> pageids) {
		return StringUtils.join(pageids, "|");
	}
}
