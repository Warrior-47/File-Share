package file.share;

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws IOException {
        File f = new File("H:\\Uni Work\\10th Sem\\CSE341\\Lab.zip");
        FileInputStream fs = null;
        BufferedInputStream finput = null;
        ServerSocket ss = null;
        Socket s = null;
        DataOutputStream temp = null;
        OutputStream sendData = null;
        if(!f.isDirectory()) {
            try {
                fs = new FileInputStream(f);
                finput = new BufferedInputStream(fs);
                ss = new ServerSocket(1830);
                System.out.println("Connecting...");
                s = ss.accept();
                System.out.println("Connection Established.\nSending File: "+f.getName()+"\nFile Size: "+f.length());
                temp = new DataOutputStream(s.getOutputStream());
                temp.writeUTF(" ");
                temp.writeInt(1);
                temp.writeUTF(f.getName());
                temp.writeLong(f.length());
                temp.flush();
                sendData = s.getOutputStream();
                byte[] file = new byte[32768];
                long dataLeft = f.length();
                int fileSize = finput.read(file, 0, (int)(((long)file.length<dataLeft) ? file.length : dataLeft));
                long totalSize = 0;
                System.out.println("Uploading...");
                while(dataLeft!=0) {
                    totalSize += fileSize;
                    sendData.write(file);
                    dataLeft -= fileSize;
                    fileSize = finput.read(file, 0, (int)(((long)file.length<dataLeft) ? file.length : dataLeft));
                    sendData.flush();
                }
                System.out.println("Total Sent: "+totalSize);
            }catch(SocketException e) {
                System.err.println("Failed to Send. Check Internet Connection.");
            }catch(Exception e) {
                e.printStackTrace();
            }finally {
                sendData.close();
                finput.close();
                temp.close();
                fs.close();
                s.close();
            }
        }else {
            try {
                File[] dir = f.listFiles();
                long dirSize = dirSize(dir);
                int noOfFiles = dir.length;
                ss = new ServerSocket(1830);
                System.out.println("Connecting...");
                s = ss.accept();
                System.out.println("Connection Established.\nSending Folder: "+f.getName()+"\nFolder Size: "+dirSize);
                temp = new DataOutputStream(s.getOutputStream());
                temp.writeUTF(f.getName());
                temp.writeInt(noOfFiles);
                for(int i=0;i<noOfFiles;i++) {
                    temp.writeUTF(dir[i].getName());
                    temp.writeLong(dir[i].length());
                }
                temp.flush();
                long totalSize = 0;
                sendData = s.getOutputStream();
                for(int i=0;i<noOfFiles;i++) {
                    File fi = dir[i];
                    fs = new FileInputStream(fi);
                    long dataLeft = fi.length();
                    System.out.println("Uploading File: "+fi.getName()+"\tSize: "+dataLeft);
                    byte[] file = new byte[32768];
                    int fileSize = fs.read(file, 0, (int)((file.length<dataLeft) ? file.length : dataLeft));
                    while(fileSize!=-1 && dataLeft!=0) {
                        totalSize += fileSize;
                        sendData.write(file);
                        dataLeft -= fileSize;
                        fileSize = fs.read(file, 0, (int)((file.length<dataLeft) ? file.length : dataLeft));
                    }
                    System.out.println("File "+(i+1)+" Uploaded.");
                    sendData.flush();
                    fs.close();
                }
                System.out.println("Total Sent: "+totalSize);
            }catch(SocketException e) {
                System.err.println("Failed to Send. Check Internet Connection.");
            }catch(Exception e) {
                System.out.println(e);
            }finally {
                sendData.close();
                temp.close();
                s.close();
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

