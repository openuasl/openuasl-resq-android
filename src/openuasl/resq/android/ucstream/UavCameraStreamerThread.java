package openuasl.resq.android.ucstream;

import java.io.IOException;
import java.net.UnknownHostException;

import openuasl.resq.android.activity.LoginActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class UavCameraStreamerThread extends Thread {
	
	SurfaceHolder mHolder;
	Canvas canvas = null;
	Bitmap bitmap = null;
	public UavCameraStreamer streamer;
	
	String devid = null;
	byte[] euc_kr_devid = null;
	
	Bitmap resized = null;
	
	int dsp_w;
	int dsp_h;

	public UavCameraStreamerThread(SurfaceHolder holder, Context context) 
			throws UnknownHostException, IOException {
		mHolder = holder;
		streamer = UavCameraStreamer.getInstance();
		
		euc_kr_devid = LoginActivity.devid_hex_num.getBytes();
		
		streamer.connectServer();
		streamer.sendDeviceId(euc_kr_devid);
		
		Display display;
		display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
		dsp_w = display.getWidth();
		dsp_h = display.getHeight();
	}
	
	@Override
	public void run() {		
		
		Log.i("Streamming Thread","Run!!");
		streamer.startStramming();
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while (streamer.isConnected()) {
						
			byte[] data = streamer.receive1Frame();
			
			if(data == null) continue;				
			
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

			Bitmap resized = Bitmap.createScaledBitmap(bitmap, dsp_w, dsp_h, true);
			
			canvas = mHolder.lockCanvas();

			try {

				synchronized (mHolder) {
					
					canvas.drawBitmap(resized, 0, 0, null);
				}

			} finally {
				if (canvas != null) {
					 
					mHolder.unlockCanvasAndPost(canvas);
				}
			}
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
}
