package file.share;

import java.io.*;
import java.net.*;

public class Client {
    
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    
    public static void main(String[] args)  {
        try {
            Socket s = new Socket("192.168.1.103",1830);
            System.out.println("Connection Established.");
            byte[] file = new byte[65536];
            DataInputStream temp = new DataInputStream(s.getInputStream());
            String dirName = (String)temp.readUTF();
            String path = "downloaded files\\";
            if(!dirName.equals(" ")) {
                path += dirName;
                System.out.println("path created: "+new File(path).mkdir());
                path += "\\";
            }
            int noOfFiles = temp.readInt();
            File[] objects = new File[noOfFiles];
            long[] fileSize = new long[noOfFiles];
            for(int i=0;i<noOfFiles;i++) {
                String fileName = temp.readUTF();
                objects[i] = new File(path+fileName);
                fileSize[i] = temp.readLong();
            }
            long dirSize = 0;
            InputStream receive = s.getInputStream();
            for(int i=0;i<noOfFiles;i++) {
                System.out.println("Downloading File: "+objects[i].getName()+"\nFile Size: "+fileSize[i]+" Bytes");
                FileOutputStream fos = new FileOutputStream(objects[i]);
                long dataLeft = fileSize[i];
                long totalData = 0;
                int dataRead = 0;
                long start = System.currentTimeMillis();
                boolean failed = false;
                while((dataRead=receive.read(file, 0, Math.min(file.length,(int)dataLeft)))==0){
                    long end = System.currentTimeMillis();
                    if((end-start)>=60000) {
                        failed = true;
                        break;
                    }
                }
                System.out.println("Downloading...");
                boolean t5, f0, s5;
                t5 = f0 = s5 = true;
                while(dataRead!=-1 && dataLeft!=0) {
                    fos.write(file,0,dataRead);
                    dataLeft -= dataRead;
                    totalData += dataRead;
                    double pern = (double)totalData/fileSize[i]*100;
                    if(t5 && pern>=25.0 && pern<50.0) {
                        System.out.printf("%s%.1f%% Downloaded.%s\n",ANSI_GREEN,pern,ANSI_RESET);
                        t5 = false;
                    }else if(f0 && pern >=50 && pern<75.0) {
                        f0 = false;
                        System.out.printf("%s%.1f%% Downloaded.%s\n",ANSI_GREEN,pern,ANSI_RESET);
                    }else if(s5 && pern>=75 && pern<100.0) {
                        s5 = false;
                        System.out.printf("%s%.1f%% Downloaded.%s\n",ANSI_GREEN,pern,ANSI_RESET);
                    }else if(pern==100){
                        System.out.printf("%s%.1f%% Downloaded.%s\n",ANSI_GREEN,pern,ANSI_RESET);
                    }
                    if(totalData==fileSize[i])
                        break;
                    start = System.currentTimeMillis();
                    while((dataRead=receive.read(file, 0, min(file.length,dataLeft)))==0){
                        long end = System.currentTimeMillis();
                        if((end-start)>=60000) {
                            failed = true;
                            break;
                        }
                    }
                }
                fos.close();
                if(failed) {
                    objects[i].delete();
                    System.err.println("Failed to Download "+objects[i].getName()+". Check Internet Connection.\nTotal Received: "+totalData);
                }else {
                    System.out.println(ANSI_GREEN+"Download Completed."+ANSI_RESET);
                }
                dirSize += totalData;
            }
            System.out.println("Total File Size: "+dirSize);
            receive.close();
            temp.close();
            s.close();
            
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        
    }
    
    public static void printa(byte[] f, long r) {
        for(int i=0;i<r;i++) {
            System.out.println(f[i]);
        }
    }
    
    public static int min(int x, long y) {
        if(x<y)
            return x;
        return (int)y;
    }
}