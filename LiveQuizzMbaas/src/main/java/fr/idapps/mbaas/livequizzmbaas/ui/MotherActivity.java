package fr.idapps.mbaas.livequizzmbaas.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import fr.idapps.mbaas.livequizzmbaas.mbaas.Mbaas;
import fr.idapps.mbaas.livequizzmbaas.ui.fragments.DialogProgressFragment;
import fr.idapps.mbaas.livequizzmbaas.utils.LiveQuizzMbaasConstants;

public class MotherActivity extends Activity {

    EventBus mEventBus;
    DialogFragment mProgressFragment;

    private static final String DIALOG_LOADER = "DIALOG_LOADER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBus = EventBus.getDefault();
        mEventBus.register(this);
        mProgressFragment = DialogProgressFragment.newInstance();
    }

    public void onEventMainThread(Mbaas.MbaasEvent event){

    }

    SharedPreferences getSharedPreferences(){
        return getSharedPreferences(LiveQuizzMbaasConstants.SHARED_PREFS,MODE_PRIVATE);
    }

    SharedPreferences.Editor getSharedPreferencesEditor(){
        return getSharedPreferences(LiveQuizzMbaasConstants.SHARED_PREFS,MODE_PRIVATE).edit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
    }

    protected void injectViews(){
        ButterKnife.inject(this);
    }

    void showLoader() {
        mProgressFragment.show(getFragmentManager(), DIALOG_LOADER);
    }

    void hideLoader() {
        mProgressFragment.dismiss();
    }

}
