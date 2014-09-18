package fr.idapps.mbaas.livequizzmbaas.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import fr.idapps.mbaas.livequizzmbaas.R;
import fr.idapps.mbaas.livequizzmbaas.mbaas.Mbaas;
import fr.idapps.mbaas.livequizzmbaas.model.Answer;
import fr.idapps.mbaas.livequizzmbaas.model.Question;
import fr.idapps.mbaas.livequizzmbaas.model.Quizz;
import fr.idapps.mbaas.livequizzmbaas.ui.listitems.AnswerButtonLinearLayout;
import fr.idapps.mbaas.livequizzmbaas.utils.GLog;
import fr.idapps.mbaas.livequizzmbaas.utils.LiveQuizzMbaasConstants;

/**
 * Play the quizz.
 * <p/>
 * Created by guiguito on 25/05/2014.
 */
public class PlayQuizzActivity extends MotherActivity {

    private static final int REQUEST_ANSWER_QUESTION = 1;
    Quizz mQuizz;

    @InjectView(R.id.mWaitingLinearLayout)
    LinearLayout mWaitingLinearLayout;

    //free question
    @InjectView(R.id.mAnswerFreeQuestionLinearLayout)
    LinearLayout mAnswerFreeQuestionLinearLayout;

    @InjectView(R.id.mFreeQuestionTextDisplayTextView)
    TextView mFreeQuestionTextDisplayTextView;

    @InjectView(R.id.mAnswerFreeQuestionEditText)
    EditText mAnswerFreeQuestionEditText;

    @InjectView(R.id.mAnswerFreeQuestionButton)
    Button mAnswerFreeQuestionButton;

    //QCM question
    @InjectView(R.id.mAnswerQuestionLinearLayout)
    LinearLayout mAnswerQuestionLinearLayout;

    @InjectView(R.id.mAnswersQuestionLinearLayout)
    LinearLayout mAnswersQuestionLinearLayout;

    @InjectView(R.id.mQuestionTextDisplayTextView)
    TextView mQuestionTextDisplayTextView;

    //Street view question
    @InjectView(R.id.mStreetViewQuestionLinearLayout)
    LinearLayout mStreetViewQuestionLinearLayout;

    @InjectView(R.id.mStreetView)
    StreetViewPanoramaView mStreetView;

    @InjectView(R.id.mStreetViewQuestionTextView)
    TextView mStreetViewQuestionTextView;

    @InjectView(R.id.mMapView)
    MapView mMapView;

    Marker mMarker;

    private static final LatLng SAN_FRAN = new LatLng(37.765927, -122.449972);

    Firebase mQuizzReference;
    Firebase mQuizzStatusReference;
    Firebase mQuestionsReference;
    Firebase mCurrentQuestionReference;
    Firebase mParticipantReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_quizz);
        injectViews();
        mQuizz = (Quizz) getIntent().getParcelableArrayExtra(LiveQuizzMbaasConstants.KEY_QUIZZ)[0];
        //start in firebase
        mQuizzReference = new Firebase("https://livequizzmbaas.firebaseio.com/quizzes/" + mQuizz.getId());
        mQuizzStatusReference = mQuizzReference.child("status");
        mQuizzStatusReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null || dataSnapshot.getValue().equals(LiveQuizzMbaasConstants.STATUS_FINISHED)) {
                    Toast.makeText(PlayQuizzActivity.this, getString(R.string.quizz_finished), Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(PlayQuizzActivity.this, getString(R.string.quizz_finished), Toast.LENGTH_LONG).show();
                finish();
            }
        });
        mQuestionsReference = mQuizzReference.child("questions");
        mQuestionsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //ignore
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                checkQuestions(dataSnapshot, s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //ignore
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //ignore
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(PlayQuizzActivity.this, getString(R.string.quizz_finished), Toast.LENGTH_LONG).show();
                finish();
            }
        });

        mStreetViewQuestionLinearLayout.setVisibility(View.GONE);

        MapsInitializer.initialize(this);
        mMapView.onCreate(savedInstanceState);
        mStreetView.onCreate(savedInstanceState);
        setUpStreetViewPanoramaIfNeeded();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(SAN_FRAN);
        mMapView.getMap().animateCamera(cameraUpdate);

        mMapView.getMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                updateMapAnswer(latLng);
            }
        });

        //load active question
        mQuestionsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> questionsDataSnapshotIterable = dataSnapshot.getChildren();
                for (DataSnapshot dataSnapshot1 : questionsDataSnapshotIterable) {
                    if (dataSnapshot1.child("status") != null && dataSnapshot1.child("status").getValue() != null
                            && dataSnapshot1.child("status").getValue().equals(LiveQuizzMbaasConstants.STATUS_STARTED)) {
                        checkQuestions(dataSnapshot1, null);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //user presence
        mParticipantReference = mQuizzReference.child("participants/" + Mbaas.getInstance().getUserId());
        mParticipantReference.child("name").setValue(Mbaas.getInstance().getUserName());
        mParticipantReference.onDisconnect().removeValue();

        //map views
        mMapView.onResume();
        mStreetView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //user presence
        mParticipantReference.removeValue();
        //maps
        mMapView.onPause();
        mStreetView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mStreetView.onDestroy();
    }

    private void updateMapAnswer(LatLng latLng) {
        if (mMarker != null) {
            mMarker.remove();
        }
        mMarker = mMapView.getMap().addMarker(new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.quizz_proposed_place)));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
        mMapView.getMap().animateCamera(cameraUpdate);
    }

    private void setUpStreetViewPanoramaIfNeeded() {
        if (mStreetView != null) {
            mStreetView.getStreetViewPanorama().setPanningGesturesEnabled(true);
            mStreetView.getStreetViewPanorama().setUserNavigationEnabled(false);
            mStreetView.getStreetViewPanorama().setZoomGesturesEnabled(true);
            mStreetView.getStreetViewPanorama().setStreetNamesEnabled(false);
        }
    }

    private void checkQuestions(DataSnapshot dataSnapshot, String s) {
        GLog.d(this, dataSnapshot.getValue().toString());
        //check that there is not already my result
        boolean alreadyAnswered = false;
        Iterable<DataSnapshot> results = dataSnapshot.child("results").getChildren();
        for (DataSnapshot result : results) {
            if (result.child("userId").getValue().equals(Mbaas.getInstance().getUserId())) {
                alreadyAnswered = true;
                break;
            }
        }

        if (dataSnapshot.child("status").getValue().equals(LiveQuizzMbaasConstants.STATUS_STARTED) && !alreadyAnswered) {
            mCurrentQuestionReference = dataSnapshot.getRef();
            mCurrentQuestionReference.child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null && !dataSnapshot.getValue().equals(LiveQuizzMbaasConstants.STATUS_STARTED)) {
                        mAnswersQuestionLinearLayout.removeAllViews();
                        mWaitingLinearLayout.setVisibility(View.VISIBLE);
                        mAnswerQuestionLinearLayout.setVisibility(View.GONE);
                        mAnswerFreeQuestionLinearLayout.setVisibility(View.GONE);
                        mStreetViewQuestionLinearLayout.setVisibility(View.GONE);
                        Toast.makeText(PlayQuizzActivity.this, getString(R.string.quizz_question_finished), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    mCurrentQuestionReference = null;
                }
            });
            Question questionObject = new Question();
            questionObject.setType((String) dataSnapshot.child("type").getValue());
            questionObject.setQuestionText((String) dataSnapshot.child("questionText").getValue());
            questionObject.setLatitude((Double) dataSnapshot.child("latitude").getValue());
            questionObject.setLongitude((Double) dataSnapshot.child("longitude").getValue());
            questionObject.setStatus((String) dataSnapshot.child("status").getValue());
            List<String> solutions = new ArrayList<String>();
            Iterable<DataSnapshot> solutionsList = dataSnapshot.child("solutions").getChildren();
            for (DataSnapshot snapshot : solutionsList) {
                solutions.add((String) snapshot.getValue());
            }
            questionObject.setSolutions(solutions);

            hideKeyboard();
            mWaitingLinearLayout.setVisibility(View.GONE);
            if (questionObject.getType().equals(getResources().getStringArray(R.array.questions_types_array)[1])) {
                //QCM questions
                mQuestionTextDisplayTextView.setText(questionObject.getQuestionText());
                mAnswersQuestionLinearLayout.removeAllViews();
                for (String answer : solutions) {
                    AnswerButtonLinearLayout layout = (AnswerButtonLinearLayout) getLayoutInflater().inflate(R.layout.linearlayout_answer_button_admin, null);
                    layout.setAnswer(answer);
                    mAnswersQuestionLinearLayout.addView(layout);
                }
                mAnswerQuestionLinearLayout.setVisibility(View.VISIBLE);
            } else if (questionObject.getType().equals(getResources().getStringArray(R.array.questions_types_array)[2])) {
                //street view question type
                if (mMarker != null) {
                    mMarker.remove();
                }
                mMarker = null;
                mStreetViewQuestionTextView.setText(questionObject.getQuestionText());
                LatLng latLng = new LatLng(questionObject.getLatitude(), questionObject.getLongitude());
                mStreetView.getStreetViewPanorama().setPosition(latLng);
                mStreetViewQuestionLinearLayout.setVisibility(View.VISIBLE);
            } else {
                //free answer
                mFreeQuestionTextDisplayTextView.setText(questionObject.getQuestionText());
                mAnswerFreeQuestionEditText.setText("");
                mAnswerFreeQuestionLinearLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @OnClick(R.id.mAnswerFreeQuestionButton)
    public void onAnswerFreeQuestionClicked() {
        if (!TextUtils.isEmpty(mAnswerFreeQuestionEditText.getText().toString())) {
            Answer answer = new Answer();
            answer.setUserId(Mbaas.getInstance().getUserId());
            answer.setUserName(Mbaas.getInstance().getUserName());
            answer.setAnswer(mAnswerFreeQuestionEditText.getText().toString());
            mCurrentQuestionReference.child("results").push().setValue(answer);
            Toast.makeText(this, getString(R.string.quizz_question_answered), Toast.LENGTH_LONG).show();
            mWaitingLinearLayout.setVisibility(View.VISIBLE);
            mAnswerFreeQuestionLinearLayout.setVisibility(View.GONE);
            hideKeyboard();
        } else {
            Toast.makeText(this, getString(R.string.quizz_question_enter_answer), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(AnswerButtonLinearLayout.AnwserButtonLinearLayoutEvent event) {
        Answer answer = new Answer();
        answer.setUserId(Mbaas.getInstance().getUserId());
        answer.setUserName(Mbaas.getInstance().getUserName());
        answer.setAnswer(event.answer);
        mCurrentQuestionReference.child("results").push().setValue(answer);
        Toast.makeText(this, getString(R.string.quizz_question_answered), Toast.LENGTH_LONG).show();
        mAnswersQuestionLinearLayout.removeAllViews();
        mWaitingLinearLayout.setVisibility(View.VISIBLE);
        mAnswerQuestionLinearLayout.setVisibility(View.GONE);
    }

    @OnClick(R.id.mAnswerMapQuestionButton)
    public void onAnswerMapQuestionClicked() {
        if (mMarker != null) {
            Answer answer = new Answer();
            answer.setUserId(Mbaas.getInstance().getUserId());
            answer.setUserName(Mbaas.getInstance().getUserName());
            answer.setLatitude(mMarker.getPosition().latitude);
            answer.setLongitude(mMarker.getPosition().longitude);
            mCurrentQuestionReference.child("results").push().setValue(answer);
            Toast.makeText(this, getString(R.string.quizz_question_answered), Toast.LENGTH_LONG).show();
            mWaitingLinearLayout.setVisibility(View.VISIBLE);
            mStreetViewQuestionLinearLayout.setVisibility(View.GONE);
            if (mMarker != null) {
                mMarker.remove();
            }
            mMarker = null;
        } else {
            Toast.makeText(this, getString(R.string.quizz_question_pick_place), Toast.LENGTH_LONG).show();
        }
    }
}