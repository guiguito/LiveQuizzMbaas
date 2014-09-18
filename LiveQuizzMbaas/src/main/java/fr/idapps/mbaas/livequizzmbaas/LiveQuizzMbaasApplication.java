package fr.idapps.mbaas.livequizzmbaas;

import android.app.Application;

import fr.idapps.mbaas.livequizzmbaas.mbaas.Mbaas;


/**
 * Custom application to perform some inits.
 * Created by guiguito on 25/05/2014.
 */
public class LiveQuizzMbaasApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        Mbaas.getInstance(this);
    }



}
