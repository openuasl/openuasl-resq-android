package openuasl.resq.android.ucstream;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import openuasl.resq.android.net.UavAuthorizationClient;

public class UavCameraStreamer extends UavAuthorizationClient{

	private static UavCameraStreamer instance = null;
	private OnStartStreammingListener start_listener = null;
	private ArrayList<byte[]> jpeg_buffer = new ArrayList<byte[]>();
	
	private UavCameraStreamer(String ip, int port) {
		super(ip, port);
	}
	
	public static UavCameraStreamer getInstance(){
		if(instance == null){
			instance = new UavCameraStreamer(
					UavCameraStreamerConf.server_ip, 
					UavCameraStreamerConf.server_port);
		}
		
		return instance;
	}
	
	public void setStartStreammingListener(
			OnStartStreammingListener listener) {
		start_listener = listener;
	}
	
	public void connectServer() 
			throws UnknownHostException, IOException{
		super.connectServer(false);
	}
	
	public void sendReadyForStreamming() 
			throws IOException{	
		if(!isAuth())
			throw new IOException("image streamming is not ready.");

		buffer[0] = UavCameraStreamerConf.IMGS_RESQPROTO_REQ.resq_req_ready;

		out_stream.write(buffer, 0, 1);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					in_stream.read(buffer);
					boolean result = false;

					if (buffer[0] == UavCameraStreamerConf
							.IMGS_RESQPROTO_REP.resq_rep_start) {
						result = true;
					}

					start_listener.onStartStreamming(result);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public byte[] receive1Frame() throws IOException{
		int bytes_len = 0;
		byte[] recv_buffer = new byte[UavCameraStreamerConf.imgbuf_size];
		
		bytes_len = in_stream.read(recv_buffer);
		
		while(recv_buffer[0] != UavCameraStreamerConf
				.IMGS_RESQPROTO_REP.resq_rep_imgseg){
			in_stream.read(recv_buffer);
		}
		
		int total_length = 0;
		// little endian
		total_length |= recv_buffer[1] << 24;
		total_length |= recv_buffer[2] << 16;
		total_length |= recv_buffer[3] << 8;
		total_length |= recv_buffer[4];
		
		while(total_length > 0){
			bytes_len = in_stream.read(recv_buffer);
			jpeg_buffer.add(recv_buffer);
			total_length -= bytes_len;
		}
				
		int count = jpeg_buffer.size();
		byte[] image_data = new byte[
		       (count - 1) * UavCameraStreamerConf.imgbuf_size
				+ jpeg_buffer.get(count - 1).length];

		for(int i=0; i < count; i++){
			byte[] tmp = jpeg_buffer.get(i);
			System.arraycopy(tmp, 0, image_data, 
					i*UavCameraStreamerConf.imgbuf_size, tmp.length);
		}
		
		jpeg_buffer.clear();
		
		return image_data;
	}
	
	public boolean sendStopStreamming(){
		buffer[0] = UavCameraStreamerConf
				.IMGS_RESQPROTO_REQ.resq_req_stop;
		
		try {
			out_stream.write(buffer,0,1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
}
