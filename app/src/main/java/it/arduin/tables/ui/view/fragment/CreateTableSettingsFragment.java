package it.arduin.tables.ui.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.arduin.tables.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateTableSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateTableSettingsFragment extends Fragment {


    @InjectView(R.id.tableName)
    EditText tableName;
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
        View v = inflater.inflate(R.layout.fragment_create_table_settings, container, false);
        ButterKnife.inject(this,v);
        return v;
    }


    public String getTableName() {
        return tableName.getText().toString();
    }
}
