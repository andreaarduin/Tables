package it.arduin.tables.ui.createTable;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;
import it.arduin.tables.R;
import it.arduin.tables.model.ColumnSettingsHolder;
import it.arduin.tables.model.DatabaseHolder;
import it.arduin.tables.model.TableStructure;
import it.arduin.tables.ui.BaseProjectActivity;
import it.arduin.tables.utils.DBUtils;

public class TableCreateActivity extends BaseProjectActivity {
    private DatabaseHolder dbh;
    @InjectView(R.id.container) ViewPager mViewPager;
    private CreateTableViewPagerAdapter mViewPagerAdapter;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.buttonNext) Button btnNext;
    @InjectView(R.id.buttonPrev) Button btnPrev;
    @InjectView(R.id.fab)  FloatingActionButton fab;
    //@InjectView(R.id.bottombar_container) FrameLayout bottombar_container;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_create);
        dbh = getIntent().getParcelableExtra("db");
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(0xFFFFFFFF);
            toolbar.setTitle(dbh.getName());
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
        onPageChangeListener = new PageChangeListener();
        mViewPager.addOnPageChangeListener(onPageChangeListener);
        mViewPager.setCurrentItem(0);
    }

    @OnClick(R.id.buttonPrev)
    public void onButtonPreviousPressed(){
        if(0==mViewPager.getCurrentItem()) Toast.makeText(this,getString(R.string.CreateTableActivity_error_first_page),Toast.LENGTH_LONG).show();
        else mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
    }
    @OnClick(R.id.buttonNext)
    public void onButtonNextPressed(){
        if((mViewPager.getCurrentItem()) == mViewPagerAdapter.getCount()-1){
            onConfirm();
        }
        if(mViewPagerAdapter.getCount()==mViewPager.getCurrentItem()) return;
        else mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
    }

    @OnClick(R.id.fab)
    public void addAPage(){
        mViewPagerAdapter.addColumnPage();
        setNextButton();
    }

    @Override
    public void onResume(){
        super.onResume();
        toolbar.setTitle(dbh.getName());//for some reason settitle didn't work in oncreate but it does here lol
    }

    private void onConfirm() {
        if(mViewPagerAdapter.getCount() == 1){
            Toast.makeText(this,getString(R.string.CreateTableActivity_error_no_columns),Toast.LENGTH_LONG).show();
            return;
        }
        //TODO show a dialog and ask the user to confirm the creation or get back and make changes
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.TableCreateActivity_confirm_message));
        alertDialogBuilder.setTitle(getString(R.string.action_confirm));
        alertDialogBuilder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        confirm();
                    }
                });
        alertDialogBuilder.setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void confirm(){
        CreateTableSettingsFragment firstScreen = (CreateTableSettingsFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + 0);
        String tblName = firstScreen.getTableName();//TODO get full string intstead of the name
        ArrayList<ColumnSettingsHolder> columns = new ArrayList<>();
        ArrayList<ColumnSettingsHolder> primaryKeys = new ArrayList<>();
        ArrayList<ColumnSettingsHolder> uniques = new ArrayList<>();
        ArrayList<ColumnSettingsHolder> list = new ArrayList<>();
        for(int i = 1 ; i < mViewPagerAdapter.getCount();i++){
            CreateTableColumnFragment page  = (CreateTableColumnFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + i);
            list.add(page.getColumn());
        }
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
        else if( id == R.id.action_delete){
            deleteCurrentPage();
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteCurrentPage() {
        if(mViewPager.getCurrentItem() == 0) {
            Toast.makeText(this, getString(R.string.CreateTableActivity_error_first_page_delete), Toast.LENGTH_LONG).show();
            return;
        }
        else{
            mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
            mViewPagerAdapter.deletePage(mViewPager.getCurrentItem());
            onPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
        }
    }

    private class PageChangeListener extends ViewPager.SimpleOnPageChangeListener{
        private static final int FIRST_PAGE=0;
        public void onPageSelected(int position) {
            if(position == mViewPagerAdapter.getCount()-1) setConfirmButton();
            else setNextButton();
        }
    }

    private void setNextButton() {
        btnNext.setText(getString(R.string.CreateTableActivity_next));
    }

    private void setConfirmButton() {
        btnNext.setText(getString(R.string.CreateTableActivity_confirm));
    }

}
