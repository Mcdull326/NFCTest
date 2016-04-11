package nfc.org.nfctest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;

public class HintActivity extends Activity {

    private Intent preIntent;
    private SharedPreferences main_shared, consume_shared;
    private String stuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);

        preIntent = getIntent();
        stuId = preIntent.getStringExtra("stuId");
    }


    public void onNewIntent(Intent intent){
        String activityName = preIntent.getStringExtra("activityName");

        if(activityName.equals("MainActivity")){
            //active
            Intent myIntent = new Intent(this, ActiveActivity.class);
            myIntent.putExtra("stuId", stuId);
            myIntent.putExtras(intent);
            myIntent.setAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
            startActivity(myIntent);

        }else if(activityName.equals("ConsumeActivity")){
            //consume
            Long pay = preIntent.getLongExtra("pay", 0);
            Intent myIntent = new Intent(this, ConsumeAction.class);
            myIntent.putExtra("stuId", stuId);
            myIntent.putExtra("pay", pay);
            myIntent.putExtras(intent);
            myIntent.setAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
            startActivity(myIntent);
        }
    }
}
