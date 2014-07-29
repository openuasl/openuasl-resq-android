package openuasl.resq.android.ucstream;

public class FrameBuffer {

	private int front;
	private int rear;
	private int maxSize;
	private byte[][] buffer;

	public FrameBuffer() {

		this.front = this.rear = 0;
		this.maxSize = 2 + 1;
		this.buffer = new byte[this.maxSize][];
	}

	public boolean isEmpty() {
		return front == rear;
	}

	public void add(byte[] item) {
		rear = (rear + 1) % maxSize;
		buffer[rear] = item;
	}

	public byte[] get() {

		if (isEmpty())
			return null;

		front = (front + 1) % maxSize;
		return buffer[front];
	}

}