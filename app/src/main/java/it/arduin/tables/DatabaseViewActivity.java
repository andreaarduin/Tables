package it.arduin.tables;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.melnykov.fab.FloatingActionButton;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DatabaseViewActivity extends BaseProjectActivity {
    public Context c;
    private ArrayList<TableHolder> list;
    private String path;
    private DatabaseViewAdapter mAdapter;
    private DatabaseHolder dbh;
    private ProgressDialog progressDialog;
    final static int ACTION_DELETE_TABLE=0;
    final static int ACTION_RENAME_TABLE=1;
    final static int ACTION_VIEW_PRAGMA_TABLE=2;
    private Toolbar toolbar;
    @InjectView(R.id.list) android.support.v7.widget.RecyclerView mRecyclerView;
    @InjectView(R.id.fab) com.melnykov.fab.FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_databaseview);
        c=this;
        if(mRecyclerView==null) Log.wtf("w","Tf");
        if(savedInstanceState!=null){
            dbh=savedInstanceState.getParcelable("db");
            path=dbh.getPath();
        }
        else{
            Intent intent=getIntent();
            dbh=intent.getParcelableExtra("db");
            path=dbh.getPath();
        }
        //dichiarazioni

//toolbar settings------------------------------------------------------
        toolbar = ToolbarUtils.getSettedToolbar(this,R.id.toolbar);
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

        list= new ArrayList<>();
        mAdapter = new DatabaseViewAdapter(list,this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //new LoadTablesList(c).execute();
        loadTables();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(c,CreateTableActivity.class);
                intent.putExtra("path",path);
                intent.putExtra("name",toolbar.getTitle()+",new table");
                startActivityForResult(intent, 1);
            }
        });

    }

    private void setMRecyclerView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(c, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent=new Intent(c,QuerySelectViewActivity.class);
                        String query="SELECT ";
                        ArrayList<ColumnPair> definitions=DBUtils.getColumns(path,list.get(position).getName());
                        String[] columnTypes= ColumnPair.getTypeArray(definitions);
                        String []columnNames= ColumnPair.getNameArray(definitions);
                        int columns=columnNames.length;
                        for(int i=0;i<columns;i++){
                            if(columnTypes[i].toLowerCase().trim().equals("blob")) query+="quote("+columnNames[i]+")";
                            else query+=columnNames[i];
                            if(i!=columns-1) query+=",";
                        }
                        query+=" FROM "+ list.get(position).getName();
                        query+=" LIMIT "+ SettingsActivity.getQueryLimit(getContext());
                        Log.d("qry",query);
                        intent.putExtra("query",query);
                        intent.putExtra("table",list.get(position).getName());
                        intent.putExtra("customQuery",false);
                        intent.putExtra("path",path);
                        // ((dataContainer)getApplication()).db=db;
                        startActivity(intent);                    }
                })
        );
        mRecyclerView.setItemAnimator(new FadeInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(600);
        mRecyclerView.getItemAnimator().setRemoveDuration(600);
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
        if (id == R.id.action_sqlite_master) {
            Intent intent=new Intent(c,QuerySelectViewActivity.class);
            intent.putExtra("query","select * from sqlite_master");
            intent.putExtra("table","sqlite_master");
            intent.putExtra("customQuery",true);
            intent.putExtra("path",path);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("path", path);
        bundle.putParcelable("db", dbh);
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( c);
        // set title
        alertDialogBuilder.setTitle(getString(R.string.action_confirm));
        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.DatabaseViewActivity_refresh_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        mAdapter.list=new ArrayList<TableHolder>();
                        new LoadTablesList(c).execute();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }//onActivityResult


    public void showOptionsMenu(final String name, final int position){
        AlertDialog optionsDialog;
        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case ACTION_DELETE_TABLE:
                            deleteTableFromDB(name,position);
                            break;
                        case ACTION_RENAME_TABLE:
                            renameTable(name,position);
                            break;
                        case ACTION_VIEW_PRAGMA_TABLE:
                            viewTableInfo(name, position,c);
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



    public void deleteTableFromDB(final String name,final int position){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( c);
        // set title
        alertDialogBuilder.setTitle(getString(R.string.action_confirm));
        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.DatabaseViewActivity_delete_table_message)+name+" ?")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                        //String path=DatabaseView.path;
                        try{
                            String sql="DROP TABLE IF EXISTS '"+name+"'";
                            //Toast.makeText(DatabaseView.c,sql,Toast.LENGTH_LONG).show();
                            SQLiteDatabase db=SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READWRITE);
                            db.execSQL(sql);
                            db.close();
                            DatabaseViewAdapter ad=(DatabaseViewAdapter)mRecyclerView.getAdapter();
                            ad.remove(position);
                        }
                        catch (Exception e){
                            Toast.makeText(c,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
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
    public void renameTable(final String name,final int position){
        AlertDialog.Builder alert = new AlertDialog.Builder(c);
        alert.setTitle(getString(R.string.DatabaseViewActivity_rename_table_message));
        // Set an EditText view to get user input
        final EditText nameBox = new EditText(c);
        nameBox.setText(name);
        alert.setView(nameBox);
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String input = nameBox.getText().toString();
                try {
                    SQLiteDatabase db;
                    db=SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
                    db.execSQL("ALTER TABLE '"+name+"' RENAME TO '"+input+"'");
                    TableHolder t=mAdapter.list.get(position);
                    t=new TableHolder(input,t.getFields());
                    mAdapter.list.set(position,t);
                    mAdapter.notifyDataSetChanged();

                    //Toast.makeText(c,input,Toast.LENGTH_LONG).show();
                }
                catch(Exception e){Toast.makeText(c,e.getMessage(),Toast.LENGTH_LONG).show();}

            }
        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }
    public void viewTableInfo(final String name, final int position,Context context){
        Intent intent=new Intent(c,QuerySelectViewActivity.class);
        String table=name;
        intent.putExtra("query","PRAGMA table_info('"+table+"')");
        intent.putExtra("table",table);
        intent.putExtra("customQuery",true);
        intent.putExtra("path",path);
        context.startActivity(intent);
    }

    private class LoadTablesList extends AsyncTask<Object,TableHolder,Object> {
        int tableNumber, done;
        ProgressDialog progressPopup;
        Context context;
        SQLiteDatabase db;
        //ArrayList<TableHolder> list;
        private LoadTablesList(Context context) {
            this.context = context;
        }



        @Override
        protected void onPreExecute() {
            lockScreenOrientation();
            db = null;
            try {
                db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            } catch (Exception e) {
                Toast.makeText(c, e.getMessage(), Toast.LENGTH_LONG).show();
                this.cancel(true);
            }
            if (db == null) Toast.makeText(c, getString(R.string.error), Toast.LENGTH_LONG).show();
            else db.close();
            progressPopup=new ProgressDialog(context);
            progressPopup.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressPopup.setIndeterminate(false);
            progressPopup.setCancelable(false);
            progressPopup.show();
        }

        @Override
        protected Object doInBackground(Object... params) {
            done = 0;
            ArrayList<String> tables;
            tables = dbh.getTables(context);
            //Log.d("dim",""+tables.size());
            tableNumber = tables.size();
            progressPopup.setMax(tableNumber);
            for (int i = 0; i < tables.size(); i++) {
                publishProgress(new TableHolder(tables.get(i), ColumnPair.getStringDefinitionArray(DBUtils.getColumns(path, tables.get(i)))));

                /*try {
                    Thread.sleep(300);
                } catch (Exception e) {
                }*/
            }
            return null;
        }


        @Override
        protected void onPostExecute(Object param) {
            progressPopup.dismiss();
            setMRecyclerView();
            unlockScreenOrientation();
        }





        @Override
        protected void onProgressUpdate(TableHolder... param){
            done++;
            progressPopup.setProgress(done);
            try{mAdapter.list.add(param[0]);}
            catch(Exception e){
                //Log.d("errore" ,param[0].toString());
                }
            mAdapter.notifyDataSetChanged();
        }

        private void lockScreenOrientation() {
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        private void unlockScreenOrientation() {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    private Observable<TableHolder> getTableObservable(){
        return Observable.create(new Observable.OnSubscribe<TableHolder>() {
            @Override
            public void call(Subscriber<? super TableHolder> sub) {
                if(!dbh.isAccessible()) sub.onError(new Exception("db non apribile"));
                final ArrayList<String> tables = dbh.getTables(getContext());
                //tableNumber = tables.size();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setMax(tables.size());
                    }
                });
                for (int i = 0; i < tables.size(); i++) {
                    sub.onNext(new TableHolder(tables.get(i), ColumnPair.getStringDefinitionArray(DBUtils.getColumns(path, tables.get(i)))));
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
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.QuerySelectViewActivity_loading_message));//cambia stringa mouna
                progressDialog.show();
            }
            @Override
            public void onCompleted() {
                setMRecyclerView();
                progressDialog.cancel();
            }

            @Override
            public void onError(Throwable e) {
                Log.wtf("error",e.getLocalizedMessage());
            }

            @Override
            public void onNext(TableHolder tableHolder) {
                done++;
                //progressPopup.setProgress(done);
                try{
                    mAdapter.list.add(tableHolder);
                }
                catch(Exception e){
                    //Log.d("errore" ,param[0].toString());
                }
                mAdapter.notifyDataSetChanged();
                request(1);
            }
        };
    }

    private void loadTables(){
        Observable<TableHolder> myObservable=getTableObservable();
        Subscriber<TableHolder> mySubscriber=getTableSubscriber();
        Subscription mySubscription = myObservable.onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mySubscriber);
    }
}
