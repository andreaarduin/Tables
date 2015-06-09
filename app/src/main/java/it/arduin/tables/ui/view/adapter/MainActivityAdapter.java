package it.arduin.tables.ui.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.arduin.tables.model.DatabaseHolder;
import it.arduin.tables.R;
import it.arduin.tables.model.SharedPreferencesOperations;
import it.arduin.tables.ui.view.activity.MainActivity;

/**
 * Created by a on 05/12/2014.
 */
public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder>{

    private List<DatabaseHolder> data;
    private int rowLayout;
    private MainActivity instance;
    private Context c;

    public MainActivityAdapter(List<DatabaseHolder> d, int rowLayout, MainActivity a) {
        this.data = d;
        this.rowLayout = rowLayout;
        this.instance=a;
        c=a;
    }
    public DatabaseHolder getItemAt(int i){
        return data.get(i);
    }
    public void insert(DatabaseHolder d){
        new SharedPreferencesOperations(c).addAndSaveDatabase(d.getPath(),d.getName(), data.size());
        data.add(d);
        notifyItemInserted(data.size());
    }
    public void add(DatabaseHolder d){
        data.add(d);
        notifyItemInserted(data.size());
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    public void forget(int position){
        new SharedPreferencesOperations(c).forgetDatabase(position, data.size());
        data.remove(position);
        notifyItemRemoved(position);
    }
    public void flush(){
        data=new ArrayList<>();
        new SharedPreferencesOperations(c).deleteAll();
        notifyDataSetChanged();
    }
    public void empty(){
        data=new ArrayList<>();
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        DatabaseHolder c = data.get(i);
        viewHolder.dText.setText(c.getName());
        viewHolder.dDesc.setText(c.getPath());
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dText,dDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            dText = (TextView) itemView.findViewById(R.id.txt);
            dDesc = (TextView) itemView.findViewById(R.id.desc);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    instance.showForgetDatabaseAlert(getPosition());
                    return false;
                }
            });
        }

    }
}