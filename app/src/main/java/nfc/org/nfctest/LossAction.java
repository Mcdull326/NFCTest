package nfc.org.nfctest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import nfc.org.nfctest.lib.BasicURL;
import nfc.org.nfctest.lib.HttpConnect;

public class LossAction extends Activity {

    private Handler handler;
    private String stuId;
    final private String URL = BasicURL.getBasicURL() + "card/loss/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();
        stuId = getIntent().getStringExtra("stuId");
        Log.i("stuId", stuId);

        loss();
    }

    private void loss(){
        new Thread() {
            public void run() {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("stuId", stuId);
                    Log.i("loss stuId", stuId.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final JSONObject result = new HttpConnect(URL, jsonObject).doHttpClientPost();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(result.getString("msg").equals("success")){
                                toastInfo("挂失成功!");
                                toActivity(MainActivity.class);
                            }else{
                                toastInfo("error");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.start();
    }

    private void toActivity(Class tclass) {
        Intent intent = new Intent();
        intent.setClass(this, tclass);
        startActivity(intent);
        this.finish();
    }

    private void toastInfo(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
