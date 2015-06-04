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
import it.arduin.tables.view.fragment.*;
import it.arduin.tables.view.activity.RecordAddActivity;
import it.arduin.tables.view.activity.RecordViewActivity;

/**
 * Created by Andrea on 18/05/2015.
 */
public class QuerySelectViewPresenterFragImpl implements QuerySelectViewPresenter {
    QuerySelectViewFragment mFragment;

    public QuerySelectViewPresenterFragImpl(QuerySelectViewFragment frag) {
        this.mFragment = frag;
    }
    @Override
    public void onFabClick(){
        mFragment.showCustomQueryAlert();
    }

    @Override
    public void openNewRecordActivity() {
        Intent intent =new Intent(mFragment.getActivity(),RecordAddActivity.class);
        ArrayList<String> a=new ArrayList<>(Arrays.asList(mFragment.columnNames));
        intent.putStringArrayListExtra("names", a);
        intent.putExtra("table", mFragment.table);
        intent.putExtra("path", mFragment.path);
        ArrayList<String> values= new ArrayList<>();
        for(int i=0;i< mFragment.columns;i++) values.add(a.get(i));
        intent.putStringArrayListExtra("values", values);
        mFragment.startActivityForResult(intent, QuerySelectViewFragment.NEW_RECORD);
    }
    @Override
    public void onCustomQueryAction() {
        mFragment.showCustomQueryAlert();
    }

    @Override
    public void onRowClick(final View v) {
        Intent intent=new Intent(mFragment.getActivity(),RecordViewActivity.class);
        intent.putExtra("customQuery", mFragment.customQuery);
        Log.d("columnNames", "" + mFragment.columnNames.length);
        intent.putStringArrayListExtra("names", new ArrayList<>(Arrays.asList(mFragment.columnNames)));
        intent.putExtra("table", mFragment.table);
        intent.putExtra("path", mFragment.path);
        ArrayList<String> values = new ArrayList<String>();
        for (int i = 0; i < mFragment.columns; i++) {
            ShortenedTextView t = (ShortenedTextView) ((TableRow) v).getChildAt(i);
            String text = t.getText();
            //text=text.substring(1,tableName.length());
            values.add(text);
        }
        intent.putExtra("values", values);
        mFragment.startActivityForResult(intent, QuerySelectViewFragment.VIEW_RECORD);
    }

    @Override
    public void openCustomQuery(final EditText input) {

        Intent intent = new Intent(mFragment.getActivity(), QuerySelectViewFragment.class);
        intent.putExtra("query", input.getText().toString());
        intent.putExtra("table", "Query: " + input.getText().toString());
        intent.putExtra("customQuery", true);
        intent.putExtra("path", mFragment.path);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        mFragment.startActivity(intent);
        // query=input.getText().toString();
        //initTable();
    }

    @Override
    public void onRefreshAction() {
        mFragment.loadQuery();
    }

    @Override
    public void deleteRow(final TableRow tr, View v) {
        ArrayList<String> values = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < mFragment.columns; i++) {
            TextView t = (TextView) tr.getChildAt(i);
            String text = t.getText().toString();
            values.add(text);
        }
        for( int i=0; i < mFragment.columns; i++)
            names.add(mFragment.columnNames[i]);
        try{
            DBUtils.deleteRecord(mFragment.path, mFragment.table,names,values);
            mFragment.stk.removeView(v);
        }
        catch(Exception e){
        Toast.makeText(mFragment.getContext(), mFragment.getString(R.string.QuerySelectViewActivity_error_delete_record_message)+" "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
        mFragment.db.close();
    }
}
