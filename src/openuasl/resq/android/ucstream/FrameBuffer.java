package openuasl.resq.android.ucstream;

import android.util.Log;

public class FrameBuffer {

	private byte[] buffer;
	private int frame_count;
		
	public FrameBuffer() {
		buffer = null;
		frame_count = 0;
	}

	public boolean isEmpty() {
		return buffer == null? true : false;
	}

	public void add(byte[] item) {
		buffer = item;
		frame_count++;
	}

	public byte[] get() {

		if (isEmpty())
			return null;

		Log.i("lost frame : ", Integer.toString(frame_count));
		frame_count = 0;
		
		byte[] r = buffer;
		buffer = null;
		
		return r;
	}

}