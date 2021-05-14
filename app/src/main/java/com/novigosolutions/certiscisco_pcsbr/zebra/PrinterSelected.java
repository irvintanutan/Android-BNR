package com.novigosolutions.certiscisco_pcsbr.zebra;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;

import com.novigosolutions.certiscisco_pcsbr.activites.BaseActivity;
import com.novigosolutions.certiscisco_pcsbr.activites.PrintSelectedJobActivity;
import com.novigosolutions.certiscisco_pcsbr.activites.SelectedJobListActivity;
import com.novigosolutions.certiscisco_pcsbr.interfaces.PrintCallBack;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.ZebraPrinterLinkOs;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static android.content.ContentValues.TAG;

public class PrinterSelected {

    public static final int TEST_PRINT = 1;
    public static final int PRINT = 2;
    public static final String TYPE_SINGLE_PRINT = "Single Print";
    public static final String TYPE_BULK_PRINT = "Single Print";
    public String receiptType;
    public String setZplTemplate;
    public Context context;
    private Connection connection;
    private BluetoothAdapter mBTAdapter;
    public static String DEVICE_BT_ADDRESS = "";

    private Button btnTestPrint, btnReceiptPrint;
    private ZebraPrinter printer;

    PrintCallBack printCallBack;
    private SelectedBulkImagePrintTask bulkImagePrintTask = null;
    String status;

    public PrinterSelected(BaseActivity context, PrintCallBack printCallBack) {
        this.context = context;
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        this.printCallBack = printCallBack;
        setMacAdress();
    }

    public PrinterSelected(Context context){
        this.context=context;
    }

    public void setMacAdress() {
        DEVICE_BT_ADDRESS = Preferences.getPrinterMacAddres(context);
    }

    public boolean checkBluetoothConection() {
        return mBTAdapter != null && mBTAdapter.isEnabled();
    }

    public boolean checkDeviceAvailability() {
        if (TextUtils.isEmpty(Preferences.getPrinterDeviceName(context)) || TextUtils.isEmpty(Preferences.getPrinterMacAddres(context))) {
            return false;
        } else {
            return true;
        }
    }

    public void testprint(int type, String status) {
        printAuthalert(Preferences.getPrinterDeviceName(context) + " " + Preferences.getPrinterMacAddres(context), type, status);
    }

    public void printBulk(int type, String status) {
        printAuthalert(Preferences.getPrinterDeviceName(context) + " " + Preferences.getPrinterMacAddres(context), type, status);
    }

    private void printAuthalert(String str, final int type, final String status) {
        if (context != null) {
            this.status = status;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            // AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            alertDialogBuilder.setPositiveButton("Print", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (type) {
                        case TEST_PRINT:
                            //  testPrint();
                            dialog.dismiss();
                            doConnectionTest(true);
                            break;
                        case PRINT:
                            dialog.dismiss();
                            printBulk();
                            break;
                    }
                }
            });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                    if (status.equals("PRINT")) {
                        ((SelectedJobListActivity) context).onSupportNavigateUp();
                    } else if (status.equals("SINGLE")) {
                        ((PrintSelectedJobActivity) context).cancelProcess();
                    }else if(status.equals("COMPLETED")){
                        ((PrintSelectedJobActivity)context).cancelProcess();
                    }
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.setTitle("Confirmation");
            alertDialog.setMessage("Do you want to take test print on the device " + str);
            alertDialog.show();
        }

    }

//    public void testPrint(){
//        new Thread(new Runnable() {
//            public void run() {
//                Looper.prepare();
//                doConnectionTest(true);
//                Looper.loop();
//                Looper.myLooper().quit();
//            }
//        }).start();
//    }

//    public void receiptPrint(){
//        new Thread(new Runnable() {
//            public void run() {
//                Looper.prepare();
//                doConnectionTest(false);
//                Looper.loop();
//                Looper.myLooper().quit();
//            }
//        }).start();
//    }

    private void doConnectionTest(boolean isTest) {
        printer = connect();
        if (printer != null) {
            if (isTest) {
                //  sendTestPrint();
                print(getSetZplTemplate());
            } else {
                //sendReceiptPrint();
            }
        } else {
            disconnect();
        }
    }

    public boolean doConnectionTestForBulk() {
        printer = connect();
        if (printer != null) {
            return true;
        } else {
            disconnect();
            return false;
        }
    }

    // test print
    public void generatestTemplate() {
        //nidheesh
        Bitmap bitmap = logoConvertBitmap();
        Bitmap resizeImg = scaleBitmap(bitmap, 100, 40);
        Bitmap removeTranspancy = removeTransparency(resizeImg);
        Bitmap blackScale = blackScale(removeTranspancy);
//        Bitmap colorChange=processColorChangeBitmap(blackScale);
        //  Bitmap blackScale=blackScale(colorChange);
//        Bitmap grayScaleBitMap = ZPLUtil.toGrayScale(colorChange);

        //End
        String logo = getBase64String(blackScale);
        String header;
        String footer;
        if (TextUtils.isEmpty(Preferences.getPrintHeader(context))) {
            header = " ";
        } else {
            header = Preferences.getPrintHeader(context);
        }
        if (TextUtils.isEmpty(Preferences.getPrintFooter(context))) {
            footer = " ";
        } else {
            footer = Preferences.getPrintFooter(context);
        }

        Print print = new Print();
        print.setCertisAddress(header);
        print.setFooter(footer);
        print.setLogo(logo);
        GenerateImage generateImage = new GenerateImage(context, "test", print);
        generateImage.execute();
    }


    // receipt print
    public void printBulkReceipt() {
        //String zplTemplate = ZebraPrintUtility.prepareZpl(getPrintData());
        //printBulk(getSetZplTemplate());
    }

//    public void sendReceiptPrintBulk(Print print){
//        String zplTemplate = ZebraPrintUtility.prepareZpl(print);
//        printBulk(zplTemplate);
//    }

    // print template
    public void print(String zplTemplate) {
        try {
            ZebraPrinterLinkOs linkOsPrinter = ZebraPrinterFactory.createLinkOsPrinter(printer);

            PrinterStatus printerStatus = (linkOsPrinter != null) ? linkOsPrinter.getCurrentStatus() : printer.getCurrentStatus();

            if (printerStatus.isReadyToPrint) {

                byte[] configLabel = getConfigLabel(zplTemplate);

                connection.write(configLabel);
                // Toast.makeText(context, "Sending data...", Toast.LENGTH_SHORT).show();
                printCallBack.onPrint(2, "update count");
                showToast("Sending data...");
            }

            DemoSleeper.sleep(1500);
//            if (connection instanceof BluetoothConnection) {
//                String friendlyName = ((BluetoothConnection) connection).getFriendlyName();
//                DemoSleeper.sleep(500);
//            }
        } catch (ConnectionException e) {
            Log.e(TAG, "connect: ", e);
            // Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            showToast(e.getMessage());
        } finally {
            disconnect();
        }
        if (context instanceof SelectedJobListActivity) {
            printCallBack.onPrint(1, "completed");
        }
    }

    public void cancelBulkPrint() {
        if (bulkImagePrintTask != null) {
            bulkImagePrintTask.cancel(true);
        }
    }

    private void printBulk() {
        final List<String> zplTemplate = ((PrintSelectedJobActivity) context).getbulktemplateList();
        bulkImagePrintTask = new SelectedBulkImagePrintTask((PrintSelectedJobActivity) context, printer, connection, zplTemplate,status);
        bulkImagePrintTask.execute();
    }


    public ZebraPrinter connect() {
        //  Toast.makeText(context, "Connecting...", Toast.LENGTH_SHORT).show();
        showToast("Connecting...");
        if (connection == null) {
            connection = new BluetoothConnection(DEVICE_BT_ADDRESS);
        }
       // connection = new BluetoothConnection(DEVICE_BT_ADDRESS);

        try {
            DemoSleeper.sleep(2000);
            connection.open();
            //Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
            showToast("Connected");
        } catch (ConnectionException e) {
            Log.e(TAG, "connect: ", e);
            // Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            showToast(e.getMessage());
            DemoSleeper.sleep(1000);
            disconnect();
            crashalert(context, e);
        }

        ZebraPrinter printer = null;

        if (connection.isConnected()) {
            try {

                printer = ZebraPrinterFactory.getInstance(connection);
                String pl = SGD.GET("device.languages", connection);
            } catch (ConnectionException | ZebraPrinterLanguageUnknownException e) {
                Log.e(TAG, "connect: ", e);
                //   Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                showToast(e.getMessage());
                printer = null;
                DemoSleeper.sleep(1000);
                disconnect();
            }
        }

        return printer;
    }


    public void disconnect() {
        try {
            // Toast.makeText(context, "Disconnecting", Toast.LENGTH_SHORT).show();
            showToast("Disconnecting...");
            if (connection != null) {
                connection.close();
            }
        } catch (ConnectionException e) {
            Log.e(TAG, "connect: ", e);
            showToast(e.getMessage());
            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    private byte[] getConfigLabel(String zplTemplate) {
        byte[] configLabel = null;
        try {
            PrinterLanguage printerLanguage = printer.getPrinterControlLanguage();
            SGD.SET("device.languages", "zpl", connection);

            if (printerLanguage == PrinterLanguage.ZPL) {
                configLabel = zplTemplate.getBytes();
            } else if (printerLanguage == PrinterLanguage.CPCL) {
                String cpclConfigLabel = "! 0 200 200 406 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 TEST\r\n" + "PRINT\r\n";
                configLabel = cpclConfigLabel.getBytes();
            }
        } catch (ConnectionException e) {
            Log.e("ConectionExeption", e.getMessage() + " " + e.getCause());
        }

        return configLabel;
    }

    public void crashalert(final Context context, Exception e) {
        if (context != null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            // AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            alertDialogBuilder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ((Activity) context).finish();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.setTitle("Oops!");
            alertDialog.setMessage("Something went wrong" + String.valueOf(e.getMessage()));
            alertDialog.show();
        }
    }

    public Bitmap logoConvertBitmap() {
        String Base64Logo = Preferences.getLogo(context);
        byte[] decodedString = Base64.decode(Base64Logo, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }


    public Bitmap signConvertBitmap(String Base64Logo) {
        byte[] decodedString = Base64.decode(Base64Logo, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

//    private Bitmap processColorChangeBitmap(Bitmap src){
//
//        Bitmap dest = Bitmap.createBitmap(
//                src.getWidth(), src.getHeight(), src.getConfig());
//
//        for(int x = 0; x < src.getWidth(); x++){
//            for(int y = 0; y < src.getHeight(); y++){
//                int pixelColor = src.getPixel(x, y);
//                int pixelAlpha = Color.alpha(pixelColor);
//                int pixelRed = Color.red(pixelColor);
//                int pixelGreen = Color.green(pixelColor);
//                int pixelBlue = Color.blue(pixelColor);
//                int pixelBW = (pixelRed + pixelGreen + pixelBlue)/3;
//                int newPixel = Color.argb(
//                        pixelAlpha, pixelBW, pixelBW, pixelBW);
//
//                dest.setPixel(x, y, newPixel);
//            }
//        }
//        return dest;
//    }

    public Bitmap removeTransparency(Bitmap image) {
        Bitmap newBitmap = Bitmap.createBitmap(image.getWidth(),
                image.getHeight(), image.getConfig());
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(image, 0F, 0F, null);
        return newBitmap;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
        canvas.drawBitmap(bitmap, m, new Paint());
        return output;
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    private String getBase64String(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        return base64String;
    }

    private Bitmap blackScale(Bitmap bitmap) {
        Bitmap bwBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        float[] hsv = new float[3];
        for (int col = 0; col < bitmap.getWidth(); col++) {
            for (int row = 0; row < bitmap.getHeight(); row++) {
                Color.colorToHSV(bitmap.getPixel(col, row), hsv);
                if (hsv[2] > 0.8f) {
                    bwBitmap.setPixel(col, row, 0xffffffff);
                } else {
                    bwBitmap.setPixel(col, row, 0xff000000);
                }
            }
        }
        return bwBitmap;
    }

    private void showToast(final String message) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getSetZplTemplate() {
        return setZplTemplate;
    }

    public void setSetZplTemplate(String setZplTemplate) {
        this.setZplTemplate = setZplTemplate;
    }

    public String getLogo() {
        Bitmap bitmap = logoConvertBitmap();
        Bitmap resizeImg = scaleBitmap(bitmap, 100, 40);
        Bitmap removeTranspancy = removeTransparency(resizeImg);
        Bitmap blackScale = blackScale(removeTranspancy);
        String logo = getBase64String(blackScale);
        return logo;
    }

    public String getSign() {
        String sign = Preferences.getString("signature", context);
        if (sign == null || sign.isEmpty()) {
            return "";
        } else {
            Bitmap bitmap = signConvertBitmap(sign);
         //   Bitmap resizeImg = scaleBitmap(bitmap, 100, 40);
            Bitmap resizeImg=getResizedBitmap(bitmap,150,550);
            Bitmap blackScale = blackScale(resizeImg);
            String sig = getBase64String(blackScale);
            return sig;
        }
    }

    public String getSign(String signature) {
        String sign = signature;
        if (sign == null || sign.isEmpty()) {
            return "";
        } else {
            Bitmap bitmap = signConvertBitmap(sign);
            Bitmap resizeImg=getResizedBitmap(bitmap,150,550);
            //Bitmap resizeImg = scaleBitmap(bitmap, 100, 40);
            Bitmap blackScale = blackScale(resizeImg);
            String sig = getBase64String(blackScale);
            return sig;
        }
    }


}
