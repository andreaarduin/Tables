package it.arduin.tables.view.activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import it.arduin.tables.presenter.RecordAddPresenter;
import it.arduin.tables.view.adapter.RecordAddListAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;


public class RecordAddActivity extends BaseProjectActivity {
    RecordAddPresenter mPresenter;
    Context c;
    public RecyclerView mRecyclerView;
    Toolbar toolbar;
    ArrayList<String> columnValues,columnNames;
    int columns;
    public String table;
    public String path;
    public Intent intent;
    @InjectView(R.id.fab)
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_add);
        c=this;
        mPresenter = new RecordAddPresenter(this);
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onFabClick();
            }
        });
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
            mPresenter.onActionConfirmPressed();
            return true;
        }
        if(id == android.R.id.home){
            mPresenter.onBackButtonPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
