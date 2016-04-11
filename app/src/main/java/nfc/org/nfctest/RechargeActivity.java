package nfc.org.nfctest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RechargeActivity extends Activity {

    private String stuId;
    private EditText recharge_edt_price;
    private Button btn_recharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        init();
    }

    private void init(){
        stuId = getIntent().getStringExtra("stuId");
        recharge_edt_price = (EditText) this.findViewById(R.id.recharge_edt_price);
        btn_recharge = (Button)this.findViewById(R.id.btn_recharge);
        btn_recharge.setOnClickListener(new payOnClick());
    }

    private class payOnClick implements View.OnClickListener{
        public void onClick(View v){
            toActivity(HintActivity.class);
        }
    }

    private void toActivity(Class tClass) {
        Long recharge = Long.parseLong(recharge_edt_price.getText().toString());

        Intent intent = new Intent();
        intent.putExtra("stuId", stuId);
        intent.putExtra("recharge", recharge);
        intent.putExtra("activityName","RechargeActivity");
        intent.setClass(this, tClass);
        startActivity(intent);
        this.finish();
    }
}
