package openuasl.resq.android.ucstream;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import android.util.Log;

import openuasl.resq.android.auth.UavAuthorizationClient;

public class UavCameraStreamer extends UavAuthorizationClient{

	private static UavCameraStreamer instance = null;
	private OnStartStreammingListener start_listener = null;
	private FrameBuffer frame_buffer = new FrameBuffer();
	private ArrayList<byte[]> jpeg_buffer = new ArrayList<byte[]>();
	
	private boolean isStarting = false;
	
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
		super.Connect();
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
					isStarting = false;

					if (buffer[0] == UavCameraStreamerConf
							.IMGS_RESQPROTO_REP.resq_rep_start) {
						isStarting = true;
					}

					start_listener.onStartStreamming(isStarting);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void startStramming(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(isAuth()){
					try {
						readJpegStream();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}).start();
	}
	
	public byte[] receive1Frame(){
		
		if(frame_buffer.isEmpty()){
			return null;
		}
		
		return frame_buffer.get();
	}
	
	private void readJpegStream() throws IOException{
		int bytes_len = 0;
		byte[] recv_buffer = new byte[UavCameraStreamerConf.imgbuf_size];
		
		bytes_len = in_stream.read(recv_buffer);
		
		while(recv_buffer[0] != UavCameraStreamerConf
				.IMGS_RESQPROTO_REP.resq_rep_imgseg){
			in_stream.read(recv_buffer);
		}
		
		int total_length = 0, total_recv = 0;
		// little endian
		total_recv |= (recv_buffer[5] & 0xFF) << 24;
		total_recv |= (recv_buffer[6] & 0xFF) << 16;
		total_recv |= (recv_buffer[7] & 0xFF) << 8;
		total_recv |= (recv_buffer[8] & 0xFF);
				
		total_length = total_recv;
				
		while(total_recv > 0){
			bytes_len = in_stream.read(recv_buffer);
			byte[] tmp = new byte[bytes_len];
			System.arraycopy(recv_buffer, 0, tmp, 0, bytes_len);
			jpeg_buffer.add(tmp);
			total_recv -= bytes_len;
		}
				
		int count = jpeg_buffer.size();
		byte[] image_data = new byte[total_length];

		for(int i=0; i < count; i++){
			byte[] tmp = jpeg_buffer.get(i);
			System.arraycopy(tmp, 0, image_data, 
					i*UavCameraStreamerConf.imgbuf_size, tmp.length);
		}
		
		jpeg_buffer.clear();
		
		frame_buffer.add(image_data);
	}
	
	public boolean sendStopStreamming(){
		buffer[0] = UavCameraStreamerConf
				.IMGS_RESQPROTO_REQ.resq_req_stop;
		
		try {
			out_stream.write(buffer,0,1);
		} catch (IOException e) {
			// test
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
}
