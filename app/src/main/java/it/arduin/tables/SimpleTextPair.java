package it.arduin.tables;

/**
 * Created by a on 08/05/2015.
 */
public class SimpleTextPair {
    String desc,text;

    public SimpleTextPair(String desc, String text) {
        this.desc = desc;
        this.text = text;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
