package file.share;

import java.io.*;
import java.net.*;

public class Client {
    
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    
    public static void main(String[] args)  {
        try {
            Socket s = new Socket("localhost",1830);
            System.out.println("Connection Established.");
            byte[] file = new byte[8192];
            BufferedReader temp = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String dirName = temp.readLine();
            String path = "downloaded files\\";
            if(!dirName.equals(" ")) {
                path += dirName;
                System.out.println("path created: "+new File(path).mkdir());
                path += "\\";
            }
            int noOfFiles = Integer.parseInt(temp.readLine());
            long dirSize = 0;
            InputStream receive = s.getInputStream();
            for(int i=0;i<noOfFiles;i++) {
                String fileName = temp.readLine();
                String filePath = path + fileName;
                long fileSize = Long.parseLong(temp.readLine());
                System.out.println("Downloading File: "+fileName+"\nFile Size: "+fileSize+" Bytes");
                FileOutputStream fos = new FileOutputStream(filePath);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                long dataLeft = fileSize;
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
                while(dataRead!=0) {
                    bos.write(file,0,dataRead);
                    bos.flush();
                    dataLeft -= dataRead;
                    totalData += dataRead;
                    double pern = (double)totalData/fileSize*100;
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
                    if(totalData==fileSize)
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
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                dos.writeBoolean(true);
                dos.close();
                bos.close();
                fos.close();
                if(failed) {
                    File f = new File(filePath);
                    f.delete();
                    System.err.println("Failed to Download "+f.getName()+". Check Internet Connection.\nTotal Received: "+totalData);
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
    
    public static int min(int x, long y) {
        if(x<y)
            return x;
        return (int)y;
    }
}