package nfc.org.nfctest.lib;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by zhaozeqing on 16/3/27.
 */
public class JSON {

    private Field[] fields;
    private int fieldsLength;
    private Object obj;

    public JSON(Object obj) {
        this.obj = obj;
        fields = obj.getClass().getDeclaredFields();
        fieldsLength = fields.length;
    }

    public JSONObject Object2JSON() {
        JSONObject jsonObject = new JSONObject();
        String fieldName;

        for (int i = 0; i < fieldsLength; i++) {
            fieldName = fields[i].getName();
            try {
                jsonObject.put(fieldName, getFieldValueByName(fieldName, obj));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public Object JSON2Object(String json) {
        JSONObject jsonObject;
        String fieldName;

        try {
            jsonObject = (JSONObject) new JSONTokener(json).nextValue();
            for (int i = 0; i < fieldsLength; i++) {
                fieldName = fields[i].getName();
                if (!jsonObject.isNull(fieldName)) {
                    setFieldValue(fieldName, jsonObject.get(fieldName), obj);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private Object getFieldValueByName(String fieldName, Object obj) {
        Field f = null;
        Object value = null;
        try {
            f = obj.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
        try {
            value = f.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }

    private void setFieldValue(String fieldName, Object value, Object obj) {
        Field f = null;
        try {
            f = obj.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
        try {
            if (value != null) {
                if (fieldName.equals("balance")) {
                    value = Long.parseLong(value.toString());
                    f.set(obj, value);
                } else {
                    f.set(obj, value);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
