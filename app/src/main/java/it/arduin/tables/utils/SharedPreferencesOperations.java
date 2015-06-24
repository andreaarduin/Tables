package it.arduin.tables.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

import it.arduin.tables.model.DatabaseHolder;


public class SharedPreferencesOperations {
    SharedPreferences prefs;
    public SharedPreferencesOperations(Context c){
        prefs = PreferenceManager.getDefaultSharedPreferences(c);
    }
    public void forgetAll(){
        int size=getSize();
        for(int i=0;i<size;i++) forgetDatabase(i);
    }
    public void addAndSaveDatabase(String filePath){
        SharedPreferences.Editor editor = prefs.edit();
        int size = getSize();
        filePath= filePath.trim();
        editor.putString("path"+size, filePath);
        editor.putInt("size",size+1);
        editor.apply();
    }
    public int getSize(){
        return prefs.getInt("size", 0);
    }
    public void setSize(int size){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("size",size);
        editor.apply();
    }

    public void forgetDatabase(int position){
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("path"+position);
        int max=getSize();
        editor.putInt("size",max-1);
        editor.apply();
    }

    public void forgetDatabase(String path){
        SharedPreferences.Editor editor = prefs.edit();
        int size=getSize();
        int deleted=0;
        for(int i = 0; i < size; i++){
            String db = prefs.getString("path"+i,"");
            if(db.equals(path)){
                editor.remove("path"+i);
                deleted++;
                Log.wtf("forgotdb",path+" at index "+i);
            }
        }
        editor.putInt("size",size-deleted);
        editor.apply();
    }

    public ArrayList<DatabaseHolder> loadList() throws Exception{
        int size=getSize();
        Log.wtf("sizei",size+"");
        ArrayList<DatabaseHolder> list=new ArrayList<>();
        for(int i=0;i<size;i++){
            String path;
            try{
                path=prefs.getString("path"+i,"err");
                path=path.trim();
            }
            catch(Exception e){
                throw e;
            }
            list.add(new DatabaseHolder(path));
        }
        Log.wtf("sizef",list.size()+"");
        return list;
    }


    ////
    public void setQueryLimit(int limit){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("query_limit",limit);
        editor.apply();
    }
    public void setQueryLimitSwitch(boolean b){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("query_limit_switch", b);
        editor.apply();
    }
    public int getQueryLimit(){
        return prefs.getInt("query_limit", 6);
    }
    public void setColumnLengthLimit(int limit){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("column_limit",limit);
        editor.apply();
    }
    public boolean getQueryLimitSwitch(){
        return prefs.getBoolean("query_limit_switch",true);
    }
    public void setColumnLimitSwitch(boolean b){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("column_limit_switch",b);
        editor.apply();
    }
    public boolean getColumnLimitSwitch(){
        return prefs.getBoolean("column_limit_switch",true);
    }
    public int getColumnLengthLimit(){
        return prefs.getInt("column_limit", 20);
    }
    public boolean getHeaderLimitSwitch(){
        return prefs.getBoolean("header_limit_switch", true);
    }
    public int getHeaderLengthLimit(){
        return prefs.getInt("column_limit", 20);
    }
    public void setHeaderLimitSwitch(boolean b){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("header_limit_switch",b);
        editor.apply();
    }
}

