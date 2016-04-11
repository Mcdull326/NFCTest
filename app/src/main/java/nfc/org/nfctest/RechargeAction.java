package nfc.org.nfctest;

import android.app.Activity;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import nfc.org.nfctest.entity.record;
import nfc.org.nfctest.lib.BasicURL;
import nfc.org.nfctest.lib.HttpConnect;
import nfc.org.nfctest.lib.JSON;
import nfc.org.nfctest.lib.TextRecord;

public class RechargeAction extends Activity {

    private Handler handler;
    private Intent preIntent;
    private Tag tag;
    private Ndef ndef;
    private String stuId;
    private Long recharge;
    final private String URL = BasicURL.getBasicURL() + "record/recharge/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        try {
            consume();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        handler = new Handler();
        preIntent = getIntent();
        tag = preIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        ndef = Ndef.get(tag);
        stuId = preIntent.getStringExtra("stuId");
        recharge = preIntent.getLongExtra("recharge", 0);
    }

    //生成新card
    private void consume() throws JSONException {
        final String content = readTag();
        final JSONObject cardJSON = new JSONObject(content);
        final Long newBalance = cardJSON.getLong("balance") + recharge;

        if (content == null || content.equals("")) {
            toastInfo("此卡为空,无法识别");
        } else if (cardJSON.getInt("status") != 1) {
            toastInfo("此卡已注销,无法充值");

        } else {

            new Thread() {
                public void run() {

                    try {
                        record newRecord = new record();
                        newRecord.setStuId(stuId);
                        newRecord.setCardId(cardJSON.getInt("id"));
                        newRecord.setAmount(recharge);
                        newRecord.setLocationId(1);
                        newRecord.setStatus(2);

                        Log.i("consumeJSON", "before");
                        JSONObject requestJSON = new JSON(newRecord).Object2JSON();
                        Log.i("consumeJSON",requestJSON.toString());

                        final JSONObject result = new HttpConnect(URL, requestJSON).doHttpClientPost();

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    toastInfo(result.getString("msg"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                toActivity(MainActivity.class);
                            }
                        });

                        //new card content
                        cardJSON.put("balance", newBalance);
                        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{
                                createTextRecord(cardJSON.toString())
                        });
                        writeTag(ndefMessage);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }


    private NdefRecord createTextRecord(String text) {
        byte[] langBytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = Charset.forName("UTF-8");
        byte[] textBytes = text.getBytes(utfEncoding);
        int utfBit = 0;
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    private boolean writeTag(NdefMessage ndefMessage) {
        try {
            ndef.connect();
            ndef.writeNdefMessage(ndefMessage);
            return true;
        } catch (IOException | FormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String readTag() {
        Parcelable[] rawMessages = preIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage ndefMessages[] = null;
        //int contentSize = 0;

        if (rawMessages != null) {
            ndefMessages = new NdefMessage[rawMessages.length];
            for (int i = 0; i < rawMessages.length; i++) {
                ndefMessages[i] = (NdefMessage) rawMessages[i];
                //contentSize += ndefMessages[i].toByteArray().length;
            }
        }

        if (ndefMessages != null) {
            NdefRecord record = ndefMessages[0].getRecords()[0];
            TextRecord textRecord = TextRecord.parse(record);
            if (textRecord != null) {
                return textRecord.getText();
            }
        }
        return null;
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
