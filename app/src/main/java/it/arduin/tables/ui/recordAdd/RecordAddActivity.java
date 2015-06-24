package it.arduin.tables.ui.recordAdd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import butterknife.InjectView;
import it.arduin.tables.R;
import it.arduin.tables.ui.BaseProjectActivity;
import jp.wasabeef.recyclerview.animators.LandingAnimator;


public class RecordAddActivity extends BaseProjectActivity {
    RecordAddPresenter mPresenter;
    Toolbar toolbar;
    public ArrayList<String> columnNames;
    int columns;
    public String table;
    public String path;
    public Intent intent;
    @InjectView(R.id.fab) FloatingActionButton fab;
    @InjectView(R.id.list) public RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_add);
        mPresenter = new RecordAddPresenterImpl(this);
        //retrieve intent
        intent=getIntent();

        columnNames=intent.getStringArrayListExtra("names");
        columns=columnNames.size();
        for(int i=0;i<columns;i++) Log.wtf("Colonna",i+"  "+columnNames.get(i));
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

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new LandingAnimator());
        RecordAddListAdapter myAdapter = new RecordAddListAdapter(columnNames);
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
