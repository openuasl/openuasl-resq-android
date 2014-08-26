package openuasl.resq.android.uavcontrol;

import java.util.ArrayList;

import android.util.Log;

import com.ezio.multiwii.mw.MultiWii230;
import com.ezio.multiwii.waypoints.Waypoint;

import communication.Communication;

public class MultiWii230_EX extends MultiWii230 {

	public MultiWii230_EX(Communication bt) {
		super(bt);		
	}

	@Override
	public void evaluateCommand(byte cmd, int dataSize) {
		//super.evaluateCommand(cmd, dataSize);
		
		int i;
		int icmd = (int) (cmd & 0xFF);
		switch (icmd) {
		case MSP_IDENT:
			version = read8();
			multiType = read8();
			MSPversion = read8(); // MSP version
			multiCapability = read32();// capability
			if ((multiCapability & 1) > 0)
				multi_Capability.RXBind = true;
			if ((multiCapability & 4) > 0)
				multi_Capability.Motors = true;
			if ((multiCapability & 8) > 0)
				multi_Capability.Flaps = true;

			if ((multiCapability & 16) > 0)
				multi_Capability.Nav = true;

			if ((multiCapability & 0x80000000) > 0)
				multi_Capability.ByMis = true;

			break;

		case MSP_STATUS:
			cycleTime = read16();
			i2cError = read16();
			SensorPresent = read16();
			mode = read32();
			confSetting = read8();

			if ((SensorPresent & 1) > 0)
				AccPresent = 1;
			else
				AccPresent = 0;

			if ((SensorPresent & 2) > 0)
				BaroPresent = 1;
			else
				BaroPresent = 0;

			if ((SensorPresent & 4) > 0)
				MagPresent = 1;
			else
				MagPresent = 0;

			if ((SensorPresent & 8) > 0)
				GPSPresent = 1;
			else
				GPSPresent = 0;

			if ((SensorPresent & 16) > 0)
				SonarPresent = 1;
			else
				SonarPresent = 0;

			for (i = 0; i < CHECKBOXITEMS; i++) {
				if ((mode & (1 << i)) > 0)
					ActiveModes[i] = true;
				else
					ActiveModes[i] = false;

			}

			break;
		case MSP_RAW_IMU:

			ax = read16();
			ay = read16();
			az = read16();

			gx = read16() / 8;
			gy = read16() / 8;
			gz = read16() / 8;

			magx = read16() / 3;
			magy = read16() / 3;
			magz = read16() / 3;
			break;

		case MSP_SERVO:
			for (i = 0; i < 8; i++)
				servo[i] = read16();
			break;
		case MSP_MOTOR:
			for (i = 0; i < 8; i++)
				mot[i] = read16();
			if (multiType == SINGLECOPTER)
				servo[7] = mot[0];
			if (multiType == DUALCOPTER) {
				servo[7] = mot[0];
				servo[6] = mot[1];
			}
			break;
		case MSP_RC:
			rcRoll = read16();
			rcPitch = read16();
			rcYaw = read16();
			rcThrottle = read16();
			rcAUX1 = read16();
			rcAUX2 = read16();
			rcAUX3 = read16();
			rcAUX4 = read16();			
			Log.d("aaa", "CL:" 
					+ String.valueOf(rcRoll) + " " 
					+ String.valueOf(rcPitch) + " " 
					+ String.valueOf(rcYaw) + " " 
					+ String.valueOf(rcThrottle) + " " 
					+ String.valueOf(rcAUX1) + " "
					+ String.valueOf(rcAUX2) + " "
					+ String.valueOf(rcAUX3) + " " 
					+ String.valueOf(rcAUX4));
			break;
		case MSP_RAW_GPS:
			GPS_fix = read8();
			GPS_numSat = read8();
			GPS_latitude = read32();
			GPS_longitude = read32();
			GPS_altitude = read16();
			GPS_speed = read16();
			GPS_ground_course = read16();
			break;
		case MSP_COMP_GPS:
			GPS_distanceToHome = read16();
			GPS_directionToHome = read16();
			GPS_update = read8();
			break;
		case MSP_ATTITUDE:
			angx = read16() / 10;
			angy = read16() / 10;
			head = read16();
			break;
		case MSP_ALTITUDE:
			alt = ((float) read32() / 100) ;
			vario = read16();
			break;
		case MSP_ANALOG:
			bytevbat = read8();
			pMeterSum = read16();
			rssi = read16();
			amperage = read16();
			break;
		case MSP_RC_TUNING:
			byteRC_RATE = read8();
			byteRC_EXPO = read8();
			byteRollPitchRate = read8();
			byteYawRate = read8();
			byteDynThrPID = read8();
			byteThrottle_MID = read8();
			byteThrottle_EXPO = read8();
			break;
		case MSP_ACC_CALIBRATION:
			break;
		case MSP_MAG_CALIBRATION:
			break;
		case MSP_PID:
			for (i = 0; i < PIDITEMS; i++) {
				byteP[i] = read8();
				byteI[i] = read8();
				byteD[i] = read8();
			}
			break;
		case MSP_BOX:
			for (i = 0; i < CHECKBOXITEMS; i++) {
				activation[i] = read16();
				for (int aa = 0; aa < 12; aa++) {
					if ((activation[i] & (1 << aa)) > 0)
						Checkbox[i][aa] = true;
					else
						Checkbox[i][aa] = false;
				}
			}

			break;
		case MSP_BOXNAMES:
			BoxNames = new String(inBuf, 0, dataSize).split(";");
			Log.d("aaa", new String(inBuf, 0, dataSize));
			for (String s : BoxNames) {
				Log.d("aaa", s);
			}
			init();
			break;
		case MSP_PIDNAMES:
			PIDNames = new String(inBuf, 0, dataSize).split(";");
			break;

		case MSP_SERVO_CONF:
			// min:2 / max:2 / middle:2 / rate:1
			for (i = 0; i < 8; i++) {
				ServoConf[i].Min = read16();
				ServoConf[i].Max = read16();
				ServoConf[i].MidPoint = read16();
				ServoConf[i].Rate = read8();
			}
			break;
		case MSP_MISC:
			intPowerTrigger = read16(); // a

			minthrottle = read16();// b
			maxthrottle = read16();// c
			mincommand = read16();// d
			failsafe_throttle = read16();// e
			ArmCount = read16();// f
			LifeTime = read32();// g
			mag_decliniation = read16() / 10f;// h

			vbatscale = read8();// i
			vbatlevel_warn1 = (float) (read8() / 10.0f);// j
			vbatlevel_warn2 = (float) (read8() / 10.0f);// k
			vbatlevel_crit = (float) (read8() / 10.0f);// l
			if (ArmCount < 1)
				Log_Permanent_Hidden = true;
			break;

		case MSP_MOTOR_PINS:
			for (i = 0; i < 8; i++) {
				byteMP[i] = read8();
			}
			break;
		case MSP_DEBUG:
			debug1 = read16();
			debug2 = read16();
			debug3 = read16();
			debug4 = read16();
			break;
		case MSP_DEBUGMSG:
			while (dataSize-- > 0) {
				char c = (char) read8();
				if (c != 0) {
					DebugMSG += c;
				}
			}
			break;
		case MSP_WP:
			Waypoint WP = new Waypoint();
			WP.Number = read8();
			WP.Lat = read32();
			WP.Lon = read32();
			WP.Altitude = read32();
			WP.Heading = read16();
			WP.TimeToStay = read16();
			WP.NavFlag = read8();

			Waypoints[WP.Number] = WP;

			Log.d("aaa", "MSP_WP (get) " + String.valueOf(WP.Number) + "  " + String.valueOf(WP.Lat) + "x" + String.valueOf(WP.Lon) + " " + String.valueOf(WP.Altitude) + " " + String.valueOf(WP.NavFlag));
			break;

		case MSP_SET_RAW_RC:
			rcRoll = read16();
			rcPitch = read16();
			rcYaw = read16();
			rcThrottle = read16();
			rcAUX1 = read16();
			rcAUX2 = read16();
			rcAUX3 = read16();
			rcAUX4 = read16();
			Log.d("aaa", "RC:" 
					+ String.valueOf(rcRoll) + " " 
					+ String.valueOf(rcPitch) + " " 
					+ String.valueOf(rcYaw) + " " 
					+ String.valueOf(rcThrottle) + " " 
					+ String.valueOf(rcAUX1) + " "
					+ String.valueOf(rcAUX2) + " "
					+ String.valueOf(rcAUX3) + " " 
					+ String.valueOf(rcAUX4));
			break;
		default:
			Log.e("aaa", "Error command - unknown replay " + String.valueOf(icmd));

		}
	}
	
	@Override
	public void SendRequestMSP_SET_RAW_RC(int[] channels8) {
		ArrayList<Character> payload = new ArrayList<Character>();
		for (int i = 0; i < 8; i++) {
			payload.add((char) (channels8[i] & 0xFF));
			payload.add((char) ((channels8[i] >> 8) & 0xFF));
		}

		sendRequestMSP(requestMSP(MSP_SET_RAW_RC, payload.toArray(new Character[payload.size()])));

	}
}
