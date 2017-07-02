package mobi.stolicus.wikiloc.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * provides application context
 */
@Module
public class ContextModule {
	private Context mAppContext;

	public ContextModule(Context context) {
		mAppContext = context.getApplicationContext();
	}

	@Provides
	@Singleton
	public Context provideContext() {
		return mAppContext;
	}
}
