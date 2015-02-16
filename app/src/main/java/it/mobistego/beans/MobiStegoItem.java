package it.mobistego.beans;

import android.graphics.Bitmap;

/**
 * Created by paspao on 15/02/15.
 */
public class MobiStegoItem {
    
    private String message;
    private Bitmap bitmap;
    private boolean encoded;

    public MobiStegoItem(){
        super();
        
    }

    public MobiStegoItem(String message,Bitmap bitmap,boolean encoded){
        this();
        this.bitmap=bitmap;
        this.encoded=encoded;
        this.message=message;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isEncoded() {
        return encoded;
    }

    public void setEncoded(boolean encoded) {
        this.encoded = encoded;
    }
}
