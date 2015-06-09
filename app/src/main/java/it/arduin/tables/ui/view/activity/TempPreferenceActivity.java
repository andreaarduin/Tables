package it.arduin.tables.ui.view.activity;

import android.content.Context;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.arduin.tables.R;
import it.arduin.tables.model.SharedPreferencesOperations;


public class TempPreferenceActivity extends ActionBarActivity {
    SharedPreferencesOperations prefs;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_preference);
        ButterKnife.inject(this);
        prefs=new SharedPreferencesOperations(this);
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
        seekBar.setMax(SettingsActivity.QUERY_LIMIT_MAX);
       //Toast.makeText(this,""+ new SharedPreferencesOperations(this).getQueryLimit(), Toast.LENGTH_LONG).show();
        seekBar.setProgress(new SharedPreferencesOperations(this).getQueryLimit());
        final TextView textView=(TextView)findViewById(R.id.seekbar_query_value);
        textView.setText(""+seekBar.getProgress()*SettingsActivity.RATIO);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress<1){ seekBar.setProgress(1);
                progress=1;}
                if(progress==SettingsActivity.QUERY_LIMIT_MAX) textView.setText("MAX");
                textView.setText(""+SettingsActivity.RATIO*progress);
                prefs.setQueryLimit(seekBar.getProgress());
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
        seekBarColumn.setMax(SettingsActivity.COLUMN_LIMIT_MAX);
        seekBarColumn.setProgress(new SharedPreferencesOperations(this).getColumnLengthLimit());
        final TextView textViewColumn=(TextView)findViewById(R.id.seekbar_column_value);
        textViewColumn.setText(""+seekBarColumn.getProgress());
        seekBarColumn.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 1) {
                    seekBar.setProgress(1);
                    progress = 1;
                }
                if (progress == seekBar.getMax())
                    textViewColumn.setText("MAX");
                textViewColumn.setText("" +  progress);
                prefs.setColumnLengthLimit(seekBar.getProgress());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        //buttons block
       // Switches
        Switch queryLimit=(Switch) findViewById(R.id.query_limit_switch);
        queryLimit.setChecked(prefs.getQueryLimitSwitch());
        Switch columnLimit=(Switch) findViewById(R.id.column_limit_switch);
        columnLimit.setChecked(prefs.getColumnLimitSwitch());
        Switch headerLimit=(Switch) findViewById(R.id.header_limit_switch);
        headerLimit.setChecked(prefs.getHeaderLimitSwitch());
        queryLimit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.setQueryLimitSwitch(isChecked);
            }
        });
        columnLimit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.setColumnLimitSwitch(isChecked);
            }
        });
        headerLimit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.setHeaderLimitSwitch(isChecked);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_temp_preference, menu);
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
           // Toast.makeText(this,"porkadyo amiki",Toast.LENGTH_LONG).show();
            NavUtils.navigateUpTo(this,getIntent());
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
