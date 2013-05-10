package cn.edu.xidian.robot.answer;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.util.Log;
import cn.edu.xidian.robot.data.FileManager;
import cn.edu.xidian.robot.search.HuDong;
import cn.edu.xidian.robot.serialport.ConnectHardware;
import cn.edu.xidian.robot.string.StringProcesser;

public class AnswerManager {
	
	   private ConnectHardware hardwareManager;
	   
       public static AnswerManager instance=new AnswerManager();
       
     
       private ArrayList<AnswerAll> answerAllArray = new ArrayList<AnswerAll>(); 
       
       private int currentUser = 0; 
       
       private static final String[] answerRandom ={"��������е���ѽ��","����ĳ������ҵķ�Χ��","��Ҫ���ú�������̫����Ŷ��","���ǲ���̫���ˣ�����Խ̽���Ŷ��"};
       
	   private AnswerManager(){};
       
       public static AnswerManager getInstance(){
    	      return instance;
       }
       
       public String init(){    	      
    	      //���û���Ϊdefault
    	      for(int i=0 ; i < answerAllArray.size() ; i++ ){
                   if(answerAllArray.get(i).getUserName().equals("default")) currentUser = i;
    	      }
    	      //�򿪴���ʵ�ִ��ڳ�ʼ���������ظ������ڴ���Ϣ
    	      hardwareManager = ConnectHardware.getInstance();    	      
    	      String re = hardwareManager.info;
    	      return re;
       }
       
       public ArrayList<String> getUsers() {
   		      ArrayList<String> user = new ArrayList<String>();
   		      for(AnswerAll aa : answerAllArray){
   		    	  user.add(aa.getUserName()); 
   		      }
   		      return user;
   	   }

	   public int getCurrentUser() {
		     return currentUser;
	   }

	   public void setCurrentUser(int currentUser) {
		     this.currentUser = currentUser;
	   }
	   public void addAnswerAll(AnswerAll answerAll){
		      answerAllArray.add(answerAll);
	   }

	   public String getAnswer(String ask){
		      if(ask.length() == 0) return ask;
			   String result = new SpecialAnswer().preprocess(ask);
			   if(result != null) return result;
		      //�����Ƿ������ж�
		      /*if(toSearch(ask)) return new Search().getContentByKeys(ask);*/
		      /*����Ƿ��Ǹı�״̬�仯*/
		      ConnectHardware.getInstance().setState(ConnectHardware.ON_RECOGNIZE_STATE);
              
		      return normalAnswer(ask);
		      
	   }
	   //�����ڶ�̬���
	   public void addByUserName(String userName,AnswerInfo answerInfo){
		     for(AnswerAll aa:answerAllArray){
		    	 if(aa.getUserName() == userName){
		    		 aa.addAnswerInfo(answerInfo);
		    	 }
		     }
	   }
	   //����һ��Ĭ�ϻظ�
	   private String getRandomAnswer(){
		   Random r = new Random();
		   int index = r.nextInt(answerRandom.length);
		   return answerRandom[index];
	   }
	   
	   private String normalAnswer(String ask){
		   String result;
		   ArrayList<AnswerInfo> answerInfoArray = answerAllArray.get(currentUser).getAnswerInfoArray();
		      int index = 0;
		      double match = 0.0;
		      for(int i = 0;i<answerInfoArray.size();i++){
		    	  double tmp = StringProcesser.match(answerInfoArray.get(i).getAskStr(),ask);
		    	  if(match < tmp){
		    		  match = tmp;
		    		  index = i;
		    	  }
		      }
		      if(match >= 0.6){
		    	  AnswerInfo ai = answerInfoArray.get(index);
		    	  //TODO ��Ӷ�����Ļظ� 
		    	  
		    	  String action = ai.getAction();
		    	  String portName;
		    	  try{
		    		  portName = action.substring(0,4);
		    		  if(portName.equals("spm2")) {
		    			  hardwareManager.spm2.write(action.substring(4));
		    		  }
		    		  if(portName.equals("spm3")) {
		    			  hardwareManager.spm3.write(action.substring(4));
		    		  }
		    	  }catch(Exception e){
		    		  Log.v("ANSWER", "Ӧ���������Ϲ淶");
		    	  }
		    	  
		    	  result = ai.getAnswerByRandom();
		      }
		      else {
				   //����¼
				   FileManager.recordQuestion(ask);
				   
				   result = searchAnswer(ask);
				   if(result == null){
					 result = getRandomAnswer();
				   }
				   
		      }
		      return result;
	   }
	   private String searchAnswer(String ask){
		   HuDong hd = new HuDong();
		   return hd.getAnswer(ask);
	   }
}

