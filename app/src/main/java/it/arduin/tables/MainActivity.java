package it.arduin.tables;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
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
    MainPresenter mPresenter;
    private ArrayList<DatabaseHolder> list;
    private SharedPreferencesOperations prefs;
    protected MainActivityAdapter myAdapter;
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
        mPresenter=new MainPresenter(this);
        prefs = new SharedPreferencesOperations(c);
        //---------------toolbar
        toolbar = ToolbarUtils.getSettedToolbar(this,R.id.toolbar);
        list= new ArrayList<>();
        initList();
        //new loadDBSTask(this).execute();
        loadDBs();
    }

    //pitchy patchy way to hide the bottom menu when 'menu' physical button gets pressed, instead showing the overflow menu
    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_MENU:
                getSupportActionBar().openOptionsMenu();
                return true;
        }

        return super.onKeyDown(keycode, e);
    }

    @Override
    public void onBackPressed(){
        mPresenter.onBackButtonPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        if(requestCode == ACTIVITY_CHOOSE_FILE) {///If the request was the file chooser, we check the path for a valid db and save it
            mPresenter.checkAndSaveDatabase(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            mPresenter.onDeleteAllPressed();
        }
        if (id == R.id.action_about) {
            mPresenter.onAboutPressed();
        }
        if(id == R.id.action_settings){
            mPresenter.startSettingsActivity();
        }

        return super.onOptionsItemSelected(item);
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
                        mPresenter.startDatabaseView(myAdapter.getItemAt(position));
                    }
                })
        );

        myAdapter = new MainActivityAdapter(list, R.layout.row_mainactivity, this);
        mRecyclerView.setAdapter(myAdapter);
        //fab.attachToRecyclerView(mRecyclerView);
        c=this;
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPresenter.onFABClick();
            }
        });

    }

    public void showCloseAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(c);
        alert.setTitle(c.getString(R.string.mainActivity_close_confirmation_title));
        alert.setMessage(c.getString(R.string.mainActivity_close_confirmation_message));
        alert.setPositiveButton(c.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mPresenter.onClosePressed();
            }
        });
        alert.setNegativeButton(c.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alert.create().show();
    }

    public void showNewDatabasePopup(){
        AlertDialog optionsDialog;
        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case NEW_DB_PATH:
                        mPresenter.newDatabaseFromPathAction();
                        break;
                    case NEW_DB_CHOOSE:
                        mPresenter.newDatabaseFromFileChooserAction();
                        break;
                    case NEW_DB_CREATE:
                        mPresenter.createNewDatabaseAction();
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


    public void showNewDatabaseFromPathAlert(){
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
                mPresenter.saveDatabase(value, fileName);
            }
        });
        alert.setNegativeButton(c.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }


    public void showNewDatabaseAlert(){
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
                    mPresenter.addAndSaveDatabase(path + "/" + name, name);
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




    public void showForgetDatabaseAlert(final int position){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setMessage(c.getString(R.string.mainActivity_forget_database) +" " + myAdapter.getItemAt(position).getName() + " ?");
        alertDialogBuilder.setTitle(c.getString(R.string.action_confirm));
        alertDialogBuilder.setPositiveButton(c.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        mPresenter.forgetDatabase(position);
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



    public void showDeleteAllAlert(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(c.getString(R.string.mainActivity_forget_all_dbs));
        alertDialogBuilder.setTitle(c.getString(R.string.action_confirm));
        alertDialogBuilder.setPositiveButton(c.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        mPresenter.forgetAllDatabases();
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




    protected void showAboutAlert() {
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
