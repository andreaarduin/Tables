package it.arduin.tables;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.InjectView;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends BaseProjectActivity {
    private ArrayList<DatabaseHolder> list;
    private SharedPreferencesOperations prefs;
    private MainActivityAdapter myAdapter;
    private ProgressDialog progressDialog;
    static final int ACTIVITY_CHOOSE_FILE = 3;
    static final int NEW_DB_PATH=0;
    static final int NEW_DB_CREATE=1;
    static final int NEW_DB_CHOOSE=2;
    Context c;
    Toolbar toolbar;
    @InjectView(R.id.list)
    android.support.v7.widget.RecyclerView mRecyclerView;
    @InjectView(R.id.fab)
    com.melnykov.fab.FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        c=this;
        prefs = new SharedPreferencesOperations(c);
        //---------------toolbar
        toolbar = ToolbarUtils.getSettedToolbar(this,R.id.toolbar);
        list= new ArrayList<>();
        initList();
        //new loadDBSTask(this).execute();
        loadDBs();
    }

    private class loadDBSTask extends AsyncTask<Object,Object,Object>
    {
        int done;
        ProgressDialog progressPopup;
        Context context;

        private loadDBSTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            lockScreenOrientation();
            done=0;
            progressPopup=new ProgressDialog(context);
            progressPopup.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressPopup.setIndeterminate(false);
            progressPopup.setCancelable(false);
            progressPopup.show();
        }
        @Override
        protected Object doInBackground(Object... params) {
            try{
                list = prefs.loadList();
                progressPopup.setMax(list.size());
                publishProgress();

            }
            catch(Exception e){
             //Toast.makeText(c,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Object param) {
            progressPopup.dismiss();
            unlockScreenOrientation();
        }
        @Override
        protected void onProgressUpdate(Object... param){
            done++;
            progressPopup.setProgress(done);
            myAdapter.notifyDataSetChanged();
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
    private void initList(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new FadeInLeftAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(600);
        mRecyclerView.getItemAnimator().setRemoveDuration(600);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(c, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(c, DatabaseViewActivity.class);
                        intent.putExtra("db", myAdapter.getItemAt(position));
                        startActivity(intent);
                    }
                })
        );


        //for(int i=0;i<12;i++) list.insert(new DatabaseHolder("DB "+i,null,"percorso:/."));
        //list.insert(0,new DatabaseHolder("test",null,"/storage/emulated/0/dbTest.sqlite"));
        myAdapter = new MainActivityAdapter(list, R.layout.row_mainactivity, this);
        mRecyclerView.setAdapter(myAdapter);
        //fab.attachToRecyclerView(mRecyclerView);
        c=this;
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showNewDatabasePopup();
            }
        });

    }
    public void showNewDatabasePopup(){
        AlertDialog optionsDialog;
        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case NEW_DB_PATH:
                        addDatabaseFromPath();
                        break;
                    case NEW_DB_CHOOSE:
                        showFileChooser();
                        break;
                    case NEW_DB_CREATE:
                        createNewDatabase();
                        break;

                }
            }
        };
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setTitle(this.getString(R.string.mainActivity_add_db_title));
        CharSequence[] c=new CharSequence[3];
        c[NEW_DB_CHOOSE]=this.getString(R.string.mainActivity_add_db_chooser);
        c[NEW_DB_PATH]=this.getString(R.string.mainActivity_add_db_path);
        c[NEW_DB_CREATE]=this.getString(R.string.mainActivity_add_db_create);
        alertDialogBuilder.setItems(c, dialogListener);
        optionsDialog=alertDialogBuilder.create();
        optionsDialog.show();
    }
    public void saveDatabase(String name,String fileName){
        try{
            SQLiteDatabase.openDatabase(fileName,null, SQLiteDatabase.OPEN_READWRITE);
            addAndSaveDatabase(name, fileName);
        }
        catch(Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public void addDatabaseFromPath(){
        AlertDialog.Builder alert = new AlertDialog.Builder(c);
        alert.setTitle(c.getString(R.string.mainActivity_add_db_insert_path));
        // Set an EditText view to get user input
        final EditText input = new EditText(c);
        input.setText("/storage/emulated/0/");
        alert.setView(input);
        alert.setPositiveButton(c.getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                int barra = value.lastIndexOf("/");
                int punto = value.lastIndexOf(".");
                if (punto == -1) punto = value.length();
                String fileName = value.substring(1 + barra, punto);
                saveDatabase(value, fileName);
            }
        });
        alert.setNegativeButton(c.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }


    public void createNewDatabase(){
        AlertDialog.Builder alert = new AlertDialog.Builder(c);
        alert.setTitle(c.getString(R.string.mainActivity_add_db_insert_path));
        // Set an EditText view to get user input
        LinearLayout layout = new LinearLayout(c);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText pathBox = new EditText(c);
        pathBox.setText("/storage/emulated/0");
        layout.addView(pathBox);
        final EditText nameBox = new EditText(c);
        nameBox.setText("x.sqlite");
        layout.addView(nameBox);
        alert.setView(layout);
        alert.setPositiveButton(c.getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String path = pathBox.getText().toString();
                String name = nameBox.getText().toString();
                try {
                    //    SQLiteOpenHelper = new SQLiteOpenHelper(c,path+name,null,1);
                    //SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(path + name, null);
                    addAndSaveDatabase(path + "/" + name, name);
                } catch (Exception e) {
                    Toast.makeText(c, e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
        alert.setNegativeButton(c.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }




    private void showFileChooser() {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("application/octet-stream");
        intent = Intent.createChooser(chooseFile, c.getString(R.string.mainActivity_file_chooser_title));
        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String filePath;
        if (resultCode != RESULT_OK) return;
        if(requestCode == ACTIVITY_CHOOSE_FILE) {///If the request was the file chooser, we check the path for a valid db and save it
            Uri uri = data.getData();
            filePath = DBUtils.getPath(c, uri);
            if (filePath == null) filePath = uri.getPath();
            try{
                openOrCreateDatabase(filePath,SQLiteDatabase.OPEN_READWRITE,null);
            }
            catch(Exception e){
                Toast.makeText(c,c.getString(R.string.mainActivity_wrong_file),Toast.LENGTH_LONG).show();
            }
            String fileName = filePath.substring(1 + filePath.lastIndexOf("/"), filePath.lastIndexOf('.'));
            if (filePath.equals(""))
                Toast.makeText(this, "errore, path nullo", Toast.LENGTH_LONG).show();
            else {
                addAndSaveDatabase(filePath, fileName);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void forgetAndSaveDatabase(final int position){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setMessage(c.getString(R.string.mainActivity_forget_database) + myAdapter.getItemAt(position).getName() + " ?");
        alertDialogBuilder.setTitle(c.getString(R.string.action_confirm));
        alertDialogBuilder.setPositiveButton(c.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        myAdapter.remove(position);
                    }
                });
        alertDialogBuilder.setNegativeButton(c.getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
    public void addAndSaveDatabase(String filePath,String fileName){

        myAdapter.insert(new DatabaseHolder(fileName, filePath));
    }


    public void deleteAll(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(c.getString(R.string.mainActivity_forget_all_dbs));
        alertDialogBuilder.setTitle(c.getString(R.string.action_confirm));
        alertDialogBuilder.setPositiveButton(c.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        myAdapter.flush();
                    }
                });
        alertDialogBuilder.setNegativeButton(c.getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_deleteAll) {
            deleteAll();

        }
        if (id == R.id.action_about) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("By Andrea Arduin");
            alertDialogBuilder.setTitle(c.getString(R.string.action_about));
            alertDialogBuilder.setNeutralButton(c.getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        if(id == R.id.action_settings){
            Intent intent= new Intent(this,TempPreferenceActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    private Observable<DatabaseHolder> getTableObservable(){
        return Observable.create(new Observable.OnSubscribe<DatabaseHolder>() {
            @Override
            public void call(Subscriber<? super DatabaseHolder> sub) {
                try {
                    list = prefs.loadList();
                } catch (Exception e) {
                    sub.onError(e);
                }
                for (int i = 0; i < list.size(); i++) sub.onNext(list.get(i));
                progressDialog.setMax(list.size());
                Log.wtf("size", "" + list.size());
                sub.onCompleted();
            }
        });
    }

    private Subscriber<DatabaseHolder> getTableSubscriber(){
        return new Subscriber<DatabaseHolder>() {
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
                progressDialog.cancel();
            }

            @Override
            public void onError(Throwable e) {
                Log.wtf("error",e.getLocalizedMessage());
            }

            @Override
            public void onNext(DatabaseHolder d) {
                done++;
                progressDialog.setProgress(done);
                try{
                    myAdapter.add(d);
                }
                catch(Exception e){
                    //Log.d("errore" ,param[0].toString());
                }
                request(1);
            }
        };
    }

    private void loadDBs(){
        Observable<DatabaseHolder> myObservable=getTableObservable();
        Subscriber<DatabaseHolder> mySubscriber=getTableSubscriber();
        Subscription mySubscription = myObservable.onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mySubscriber);
    }
}
