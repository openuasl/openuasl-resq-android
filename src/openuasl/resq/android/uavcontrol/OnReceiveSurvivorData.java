package openuasl.resq.android.uavcontrol;

public interface OnReceiveSurvivorData {
	void onReceiveSurvivorData(String name, String mac, int rssi);
}
