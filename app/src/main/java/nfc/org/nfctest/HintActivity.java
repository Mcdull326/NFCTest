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

        switch (activityName){
            case "MainActivity":
                //active
                Intent mainIntent = new Intent(this, ActiveActivity.class);
                mainIntent.putExtra("stuId", stuId);
                mainIntent.putExtras(intent);
                mainIntent.setAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
                startActivity(mainIntent);
                break;
            case "ConsumeActivity":
                //consume
                Long pay = preIntent.getLongExtra("pay", 0);
                Intent consumeIntent = new Intent(this, ConsumeAction.class);
                consumeIntent.putExtra("stuId", stuId);
                consumeIntent.putExtra("pay", pay);
                consumeIntent.putExtras(intent);
                consumeIntent.setAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
                startActivity(consumeIntent);
                break;
            case "RechargeActivity":
                //recharge
                Long recharge = preIntent.getLongExtra("recharge", 0);
                Intent rechargeIntent = new Intent(this, RechargeAction.class);
                rechargeIntent.putExtra("stuId", stuId);
                rechargeIntent.putExtra("recharge", recharge);
                rechargeIntent.putExtras(intent);
                rechargeIntent.setAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
                startActivity(rechargeIntent);
                break;
        }
    }
}
