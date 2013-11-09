package me.pjq.pushup;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pjq on 11/9/13.
 */
public class RecordItem {
    private String date;

    private int count;

    public RecordItem(JSONObject jsonObject) {
        if (null == jsonObject) {
            return;
        }

        date = jsonObject.optString("date");
        count = jsonObject.optInt("count");
    }

    public RecordItem(String date, int count) {
        this.date = date;
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("date", date);
            jsonObject.put("count", count);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
