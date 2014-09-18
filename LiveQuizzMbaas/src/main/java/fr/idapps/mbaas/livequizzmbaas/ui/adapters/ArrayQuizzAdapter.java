package fr.idapps.mbaas.livequizzmbaas.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import fr.idapps.mbaas.livequizzmbaas.R;
import fr.idapps.mbaas.livequizzmbaas.model.Quizz;
import fr.idapps.mbaas.livequizzmbaas.ui.listitems.QuizzLinearLayout;

/**
 * Created by guiguito on 09/06/2014.
 */
public class ArrayQuizzAdapter extends ArrayAdapter<Quizz> {


    public ArrayQuizzAdapter(Context context, int resource) {
        super(context, resource);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.linearlayout_quizz, null);
        }
        Quizz quizz = getItem(position);
        QuizzLinearLayout quizzLinearLayout = (QuizzLinearLayout) convertView;
        quizzLinearLayout.setQuizz(quizz);
        return convertView;
    }
}
