package nfc.org.nfctest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import nfc.org.nfctest.lib.BasicURL;
import nfc.org.nfctest.entity.student;
import nfc.org.nfctest.lib.HttpConnect;
import nfc.org.nfctest.lib.JSON;

public class RegActivity extends Activity {

    private Handler handler;
    private EditText edt_stu_id, edt_net_pwd, edt_stu_name, edt_stu_pwd, edt_stu_phone;
    private Button btn_reg, btn_to_login;
    private String id, net_pwd, name, pwd, phone;

    final private String URL = BasicURL.getBasicURL() + "student/add/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        init();
        btn_reg.setOnClickListener(new regOnClick());
        btn_to_login.setOnClickListener(new logOnClick());
    }

    private void init() {
        handler = new Handler();
        edt_stu_id = (EditText) findViewById(R.id.reg_edt_stu_id);
        edt_net_pwd = (EditText) findViewById(R.id.reg_edt_net_pwd);
        edt_stu_name = (EditText) findViewById(R.id.reg_edt_stu_name);
        edt_stu_pwd = (EditText) findViewById(R.id.reg_edt_stu_pwd);
        edt_stu_phone = (EditText) findViewById(R.id.reg_edt_stu_phone);
        btn_reg = (Button) findViewById(R.id.btn_reg);
        btn_to_login = (Button) findViewById(R.id.btn_to_login);
    }

    private class regOnClick implements View.OnClickListener {
        public void onClick(View v) {
            id = edt_stu_id.getText().toString().trim();
            net_pwd = edt_net_pwd.getText().toString().trim();
            name = edt_stu_name.getText().toString().trim();
            pwd = edt_stu_pwd.getText().toString().trim();
            phone = edt_stu_phone.getText().toString().trim();

            if (id == null || id.length() == 0) {
                toastInfo("学号不能为空");
            }
            if (net_pwd == null || net_pwd.length() == 0) {
                toastInfo("上网密码不能为空");
            }
            if (name == null || name.length() == 0) {
                toastInfo("昵称不能为空");
            }
            if (pwd == null || pwd.length() == 0) {
                toastInfo("密码不能为空");
            }
            if (phone == null || phone.length() == 0) {
                toastInfo("手机号不能为空");
            }
            //验证学号和上网密码

            final student student = new student();
            student.setId(id);
            student.setName(name);
            student.setPassword(pwd);
            student.setPhone(phone);
            student.setStatus(1);

            new Thread() {
                public void run() {

                    JSONObject jsonObject = new JSON(student).Object2JSON();
                    final JSONObject result = new HttpConnect(URL, jsonObject).doHttpClientPost();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                toastInfo(result.getString("msg"));
                                toActivity(LoginActivity.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }.start();
        }
    }

    private class logOnClick implements View.OnClickListener {
        public void onClick(View v) {
            toActivity(LoginActivity.class);
        }
    }

    private void toActivity(Class tClass) {
        Intent intent = new Intent();
        intent.setClass(this, tClass);
        startActivity(intent);
        this.finish();
    }

    private void toastInfo(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
