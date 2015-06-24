package it.arduin.tables.ui.main;

import android.content.Intent;

import it.arduin.tables.model.DatabaseHolder;

public interface MainPresenter {

    void addAndSaveDatabase(String filePath);

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

    void createAndSaveDatabase(String fullPath, String name);
}
