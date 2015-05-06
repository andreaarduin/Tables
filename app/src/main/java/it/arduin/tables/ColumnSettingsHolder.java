package it.arduin.tables;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

/**
 * Created by a on 01/02/2015.
 */
public class ColumnSettingsHolder {
    String name;
    Boolean autoincrement, unique, primaryKey, notNull;
    String type;
    /*INTEGER
        TEXT
        BLOB
        REAL
        NUMERIC
     */
    public String getColumnDefinition(){
        String definition=("`"+name+"` ");
        definition+=type;
        if(notNull) definition+=" NOT NULL ";
        if(autoincrement) definition+=" AUTOINCREMENT ";
        if(unique) definition+=" UNIQUE ";
        return definition;
    }

    public ColumnSettingsHolder(ColumnSettingsAdapter.ViewHolder v){
        this.name=v.name.getText().toString();
        this.autoincrement=v.ai.isChecked();
        this.unique=v.un.isChecked();
        this.primaryKey=v.pk.isChecked();
        this.notNull=v.nn.isChecked();
        this.type=v.type.getSelectedItem().toString();
    }

    public ColumnSettingsHolder(String type, String name) {
        this.type = type;
        this.name = name;
        notNull=false;
        autoincrement=false;
        primaryKey=false;
        unique=false;
    }
    public ColumnSettingsHolder( String name) {
        this.type = "TEXT";
        this.name = name;
        notNull=false;
        autoincrement=false;
        primaryKey=false;
        unique=false;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAutoincrement() {
        return autoincrement;
    }

    public void setAutoincrement(Boolean autoincrement) {
        this.autoincrement = autoincrement;
    }

    public Boolean getUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public Boolean getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Boolean getNotNull() {
        return notNull;
    }

    public void setNotNull(Boolean notNull) {
        this.notNull = notNull;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}