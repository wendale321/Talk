package cn.edu.xidian.robot.serialport;

import java.io.UnsupportedEncodingException;

import com.friendlyarm.AndroidSDK.HardwareControler;

import cn.edu.xidian.robot.serialport.SerialPortManager;

public class ConnectHardware {
    /*串口2，用于舵机*/
    public SerialPortManager spm2 = new SerialPortManager();
    public SerialPortManager spm3 = new SerialPortManager();
    private static ConnectHardware instance;
    /*机器人状态管理*/
    public static final int BEGIN_SPEAK_STATE = 0;
    public static final int ON_SPEAK_STATE = 1;
    public static final int END_SPEAK_STATE = 2;
    public static final int BEGIN_RECOGNIZE_STATE = 3;
    public static final int ON_RECOGNIZE_STATE = 4;
    public static final int END_RECOGNIZE_STATE = 5;
    public static final int BEGIN_THINK_STATE = 6;
    public static final int ON_THINK_STATE = 7;
    public static final int END_THINK_STATE = 8;
    private int courrentState = 0;
    public String info = "";
    private String command[] = {"m","y","m","m","s","m","m","c","m"};
    private ConnectHardware(){
	      if(spm2.open(SerialPortManager.SERIAL_2) == -1) info += "\n串口2打开失败。";
	      if(spm3.open(SerialPortManager.SERIAL_3) == -1) info += "\n串口3打开失败。";
    }
    
    public static ConnectHardware getInstance(){
    	if(instance == null) return instance = new ConnectHardware();
    	return instance;
    }
    public void setState(int state){
    	courrentState = state;
    	spm3.write(new String(command[state]));
    }
    public int getState(){
    	return this.courrentState;
    }
    
    public String detectSPM2(){
    	byte buffer[] = new byte[256];
    	int len = HardwareControler.read(spm2.getFD(), buffer, 256);
    	if(len <= 0) return null;
    	try {
			return new String(buffer, 0, len, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return null;
		}
    }
    
    public String detectSPM3(){
    	byte buffer[] = new byte[256];
    	int len = HardwareControler.read(spm3.getFD(), buffer, 256);
    	if(len <= 0) return null;
    	try {
			return new String(buffer, 0, len, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return null;
		}
    }
}

