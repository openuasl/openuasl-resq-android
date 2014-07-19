package openuasl.resq.android.net;

public class UavAuthorizationConf {
	
	public static final int netbuf_size = 1024;

	public static class UAVC_RESQPROTO_REQ {
		public final static byte resq_req_devid = 0x31;
		public final static byte resq_req_qrcode = 0x32;
	};

	public static class UAVC_RESQPROTO_REP {
		public final static byte resq_rep_ready = 0x41;
		public final static byte resq_rep_mismatch = 0x42;
	};
}
