package cn.edu.xidian.robot.answer;

import java.util.ArrayList;
import java.util.Random;

public class AnswerInfo {
       private String askStr = null;
       private ArrayList<String> answersArray = null;
       private String action;//-1表示没动作
       
       public AnswerInfo(){
    	      askStr = new String();
    	      answersArray = new ArrayList<String>();
    	      action = null;
       }
       
      public AnswerInfo(String askStr,ArrayList<String> answersArray,String action ){
    	  this.askStr = askStr;   
    	  this.answersArray = answersArray;
    	  this.action = action;    	  
      }

	  public String getAskStr() {
	 	  return askStr;
	  }

	  public void setAskStr(String askStr) {
		  this.askStr = askStr;
	  }

	  public ArrayList<String> getAnswersArray() {
		  return answersArray;
	  }

	  public void setAnswersArray(ArrayList<String> answersArray) {
		  this.answersArray = answersArray;
	  }
      
	  public void addToAnswer(String answer){
		     answersArray.add(answer);
	  }
	  public String getAction() {
		  return action;
	  }

	  public void setAction(String action) {
		  this.action = action;
	  }
      public String getAnswerByRandom(){
    	  if(answersArray.size() == 0) return "这个问题很有问题呢。";
    	  Random r = new Random();
    	  int index = r.nextInt(answersArray.size());
    	  return answersArray.get(index);
      }
}
