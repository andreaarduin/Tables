package it.arduin.tables.ui.databaseInfo;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NavUtils;

import java.io.File;

import it.arduin.tables.ui.databaseView.DatabaseViewActivity;
import it.arduin.tables.utils.SharedPreferencesOperations;

public class DatabaseInfoPresenterImpl implements DatabaseInfoPresenter {
    DatabaseInfoActivity mActivity;

    public DatabaseInfoPresenterImpl(DatabaseInfoActivity mActivity) {
        this.mActivity = mActivity;
    }

    public void onDeleteAction() {
        mActivity.showDeletePopup();
    }

    @Override
    public void onFabClicked() {
        String fileName = mActivity.dbh.getPath();
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("application/octet-stream");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"Share File"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "File Name");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(fileName)));
        mActivity.startActivity(Intent.createChooser(emailIntent, "Share File"));
    }

    public void deleteDatabase() {
        try {
            mActivity.dbh.delete();
            new SharedPreferencesOperations(mActivity).forgetDatabase(mActivity.dbh.getPath());
            mActivity.setResult(DatabaseViewActivity.DATABASE_DELETED, new Intent());
            NavUtils.navigateUpTo(mActivity, mActivity.getIntent());
            mActivity.finish();
        }
        catch (Exception e){
            mActivity.showError(e);
        }
    }
}
