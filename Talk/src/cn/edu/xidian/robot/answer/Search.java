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
	  private final static String WORD_NOT_FIND = "我还不明白这是什么意思";
	  private final static String LOCAL_FILE = Environment.getExternalStorageDirectory().
                                               getAbsolutePath() + "/Talk/";
	  private static String[] indexTable = null ;
	  /*把索引表读进内存
	   *最好在程序初始化完成，因为这可能要用掉很长时间
	   *初始化失败返回false
	   *在FileManager.initData()调用
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
      //通过关键词搜索百度百科解释
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
     *根据所给字符串找到相应content
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
