package nfc.org.nfctest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import nfc.org.nfctest.entity.card;
import nfc.org.nfctest.lib.BasicURL;
import nfc.org.nfctest.lib.HttpConnect;
import nfc.org.nfctest.lib.JSON;
import nfc.org.nfctest.lib.TextRecord;

public class ActiveActivity extends Activity {

    private String stuId;
    private Intent preIntent;
    private Tag tag;
    private Ndef ndef;
    private card newCard;
    final private String URL = BasicURL.getBasicURL() + "card/add/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        //toastInfo(ndef.getType());
        activeTag();
    }


    private void init() {
        preIntent = getIntent();
        stuId = preIntent.getStringExtra("stuId");

        tag = preIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        ndef = Ndef.get(tag);
        newCard = new card();
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

    //生成新card
    private void activeTag() {
        String content = readTag();
        Log.i("content", content + "lalal");

        if (content == null || content.equals("")) {
            Log.i("if", "1");

            new Thread() {
                public void run() {

                    newCard.setStuId(stuId);
                    newCard.setBalance((long) 0);
                    newCard.setStatus(1);

                    JSONObject jsonObject = new JSON(newCard).Object2JSON();
                    JSONObject result = new HttpConnect(URL, jsonObject).doHttpClientPost();
                    int cardId = 0;

                    try {
                        cardId = Integer.parseInt(result.getString("msg").trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i("str", String.valueOf(cardId));
                    newCard.setId(cardId);
                    Log.i("Integer Str", String.valueOf(cardId));
                    JSON json = new JSON(newCard);
                    Log.i("writeContent", newCard.getId() + "mama");
                    String writeContent = json.Object2JSON().toString();
                    Log.i("write", writeContent);
                    NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{
                            createTextRecord(writeContent)
                    });
                    writeTag(ndefMessage);
                }
            }.start();
        } else {
            try {
                Log.i("if", "2");
                JSONObject jsonObject = new JSONObject(content);
                int id = jsonObject.getInt("id");
                String stu_id = jsonObject.getString("stuId");
                toastInfo("此卡已激活,卡号是:" + id + "\n卡主的学号是:" + stu_id);
            } catch (JSONException e) {
                NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{
                        createTextRecord("")
                });
                writeTag(ndefMessage);
            }
        }
    }

    private void toastInfo(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
