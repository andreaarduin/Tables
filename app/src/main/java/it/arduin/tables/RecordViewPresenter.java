package it.arduin.tables;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;

/**
 * Created by Andrea on 18/05/2015.
 */
public class RecordViewPresenter {
    RecordViewActivity mActivity;

    public RecordViewPresenter(RecordViewActivity mActivity) {
        this.mActivity = mActivity;
    }

    public void onFabClick() {
        onExit();
    }
    public void onExit(){
        if(mActivity.modified!=mActivity.columns)
            mActivity.applyChanges();
        mActivity.setResult(Activity.RESULT_OK, new Intent());
        NavUtils.navigateUpTo(mActivity, mActivity.getIntent());
        mActivity.finish();
    }
}
