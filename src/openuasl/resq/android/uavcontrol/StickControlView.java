package openuasl.resq.android.uavcontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.ezio.multiwii.radio.Stick2View;

public class StickControlView extends Stick2View {

	private OnRawRCSetListener listener;
	
	// false : RollPitchController
	public boolean throttle;
	public float th_y;
	
	private int pos_center;
	private double pos_range;
	
	
	public StickControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		throttle = false;
		pos_center = 200 / 2;
		pos_range = pos_center * Math.sin(Math.toRadians(45));
		
	}
	
	@Override
	protected void onDraw(Canvas c) {		
		super.onDraw(c);
		
		invalidate();
	}
	
	public interface OnRawRCSetListener{
		void onRawRCSetEvent(float x, float y);
	};
	
	public void setOnRawRCSetListener(OnRawRCSetListener listener){
		this.listener = listener;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		int action = event.getAction();
		
		switch(action){
		case MotionEvent.ACTION_UP:
			
			if(listener != null){
				if(this.throttle){
					listener.onRawRCSetEvent(1500, th_y);
					SetPosition(1500,th_y);
				}else{
					listener.onRawRCSetEvent(1500, 1500);
					SetPosition(1500,1500);
				}
			}
						
			return false;
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_DOWN:
			float px = cvtXPosToValue(event.getX());
			th_y = cvtYPosToValue(event.getY());
						
			SetPosition(px,th_y);
			
			if(listener != null)
				listener.onRawRCSetEvent(px, th_y);
			break;
		}
				
		return true;
	}
	
	private int cvtXPosToValue(float x){
		
		if(x < pos_center - pos_range){
			return 1000;
		}else if(x > pos_center + pos_range){
			return 2000;
		}
		
		return (int)((x - (pos_center - pos_range)) * (1000/(2*pos_range)) + 1000);
	}
	
	private int cvtYPosToValue(float y){
		
		if(y > pos_center + pos_range){
			return 1000;
		}else if(y < pos_center - pos_range){
			return 2000;
		}
		
		return (int)(((pos_center + pos_range) - y) * (1000/(2*pos_range)) + 1000);
	}
}
