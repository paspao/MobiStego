package it.mobistego.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import it.mobistego.beans.MobiStegoItem;

/**
 * Created by paspao on 05/01/15.
 * <p/>
 * <p/>
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

public class Utility {

    private final static String TAG=Utility.class.getName();
    public static final int SQUARE_BLOCK = 512;


    public static File createImageFile(Context ctx) throws IOException {
        // Create an image file name
        String mCurrentPhotoPath=null;
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.ITALY).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = Environment.getExternalStoragePublicDirectory(
          //      Environment.DIRECTORY_PICTURES);
        File storageDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath =  image.getAbsolutePath();
        Log.d(TAG,"Image file created in "+mCurrentPhotoPath);
        return image;
    }

    public static int squareBlockNeeded(int pixels) {
        int result = 0;
        int quadrato = SQUARE_BLOCK * SQUARE_BLOCK;
        int divid = pixels / (quadrato);
        int resto = pixels % (quadrato);
        result = divid + (resto > 0 ? 1 : 0);
        return result;
    }

    public static List<Bitmap> splitImage(Bitmap bitmap) {

        //For the number of rows and columns of the grid to be displayed


        //For height and width of the small image chunks
        int chunkHeight, chunkWidth;

        //To store all the small image chunks in bitmap format in this list
        ArrayList<Bitmap> chunkedImages = new ArrayList<Bitmap>();


        int rows = bitmap.getHeight() / SQUARE_BLOCK;
        int cols = bitmap.getWidth() / SQUARE_BLOCK;

        int chunkH_mod = bitmap.getHeight() % SQUARE_BLOCK;
        int chunkW_mod = bitmap.getWidth() % SQUARE_BLOCK;


        if (chunkH_mod > 0)
            rows++;
        if (chunkW_mod > 0)
            cols++;


        //xCoord and yCoord are the pixel positions of the image chunks
        int yCoord = 0;
        for (int x = 0; x < rows; x++) {
            int xCoord = 0;
            for (int y = 0; y < cols; y++) {
                chunkHeight = SQUARE_BLOCK;
                chunkWidth = SQUARE_BLOCK;

                if (y == cols - 1 && chunkW_mod > 0)
                    chunkWidth = chunkW_mod;

                if (x == rows - 1 && chunkH_mod > 0)
                    chunkHeight = chunkH_mod;

                chunkedImages.add(Bitmap.createBitmap(bitmap, xCoord, yCoord, chunkWidth, chunkHeight));
                xCoord += SQUARE_BLOCK;
            }
            yCoord += SQUARE_BLOCK;
        }


        return chunkedImages;
    }

    public static Bitmap mergeImage(List<Bitmap> images, int originalHeight, int originalWidth) {

        int rows = originalHeight / SQUARE_BLOCK;
        int cols = originalWidth / SQUARE_BLOCK;

        int chunkH_mod = originalHeight % SQUARE_BLOCK;
        int chunkW_mod = originalWidth % SQUARE_BLOCK;


        if (chunkH_mod > 0)
            rows++;
        if (chunkW_mod > 0)
            cols++;

        //create a bitmap of a size which can hold the complete image after merging
        Log.d(TAG, "Size width " + originalWidth + " size height " + originalHeight);
        Bitmap bitmap = Bitmap.createBitmap(originalWidth, originalHeight, Bitmap.Config.ARGB_4444);

        Canvas canvas = new Canvas(bitmap);
        int count = 0;
        int chunkWidth = SQUARE_BLOCK;
        int chunkHeight = SQUARE_BLOCK;

        for (int irows = 0; irows < rows; irows++) {
            for (int icols = 0; icols < cols; icols++) {

                canvas.drawBitmap(images.get(count), (chunkWidth * icols), (chunkHeight * irows), null);
                count++;

            }
        }

        return bitmap;
    }


    /**
     * Convert the byte array to an int array.
     *
     * @param b The byte array.
     * @return The int array.
     */

    public static int[] byteArrayToIntArray(byte[] b) {
        Log.v("Size byte array", b.length + "");
        int size = b.length / 3;
        Log.v("Size Int array", size + "");
        System.runFinalization();
        System.gc();
        Log.v("FreeMemory", Runtime.getRuntime().freeMemory() + "");
        int[] result = new int[size];
        int off = 0;
        int index = 0;
        while (off < b.length) {
            result[index++] = byteArrayToInt(b, off);
            off = off + 3;
        }

        return result;
    }

    /**
     * Convert the byte array to an int.
     *
     * @param b The byte array
     * @return The integer
     */
    public static int byteArrayToInt(byte[] b) {
        return byteArrayToInt(b, 0);
    }

    /**
     * Convert the byte array to an int starting from the given offset.
     *
     * @param b      The byte array
     * @param offset The array offset
     * @return The integer
     */
    public static int byteArrayToInt(byte[] b, int offset) {
        int value = 0x00000000;
        for (int i = 0; i < 3; i++) {
            int shift = (3 - 1 - i) * 8;
            value |= (b[i + offset] & 0x000000FF) << shift;
        }
        value = value & 0x00FFFFFF;
        return value;
    }

    /**
     * Convert integer array representing [argb] values to byte array
     * representing [rgb] values
     *
     * @param array Integer array representing [argb] values.
     * @return byte Array representing [rgb] values.
     */

    public static byte[] convertArray(int[] array) {
        byte[] newarray = new byte[array.length * 3];

        for (int i = 0; i < array.length; i++) {

			/*
             * newarray[i * 3] = (byte) ((array[i]) & 0xFF); newarray[i * 3 + 1]
			 * = (byte)((array[i] >> 8)& 0xFF); newarray[i * 3 + 2] =
			 * (byte)((array[i] >> 16)& 0xFF);
			 */

            newarray[i * 3] = (byte) ((array[i] >> 16) & 0xFF);
            newarray[i * 3 + 1] = (byte) ((array[i] >> 8) & 0xFF);
            newarray[i * 3 + 2] = (byte) ((array[i]) & 0xFF);

        }
        return newarray;
    }

    public static MobiStegoItem saveMobiStegoItem(String message, Bitmap srcEncoded,Context ctx) throws IOException {

        String name = UUID.randomUUID().toString();

        String fileNameOriginalPng = name + Constants.FILE_PNG_EXT;
        String fileNameCompressedJpg = name + Constants.FILE_JPG_EXT;
        String fileNameTxt = name + Constants.FILE_TXT_EXT;

        File mobiStegoDir = ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File rootDir = new File(mobiStegoDir,
                name);
        rootDir.mkdir();

        File originalImage = new File(rootDir,
                fileNameOriginalPng);
        originalImage.createNewFile();
        File compressedImage = new File(rootDir,
                fileNameCompressedJpg);
        compressedImage.createNewFile();
        File txt = new File(rootDir,
                fileNameTxt);
        txt.createNewFile();
        //originalImage.createNewFile();
        //txt.createNewFile();
        FileOutputStream foutOriginalImage = new FileOutputStream(originalImage);
        srcEncoded.compress(Bitmap.CompressFormat.PNG, 100, foutOriginalImage);
        foutOriginalImage.flush();
        foutOriginalImage.close();
        FileOutputStream foutCompressedImage = new FileOutputStream(compressedImage);
        srcEncoded.compress(Bitmap.CompressFormat.JPEG, 50, foutCompressedImage);
        foutCompressedImage.flush();
        foutCompressedImage.close();


        FileOutputStream foutText = new FileOutputStream(txt);
        OutputStreamWriter writer = new OutputStreamWriter(foutText);
        writer.append(message);
        writer.flush();
        writer.close();
        foutText.flush();
        foutText.close();
        MobiStegoItem result = new MobiStegoItem(message, originalImage, name, true,"");

        return result;
    }

    public static List<MobiStegoItem> listMobistegoItem(Context ctx) {
        List<MobiStegoItem> result = new ArrayList<>();
        String[] dirs = listMobistegoDir(ctx);
        if (dirs != null)
            for (String dir : dirs)
                result.add(loadMobiStegoItem(dir,ctx));
        return result;
    }

    public static String[] listMobistegoDir(Context ctx) {
        File mobiStegoDir =ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        String[] directories = mobiStegoDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return new File(dir, filename).isDirectory();
            }
        });
        return directories;
    }

    public static File getBitmapFile(MobiStegoItem mobiStegoItem,Context ctx) {
        File result = null;
        File mobiStegoDir = ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File rootDir = new File(mobiStegoDir,
                mobiStegoItem.getUuid());
        if (rootDir.exists()) {
            result = new File(rootDir,
                    mobiStegoItem.getUuid() + Constants.FILE_PNG_EXT);
        }
        return result;
    }

    public static MobiStegoItem loadMobiStegoItem(String dirName,Context ctx) {

        String fileNamePng = dirName + Constants.FILE_PNG_EXT;

        String fileNameTxt = dirName + Constants.FILE_TXT_EXT;
        File mobiStegoDir =ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File rootDir = new File(mobiStegoDir,
                dirName);
        File imageOriginal = new File(rootDir,
                fileNamePng);
        File txt = new File(rootDir,
                fileNameTxt);
        Scanner scan = null;
        MobiStegoItem result = null;
        try {
            scan = new Scanner(txt);

            StringBuilder message = new StringBuilder();
            while (scan.hasNextLine())
                message.append(scan.nextLine());
            scan.close();
            result = new MobiStegoItem(message.toString(), imageOriginal, dirName, true,"");
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Problem while loading", e);
        }
        return result;
    }

    public static boolean deleteMobiStegoItem(MobiStegoItem item,Context ctx) {
        boolean result = false;
        String dirName = item.getUuid() == null ? item.getBitmap().getParentFile().getName() : item.getUuid();

        String fileNamePng = dirName + Constants.FILE_PNG_EXT;
        String fileNameJpg = dirName + Constants.FILE_JPG_EXT;
        String fileNameTxt = dirName + Constants.FILE_TXT_EXT;

        File mobiStegoDir = ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File rootDir = new File(mobiStegoDir,
                dirName);
        File image = new File(rootDir,
                fileNamePng);
        File imageCompressed = new File(rootDir,
                fileNameJpg);
        File txt = new File(rootDir,
                fileNameTxt);
        if (rootDir.exists()) {
            image.delete();
            txt.delete();
            imageCompressed.delete();
            rootDir.delete();
            result = true;
        }
        return result;
    }

    public static boolean isEmpty(String str)
    {
        boolean result=true;
        if(str==null);
        else
        {
            str=str.trim();
            if(str.length()>0 && !str.equals("undefined"))
                result=false;
        }

        return result;
    }


    public static boolean isEmpty(Collection<?> collection)
    {
        boolean result=true;
        if (collection == null) {
            result=true;
        } else if (collection.isEmpty()) {
            result=true;
        } else {
            result=false;
        }
        return result;
    }
/*
    public static String getRealPathFromURI(Uri contentURI, ContentResolver resolver) {
        String result;
        Cursor cursor = resolver.query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
*/

    //From http://stackoverflow.com/questions/3401579/get-filename-and-path-from-uri-from-mediastore

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public static String encrypt(String valueToEnc,String password) throws Exception {

        //byte[] keyAsBytes;
        //keyAsBytes = password.getBytes("UTF-8");
        //Key key = new SecretKeySpec(keyAsBytes, "AES");
        MessageDigest digester = MessageDigest.getInstance("SHA-256");
        digester.update(String.valueOf(password).getBytes("UTF-8"));
        byte[] key = digester.digest();
        SecretKeySpec spec = new SecretKeySpec(key, "AES");

        Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, spec);  //////////LINE 20
        byte[] encValue = c.doFinal(valueToEnc.getBytes("UTF-8"));
        String encryptedValue = Base64.encodeToString(encValue,Base64.NO_CLOSE);
        return encryptedValue;
    }
    public static String decrypt(byte[] valueToDecode64,String password) throws Exception {
        byte[] todecode= Base64.decode(valueToDecode64,Base64.NO_WRAP);
        //byte[] keyAsBytes;
        //keyAsBytes = password.getBytes("UTF-8");
        //Key key = new SecretKeySpec(keyAsBytes, "AES");
        MessageDigest digester = MessageDigest.getInstance("SHA-256");
        digester.update(String.valueOf(password).getBytes("UTF-8"));
        byte[] key = digester.digest();
        SecretKeySpec spec = new SecretKeySpec(key, "AES");

        Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, spec);  //////////LINE 20
        byte[] encValue = c.doFinal(todecode);
        //String encryptedValue = Base64.encodeToString(encValue,Base64.DEFAULT);
        //return encryptedValue;
        return new String(encValue,"UTF-8");
    }
}
