package nfc.org.nfctest.lib;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by zhaozeqing on 16/4/2.
 */
public class HttpConnect {

    private String url;
    private JSONObject jsonObject;

    public HttpConnect(String url) {
        this.url = url;
    }

    public HttpConnect(String url, JSONObject jsonObject) {
        this.url = url;
        this.jsonObject = jsonObject;
    }

    public JSONObject doHttpClientGet() {

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public JSONObject doHttpClientPost() {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost;

        try {
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost = new HttpPost(new URI(url));
            httpPost.setEntity(stringEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);

            Log.i("code", String.valueOf(httpResponse.getStatusLine().getStatusCode()));

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());
                Log.i("response", result);
                return new JSONObject(result);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
