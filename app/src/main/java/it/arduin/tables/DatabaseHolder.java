package it.arduin.tables;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.ArrayList;

public class DatabaseHolder implements Parcelable {
    private String name;
    private String path;
    //base
    public String getName(){
        return name;
    }
    public String getPath(){
        return path;
    }

    public DatabaseHolder(String text, String desc) {
        this.name = text;
        this.path=desc;
    }
    //parcelable
    public DatabaseHolder(Parcel source){
        name=source.readString();
        path=source.readString();
    }
    @Override
    public int describeContents(){
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest,int flags){
        dest.writeString(name);
        dest.writeString(path);
    }
    public final static Creator CREATOR = new Creator(){
        @Override
        public DatabaseHolder createFromParcel(Parcel source){
            return new DatabaseHolder(source);
        }
        @Override
        public DatabaseHolder[] newArray(int size){
            return new DatabaseHolder[size];
        }
    };
    //methods
    public ArrayList<String> getTables() {
        SQLiteDatabase db;
        Cursor c;
        ArrayList<String> list=new ArrayList<>();
        try {
            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            c = db.rawQuery("SELECT name FROM sqlite_master WHERE type = \"table\"", null);
        } catch (Exception e) {return list;}
        /*try{
            Cursor cursor=db.rawQuery("SELECT * from sqlite_master",null);
            if(cursor.getCount()!=0) list.insert("sqlite_master");
        }
        catch(Exception e){return list;}*/
        //if(db==null) return list;
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

    public boolean isAccessible(){
        try{
            SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READWRITE).close();
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
    public int getTableNumber(){
        return getTables().size();
    }
    public ArrayList<String> getIndexes() {
        SQLiteDatabase db;
        Cursor c;
        ArrayList<String> list=new ArrayList<>();
        try {
            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            c = db.rawQuery("SELECT name FROM sqlite_master WHERE type = \"index\"", null);
        } catch (Exception e) {return list;}
        /*try{
            Cursor cursor=db.rawQuery("SELECT * from sqlite_master",null);
            if(cursor.getCount()!=0) list.insert("sqlite_master");
        }
        catch(Exception e){return list;}*/
        //if(db==null) return list;
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
    public int getIndexNumber(){
        return getIndexes().size();
    }

    public void delete() throws Exception{
        try{
            File f= new File(path);
            f.delete();
        }
        catch (Exception e){
            throw e;
        }
    }
}
