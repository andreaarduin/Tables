package it.arduin.tables.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by a on 19/02/2015.
 */
public class ShortenedTextView extends TextView {
    private String realText;
    public ShortenedTextView(Context c){
        super(c);
    }
    public ShortenedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public ShortenedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setShortenedText(String s,int length){
        realText=s;
        if(s==null) this.setText("");
        else if(length==-1) this.setText(s);
        else if(s.length()>length) this.setText(s.substring(0,length)+"..");
        else this.setText(s);
    }
    public String getText(){
        return realText;
    }
}
