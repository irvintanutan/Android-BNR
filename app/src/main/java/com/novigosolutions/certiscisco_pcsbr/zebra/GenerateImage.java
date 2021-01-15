package com.novigosolutions.certiscisco_pcsbr.zebra;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.gson.internal.$Gson$Types;
import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.content.WebViewContent;
import com.novigosolutions.certiscisco_pcsbr.activites.PrintActivity;
import com.novigosolutions.certiscisco_pcsbr.activites.PrinterConfigurationActivity;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class GenerateImage extends AsyncTask<Void, Void, Bitmap> {

    private Context context;
    private String type;
    private Print print;

    public GenerateImage(Context context, String type, Print print) {
        this.context = context;
        this.type = type;
        this.print = print;
    }

    @Override
    public Bitmap doInBackground(Void... args) { // separate thread

        String html = "";
        if (type.equals("test")) {
            html = Template.generateTestPrintTemplate(print);
        } else {
            html = Template.generateReceiptPrintTemplate(print);
        }
//        Bitmap b = new Html2Bitmap.Builder()
//                .setContext(context)
//                .setContent(WebViewContent.html(html))
//                .build()
//                .getBitmap();
        Bitmap b = new Html2Bitmap.Builder()
                .setContext(context)
                .setBitmapWidth(550)
                .setContent(WebViewContent.html(html))
                .build()
                .getBitmap();
        return b;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        float min = 1.2f;
        int bitmaHeight = bitmap.getHeight();
        min = min * bitmaHeight;
        String layoutHeight = "^LL" + (int) min;
        // main thread
        if (bitmap != null) {
            Bitmap grayScaleBitMap = ZPLUtil.toGrayScale(bitmap);
            String template = ZPLUtil.getZplCode(grayScaleBitMap, true,true);
            template = template.replaceAll("#SPACE", layoutHeight);
            Log.i("template", template);
            if (context instanceof PrinterConfigurationActivity) {
                ((PrinterConfigurationActivity) context).setTemplate(template);
            } else if (context instanceof PrintActivity) {
               ((PrintActivity) context).setTemplate(template);
            }
        }
    }

}
