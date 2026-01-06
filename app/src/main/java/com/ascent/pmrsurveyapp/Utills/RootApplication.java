package com.ascent.pmrsurveyapp.Utills;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class RootApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

       // FontsOverride.setDefaultFont(this, "DEFAULT", "Latinotype_Aestetico_Light.otf");
        //FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Latinotype_AesteticoBold.otf");
        FontsOverride.setDefaultFont(this, "DEFAULT", "Fonts/Latinotype_Aestetico_Regular.otf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "Fonts/Latinotype_AesteticoBold.otf");
//        FontsOverride.setDefaultFont(this, "SERIF", "fonts/raleway.ttf");
//        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/raleway.ttf");
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("pmrdb.realm") // Optional: Specify a file name
                .schemaVersion(1)      // Optional: Increment when schema changes
                .deleteRealmIfMigrationNeeded() // Optional: Handle migrations
                .allowWritesOnUiThread(true)
                .build();
        Realm.setDefaultConfiguration(config);

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
