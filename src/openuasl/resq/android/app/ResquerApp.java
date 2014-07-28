package openuasl.resq.android.app;

import openuasl.resq.android.net.ResquerClient;

import com.ezio.multiwii.app.App;


public class ResquerApp extends App {
	
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		
		
	}
	
	@Override
	public void Init() {
		// TODO Auto-generated method stub
		super.Init();
		
		super.commMW = new ResquerClient(getApplicationContext());
	}
	
}
