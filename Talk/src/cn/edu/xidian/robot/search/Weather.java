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

public class Weather {
	private static final String WebAPI_Path = "http://m.weather.com.cn/data/";
	private static final String TAG = "CITY_WEATHER";
	private static final String XIAN = "101110101";
	private static final String TEMP1 = "\"temp1\":\"";
	private static final String TEMP2 = "\"temp2\":\"";
	private static final String TEMP3 = "\"temp3\":\"";
	private static final String WEATHER1 = "\"weather1\":\"";
	private static final String WEATHER2 = "\"weather2\":\"";
	private static final String WEATHER3 = "\"weather3\":\"";
	@SuppressWarnings("finally")
	public static String getWeather(String cityCode) {
		HttpClient httpClient = null;
		String strResult = "网络不给力呀";
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000);//网络超时10s
		HttpConnectionParams.setSoTimeout(httpParams, 10000);
		httpClient = new DefaultHttpClient(httpParams);
		try {
			String strQuestion = WebAPI_Path + cityCode + ".html";
			HttpGet httpRequest = new HttpGet(strQuestion);
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				String str = EntityUtils.toString(httpResponse.getEntity());
				//strResult = new String(str.getBytes("ISO-8859-1"), "UTF-8");
				strResult = str;
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
	public static String parseData(String content) throws Exception{
		int index1 = content.indexOf(TEMP1);
		StringBuffer sb1 = new StringBuffer();
		for(int i = index1 + TEMP1.length(); ; i++){
			if(content.charAt(i) == '"') break;
			sb1.append(content.charAt(i));
		}
		
		int index2 = content.indexOf(TEMP2);
		StringBuffer sb2 = new StringBuffer();
		for(int i = index2 + TEMP2.length(); ; i++){
			if(content.charAt(i) == '"') break;
			sb2.append(content.charAt(i));
		}
		
		int index3 = content.indexOf(TEMP3);
		StringBuffer sb3 = new StringBuffer();
		for(int i = index3 + TEMP3.length(); ; i++){
			if(content.charAt(i) == '"') break;
			sb3.append(content.charAt(i));
		}
		
		int index4 = content.indexOf(WEATHER1);
		StringBuffer sb4 = new StringBuffer();
		for(int i = index4 + WEATHER1.length(); ; i++){
			if(content.charAt(i) == '"') break;
			sb4.append(content.charAt(i));
		}
		
		int index5 = content.indexOf(WEATHER2);
		StringBuffer sb5 = new StringBuffer();
		for(int i = index5 + WEATHER2.length(); ; i++){
			if(content.charAt(i) == '"') break;
			sb5.append(content.charAt(i));
		}
		
		int index6 = content.indexOf(WEATHER3);
		StringBuffer sb6 = new StringBuffer();
		for(int i = index6 + WEATHER3.length(); ; i++){
			if(content.charAt(i) == '"') break;
			sb6.append(content.charAt(i));
		}
		
		StringBuffer ans = new StringBuffer();
		
		ans.append("今后三天天气情况如下,今天，气温:");
		ans.append(sb1);
		ans.append(',');
		ans.append(sb4);
		ans.append(',');
		
		ans.append("明天，气温:");
		ans.append(sb2);
		ans.append(',');
		ans.append(sb5);
		ans.append(',');
		
		ans.append("后天，气温:");
		ans.append(sb3);
		ans.append(',');
		ans.append(sb6);
		ans.append('.');
		
		return ans.toString();
	}
	
	public static String getWeather(){
		String content = getWeather(XIAN);
		try{
		return parseData(content);
		}catch(Exception ex){
			return "网络不给力，获取天气数据失败\n";
		}
	}
}
