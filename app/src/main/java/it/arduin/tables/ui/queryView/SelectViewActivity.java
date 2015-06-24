package it.arduin.tables.ui.queryView;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import it.arduin.tables.R;
import it.arduin.tables.ui.BaseProjectActivity;


public class SelectViewActivity extends BaseProjectActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_view);
        Intent intent=getIntent();
        String table=intent.getStringExtra("table");
        String path=intent.getStringExtra("path");
        Boolean customQuery=intent.getBooleanExtra("customQuery", true);
        String query=intent.getStringExtra("query");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment newFragment = QuerySelectViewFragment.newInstance(path,table,query,customQuery);
        ft.add(R.id.container, newFragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
