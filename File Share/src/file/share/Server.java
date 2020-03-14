package file.share;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        File f = new File("G:\\Pukimans.zip");
        if(!f.isDirectory()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                BufferedInputStream finput = new BufferedInputStream(fs);
                ServerSocket ss = new ServerSocket(1830);
                System.out.println("Connecting...");
                Socket s = ss.accept();
                System.out.println("Connection Established.\nSending File: "+f.getName()+"\nFile Size: "+f.length());
                PrintWriter temp = new PrintWriter(s.getOutputStream(),true);
                temp.println(" ");
                temp.println(1);
                temp.println(f.getName());
                temp.println(f.length());
                OutputStream sendData = s.getOutputStream();
                byte[] file = new byte[8192];
                long dataLeft = f.length();
                int fileSize = finput.read(file, 0, Math.min(file.length,(int)dataLeft));
                long totalSize = 0;
                while(dataLeft!=0) {
                    totalSize += fileSize;
                    sendData.write(file);
                    sendData.flush();
                    dataLeft -= fileSize;
                    fileSize = finput.read(file, 0, Math.min(file.length,(int)dataLeft));
                }
                System.out.println("Total Sent: "+totalSize);
                finput.close();
                fs.close();
                s.close();
            }catch(SocketException e) {
                System.err.println("Failed to Send. Check Internet Connection.");
            }catch(Exception e) {
                e.printStackTrace();
            }
        }else {
            try {
                File[] dir = f.listFiles();
                long dirSize = dirSize(dir);
                int noOfFiles = dir.length;
                ServerSocket ss = new ServerSocket(1830);
                System.out.println("Connecting...");
                Socket s = ss.accept();
                System.out.println("Connection Established.\nSending Folder: "+f.getName()+"\nFolder Size: "+dirSize);
                PrintWriter temp = new PrintWriter(s.getOutputStream(),true);
                temp.println(f.getName());
                temp.println(noOfFiles);
                OutputStream sendData = s.getOutputStream();
                long totalSize = 0;
                for(int i=0;i<dir.length;i++) {
                    File fi = dir[i];
                    FileInputStream fs = new FileInputStream(fi);
                    BufferedInputStream finput = new BufferedInputStream(fs);
                    temp.println(fi.getName());
                    temp.println(fi.length());
                    System.out.println("Uploading File: "+fi.getName()+"\tSize: "+fi.length());
                    byte[] file = new byte[8192];
                    int fileSize = finput.read(file, 0, file.length);
                    while(fileSize!=-1) {
                        totalSize += fileSize;
                        sendData.write(file);
                        fileSize = finput.read(file, 0, file.length);
                    }
                    sendData.flush();
                    finput.close();
                    fs.close();
                }
                System.out.println("Total Sent: "+totalSize);
            }catch(SocketException e) {
                System.err.println("Failed to Send. Check Internet Connection.");
            }catch(Exception e) {
                System.out.println(e);
            }
        }
    }
    
    public static long dirSize(File[] dir) {
        long tot = 0;
        for(int i=0;i<dir.length;i++) {
            tot += dir[i].length();
        }
        return tot;
    }
} 

