package mobi.stolicus.wikiloc.di;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mobi.stolicus.wikiloc.network.WikiApi;
import mobi.stolicus.wikiloc.network.WikiService;
import retrofit2.Retrofit;

@Module(includes = {RetrofitModule.class})
public class WikiModule {

	@Provides
	@Singleton
	WikiApi provideWikiApi(Retrofit retrofit) {
		return retrofit.create(WikiApi.class);
	}

	@Provides
	@Singleton
	WikiService provideWikiService(WikiApi authApi) {
		return new WikiService(authApi);
	}
}