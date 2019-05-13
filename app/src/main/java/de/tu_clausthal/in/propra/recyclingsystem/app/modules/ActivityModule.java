package de.tu_clausthal.in.propra.recyclingsystem.app.modules;


import dagger.Module;
import dagger.android.ContributesAndroidInjector;

import de.tu_clausthal.in.propra.recyclingsystem.ui.MainActivity;
import de.tu_clausthal.in.propra.recyclingsystem.ui.ScanCodeDialogActivity;

@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract ScanCodeDialogActivity contributeScanCodeDialogActivity();
}
