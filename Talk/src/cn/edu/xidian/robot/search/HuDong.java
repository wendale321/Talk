package cn.edu.xidian.robot.search;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HuDong {
	private String TAG = "ChongDong";
	//private String WebAPI_Path = "http://wap.unidust.cn/api/searchout.do?type=wap&ch=1001&appid=111&info=";
	private String WebAPI_Path = "http://www.baike.com/wiki/";
	private HttpClient httpClient = null;
	private final int MAX_LEN = 200;//返回字符串最大长度
//	private final static String commonAnswer = "答案在千里之外，恐一时难以将其擒获";
	
	private int cnt;
	@SuppressWarnings("finally")
	public String getResult(String question) {
		String strResult = null;
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000);//网络超时10s
		HttpConnectionParams.setSoTimeout(httpParams, 10000);
		httpClient = new DefaultHttpClient(httpParams);
		try {
			String strQuestion = WebAPI_Path + question;
			HttpGet httpRequest = new HttpGet(strQuestion);
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				String str = EntityUtils.toString(httpResponse.getEntity());
				strResult = new String(str.getBytes("ISO-8859-1"), "UTF-8");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.v(TAG, e.toString() + "@ClientProtocolException");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.v(TAG, e.toString() + "@IOException");
		} catch(Exception e){
			Log.v(TAG, e.toString() + "@Exception");
		}
		finally {
			return strResult;
		}
	}

	
	public String getAnswer(String question){
		String result=getResult(question);
		cnt = 0;
		return extractString(result);
	}
	private String extractString(String result){
		cnt++;
		if(cnt > 5) //return commonAnswer;
			return null;
		Log.v("STR", result);
		String flag = "<meta content=";
		StringBuffer answer = new StringBuffer();
		int firstIndex=result.indexOf(flag) + flag.length() + 1;
		if(firstIndex < flag.length() + 1){
			//return commonAnswer;
			return null;
		}
		char c;
		int len = 0;
		while((c = result.charAt(firstIndex + len)) != '>' && len <= MAX_LEN){
			len++;
			answer.append(c);
		}
		int endIndex = answer.indexOf("name=");
		if(endIndex >= answer.length()){
			return answer.toString();
		}
		if(endIndex == -1){
			return extractString(result.substring(firstIndex + flag.length()));
		}
		return answer.substring(0,endIndex);
	}
   }
	




