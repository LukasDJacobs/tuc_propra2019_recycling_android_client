package de.tu_clausthal.in.propra.recyclingsystem.app;

import android.app.Activity;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import de.tu_clausthal.in.propra.recyclingsystem.app.components.AppComponent;
import de.tu_clausthal.in.propra.recyclingsystem.app.components.DaggerAppComponent;

public class App extends DaggerApplication implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<App> injector;

    AppComponent appComponent;

    @Override
    public void onCreate() {
        appComponent = DaggerAppComponent.builder().app(this).build();
        appComponent.inject(this);

        super.onCreate();
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return injector;
    }

    public static App get(Activity activity) {
        return (App) activity.getApplication();
    }
}
