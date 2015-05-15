package it.arduin.tables;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DatabaseViewActivity extends BaseProjectActivity {
    protected static final int DATABASE_INFO = 8;
    protected static final int DATABASE_DELETED = 9;
    public Context c;
    DatabaseViewPresenter mPresenter;
    protected String path;
    protected DatabaseViewAdapter mAdapter;
    protected DatabaseHolder dbh;
    private ProgressDialog progressDialog;
    final static int ACTION_DELETE_TABLE=0;
    final static int ACTION_RENAME_TABLE=1;
    final static int ACTION_VIEW_PRAGMA_TABLE=2;
    protected Toolbar toolbar;
    @InjectView(R.id.list) android.support.v7.widget.RecyclerView mRecyclerView;
    @InjectView(R.id.fab) com.melnykov.fab.FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_databaseview);
        c=this;
        if(savedInstanceState!=null){
            dbh=savedInstanceState.getParcelable("db");
            path=dbh.getPath();
        }
        else{
            Intent intent=getIntent();
            dbh=intent.getParcelableExtra("db");
            path=dbh.getPath();
        }
        mPresenter=new DatabaseViewPresenter(this);
        //dichiarazioni

//toolbar settings------------------------------------------------------
        toolbar = ViewUtils.getSettedToolbar(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try{
            int punto=path.lastIndexOf('.');
            int barra=path.lastIndexOf('/');
            if(punto==-1) punto=path.length();
            if(barra==-1) barra=0;
            toolbar.setTitle(path.substring(1 + barra, punto));
        }
        catch(Exception e){Toast.makeText(c,e.getMessage(),Toast.LENGTH_LONG).show();}

        mAdapter = new DatabaseViewAdapter(new ArrayList<TableHolder>(),this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //new LoadTablesList(c).execute();
        loadTables();
        setMRecyclerView();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onFabClick();
            }
        });

    }

    private void setMRecyclerView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(c, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mPresenter.onRecyclerItemPressed(view,position);
                    }
                })
        );
        ViewUtils.setRecyclerViewAnimator(mRecyclerView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_database_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            mPresenter.onActionInfoPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("path", path);
        bundle.putParcelable("db", dbh);
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == DATABASE_INFO && resultCode == DATABASE_DELETED){///
            finish();
        }
        mPresenter.onActivityResult();
    }//onActivityResult

    public void showReloadAlert(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( c);
        // set title
        alertDialogBuilder.setTitle(getString(R.string.action_confirm));
        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.DatabaseViewActivity_refresh_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        mPresenter.reloadTables();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void showOptionsMenu(final String name, final int position){
        AlertDialog optionsDialog;
        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case ACTION_DELETE_TABLE:
                            mPresenter.onDeleteOptionPressed(name, position);
                            break;
                        case ACTION_RENAME_TABLE:
                            mPresenter.onRenameOptionPressed(name, position);
                            break;
                        case ACTION_VIEW_PRAGMA_TABLE:
                            mPresenter.viewTableInfo(name, position,c);
                            break;

                }
            }
        };
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setTitle(name);
        CharSequence[] c=new CharSequence[3];
        c[ACTION_DELETE_TABLE]=this.getString(R.string.DatabaseViewActivity_delete_table);
        c[ACTION_RENAME_TABLE]=this.getString(R.string.DatabaseViewActivity_rename_table);
        c[ACTION_VIEW_PRAGMA_TABLE]=this.getString(R.string.DatabaseViewActivity_view_pragma);
        alertDialogBuilder.setItems(c, dialogListener);
        optionsDialog=alertDialogBuilder.create();
        optionsDialog.show();
    }



    private Observable<TableHolder> getTableObservable(){
        return Observable.create(new Observable.OnSubscribe<TableHolder>() {
            @Override
            public void call(Subscriber<? super TableHolder> sub) {
                if(!dbh.isAccessible()) sub.onError(new Exception(getString(R.string.DatabaseViewActivity_error_opening_db)));
                final ArrayList<String> tables = dbh.getTables();
                //tableNumber = tables.size();
                if(tables.size()==0) sub.onCompleted();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setMax(tables.size());
                    }
                });
                for (int i = 0; i < tables.size(); i++) {
                    sub.onNext(new TableHolder(tables.get(i), ColumnPair.getStringDefinitionArray(DBUtils.getColumns(path, tables.get(i))),dbh));
                }
                sub.onCompleted();
            }
        });
    }

    private Subscriber<TableHolder> getTableSubscriber(){
        return new Subscriber<TableHolder>() {
            int done=0;
            @Override
            public void onStart(){
                request(1);
                progressDialog = ViewUtils.getSettedCancelableProgressDialog(DatabaseViewActivity.this,getString(R.string.DatabaseViewActivity_loading_message));
                progressDialog.show();
            }
            @Override
            public void onCompleted() {
                progressDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                progressDialog.cancel();
                Log.wtf("error",e.getLocalizedMessage());
            }

            @Override
            public void onNext(TableHolder tableHolder) {

                done++;
                //progressPopup.setProgress(done);
                try{
                    mAdapter.add(tableHolder);
                }
                catch(Exception e) {
                    //Log.d("errore" ,param[0].toString());
                }
                request(1);
            }
        };
    }

    protected void loadTables(){
        Observable<TableHolder> myObservable=getTableObservable();
        Subscriber<TableHolder> mySubscriber=getTableSubscriber();
        Subscription mySubscription = myObservable.onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mySubscriber);
    }

    public void showDeleteTableAlert(final String name,final int position) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( c);
        // set title
        alertDialogBuilder.setTitle(getString(R.string.action_confirm));
        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.DatabaseViewActivity_delete_table_message)+name+" ?")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        mPresenter.deleteTable(name,position);
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void showRenameAlert(final String name,final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(c);
        alert.setTitle(getString(R.string.DatabaseViewActivity_rename_table_message));
        // Set an EditText view to get user input
        final EditText nameBox = new EditText(c);
        nameBox.setText(name);
        alert.setView(nameBox);
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String input = nameBox.getText().toString();
                mPresenter.renameTable(name,position,input);

            }
        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }
}
