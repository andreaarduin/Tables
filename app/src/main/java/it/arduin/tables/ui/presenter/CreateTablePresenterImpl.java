package it.arduin.tables.ui.presenter;

import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import it.arduin.tables.model.ColumnSettingsHolder;
import it.arduin.tables.model.TableStructure;
import it.arduin.tables.utils.DBUtils;
import it.arduin.tables.ui.view.activity.CreateTableActivity;
import it.arduin.tables.ui.view.adapter.ColumnSettingsAdapter;

/**
 * Created by Andrea on 20/05/2015.
 */
public class CreateTablePresenterImpl implements CreateTablePresenter {
    CreateTableActivity mActivity;

    public CreateTablePresenterImpl(CreateTableActivity mActivity) {
        this.mActivity = mActivity;
    }

    public void onFabPressed() {
        ColumnSettingsAdapter adapter=(ColumnSettingsAdapter)mActivity.mRecyclerView.getAdapter();
        adapter.add(new ColumnSettingsHolder(""));
    }

    public void onBackButtonPressed() {
        NavUtils.navigateUpTo(mActivity,mActivity.intent);
        mActivity.finish();
    }

    public void onActionConfirmPressed() {
        createTable();
        NavUtils.navigateUpTo(mActivity,mActivity.intent);
        mActivity.finish();
    }

    public void createTable() {
        try {
            String tablename = mActivity.tableName.getText().toString();
            ArrayList<ColumnSettingsHolder> columns = new ArrayList<>();
            ArrayList<ColumnSettingsHolder> primaryKeys = new ArrayList<>();
            ArrayList<ColumnSettingsHolder> uniques = new ArrayList<>();
            for (int i = 0; i <  mActivity.mRecyclerView.getAdapter().getItemCount(); i++) {
                View v =  mActivity.mRecyclerView.getChildAt(i);
                ColumnSettingsAdapter.ViewHolder view = new ColumnSettingsAdapter.ViewHolder(v);
                ColumnSettingsHolder data = new ColumnSettingsHolder(view);
                if(data.primaryKey) primaryKeys.add(data);
                if(data.getUnique()) uniques.add(data);
                columns.add(data);
            }
            TableStructure t = new TableStructure(tablename,columns,primaryKeys,uniques);
            DBUtils.createTable( mActivity.db.getPath(), t);
        }
        catch(Exception e){
            Toast.makeText( mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
