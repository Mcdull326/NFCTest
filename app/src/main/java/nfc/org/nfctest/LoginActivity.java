package nfc.org.nfctest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import nfc.org.nfctest.lib.BasicURL;
import nfc.org.nfctest.lib.HttpConnect;

public class LoginActivity extends Activity {

    private Handler handler;
    private EditText edt_stu_id, edt_stu_pwd;
    private Button btn_login, btn_to_reg;
    private String id, pwd;
    SharedPreferences sharedPreferences;
    final private String URL = BasicURL.getBasicURL() + "student/login/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        handler = new Handler();
        edt_stu_id = (EditText) this.findViewById(R.id.login_edt_stu_id);
        edt_stu_pwd = (EditText) this.findViewById(R.id.login_edt_stu_pwd);
        btn_login = (Button) this.findViewById(R.id.btn_login);
        btn_to_reg = (Button) this.findViewById(R.id.btn_to_reg);
        sharedPreferences = getSharedPreferences("login_share", MODE_PRIVATE);

        btn_login.setOnClickListener(new logOnClick());
        btn_to_reg.setOnClickListener(new regOnClick());
    }


    private boolean checkLogin(String id, String pwd) {
        if (id.length() == 0) {
            toastInfo("学号不能为空");
        } else if (pwd.length() == 0) {
            toastInfo("密码不能为空");
        } else {
            return true;
        }
        return false;
    }

    private class logOnClick implements View.OnClickListener {
        public void onClick(View v) {
            id = edt_stu_id.getText().toString().trim();
            pwd = edt_stu_pwd.getText().toString().trim();

            if (checkLogin(id, pwd)) {

                new Thread() {
                    public void run() {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("id", id);
                            jsonObject.put("pwd", pwd);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final JSONObject result = new HttpConnect(URL, jsonObject).doHttpClientPost();

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String stuStr = result.getString("student");
                                    String cardStr = result.getString("card");

                                    if(result == null || stuStr.equals("")){
                                        toastInfo("用户不存在或密码错误!");
                                        return;
                                    }
                                    Editor editor = sharedPreferences.edit();
                                    editor.putString("student", stuStr);
                                    editor.putString("card", cardStr);
                                    editor.commit();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                toActivity(MainActivity.class);
                            }
                        });
                    }
                }.start();
            }
        }
    }

    private class regOnClick implements View.OnClickListener {
        public void onClick(View v) {
            toActivity(RegActivity.class);
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
