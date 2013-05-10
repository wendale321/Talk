package cn.edu.xidian.robot.data;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cn.edu.xidian.robot.answer.AnswerInfo;
public class MySax extends DefaultHandler {
	//���ĵ�����Ӧ����Ϣ
	private ArrayList<AnswerInfo> answerInfoArray = new ArrayList<AnswerInfo>();
	
	//���ڵ��ǩ����
	private static final String TALK = "talk" ;
	private static final String ASK = "ask" ;
	private static final String ANSWER = "answer" ;
	private static final String ACTION = "action" ;
	
	//��Ҫ��ӵ�Ӧ���
	private AnswerInfo answerInfo = null;
	
	public void startElement(String uri,
                         String localName,
                         String qName,
                         Attributes attributes)
                  throws SAXException{
                  if(qName.equals(TALK)){
                	  answerInfo = new AnswerInfo();
                	  return ;
                  }
                  if(qName.equals(ASK)){
                	  answerInfo.setAskStr(attributes.getValue(0));
                	  return ;
                  }
                  if(qName.equals(ANSWER)){
                	  answerInfo.addToAnswer(attributes.getValue(0));
                	  return ;
                  }
                  if(qName.equals(ACTION)){
                	  answerInfo.setAction(attributes.getValue(0));
                	  return ;
                  }
                  //throw new SAXException();
		
	}
	
	public void endElement(String uri,
                       String localName,
                       String qName)
                throws SAXException{
		if(qName.equals(TALK)){
			answerInfoArray.add(answerInfo);
      	  return ;
        }
        if(qName.equals(ASK)){
      	  return ;
        }
        if(qName.equals(ANSWER)){
      	  return ;
        }
        if(qName.equals(ACTION)){
      	  return ;
        }
        //throw new SAXException();
		
	}
	
	public ArrayList<AnswerInfo> getAnswerInfoArray() {
		return answerInfoArray;
	}
	
}

