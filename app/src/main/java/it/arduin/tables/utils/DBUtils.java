package it.arduin.tables.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

import it.arduin.tables.R;
import it.arduin.tables.model.ColumnPair;
import it.arduin.tables.model.ColumnSettingsHolder;
import it.arduin.tables.model.DatabaseHolder;
import it.arduin.tables.model.TableStructure;

/**
 * Created by a on 16/12/2014.
 */
public class DBUtils {




    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }



    public static void insertNewRecord(String dbPath,String table,ArrayList<String> names,ArrayList<String> values) throws Exception{
        try{
            String sql = "INSERT INTO " + table + "(",sql2=" VALUES(";
            int columns=names.size();
            for (int i = 0; i < columns; i++) {
                sql = sql + names.get(i)+ ", ";
                sql2=sql2+"'"+values.get(i)+"',";
            }
            if(sql.lastIndexOf(",")!=-1) sql=sql.substring(0,sql.lastIndexOf(","));
            if(sql2.lastIndexOf(",")!=-1) sql2=sql2.substring(0,sql2.lastIndexOf(","));
            sql=sql+")"+sql2+")";
            //Toast.makeText(c,sql,Toast.LENGTH_LONG).show();
            SQLiteDatabase db= SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            db.execSQL(sql);
            db.close();
        }
        catch(Exception e) {
            throw e;
        }

    }

    public static ArrayList<String> getTables(String path){
        SQLiteDatabase db;
        Cursor c;
        ArrayList<String> list=new ArrayList<>();
        try {
            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            c = db.rawQuery("SELECT name FROM sqlite_master WHERE type = \"table\"", null);
        } catch (Exception e) {return list;}
        if(c==null) return list;
        if (c.moveToFirst()) {
            c.moveToNext();
            while ( !c.isAfterLast() ) {
                list.add(c.getString(0));
                c.moveToNext();
            }
        }if(!c.isClosed()) c.close();
        if(db.isOpen()) db.close();
        return list;
    }

    public static ArrayList<String> getIndexes(String path) {
        SQLiteDatabase db;
        Cursor c;
        ArrayList<String> list=new ArrayList<>();
        try {
            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            c = db.rawQuery("SELECT name FROM sqlite_master WHERE type = \"index\"", null);
        } catch (Exception e) {return list;}
        if(c==null) return list;
        if (c.moveToFirst()) {
            c.moveToNext();
            while ( !c.isAfterLast() ) {
                list.add(c.getString(0));
                c.moveToNext();
            }
        }if(!c.isClosed()) c.close();
        if(db.isOpen()) db.close();
        return list;
    }


    public static void deleteRecord(String dbPath,String table,ArrayList<String> columnNames,ArrayList<String> columnValues) throws Exception{
        try{
            String sql = "DELETE FROM " + table + " WHERE ";
            int columns=columnNames.size();
            for (int i = 0; i < columns; i++)
                sql = sql + columnNames.get(i) + "='" + columnValues.get(i) + "' AND ";
            sql = sql.replace("=null", " is null");
            //sql = sql.replace("=''", " is null");
            sql=sql.substring(0,sql.lastIndexOf("AND"));
            //Toast.makeText(c,sql,Toast.LENGTH_LONG).show();
            // Toast.makeText(c,sql,Toast.LENGTH_LONG).show();
            SQLiteDatabase db= SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            db.execSQL(sql);
            db.close();
        }
        catch(Exception e){throw e;}

    }

    public static ArrayList<ColumnPair> getColumns(String path,String tableName){
        SQLiteDatabase db=null;
        try {
            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        } catch (Exception e) {        }
        ArrayList<ColumnPair> p= new ArrayList<>();
        if(db==null) return p;
        Cursor c = db.rawQuery("PRAGMA table_info('" + tableName + "')", null);
        if (c.moveToFirst()) {
            do {
                ColumnPair def=new ColumnPair(c.getString(1),c.getString(2));
                p.add(def);
                // Toast.makeText(DatabaseView.c,new Pair(c.getString(1),c.getString(2)).ToString(),Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
        }
        if(!c.isClosed()) c.close();
        return p;
    }
    public static ArrayList<String> getColumnsString(String path,String tableName){
        SQLiteDatabase db=null;
        try {
            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        } catch (Exception e) {        }
        ArrayList<String> p= new ArrayList<>();
        if(db==null) return p;
        Cursor c = db.rawQuery("PRAGMA table_info('" + tableName + "')", null);
        if (c.moveToFirst()) {
            do {
                p.add(c.getString(1));
                // Toast.makeText(DatabaseView.c,new Pair(c.getString(1),c.getString(2)).ToString(),Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
        }
        if(!c.isClosed()) c.close();
        return p;
    }

    public static void renameTable(String dbPath,String oldName,String newName) throws Exception{
        SQLiteDatabase db;
        db = SQLiteDatabase.openDatabase( dbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        db.execSQL("ALTER TABLE '" + oldName + "' RENAME TO '" + newName + "'");
    }

    public static void deleteTable(String dbPath,String table) throws Exception{
        try {
            String sql = "DROP TABLE IF EXISTS '" + table + "'";
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
            db.execSQL(sql);
            db.close();
        }
        catch (Exception e){
            throw e;
        }
    }

    public static boolean deleteDatabase(String path)throws Exception{
        File f= new File(path);
        return f.delete();
    }

    public static void createTable(String path,String name,ArrayList<ColumnSettingsHolder> data){
        try {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path,null);
            String sql = "CREATE TABLE IF NOT EXISTS `" + name + "` (";
            String primaryKeys = "";
            for (int i = 0; i < data.size(); i++) {
                sql += data.get(i).getColumnDefinition();
                if (i != data.size() - 1) {
                    sql += ",";
                }
                if (data.get(i).primaryKey) {
                    primaryKeys += data.get(i).name + ",";
                }
            }
            if (primaryKeys.length() != 0) {
                primaryKeys = primaryKeys.substring(0, primaryKeys.length() - 1);
                sql = sql + ", PRIMARY KEY(" + primaryKeys + ")";
            }
            sql += ")";
            db.execSQL(sql);
        }
        catch (Exception e){
            throw e;
        }
    }

    public static void createTable(String path,TableStructure t)throws Exception{
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path,null);
        String command = t.getCreateCommand();
        Log.wtf("execSQL", command);
        db.execSQL(command);

    }
}
