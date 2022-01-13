package com.example.uhfreader816ubt;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.example.uhfreader816ubt.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class GetActive extends Activity implements OnClickListener {
	public String EPCList ="";
	Button btClear;
	 private Handler myHandler = new Handler() {  
	        public void handleMessage(Message msg) {
	             switch (msg.what) {   
		              case 0:
		              {
		            	  String uid = msg.getData().getString("str_uid");
		            	  myAdapter.addDevice(uid);
	            		  myAdapter.notifyDataSetChanged();
		            	  break;
		              }
	             }
	             super.handleMessage(msg);   
	        }  
	   };  
	   ListAdapter myAdapter;
	   ListView lv;
	   public Timer timer;
	   private static final int SCAN_INTERVAL = 10;
	   private boolean Scanflag=false;
	   public int m_type=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_active);
		lv=(ListView)findViewById(R.id.list_act);
		myAdapter=new ListAdapter();
		lv.setAdapter(myAdapter);
		btClear = (Button)findViewById(R.id.btClear);
		btClear.setOnClickListener(this);
		//m_type=0;
	}
	@SuppressLint("ResourceAsColor")
	@Override
	public void onClick(View v) {
		if(v==btClear)
		{
			myAdapter.mList.clear();
			myAdapter.notifyDataSetChanged();
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Scanflag=false;
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(Scanflag)return;
				Scanflag=true;
				if(!MyService.RecvString.equals(""))
				{
					EPCList += MyService.RecvString;
					MyService.RecvString ="";
					while(EPCList.length()>0)
					{
						int index = EPCList.indexOf("00EE00");
						if(index>0)
						{
							EPCList = EPCList.substring(index-2);
							int len = Integer.valueOf(EPCList.substring(0,2), 16);
							if(EPCList.length()<(len+1)*2)
							{
								break;
							}
							String sEPC = EPCList.substring(0,((len+1)*2));
							if(sEPC == EPCList)
							{
								EPCList="";
							}
							else
							{
								EPCList = EPCList.substring((len+1)*2);
							}
							byte[] data =new byte[len+1];
							data = BTClient.hexStringToBytes(sEPC);
							if(BTClient.CheckCRC(data, len+1))
							{
								Log.d("Len", String.valueOf(len+1));
								Log.d("EPC", sEPC);
								int elen = (len-5)*2;
								Log.d("eLen", String.valueOf(elen));
								String temp =sEPC.substring(8,elen+8);
								updateuid(temp);
							}

						}
						else
						{
							EPCList=EPCList.substring(2);
						}
					}
				}
				Scanflag=false;
			}
		}, 0, SCAN_INTERVAL);
	}
	 private void updateuid( String uid)
	 {
	       Message msg =new Message();
	       msg.what=0;
	       Bundle b = new Bundle();
	       b.putString("str_uid", uid);
	       msg.setData(b);
	   	   myHandler.sendMessage(msg);
	   	   System.out.println("str_uid:"+uid);
	 }
	 
	 private class ListAdapter extends BaseAdapter {
	        private ArrayList<String> mList;

	        private LayoutInflater mInflator;

	        public ListAdapter(){
	            super();
	            mList = new ArrayList<String>();
	            mInflator = getLayoutInflater();
	        }
 
	        public void addDevice(String uid) {
	        	mList.add(0, uid);
	        }
            
	        public String getDevice(int position) {
	            return mList.get(position);
	        }

	        public void clear() {
	        	mList.clear();
	        }

	        @Override
	        public int getCount() {
	            return mList.size();
	        }

	        @Override
	        public Object getItem(int i) {
	            return mList.get(i);
	        }

	        @Override
	        public long getItemId(int i) {
	            return 0;
	        }

	        @Override
	        public View getView(int i, View view, ViewGroup viewGroup) {
	          
	            // General ListView optimization code.
	            view = mInflator.inflate(R.layout.listgl, null);
	            TextView txt_uid = (TextView) view.findViewById(R.id.txt_uid);
	            String device = mList.get(i);
	            txt_uid.setText( device);
	            return view;
	        }
	    }
		
		
	    @Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			myHandler.removeMessages(0);
			if(timer != null){
				timer.cancel();
				timer = null;
			}
		}
	    
	    @Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onPause();
			Intent intent=new Intent(this,MyService.class);
	        stopService(intent);
		}

}
