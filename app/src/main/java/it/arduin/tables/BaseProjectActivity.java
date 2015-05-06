package it.arduin.tables;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import butterknife.ButterKnife;

/**
 * Created by a on 04/05/2015.
 */
public class BaseProjectActivity extends ActionBarActivity {

    @Override
    public void setContentView(int resid){
        super.setContentView(resid);
        ButterKnife.inject(this);
    }
    public Context getContext(){
        return this;
    }
}
