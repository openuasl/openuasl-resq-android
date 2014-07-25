package openuasl.resq.android.ucstream;

public class UavCameraStreamerConf {

	public static final String server_ip =  "210.118.69.45";
	public static final int server_port = 54321;
	
	public static final int imgbuf_size = 4096;
	

	public static class IMGS_RESQPROTO_REQ {
		public final static byte resq_req_ready = 0x33;
		public final static byte resq_req_stop = 0x34;
	};

	public static class IMGS_RESQPROTO_REP {
		public final static byte resq_rep_start = 0x43;
		public final static byte resq_rep_imgseg = 0x44;
	};
	
}
