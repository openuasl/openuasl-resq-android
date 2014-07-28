package openuasl.resq.android.uavcontrol;

import java.io.IOException;
import java.net.UnknownHostException;

import openuasl.resq.android.net.UavAuthorizationClient;


public class ResquerClient extends UavAuthorizationClient {

	public ResquerClient(String ip, int port) {
		super(ip, port);
	}

	@Override
	public void Connect()
			throws UnknownHostException, IOException {
		super.Connect();
		
	}
	
	public void sendReadyForControl()
			throws IOException{
		
	}
	
	public void write(byte[] arr) throws IOException{
		super.out_stream.write(arr);
	}

	public int read(byte[] arr) throws IOException{
		return super.in_stream.read(arr);
	}
	
}
