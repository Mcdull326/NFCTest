package nfc.org.nfctest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nfc.org.nfctest.entity.student;
import nfc.org.nfctest.entity.card;
import nfc.org.nfctest.lib.JSON;

public class MainActivity extends Activity {

    private TextView tv_welcome;
    private Button btn_active, btn_consume, btn_loss;
    private SharedPreferences login_shared, main_shared;
    private student stu = new student();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        tv_welcome = (TextView) this.findViewById(R.id.tv_welcome);
        btn_active = (Button) this.findViewById(R.id.btn_active);
        btn_consume = (Button) this.findViewById(R.id.btn_consume);
        btn_loss = (Button) this.findViewById(R.id.btn_loss);
        btn_active.setOnClickListener(new activeOnClick());
        btn_consume.setOnClickListener(new consumeOnClick());
        btn_loss.setOnClickListener(new lossOnClick());

        //MainActivity显示
        login_shared = getSharedPreferences("login_share", MODE_PRIVATE);
        String stuStr = login_shared.getString("student", "");
        String cardStr = login_shared.getString("card", "");
        stu = (student) new JSON(stu).JSON2Object(stuStr);

        String text = "欢迎," + stu.getName() + "\n学号:" + stu.getId();
        if (cardStr.equals("")) {
            btn_active.setVisibility(View.VISIBLE);
            text += "\n您尚未激活卡";
        } else {
            Log.i("card", cardStr);
            card card = new card();
            btn_consume.setVisibility(View.VISIBLE);
            btn_loss.setVisibility(View.VISIBLE);
            card = (card) new JSON(card).JSON2Object(cardStr);
            text += "\n卡内余额为:" + card.getBalance();
        }
        tv_welcome.setText(text);
    }

    //active
    private class activeOnClick implements View.OnClickListener {
        public void onClick(View v) {
            toActivity(HintActivity.class);
        }
    }

    //consume
    private class consumeOnClick implements View.OnClickListener {
        public void onClick(View v) {
            toActivity(ConsumeActivity.class);
        }
    }

    //loss
    private class lossOnClick implements View.OnClickListener {
        public void onClick(View v) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("确认")
                    .setMessage("确定挂失吗？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            toActivity(LossAction.class);
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    private void toActivity(Class tClass) {
        Intent intent = new Intent();
        intent.setClass(this, tClass);
        intent.putExtra("stuId", stu.getId());
        intent.putExtra("activityName", "MainActivity");
        startActivity(intent);
        this.finish();
    }

}
