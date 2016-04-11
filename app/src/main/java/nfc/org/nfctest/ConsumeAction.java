package nfc.org.nfctest;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import nfc.org.nfctest.lib.HttpConnect;
import nfc.org.nfctest.lib.JSON;
import nfc.org.nfctest.lib.TextRecord;

public class ConsumeAction extends ActionBarActivity {

    private Intent preIntent;
    private String stuId;
    private Long pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preIntent = getIntent();
        stuId = preIntent.getStringExtra("stuId");
        pay = preIntent.getLongExtra("pay", 0);
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

        NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
        return ndefRecord;
    }

    private boolean writeTag(NdefMessage ndefMessage, Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);
            ndef.connect();
            ndef.writeNdefMessage(ndefMessage);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
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
            return textRecord.getText();
        }
        return null;
    }



    private void toastInfo(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
