package it.arduin.tables.view.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.TintCheckBox;
import android.support.v7.internal.widget.TintCheckedTextView;
import android.support.v7.internal.widget.TintEditText;
import android.support.v7.internal.widget.TintRadioButton;
import android.support.v7.internal.widget.TintSpinner;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import it.arduin.tables.R;
import it.arduin.tables.model.SharedPreferencesOperations;


public class SettingsActivity extends ActionBarActivity {
    final static int RATIO=50;// query limit unit/ minimum change
    final static int QUERY_LIMIT_MAX=41;//1+query limit range
    final static int COLUMN_LIMIT_MAX=51;//1+column limit range
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new MyPreferenceFragment()).commit();

        //addPreferencesFromResource(R.xml.settings);
        /*
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);
        //toolbar.setLogo(getResources().getDrawable(R.drawable.ic_dblogo));
        try{ setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(0xFFFFFFFF);
            toolbar.setTitle(getString(R.string.action_settings));
        }
        catch(Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        final Context c=this;
        SeekBar seekBar=(SeekBar)findViewById(R.id.seekbar_query);
        seekBar.setMax(QUERY_LIMIT_MAX);
       //Toast.makeText(this,""+ new SharedPreferencesOperations(this).getQueryLimit(), Toast.LENGTH_LONG).show();
        seekBar.setProgress(new SharedPreferencesOperations(this).getQueryLimit());
        final TextView textView=(TextView)findViewById(R.id.seekbar_query_value);
        textView.setText(""+seekBar.getProgress()*RATIO);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress<1){ seekBar.setProgress(1);
                progress=1;}
                if(progress==QUERY_LIMIT_MAX) textView.setText(getString(R.string.query_limit_preference_off));
                textView.setText(""+RATIO*progress);
                new SharedPreferencesOperations(c).setQueryLimit(seekBar.getProgress());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        //COLUMN LIMIT VIEW BLOCK
        SeekBar seekBarColumn=(SeekBar)findViewById(R.id.seekbar_column);
        seekBarColumn.setMax(COLUMN_LIMIT_MAX);
        seekBarColumn.setProgress(new SharedPreferencesOperations(this).getColumnLengthLimit());
        final TextView textViewColumn=(TextView)findViewById(R.id.seekbar_column_value);
        textViewColumn.setText(""+seekBarColumn.getProgress()*RATIO);
        seekBarColumn.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 1) {
                    seekBar.setProgress(1);
                    progress = 1;
                }
                if (progress == COLUMN_LIMIT_MAX)
                    textViewColumn.setText(getString(R.string.query_limit_preference_off));
                textViewColumn.setText("" + RATIO * progress);
                new SharedPreferencesOperations(c).setColumnLengthLimit(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });*/
    }



    public static int getQueryLimit(Context c){
        return new SharedPreferencesOperations(c).getQueryLimit()*RATIO;
    }
    public static int getColumnLimit(Context c){
        SharedPreferencesOperations prefs= new SharedPreferencesOperations(c);
        if(!prefs.getColumnLimitSwitch()) return -1;
        else  return prefs.getColumnLengthLimit();
    }
    public static int getHeaderLimit(Context c){
        SharedPreferencesOperations prefs= new SharedPreferencesOperations(c);
        if(!prefs.getHeaderLimitSwitch()) return -1;
        else  return prefs.getColumnLengthLimit();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        // Allow super to try and create a view first
        final View result = super.onCreateView(name, context, attrs);
        if (result != null) {
            return result;
        }
        Log.d("att",attrs.getAttributeValue("http://schemas.android.com/apk/res/android"," " +"tint"));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // If we're running pre-L, we need to 'inject' our tint aware Views in place of the
            // standard framework versions
            switch (name) {
                case "EditText":{
                    TintEditText t=new TintEditText(this, attrs);

                    return t;
                    }
                case "Spinner":
                    return new TintSpinner(this, attrs);
                case "CheckBox":
                    return new TintCheckBox(this, attrs);
                case "RadioButton":
                    return new TintRadioButton(this, attrs);
                case "CheckedTextView":
                    return new TintCheckedTextView(this, attrs);
            }
        }

        return null;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_qryselect_view, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            NavUtils.navigateUpTo(this,getIntent());
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }

    }
}
