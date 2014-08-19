package openuasl.resq.android.activity;

import openuasl.resq.android.R;
import openuasl.resq.android.app.ResquerApp;
import openuasl.resq.android.uavcontrol.StickControlView;
import openuasl.resq.android.uavcontrol.UavControlConf;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.SearchView.OnCloseListener;
import android.widget.TextView;

import com.ezio.multiwii.dashboard.PitchRollView;
import com.ezio.multiwii.dashboard.dashboard3.AltitudeView;
import com.ezio.multiwii.dashboard.dashboard3.HeadingView;
import com.ezio.multiwii.dashboard.dashboard3.HorizonView;
import com.ezio.multiwii.dashboard.dashboard3.VarioView;
import com.ezio.multiwii.waypoints.MapHelperClass;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


public class ControllerActivity extends FragmentActivity{

	ResquerApp app;
	MapHelperClass map_helper;
	RelativeLayout sv_view;
	SurfaceView sv;
	StickControlView ctrl_left;
	StickControlView ctrl_right;
	StickControlView.OnRawRCSetListener ctrl_left_listener;
	StickControlView.OnRawRCSetListener ctrl_right_listener;
	
	// left
	PitchRollView ctrl_pitch;
	PitchRollView ctrl_roll;
	HorizonView ctrl_horizon;
	
	// right
	HeadingView ctrl_heading;
	AltitudeView ctrl_altitude;
	VarioView ctrl_vario;
	
	TextView ctrl_info;
	Thread update_thread;
	
	private final Handler commMW_handler = new Handler();
	//private final Handler commFrsky_handler = new Handler();
	private Handler ui_update_handler = new Handler();
	private boolean stop_update = false;
	
	private long timer = 0;
	private long ui_timer = 0;
	private long center_step = 0;
	private boolean move_map = true;
	
	private int a;
	
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
		a = app.ReverseRoll? -1 : 1;
		initControlViews();
		app.mw.rcThrottle = 980;
		
		update_thread = new Thread(update);
		update_thread.start();
		/*
		new Thread(new Runnable() {	
			@Override
			public void run() {
				app.commMW.Connect(UavControlConf.server_ip, UavControlConf.server_port);
				app.certificateProcess();
				app.commMW.SetHandler(commMW_handler);
				//app.commFrsky.SetHandler(commFrsky_handler);
			}
		}).start();
		*/
	}

	public volatile boolean changed;
	public volatile int roll=1500;
	public volatile int pitch=1500;
	public volatile int yaw=1500;
	public volatile int throttle=1000;
	
	private void initControlViews(){
		ctrl_left = (StickControlView)findViewById(R.id.ctrlui_left);
		ctrl_left_listener = new StickControlView.OnRawRCSetListener() {
			
			@Override
			public void onRawRCSetEvent(float x, float y) {
				yaw = (int)x;
				pitch = (int)y;
				
				changed = true;
			}
		};
		ctrl_left.setOnRawRCSetListener(ctrl_left_listener);
		
		ctrl_right = (StickControlView)findViewById(R.id.ctrlui_right);
		ctrl_right.throttle = true;
		ctrl_right_listener = new StickControlView.OnRawRCSetListener() {
			
			@Override
			public void onRawRCSetEvent(float x, float y) {
				roll = (int)x;
				throttle = (int)y;
				
				changed = true;
			}
		};
		ctrl_right.setOnRawRCSetListener(ctrl_right_listener);		
		
		ctrl_roll = (PitchRollView)findViewById(R.id.ctrlui_roll);
		ctrl_pitch = (PitchRollView)findViewById(R.id.ctrlui_pitch);
		ctrl_pitch.arrow = true;
		ctrl_pitch.init();
		ctrl_horizon = (HorizonView)findViewById(R.id.ctrlui_horizon);
						
		ctrl_heading = (HeadingView)findViewById(R.id.ctrlui_heading);
		ctrl_altitude = (AltitudeView)findViewById(R.id.ctrlui_altitude);
		ctrl_vario = (VarioView)findViewById(R.id.ctrlui_vario);
		
		ctrl_info = (TextView)findViewById(R.id.ctrlui_info);
		
		map_helper = new MapHelperClass(((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap(), 5);
				
		map_helper.map.setOnCameraChangeListener(new OnCameraChangeListener() {
			
			@Override
			public void onCameraChange(CameraPosition position) {
				/*
				if (app.mw.GPS_fix == 1){
					app.MapZoomLevel = (int) position.zoom;
				}else{
					
				}
				*/
				
				app.MapZoomLevel = (int) position.zoom;
			}
		});
	}
	
	private Runnable ui_update = new Runnable() {
		
		@Override
		public void run() {
			
			if(ui_timer < System.currentTimeMillis()){
				ctrl_pitch.Set(app.mw.angy);
				ctrl_roll.Set(app.mw.angx);
				ctrl_info.setText(getInformationString());
				centeringMap();
				ctrl_horizon.Set(-app.mw.angx * a, -app.mw.angy * 1.5f);
				ctrl_altitude.Set(app.mw.alt * 10);
				ctrl_heading.Set(app.mw.head);
				ctrl_vario.Set(app.mw.vario * 0.6f);
				
				ui_timer = System.currentTimeMillis() + app.RefreshRate;
			}
			
			//ctrl_right.SetPosition(app.mw.rcRoll, app.mw.rcThrottle);
			//ctrl_left.SetPosition(app.mw.rcYaw, app.mw.rcPitch);
			
			if (!stop_update)
				ui_update_handler.postDelayed(ui_update, 20);
		}
	};
	
	public void sendRawRCDatas() {
		int channels[] = new int[8];
		channels[0] = roll;
		channels[1] = pitch;
		channels[2] = yaw;
		channels[3] = throttle;
		channels[4] = app.mw.rcAUX1;
		channels[5] = app.mw.rcAUX2;
		channels[6] = app.mw.rcAUX3;
		channels[7] = app.mw.rcAUX4;
		
		app.mw.SendRequestMSP_SET_RAW_RC(channels);
		
		changed = false;
	}
	
	private Runnable update = new Runnable() {
		
		@Override
		public void run() {
			app.commMW.Connect(UavControlConf.server_ip, UavControlConf.server_port);
			app.certificateProcess();
			app.commMW.SetHandler(commMW_handler);

			while(!stop_update){
				app.mw.ProcessSerialData(app.loggingON);
				
				if(timer < System.currentTimeMillis()){
					app.Frequentjobs();
					app.mw.SendRequest(app.MainRequestMethod);
					
					timer = System.currentTimeMillis() + app.RefreshRate;
				}
				
				if(changed)
					sendRawRCDatas();
				
				//app.mw.SendRequestMSP(app.mw.MSP_RC);
				
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	
	private String getInformationString(){
		String s = "";
		s += getString(R.string.Altitude) + ":" + String.valueOf(app.mw.alt) + "\n";
		s += getString(R.string.Battery) + ":" + String.valueOf((float) (app.mw.bytevbat / 10.0)) + "\n";
		s += getString(R.string.Satellites) + ":" + String.valueOf(app.mw.GPS_numSat) + "\n";
		s += getString(R.string.GPS_speed) + ":" + String.valueOf(app.mw.GPS_speed) + "\n";
		s += getString(R.string.CycleTime) + ":" + String.valueOf(app.mw.cycleTime) + "\n";
		
		return s;
	}
	
	private void centeringMap(){
		LatLng copterPositionLatLng = new LatLng(app.mw.GPS_latitude / Math.pow(10, 7), app.mw.GPS_longitude / Math.pow(10, 7));
		map_helper.SetCopterLocation(copterPositionLatLng, app.mw.head, app.mw.alt);
		map_helper.DrawFlightPath(copterPositionLatLng);
		map_helper.PositionHoldMarker.setPosition(new LatLng(app.mw.Waypoints[16].Lat / Math.pow(10, 7), app.mw.Waypoints[16].Lon / Math.pow(10, 7)));
		map_helper.HomeMarker.setPosition(new LatLng(app.mw.Waypoints[0].Lat / Math.pow(10, 7), app.mw.Waypoints[0].Lon / Math.pow(10, 7)));
				
		if (move_map && center_step < System.currentTimeMillis()) {
			if (app.mw.GPS_fix == 1) {
				map_helper.map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(copterPositionLatLng, app.MapZoomLevel, 0, app.mw.head)));
			} else {
				map_helper.map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(app.sensors.PhoneLatitude, app.sensors.PhoneLongitude), app.MapZoomLevel, 0, 0)));
			}
			center_step = System.currentTimeMillis() + app.MapCenterPeriod * 1000;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		stop_update = true;
		ui_update_handler.removeCallbacks(ui_update);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		stop_update = false;
		ui_update_handler.postDelayed(ui_update, 20);
		
	}
}

