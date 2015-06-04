package it.arduin.tables;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.InjectView;
import it.arduin.tables.utils.ViewUtils;
import it.arduin.tables.view.activity.BaseProjectActivity;
import it.arduin.tables.view.fragment.CreateTableSettingsFragment;

public class TableCreateActivity extends BaseProjectActivity {
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.fab) com.melnykov.fab.FloatingActionButton fab;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_create);
        FragmentManager fragMan = getFragmentManager();
        final FragmentTransaction fragTransaction = fragMan.beginTransaction();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(0xFFFFFFFF);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment myFrag = CreateTableSettingsFragment.newInstance();
                fragTransaction.add(R.id.container, myFrag ,"f1");
                fragTransaction.commit();
            }
        });
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
}
