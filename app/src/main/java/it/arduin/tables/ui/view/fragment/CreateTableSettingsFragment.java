package it.arduin.tables.ui.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.arduin.tables.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateTableSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateTableSettingsFragment extends Fragment {


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateTableSettingsFragment.
     */
    public static CreateTableSettingsFragment newInstance() {
        CreateTableSettingsFragment fragment = new CreateTableSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public CreateTableSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_table_settings, container, false);
    }


}
