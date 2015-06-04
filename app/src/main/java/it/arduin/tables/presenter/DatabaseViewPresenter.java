package it.arduin.tables.presenter;

import android.content.Context;
import android.view.View;

/**
 * Created by Andrea on 15/05/2015.
 */
public interface DatabaseViewPresenter {

    void onFabClick();

    void onRecyclerItemPressed(View view, int position);

    void onActionInfoPressed();

    void onDeleteOptionPressed(String name, int position);

    void deleteTable(String name, int position);

    void onActivityResult() ;

    void reloadTables();

    void onRenameOptionPressed(String name, int position);

    void renameTable(String name, int position, String input);

    void viewTableInfo(String name, int position, Context c);

    void onActionQueryPressed();

    void query(String value);

    void onActionSelectQueryPressed();

    void querySelect(String value);
}
