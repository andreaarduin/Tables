package it.arduin.tables.ui.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import it.arduin.tables.R;
import it.arduin.tables.model.SimpleTextPair;

/**
 * Created by a on 15/12/2014.
 */
public class SimpleTextAdapter extends RecyclerView.Adapter<SimpleTextAdapter.ViewHolder> {

    public ArrayList<SimpleTextPair> list;

    public SimpleTextAdapter() {
        this.list = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.isEmpty()?0:list.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String d = list.get(i).getDesc();
        String t = list.get(i).getText();
        viewHolder.desc.setText(d);
        viewHolder.text.setText(t);
    }
    public void remove(int position){
        list.remove(position);
        notifyItemRemoved(position);
    }
    public void add(String d,String t){
        list.add(new SimpleTextPair(d,t));
        notifyItemInserted(list.size());
    }
    public SimpleTextAdapter getThis(){
        return this;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.row_simple_text, viewGroup, false);

        return new ViewHolder(itemView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView desc,text;
        public ViewHolder(final View v) {
            super(v);
            text = (TextView) v.findViewById(R.id.text);
            desc = (TextView) v.findViewById(R.id.desc);
        }
    }
}