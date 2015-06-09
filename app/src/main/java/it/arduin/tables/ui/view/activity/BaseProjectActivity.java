package it.arduin.tables.ui.view.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.ButterKnife;

/**
 * Created by a on 04/05/2015.
 */
public class BaseProjectActivity extends AppCompatActivity {

    @Override
    public void setContentView(int resid){
        super.setContentView(resid);
        ButterKnife.inject(this);
    }
    public Context getContext(){
        return this;
    }

    public void showToast(String string) {
        Toast.makeText(this, string,Toast.LENGTH_LONG).show();
    }
}
