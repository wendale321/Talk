package cn.edu.xidian.robot.answer;

import android.content.Context;
import cn.edu.xidian.robot.search.Weather;
import cn.edu.xidian.robot.serialport.ConnectHardware;
import cn.edu.xidian.robot.string.StringProcesser;

public class SpecialAnswer {
	private static final String WEATHER = "天气";
	private static final String HELLO = "你好";
	private static final String ANSWER_HELLO = " ";
	private static final String PLUS = "加";
	private static final String MINUS = "减";
	private static final String MULTIPLY = "乘";
	private static final String DEVIDE = "除";
	
	//特殊功能处理，当匹配到特殊功能返回非null值
	public String preprocess(String ask){
		//1、握手监测过程
		if(StringProcesser.match(ask, HELLO) >= 0.6){
			ConnectHardware.getInstance().spm2.write("duojib");
			ConnectHardware.getInstance().spm2.write("cgqK");

			return ANSWER_HELLO;
		}
		
		//2、做运算
		String calResult = doCal(ask); 
		if(calResult != null){
			return calResult;
		}
		
		//3、查天气
		if(StringProcesser.match(ask, WEATHER) >= 0.6){
			return Weather.getWeather();
		}
		return null;
	}
	
	//处理加减乘除运算
	private String doCal(String ask){
		StringBuffer[] sbBuffer = new StringBuffer[2];
		sbBuffer[0] = new StringBuffer();
		sbBuffer[1] = new StringBuffer();
		int index = 0;
		
		for(int i = 0; i < ask.length(); i++){
			char c = ask.charAt(i);
			if(c >= '0' && c <= '9'){
				while(i < ask.length()){
					c = ask.charAt(i);
					if(c >= '0' && c <= '9') {
						sbBuffer[index].append(c);
					}
					else{
						break;
					}
					i++;
				}
				index++;
				if(index >= 2) break;
			}
		}
		
			if(index < 2) 
			{
				//加
				if(ask.indexOf(PLUS) > 0){
					return "你是让我做加法运算吗，但是我真的没完全听清楚，请再说一遍。";
				}
				//减
				if(ask.indexOf(MINUS) > 0){
					return "你是让我做减法运算吗，但是我真的没完全听清楚，请再说一遍。";
				}
				//乘
				if(ask.indexOf(MULTIPLY) > 0){
					return "你是让我做乘法运算吗，但是我真的没完全听清楚，请再说一遍。";
				}
				//除
				if(ask.indexOf(DEVIDE) > 0){
					return "你是让我做除法运算吗，但是我真的没完全听清楚，请再说一遍。";
				}
				return null;
			}
		
		try{
			int num1 = Integer.decode(sbBuffer[0].toString()).intValue();
			int num2 = Integer.decode(sbBuffer[1].toString()).intValue();
			
			//加
			if(ask.indexOf(PLUS) > 0){
				return "结果是" + String.valueOf(num1 + num2);
			}
			//减
			if(ask.indexOf(MINUS) > 0){
				return "结果是" + String.valueOf(num1 - num2);
			}
			//乘
			if(ask.indexOf(MULTIPLY) > 0){
				return "结果是" + String.valueOf(num1 * num2);
			}
			//除
			if(ask.indexOf(DEVIDE) > 0){
				if(0 == num2){
					return "额，0不能做被除数呀。";
				}
				return "结果是" + String.valueOf(num1 / num2);
			}
			
		}catch(Exception e){
			return null;
		}
		return null;
	}
}
