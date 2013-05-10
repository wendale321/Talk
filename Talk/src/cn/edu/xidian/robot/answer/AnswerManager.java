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
       
       private static final String[] answerRandom ={"这个问题有点难呀。","这真的超过了我的范围。","不要觉得和我聊天太无聊哦。","我是不是太笨了，你可以教教我哦。"};
       
	   private AnswerManager(){};
       
       public static AnswerManager getInstance(){
    	      return instance;
       }
       
       public String init(){    	      
    	      //把用户设为default
    	      for(int i=0 ; i < answerAllArray.size() ; i++ ){
                   if(answerAllArray.get(i).getUserName().equals("default")) currentUser = i;
    	      }
    	      //打开串口实现串口初始化，并返回各个串口打开信息
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
		      //进行是否搜索判断
		      /*if(toSearch(ask)) return new Search().getContentByKeys(ask);*/
		      /*检测是否是改变状态变化*/
		      ConnectHardware.getInstance().setState(ConnectHardware.ON_RECOGNIZE_STATE);
              
		      return normalAnswer(ask);
		      
	   }
	   //服务于动态添加
	   public void addByUserName(String userName,AnswerInfo answerInfo){
		     for(AnswerAll aa:answerAllArray){
		    	 if(aa.getUserName() == userName){
		    		 aa.addAnswerInfo(answerInfo);
		    	 }
		     }
	   }
	   //给出一个默认回复
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
		    	  //TODO 添加动作类的回复 
		    	  
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
		    		  Log.v("ANSWER", "应答动作不符合规范");
		    	  }
		    	  
		    	  result = ai.getAnswerByRandom();
		      }
		      else {
				   //做记录
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

