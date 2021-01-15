package com.novigosolutions.certiscisco_pcsbr.zebra;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.content.WebViewContent;
import com.novigosolutions.certiscisco_pcsbr.activites.PrintActivity;

import java.util.ArrayList;
import java.util.List;

public class BulkImageGenerator extends AsyncTask<Void, Void, List<String>> {

    private Context context;
    private List<Print> printList;
    private ProgressDialog dialog;

    public BulkImageGenerator(Context context, List<Print> printList) {
        this.context = context;
        this.printList = printList;
        dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("The data is being prepared. Kindly wait...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected List<String> doInBackground(Void... voids) {

        List<String> imageList = new ArrayList<>();

        for (int i = 0; i < printList.size(); i++) {
            String html = Template.generateReceiptPrintTemplate(printList.get(i));
//            Bitmap b = new Html2Bitmap.Builder()
//                    .setContext(context)
//                    .setContent(WebViewContent.html(html))
//                    .build()
//                    .getBitmap();
            Bitmap b = new Html2Bitmap.Builder()
                    .setContext(context)
                    .setBitmapWidth(550)
                    .setContent(WebViewContent.html(html))
                    .build()
                    .getBitmap();

            if (b != null) {
                float min = 1.2f;
                int bitmaHeight = b.getHeight();
                min = min * bitmaHeight;
                String layoutHeight = "^LL" + (int) min;
                Bitmap grayScaleBitMap = ZPLUtil.toGrayScale(b);
                String template = ZPLUtil.getZplCode(grayScaleBitMap, true,false);
                template = template.replaceAll("#SPACE", layoutHeight);
                imageList.add(template);
            }
        }

        return imageList;
    }

    @Override
    protected void onPostExecute(List<String> bulkPrintTemplate) {
        super.onPostExecute(bulkPrintTemplate);
        if (dialog != null) {
            dialog.dismiss();
        }
        ((PrintActivity)context).setbulktemplateList(bulkPrintTemplate);
    }
}
