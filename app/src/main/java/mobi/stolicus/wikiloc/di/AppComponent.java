package mobi.stolicus.wikiloc.di;

import javax.inject.Singleton;

import dagger.Component;
import mobi.stolicus.wikiloc.ui.MainActivity;

@Singleton
@Component(modules = {ContextModule.class, WikiModule.class, LocationModule.class})
public interface AppComponent {
	void inject(MainActivity mainActivity);
}
