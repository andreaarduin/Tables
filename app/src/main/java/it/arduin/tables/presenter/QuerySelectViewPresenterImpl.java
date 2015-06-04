package it.arduin.tables.presenter;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import it.arduin.tables.R;
import it.arduin.tables.utils.DBUtils;
import it.arduin.tables.view.ShortenedTextView;
import it.arduin.tables.view.activity.QuerySelectViewActivity;
import it.arduin.tables.view.activity.RecordAddActivity;
import it.arduin.tables.view.activity.RecordViewActivity;

/**
 * Created by Andrea on 18/05/2015.
 */
public class QuerySelectViewPresenterImpl implements QuerySelectViewPresenter {
    QuerySelectViewActivity mActivity;

    public QuerySelectViewPresenterImpl(QuerySelectViewActivity mActivity) {
        this.mActivity = mActivity;
    }
    @Override
    public void onFabClick(){
        mActivity.showCustomQueryAlert();
    }

    @Override
    public void openNewRecordActivity() {
        Intent intent =new Intent(mActivity,RecordAddActivity.class);
        ArrayList<String> a=new ArrayList<>(Arrays.asList(mActivity.columnNames));
        intent.putStringArrayListExtra("names", a);
        intent.putExtra("table", mActivity.table);
        intent.putExtra("path", mActivity.path);
        ArrayList<String> values= new ArrayList<>();
        for(int i=0;i<mActivity.columns;i++) values.add(a.get(i));
        intent.putStringArrayListExtra("values", values);
        mActivity.startActivityForResult(intent, QuerySelectViewActivity.NEW_RECORD);
    }
    @Override
    public void onCustomQueryAction() {
        mActivity.showCustomQueryAlert();
    }

    @Override
    public void onRowClick(final View v) {
        Intent intent=new Intent(mActivity,RecordViewActivity.class);
        intent.putExtra("customQuery",mActivity.customQuery);
        Log.d("columnNames", "" + mActivity.columnNames.length);
        intent.putStringArrayListExtra("names", new ArrayList<>(Arrays.asList(mActivity.columnNames)));
        intent.putExtra("table", mActivity.table);
        intent.putExtra("path", mActivity.path);
        ArrayList<String> values = new ArrayList<String>();
        for (int i = 0; i < mActivity.columns; i++) {
            ShortenedTextView t = (ShortenedTextView) ((TableRow) v).getChildAt(i);
            String text = t.getText();
            //text=text.substring(1,tableName.length());
            values.add(text);
        }
        intent.putExtra("values", values);
        mActivity.startActivityForResult(intent, QuerySelectViewActivity.VIEW_RECORD);
    }

    @Override
    public void openCustomQuery(final EditText input) {

        Intent intent = new Intent(mActivity, QuerySelectViewActivity.class);
        intent.putExtra("query", input.getText().toString());
        intent.putExtra("table", "Query: " + input.getText().toString());
        intent.putExtra("customQuery", true);
        intent.putExtra("path", mActivity.path);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        mActivity.startActivity(intent);
        // query=input.getText().toString();
        //initTable();
    }

    @Override
    public void onRefreshAction() {
        mActivity.loadQuery();
    }

    @Override
    public void deleteRow(final TableRow tr, View v) {
        ArrayList<String> values = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < mActivity.columns; i++) {
            TextView t = (TextView) tr.getChildAt(i);
            String text = t.getText().toString();
            values.add(text);
        }
        for( int i=0; i < mActivity.columns; i++)
            names.add(mActivity.columnNames[i]);
        try{
            DBUtils.deleteRecord(mActivity.path,mActivity.table,names,values);
            mActivity.stk.removeView(v);
        }
        catch(Exception e){
            Toast.makeText(mActivity,mActivity.getString(R.string.QuerySelectViewActivity_error_delete_record_message)+" "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
        mActivity.db.close();
    }
}
