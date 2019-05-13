package de.tu_clausthal.in.propra.recyclingsystem.app.components;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import de.tu_clausthal.in.propra.recyclingsystem.app.AppScope;
import de.tu_clausthal.in.propra.recyclingsystem.app.modules.ActivityModule;
import de.tu_clausthal.in.propra.recyclingsystem.app.modules.NetworkModule;
import de.tu_clausthal.in.propra.recyclingsystem.RecyclerWebservice;
import de.tu_clausthal.in.propra.recyclingsystem.app.App;

@AppScope
@Singleton
@Component(modules = {NetworkModule.class, ActivityModule.class, AndroidInjectionModule.class})
public interface AppComponent extends AndroidInjector<App> {

    public RecyclerWebservice getWebservice();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder app(App app);

        AppComponent build();
    }

    public void injectApp(App app);

}
