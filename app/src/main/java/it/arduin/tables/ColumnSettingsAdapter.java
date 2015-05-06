package it.arduin.tables;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.List;

/**
 * Created by a on 05/12/2014.
 */
public class ColumnSettingsAdapter extends RecyclerView.Adapter<ColumnSettingsAdapter.ViewHolder>{

    static private List<ColumnSettingsHolder> data;
    Context c;

    public ColumnSettingsAdapter(List<ColumnSettingsHolder> d,Context c) {
        data = d;
        this.c = c;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void remove(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void add(ColumnSettingsHolder obj) {
        int position=data.size();
        data.add(position, obj);
        notifyItemInserted(position);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ColumnSettingsHolder d = data.get(i);
        viewHolder.name.setText(d.getName());
        Spinner dropdown= viewHolder.type;
        String[] items = new String[]{"INTEGER", "TEXT", "BLOB","REAL","NUMERIC"};
        ArrayAdapter adapter = new ArrayAdapter<>(c, R.layout.support_simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_column_settings, viewGroup, false);
        final ViewHolder view=new ViewHolder((itemView));
       /*itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColumnSettingsHolder c=new ColumnSettingsHolder(view);
                Toast.makeText(CreateTableActivity.c,c.getColumnDefinition(),Toast.LENGTH_LONG).show();
            }
        });*/
        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public EditText name;
        public Spinner type;
        public Switch ai,pk,nn,un;
        public ViewHolder(View v) {
            super(v);
            name = (EditText) v.findViewById(R.id.name);
            type = (Spinner) v.findViewById(R.id.type);
            ai = (Switch) v.findViewById(R.id.autoinc);
            pk = (Switch) v.findViewById(R.id.primarykey);
            nn = (Switch) v.findViewById(R.id.notnull);
            un = (Switch) v.findViewById(R.id.unique);
        }
    }
}