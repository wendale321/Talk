package cn.edu.xidian.robot.serialport;

import android.util.Log;

import com.friendlyarm.AndroidSDK.HardwareControler;

public class SerialPortManager {
       public static final String SERIAL_1="/dev/s3c2410_serial1";
       public static final String SERIAL_2="/dev/s3c2410_serial2";
       public static final String SERIAL_3="/dev/s3c2410_serial3";
       
       private int fileDescriptor = -1;
       public int open(String portName){
    	      //������:9600   ����λ:8 ֹͣλ:1
    	      return fileDescriptor = HardwareControler.openSerialPort(portName, 9600, 8, 1);
       }
       
       public int write(String command){
              int re = -1;
              try{
    	            re = HardwareControler.write(fileDescriptor, command.getBytes("UTF-8"));
              }catch(Exception e){
            	    Log.v("SEND", "��ʽ��֧��");
              }
              return re;
       }
       
       public int getFD(){
    	   return this.fileDescriptor;
       }
       
}
