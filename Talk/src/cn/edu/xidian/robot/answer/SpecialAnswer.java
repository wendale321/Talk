package cn.edu.xidian.robot.answer;

import android.content.Context;
import cn.edu.xidian.robot.search.Weather;
import cn.edu.xidian.robot.serialport.ConnectHardware;
import cn.edu.xidian.robot.string.StringProcesser;

public class SpecialAnswer {
	private static final String WEATHER = "����";
	private static final String HELLO = "���";
	private static final String ANSWER_HELLO = " ";
	private static final String PLUS = "��";
	private static final String MINUS = "��";
	private static final String MULTIPLY = "��";
	private static final String DEVIDE = "��";
	
	//���⹦�ܴ�����ƥ�䵽���⹦�ܷ��ط�nullֵ
	public String preprocess(String ask){
		//1�����ּ�����
		if(StringProcesser.match(ask, HELLO) >= 0.6){
			ConnectHardware.getInstance().spm2.write("duojib");
			ConnectHardware.getInstance().spm2.write("cgqK");

			return ANSWER_HELLO;
		}
		
		//2��������
		String calResult = doCal(ask); 
		if(calResult != null){
			return calResult;
		}
		
		//3��������
		if(StringProcesser.match(ask, WEATHER) >= 0.6){
			return Weather.getWeather();
		}
		return null;
	}
	
	//����Ӽ��˳�����
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
				//��
				if(ask.indexOf(PLUS) > 0){
					return "�����������ӷ������𣬵��������û��ȫ�����������˵һ�顣";
				}
				//��
				if(ask.indexOf(MINUS) > 0){
					return "�������������������𣬵��������û��ȫ�����������˵һ�顣";
				}
				//��
				if(ask.indexOf(MULTIPLY) > 0){
					return "�����������˷������𣬵��������û��ȫ�����������˵һ�顣";
				}
				//��
				if(ask.indexOf(DEVIDE) > 0){
					return "�������������������𣬵��������û��ȫ�����������˵һ�顣";
				}
				return null;
			}
		
		try{
			int num1 = Integer.decode(sbBuffer[0].toString()).intValue();
			int num2 = Integer.decode(sbBuffer[1].toString()).intValue();
			
			//��
			if(ask.indexOf(PLUS) > 0){
				return "�����" + String.valueOf(num1 + num2);
			}
			//��
			if(ask.indexOf(MINUS) > 0){
				return "�����" + String.valueOf(num1 - num2);
			}
			//��
			if(ask.indexOf(MULTIPLY) > 0){
				return "�����" + String.valueOf(num1 * num2);
			}
			//��
			if(ask.indexOf(DEVIDE) > 0){
				if(0 == num2){
					return "�0������������ѽ��";
				}
				return "�����" + String.valueOf(num1 / num2);
			}
			
		}catch(Exception e){
			return null;
		}
		return null;
	}
}
