package openuasl.resq.android.activity;

import openuasl.resq.android.R;
import openuasl.resq.android.app.ResquerApp;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.RelativeLayout;
import com.ezio.multiwii.radio.*;


public class ControllerActivity extends Activity {

	RelativeLayout sv_view;
	SurfaceView sv;
	Stick2View ctrl_left;
	Stick2View ctrl_right;
	ResquerApp app;
	private final Handler commMW_handler = new Handler();
	private final Handler commFrsky_handler = new Handler();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window win = getWindow();
		win.setContentView(R.layout.controller_surface_view);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout controller_layout = (RelativeLayout) inflater.inflate(
				R.layout.activity_controller, null);

		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		win.addContentView(controller_layout, param);
		
		sv_view = (RelativeLayout)inflater.inflate(R.layout.controller_surface_view, null);
		
		app = (ResquerApp)getApplication();
		
		app.commMW.SetHandler(commMW_handler);
		app.commFrsky.SetHandler(commFrsky_handler);
		
	}

	
	
	
}

