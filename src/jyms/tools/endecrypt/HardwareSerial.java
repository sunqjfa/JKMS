package jyms.tools.endecrypt;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import jyms.data.TxtLogger;

public class HardwareSerial {
        private static final String FILE_NAME = "SoftWareUtils.java";

        /**
            * 函数:      getMotherboardSN
            * 函数描述:  获取主板序列号（该函数的需要运行时间将近0秒）
            * @return String 主板序列号。失败返回""
        */
	public static String getMotherboardSN() {
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
//                        if (result.trim().length() < 1) {
//                            result = "无主板序列号被读取";
//                        }
                        return result.trim();
		} catch (Exception e) {
			TxtLogger.append(FILE_NAME, "getMotherboardSN()","系统在获取主板序列号的过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString()); 
		}
		return "";//"无主板序列号被读取";
	}

        /**
            * 函数:      getHardDiskSN
            * 函数描述:  获取硬盘序列号（一般不用该函数，原因如下）
            *             磁盘序列号，简称磁盘ID，是对磁盘进行格式化时随机产生的磁盘标识信息，是一个卷序列号。
            *             同一机器两次格式化随机产生固定格式的序列号相同几率几乎为零，即如果重新分区，磁盘序列号将会随之改变。
            * @param drive String盘符
            * @return String 硬盘序列号。
        */
	public static String getHardDiskSN(String drive) {
		String result = "";
		try {
			File file = File.createTempFile("realhowto", ".vbs");
			file.deleteOnExit();
			FileWriter fw = new java.io.FileWriter(file);

			String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
					+ "Set colDrives = objFSO.Drives\n"
					+ "Set objDrive = colDrives.item(\""
					+ drive
					+ "\")\n"
					+ "Wscript.Echo objDrive.SerialNumber"; // see note
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
//                        if (result.trim().length() < 1) {
//                            result = "无硬盘序列号被读取";
//                        }
                        return result.trim();
		} catch (Exception e) {
			TxtLogger.append(FILE_NAME, "getHardDiskSN()","系统在获取硬盘序列号的过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());   
		}
		return "";//"无硬盘序列号被读取";
	}

	/**
            * 函数:      getCPUSerial2
            * 函数描述:  获取CPU序列号（为getCPUSerial的备用函数，因为该函数的需要运行时间将近1秒）
            * @return String CPU序列号。失败返回""
        */
	public static String getCPUSerial2() {
		String result = "";
		try {
			File file = File.createTempFile("tmp", ".vbs");
			file.deleteOnExit();
			FileWriter fw = new java.io.FileWriter(file);
			String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
					+ "Set colItems = objWMIService.ExecQuery _ \n"
					+ "   (\"Select * from Win32_Processor\") \n"
					+ "For Each objItem in colItems \n"
					+ "    Wscript.Echo objItem.ProcessorId \n"
					+ "    exit for  ' do the first cpu only! \n" + "Next \n";

			// + "    exit for  \r\n" + "Next";
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
			file.delete();
//                        if (result.trim().length() < 1) {
//                                result = "无CPU序列号被读取";
//                        }
                        return result.trim();
		} catch (Exception e) {
			TxtLogger.append(FILE_NAME, "getCPUSerial2()","系统在获取CPU序列号的过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());   
		}
		return "";//"无CPU序列号被读取";
	}
        

        /**
            * 函数:      getCPUSerial
            * 函数描述:  获取CPU序列号（该函数的需要运行时间将近0秒）
            * @return String CPU序列号。失败返回""
        */
        public static String getCPUSerial() {
                try {
                    Process process = Runtime.getRuntime().exec(
                            new String[] { "wmic", "cpu", "get", "ProcessorId" });
                    process.getOutputStream().close();
                    @SuppressWarnings("resource")
                            Scanner sc = new Scanner(process.getInputStream());
                    sc.next();
                    String serial = sc.next();
//                    if (serial.trim().length() < 1) {
//                        serial = "无CPU序列号被读取";
//                    }
                    return serial;
                } catch (IOException e) {
                    TxtLogger.append(FILE_NAME, "getCPUSerial()","系统在获取CPU序列号的过程中，出现错误"
                             + "\r\n                       Exception:" + e.toString());   
                }
                return "";//"无CPU序列号被读取";
        }
        
        public static String generateMachineCode(String HardwareSerial){
            return MD5Encrypt.getMD5Str(HardwareSerial);
            //return MD5Encrypt.getMD5Str(getMotherboardSN()+getCPUSerial());
        }

	public static void main(String[] args) {
//		System.out.println("CPU  SN:"+HardwareSerial.getCPUSerial());
//		System.out.println("主板   SN:"+HardwareSerial.getMotherboardSN());
//		System.out.println("C盘     SN:"+HardwareSerial.getHardDiskSN("c"));
                String ss = getMotherboardSN()+";"+getCPUSerial();
                System.out.println(ss);
                System.out.println(MD5Encrypt.getMD5Str(ss));
	}

}
