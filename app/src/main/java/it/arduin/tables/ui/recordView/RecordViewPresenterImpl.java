package it.arduin.tables.ui.recordView;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;

/**
 * Created by Andrea on 18/05/2015.
 */
public class RecordViewPresenterImpl implements RecordViewPresenter {
    RecordViewActivity mActivity;

    public RecordViewPresenterImpl(RecordViewActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void onFabClick() {
        onExit();
    }
    @Override
    public void onExit(){
        if(mActivity.modified!=mActivity.columns && !mActivity.isCustomQuery())
            mActivity.applyChanges();
        mActivity.setResult(Activity.RESULT_OK, new Intent());
        NavUtils.navigateUpTo(mActivity, mActivity.getIntent());
        mActivity.finish();
    }

    public void applyChanges(){

    }
}
