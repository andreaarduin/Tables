package it.arduin.tables;

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
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by a on 16/12/2014.
 */
public class DBUtils {


    public static ArrayList<String> getTables(String path){
        SQLiteDatabase db=null;
        Cursor c=null;
        ArrayList<String> list=new ArrayList<>();

        try {
            //Toast.makeText(DatabaseView.c,path,Toast.LENGTH_LONG).show();
            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            c = db.rawQuery("SELECT name FROM sqlite_master WHERE type = \"table\"", null);
        } catch (Exception e) {return list;}
        try{
            Cursor cursor=db.rawQuery("SELECT * from sqlite_master",null);
            if(cursor.getCount()!=0) list.add("sqlite_master");
        }
        catch(Exception e){}
        if(db==null) return list;
        if(c==null) return list;
        if (c.moveToFirst()) {
            c.moveToNext();
            while ( !c.isAfterLast() ) {
                list.add(c.getString(0));
                //Toast.makeText(DatabaseView.c,c.getString(0),Toast.LENGTH_LONG).show();
                c.moveToNext();
            }
        }if(!c.isClosed()) c.close();
        if(db.isOpen()) db.close();
        return list;
    }

    public void deleteRecord(DatabaseHolder dbh,String table,ArrayList<String> columnNames,ArrayList<String> columnValues){
        String sql = "DELETE FROM " + table + " WHERE ";
        int columns=columnNames.size();
        for (int i = 0; i < columns; i++)
            sql = sql + columnNames.get(i) + "='" + columnValues.get(i) + "' AND ";
        sql = sql.replace("=null", " is null");
        //sql = sql.replace("=''", " is null");
        sql=sql.substring(0,sql.lastIndexOf("AND"));
        //Toast.makeText(c,sql,Toast.LENGTH_LONG).show();
        // Toast.makeText(c,sql,Toast.LENGTH_LONG).show();
        SQLiteDatabase db= SQLiteDatabase.openDatabase(dbh.getPath(), null, SQLiteDatabase.CREATE_IF_NECESSARY);
        try{db.execSQL(sql);}
        catch(Exception e){throw e;}
        db.close();
    }

    public static ArrayList<ColumnPair> getColumns(String path,String tableName){
        SQLiteDatabase db=null;
        try {
            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        } catch (Exception e) {        }
        ArrayList<ColumnPair> p= new ArrayList<>();
        if(db==null) return p;
        Cursor c = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
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

}
