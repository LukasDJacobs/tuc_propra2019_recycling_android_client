package de.tu_clausthal.in.propra.recyclingsystem.app.components;

import android.content.Context;

import dagger.Component;
import de.tu_clausthal.in.propra.recyclingsystem.app.ActivityContext;
import de.tu_clausthal.in.propra.recyclingsystem.app.modules.ActivityModule;
import de.tu_clausthal.in.propra.recyclingsystem.app.ActivityScope;
import de.tu_clausthal.in.propra.recyclingsystem.ui.MainActivity;

@ActivityScope
@Component(modules = ActivityModule.class, dependencies = AppComponent.class)
public interface ActivityComponent {

    @ActivityContext
    Context getContext();

    void injectMainActivity(MainActivity mainActivity);

}
