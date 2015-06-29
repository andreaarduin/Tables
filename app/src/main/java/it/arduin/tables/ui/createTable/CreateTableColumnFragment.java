package it.arduin.tables.ui.createTable;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.arduin.tables.R;
import it.arduin.tables.model.ColumnSettingsHolder;
import it.arduin.tables.ui.RecyclerViewUtils;
import it.arduin.tables.ui.ViewUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateTableColumnFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateTableColumnFragment extends Fragment {
    @InjectView(R.id.type)
    Spinner dropdown;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateTableColumnFragment.
     */
    public static CreateTableColumnFragment newInstance() {
        return new CreateTableColumnFragment();
    }

    public CreateTableColumnFragment() {
        // Required clear public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_table_columns, container, false);
        ButterKnife.inject(this, v);
        return v;
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        String[] items = new String[]{"INTEGER", "TEXT", "BLOB","REAL","NUMERIC"};
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }

    public ColumnSettingsHolder getColumn(){
        View v =  getView().findViewById(R.id.root);
        if(v == null) Toast.makeText(this.getActivity(),"FSAas",Toast.LENGTH_LONG).show();
        ColumnSettingsAdapter.ViewHolder view = new ColumnSettingsAdapter.ViewHolder(v);
        return new ColumnSettingsHolder(view);
    }
}
