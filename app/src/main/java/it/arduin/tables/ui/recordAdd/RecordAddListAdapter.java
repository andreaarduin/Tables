package it.arduin.tables.ui.recordAdd;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.arduin.tables.R;

/**
 * Created by a on 15/12/2014.
 */
public class RecordAddListAdapter extends RecyclerView.Adapter<RecordAddListAdapter.ViewHolder> {

    private ArrayList<String> columnList;

    public RecordAddListAdapter(ArrayList<String> columnList) {
        this.columnList = columnList;
    }

    @Override
    public int getItemCount() {
        return columnList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String name;
        name=columnList.get(i);
        Log.wtf("onbind", i + "  " + name);
        //viewHolder.label.setHint(name);
        viewHolder.label.getEditText().setHint(name);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.row_recordadd, viewGroup, false);

        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public android.support.design.widget.TextInputLayout label;
        public ViewHolder(View v){
            super(v);
            label=(android.support.design.widget.TextInputLayout) v.findViewById(R.id.columnValue);
        }
    }
}