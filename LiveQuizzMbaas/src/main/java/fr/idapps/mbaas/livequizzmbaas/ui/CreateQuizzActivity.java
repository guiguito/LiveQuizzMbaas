package fr.idapps.mbaas.livequizzmbaas.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.InjectView;
import butterknife.OnClick;
import fr.idapps.mbaas.livequizzmbaas.R;
import fr.idapps.mbaas.livequizzmbaas.mbaas.Mbaas;
import fr.idapps.mbaas.livequizzmbaas.model.Quizz;
import fr.idapps.mbaas.livequizzmbaas.utils.LiveQuizzMbaasConstants;

/**
 * Activity to create a quizz.
 * <p/>
 * Created by guiguito on 25/05/2014.
 */
public class CreateQuizzActivity extends MotherActivity {


    @InjectView(R.id.mQuizzNameEditText)
    EditText mQuizzNameEditText;

    @InjectView(R.id.mQuizzTeaserEditText)
    EditText mQuizzTeaserEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quizz);
        injectViews();
    }


    @OnClick(R.id.mCreateQuizzButton)
    void createQuizz() {
        if (checkForm()) {
            Quizz quizz = new Quizz();
            quizz.setCreatorId(Mbaas.getInstance().getUserId());
            quizz.setStatus(LiveQuizzMbaasConstants.STATUS_FINISHED);
            quizz.setName(mQuizzNameEditText.getText().toString());
            quizz.setTeaser(mQuizzTeaserEditText.getText().toString());
            Mbaas.getInstance().createQuizz(quizz);
        }
    }

    private boolean checkForm() {
        if (TextUtils.isEmpty(mQuizzNameEditText.getText().toString())) {
            Toast.makeText(this, getString(R.string.quizz_name_empty), Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(mQuizzTeaserEditText.getText().toString())) {
            Toast.makeText(this, getString(R.string.teaser_empty), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void onEventMainThread(Mbaas.CreateQuizzEvent event) {
        if (!event.isSuccessfull()) {
            Toast.makeText(this, event.exception.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            finish();
        }
    }
}
