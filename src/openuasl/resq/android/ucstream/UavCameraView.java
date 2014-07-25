package openuasl.resq.android.ucstream;

import java.io.IOException;
import java.net.UnknownHostException;

import openuasl.resq.android.activity.QRCodeActivity;
import openuasl.resq.android.net.OnQRCodeCertResultListener;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class UavCameraView extends SurfaceView implements SurfaceHolder.Callback  {

	SurfaceHolder holder;
	UavCameraStreamerThread mThread;
	Context context;
	
	public UavCameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		this.context = context;
		holder = getHolder();
		holder.addCallback(this);
	}

	public void initListeners(){
		mThread.streamer.setQRCodeCertListener(
				new OnQRCodeCertResultListener() {
			
			@Override
			public void onQRCodeCertResult(boolean result) {
				
				if(result){
					Log.i("onQRCodeCertResult","success");
											
					try {
						mThread.streamer.sendReadyForStreamming();
					} catch (IOException e) {
						e.printStackTrace();
						// view_exception
						
					}
					
				}else{ // mismatch
					Log.i("onQRCodeCertResult","fail");
					
					
				}
			}
		});
		
		mThread.streamer.setStartStreammingListener(
				new OnStartStreammingListener() {
			
			@Override
			public void onStartStreamming(boolean result) {
				
				if(result){
					Log.i("onStartStreamming","Start Streamming");
										
					// START!!! 
					mThread.start();
				}else{
					// view_exception
					Log.i("onStartStreamming","fail");
				}

			}
		});
		
		
	}
	
	public void initImgStreamComponents() {
		ConnectivityManager conMgr = (ConnectivityManager) getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

		if (netInfo != null && netInfo.isConnected()) {
			
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							mThread = new UavCameraStreamerThread(holder, context);
							
							initListeners();
							mThread.streamer.sendQRCodeCert(QRCodeActivity
									.qrvalueresult.getBytes("euc-kr"));
							
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				
							

		} else {
			// throw new Exception(getString(R.string.network_error));
		}
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initImgStreamComponents();
		
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, 
			int format, int width, int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean done = true;
		while (done) {
			try {
				mThread.join();
				done = false;
			} catch (InterruptedException e) {
				
				
			}
		}
	}

}
