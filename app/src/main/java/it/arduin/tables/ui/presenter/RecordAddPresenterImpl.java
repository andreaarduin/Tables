package it.arduin.tables.ui.presenter;

import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import it.arduin.tables.R;
import it.arduin.tables.utils.DBUtils;
import it.arduin.tables.ui.view.activity.RecordAddActivity;

/**
 * Created by Andrea on 20/05/2015.
 */
public class RecordAddPresenterImpl implements RecordAddPresenter {
    RecordAddActivity mActivity;

    public RecordAddPresenterImpl(RecordAddActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void insertNewRecord(){
        try{
            ArrayList<String> names,values;
            names = mActivity.columnNames;
            values = new ArrayList<>();
            for (int i = 0; i < mActivity.mRecyclerView.getChildCount(); i++) {
                LinearLayout l=(LinearLayout)mActivity.mRecyclerView.getChildAt(i);
                EditText e=((TextInputLayout)l.findViewById(R.id.columnValue)).getEditText();
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

    @Override
    public void onFabClick() {
        insertNewRecord();
    }

    @Override
    public void onActionConfirmPressed() {
        insertNewRecord();
    }

    @Override
    public void onBackButtonPressed() {
        NavUtils.navigateUpTo(mActivity, mActivity.intent);
        mActivity.finish();
    }
}
