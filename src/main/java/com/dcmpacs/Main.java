package com.dcmpacs;

import java.io.File;
//import java.util.List;

//import org.apache.commons.cli.CommandLine;
//import org.dcm4che2.data.DicomObject;
//import org.dcm4che2.net.CommandUtils;
//import org.dcm4che2.net.UserIdentity;
import org.dcm4che2.tool.dcmsnd.DcmSnd;


public class Main {
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        //DcmSnd dicom = new DcmSnd();

        //CommandLine cl = parse(args);
        DcmSnd dcmsnd = new DcmSnd("DCMSND");
        dcmsnd.setCalledAET("RIOGRANDE");
        dcmsnd.setRemoteHost("192.168.70.70");
        dcmsnd.setRemotePort(11112);
        dcmsnd.setSendFileRef(false);

        dcmsnd.setStorageCommitment(false);
        String remoteStgCmtAE = null;
        
        dcmsnd.setPackPDV(true);
        dcmsnd.setTcpNoDelay(true);
        //dcmsnd.setPriority(CommandUtils.HIGH);
        
        System.out.println("Scanning files to send");

        /*
         * adicionar arquivos dicom para lista 
         *
        for (int i = 1, n = argList.size(); i < n; ++i)*/
            dcmsnd.addFile(new File("C:\\Users\\victormacedo\\Desktop\\10273"));

        
        if (dcmsnd.getNumberOfFilesToSend() == 0) {
            System.exit(2);
        }

        dcmsnd.configureTransferCapability();
        dcmsnd.setTlsNeedClientAuth(true);
        try {
            dcmsnd.start();
        } catch (Exception e) {
            System.err.println("ERROR: Failed to start server for receiving " +
                    "Storage Commitment results:" + e.getMessage());
            System.exit(2);
        }
        try {
            try {
                dcmsnd.open();
            } catch (Exception e) {
                System.err.println("ERROR: Failed to establish association:"
                        + e.getMessage());
                System.exit(2);
            }
            dcmsnd.send();
            if (dcmsnd.isStorageCommitment()) {
                dcmsnd.commit();
                dcmsnd.close();
            }
            if (remoteStgCmtAE != null) {
                try {
                    dcmsnd.openToStgcmtAE();
                    dcmsnd.commit();
                    dcmsnd.close();
                } catch (Exception e) {
                    System.err.println("ERROR: Failed to establish association:"
                            + e.getMessage());
                    System.exit(2);
                }

            }
        } finally {
            dcmsnd.stop();
        }
    }
}