package it.arduin.tables;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by a on 05/12/2014.
 */
public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder>{

    private List<DatabaseHolder> data;
    private int rowLayout;
    private MainActivity instance;

    public MainActivityAdapter(List<DatabaseHolder> d, int rowLayout, MainActivity a) {
        this.data = d;
        this.rowLayout = rowLayout;
        this.instance=a;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);

        return new ViewHolder(v);
    }

    public void insert(DatabaseHolder d){
        data.add(d);
        notifyItemInserted(data.size());
    }
    public void remove(int position){
        data.remove(position);
        notifyItemRemoved(position);
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
                    instance.forgetAndSaveDatabase(getPosition());
                    return false;
                }
            });
        }

    }
}