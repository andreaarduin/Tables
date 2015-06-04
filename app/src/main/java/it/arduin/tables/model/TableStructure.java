package it.arduin.tables.model;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Andrea on 21/05/2015.
 */
public class TableStructure {
    String name;
    ArrayList<ColumnSettingsHolder> columns;
    ArrayList<ColumnSettingsHolder> primaryKeys;
    ArrayList<ColumnSettingsHolder> uniques;

    public TableStructure(String name){
        this.name=name;
    }

    public TableStructure(String name, ArrayList<ColumnSettingsHolder> columns, ArrayList<ColumnSettingsHolder> primaryKeys, ArrayList<ColumnSettingsHolder> uniques) {
        this.name = name;
        this.columns = columns;
        this.primaryKeys = primaryKeys;
        this.uniques = uniques;
    }

    public ArrayList<ColumnSettingsHolder> getColumns() {

        return columns;
    }

    public void setColumns(ArrayList<ColumnSettingsHolder> columns) {
        this.columns = columns;
    }

    public ArrayList<ColumnSettingsHolder> getUniques() {
        return uniques;
    }

    public void setUniques(ArrayList<ColumnSettingsHolder> uniques) {
        this.uniques = uniques;
    }

    public ArrayList<ColumnSettingsHolder> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(ArrayList<ColumnSettingsHolder> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public String getCreateCommand(){
        String r="CREATE TABLE IF NOT EXISTS '"+name+"' (";
        String c="",pk="",u="";
        for(int i=0;i<columns.size();i++){
            if(i==columns.size()-1) c+=columns.get(i).getColumnDefinition();
            else c+=columns.get(i).getColumnDefinition()+",";
        }
        pk=" PRIMARY KEY(";
        for(int i=0;i<primaryKeys.size();i++){
            Log.d("s",primaryKeys.get(i).getName());
            pk+=primaryKeys.get(i).getName();
            if(i!=primaryKeys.size()-1) pk+=" , ";
        }
        pk+=")";
        u=" UNIQUE(";
        for(int i=0;i<uniques.size();i++){
            if(i==uniques.size()-1) u+=uniques.get(i).getName();
            else u+=uniques.get(i).getName()+",";
        }
        u+=")";
        r=r+c;
        Log.d("pk",pk);
        if(!pk.equals(" PRIMARY KEY()") && u.equals(" UNIQUE()")) r = r + "," + pk;
        else if(pk.equals(" PRIMARY KEY()") && !u.equals(" UNIQUE()")) r = r + "," + u;
        else if(!pk.equals(" PRIMARY KEY()") && !u.equals(" UNIQUE()")) r = r + "," + pk + "," +u;
        r+=")";
        Log.d("r",r);
        return r;
    }
}
