package it.arduin.tables.utils;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.widget.Toolbar;

import it.arduin.tables.ui.view.activity.BaseProjectActivity;
import jp.wasabeef.recyclerview.animators.*;

/**
 * Created by a on 30/04/2015.
 */
public class ViewUtils {
    public static Toolbar getSettedToolbar(BaseProjectActivity a,int resId){
        Toolbar toolbar=(Toolbar) a.findViewById(resId);
        if (toolbar != null) {
            a.setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(0xFFFFFFFF);
        }
        return toolbar;
    }
    public static ProgressDialog getSettedCancelableProgressDialog(final BaseProjectActivity caller,String message){
        ProgressDialog progressDialog = new ProgressDialog(caller.getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                caller.finish();
            }
        });
        progressDialog.setMessage(message);//cambia stringa mouna
        return progressDialog;
    }
    public static ProgressDialog getSettedProgressDialog(final BaseProjectActivity caller,String message){
        ProgressDialog progressDialog = new ProgressDialog(caller.getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);//cambia stringa mouna
        return progressDialog;
    }
    public static void setRecyclerViewAnimator(android.support.v7.widget.RecyclerView r){
        r.setItemAnimator(new SlideInLeftAnimator());
        r.getItemAnimator().setAddDuration(500);
        r.getItemAnimator().setRemoveDuration(500);
    }


}
