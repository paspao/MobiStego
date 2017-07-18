package it.mobistego.beans;

import java.io.File;

import it.mobistego.utils.Constants;

/**
 * Created by paspao on 15/02/15.
 * Copyright (C) 2015  Pasquale Paola
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
public class MobiStegoItem {

    private String message;
    private File bitmap;
    private File bitmapCompressed;
    private boolean encoded;
    private String uuid;
    private String password;

    private MobiStegoItem() {
        super();

    }

    public MobiStegoItem(String message, File bitmap, String uuid, boolean encoded,String password) {
        this();
        this.bitmap = bitmap;
        String tmp = bitmap.getAbsolutePath();
        tmp = tmp.substring(0, tmp.length() - 4);
        this.bitmapCompressed = new File(tmp + Constants.FILE_JPG_EXT);
        this.encoded = encoded;
        this.message = message;
        this.password=password;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public File getBitmap() {
        return bitmap;
    }

    public void setBitmap(File bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isEncoded() {
        return encoded;
    }

    public void setEncoded(boolean encoded) {
        this.encoded = encoded;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public File getBitmapCompressed() {
        return bitmapCompressed;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
