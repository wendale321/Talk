package cn.edu.xidian.robot.talk;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import cn.edu.xidian.robot.answer.AnswerManager;
import cn.edu.xidian.robot.data.FileManager;
import cn.edu.xidian.robot.serialport.ConnectHardware;
import cn.edu.xidian.robot.string.StringProcesser;

import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.speech.SpeechError;
import com.iflytek.speech.SynthesizerPlayer;
import com.iflytek.speech.SynthesizerPlayerListener;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;

public class TalkActivity extends Activity implements OnClickListener,OnInitListener,
                                                TextToSpeech.OnUtteranceCompletedListener{
    /** Called when the activity is first created. */
	private Button btnSet;
	private Button btnTeach;
	private Button btnPause;
	private EditText etShow;
	private AnswerManager answerManager=null;
	private TextToSpeech mTts = null ;//���������ϳ���
	private boolean recFlag=true;//ʶ��״̬����
	
	private int speakSpeed=50;//����
	private int namePos=0;//��������������check=0ʱ������
	private int check = R.id.tts_online;//�����ϳɷ�ʽ
	private final String[] voiceName={"vixy","vixm","vixk","vixying","vixx"};
	//��ʼ����Ϣ
	private    String info="";
	
	//����ÿ��ʶ��Ľ��
	private String askStr = "";
	   
	private final static int TEACH_CODE=0;
	private final static int SET_CODE=1;
	
	HashMap<String, String> params = new HashMap<String, String>();  
	
	private Thread detectThread;
	
	private BackgroundMusic musicPlayer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /*ȡ������,ȫ����ʾ*/
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN_ON , )
        
        setContentView(R.layout.main);
        
   	    answerManager = AnswerManager.getInstance();
   	    mTts = new TextToSpeech(this,this);
     
        findAndSetView();
     
     
        if(FileManager.getInstance().initData()) info += "���ݼ��سɹ���";
        else info += "���ݼ���ʧ�ܡ�";
     
        info += answerManager.init();
     
        etShow.setText(info);
        speakOnline(info);

        detectSerialPort();
        
        musicPlayer = new BackgroundMusic(this);
     
    }
    private void findAndSetView(){
    	    btnSet=(Button)findViewById(R.id.btnSet);
    	    btnTeach=(Button)findViewById(R.id.btnTeach);
    	    btnPause=(Button)findViewById(R.id.btnPause);
    	    etShow=(EditText)findViewById(R.id.etShow);
    	    btnSet.setOnClickListener(this);
    	    btnTeach.setOnClickListener(this);
    	    btnPause.setOnClickListener(this);
    }
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.btnSet:
			Bundle bundle = new Bundle();
			bundle.putInt("SPEED", speakSpeed);
			bundle.putInt("NAME_POS", namePos);
			bundle.putInt("CHECK", check);
    		Intent intent1=new Intent(this,SettingsActivity.class);
    		intent1.putExtras(bundle);
     	    startActivityForResult(intent1,SET_CODE);
			break;
		case R.id.btnTeach:
			Intent intent2=new Intent(this,TeachMeActivity.class);
    		startActivityForResult(intent2,TEACH_CODE);
			break;
		case R.id.btnPause:
			recognizerManager();
			break;
			default:break;
		}
	}
	private void recognizerManager(){
		 if(recFlag){
			 recognizerPause();
		 }
		 else{
			 recognizerRestart();
		 }
	}
	public void recognizerPause(){
		recFlag=false;
		btnPause.setText(R.string.begin);
	}
    public void recognizerRestart(){
    	recFlag=true;			 
		btnPause.setText(R.string.pause);	
		this.setRecognizer();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch(requestCode){
		case TEACH_CODE:
			break;
		case SET_CODE:
			Bundle bundle = data.getExtras();
			speakSpeed = bundle.getInt("SPEED");
			namePos = bundle.getInt("NAME_POS");
			check = bundle.getInt("CHECK");
			break;
		default:break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}	
	//����ʶ����
		public void setRecognizer(){
			if(! recFlag) return;
			ConnectHardware.getInstance().setState(ConnectHardware.ON_RECOGNIZE_STATE);
			askStr="";
			RecognizerDialog isrDialog = new RecognizerDialog(TalkActivity.this,"appid=5038f104");
			isrDialog.setEngine("sms",null,null);
			isrDialog.setSampleRate(RATE.rate8k);
			isrDialog.show();
			RecognizerDialogListener recognizeListener = new RecognizerDialogListener(){

				public void onEnd(SpeechError arg0) {
					// TODO Auto-generated method stub
					//�������ʶ�����
					if(arg0 != null){
						switch(arg0.getErrorCode()){
						//����������
						case 20001:
							 etShow.setText(arg0.toString() + "\n����ʶ��ֹͣ");
							 //ֹͣ����ʶ��ע�������水ť���ݸı�
							 recognizerPause();
							 break;
					    default:
					    	askStr="";
							speak(askStr);
							break;
						}
						return ;
					}
					//��ʶ�������д����޳����еı�㡢�š�Ŷ����ʶ�����������Ϣ
	                askStr = StringProcesser.pureContent(askStr);
	                if(askStr.length() <= 1) {
	                    askStr = "";
	                	speak(askStr);
	                	return ;
	                }
	                //����޸ķ��Ըı�
	                String checkResult = checkLanguage(askStr);
	                if(null != checkResult){
	                	etShow.setText("��˵��" + askStr + "\n�ظ���" + checkResult);
	                	speak(checkResult);
	                	return;
	                }
	                //��Ӳ������ֹ���
	                if(StringProcesser.match(askStr, "����") >= 0.6 ||
	                   StringProcesser.match(askStr, "��") >= 0.6){
	                	musicPlayer.playBackgroundMusic();
	                	while(musicPlayer.isBackgroundMusicPlaying());
	                	speakOnline("���ֲ������.");
	                	etShow.setText("���ֲ������.");
	                	return;
	                }
	                
	                
	                String answer = answerManager.getAnswer(askStr); 
	                etShow.setText("��˵��" + askStr + "\n�ظ���" + answer);
	                speakOnline(answer);
	                 
				}
				public void onResults(ArrayList<RecognizerResult> arg0, boolean arg1) {
					// TODO Auto-generated method stub
					for(int i = 0; i < arg0.size(); i++)
						askStr += arg0.get(i).text;
				}
			};
			isrDialog.setListener(recognizeListener);
			isrDialog.showErrorView(false, false);
		}
		public void speak(String arg0){
			ConnectHardware.getInstance().setState(ConnectHardware.ON_SPEAK_STATE);
			if(R.id.tts_online == check) speakOnline(arg0);
			else speakOffline(arg0);
		}
		//�����ϳɺ���
		public void speakOnline(String arg0){
			SynthesizerPlayer player = SynthesizerPlayer.createSynthesizerPlayer(this, "appid=5038f104");
			player.setVoiceName(voiceName[namePos]);
			player.setSpeed(speakSpeed);
			SynthesizerPlayerListener synbgListener = new SynthesizerPlayerListener (){
			public void onPlayBegin(){
			// ���ſ�ʼ�ص�����ʾ�ѻ�õ�һ�黺������Ƶ��ʼ����
			}
			public void onBufferPercent(int percent,int beginPos,int endPos){
			// ����ص���֪ͨ��ǰ�������
			}
			public void onPlayPaused(){
			// ��֪ͣͨ����ʾ�������ݲ�����ɣ���������Ƶ�������ڻ�ȡ
			}
			public void onPlayResumed(){
			// ��֪ͣͨ�����»�ȡ��������Ƶ����ʾ���¿�ʼ����
			}
			public void onPlayPercent(int percent,int beginPos,int endPos){
			// ���Żص���֪ͨ��ǰ���Ž���
			}
			public void onEnd(SpeechError error){
					ConnectHardware.getInstance().setState(ConnectHardware.END_SPEAK_STATE);
				   setRecognizer();
			}
			};
			player.playText(arg0,"ent=vivi21,bft=0",synbgListener);
		}
		public void speakOffline(String arg0){
			params.clear();
			params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "0");
			mTts.speak(arg0, TextToSpeech.QUEUE_ADD, params);
		}
		//����������ʼ��
		public void onInit(int status) {
			// TODO Auto-generated method stub
			mTts.setOnUtteranceCompletedListener(this);
	        //speak(info);
		}
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			if(mTts != null) mTts.shutdown();
		}
		public void onUtteranceCompleted(String utteranceId) {
			// TODO Auto-generated method stub
		   // setRecognizer();
		//	speakOnline("");
		}
		
		public String checkLanguage(String ask){
			if(StringProcesser.match(ask, "��ͨ��") > 0.6){
				namePos = 0;
				return "����˵��ͨ������";
			}
			if(StringProcesser.match(ask, "����") > 0.6 ||
			   StringProcesser.match(ask, "�㶫��") > 0.6){
				namePos = 1;
				return "����˵�㶫����.";
			}
			if(StringProcesser.match(ask, "���ϻ�") > 0.6){
				namePos = 2;
				return "����˵���ϻ���.";
			}
			if(StringProcesser.match(ask, "������") > 0.6){
				namePos = 3;
				return "����˵����������";
			}
			if(StringProcesser.match(ask, "����С��") > 0.6){
				namePos = 4;
				return "��˵��������С�²���";
			}
			return null;
		}
		private void detectSerialPort(){
			 detectThread = new Thread() {
				// public Handler mHandler;
				 @Override
		            public void run() {
					 	while(true){
					 		Log.v("DETECT", "I am running");
					 		String info_detect = ConnectHardware.getInstance().detectSPM2();
					 		if(info_detect != null) processInfo(info_detect);
					 		info_detect = ConnectHardware.getInstance().detectSPM3();
					 		if(info_detect != null) processInfo(info_detect);
					 		try{
					 			Thread.sleep(1000);
					 		}catch(Exception e){
					 		}
					 	}
		            }
				private void processInfo(String info){                   
//					too hot\n�����¾��淢��
					if(info.indexOf("too hot\n") != -1){
						speakOffline("����̫��ѽ�������Ҹ������ĵط��İɡ�");
					}
//				      shake hands\n�������źŷ���
					if(info.indexOf("shake hands\n") != -1){
						//TODO ����Ϊһ��������̴���
						speakOffline("���ѽ��");
						ConnectHardware.getInstance().spm2.write("duojid");
						try {
							while(true){
								ConnectHardware.getInstance().spm2.write("cgqK");
								sleep(500);
								String info_detect = ConnectHardware.getInstance().detectSPM2();
								if(info_detect.indexOf("shake hands\n") == -1) break;
							}
							ConnectHardware.getInstance().spm2.write("duojia");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
//				      dress was lifted\n����ȹ�źŷ���
					if(info.indexOf("dress was lifted\n") != -1){
						speakOffline("��Ҫ����ѽ�����Ƕ��������ˡ�");
					}
//				      right breast touched\n�����ر��Ӵ��źŷ���
					if(info.indexOf("right breast touched\n") != -1){
						speakOffline("��å����̯�ϴ����ˣ�");
					}
//				      left breast touched\n�����ر��Ӵ��źŷ���
					if(info.indexOf("left breast touched\n") != -1){
						speakOffline("��å����̯�ϴ����ˣ�");
					}
//				      left shoulder touched\n����类�Ӵ��źŷ���
					if(info.indexOf("left shoulder touched\n") != -1){
						speakOffline("���ǣ��Һܼ�ǿ!");
					}
//				      right shoulder touched\n���Ҽ类�Ӵ��źŷ���
					if(info.indexOf("right shoulder touched\n") != -1){
						speakOffline("���ǣ��Һܼ�ǿ!");
					}
				}
			 };
		     detectThread.start(); /* �����߳� */
		}
		
		/*����Ӳ����������Ϣ*/
	
}