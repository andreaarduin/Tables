package it.arduin.tables.model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by a on 15/12/2014.
 */
public class TableHolder {
    public String name;
    public ArrayList<String> fields;
    public DatabaseHolder parent;

    public TableHolder(String name, ArrayList<String> fields,DatabaseHolder parent) {
        this.name = name;
        this.fields = fields;
        this.parent=parent;
    }
    public TableHolder(String name, String[] fields,DatabaseHolder parent) {
        this.name = name;
        this.fields = new ArrayList<>(Arrays.asList(fields));
        this.parent=parent;
    }



    public String getFieldsString(){
        String s="";
        try {
            for (int i = 0; i < fields.size(); i++) s = s + fields.get(i) + "\n";
            s = s.substring(0, s.length() - 1);
        }
        catch(Exception e){}
        return s;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getFields() {
        return fields;
    }


    @Override
    public String toString() {
        return "TableHolder{" +
                "name='" + name + '\'' +
                ", fields=" + fields.toString() +
                '}';
    }
}
