package cn.edu.xidian.robot.answer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Environment;
import android.util.Log;
import cn.edu.xidian.robot.string.StringProcesser;

public class Search {
	  private final static String WORD_NOT_FIND = "�һ�����������ʲô��˼";
	  private final static String LOCAL_FILE = Environment.getExternalStorageDirectory().
                                               getAbsolutePath() + "/Talk/";
	  private static String[] indexTable = null ;
	  /*������������ڴ�
	   *����ڳ����ʼ����ɣ���Ϊ�����Ҫ�õ��ܳ�ʱ��
	   *��ʼ��ʧ�ܷ���false
	   *��FileManager.initData()����
	   **/
      public static boolean initIndexTable(){
    	    File file = new File(LOCAL_FILE + "index.txt");
    	    Reader in = null;
    	    try{
    	    	 in = new FileReader(file);
    	    	 StringBuffer sb = new StringBuffer();
    	    	 int c =0;
    	    	 while((c = in.read()) != -1){
    	    		 sb.append((char)c);
    	    	 }
    	    	 indexTable = sb.toString().split("/n");
    	    }catch(Exception e){
    	    	Log.v("InitIndexTable", e.toString());
    	    	return false;
    	    }finally{
    	    	try{
    	    	in.close();
    	    	}catch(Exception e){
    	    		
    	    	}
    	    }
    	    //test();
    	    return true;
      }
      //ͨ���ؼ��������ٶȰٿƽ���
      public String getContentByKeys(String keys){
   	   String url = "http://baike.baidu.com/view/" + (findApproximate(keys) +1 ) +".htm";
   	   String re = getURLContent(url);
   	   //return re.substring(0, 300);
       //return getContent(re);
   	   return re;
     }
      private int findApproximate(String keys){
    	  int index = 0;
    	  double match = 0.0;
    	  for(int i = 1;i<indexTable.length;i++){
	    	  double tmp = StringProcesser.match(indexTable[i],keys);
	    	  if(match < tmp){
	    		  match = tmp;
	    		  index = i;
	    	  }
    	 }
	     return index;
      }
      private String getURLContent(String u) {
  	    try {
  	        URL url = new URL(u); // URL setting
  	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
  	        conn.setConnectTimeout(5*1000); // timeout setting
  	        // the first line of body
  	        BufferedReader reader = new BufferedReader(new InputStreamReader(
  	                conn.getInputStream()));
  	        return reader.readLine();
  	    } catch (MalformedURLException e) {
  	        //return WORD_NOT_FIND ;
  	    	return e.toString();
  	    } catch (IOException e) {
  	    	//return WORD_NOT_FIND ;
  	    	return e.toString();
  	  }
    }
    /*
     *���������ַ����ҵ���Ӧcontent
     **/
    private String getContent(String str){
    	int index = str.indexOf("</title>");
    	if(index == -1) return WORD_NOT_FIND ;
    	int start =0,end=0;
    	for(start = index;start<str.length();start++){
    		if(str.charAt(start) == '\"') break;
    	}
    	start ++;
    	for(end = start; end <str.length();end++){
    		if(str.charAt(end) == '\"') break;
    	}
    	return str.substring(start, end);
    }
}
