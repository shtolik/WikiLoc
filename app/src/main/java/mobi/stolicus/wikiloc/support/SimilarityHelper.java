package mobi.stolicus.wikiloc.support;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.math3.stat.StatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mobi.stolicus.wikiloc.BuildConfig;
import mobi.stolicus.wikiloc.model.Image;

/**
 * Helper for finding similar names
 * Created by shtolik on 01.07.2017.
 */

public class SimilarityHelper {

	public static final int MAX_SIMILAR_COUNT = 10;
	private static final Logger logger = LoggerFactory.getLogger(SimilarityHelper.class);

	private SimilarityHelper() {
		throw new IllegalAccessError("Utility helper class with only static methods and constants");
	}

	/**
	 * Finding similar image
	 * Seems that easiest way is to calculate distance between two strings using Levenshtein
	 * algorithm in Java Commons StringUtils.getLevenshteinDistance(CharSequence s, CharSequence t)
	 *
	 * @param allImages list of all images
	 */
	public static synchronized void calculateSimilarity(@NonNull Set<Image> allImages) {
		StopWatch measure = null;
		if (BuildConfig.IS_DEBUG) {
			measure = new StopWatch();
			measure.start();
		}

		for (Image image : allImages) {
			Map<String, Double> map = calculateDistance(image, allImages);
			image.setEvaluations(map);
			if (measure != null)
				logger.debug("/calculateSimilarity/took {}", measure);
		}

		if (measure != null)
			logger.debug("/calculateSimilarity/took {}", measure);
	}

	/**
	 * @param currentImage current Image being evaluated
	 * @param allImages    all images (some of which are alrady evaluated)
	 * @return map of all images evaluated
	 */
	private static synchronized Map<String, Double> calculateDistance(Image currentImage, Set<Image> allImages) {
		Map<String, Double> map = new HashMap<>();

		int existingEvals = 0;
		int newEvals = 0;
		for (Image otherImage : allImages) {
			// can check if distance already calculated in the otherImage
			Double otherDistanceToCurrentCollection = otherImage.getEvaluations().get(currentImage.getStripped());
			if (otherDistanceToCurrentCollection != null) {
				//if there is distance already calculated for current stripped image name,
				// then picking first even if there are more than one (unlikely but possible if there are two same files)
				map.put(otherImage.getStripped(), otherDistanceToCurrentCollection);
				existingEvals++;
			} else {
				double distance = StringUtils.getLevenshteinDistance(currentImage.getStripped(), otherImage.getStripped());
				map.put(otherImage.getStripped(), distance);
				newEvals++;
			}
		}
		logger.debug("/calculateDistance/{}/new evaluations:{}, existing evaluations: {}", currentImage.getStripped(), newEvals, existingEvals);

		return map;
	}

	/**
	 * calculating mean average using apache commons math library.
	 *
	 * @param values distances
	 * @return mean average
	 */
	public static Double getMeanAverageOfEvaluations(Collection<Double> values) {

		//had to cast Double to double
		double[] evaluations = new double[values.size()];
		int i = 0;
		for (Double value : values) {
			evaluations[i] = value;
			i++;
		}
		return StatUtils.mean(evaluations);
	}

	/**
	 * @param allImages   full list of images
	 * @param limitToShow amount of images to include in result
	 * @return Top x similar images
	 */
	public static List<Image> getSortedTopSimilar(Set<Image> allImages, int limitToShow) {
		logger.debug("/getSortedTopSimilar/got {} to sort", allImages.size());

		StopWatch measure = null;
		if (BuildConfig.IS_DEBUG) {
			measure = new StopWatch();
			measure.start();
		}
		List<Image> similarImages = new ArrayList<>();
		similarImages.addAll(allImages);
		if (measure != null)
			logger.debug("/getSortedTopSimilar/from set to list took {}", measure);


		//sorts by increasing distance
		Collections.sort(similarImages, (o1, o2) -> Double.compare(o1.getMeanAverageOfEvaluation(), o2.getMeanAverageOfEvaluation()));

		if (measure != null)
			logger.debug("/getSortedTopSimilar/sorting {} records took {}", allImages.size(), measure);

		//keeping only first X with smallest average distance
		return similarImages.subList(0, similarImages.size() > limitToShow ? limitToShow : allImages.size());
	}
}
