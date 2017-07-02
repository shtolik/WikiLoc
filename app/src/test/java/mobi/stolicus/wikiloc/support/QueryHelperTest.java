package mobi.stolicus.wikiloc.support;

import android.location.Location;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * testing query related methods
 * Created by shtolik on 01.07.2017.
 */

public class QueryHelperTest {

	@Test
	public void getGscoordFromLocation() {

		Location location = mock(Location.class);

		// define return value for method getUniqueId()
		when(location.getLongitude()).thenReturn(-122.399677);
		when(location.getLatitude()).thenReturn(37.786971);

		Assert.assertEquals("37.786971|-122.399677", QueryHelper.getGscoordFromLocation(location));
	}

	@Test
	public void getPageidsStringFromList() {
		List<Integer> a = Arrays.asList(9329663, 207766695, 209693337, 209329709, 206184643, 2016947730);
		Assert.assertEquals("9329663|207766695|209693337|209329709|206184643|2016947730", QueryHelper.getPageIdsFromList(a.iterator()));
	}

}