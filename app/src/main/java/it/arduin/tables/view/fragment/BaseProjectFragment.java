package it.arduin.tables.view.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import it.arduin.tables.R;

/**
 * Created by Andrea on 26/05/2015.
 */
public class BaseProjectFragment extends Fragment {
    public View rootView;
    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        rootView=getView();
    }
    public Context getContext(){
        return getActivity().getApplicationContext();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

}
