package cn.edu.xidian.robot.talk;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import cn.edu.xidian.robot.answer.AnswerManager;
import cn.edu.xidian.robot.data.FileManager;

public class TeachMeActivity extends Activity implements 
             OnItemSelectedListener,OnClickListener{
	private ArrayAdapter<CharSequence> arrayAdapter;
    private AnswerManager answerManager=null;
    private ArrayList<String> users=null;
    private int currentUser;
    
    private Spinner spinnerUser=null;
    private Button btnOk=null;
    private Button btnCancle=null;
    private EditText eWhat=null;
    private EditText eAnswer=null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teach);
		
		answerManager=AnswerManager.getInstance();
		users=answerManager.getUsers();

		arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,users);
		currentUser=answerManager.getCurrentUser();
		
		spinnerUser=(Spinner)findViewById(R.id.spinner_teach);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		spinnerUser.setAdapter(arrayAdapter);
		spinnerUser.setSelection(currentUser);
		spinnerUser.setOnItemSelectedListener(this);
		
		btnOk=(Button)findViewById(R.id.btnOk);
		btnCancle=(Button)findViewById(R.id.btnCancle);
		btnOk.setOnClickListener(this);
		btnCancle.setOnClickListener(this);
		
		eWhat=(EditText)findViewById(R.id.etAsk);
		eAnswer=(EditText)findViewById(R.id.etAnswer);
		
	}

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		currentUser=arg2;
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.btnOk:
			answerManager.setCurrentUser(currentUser);
			String ask=eWhat.getText().toString();
			String answer=eAnswer.getText().toString();
			if(0==ask.length() || 0==answer.length()){
				Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
				return;
			}
			// TODO 保存用户添加信息,并且添加到当前用户应答表中
			if(FileManager.getInstance().writeIn(users.get(currentUser), ask, answer))
				Toast.makeText(this, "成功添加一条对话", Toast.LENGTH_SHORT).show();
			else Toast.makeText(this, "添加失败，请检查SD卡", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btnCancle:
			finish();
			break;
		default:break;
		}
	}

}
