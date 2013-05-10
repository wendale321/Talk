package cn.edu.xidian.robot.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.os.Environment;
import android.util.Log;
import cn.edu.xidian.robot.answer.AnswerAll;
import cn.edu.xidian.robot.answer.AnswerInfo;
import cn.edu.xidian.robot.answer.AnswerManager;
import cn.edu.xidian.robot.answer.Search;
import cn.edu.xidian.robot.string.StringProcesser;

public class FileManager {
	   private  boolean hasRead = false;
	   private  boolean readState ;
       private static FileManager instance=new FileManager();
       private final static String LOCAL_FILE = Environment.getExternalStorageDirectory().
    		                                    getAbsolutePath() + "/Talk/";
       private FileManager(){};
       private static AnswerManager answerManager = AnswerManager.getInstance();
       
       private static final int MAX_BUF = 1000000;
       
       private static int index = 0;
       
       public static FileManager getInstance(){
    	      return instance;
       }
       public  boolean initData(){
    	      if(! hasRead)  {
    	    	  readState=readFile() ;
    	    	  hasRead = true;
    	    	  //对搜索索引表的初始化
    	    	  Search.initIndexTable();
    	      }
    	      return readState;
       }
       private  boolean readFile(){
    	   try{
    	      File fdir = new File(LOCAL_FILE);
    	      if(! fdir.exists()){
    	    	  fdir.mkdirs();
    	      }
    	      String defaultUserDir = LOCAL_FILE+"default.xml";
    	      File defaultUserFile = new File(defaultUserDir);
    	      if( ! defaultUserFile.exists()){
    	    	  defaultUserFile.createNewFile();
    	    	  try{
    	    		   FileOutputStream fos = new FileOutputStream(defaultUserDir);
    	    		   fos.write("<root>\n</root>".getBytes());
    	    		   fos.close();
    	    	  }catch(Exception e){
    	    		  
    	    	  }finally{
    	    		  
    	    	  }
    	      }
    	      String[] files = fdir.list();
    	    	  for(String file : files){
    	    		  //读取每个用户的信息，添加到应答管理中
    	    		  if(file.length() < 4) continue;
    	    		  if(!file.substring(file.length()-4, file.length()).equals(".xml")) continue;
    	    		  File fdata = new File(LOCAL_FILE + file);
    	    		  try{
    	    		      AnswerAll answerAll = new AnswerAll(StringProcesser.getOffSuffixe(file),parseFile(fdata));
    	    		      answerManager.addAnswerAll(answerAll);
    	    		      //仅供测试用
    	    		      //test(answerAll);
    	    		  }catch(Exception ex){
    	    			  Log.v("FILE", ex.toString());
    	    			  return false;
    	    		  }
    	    	  }
    	   }catch(Exception ex){
    		   return false;
    	   }
    	      return true;
       }
       public  boolean writeIn(String userName,String ask,String answer){
    	      File toWriteIn= new File(LOCAL_FILE + userName + ".xml");
    	      if(! toWriteIn.exists()) return false;
    	      try{
    	    	  FileInputStream fis = new FileInputStream(toWriteIn);
    	    	  byte buf[] = new byte[MAX_BUF];
    	    	  int len = fis.read(buf);
    	    	  String writeIn = new String(buf,0,len);
    	    	  writeIn = writeIn.substring(0, writeIn.length()-7);
    	    	  FileOutputStream fos = new FileOutputStream(toWriteIn);
    	          writeIn += ("\n  <talk>\n     <ask name=\"" + ask + "\"/>\n"); 
    	          writeIn += ("     <answer name=\"" + answer + "\"/>\n  </talk>\n</root>");
    	          fos.write(writeIn.getBytes());
    	          fos.close();
    	          fis.close();
    	      }catch(Exception ex){
    	    	  return false;
    	      }
    	      AnswerInfo answerInfo = new AnswerInfo();
    	      answerInfo.setAskStr(ask);
    	      answerInfo.addToAnswer(answer);
    	      answerManager.addByUserName(userName, answerInfo);
    	      return true;
       }
       private ArrayList<AnswerInfo> parseFile(File file) throws Exception{
    	       MySax ms = new MySax();
    	       
    	   	   SAXParserFactory factory = SAXParserFactory.newInstance() ;
    		   SAXParser parser = factory.newSAXParser() ;
    		   parser.parse(file,ms) ;
    		   
    	       return ms.getAnswerInfoArray();
       }
       
       public static void recordQuestion(String ask){
    	   	File recordFile = new File(LOCAL_FILE+"record.txt");
    	   	if(!recordFile.exists()) {
    	   		try {
					recordFile.createNewFile();
				} catch (IOException e) {
				}
    	   	}
    	   	try {
				FileOutputStream fos = new FileOutputStream(recordFile, true);
				fos.write((index + ":" + ask + "\n").getBytes());
				index++;
				fos.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       }
    	 
}
