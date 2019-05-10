package de.tu_clausthal.in.propra.recyclingsystem.app.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import de.tu_clausthal.in.propra.recyclingsystem.app.AppContext;
import de.tu_clausthal.in.propra.recyclingsystem.app.AppScope;

@Module
public class ContextModule {

    private final Context mContext;

    public ContextModule(Context context) {
        mContext = context;
    }

    @Provides
    @AppScope
    @AppContext
    public Context provideAppContext() {
        return mContext;
    }
}
