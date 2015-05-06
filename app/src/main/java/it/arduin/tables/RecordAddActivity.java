package it.arduin.tables;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.LandingAnimator;


public class RecordAddActivity extends ActionBarActivity {

    Context c;
    RecyclerView mRecyclerView;
    Toolbar toolbar;
    ArrayList<String> columnValues,columnNames;
    int columns;
    String table,path;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_add);
        c=this;
        //retrieve intent
        intent=getIntent();
        columnValues=intent.getStringArrayListExtra("values");
        columnNames=intent.getStringArrayListExtra("names");
        columns=columnValues.size();
        table=intent.getStringExtra("table");
        path=intent.getStringExtra("path");

        //ui settings

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);
        //toolbar.setLogo(getResources().getDrawable(R.drawable.ic_dblogo));
        try{ setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(0xFFFFFFFF);
            toolbar.setTitle(getString(R.string.RecordAddActivity_title));}
        catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        mRecyclerView = (RecyclerView)findViewById(R.id.recordAddList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new LandingAnimator());
        RecordAddListAdapter myAdapter = new RecordAddListAdapter(columnNames,columnValues, this);
        mRecyclerView.setAdapter(myAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_confirm) {
            insertNewRecord();
            NavUtils.navigateUpTo(this,intent);
            finish();
            return true;
        }
        if(id == android.R.id.home){
            //if(modified) //alert vuoi inserire il record?
            //setResult(Activity.RESULT_OK, intent);
            NavUtils.navigateUpTo(this,intent);
            finish();   return true;}

        return super.onOptionsItemSelected(item);
    }

    public void insertNewRecord(){
        String sql = "INSERT INTO " + table + "(",sql2=" VALUES(";
        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
            LinearLayout l=(LinearLayout)mRecyclerView.getChildAt(i);
            EditText e=(EditText)l.getChildAt(1);
            TextView t=(TextView)l.getChildAt(0);
            sql = sql + t.getText()+ ", ";
            sql2=sql2+"'"+e.getText().toString()+"',";
        }

        if(sql.lastIndexOf(",")!=-1) sql=sql.substring(0,sql.lastIndexOf(","));
        if(sql2.lastIndexOf(",")!=-1) sql2=sql2.substring(0,sql2.lastIndexOf(","));

        sql=sql+")"+sql2+")";

        //Toast.makeText(c,sql,Toast.LENGTH_LONG).show();
        SQLiteDatabase db= SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        try{
            db.execSQL(sql);
        }
        catch(Exception e) {
            Toast.makeText(c,getString(R.string.RecordAddActivity_error_message)+" "+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
        db.close();
    }
}
