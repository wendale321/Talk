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

import android.text.Html;
import android.util.Log;

public class ChongDong {
	private String WebAPI_Path = "http://wap.unidust.cn/api/searchout.do?type=wap&ch=1001&appid=111&info=";
	private HttpClient httpClient = null;
	//private final int MAX_LEN = 100;//返回字符串最大长度
	
	@SuppressWarnings("finally")
	public String getResult(String question) {
		String strResult = "网络不给力呀";
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
			Log.v("ClientProtocolException", e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.v("IOException", e.toString());
		} catch(Exception e){
			Log.v("Exception", e.toString());
		}
		finally {
			return strResult;
		}
	}
	
	public String getAnswer(String question){
		String result=getResult(question);
		if(result.equals("网络不给力呀")) return "网络不给力";
		int firstIndex=result.indexOf("<br/>")+5;
		int lastIndex=result.lastIndexOf("<br/>");
		String answer = Html.fromHtml(result.substring(firstIndex,lastIndex)).toString();
		Log.v("ANSWER", answer);
		try{
		    return pureAnswer(answer);
		}catch(Exception e){
			return answer;
		}
	}
	/*过滤获得的结果，可以无限添加*/
	private String pureAnswer(String answer) throws Exception{
		if(answer.length() > 2 && answer.substring(0,2).equals("抱歉")){
			return "这个没搜到";
		}
		if(answer.length() > 6 && answer.substring(0,6).equals("新闻网页贴吧")){
		     int start = answer.indexOf('1');
		     int end = start + 2;
		     for(;end < answer.length();end++){
		    	 char c = answer.charAt(end);
		    	 if( c =='2' || c == '。' || c == '.') break;
		     }
		     return answer.substring(start+2,end);
		}
		int index = answer.indexOf("已关注");
		if(index != -1){
			int end = answer.indexOf('。');
			return answer.substring(index + 3,end + 1);
		}
		index = answer.indexOf("提问者");
		if(index != -1){
			int start = answer.indexOf("最佳答案");
			int end = answer.indexOf('。');
			return answer.substring(start+4,end+1);
		}
		return answer;
	}
	
}


