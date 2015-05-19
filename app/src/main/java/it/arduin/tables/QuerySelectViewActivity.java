package it.arduin.tables;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.ObservableScrollView;

import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.database.sqlite.SQLiteDatabase.openDatabase;
import static android.graphics.Color.BLACK;


public class QuerySelectViewActivity extends BaseProjectActivity {
    public static final int NEW_RECORD = 29;
    public static final int VIEW_RECORD = 0;
    private String query;
    protected String table;
    private String title;
    protected int columns;
    private int rowCount;
    private Intent intent;
    protected String[] columnNames;
    protected SQLiteDatabase db;
    protected String path;
    protected boolean customQuery;
    private final Context c=this;
    private ProgressDialog progressPopup;
    protected QuerySelectViewPresenter mPresenter;
    @InjectView(R.id.scrollView1) ScrollView scrollview;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.fab)  com.melnykov.fab.FloatingActionButton fab;
    @InjectView(R.id.table_main) TableLayout stk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qryselect_view);
        intent=getIntent();
        table=intent.getStringExtra("table");
        path=intent.getStringExtra("path");
        customQuery=intent.getBooleanExtra("customQuery", true);
        query=intent.getStringExtra("query");
        mPresenter = new QuerySelectViewPresenter(this);
        scrollview.post(new Runnable() {
            @Override public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_UP);///setta lo scroll all'inizio
            }
        });
        //toolbar settings------------------------------------------------------

        if (toolbar != null) setSupportActionBar(toolbar);
        //toolbar.setLogo(getResources().getDrawable(R.drawable.ic_dblogo));
       try{ setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setTitle("");
        }
       catch(Exception e){
           Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
       }

        fab.attachToScrollView((ObservableScrollView) findViewById(R.id.scrollView1));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPresenter.onFabClick();
            }
        });

        loadQuery();

    }

    public Context getContext(){
        return this;
    }

    private void showDeleteRecordAlert(final View v){
        if(customQuery) return;
        final TableRow tr=(TableRow) v;
        AlertDialog.Builder alert = new AlertDialog.Builder(QuerySelectViewActivity.this);
        alert.setTitle(getString(R.string.action_confirm));
        alert.setMessage(getString(R.string.QuerySelectViewActivity_delete_message));
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mPresenter.deleteRow(tr,v);
            }

        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }


    protected void showCustomQueryAlert(){
        AlertDialog.Builder alert = new AlertDialog.Builder(QuerySelectViewActivity.this);
        alert.setTitle(getString(R.string.QuerySelectViewActivity_insert_query_message));
        // Set an EditText view to get user input
        final EditText input = new EditText(QuerySelectViewActivity.this);
        input.setText(query);
        alert.setView(input);
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mPresenter.openCustomQuery(input);
            }
        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       Toast.makeText(this, getString(R.string.QuerySelectViewActivity_refresh_old_data_message), Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qryselect_view, menu);
        if(menu != null && customQuery){
            MenuItem item_up = menu.findItem(R.id.action_newRecord);
            item_up.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_newRecord){
            mPresenter.openNewRecordActivity();
            return true;
        }
        if (id == R.id.action_newQuery) {
            mPresenter.onCustomQueryAction();
            return true;
        }
        if (id == R.id.action_refresh) {
            //new LoadQueryTask(path,query,table,customQuery,this,this).execute();
            mPresenter.onRefreshAction();
            return true;
        }
        if(id == android.R.id.home){
            NavUtils.navigateUpTo(this,intent);
            finish();
        return true;
    }


        return super.onOptionsItemSelected(item);
    }

    private ShortTextView getBaseSTV(Context context){
        ShortTextView tv0 = new ShortTextView(context);
        tv0.setTextColor(BLACK);
        tv0.setPadding(2, 2, 2, 2);
        tv0.setBackgroundResource(R.drawable.borderselector);
        tv0.setMaxLines(1);
        return tv0;
    }

    private void setTableRowListeners(TableRow tbrow0){

        tbrow0.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                mPresenter.onRowClick(v);
            }

        });

        if(!customQuery) tbrow0.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteRecordAlert(v);
                return true;

            }
        });
    }

    private Observable<TableRow> getTableObservable(){
        return Observable.create(
                new Observable.OnSubscribe<TableRow>() {
                    @Override
                    public void call(Subscriber<? super TableRow> sub) {
                        ///BACKGROUND LOADING
                        Cursor c;
                        int columnLengthLimit,headerLengthLimit;
                        Context context=getContext();
                        Log.wtf("query",query);
                        db=SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
                        c = db.rawQuery(query, null);
                        columns = c.getColumnCount();
                        columnNames = c.getColumnNames();
                        rowCount=c.getCount();
                        final int rc=rowCount;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressPopup.setMax(rc);
                            }
                        });
                        Log.d("columns",columns+"");
                        //Log.d("columnNames",columnNames.toString());
                        Log.d("rows",rowCount+"");
                        columnLengthLimit=SettingsActivity.getColumnLimit(context);
                        headerLengthLimit=SettingsActivity.getHeaderLimit(context);
                        TableRow tbrowh = new TableRow(context);
                        String s;
                        for(int i=0;i<columns;i++) {///header
                            final ShortTextView tv0 = getBaseSTV(context);
                            s=columnNames[i];
                            if(s.length()>6&&(s.substring(0,6).equals("quote("))) {
                                Log.wtf("quotename",s);
                                s=s.substring(6,s.length()-1);//shitty patch for quote(x) header
                            }
                            tv0.setShortenedText(s, headerLengthLimit);


                            tbrowh.addView(tv0);
                        }
                        sub.onNext(tbrowh);

                        if(c.moveToFirst()) {
                            do{
                                TableRow tbrow0=new TableRow(context);
                                for(int i=0;i<columns;i++) {///header
                                    ShortTextView tv0 = getBaseSTV(context);
                                    s=c.getString(i);
                                    tv0.setShortenedText(s, columnLengthLimit);
                                    tbrow0.addView(tv0);
                                }

                                setTableRowListeners(tbrow0);
                                sub.onNext(tbrow0);
                                //try{Thread.sleep(10);}catch (Exception e){sub.onError(e);}
                            }
                            while(c.moveToNext());
                            c.close();
                            db.close();
                        }
                        sub.onCompleted();
                    }
                }
        );
    }

    private Subscriber<TableRow> getTableSubscriber(){
        return new Subscriber<TableRow>() {
            int done=0;
            @Override
            public void onStart() {
                request(1);
                progressPopup=ViewUtils.getSettedCancelableProgressDialog(QuerySelectViewActivity.this,getString(R.string.QuerySelectViewActivity_loading_message));
                progressPopup.show();
            }

            @Override
            public void onNext(TableRow t) {//PROGRESS UPDATE
                try {
                    done++;
                    Log.wtf("done", "" + done);
                    progressPopup.setProgress(done);
                    //loadingPopup.setProgress(done);
                    //Log.d("progress",progressPopup.getMax()+"    "+done);
                    //TableLayout stk = (TableLayout) findViewById(R.id.table_main);
                    stk.addView(t);

                }
                catch(Exception e){
                    onError(e);
                }
                request(1);
            }

            @Override
            public void onCompleted() {
                Log.d("end", "end");
                toolbar.setTitle(rowCount + " " + getString(R.string.QuerySelectViewActivity_title_results));
                progressPopup.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                Log.d("error",e.getMessage());
            }
        };
    }

    protected void loadQuery(){
        stk.removeAllViews();
        Observable<TableRow> myObservable = getTableObservable();
        Subscriber<TableRow> mySubscriber = getTableSubscriber();
        Subscription mySubscription = myObservable.onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mySubscriber);
    }


}
