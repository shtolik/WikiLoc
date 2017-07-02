package mobi.stolicus.wikiloc;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mobi.stolicus.wikiloc.di.AppComponent;
import mobi.stolicus.wikiloc.di.ContextModule;
import mobi.stolicus.wikiloc.di.DaggerAppComponent;

public class WikiLocApp extends Application {
	protected static final Logger logger = LoggerFactory.getLogger(WikiLocApp.class);
	private AppComponent appComponent;

	public static WikiLocApp get(Context context) {
		return (WikiLocApp) context.getApplicationContext();
	}

	@Override
	public void onCreate() {
		super.onCreate();

		appComponent = DaggerAppComponent.builder()
				.contextModule(new ContextModule(this))
				.build();

		if (BuildConfig.IS_DEBUG) {
			enableStrictMode();
			LeakCanary.install(this);
			//Enable Stetho for debugging
			Stetho.initialize(Stetho.newInitializerBuilder(this)
					.enableDumpapp(
							Stetho.defaultDumperPluginsProvider(this))
//                    .enableWebKitInspector(
//                            Stetho.defaultInspectorModulesProvider(this))
					.build());
		}

	}

	public AppComponent getAppComponent() {
		return appComponent;
	}

	private void enableStrictMode() {
		if (BuildConfig.DEBUG) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
						.detectCustomSlowCalls()
						.detectNetwork()
						.detectResourceMismatches()
						.penaltyLog()
						.penaltyFlashScreen()
						.build());
			} else {
				StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
						.detectCustomSlowCalls()
						.detectNetwork()
						.penaltyLog()
						.penaltyFlashScreen()
						.build());
			}

			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll()
					.penaltyLog()
					.build());

			logger.debug("StrictMode Initialized");
		}
	}

}
