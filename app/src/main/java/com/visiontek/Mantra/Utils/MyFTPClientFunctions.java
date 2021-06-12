package com.visiontek.Mantra.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileOutputStream;

public class MyFTPClientFunctions {

    public int Toknow, working;
    public FileOutputStream desFileStream;
    public String fdate, name;
    public String network;
    public FTPClient mFTPClient = new FTPClient();

    public boolean ftpConnect(String host, String username, String password,
                              int port) {

        Toknow = 0;
        try {
            mFTPClient = new FTPClient();
            mFTPClient.setConnectTimeout(30000);
            mFTPClient.connect(host, port);
            System.out.println("...Host Found...MyFTPClientFunction" + Toknow);
            try {
                if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                    mFTPClient.setDataTimeout(45000);
                    mFTPClient.setSoTimeout(45000);
                    boolean status = mFTPClient.login(username, password);
                    mFTPClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
                    mFTPClient.enterLocalPassiveMode();
                    Toknow = 10;
                    System.out.println("Authentication=" + status);
                    return status;
                }
                Toknow = 11;
                System.out.println("Replay code Null...MyFTPClientFuntction " + Toknow);
                return false;
            } catch (Exception e) {
                System.out.println("Connection Error or Server Down....returns false" + Toknow);
                Toknow = 12;
                return false;
            }
        } catch (Exception e) {
            System.out.println("0.Server downor Connection error...MyFTPClientFuntction " + Toknow);
            Toknow = 13;
            return false;
        }

    }


    public void ftpDisconnect() {
        if (mFTPClient.isConnected()) {
            try {
                mFTPClient.disconnect();
            } catch (Exception e) {
                System.out.println("Error occurred while disconnecting from ftp server." + Toknow);
            }
        }
    }

    public String[] ftpPrintFilesList(String hname, String uname, String pword, String dir_path) {
        String[] fileList = null;

        boolean state = ftpConnect(hname, uname, pword, 21);
        if (state) {
            try {

                mFTPClient.setDataTimeout(45000);
                mFTPClient.setSoTimeout(45000);
                FTPFile[] ftpFiles = mFTPClient.listFiles(dir_path);
                int length = ftpFiles.length;
                fileList = new String[length];
                for (int i = 0; i < length; i++) {
                    name = ftpFiles[i].getName();
                    boolean isFile = ftpFiles[i].isFile();

                    if (isFile) {
                        fileList[i] = name;
                    } else {
                        fileList[i] = name + "/";
                    }
                }
                Toknow = 20;
                System.out.println("Printed File...MyFTPClientFuntction " + Toknow);
                return fileList;

            } catch (Exception e) {
                Toknow = 21;
                System.out.println("Something Went Wrong Please try again...PrintFiles...MyFTPClientFuntction " + Toknow);
                return fileList;
            }
        } else {
            if (Toknow == 10) {
                Toknow = 22;
                System.out.println("FTP Authentication Failed0000...PrintFiles...MyFTPClientFuntction " + Toknow);
                return null;
            } else if (Toknow == 11) {
                Toknow = 23;
                System.out.println("FTP Replay Code is Negetive From Server...PrintFiles...MyFTPClientFuntction " + Toknow);
                return null;
            } else if (Toknow == 12) {
                Toknow = 24;
                System.out.println("FTP Connection Problem or Server down...PrintFiles...MyFTPClientFuntction " + Toknow);
                return null;
            } else if (Toknow == 13) {
                Toknow = 25;
                System.out.println("FTP Server Down or connection Error...PrintFiles...MyFTPClientFuntction " + Toknow);
                return null;
            } else {
                Toknow = 26;
                System.out.println("UNKNOWN ERROR0000...PrintFiles...MyFTPClientFuntction " + Toknow);
                return null;
            }
        }
    }


    public boolean ftpDownload(String srcFilePath, String desFilePath) {
        System.out.println(srcFilePath + "++++++++++++++" + desFilePath);

        try {
            mFTPClient.setDataTimeout(30000);
            mFTPClient.setSoTimeout(15000);
            mFTPClient.setBufferSize(1024 * 1024);
            desFileStream = new FileOutputStream(desFilePath);
            boolean status = mFTPClient.retrieveFile(srcFilePath, desFileStream);
            desFileStream.close();
            Toknow = 30;
            return status;
        } catch (Exception e) {
            Toknow = 31;
            System.out.println(e.toString());
            System.out.println("Something Went Wrong Please try again...Download...MyFTPClientFuntction " + Toknow);
            return false;

        }

    }


    public Long Ffinding(String hname, String uname, String pword, String dir_path, String file) {
        Long fsize = null;
        boolean state = ftpConnect(hname, uname, pword, 21);
        if (state) {
            try {
                mFTPClient.setDataTimeout(45000);
                mFTPClient.setSoTimeout(45000);
                FTPFile[] ftpFiles = mFTPClient.listFiles(dir_path);
                //int length = ftpFiles.length;
                for (FTPFile ftpFile : ftpFiles) {
                    String name = ftpFile.getName();
                    if (file.equals(name)) {
                        fsize = ftpFile.getSize();
                        fdate = String.valueOf(ftpFile);
                        //fdate = fdate.substring(0, 8);
                        System.out.println(fdate);
                        Toknow = 40;
                        System.out.println("MyFTPClientFunction...Finding Size.....File Size==" + fsize);
                        return fsize;
                    }else {
                        Toknow = 46;
                        return null;
                    }
                }
                Toknow = 401;
                System.out.println("MyFTPClientFunction...Finding Size.....File Size==" + null);
                return null;
            } catch (Exception e) {
                Toknow = 41;
                System.out.println("Something Went Wrong Please try again...Finding Size...MyFTPClientFuntction " + Toknow);
                return fsize;
            }
        } else {
            if (Toknow == 10) {
                Toknow = 42;
                System.out.println("FTP Authentication Error...Finding Size...MyFTPClientFuntction ");
                return null;
            } else if (Toknow == 11) {
                Toknow = 43;
                System.out.println("FTP Reply Code is Negetive From Server...Finding Size...MyFTPClientFuntction ");
                return null;
            } else if (Toknow == 12) {
                Toknow = 44;
                System.out.println("FTP Connection Prob or Server Down...Finding Size...MyFTPClientFuntction ");
                return null;
            } else if (Toknow == 13) {
                Toknow = 45;
                System.out.println("FTP Server Down or Connection Problem...Finding Size...MyFTPClientFuntction ");
                return null;
            } else {
                Toknow = 46;
                System.out.println("UNKNOWN ERROR...Finding Size...MyFTPClientFuntction ");
                return null;
            }

        }
    }


    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }

        if (netInfo != null && netInfo.isConnected()) {
            network = netInfo.getTypeName();

            return true;
        }
        return false;
    }


    public String ftpGetCurrentWorkingDirectory() {
        try {
            //ftpConnect(hname, uname, pword, 21);
            //mFTPClient.setConnectTimeout(30000);
            mFTPClient.setDataTimeout(10000);
            mFTPClient.setSoTimeout(1000);
            String workingDir = mFTPClient.printWorkingDirectory();
            working = 1;
            return workingDir;
        } catch (Exception e) {
            System.out.println("Something Went Wrong Please try again....returns false");
            Toknow = 21;
            return null;

        }

    }

}
