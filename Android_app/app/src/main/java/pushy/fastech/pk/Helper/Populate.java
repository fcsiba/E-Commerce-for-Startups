package pushy.fastech.pk.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Populate {

    public static List<Classes> clsList = new ArrayList<>();
    public static List<Sections> secList = new ArrayList<>();
    public static List<Faculty> facList = new ArrayList<>();
    public static List<Subjects> subsList = new ArrayList<>();
    public static List<Dropdown> termList = new ArrayList<>();
    public static List<Dropdown> sessionList = new ArrayList<>();

    RequestQueue queue;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String username = "", dbName = "";

    public Classes getSelectedClass(String cls) {
        for (Classes c : clsList) {
            if (c.getCls().equals(cls))
                return c;
        }
        Log.d("data.fastech.pk", "Selected class obj is null");
        return null;
    }

    public Faculty getFacultyID(String faculty) {
        if (faculty != null && !faculty.equals("") && facList.size() > 0) {
            for (Faculty c : facList) {
                if (c.getName().equals(faculty))
                    return c;
            }
        }
        return null;
    }

    public Subjects getSubjectID(String subject) {
        for (Subjects c : subsList) {
            if (c.getSubject().equals(subject))
                return c;
        }
        return null;
    }

    public Dropdown getTerm(String term) {
        for (Dropdown c : termList) {
            if (c.getName().equals(term))
                return c;
        }
        return null;
    }

    public Dropdown getSession(String term) {
        for (Dropdown c : sessionList) {
            if (c.getName().equals(term))
                return c;
        }
        return null;
    }

    public Populate(Context context) {
        queue = Volley.newRequestQueue(context);
        pref = context.getSharedPreferences("pushy.fastech.pk", 0); // 0 - for private mode
        username = pref.getString("username", "");
        dbName = pref.getString("dbName", "");
    }
}


