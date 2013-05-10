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
	private TextToSpeech mTts = null ;//离线语音合成器
	private boolean recFlag=true;//识别状态控制
	
	private int speakSpeed=50;//语速
	private int namePos=0;//发音人索引，当check=0时有意义
	private int check = R.id.tts_online;//语音合成方式
	private final String[] voiceName={"vixy","vixm","vixk","vixying","vixx"};
	//初始化信息
	private    String info="";
	
	//保存每次识别的结果
	private String askStr = "";
	   
	private final static int TEACH_CODE=0;
	private final static int SET_CODE=1;
	
	HashMap<String, String> params = new HashMap<String, String>();  
	
	private Thread detectThread;
	
	private BackgroundMusic musicPlayer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /*取消标题,全屏显示*/
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN_ON , )
        
        setContentView(R.layout.main);
        
   	    answerManager = AnswerManager.getInstance();
   	    mTts = new TextToSpeech(this,this);
     
        findAndSetView();
     
     
        if(FileManager.getInstance().initData()) info += "数据加载成功。";
        else info += "数据加载失败。";
     
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
	//语音识别函数
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
					//如果出现识别错误
					if(arg0 != null){
						switch(arg0.getErrorCode()){
						//网络错误代码
						case 20001:
							 etShow.setText(arg0.toString() + "\n语音识别停止");
							 //停止语音识别，注意主界面按钮内容改变
							 recognizerPause();
							 break;
					    default:
					    	askStr="";
							speak(askStr);
							break;
						}
						return ;
					}
					//对识别结果进行处理，剔除其中的标点、嗯、哦等误识别的无意义信息
	                askStr = StringProcesser.pureContent(askStr);
	                if(askStr.length() <= 1) {
	                    askStr = "";
	                	speak(askStr);
	                	return ;
	                }
	                //检查修改方言改变
	                String checkResult = checkLanguage(askStr);
	                if(null != checkResult){
	                	etShow.setText("你说：" + askStr + "\n回复：" + checkResult);
	                	speak(checkResult);
	                	return;
	                }
	                //添加播放音乐功能
	                if(StringProcesser.match(askStr, "音乐") >= 0.6 ||
	                   StringProcesser.match(askStr, "歌") >= 0.6){
	                	musicPlayer.playBackgroundMusic();
	                	while(musicPlayer.isBackgroundMusicPlaying());
	                	speakOnline("音乐播放完毕.");
	                	etShow.setText("音乐播放完毕.");
	                	return;
	                }
	                
	                
	                String answer = answerManager.getAnswer(askStr); 
	                etShow.setText("你说：" + askStr + "\n回复：" + answer);
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
		//语音合成函数
		public void speakOnline(String arg0){
			SynthesizerPlayer player = SynthesizerPlayer.createSynthesizerPlayer(this, "appid=5038f104");
			player.setVoiceName(voiceName[namePos]);
			player.setSpeed(speakSpeed);
			SynthesizerPlayerListener synbgListener = new SynthesizerPlayerListener (){
			public void onPlayBegin(){
			// 播放开始回调，表示已获得第一块缓冲区音频开始播放
			}
			public void onBufferPercent(int percent,int beginPos,int endPos){
			// 缓冲回调，通知当前缓冲进度
			}
			public void onPlayPaused(){
			// 暂停通知，表示缓冲数据播放完成，后续的音频数据正在获取
			}
			public void onPlayResumed(){
			// 暂停通知后重新获取到后续音频，表示重新开始播放
			}
			public void onPlayPercent(int percent,int beginPos,int endPos){
			// 播放回调，通知当前播放进度
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
		//离线语音初始化
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
			if(StringProcesser.match(ask, "普通话") > 0.6){
				namePos = 0;
				return "我在说普通话啦。";
			}
			if(StringProcesser.match(ask, "粤语") > 0.6 ||
			   StringProcesser.match(ask, "广东话") > 0.6){
				namePos = 1;
				return "我再说广东话啦.";
			}
			if(StringProcesser.match(ask, "河南话") > 0.6){
				namePos = 2;
				return "我在说河南话啦.";
			}
			if(StringProcesser.match(ask, "陕西话") > 0.6){
				namePos = 3;
				return "我在说陕西话啦。";
			}
			if(StringProcesser.match(ask, "蜡笔小新") > 0.6){
				namePos = 4;
				return "你说我像蜡笔小新不。";
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
//					too hot\n：高温警告发送
					if(info.indexOf("too hot\n") != -1){
						speakOffline("天气太热呀，咱们找个凉快点的地方聊吧。");
					}
//				      shake hands\n：握手信号发送
					if(info.indexOf("shake hands\n") != -1){
						//TODO 这作为一个特殊过程处理
						speakOffline("你好呀。");
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
//				      dress was lifted\n：掀裙信号发送
					if(info.indexOf("dress was lifted\n") != -1){
						speakOffline("不要这样呀，咱们都是文明人。");
					}
//				      right breast touched\n：右胸被接触信号发送
					if(info.indexOf("right breast touched\n") != -1){
						speakOffline("流氓，你摊上大事了！");
					}
//				      left breast touched\n：左胸被接触信号发送
					if(info.indexOf("left breast touched\n") != -1){
						speakOffline("流氓，你摊上大事了！");
					}
//				      left shoulder touched\n：左肩被接触信号发送
					if(info.indexOf("left shoulder touched\n") != -1){
						speakOffline("哥们，我很坚强!");
					}
//				      right shoulder touched\n：右肩被接触信号发送
					if(info.indexOf("right shoulder touched\n") != -1){
						speakOffline("哥们，我很坚强!");
					}
				}
			 };
		     detectThread.start(); /* 启动线程 */
		}
		
		/*处理硬件产生的信息*/
	
}