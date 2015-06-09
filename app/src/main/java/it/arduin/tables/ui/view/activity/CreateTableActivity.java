package it.arduin.tables.ui.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.InjectView;
import it.arduin.tables.model.ColumnSettingsHolder;
import it.arduin.tables.R;
import it.arduin.tables.model.DatabaseHolder;
import it.arduin.tables.ui.presenter.CreateTablePresenter;
import it.arduin.tables.ui.presenter.CreateTablePresenterImpl;
import it.arduin.tables.ui.view.adapter.ColumnSettingsAdapter;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;


public class CreateTableActivity extends BaseProjectActivity {
    public String databaseName,tablename;
    public ArrayList<ColumnSettingsHolder> list;
    public Intent intent;
    public CreateTablePresenter mPresenter;
    public DatabaseHolder db;
    @InjectView(R.id.toolbar) public Toolbar toolbar;
    @InjectView(R.id.list) public RecyclerView mRecyclerView;
    @InjectView(R.id.fab) public com.melnykov.fab.FloatingActionButton fab;
    @InjectView(R.id.tableName) public EditText tableName;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_table);
        list=new ArrayList<>();
        //get  content from intent
        intent=getIntent();
        mPresenter=new CreateTablePresenterImpl(this);
        databaseName=intent.getStringExtra("name");
        db= intent.getParcelableExtra("db");
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
        ColumnSettingsAdapter mAdapter = new ColumnSettingsAdapter(list, this);
        mRecyclerView.setAdapter(mAdapter);
        //fab_add.attachToRecyclerView(mRecyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onFabPressed();
            }
        });
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
