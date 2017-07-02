package mobi.stolicus.wikiloc.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mobi.stolicus.wikiloc.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * provides OkHttp
 * Created by shtolik on 06.01.2017.
 */
@Module(includes = {ContextModule.class})
public class OkHttpModule {

	@Provides
	@Singleton
	OkHttpClient provideOkHttpClient(OkHttpClient.Builder builder) {
		return builder.build();
	}

	/**
	 * Provides retrofit with http client which will use accessToken, but fallback
	 * to basic auth to get new token, if it's missing or expired
	 *
	 * @return okHttpClient properly setup for the job
	 */

	@Provides
	@Singleton
	OkHttpClient.Builder provideOkHttpClientBuilder() {
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		httpClient.addInterceptor(chain -> {
			Request originalRequest = chain.request();
			Request.Builder requestBuilder = originalRequest.newBuilder()
					.header("Accept", "application/json");
			requestBuilder.header("User-Agent", "android:mobi.stolicus.wikiloc:v" + BuildConfig.VERSION_NAME + "(by stolicus)");
			requestBuilder.method(originalRequest.method(), originalRequest.body());

			return chain.proceed(requestBuilder.build());

		});

		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		if (BuildConfig.IS_DEBUG)
			interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		else
			interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
		httpClient.addInterceptor(interceptor);
		return httpClient;
	}

}
