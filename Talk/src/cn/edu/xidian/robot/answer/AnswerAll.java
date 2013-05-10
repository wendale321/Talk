package cn.edu.xidian.robot.answer;

import java.util.ArrayList;

public class AnswerAll {
	   private String userName;
       private ArrayList<AnswerInfo> answerInfoArray = new ArrayList<AnswerInfo>();
       
      public AnswerAll(String userName,ArrayList<AnswerInfo> answerInfoArray) {
		      this.userName = userName;
		      this.answerInfoArray = answerInfoArray;
	  }
	  public void addAnswerInfo(AnswerInfo answerInfo){
    	      answerInfoArray.add(answerInfo);
       }
       public AnswerInfo getAnswerInfo(String askWhat){
    	      AnswerInfo answerInfo = new AnswerInfo();
    	      // TODO 根据识别结果找到应答信息
    	      
    	      return answerInfo;
       }
	   public String getUserName() {
		      return userName;
	   }
	public ArrayList<AnswerInfo> getAnswerInfoArray() {
		return answerInfoArray;
	}
}
