package com.novigosolutions.certiscisco_pcsbr.zebra;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

// source: https://stackoverflow.com/a/51686894/5180017

public class ZPLUtil {

    public static String getZplCode(Bitmap bitmap, Boolean addHeaderFooter,Boolean isSinglePrint) {
        ZPLConverter zp = new ZPLConverter();
        zp.setCompressHex(true);
        zp.setBlacknessLimitPercentage(50);
        Bitmap grayBitmap = toGrayScale(bitmap);
        return zp.convertFromImage(grayBitmap, addHeaderFooter,isSinglePrint);
    }

    public static Bitmap toGrayScale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap grayScale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(grayScale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return grayScale;
    }
    
}
