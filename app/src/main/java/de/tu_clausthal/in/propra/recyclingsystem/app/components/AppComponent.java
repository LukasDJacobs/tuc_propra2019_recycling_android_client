package de.tu_clausthal.in.propra.recyclingsystem.app.components;

import android.content.Context;

import dagger.Component;
import de.tu_clausthal.in.propra.recyclingsystem.app.AppContext;
import de.tu_clausthal.in.propra.recyclingsystem.app.AppScope;
import de.tu_clausthal.in.propra.recyclingsystem.app.modules.ContextModule;
import de.tu_clausthal.in.propra.recyclingsystem.app.modules.NetworkModule;
import de.tu_clausthal.in.propra.recyclingsystem.RecyclerWebservice;
import de.tu_clausthal.in.propra.recyclingsystem.app.App;

@AppScope
@Component(modules = {ContextModule.class, NetworkModule.class})
public interface AppComponent {

    public RecyclerWebservice getWebservice();

    @AppContext
    public Context getContext();

    public void injectApp(App app);

}
