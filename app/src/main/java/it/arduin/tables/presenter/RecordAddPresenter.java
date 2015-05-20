package it.arduin.tables.presenter;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import it.arduin.tables.R;
import it.arduin.tables.utils.DBUtils;
import it.arduin.tables.view.activity.RecordAddActivity;

/**
 * Created by Andrea on 20/05/2015.
 */
public class RecordAddPresenter {
    RecordAddActivity mActivity;

    public RecordAddPresenter(RecordAddActivity mActivity) {
        this.mActivity = mActivity;
    }

    public void insertNewRecord(){
        try{
            ArrayList<String> names,values;
            names = new ArrayList<>();
            values = new ArrayList<>();
            for (int i = 0; i < mActivity.mRecyclerView.getChildCount(); i++) {
                LinearLayout l=(LinearLayout)mActivity.mRecyclerView.getChildAt(i);
                EditText e=(EditText)l.getChildAt(1);
                TextView t=(TextView)l.getChildAt(0);
                names.add(t.getText().toString());
                values.add(e.getText().toString());
            }
            DBUtils.insertNewRecord(mActivity.path,mActivity.table,names,values);
            NavUtils.navigateUpTo(mActivity, mActivity.intent);
            mActivity.finish();
        }
        catch(Exception e) {
            Toast.makeText(mActivity, mActivity.getString(R.string.RecordAddActivity_error_message) + " " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onFabClick() {
        insertNewRecord();
    }

    public void onActionConfirmPressed() {
        insertNewRecord();
    }

    public void onBackButtonPressed() {
        NavUtils.navigateUpTo(mActivity, mActivity.intent);
        mActivity.finish();
    }
}
