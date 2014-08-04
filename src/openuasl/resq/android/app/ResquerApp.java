package openuasl.resq.android.app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import openuasl.resq.android.activity.QRCodeActivity;
import openuasl.resq.android.net.OnQRCodeCertResultListener;
import openuasl.resq.android.uavcontrol.ResquerClient;
import openuasl.resq.android.uavcontrol.UavControlCommunication;
import openuasl.resq.android.uavcontrol.UavControlConf;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ezio.multiwii.app.App;


public class ResquerApp extends App {
		
	private ResquerClient client;
	
	private byte[] devid_bytes = null;
	private String devid_hex_num = new String();
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		setDeviceId();
	}
	
	@Override
	public void Init() {
		// TODO Auto-generated method stub
		super.Init();
				
		client = new ResquerClient(
				UavControlConf.server_ip, UavControlConf.server_port);
		client.setQRCodeCertListener(new OnQRCodeCertResultListener() {
			
			@Override
			public void onQRCodeCertResult(boolean result) {
				if(result){
					Log.i("onQRCodeCertResult","success");
						
					try {
						client.sendReadyForControl();
					} catch (IOException e) {
						e.printStackTrace();						
					}
					
				}else{ // mismatch
					Log.i("onQRCodeCertResult","fail");
					
					
				}
			}
		});
		
		UavControlCommunication c = 
				new UavControlCommunication(
						getApplicationContext(), client);
		
		
		
		super.commMW =  new UavControlCommunication(
				getApplicationContext(), client);
		
	}
	
	private void setDeviceId() {

		if (devid_hex_num.compareTo("") != 0)
			return;

		final TelephonyManager tm = (TelephonyManager) getBaseContext()
				.getSystemService(Context.TELEPHONY_SERVICE);

		final String androidId;
		androidId = ""+ android.provider.Settings.Secure.getString(
						getContentResolver(), 
						android.provider.Settings.Secure.ANDROID_ID);
		
		MessageDigest md = null;
		
		try {
			md = MessageDigest.getInstance("SHA-256");
			int tmdevid = androidId.hashCode();
			final String deviceId = tmdevid + "";
			try {
				md.update(deviceId.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} 

			devid_bytes = md.digest();

			for (int i = 0; i < devid_bytes.length; i++) {
				devid_hex_num += Integer
						.toHexString(0xff & (int) devid_bytes[i]);
			}

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.i("devid", devid_hex_num);
	}
	
	public String getDeviceId(){
		return devid_hex_num;
	}
	
	public void certificateProcess(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					client.Connect();
					client.sendDeviceId(devid_hex_num.getBytes());
					client.sendQRCodeCert(QRCodeActivity.qrvalueresult.getBytes());
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	
}
