package de.tu_clausthal.in.propra.recyclingsystem.app.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import de.tu_clausthal.in.propra.recyclingsystem.app.ActivityContext;
import de.tu_clausthal.in.propra.recyclingsystem.app.ActivityScope;
import de.tu_clausthal.in.propra.recyclingsystem.MainActivity;

@Module
public class ActivityModule {

    private MainActivity mMainActivity;

    Context context;

    public ActivityModule(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        context = mainActivity;
    }

    @Provides
    @ActivityContext
    public MainActivity provideMainActivity() {
        return mMainActivity;
    }

    @Provides
    @ActivityContext
    @ActivityScope
    public Context provideContext() {
        return context;
    }

}
