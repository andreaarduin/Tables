package it.arduin.tables;

import android.app.ActionBar;
import android.app.Notification;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by a on 30/04/2015.
 */
public class ToolbarUtils {
    public static Toolbar getSettedToolbar(ActionBarActivity a,int resId){
        Toolbar toolbar=(Toolbar) a.findViewById(resId);
        if (toolbar != null) {
            a.setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(0xFFFFFFFF);
        }
        return toolbar;
    }

}
