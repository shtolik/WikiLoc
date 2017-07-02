package mobi.stolicus.wikiloc.support;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mobi.stolicus.wikiloc.model.Geosearch;
import mobi.stolicus.wikiloc.model.Image;
import mobi.stolicus.wikiloc.model.Page;
import mobi.stolicus.wikiloc.model.WikiArticlesResponse;
import mobi.stolicus.wikiloc.model.WikiImagesResponse;

/**
 * for manipulating models
 * Created by shtolik on 01.07.2017.
 */

public class ModelHelper {

	private static final Logger logger = LoggerFactory.getLogger(ModelHelper.class);

	private static final String REGEX_FOR_FILTERING_OUT = "(File:)|(.jpg)|(.png)|(.svg)";

	private ModelHelper() {
		throw new IllegalAccessError("Utility helper class with only static methods and constants");
	}

	/**
	 * getting titles of article returned in wiki response
	 *
	 * @param articlesResponse model of parsed response from wiki
	 * @return string with a list of article's titles
	 */
	@NonNull
	public static String getTitlesFromArticles(@Nullable WikiArticlesResponse articlesResponse) {
		StringBuilder titlesList = new StringBuilder();
		if (articlesResponse == null || articlesResponse.query == null || articlesResponse.query.geosearch == null) {
			return "";
		}
		for (Geosearch art : articlesResponse.query.geosearch) {
			titlesList.append(art.title).append("\n");
		}
		return titlesList.toString();
	}

	/**
	 * finding ids of images in a wiki response with articles
	 *
	 * @param articlesResponse model of parsed response from wiki
	 * @return list of article's ids
	 */
	@NonNull
	public static List<Integer> getImageIdsFromArticles(WikiArticlesResponse articlesResponse) {
		List<Integer> articleIdsList = new ArrayList<>();

		if (articlesResponse == null || articlesResponse.query == null || articlesResponse.query.geosearch == null) {
			return articleIdsList;
		}

		for (Geosearch art : articlesResponse.query.geosearch) {
			articleIdsList.add(art.pageid);
		}

		return articleIdsList;
	}

	/**
	 * getting image model out of WikiImagesResponse model
	 *
	 * @param wikiImagesResponse model parsed wiki reply
	 * @return list of all image names
	 */
	@NonNull
	public static List<Image> getImageNamesFromWikiImages(WikiImagesResponse wikiImagesResponse) {
		List<Image> imageModel = new ArrayList<>();
		if (wikiImagesResponse == null || wikiImagesResponse.query == null || wikiImagesResponse.query.pages == null) {
			return imageModel;
		}

		for (Page page : wikiImagesResponse.query.pages) {
			if (page.images == null || page.images.isEmpty()) {
				logger.trace("/getImageNamesFromWikiImages/no images in page:{}", page.title);
				continue;
			}

			logger.trace("/getImageNamesFromWikiImages/got {} images in page:{}", page.images.size(), page.title);
			for (Image image : page.images) {
				//remove extensions
				image.setStripped(image.getTitle().replaceAll(REGEX_FOR_FILTERING_OUT, ""));
				imageModel.add(image);
			}
		}
		return imageModel;
	}

	/**
	 * getting titles (and evaluations if available) of images returned in wiki response
	 *
	 * @param images                 images
	 * @param similarWithEvaluations true if needs to include calculated evaluations
	 * @return string with a list of article's titles
	 */
	@NonNull
	public static String getImageTitlesString(Iterator<Image> images, boolean similarWithEvaluations) {
		StringBuilder titlesList = new StringBuilder();
		while (images.hasNext()) {
			Image image = images.next();
			if (similarWithEvaluations) {
				titlesList.append(image.getStripped());
				titlesList.append(":").append(image.getMeanAverageOfEvaluation());
			} else {
				titlesList.append(image.getTitle());
			}
			titlesList.append("\n");
		}
		return titlesList.toString();
	}
}
