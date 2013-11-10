package me.pjq.pushup;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pjq on 11/9/13.
 */
public class RecordItem {
    private String date;
    private String millionsecond;

    private int count;

    public RecordItem(JSONObject jsonObject) {
        if (null == jsonObject) {
            return;
        }

        date = jsonObject.optString("date");
        count = jsonObject.optInt("count");
        millionsecond = jsonObject.optString("millionsecond");
    }

    public RecordItem(String millionsecond, String date, int count) {
        this.millionsecond = millionsecond;
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

    public String getMillionsecond() {
        return millionsecond;
    }

    public void setMillionsecond(String millionsecond) {
        this.millionsecond = millionsecond;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("date", date);
            jsonObject.put("count", count);
            jsonObject.put("millionsecond", millionsecond);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public RecordItem add(RecordItem item) {
        this.count = item.getCount() + this.count;

        return this;
    }
}
