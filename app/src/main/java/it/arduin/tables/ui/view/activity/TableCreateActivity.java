package it.arduin.tables.ui.view.activity;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;
import it.arduin.tables.R;
import it.arduin.tables.model.ColumnSettingsHolder;
import it.arduin.tables.model.DatabaseHolder;
import it.arduin.tables.model.TableStructure;
import it.arduin.tables.ui.view.activity.BaseProjectActivity;
import it.arduin.tables.ui.view.adapter.ColumnSettingsAdapter;
import it.arduin.tables.ui.view.adapter.CreateTableViewPagerAdapter;
import it.arduin.tables.ui.view.fragment.CreateTableColumnsFragment;
import it.arduin.tables.ui.view.fragment.CreateTableSettingsFragment;
import it.arduin.tables.utils.DBUtils;

public class TableCreateActivity extends BaseProjectActivity {
    DatabaseHolder dbh;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.container) ViewPager mViewPager;
    CreateTableViewPagerAdapter mViewPagerAdapter;
    @InjectView(R.id.toolbarContainer) LinearLayout toolbarContainer;
    @InjectView(R.id.buttonNext) Button btnNext;
    @InjectView(R.id.buttonPrev) Button btnPrev;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_create);
        dbh = getIntent().getParcelableExtra("db");
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(0xFFFFFFFF);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(R.id.rootView),"Fiko",Snackbar.LENGTH_LONG)
                        .show();
            }
        });*/
        mViewPagerAdapter = new CreateTableViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(0);
    }

    @OnClick(R.id.buttonPrev)
    public void goBack(){
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        if((mViewPager.getCurrentItem()-1) == 0){
            btnPrev.setClickable(false);
            btnPrev.setText("");
        }
        else{
            btnPrev.setClickable(true);
            btnPrev.setText(getString(R.string.CreateTableActivity_previous));
        }
    }
    @OnClick(R.id.buttonNext)
    public void goFwd(){
        Log.wtf("current", "" + mViewPager.getCurrentItem());
        if((mViewPager.getCurrentItem()) == mViewPagerAdapter.getCount()-1){
            onConfirm();
        }
        else if((mViewPager.getCurrentItem()) == mViewPagerAdapter.getCount()-2){
            btnNext.setText(getString(R.string.CreateTableActivity_confirm));
            mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
        }
        else{
            mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
            btnNext.setClickable(true);
            btnNext.setText(getString(R.string.CreateTableActivity_next));
        }
    }

    private void onConfirm() {
        //TODO show a dialog and ask the user to confirm the creation or get back and make changes

        CreateTableSettingsFragment firstScreen = (CreateTableSettingsFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + 0);
        String tblName = firstScreen.getTableName();//TODO get full string intstead of the name
        ArrayList<ColumnSettingsHolder> columns = new ArrayList<>();
        ArrayList<ColumnSettingsHolder> primaryKeys = new ArrayList<>();
        ArrayList<ColumnSettingsHolder> uniques = new ArrayList<>();
        CreateTableColumnsFragment secondScreen = (CreateTableColumnsFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + 1);
        ArrayList<ColumnSettingsHolder> list = secondScreen.getColumns();
        for (int i = 0; i <  list.size(); i++) {
            ColumnSettingsHolder data = list.get(i);
            if(data.primaryKey) primaryKeys.add(data);
            if(data.getUnique()) uniques.add(data);
            columns.add(data);
        }
        TableStructure t = new TableStructure(tblName,columns,primaryKeys,uniques);
        try{
            DBUtils.createTable(dbh.getPath(), t);
            finish();
        }
        catch (Exception e){
            Toast.makeText(this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_table_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public LinearLayout getToolbarContainer(){
        return toolbarContainer;
    }
}
