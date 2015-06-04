package it.arduin.tables.presenter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import it.arduin.tables.utils.DBUtils;
import it.arduin.tables.model.DatabaseHolder;
import it.arduin.tables.R;
import it.arduin.tables.model.SharedPreferencesOperations;
import it.arduin.tables.view.activity.DatabaseViewActivity;
import it.arduin.tables.view.activity.MainActivity;
import it.arduin.tables.view.activity.TempPreferenceActivity;

public interface MainPresenter {

    void addAndSaveDatabase(String filePath,String fileName);

    void startDatabaseView(DatabaseHolder dbh);

    void saveDatabase(String fileName,String name);

    void createNewDatabaseAction();

    void checkAndSaveDatabase(Intent data);

    void forgetDatabase(int position);

    void forgetAllDatabases();

    void onDeleteAllPressed();

    void startSettingsActivity();

    void newDatabaseFromFileChooserAction();

    void newDatabaseFromPathAction();

    void onFABClick();

    void onAboutPressed();

    void onBackButtonPressed();

    void onClosePressed();
}
