package nfc.org.nfctest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConsumeActivity extends Activity {

    private String stuId;
    private EditText consume_edt_price;
    private Button btn_pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consume);

        init();
    }

    private void init(){
        stuId = getIntent().getStringExtra("stuId");
        consume_edt_price = (EditText) this.findViewById(R.id.consume_edt_price);
        btn_pay = (Button)this.findViewById(R.id.btn_pay);
        btn_pay.setOnClickListener(new payOnClick());
    }

    private class payOnClick implements View.OnClickListener{
        public void onClick(View v){
            toActivity(HintActivity.class);
        }
    }

    private void toActivity(Class tClass) {
        Long pay = Long.parseLong(consume_edt_price.getText().toString());

        Intent intent = new Intent();
        intent.putExtra("stuId", stuId);
        intent.putExtra("pay", pay);
        intent.putExtra("activityName","ConsumeActivity");
        intent.setClass(this, tClass);
        startActivity(intent);
        this.finish();
    }
}
