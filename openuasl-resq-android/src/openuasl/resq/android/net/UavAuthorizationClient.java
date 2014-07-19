package openuasl.resq.android.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class UavAuthorizationClient {

	private boolean is_connected = false;
	private boolean is_auth = false;
	
	private String server_ip = "";
	private int server_port = 0;

	private OnQRCodeCertResultListener qrcode_listener = null;
	

	protected Socket socket;
	protected InputStream in_stream;
	protected OutputStream out_stream;
	protected byte[] buffer = new byte[UavAuthorizationConf.netbuf_size];

	protected UavAuthorizationClient(String ip, int port) {
		server_ip = ip;
		server_port = port;
	}

	protected void connectServer(boolean use_ssl) 
			throws UnknownHostException, IOException {
		
			if (use_ssl == false) {
					socket = new Socket(server_ip, server_port);
				
			} else {
				SSLSocketFactory f = (SSLSocketFactory) SSLSocketFactory
						.getDefault();
				socket = (SSLSocket) f.createSocket(server_ip, server_port);
				((SSLSocket) socket).startHandshake();
			}

			in_stream = socket.getInputStream();
			out_stream = socket.getOutputStream();
			
			is_connected = true;
	}
	
	public void setQRCodeCertListener(
			OnQRCodeCertResultListener listener) {
		qrcode_listener = listener;
	}
	
	public void sendDeviceId(byte[] devid) 
			throws IOException {
		buffer[0] = UavAuthorizationConf
				.UAVC_RESQPROTO_REQ.resq_req_devid;
		System.arraycopy(devid, 0, buffer, 1, devid.length);
		out_stream.write(buffer, 0, devid.length + 1);
	}

	public void sendQRCodeCert(byte[] qrcode) 
			throws IOException {
		buffer[0] = UavAuthorizationConf.UAVC_RESQPROTO_REQ.resq_req_qrcode;
		System.arraycopy(qrcode, 0, buffer, 1, qrcode.length);
		out_stream.write(buffer, 0, qrcode.length + 1);

		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					in_stream.read(buffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				boolean result = false;

				if (buffer[0] == UavAuthorizationConf
						.UAVC_RESQPROTO_REP.resq_rep_ready) {
					result = true;
					is_auth = true;
					
				} else if (buffer[0] == UavAuthorizationConf
						.UAVC_RESQPROTO_REP.resq_rep_mismatch) {
					result = false;
					is_auth = false;
				}

				qrcode_listener.onQRCodeCertResult(result);

			}
		}).start();
	}
		
	public boolean isConnected(){
		return is_connected;
	}
	
	public boolean isAuth(){
		return is_auth;
	}
}
