package it.arduin.tables.ui.queryView;

import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;

/**
 * Created by Andrea on 26/05/2015.
 */
public interface QuerySelectViewPresenter {
    void onFabClick();

    void openNewRecordActivity();

    void onCustomQueryAction();

    void onRowClick(View v);

    void openCustomQuery(EditText input);

    void onRefreshAction();

    void deleteRow(TableRow tr, View v);
}
