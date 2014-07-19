package openuasl.resq.android.ucstream;

import java.io.IOException;
import java.net.UnknownHostException;

import openuasl.resq.android.activity.LoginActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class UavCameraStreamerThread extends Thread {
	
	SurfaceHolder mHolder;
	Canvas canvas = null;
	Bitmap bitmap = null;
	public UavCameraStreamer streamer;
	
	String devid = null;
	byte[] euc_kr_devid = null;

	public UavCameraStreamerThread(SurfaceHolder holder, Context context) 
			throws UnknownHostException, IOException {
		mHolder = holder;
		streamer = UavCameraStreamer.getInstance();
				
		euc_kr_devid = LoginActivity
				.devid_hex_num.getBytes("euc-kr");
		
		streamer.connectServer();
		streamer.sendDeviceId(euc_kr_devid);
	}
	
	@Override
	public void run() {		
		
		Log.i("Streamming Thread","Run!!");
		
		while (streamer.isConnected()) {
			
			byte[] data = null;
			
			try {
				data = streamer.receive1Frame();
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
			
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

			canvas = mHolder.lockCanvas();

			try {

				synchronized (mHolder) {

					canvas.setBitmap(bitmap);
				}

			} finally {
				if (canvas != null) {
					 
					mHolder.unlockCanvasAndPost(canvas);
				}
			}
		}		
	}

}
