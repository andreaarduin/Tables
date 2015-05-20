package it.arduin.tables.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;

import butterknife.InjectView;
import it.arduin.tables.model.DatabaseHolder;
import it.arduin.tables.R;
import it.arduin.tables.utils.ViewUtils;
import it.arduin.tables.presenter.DatabaseInfoPresenter;
import it.arduin.tables.view.adapter.SimpleTextAdapter;


public class DatabaseInfoActivity extends BaseProjectActivity {
    @InjectView(R.id.list)
    RecyclerView mRecyclerView;
    private SimpleTextAdapter mAdapter;
    @InjectView(R.id.fab)
    FloatingActionButton fab;
    Toolbar toolbar;
    public DatabaseHolder dbh;
    DatabaseInfoPresenter mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_view_info);
        if(savedInstanceState==null) {
            Intent i = getIntent();
            dbh=i.getParcelableExtra("db");
        }
        toolbar = ViewUtils.getSettedToolbar(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(dbh.getName());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SimpleTextAdapter();
        mRecyclerView.setAdapter(mAdapter);
        loadInfo();
        mPresenter=new DatabaseInfoPresenter(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onFabClicked();
            }
        });
    }

    public void loadInfo(){
        mAdapter.add(getString(R.string.DatabaseViewInfoActivity_filepath), dbh.getPath());
        File f=new File(dbh.getPath());
        mAdapter.add(getString(R.string.DatabaseViewInfoActivity_filesize), f.length() + " bytes");
        mAdapter.add(getString(R.string.DatabaseViewInfoActivity_filesize), (int) (f.length() / 1024) + " KB");
        mAdapter.add(getString(R.string.DatabaseViewInfoActivity_tables), dbh.getTableNumber() + "");
        mAdapter.add(getString(R.string.DatabaseViewInfoActivity_indexes), dbh.getIndexNumber() + "");
        mAdapter.add(getString(R.string.DatabaseViewInfoActivity_hashcode), f.hashCode() + "");
        SimpleDateFormat s= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        mAdapter.add(getString(R.string.DatabaseViewInfoActivity_lastedit), s.format(f.lastModified()));
    }


    public void showDeletePopup(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.DatabaseInfoActivity_delete_title));
        alert.setMessage(getString(R.string.DatabaseInfoActivity_delete_message));
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mPresenter.deleteDatabase();
            }
        });
        alert.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alert.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_database_view_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void showError(Exception e) {
        Toast.makeText(this,"e"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
    }
}
