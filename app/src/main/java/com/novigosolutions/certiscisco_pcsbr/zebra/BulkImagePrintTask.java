package com.novigosolutions.certiscisco_pcsbr.zebra;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.novigosolutions.certiscisco_pcsbr.activites.HomeActivity;
import com.novigosolutions.certiscisco_pcsbr.activites.PrintActivity;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLinkOs;

import java.util.List;

public class BulkImagePrintTask extends AsyncTask<Void, Integer, Boolean> {

    private PrintActivity context;
    private ZebraPrinter printer;
    private Connection connection;
    private List<String> printList;
    private String status;

    public BulkImagePrintTask(PrintActivity context, ZebraPrinter printer,
                              Connection connection, List<String> printList,String status) {
        this.context = context;
        this.printer = printer;
        this.connection = connection;
        this.printList = printList;
        this.status = status;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            ZebraPrinterLinkOs linkOsPrinter = ZebraPrinterFactory.createLinkOsPrinter(printer);

            SGD.SET("device.languages", "zpl", connection);

            PrinterStatus printerStatus = (linkOsPrinter != null) ? linkOsPrinter.getCurrentStatus() : printer.getCurrentStatus();

            if (printerStatus.isReadyToPrint) {
                connection.write("^XA ~JA ^XZ ".getBytes());//clear prevoius job
                for (int i = 0; i < printList.size(); i++) {
                    if (isCancelled()) {
                        break;
                    } else {
                        connection.write(printList.get(i).getBytes());
                        publishProgress((i+1));
                    }
                }
            }
        } catch (ConnectionException ex) {
            Log.e("Error", "doInBackground: ", ex);
            context.raiseSnakbar(ex.getMessage());
            return false;
        }

        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        context.onPrint(2, String.valueOf(values[0]));
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        try {
            if (connection.isConnected()) {
                connection.close();
            }
        } catch (ConnectionException ex) {
            context.raiseSnakbar(ex.getMessage());
        }

        if (aBoolean) {
            if(status.equals("SINGLE")){
                context.onPrint(1, "completed");
            }else {
                Intent intent = new Intent(context, HomeActivity.class);
                context.startActivity(intent);
                context.finish();
            }
        } else {
            context.raiseSnakbar("Something went wrong while printing receipt...");
        }
    }
}
