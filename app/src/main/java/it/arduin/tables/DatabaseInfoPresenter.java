package it.arduin.tables;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

/**
 * Created by Andrea on 14/05/2015.
 */
public class DatabaseInfoPresenter {
    DatabaseInfoActivity mActivity;

    public DatabaseInfoPresenter(DatabaseInfoActivity mActivity) {
        this.mActivity = mActivity;
    }

    public void onFabClicked() {
        mActivity.showDeletePopup();
    }

    public void deleteDatabase() {
        try {
            mActivity.dbh.delete();
            new SharedPreferencesOperations(mActivity).forgetDatabase(mActivity.dbh.getIndex());
            mActivity.setResult(DatabaseViewActivity.DATABASE_DELETED, new Intent());
            NavUtils.navigateUpTo(mActivity, mActivity.getIntent());
            mActivity.finish();
        }
        catch (Exception e){
            mActivity.showError(e);
        }
    }
}