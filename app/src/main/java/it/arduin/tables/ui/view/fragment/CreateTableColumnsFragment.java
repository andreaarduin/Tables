package it.arduin.tables.ui.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.arduin.tables.R;
import it.arduin.tables.ui.view.activity.TableCreateActivity;
import it.arduin.tables.model.ColumnSettingsHolder;
import it.arduin.tables.ui.view.adapter.ColumnSettingsAdapter;
import it.arduin.tables.utils.RecyclerViewUtils;
import it.arduin.tables.utils.ViewUtils;
import it.arduin.tables.ui.view.adapter.CreateTableColumnSettingsAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateTableColumnsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateTableColumnsFragment extends Fragment {
    @InjectView(R.id.list) RecyclerView mRecyclerView;
    @InjectView(R.id.fab) com.melnykov.fab.FloatingActionButton fab;
    private CreateTableColumnSettingsAdapter mAdapter;
    RecyclerViewUtils.ShowHideToolbarOnScrollingListener showHideToolbarListener;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateTableColumnsFragment.
     */
    public static CreateTableColumnsFragment newInstance() {
        CreateTableColumnsFragment fragment = new CreateTableColumnsFragment();
        return fragment;
    }

    public CreateTableColumnsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if(showHideToolbarListener==null) return;
        if(!isVisibleToUser) showHideToolbarListener.toolbarAnimateShow(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_table_columns, container, false);
        ButterKnife.inject(this, v);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.list);
        return v;
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ViewUtils.setRecyclerViewAnimator(mRecyclerView);
        mAdapter = new CreateTableColumnSettingsAdapter(new ArrayList<ColumnSettingsHolder>(),getActivity());
        mRecyclerView.setAdapter(mAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.add(new ColumnSettingsHolder("LOL"));
            }
        });
        TableCreateActivity t = (TableCreateActivity) getActivity();
        showHideToolbarListener = new RecyclerViewUtils.ShowHideToolbarOnScrollingListener(t.getToolbarContainer());
        mRecyclerView.addOnScrollListener(showHideToolbarListener);
    }

    public ArrayList<ColumnSettingsHolder> getColumns(){
        ArrayList<ColumnSettingsHolder> list = new ArrayList<>();
        if(mRecyclerView==null) return list;
        for(int i=0;i<mRecyclerView.getChildCount();i++){
            View v =  mRecyclerView.getChildAt(i);
            ColumnSettingsAdapter.ViewHolder view = new ColumnSettingsAdapter.ViewHolder(v);
            list.add(new ColumnSettingsHolder(view));
        }
        return list;
    }
}
