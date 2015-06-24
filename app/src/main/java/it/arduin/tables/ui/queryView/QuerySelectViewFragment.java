package it.arduin.tables.ui.queryView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.melnykov.fab.ObservableScrollView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.arduin.tables.R;
import it.arduin.tables.ui.BaseProjectFragment;
import it.arduin.tables.utils.ViewUtils;
import it.arduin.tables.ui.ShortenedTextView;
import it.arduin.tables.ui.BaseProjectActivity;
import it.arduin.tables.ui.settings.SettingsActivity;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.graphics.Color.BLACK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuerySelectViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuerySelectViewFragment extends BaseProjectFragment {
    public static final int NEW_RECORD = 29;
    public static final int VIEW_RECORD = 0;
    private String query;
    public String table;
    private String title;
    public int columns;
    private int rowCount;
    private Intent intent;
    public String[] columnNames;
    public SQLiteDatabase db;
    public String path;
    public boolean customQuery;
    private ProgressDialog progressPopup;
    protected QuerySelectViewPresenter mPresenter;
    private Subscription mySubscription;
    Observable<TableRow> myObservable;
    @InjectView(R.id.scrollView1) ScrollView scrollview;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.fab) com.melnykov.fab.FloatingActionButton fab;
    @InjectView(R.id.table_main) public TableLayout stk;
    private Subscriber<TableRow> mySubscriber;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment QuerySelectViewFragment.
     */
    public static QuerySelectViewFragment newInstance(String path, String table,String query,boolean customQuery) {
        QuerySelectViewFragment fragment = new QuerySelectViewFragment();
        Bundle args = new Bundle();
        args.putString("path", path);
        args.putString("table", table);
        args.putString("query", query);
        args.putBoolean("customQuery", customQuery);
        fragment.setArguments(args);
        return fragment;
    }

    public QuerySelectViewFragment() {
        // Required clear public constructor
    }
    //fetch args
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle args = getArguments();
        if (args != null) {
            path=args.getString("path");
            table=args.getString("table");
            query=args.getString("query");
            customQuery=args.getBoolean("customQuery");
        }
        myObservable = AppObservable.bindFragment(this, getTableObservable()).cache();
        //myObservable = getTableObservable();
        mySubscriber = getTableSubscriber();
        mPresenter = new QuerySelectViewPresenterFragImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_query_select_view, container, false);
        ButterKnife.inject(this,v);
        return v;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        scrollview.post(new Runnable() {
            @Override public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_UP);///setta lo scroll all'inizio
            }
        });
        //toolbar settings------------------------------------------------------

        if (toolbar != null) getCastActivity().setSupportActionBar(toolbar);
        //toolbar.setLogo(getResources().getDrawable(R.drawable.ic_dblogo));
        try{ getCastActivity().setSupportActionBar(toolbar);
            getCastActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(0xFFFFFFFF);
            toolbar.setTitle("");
        }
        catch(Exception e){
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        fab.attachToScrollView((ObservableScrollView) rootView.findViewById(R.id.scrollView1));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPresenter.onFabClick();
            }
        });
        loadQuery();
    }

    public SelectViewActivity getCastActivity(){
        return (SelectViewActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

    }

    private ShortenedTextView getBaseSTV(Context context){
        ShortenedTextView tv0 = new ShortenedTextView(context);
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
                        getActivity().runOnUiThread(new Runnable() {
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
                            final ShortenedTextView tv0 = getBaseSTV(context);
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
                                    ShortenedTextView tv0 = getBaseSTV(context);
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
                progressPopup= ViewUtils.getSettedCancelableProgressDialog((BaseProjectActivity) getActivity(), getString(R.string.QuerySelectViewActivity_loading_message));
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
                Log.d("error"," "+e.getMessage());
            }
        };
    }

    public synchronized void loadQuery(){
        stk.removeAllViews();
        mySubscription = myObservable.onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mySubscriber);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mySubscription.unsubscribe();
        if(progressPopup!=null) progressPopup.dismiss();
        progressPopup=null;
    }


    private void showDeleteRecordAlert(final View v){
        if(customQuery) return;
        final TableRow tr=(TableRow) v;
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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


    public void showCustomQueryAlert(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getString(R.string.QuerySelectViewActivity_insert_query_message));
        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
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


}
