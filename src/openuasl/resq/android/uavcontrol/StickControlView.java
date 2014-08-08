package openuasl.resq.android.uavcontrol;

import openuasl.resq.android.app.ResquerApp;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.ezio.multiwii.radio.Stick2View;

public class StickControlView extends Stick2View {

	private ResquerApp app;
	
	// false : RollPitchController
	public boolean throttle;
	
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
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
			float px = cvtXPosToValue(event.getX());
			float py = cvtYPosToValue(event.getY());
			Log.i("position", Float.toString(px)+","+Float.toString(py)
					+" // " +Float.toString(event.getX())+","+Float.toString(event.getY()));
			
			SetPosition(px,py);
		
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
