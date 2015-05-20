package it.arduin.tables.view.activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import butterknife.InjectView;
import it.arduin.tables.R;
import it.arduin.tables.presenter.RecordViewPresenter;
import it.arduin.tables.view.adapter.RecordViewListAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;


public class RecordViewActivity extends BaseProjectActivity {
    protected Context c;
    protected ArrayList<String> columnValues,columnNames;
    public int columns;
    protected String table,path;
    protected Boolean customQuery;
    public int modified;
    protected RecordViewPresenter mPresenter;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.list) RecyclerView mRecyclerView;
    @InjectView(R.id.fab)
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_view);
        c=this;
        modified=0;
        mPresenter = new RecordViewPresenter(this);
        //retrieve intent
        Intent intent=getIntent();
        columnValues=intent.getStringArrayListExtra("values");
        columnNames=intent.getStringArrayListExtra("names");
        customQuery=intent.getBooleanExtra("customQuery",true);
        if(customQuery) fab.hide();
        for(int i=0;i<columnNames.size();i++)
            Log.wtf("cnrv",columnNames.get(i));
        columns=columnValues.size();
        table=intent.getStringExtra("table");
        path=intent.getStringExtra("path");
        //ui settings
        if (toolbar != null) setSupportActionBar(toolbar);
        //toolbar.setLogo(getResources().getDrawable(R.drawable.ic_dblogo));
        try{ setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(0xFFFFFFFF);
            toolbar.setTitle(getString(R.string.record));}
        catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new LandingAnimator());
        RecordViewListAdapter myAdapter = new RecordViewListAdapter(columnNames,columnValues, this);
        mRecyclerView.setAdapter(myAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onFabClick();
            }
        });
        fab.attachToRecyclerView(mRecyclerView);
    }

    public void onUserInput(Editable s){
        //Toast.makeText(c, s.toString(), Toast.LENGTH_SHORT).show();
        modified++;
    }


    public void applyChanges(){
        String sql = "UPDATE " + table + " SET ";
        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
            LinearLayout l=(LinearLayout)mRecyclerView.getChildAt(i);
            EditText e=(EditText)l.getChildAt(1);
            TextView t=(TextView)l.getChildAt(0);
            sql = sql + t.getText() + "='" + e.getText().toString() + "', ";
        }

        if(sql.lastIndexOf(",")!=-1) sql=sql.substring(0,sql.lastIndexOf(","));

        sql=sql+" WHERE ";
        String sql2="";
        for (int i = 0; i < columns; i++) {

            sql2 = sql2 + columnNames.get(i) + "='" + columnValues.get(i) + "' AND ";
        }
        sql2=sql2.substring(0,sql2.lastIndexOf("AND"));
        sql2 = sql2.replace("=null", " is null");
        sql2 = sql2.replace("='null'"," is null");
        sql2 = sql2.replace("=''", " is null");
        sql=sql+sql2;
        //Toast.makeText(c,sql,Toast.LENGTH_LONG).show();
        ///preparata la stringa
        //Toast.makeText(c,sql,Toast.LENGTH_LONG).show();
        //Toast.makeText(c,"qry: "+sql,Toast.LENGTH_LONG).show();
        //Toast.makeText(c,"qry: "+sql,Toast.LENGTH_LONG).show();
        SQLiteDatabase db= SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        try{
            db.execSQL(sql);
        }
        catch(Exception e) {
            Toast.makeText(c,getString(R.string.RecordViewActivity_update_error_message )+ " " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        db.close();
        setResult(RESULT_OK, new Intent());
        NavUtils.navigateUpTo(this, getIntent());
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(customQuery) return true;
        getMenuInflater().inflate(R.menu.menu_record_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == android.R.id.home){
            mPresenter.onExit();
            return true;
        }
        if (id == R.id.action_recordDelete) {
            deleteThisRecord();
            return true;
        }
        /*if (id == R.id.action_recordApplychanges) {
            applyChanges();
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void deleteThisRecord() {
        String sql = "DELETE FROM " + table + " WHERE ";
        for (int i = 0; i < columns; i++)
            sql = sql + columnNames.get(i) + "='" + columnValues.get(i) + "' AND ";
        sql = sql.replace("=null", " is null");
        //sql = sql.replace("=''", " is null");
        sql=sql.substring(0,sql.lastIndexOf("AND"));
        //Toast.makeText(c,sql,Toast.LENGTH_LONG).show();
       // Toast.makeText(c,sql,Toast.LENGTH_LONG).show();
        SQLiteDatabase db= SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        try{db.execSQL(sql);}
        catch(Exception e){Toast.makeText(c,e.getMessage(),Toast.LENGTH_LONG).show();}
        db.close();
        mPresenter.onExit();
    }
}
