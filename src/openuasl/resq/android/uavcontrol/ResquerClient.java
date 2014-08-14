package openuasl.resq.android.uavcontrol;

import java.io.IOException;
import java.net.UnknownHostException;

import openuasl.resq.android.R;
import openuasl.resq.android.auth.UavAuthorizationClient;
import openuasl.resq.android.ucstream.UavCameraStreamerConf;


public class ResquerClient extends UavAuthorizationClient {
	
	private OnStartControlListener start_listener = null;
	private boolean isStarting = false;
	
	public ResquerClient(String ip, int port) {
		super(ip, port);
	}

	@Override
	public void Connect()
			throws UnknownHostException, IOException {
		super.Connect();
		
	}
	
	public void setStartControlListener(
			OnStartControlListener listener) {
		start_listener = listener;
	}
	
	public void sendReadyForControl()
			throws IOException{
		if(!isAuth())
			throw new IOException("control is not ready.");

		buffer[0] = UavCameraStreamerConf.IMGS_RESQPROTO_REQ.resq_req_ready;

		out_stream.write(buffer, 0, 1);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					in_stream.read(buffer);
					isStarting = false;

					if (buffer[0] == UavCameraStreamerConf
							.IMGS_RESQPROTO_REP.resq_rep_start) {
						isStarting = true;
					}

					start_listener.onStartControl(isStarting);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void write(byte[] arr) throws IOException{
		out_stream.write(arr);
	}

	public int read(byte[] arr) throws IOException{
		return super.in_stream.read(arr);
	}
	
}
