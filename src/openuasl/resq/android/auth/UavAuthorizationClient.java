package openuasl.resq.android.auth;

import android.annotation.SuppressLint;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class UavAuthorizationClient {

	private boolean is_connected = false;
	private boolean is_auth = false;
	
	private String server_ip = "";
	private int server_port = 0;

	private OnQRCodeCertResultListener qrcode_listener = null;
	

	protected SSLSocket socket;
	protected InputStream in_stream;
	protected OutputStream out_stream;
	protected byte[] buffer = new byte[UavAuthorizationConf.netbuf_size];

	protected UavAuthorizationClient(String ip, int port) {
		server_ip = ip;
		server_port = port;
	}

	@SuppressLint("TrulyRandom") 
	SSLSocketFactory getDummySocketFactory() 
			throws KeyManagementException, NoSuchAlgorithmException{
		
		SSLContext ctx = SSLContext.getInstance("SSL");
		ctx.init(null, new TrustManager[]{new DummyTrustManager()}, new SecureRandom());
				
		return ctx.getSocketFactory();
	}
	
	protected void Connect() 
			throws UnknownHostException, IOException {

		SSLSocketFactory f = null;

		/* use x509 certificate for test */
		try {
			f = getDummySocketFactory();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* use x509 certificate for test */

		socket = (SSLSocket) f.createSocket(server_ip, server_port);
		socket.startHandshake();

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

				if(qrcode_listener != null)
					qrcode_listener.onQRCodeCertResult(result);

			}
		}).start();
	}
	
	public void Disconnect() throws IOException{
		socket.close();
	}
		
	public boolean isConnected(){
		return is_connected;
	}
	
	public boolean isAuth(){
		return is_auth;
	}
}
