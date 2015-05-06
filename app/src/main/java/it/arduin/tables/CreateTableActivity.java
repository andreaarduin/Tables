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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;


public class CreateTableActivity extends ActionBarActivity {
    static String databaseName,path,tablename;
    static Context c;
    static EditText t;
    ArrayList<ColumnSettingsHolder> list;
    Intent intent;
    static SQLiteDatabase db;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.list) RecyclerView mRecyclerView;
    @InjectView(R.id.fab) com.melnykov.fab.FloatingActionButton fab;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_table);
        c=this;
        ButterKnife.inject(this);
        list=new ArrayList<>();
        //get  content from intent
        intent=getIntent();
        databaseName=intent.getStringExtra("name");
        path=intent.getStringExtra("path");
        ///
        if (toolbar != null) setSupportActionBar(toolbar);
        //toolbar.setLogo(getResources().getDrawable(R.drawable.ic_dblogo));
        try{ setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(0xFFFFFFFF);
            toolbar.setTitle(databaseName);}
        catch(Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        //initlist
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new FadeInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(600);
        mRecyclerView.getItemAnimator().setRemoveDuration(600);
        ColumnSettingsAdapter myAdapter = new ColumnSettingsAdapter(list, this);
        mRecyclerView.setAdapter(myAdapter);
        //fab_add.attachToRecyclerView(mRecyclerView);
        list.add(new ColumnSettingsHolder("n1"));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColumnSettingsAdapter adapter=(ColumnSettingsAdapter)mRecyclerView.getAdapter();
                adapter.add(new ColumnSettingsHolder("n"+(list.size()+1)));
            }
        });
        t=(EditText)(findViewById(R.id.tableName));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_table, menu);
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
            createTable();
            NavUtils.navigateUpTo(this, intent);
            finish();
            return true;
        }
        if(id == android.R.id.home){
            NavUtils.navigateUpTo(this, intent);
            finish();
            return true;}
        return super.onOptionsItemSelected(item);
    }

    public void createTable(){
        try{db=SQLiteDatabase.openOrCreateDatabase(path,null);}
        catch(Exception e){Toast.makeText(c,e.getMessage(),Toast.LENGTH_LONG).show();}
        tablename =t.getText().toString();
        String sql="CREATE TABLE IF NOT EXISTS `"+tablename+"` (";
        String primaryKeys="";
        for(int i=0;i<mRecyclerView.getAdapter().getItemCount();i++){
            View v=mRecyclerView.getChildAt(i);
            ColumnSettingsAdapter.ViewHolder view=new ColumnSettingsAdapter.ViewHolder(v);
            ColumnSettingsHolder data=new ColumnSettingsHolder(view);
            sql=sql+data.getColumnDefinition();
            if(i!=mRecyclerView.getAdapter().getItemCount()-1) {
                sql += ",";

                // Toast.makeText(c,primaryKeys+"\n"+sql,Toast.LENGTH_SHORT).show();
            }
            if(data.primaryKey){
                primaryKeys+=data.name+",";
            }

        }
        if(primaryKeys.length()!=0){
            primaryKeys=primaryKeys.substring(0,primaryKeys.length()-1);
            sql=sql+", PRIMARY KEY("+primaryKeys+")";
        }
        sql+=")";
        Toast.makeText(c,sql,Toast.LENGTH_LONG).show();
        try{
            db.execSQL(sql);
        }
        catch(Exception e){
            Toast.makeText(c,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
