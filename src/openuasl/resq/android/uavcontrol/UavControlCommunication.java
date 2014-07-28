package openuasl.resq.android.uavcontrol;

import java.io.IOException;
import java.net.UnknownHostException;

import openuasl.resq.android.R;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import communication.Communication;
import communication.SimpleQueue;

public class UavControlCommunication extends Communication {

	ResquerClient client;
	SimpleQueue<Integer> fifo = new SimpleQueue<Integer>();
	
	boolean loopStop = false;
	
	public UavControlCommunication(Context context, ResquerClient c) {
		super(context);
		client = c;
		Enable();
	}

	@Override
	public void Enable() {
		
		ConnectivityManager conMgr = (ConnectivityManager)
				super.context.getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
		
		if (netInfo != null && netInfo.isConnected()) {
			
			
		}else{
			sendMessageToUI_Toast(
					context.getString(R.string.Unabletoconnect));
		}
	}

	@Override
	public void Connect(String address, int speed) {
		
		try {
			client.Connect();
			
		} catch (UnknownHostException e) {
			sendMessageToUI_Toast(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			sendMessageToUI_Toast(e.getMessage());
			e.printStackTrace();
		}

		if (client.isAuth()) {
			super.Connected = true;
			loopStop = false;
			startMainLoop();
			
			setState(STATE_CONNECTED);
			sendDeviceName("WiFi SSL Port" + String.valueOf(speed));
			
		}else{
			super.Connected = false;
			sendMessageToUI_Toast(context.getString(R.string.Unabletoconnect));
		}
	}

	@Override
	public boolean dataAvailable() {
		return !fifo.isEmpty();
	}

	@Override
	public byte Read() {
		BytesRecieved+=1;
		return (byte) (fifo.get() & 0xff);
	}

	@Override
	public void Write(byte[] arr) {
		// TODO Auto-generated method stub
		super.Write(arr);
		
		super.Connected = client.isAuth();
		
		if(client.isAuth()){
			try {
				client.write(arr);
			} catch (IOException e) {
				sendMessageToUI_Toast(e.getMessage());
				e.printStackTrace();
			}
		}else{
			sendMessageToUI_Toast("client is not authorized");
		}
		
	}
	
	@Override
	public void Close() {
		Connected = false;
		loopStop = true;
		try {
			client.Disconnect();
		} catch (IOException e) {
			sendMessageToUI_Toast(e.getMessage());
			e.printStackTrace();
		}
		sendMessageToUI_Toast(context.getString(R.string.Disconnected));
		setState(STATE_NONE);

	}

	@Override
	public void Disable() {
		Connected = false;
		loopStop = true;
		try {
			client.Disconnect();
		} catch (IOException e) {
			sendMessageToUI_Toast(e.getMessage());
			e.printStackTrace();
		}
		//context.unregisterReceiver(mUsbReceiver);
		sendMessageToUI_Toast(context.getString(R.string.Disconnected));
		setState(STATE_NONE);

	}
	
	private synchronized void readToBuffer(){
		Connected = client.isAuth();
		// [FTDriver] Create Read Buffer
		byte[] rbuf = new byte[4096]; // 1byte <--slow-- [Transfer Speed]
										// --fast-->
										// 4096 byte
		if (client.isAuth()) {
			int len = 0;
			try {
				len = client.read(rbuf);
			} catch (IOException e) {
				e.printStackTrace();
			}

			mHandler.obtainMessage(MESSAGE_READ, len, -1, rbuf).sendToTarget();

			for (int i = 0; i < len; i++)
				fifo.put(Integer.valueOf(rbuf[i]));
		}
	}
	
	private void startMainLoop(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {

				while (!loopStop) {
					readToBuffer();
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
	}

}
