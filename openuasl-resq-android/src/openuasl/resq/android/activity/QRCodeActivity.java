package openuasl.resq.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
//import com.google.zxing.client.android.Result;
public class QRCodeActivity extends CaptureActivity {
	  public static final String EXT_BARCODE = "EXT_BARCODE";
	  public static String qrvalueresult = new String();
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	    }
	 
	    public void handleDecode(com.google.zxing.Result rawResult, 
	    		Bitmap barcode, float scaleFactor) {
	         super.handleDecode(rawResult, barcode, scaleFactor);
	       setResult(RESULT_OK, new Intent().putExtra(EXT_BARCODE, rawResult.getText()));
	    	Toast.makeText(this.getApplicationContext(), "Scanned code "+rawResult.getText(), Toast.LENGTH_LONG);
	    	Log.i("qrcode",rawResult.getText());
	    	qrvalueresult=rawResult.getText();
	    	
	    	
	    	finish();
	    }
}
