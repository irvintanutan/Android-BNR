package com.novigosolutions.certiscisco_pcsbr.zebra;

import java.util.List;

public class Template {

    private static final String FONT_SIZE = "22px";
    private static final String START_MARGIN_TOP = "4px";
    private static final String LINE_HEIGHT = "33px";

    public static String generateTestPrintTemplate(Print print) {
        return "<html>\n" +
                "<head>\n" +
                "   <style type='text/css'>\n" +
                "       p {\n" +
                "           font-size: " + FONT_SIZE +
                "       }" +
                "   </style>" +
                "</head>\n" +
                "<body>\n" +
                "   <img src=\"data:image/jpg;base64," + print.getLogo() + "\">\n" +
                "   <p>" + print.getCertisAddress() + "</p>\n" +
                "   <p>Test Print...</p>\n" +
                "        <p style=\"text-align: center; width: 100%;\" >" + print.getFooter() + "</p>\n" +
                "</body>\n" +
                "</html>";
    }

    public static String generateReceiptPrintTemplate(Print print) {

        String html = "<html>\n" +
                "<head>\n" +
                "   <style type='text/css'>\n" +
                "\t#sp{\n" +
                "\t\t      margin-top: 20px;\n" +
                "  margin-bottom: 30px;\n" +
                "\t\t}"+
                "       body {\n" +
                "           font-size: " + FONT_SIZE +";"+
                "           line-height:"+LINE_HEIGHT+";"+
                "           margin-top: " + START_MARGIN_TOP +";"+
                "           text-transform:uppercase;"+
                "       }\n" +
                " table {width: 100%;border-collapse: collapse;}\n" +
                "           th,td {padding: 4px;" +
                "border: 3px solid black;"+
                "}"+
                "       p {\n" +
                "           font-size: " + FONT_SIZE +
                "       }" +
                "   </style>" +
                "</head>\n" +
                "    <body>\n" +
                "        <img src=\"data:image/jpg;base64," + print.getLogo() + "\">\n" +
                "\n" +
                "<p>" + print.getCertisAddress() + "</p>\n" +
                "        <b><u>Service Details</u></b>\n" +
                "        <br><label>Date : " + print.getDate() + "</label>\n" +
                "        <br><label>Service Start Time : " + print.getServiceStartTime() + " </label>\n" +
                "        <br><label>Service End Time : " + print.getServiceEndTime() + "</label>\n" +
                "\n" +
                "        <br><br>\n" +
                "        <b><u>Transactional Details</u></b> \n" +
                "        <br><label>Transaction ID : " + print.getTransactionId() + " </label>\n" +
                "        <br><label>Functional Location : " + print.getFunctionalLocation() + " </label>\n" +
                "        <br><label>Delivery Point : " + print.getDeliveryPoint() + "</label>\n" +
                "        <br><label>Nature of Transaction : " + print.getNatureOfTransaction() + " </label>\n" ;

        if(print.isCollection()){
            html+= "        <br><label>collection Mode : " + print.getCollectionMode() + " </label>\n";
        }

              html+=  "        <br><label>Bank : " + print.getBank() + "</label>\n" +
                "     \n" +
                "        <br><br>\n" +
                "        <b><u>Customer Details</u></b>\n" +
                "        <br><label>Customer Name : " + print.getCustomerName() + "</label>\n" +
                "        <br><label>Branch Name : " + print.getBranchName() + "</label>\n" +
                "        <br><label>Customer Location : " + print.getCustomerLocation() + "</label>\n" +
                "\n" +
                "        <br><br>\n" +
                "        <b><u>Description of secured package(s)</u></b>\n" +
                "        <br><br>\n" +
                "        <table>\n" +
                "         <tr>   <th>" + getLabel3(print.isCollection()) + "</th><th>Quantity</th><th>Seal No/Deno</th></tr>\n";

        List<Content> contents = print.getContentList();
        int countListSize = (contents != null && !contents.isEmpty()) ? contents.size() : 0;

        for (int i = 0; i < countListSize; i++) {
            html += "<tr>" +
                    "<td>" + contents.get(i).getDescription() + "</td>" +
                    "<td><center>" + contents.get(i).getQty() + "</center></td>" +
                    "<td style=\"font-size: 26px;text-align: center;\" >";

            List<String> sealNoList = contents.get(i).getSealNoList();
            int sealNoListSize = (sealNoList != null && !sealNoList.isEmpty()) ? sealNoList.size() : 0;

            for (int j = 0; j < sealNoListSize; j++) {
                if(j > 0){
                    html += "<br>";
                }
                html += sealNoList.get(j);
            }

            List<Denomination> denominationList = contents.get(i).getDenominationList();
            int denomListSize = (denominationList != null && !denominationList.isEmpty()) ? denominationList.size() : 0;

            for (int k = 0; k < denomListSize; k++) {
                if(k>0){
                    html+= "<br>";
                }
                List<String> envelopsList = denominationList.get(k).getEnvelopsList();
                int envelopListSize = (envelopsList != null && !envelopsList.isEmpty()) ? envelopsList.size() : 0;

                html +=  denominationList.get(k).getBagName() +
                        "<br> Seal No : " + denominationList.get(k).getSealNo() +
                        "<br>Env Qty : " + envelopListSize +
                        "<br>Envelopes No :<br> ";

                for (int l = 0; l < envelopListSize; l++) {
                    if (l>0){
                        html+=  "<br>" ;
                    }
                    html += envelopsList.get(l);
                }
            }

            html += "</td>" +
                    "</tr>";
        }

        if(countListSize==0){
            html+=" <td colspan=\"3\"><br><label><center>";
            if(print.isCollection())
            {
                html+="No Collection";
            }else {
                html+="No Collection";
            }
            html+="</center></label></td></tr>";
        }

        html += "</table>";

        html += "        <br><label><b>" + getLabel1(print.isCollection()) + "</b></label>\n" +
                "        <br><label>Certis Transaction Officer (TO) : " + print.getCertisTransactionOfficer() + " </label>\n" +
                "        <br><label>Transaction Officer ID : " + print.getTransactionOfficerId() + " </label>\n" +
                "        <br><label>To Signature :</label>\n"+
                "        <br> <img height=\"70\" width=\"220\" src=\"data:image/jpg;base64,"+print.getCustomerAcknowledgment()+"\">\n" +
                "        <hr id='sp' style='border: 1px dashed black;' />\n" +
                "        <label><b>" + getLabel1(!print.isCollection()) + "</b></label>\n" +
                "        <br><label>Name : "+print.getCName()+"</label>\n" +
                "        <br><label>Staff ID : "+print.getStaffId()+"</label>\n" +
                "        <br><label>Customer Acknowledgment :</label>\n" +
                "        <br> <img height=\"70\" width=\"220\" src=\"data:image/jpg;base64,"+print.getCustomerSignature()+"\">\n" +
                "        \n" +
                "        \n" +
                "        <hr style='border: 1px dashed black;' />\n" +
                "        <p style=\"text-align: center; width: 100%;\" >" + print.getFooter() + "</p>\n" +
                "    </body>" +
                "</html>";

        return html;
    }

    private static String getLabel1(boolean isCollection) {
        return isCollection ? "Received By" : "Handed Over By";
    }

    private static String getLabel3(boolean isCollection) {
        return isCollection ? " Collection Mode " : " Delivery Mode ";
    }
}
