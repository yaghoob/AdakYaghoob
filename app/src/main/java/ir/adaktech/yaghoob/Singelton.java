package ir.adaktech.yaghoob;

import android.app.Application;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Singelton extends Application {
    public static boolean isJSONValid(String test)
    {
        try {
            new JSONObject(test);
            Log.i("4Yaghoob","Json checker function: OK JSON :)");
            return true;
        } catch(JSONException ex) {
            Log.i("4Yaghoob","Json checker function: NOT JSON :(" + ex.getMessage());
            return false;
        }
    }
}
