package it.arduin.tables.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by a on 09/03/2015.
 */
public class SharedPreferencesOperations {
    SharedPreferences prefs;
    public SharedPreferencesOperations(Context c){
        prefs = PreferenceManager.getDefaultSharedPreferences(c);
    }
    public void deleteAll(){
        int size=getSize(0);
        for(int i=0;i<size;i++) forgetDatabase(i,size);
    }
    public void addAndSaveDatabase(String filePath,String fileName,int size){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("path"+size, filePath);
        editor.putString("name"+size, fileName);
        int max=prefs.getInt("size", size);
        editor.putInt("size",max+1);
        Log.wtf("size up", "" + (max - 1));
        editor.apply();
    }
    public int getSize(int def){
        return prefs.getInt("size", def);
    }
    public void setSize(int size){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("size",size);
    }
    public void forgetDatabase(int position,int size){
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("path"+position);
        editor.remove("name" + position);
        int max=getSize(size);
        editor.putInt("size",max-1);
        Log.wtf("size up", "" + (max - 1));
        editor.apply();
    }
    public void forgetDatabase(int position){
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("path"+position);
        editor.remove("name" + position);
        int max=getSize(0);
        editor.putInt("size",max-1);
        Log.wtf("size down", "" + (max - 1));
        editor.apply();
    }
    public ArrayList<DatabaseHolder> loadList() throws Exception{
        int size=getSize(0);
        Log.wtf("sizei",size+"");
        ArrayList<DatabaseHolder> list=new ArrayList<>();
        for(int i=0;i<size;i++){
            String name="n",path="p";
            try{ name=prefs.getString("name"+i,"err");
                path=prefs.getString("path"+i,"err");
            }
            catch(Exception e){
                throw e;
            }
            list.add(new DatabaseHolder(name, path,i));
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

