package it.arduin.tables.model;

import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import it.arduin.tables.utils.DBUtils;

public class DatabaseHolder implements Parcelable {
    private String path;

    public String getPath(){
        return path;
    }
    public String getName(){
        int slash,dot;
        slash=path.lastIndexOf("/");
        dot=path.lastIndexOf('.');
        if(dot == -1) dot = path.length();
        return path.substring(1 + slash,dot);
    }

    public DatabaseHolder(String path) {
        this.path=path;
    }
    //parcelable

    public DatabaseHolder(Parcel source){
        path=source.readString();
    }
    @Override
    public int describeContents(){
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest,int flags){
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
        return DBUtils.getTables(path);
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
        return DBUtils.getIndexes(path);
    }

    public int getIndexNumber(){
        return getIndexes().size();
    }

    public void delete() throws Exception{
        try{
            DBUtils.deleteDatabase(path);
        }
        catch (Exception e){
            throw e;
        }
    }
}
