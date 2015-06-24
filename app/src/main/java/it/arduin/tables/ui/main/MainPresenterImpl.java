package it.arduin.tables.ui.main;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import it.arduin.tables.R;
import it.arduin.tables.model.DatabaseHolder;
import it.arduin.tables.utils.SharedPreferencesOperations;
import it.arduin.tables.utils.DBUtils;
import it.arduin.tables.ui.databaseView.DatabaseViewActivity;
import it.arduin.tables.ui.settings.TempPreferenceActivity;

public class MainPresenterImpl implements MainPresenter {
    MainActivity mActivity;

    public MainPresenterImpl(MainActivity mActivity) {
        this.mActivity = mActivity;
    }

    public void addAndSaveDatabase(String filePath){
        DatabaseHolder d = new DatabaseHolder(filePath);
        new SharedPreferencesOperations(mActivity).addAndSaveDatabase(d.getPath());
        mActivity.mAdapter.add(d);
    }
    public void startDatabaseView(DatabaseHolder dbh){
        Intent intent = new Intent(mActivity, DatabaseViewActivity.class);
        intent.putExtra("db", dbh);
        mActivity.startActivity(intent);
    }
    public void saveDatabase(String fileName,String name){
        try{
            SQLiteDatabase db = SQLiteDatabase.openDatabase(fileName, null, SQLiteDatabase.OPEN_READWRITE);
            db.close();
            addAndSaveDatabase(fileName);
        }
        catch(Exception e){
            Toast.makeText(mActivity, fileName, Toast.LENGTH_LONG).show();
        }
    }
    public void createNewDatabaseAction(){
        mActivity.showNewDatabaseAlert();
    }
    public void checkAndSaveDatabase(Intent data) {
        Uri uri = data.getData();
        String filePath = DBUtils.getPath(mActivity, uri);
        if (filePath == null) filePath = uri.getPath();
        try {
            SQLiteDatabase.openDatabase(filePath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception e) {
            Toast.makeText(mActivity, mActivity.getString(R.string.mainActivity_wrong_file), Toast.LENGTH_LONG).show();
        }
        String fileName = filePath.substring(1 + filePath.lastIndexOf("/"), filePath.lastIndexOf('.'));
        if (filePath.equals(""))
            Toast.makeText(mActivity, "errore, path nullo", Toast.LENGTH_LONG).show();
        else {
            addAndSaveDatabase(filePath);

        }
    }
    public void forgetDatabase(int position){
        mActivity.mAdapter.forget(position);
    }
    public void forgetAllDatabases(){
        mActivity.mAdapter.flush();
    }
    public void onDeleteAllPressed(){
        mActivity.showDeleteAllAlert();
    }
    public void startSettingsActivity(){
        Intent intent= new Intent(mActivity,TempPreferenceActivity.class);
        mActivity.startActivity(intent);
    }
    public void newDatabaseFromFileChooserAction(){
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("application/octet-stream");
        intent = Intent.createChooser(chooseFile, mActivity.getString(R.string.mainActivity_file_chooser_title));
        mActivity.startActivityForResult(intent, MainActivity.ACTIVITY_CHOOSE_FILE);
    }
    public void newDatabaseFromPathAction(){
        mActivity.showNewDatabaseFromPathAlert();
    }
    public void onFABClick(){
        mActivity.showNewDatabasePopup();
    }

    public void onAboutPressed() {
        mActivity.showAboutAlert();
    }
    public void onBackButtonPressed(){
        mActivity.showCloseAlert();
    }

    public void onClosePressed() {
        mActivity.finish();
    }

    @Override
    public void createAndSaveDatabase(String fullPath, String name) {
        SQLiteDatabase.openOrCreateDatabase(fullPath,null);
        addAndSaveDatabase(fullPath);
    }
}
