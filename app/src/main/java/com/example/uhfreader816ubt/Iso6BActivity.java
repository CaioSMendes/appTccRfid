package com.example.uhfreader816ubt;
import com.example.uhfreader816ubt.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Iso6BActivity extends Activity {

private Spinner tvpowerdBm;
	byte[] Version=new byte[2];
	byte[] Power=new byte[2];
	byte[] Fre=new byte[2];
	private Button bSetting;
	private Button bRead;
	private String[] strBand =new String[5]; 
    private String[] strmaxFrm =null; 
    private String[] strminFrm =null; 
    Spinner spBand;
    Spinner spmaxFrm;
	Spinner spminFrm;
	private ArrayAdapter<String> spada_Band; 
    private ArrayAdapter<String> spada_maxFrm; 
    private ArrayAdapter<String> spada_minFrm; 
    private static final int MSG_UPDATE_UID = 0;
    private static final int MSG_UPDATE_DATA = 1;
    private static final int MSG_UPDATE_WRITE = 2;
    private static final int MSG_SHOW_PROPERTIES=3;
    public String str_update="";
    public Handler myHandler = new Handler() {  

        public void handleMessage(Message msg) {   
             switch (msg.what) {   
	              case MSG_UPDATE_UID:
	              {
	            	 break;
	              }
                  case MSG_UPDATE_DATA:   
                  {
                     break;   
                  }  
                  case MSG_UPDATE_WRITE:   
                  {
  
                  }  
                  case MSG_SHOW_PROPERTIES:
                  {
                	  int band = (byte)((((Fre[0]&255) & 0xc0) >> 4) | ((Fre[1]&255) >> 6));
                	  showResult(band,Fre[0],Fre[1],Power[0]);
                  }
             }
             super.handleMessage(msg);   
        }  
   };  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_6b);
		tvpowerdBm = (Spinner)findViewById(R.id.power_spinner);
		ArrayAdapter<CharSequence> adapter3 =  ArrayAdapter.createFromResource(this, R.array.Power_select, android.R.layout.simple_spinner_item);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tvpowerdBm.setAdapter(adapter3); 
		tvpowerdBm.setSelection(26, true);
	    ////////////Ƶ��ѡ��
		strBand[0]="Chinese band2";
		strBand[1]="US band";
		strBand[2]="Korean band";
		strBand[3]="EU band";
		strBand[4]="Chinese band1";
		spBand=(Spinner)findViewById(R.id.band_spinner);  
		spada_Band = new ArrayAdapter<String>(Iso6BActivity.this,  
	             android.R.layout.simple_spinner_item, strBand);  
		spada_Band.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
		spBand.setAdapter(spada_Band);  
		spBand.setSelection(0,false); 
		SetFre(1);////��ʼ��Ƶ��
		 // ���Spinner�¼�����  
		spBand.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {  
	    public void onItemSelected(AdapterView<?> arg0, View arg1,  
	            int arg2, long arg3) {  
	        // TODO Auto-generated method stub  
	        // ������ʾ��ǰѡ�����  
	        arg0.setVisibility(View.VISIBLE);  
	        if(arg2==0)SetFre(1);
	        if(arg2==1)SetFre(2);
	        if(arg2==2)SetFre(3);
	        if(arg2==3)SetFre(4);
	        if(arg2==4)SetFre(8);
	        //ѡ��Ĭ��ֵ����ִ��  
	    }  
	    public void onNothingSelected(AdapterView<?> arg0) {  
	        // TODO Auto-generated method stub  
	    	}  
		});  
		bSetting = (Button)findViewById(R.id.pro_setting);
		bSetting.setOnClickListener(myListener);
		bRead = (Button)findViewById(R.id.pro_read);
		bRead.setOnClickListener(myListener);
	}
	private void showResult(int band,int dmaxfre,int dminfre, int powerdbm){
		SetFre(band);
		if(band ==8)
		{
			band=band-4;
		}
		else
		{
			band=band-1;
		}
		int frequent= ((dminfre & 0x3F)&255);
		spminFrm.setSelection(frequent,true);
		spBand.setSelection(band,true);
		frequent= ((dmaxfre & 0x3F)&255);
		spmaxFrm.setSelection(frequent,true);
		tvpowerdBm.setSelection(powerdbm,true);
	}
	private void SetFre(int m)
	{
		if(m==1){ 
		    strmaxFrm=new String[20];
         	strminFrm=new String[20];
         	for(int i=0;i<20;i++){
         		String temp="";
         		float values=(float) (920.125 + i * 0.25);
         		temp=String.valueOf(values)+"MHz";
         		strminFrm[i]=temp;
         		strmaxFrm[i]=temp;
         	}
         	spmaxFrm=(Spinner)findViewById(R.id.max_spinner);  
         	spada_maxFrm = new ArrayAdapter<String>(Iso6BActivity.this,  
                      android.R.layout.simple_spinner_item, strmaxFrm);  
         	spada_maxFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
         	spmaxFrm.setAdapter(spada_maxFrm);  
         	spmaxFrm.setSelection(19,false);
         	
         	spminFrm=(Spinner)findViewById(R.id.min_spinner);  
         	spada_minFrm = new ArrayAdapter<String>(Iso6BActivity.this,  
                      android.R.layout.simple_spinner_item, strminFrm);  
         	spada_minFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
         	spminFrm.setAdapter(spada_minFrm);  
         	spminFrm.setSelection(0,false);
     }else if(m==2){
     	strmaxFrm=new String[50];
     	strminFrm=new String[50];
     	for(int i=0;i<50;i++){
     		String temp="";
     		float values=(float) (902.75 + i * 0.5);
     		temp=String.valueOf(values)+"MHz";
     		strminFrm[i]=temp;
     		strmaxFrm[i]=temp;
     	}
     	spmaxFrm=(Spinner)findViewById(R.id.max_spinner);  
     	spada_maxFrm = new ArrayAdapter<String>(Iso6BActivity.this,  
                  android.R.layout.simple_spinner_item, strmaxFrm);  
     	spada_maxFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
     	spmaxFrm.setAdapter(spada_maxFrm);  
     	spmaxFrm.setSelection(49,false);
     	
     	spminFrm=(Spinner)findViewById(R.id.min_spinner);  
     	spada_minFrm = new ArrayAdapter<String>(Iso6BActivity.this,  
                  android.R.layout.simple_spinner_item, strminFrm);  
     	spada_minFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
     	spminFrm.setAdapter(spada_minFrm);  
     	spminFrm.setSelection(0,false);
     }else if(m==3){
      	strmaxFrm=new String[32];
      	strminFrm=new String[32];
      	for(int i=0;i<32;i++){
      		String temp="";
      		float values=(float) (917.1 + i * 0.2);
      		temp=String.valueOf(values)+"MHz";
      		strminFrm[i]=temp;
      		strmaxFrm[i]=temp;
      	}
      	spmaxFrm=(Spinner)findViewById(R.id.max_spinner);  
      	spada_maxFrm = new ArrayAdapter<String>(Iso6BActivity.this,  
                   android.R.layout.simple_spinner_item, strmaxFrm);  
      	spada_maxFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
      	spmaxFrm.setAdapter(spada_maxFrm);  
      	spmaxFrm.setSelection(31,false);
      	
      	spminFrm=(Spinner)findViewById(R.id.min_spinner);  
      	spada_minFrm = new ArrayAdapter<String>(Iso6BActivity.this,  
                   android.R.layout.simple_spinner_item, strminFrm);  
      	spada_minFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
      	spminFrm.setAdapter(spada_minFrm);  
      	spminFrm.setSelection(0,false);
      }else if(m==4){
       	strmaxFrm=new String[15];
       	strminFrm=new String[15];
       	for(int i=0;i<15;i++){
       		String temp="";
       		float values=(float) (865.1 + i * 0.2);
       		temp=String.valueOf(values)+"MHz";
       		strminFrm[i]=temp;
       		strmaxFrm[i]=temp;
       	}
       	spmaxFrm=(Spinner)findViewById(R.id.max_spinner);  
       	spada_maxFrm = new ArrayAdapter<String>(Iso6BActivity.this,  
                    android.R.layout.simple_spinner_item, strmaxFrm);  
       	spada_maxFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
       	spmaxFrm.setAdapter(spada_maxFrm);  
       	spmaxFrm.setSelection(14,false);
       	
       	spminFrm=(Spinner)findViewById(R.id.min_spinner);  
       	spada_minFrm = new ArrayAdapter<String>(Iso6BActivity.this,  
                    android.R.layout.simple_spinner_item, strminFrm);  
       	spada_minFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
       	spminFrm.setAdapter(spada_minFrm);  
       	spminFrm.setSelection(0,false);
       }else if(m==8){
		    strmaxFrm=new String[20];
         	strminFrm=new String[20];
         	for(int i=0;i<20;i++){
         		String temp="";
         		float values=(float) (840.125 + i * 0.25);
         		temp=String.valueOf(values)+"MHz";
         		strminFrm[i]=temp;
         		strmaxFrm[i]=temp;
         	}
         	spmaxFrm=(Spinner)findViewById(R.id.max_spinner);  
         	spada_maxFrm = new ArrayAdapter<String>(Iso6BActivity.this,  
                      android.R.layout.simple_spinner_item, strmaxFrm);  
         	spada_maxFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
         	spmaxFrm.setAdapter(spada_maxFrm);  
         	spmaxFrm.setSelection(19,false);
         	
         	spminFrm=(Spinner)findViewById(R.id.min_spinner);  
         	spada_minFrm = new ArrayAdapter<String>(Iso6BActivity.this,  
                      android.R.layout.simple_spinner_item, strminFrm);  
         	spada_minFrm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
         	spminFrm.setAdapter(spada_minFrm);  
         	spminFrm.setSelection(0,false);
     }
	}
	private OnClickListener myListener= new OnClickListener(){

		@Override
		public void onClick(View v) {
			
			if(v==bSetting)
			{
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						int MaxFre=0;
						int MinFre=0;
						int Power=0;
						int fband = spBand.getSelectedItemPosition();
						int band=0;
						if(fband==0)band=1;
						if(fband==1)band=2;
						if(fband==2)band=3;
						if(fband==3)band=4;
						if(fband==4)band=8;
						int Frequent= spminFrm.getSelectedItemPosition();
						MinFre = ((band & 3) << 6) | (Frequent & 0x3F);
						Frequent= spmaxFrm.getSelectedItemPosition();
						MaxFre = ((band & 0x0c) << 4) | (Frequent & 0x3F);
						Power = tvpowerdBm.getSelectedItemPosition();
						int fCmdRet = BTClient.SetPower((byte)Power);
						fCmdRet = BTClient.SetRegion((byte)MaxFre, (byte)MinFre);
						//UhfGetData.SetUhfInfo((byte)MaxFre, (byte)MinFre, (byte)Power, (byte)0);
					}
				}).start();
			}
			
			if(v==bRead)
			{
				new Thread(new Runnable() {
					@Override
					public void run() {
						Version[0]=0;
						Version[1]=0;
						Power[0]=0;
						Fre[0]=0;
						Fre[1]=0;
						int fCmdRet = BTClient.GetReaderInfo(Version, Power, Fre);
						if(fCmdRet==0)
						{
							myHandler.removeMessages(MSG_SHOW_PROPERTIES);
							myHandler.sendEmptyMessage(MSG_SHOW_PROPERTIES);
						}
					}
				}).start();
			}
		}
	};
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Thread thread=new Thread(new Runnable()  
        {  
            @Override  
            public void run()  
            { 
            	
            }  
        }); 
		thread.start();  
	}
}
