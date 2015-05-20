package it.arduin.tables.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import it.arduin.tables.R;
import it.arduin.tables.model.TableHolder;
import it.arduin.tables.view.activity.DatabaseViewActivity;

/**
 * Created by a on 15/12/2014.
 */
public class DatabaseViewAdapter extends RecyclerView.Adapter<DatabaseViewAdapter.ViewHolder> {

    public ArrayList<TableHolder> list;
    private DatabaseViewActivity instance;

    public DatabaseViewAdapter(ArrayList<TableHolder> list,DatabaseViewActivity instance) {
        this.list = list;
        this.instance=instance;
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.isEmpty()?0:list.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        TableHolder d = list.get(i);
        viewHolder.name.setText(d.name);
        viewHolder.fields.setLines(d.fields.size());
        viewHolder.fields.setText(d.getFieldsString());

    }
    public void remove(int position){
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void add(TableHolder d){
        list.add(d);
        notifyItemInserted(list.size());
    }

    public DatabaseViewAdapter getThis(){
        return this;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.row_activity_database_view, viewGroup, false);

        return new ViewHolder(itemView);
    }

    public TableHolder getItemAt(int position) {
        return list.get(position);
    }
    public void empty(){
        list=new ArrayList<>();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView fields;
        public ViewHolder(final View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.txtName);
            fields = (TextView) v.findViewById(R.id.txtFields);

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    instance.showOptionsMenu(name.getText().toString(), getPosition());
                    return true;
                }
            });



        }


    }
}