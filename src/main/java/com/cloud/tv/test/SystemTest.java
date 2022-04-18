package com.cloud.tv.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

/**
 * 系统属性
 */
public class SystemTest {

    Logger log = LoggerFactory.getLogger(System.class);

    public static void main(String[] args) {
        Properties sysProperty = java.lang.System.getProperties(); //系统属性
        Set<Object> keySet = sysProperty.keySet();
        for (Object object : keySet) {
            String property = sysProperty.getProperty(object.toString());
            java.lang.System.out.println(object.toString()+" : "+property);
        }
    }

    @Test
    public void systemInfo(){
        try {
//            Process process =Runtime.getRuntime().exec(new  String[]{"wmic","diskdrive","get","serialnumber"});
//            Process process = Runtime.getRuntime().exec(
//                    new String[]{"wmic", "path", "win32_computersystemproduct", "get", "uuid"});
            Process process =Runtime.getRuntime().exec(new  String[]{"wmic","bios","get","serialnumber"});
            process.getOutputStream().close();
            Scanner sc=new Scanner(process.getInputStream());
            String property =sc.next();
            String serial=sc.next();
            System.out.println("======:"+ serial);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMainBordId_windows() {
        String result = "";
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n"
                    + "   (\"Select * from Win32_BaseBoard\") \n"
                    + "For Each objItem in colItems \n"
                    + "    Wscript.Echo objItem.SerialNumber \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";

            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec(
                    "cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {
            log.error("获取主板信息错误",e);
        }
        return result.trim();
    }


}
