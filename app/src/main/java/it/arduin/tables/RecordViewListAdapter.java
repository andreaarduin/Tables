package it.arduin.tables;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by a on 15/12/2014.
 */
public class RecordViewListAdapter extends RecyclerView.Adapter<RecordViewListAdapter.ViewHolder> {

    private ArrayList<String> columnList,valuesList;
    private Context c;

    public RecordViewListAdapter(ArrayList<String> columnList,ArrayList<String> valuesList, Context c) {
        this.columnList = columnList;
        this.valuesList = valuesList;
        this.c=c;
    }

    @Override
    public int getItemCount() {
        return columnList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String name,value;
        name=columnList.get(i);
        value=valuesList.get(i);
        viewHolder.name.setText(name);
        viewHolder.label.setText(value);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.row_recordview, viewGroup, false);

        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public EditText label;
        public ViewHolder(View v){
            super(v);
            name=(TextView) v.findViewById(R.id.columnLabel);
            label=(EditText) v.findViewById(R.id.columnValue);
            label.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    RecordViewActivity.onUserInput(s);
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
        }
    }
}