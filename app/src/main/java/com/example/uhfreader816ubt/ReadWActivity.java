package com.example.uhfreader816ubt;

import com.example.uhfreader816ubt.R;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ReadWActivity extends Activity implements OnClickListener, OnItemSelectedListener{
	private int mode;
	private static final int MODE_6B = 0;
	private static final int MODE_6C = 1;
	static SoundPool soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100);;
	static int soundid = soundpool.load("/etc/Scan_new.ogg", 1);
	byte[]EPC=new byte [100];
	byte Enum=0;
	byte Mem=0;
	byte WordAddr=0;
	byte Num=0;
	byte[]Psd=new byte[4];
	byte[] Data=new byte[100];
	EditText edENum0;
//	EditText edENum1;
//	EditText edENum2;
//	EditText edENum3;
//	EditText[] edENums;
	int selectedEd = 3;
	int selectedWhenPause = 0;
	
	Spinner c_mem;
	EditText c_wordPtr;
	EditText c_len;
	EditText c_pwd;
	EditText c_ptr;
	
	EditText b_id;
	EditText b_addr;
	EditText b_num;
	
	EditText content;
	Button rButton;
	Button wButton;
	public byte state=0;
	public String str_update="";
	private static final int MSG_UPDATE_DATA = 0;
    private static final int MSG_UPDATE_WRITE = 1;
    private Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {
             switch (msg.what) {   
                  case MSG_UPDATE_DATA:
                  {
                	  content.setText(str_update);
                     break;   
                  }  
                  case MSG_UPDATE_WRITE:   
                  {
                	  if(str_update=="00")
                	  {
                		  Toast.makeText(ReadWActivity.this, "�ɹ���", Toast.LENGTH_SHORT).show();
                	  }else
                	  {
                		  Toast.makeText(ReadWActivity.this, "ʧ�ܣ�", Toast.LENGTH_SHORT).show();
                	  }
                      break;   
                  }  
             }
             super.handleMessage(msg);   
        }  
        
   };  
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_readw);
		getIntent().getStringExtra(MainActivity.EXTRA_MODE);
		initView();

	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.i("zhouxin",">>>>>>>>>>>>>>>>>>>>>>rw onResume");
		if(!BTClient.gettag_id().equals(edENum0.getText().toString())){
			edENum0.setText(BTClient.gettag_id());
		}
		content.setText("");
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		selectedWhenPause = selectedEd;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("zhouxin", ">>>>>>>>>>>>>>>>>>>>>rw onDestroy");
		super.onDestroy();
	}
	private void initView(){
		c_mem = (Spinner)findViewById(R.id.mem_spinner);
		ArrayAdapter<CharSequence> adapter =  ArrayAdapter.createFromResource(this, R.array.men_select, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		c_mem.setAdapter(adapter); 
		c_mem.setSelection(3, true);
		c_mem.setOnItemSelectedListener(this);
		
		c_wordPtr = (EditText)findViewById(R.id.et_wordptr);
		c_wordPtr.setText("0");
		c_len = (EditText)findViewById(R.id.et_length);
		c_len.setText("4");
		c_pwd = (EditText)findViewById(R.id.et_pwd);
		c_pwd.setText("00000000");
		content = (EditText)findViewById(R.id.et_content_6c);
		rButton = (Button)findViewById(R.id.button_read_6c);
		wButton = (Button)findViewById(R.id.button_write_6c);
		edENum0 = (EditText)findViewById(R.id.epc0);
//		edENums = new EditText[]{edENum0, edENum1, edENum2, edENum3};
		
/*		edENum0.setOnClickListener(this);
		edENum1.setOnClickListener(this);
		edENum2.setOnClickListener(this);
		edENum3.setOnClickListener(this);*/
		rButton.setOnClickListener(this);
		wButton.setOnClickListener(this);
		}
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Log.i("zhouxin",">>>>>>>>>position>>>>>>"+position);
		selectedEd = position;
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, IsoG2Activity.class);  
        intent.putExtra(MainActivity.EXTRA_MODE, getIntent().getStringExtra(MainActivity.EXTRA_MODE));
        Window w = ((ActivityGroup)getParent()).getLocalActivityManager().startActivity(  
                "FirstActivity", intent);  
        View view = w.getDecorView();  
        ((ActivityGroup)getParent()).setContentView(view); 
        //((ActivityGroup)getParent()).getLocalActivityManager().destroyActivity("SecondActivity", false);
	}
	
	@SuppressLint("ResourceAsColor")
	@Override
	public void onClick(View v) {
			String sepc=edENum0.getText().toString();
			if(sepc.length()==0)return;
			Enum = (byte)(sepc.length()/4);
			EPC= BTClient.hexStringToBytes(sepc);
			Mem = (byte)selectedEd;
			WordAddr = (byte)(int)Integer.valueOf(c_wordPtr.getText().toString());
			Psd[0]=0;
			Psd[1]=0;
			Psd[2]=0;
			Psd[3]=0;
			if(v==wButton)
			{
				Num = (byte)(content.getText().toString().length()/4);
				if(Num==0)return;
				Data = BTClient.hexStringToBytes(content.getText().toString());
				Thread thread=new Thread(new Runnable()  
	            {  
	                @Override  
	                public void run()  
	                { 
	                	int result=BTClient.WriteData_G2(Enum,EPC,Mem,WordAddr,Num,Psd,Data);
	    				if(result==0)
	    				{
	    					str_update="00";
	    					myHandler.removeMessages(MSG_UPDATE_WRITE);
	                		myHandler.sendEmptyMessage(MSG_UPDATE_WRITE);
	    				}else
	    				{
	    					str_update="";
	    					myHandler.removeMessages(MSG_UPDATE_WRITE);
	                		myHandler.sendEmptyMessage(MSG_UPDATE_WRITE);
	    					/*str_update="";
	    					myHandler.removeMessages(MSG_UPDATE_DATA);
	                		myHandler.sendEmptyMessage(MSG_UPDATE_DATA);*/
	                	}
	                }  
	            });  
				thread.start();
			}else if(v==rButton)
			{
				Num =(byte)(int)Integer.valueOf(c_len.getText().toString());
				Thread thread=new Thread(new Runnable()  
	            {  
	                @Override  
	                public void run()  
	                { 
	                	int result=BTClient.ReadData_G2(Enum, EPC, Mem, WordAddr, Num, Psd, Data);
	    				if(result==0)
	    				{
	    					str_update=BTClient.bytesToHexString(Data, 0, Num*2);
	    					myHandler.removeMessages(MSG_UPDATE_DATA);
	                		myHandler.sendEmptyMessage(MSG_UPDATE_DATA);
	    				}else
	    				{
	    					str_update="";
	    					myHandler.removeMessages(MSG_UPDATE_DATA);
	                		myHandler.sendEmptyMessage(MSG_UPDATE_DATA);
	                	}
	                }  
	            });  
				thread.start();
			}
		}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}


}
