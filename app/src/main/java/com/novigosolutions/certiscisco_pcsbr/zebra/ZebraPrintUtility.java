package com.novigosolutions.certiscisco_pcsbr.zebra;

public class ZebraPrintUtility {

    public static final int HEADING_FONT_SIZE = 15;
    public static final int LEFT_MARGIN = 3;
    public static int PREVIOUS_MARGIN = 0;

    public static String prepareZpl(Print print) {

        String zplTemplate = "^XA\n" +
                "\n" +
//                "^FO" + LEFT_MARGIN + "," + addYAxis(200) + "\n" +
                "^FO" + LEFT_MARGIN + ",0\n" +
                print.getLogo() +
                "^FO" + LEFT_MARGIN + "," + addYAxis(175) + "\n" +
                "^FB600,4,3,L\n" +
                "^CF,20\n" +
                "^FD" + print.getCertisAddress() + "^FS\n" +
                "\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(100) + "\n" +
                "^CF0," + HEADING_FONT_SIZE + "\n" +
                "^FDService Details^FS\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(25) + "\n" +
                "^CFD^FDDate: " + print.getDate() + "^FS\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(25) + "\n" +
                "^CFD^FDService Start Time: " + print.getServiceStartTime() + "^FS\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(25) + "\n" +
                "^CFD^FDService End Time: " + print.getServiceEndTime() + "^FS\n" +
                "\n" +
                "\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(40) + "\n" +
                "^CF0," + HEADING_FONT_SIZE + "\n" +
                "^FDTransaction Details^FS\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(25) + "\n" +
                "^CFD^FDTransaction ID: " + print.getTransactionId() + "^FS\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(25) + "\n" +
                "^CFD^FDFunctional Location: " + print.getFunctionalLocation() + "^FS\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(25) + "\n" +
                "^CFD^FDDelivery Point: " + print.getDeliveryPoint() + "^FS\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(25) + "\n" +
                "^CFD^FDNature of Transaction: " + print.getNatureOfTransaction() + "^FS\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(25) + "\n" +
                "^CFD^FDCollection Mode: " + print.getCollectionMode() + "^FS\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(25) + "\n" +
                "^CFD^FDBank: " + print.getBank() + "^FS\n" +
                "\n" +
                "\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(40) + "\n" +
                "^CF0," + HEADING_FONT_SIZE + "\n" +
                "^FDCustomer Details^FS\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(25) + "\n" +
                "^CFD^FDCustomer Name: " + print.getCustomerName() + "^FS\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(25) + "\n" +
                "^CFD^FDCustomer Location: " + print.getCustomerLocation() + "^FS\n" +
                "\n" +
                "\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(40) + "\n" +
                "^CF0," + HEADING_FONT_SIZE + "\n" +
                "^FDDescription of Contents^FS\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(25) + "\n" +
                "^CFO,20\n" +
                "^FH^FDDescription_09Quantity_09Seal No^FS\n";

        for (Content content : print.getContentList()) {
            zplTemplate += "^FO" + LEFT_MARGIN + "," + addYAxis(25) + "\n" +
                    "^CFO,20\n" ;
        //            "^FH^FD" + content.getDescription() + "_09" + content.getQty() + "_09" + content.getSealNo() + "^FS\n";
        }

        zplTemplate += "\n";
        zplTemplate += "^FO" + LEFT_MARGIN + "," + addYAxis(45) + "\n";

        if (print.isCollection()) {
            zplTemplate += "^CFD^FDReceived By^FS\n";
        } else {
            zplTemplate += "^CFD^FDHanded Over By^FS\n";
        }

        zplTemplate += "^FO" + LEFT_MARGIN + "," + addYAxis(25) + "\n" +
                "^CFD^FDCertis Transaction Officer: " + print.getCertisTransactionOfficer() + "^FS\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(25) + "\n" +
                "^CFD^FDTransaction Officer ID: " + print.getTransactionOfficerId() + "^FS\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(50) + "\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(10) + "^GB700,1,3^FS\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(25) + "\n";

        if (print.isCollection()) {
            zplTemplate += "^CFD^FDHanded Over By^FS\n";
        } else {
            zplTemplate += "^CFD^FDReceived By^FS\n";
        }

        zplTemplate += "^FO" + LEFT_MARGIN + "," + addYAxis(25) + "\n" +
                "^CFD^FDCustomer Acknowledgement:^FS\n" +
//                "^FO" + LEFT_MARGIN + "," + addYAxis(220) + "^GB700,1,3^FS\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(25) + print.getCustomerAcknowledgment() + "^FS\n" +
                "^FO" + LEFT_MARGIN + "," + addYAxis(150) + "^GB700,1,3^FS\n" +
                "\n" +
                "^FO30," + addYAxis(20) + "\n" +
                "^CFDC^FH\\^FDFor any inquiries, please contact " + print.getInquiryContact() + "^FS\n" +
                "\n" +
                "^FO40," + addYAxis(30) + "\n" +
                "^CFDC^FH\\^FDPlease keep the receipt for future reference.^FS\n" +
                "\n" +
                "^FO230," + addYAxis(25) + "\n" +
                "^CFDC^FH\\^FDTHANK YOU^FS\n" +
                "\n" +
                "^XZ \n";

        System.out.println(zplTemplate);

        return zplTemplate;
    }

    private static int addYAxis(int marginTop) {

        if (marginTop > 0) {
            PREVIOUS_MARGIN = PREVIOUS_MARGIN + marginTop;
        }

        return PREVIOUS_MARGIN;
    }

//    public static String getTestTemplate() {
//        return "^XA\n" +
//                "\n" +
//                "^FO50,25\n" +
//                "^CF,20\n" +
//                "^FDTest print line 1^FS\n" +
//                "^FO50,50\n" +
//                "^FDTest print line 2^FS\n" +
//                "^FO50,75\n" +
//                "^FDTest print line 3^FS\n" +
//                "\n" +
//                "^XZ";
//    }

//    public static String getTestTemplate(String logo) {
//        return "^XA\n" +
//                "~SD25\n" +
//                "^LL200\n" +
//                "^FO" + LEFT_MARGIN + ",10\n" +
//                logo +
//                "^FO25,130\n" +
//                "^CFDC^FH\\^FDFor any inquiries, please contact +65 91471620^FS\n" +
//                "^FO40,155\n" +
//                "^CFDC^FH\\^FDPlease keep the receipt for future reference.^FS\n" +
//                "^FO230,180\n" +
//                "^CFDC^FH\\^FDTHANK YOU^FS\n" +
//                "^XZ";
//    }

    public static String getTestTemplate(String logo, String header, String footer) {
        return "^XA\n" +
                "^LL350\n" +
                "^FO" + LEFT_MARGIN + ",10\n" +
                logo +
                "^FO20,130" +
                "^FB600,4,3,L" +
                "^CF,20" +
                "^FD" + header + "^FS" +
                "^FO20,250\n" +
                "^CFDC^FB555,3,5,C\n" +
                "^FD" + footer + "^FS\n" +
                "^XZ";
    }

}
