package com.example.uhfreader816ubt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.uhfreader816ubt.R;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class IsoG2Activity extends Activity implements OnClickListener, OnItemClickListener{
	private String mode;
	private Map<String,Integer> data;
	RequestQueue requestQueue;
	Button scan;
	Button bclear;
	Button enviar;
	ListView listView;
	TextView txNum;
	static Map<String, Integer> scanResult = new HashMap<String, Integer>(); // aqui ta chave valor
	static Map<String, byte[]> epcBytes = new HashMap<String, byte[]>();
	public static Timer timer;
	private MyAdapter myAdapter;
	private Handler mHandler;
	private boolean isCanceled = true;
	private static final int SCAN_INTERVAL = 20;
	
	private static final int MSG_UPDATE_LISTVIEW = 0;
	private boolean Scanflag=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_g2);
		enviar = (Button)findViewById(R.id.btEnviarAPI);
		enviar.setOnClickListener(this);
		scan = (Button)findViewById(R.id.button_scanrr9);
		scan.setOnClickListener(this);
		bclear = (Button)findViewById(R.id.btClear);
		bclear.setOnClickListener(this);
		listView = (ListView)findViewById(R.id.listrr9);//
		listView.setOnItemClickListener(this);
		data = new HashMap<String, Integer>();
		txNum = (TextView)findViewById(R.id.tx_numrr9);
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(isCanceled) return;
				switch (msg.what) {
				case MSG_UPDATE_LISTVIEW:
					data = scanResult;
					if(myAdapter == null){
						myAdapter = new MyAdapter(IsoG2Activity.this, new ArrayList(data.keySet()));
						listView.setAdapter(myAdapter);
					}else{
						myAdapter.mList = new ArrayList(data.keySet());
					}
					txNum.setText(String.valueOf(myAdapter.getCount()));
					myAdapter.notifyDataSetChanged();
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};


	}
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
class MyAdapter extends BaseAdapter{
		
		private Context mContext;
		private List<String> mList;
		private LayoutInflater layoutInflater;
		
		public MyAdapter(Context context, List<String> list) {
			mContext = context;
			mList = list;
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup viewParent) {
			// TODO Auto-generated method stub
			ItemView iv = null;
			if(view == null){
				iv = new ItemView();
				view = layoutInflater.inflate(R.layout.list, null);
				iv.tvCode = (TextView)view.findViewById(R.id.list_lable);
				iv.tvNum = (TextView)view.findViewById(R.id.list_number);
				view.setTag(iv);
			}else{
				iv = (ItemView)view.getTag();
			}
			iv.tvCode.setText(mList.get(position));
			iv.tvNum.setText(data.get(mList.get(position)).toString());
			return view;
		}
		
		public class ItemView{
			TextView tvCode;
			TextView tvNum;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		/*String id = myAdapter.mList.get(position);
		Intent intent = new Intent(this,ReadWActivity.class);
		intent.putExtra("mode", "G2");
		BTClient.settag_id(myAdapter.mList.get(position));
		//IsoG2Activity.this.startActivity(intent);
		goActivty(intent);*/
		String id = myAdapter.mList.get(position);
		Intent intent = new Intent(this,ReadWActivity.class);
		intent.putExtra(MainActivity.EXTRA_MODE, mode);
		BTClient.settag_id(myAdapter.mList.get(position));
		goActivty(intent);
	}
	private void goActivty(Intent intent){
		Log.i("zhouxin","------------------go");
        Window w = ((ActivityGroup)getParent()).getLocalActivityManager()  
                .startActivity("SecondActivity",intent);  
        View view = w.getDecorView();  
        ((ActivityGroup)getParent()).setContentView(view);
        Log.i("zhouxin", "------------------oo");
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(scan == arg0)
		{
			if(timer == null){
				if (myAdapter != null) {
					scanResult.clear();
					myAdapter.mList.clear();
					myAdapter.notifyDataSetChanged();
					mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
					mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW);
				}
				isCanceled = false;
				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						if(Scanflag)return;
						Scanflag=true;
						readuid();
						mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
						mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW);
						Scanflag=false;
					}
				}, 0, SCAN_INTERVAL);
				scan.setText("????Parar");
			}else{
				//cancelScan();
				isCanceled = true;
				if(timer != null){
					timer.cancel();
					timer = null;
					scan.setText("Buscar");
				}
				isCanceled =false;
			}
		}
		else if(bclear == arg0)
		{
			mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
			scanResult.clear();
			if (myAdapter != null) {
				myAdapter.mList.clear();
				myAdapter.notifyDataSetChanged();
			}
			txNum.setText("0");
		}
		////////////////////////////////////////////////////////////////////////////////
		// PEGA A STRING DE TODAS AS TAGS E TRANSFORMA EM JSON
		else if(enviar == arg0) { // Chamando bot??o de enviar para API
			JSONObject json =  new JSONObject(scanResult);
			System.out.printf( "JSON: %s", json); // PEGA TODA STRING E MANADA EM JSON
			mandaPostApi(scanResult);//AQUI mandar as requisicoes para API
		}
		////////////////////////////////////////////////////////////////////////////////
	}
	////////////////////////////// TRECHO REQUEST POST API ////////////////////////////
	private void mandaPostApi(Map<String, Integer> scanResult) {
		for (Map.Entry<String, Integer> entry : scanResult.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();
			System.out.println("RFID Code: " + key + ", Qtd lidas: " + value);
			String url = "https://upc-rfid-api-unb.herokuapp.com/bluetoothsearches";
			JSONObject paramsApi = new JSONObject();
			try {
				paramsApi.put("rfidCode", key);//aqui manda o params para API
			} catch (JSONException e) {
				e.printStackTrace();
			}
			JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, paramsApi, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					System.out.printf("Response", "" + response);

				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					System.out.printf("Error", error.getMessage());

				}
			});

			requestQueue = Volley.newRequestQueue(this);
			requestQueue.add(request);
		}
		Toast.makeText(this, "Enviado para a API", Toast.LENGTH_SHORT).show();
	}
	////////////////////////////////////////////////////////////////////////////////////

	private void cancelScan(){
		isCanceled = true;
		mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
		if(timer != null){
			timer.cancel();
			timer = null;
			scan.setText("Escanear BT");
			scanResult.clear();
			if (myAdapter != null) {
				myAdapter.mList.clear();
				myAdapter.notifyDataSetChanged();
			}
			txNum.setText("0");
		}
		isCanceled =false;
	}
	private void readuid(){
		int scaned_num=0;
		String[] lable = ScanUID();
		if(lable == null){ 
			scaned_num = 0;
			return ;
		}
		scaned_num = lable.length;
		for (int i = 0; i < scaned_num; i++) {
			String key = lable[i];
			if(key == null || key.equals("")) return;
			int num = scanResult.get(key) == null ? 0 : scanResult.get(key);
			scanResult.put(key, num + 1);
		}
	}
    public int errorcount=0;
	public String[] ScanUID()//
	{	
		byte[]EPCList=new byte[5000];
		int[]CardNum=new int[2];
		int[] EPCLength=new int[2];
		CardNum[0]=0;
		EPCLength[0]=0;
		int result= BTClient.Inventory_G2((byte)4, (byte)0, (byte)0, (byte)0, (byte)0, CardNum, EPCList, EPCLength);
		if(((CardNum[0]&255)>0)&&(result!=0x30))
		{
			int Scan6CNum=CardNum[0]&255;
		    String[] lable = new String[Scan6CNum];
		    StringBuffer bf;
		    int j = 0, k;
		    String str;
		    byte[] epc;
		    Log.i("zdy","num = "+ Scan6CNum + ">>>>>>"+"len = "+ EPCLength[0]);
		    for(int i = 0; i < Scan6CNum; i++){
		    	bf = new StringBuffer("");
		    	Log.i("yl","length = " + EPCList[j]);
		    	epc = new byte[EPCList[j] & 0xff];
		    	for(k = 0; k < (EPCList[j] & 0xff); k++){
		    		str = Integer.toHexString(EPCList[j+k+1] & 0xff);
		    		if(str.length() == 1){
		    			bf.append("0");
		    		}
		    		bf.append(str);
		    		epc[k] = EPCList[j+k+1];
		    	}
		    	lable[i] = bf.toString().toUpperCase();
		    	epcBytes.put(lable[i], epc);
		    	j = j+k+2;
		    }
		    return lable;
		}
		return null;
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		cancelScan();	
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	public boolean onKeyDown(int keyCode, KeyEvent event)  
    {   
		if((keyCode == KeyEvent.KEYCODE_BACK)){
			cancelScan();
			finish();
	        return false;  
        }else { 
            return super.onKeyDown(keyCode, event); 
        } 
    }

}
