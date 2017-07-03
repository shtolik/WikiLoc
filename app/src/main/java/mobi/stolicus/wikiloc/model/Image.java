
package mobi.stolicus.wikiloc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

import mobi.stolicus.wikiloc.support.SimilarityHelper;

public class Image {

	@SerializedName("ns")
	@Expose
	public Integer ns;
	@SerializedName("title")
	@Expose
	public String title;

	private String stripped = "";

	private Map<String, Double> evaluations = new HashMap<>();

	private Double averageCalculated = null;

	public String getTitle() {
		return title;
	}

	public String getStripped() {
		return stripped;
	}

	public void setStripped(String stripped) {
		this.stripped = stripped;
	}

	public Map<String, Double> getEvaluations() {
		return evaluations;
	}

	public void setEvaluations(Map<String, Double> evaluations) {
		this.evaluations = evaluations;
	}

	public Double getMeanAverageOfEvaluation() {
		if (averageCalculated == null)
			averageCalculated = SimilarityHelper.getMeanAverageOfEvaluations(evaluations.values());
		return averageCalculated;
	}

//	public void addEvaluation(String name, Double distance) {
//		this.evaluations.put(name, distance);
//	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Image image = (Image) o;

		return title != null ? title.equals(image.title) : image.title == null;

	}

	@Override
	public int hashCode() {
		return title != null ? title.hashCode() : 0;
	}
}
