package mobi.stolicus.wikiloc.di;

import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * provides OkHttp
 * Created by shtolik on 06.01.2017.
 */
@Module(includes = {ContextModule.class})
public class LocationModule {

	@Provides
	@Singleton
	FusedLocationProviderClient provideFusedLocation(Context context) {
		return LocationServices.getFusedLocationProviderClient(context);
	}
}
