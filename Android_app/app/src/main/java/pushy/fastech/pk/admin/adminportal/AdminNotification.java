package pushy.fastech.pk.admin.adminportal;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.material.components.R;
import com.material.components.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pushy.fastech.pk.Helper.Reciept;
import pushy.fastech.pk.Helper.Thread_Messages;
import pushy.fastech.pk.adapters.AdapterAddRecipientAdminNotification;
import pushy.fastech.pk.adapters.AdapterChatAdminNotification;
import pushy.fastech.pk.adapters.AdapterRecipientAdminNotification;

public class AdminNotification extends AppCompatActivity {

    private SearchView searchView;

    private String newTextRecipient;
    private ImageView btn_send;
    private EditText et_content;
    private Menu menu;

    // 1. RecyclerView for recipients that are added
    private LinearLayoutManager mLayoutManager;
    public RecyclerView recyclerViewAddedRecipient;
    private AdapterRecipientAdminNotification mAdapter;
    private List<Reciept> items;

    // 2. RecyclerView for chat messages
    private RecyclerView recyclerViewChatMessages;
    private AdapterChatAdminNotification adapter;
    private List<Thread_Messages> itemsMessages;

    // 3. RecyclerView for selecting recipients
    private RecyclerView recyclerViewSelectingRecipient;
    private AdapterAddRecipientAdminNotification adapterRecipient;
    private List<Reciept> recipientNames;
    private String recipient = "";

    // Others
    private View parent_view;
    private ActionBar actionBar;
    private ProgressBar progressBarTool;
    private LinearLayout lytMessageBox;

    // Shared preference
    String username = "", dbName = "";
    RequestQueue queue;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Integer type = 0;
    Boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notification);
        parent_view = findViewById(android.R.id.content);
        progressBarTool = findViewById(R.id.progress_bar_while_loading);
        lytMessageBox = findViewById(R.id.lyt_message_box);

        queue = Volley.newRequestQueue(getApplicationContext());
        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk", 0); // 0 - for private mode
        username = pref.getString("username", "");
        dbName = pref.getString("dbName", "");
        type = pref.getInt("type", 2);
        editor = pref.edit();
        editMode = pref.getBoolean("editMode", false);
        recipientNames = new ArrayList<>();
        items = new ArrayList<>();
        LinearLayout footer = (LinearLayout) findViewById(R.id.footer);

        Bundle b;
        b = getIntent().getExtras();
        try {
            recipient = b.getString("recipient");
        } catch (Exception x) {
            Log.e("data.fastech.pk", "Bundle error.");
        }

//        try {
//            if (editMode)
//                footer.setVisibility(View.GONE);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        initToolbar();
        initComponent();
        initComponentRecipients();
    }

    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setTitle("Notification");
        Tools.setSystemBarColor(this);
    }

    // 1. RecyclerView for recipients that are added
    private void initComponent() {
        mLayoutManager = new LinearLayoutManager(AdminNotification.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mLayoutManager.setOrientation(mLayoutManager.HORIZONTAL);

        recyclerViewAddedRecipient = findViewById(R.id.recyclerViewHorizontalNotification);
        recyclerViewAddedRecipient.setLayoutManager(mLayoutManager);
        recyclerViewAddedRecipient.setHasFixedSize(true);

        mAdapter = new AdapterRecipientAdminNotification(this, items, recyclerViewAddedRecipient);
        recyclerViewAddedRecipient.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new AdapterRecipientAdminNotification.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Reciept obj, int pos) {
            }
        });
    }

    // 2. RecyclerView for chat notifications
    private void initComponentChat() {
        recyclerViewChatMessages = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewChatMessages.setLayoutManager(layoutManager);
        recyclerViewChatMessages.setHasFixedSize(true);
        itemsMessages = new ArrayList<>();

        try {
            String url = "http://pushy.fastech.pk/Notifications/GetChat?number=" + username + "&recipient=" + recipient + "&_db=" + dbName;
            if (type == 1)
                url = "http://pushy.fastech.pk/Notifications/GetChat?number=0&recipient=" + recipient + "&_db=" + dbName;

            Log.d("data.fastech.pk", "Chat URL: " + url);
            JsonArrayRequest request = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            itemsMessages.clear();
                            Log.d("data.fastech.pk", "Loading Chats...");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Thread_Messages c = new Thread_Messages();
                                try {

                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    c.setId(jsonObject.getInt("id"));
                                    c.setTitle(jsonObject.getString("title"));
                                    c.setBody(jsonObject.getString("body"));
                                    c.setApproved(jsonObject.getBoolean("isApproved"));
                                    c.setReciept(jsonObject.getString("reciept"));
                                    c.setSendBy(jsonObject.getString("sendBy"));
                                    c.setSenderNumber(jsonObject.getString("senderNumber"));
                                    c.setRequestTime(jsonObject.getString("requestTime"));
                                    //itemsMessages.add(c);
                                    adapter.insertItem(c);

                                } catch (JSONException e) {
                                    Log.e("data.fastech.pk", e.getLocalizedMessage());
                                }
                            }
                            recyclerViewChatMessages.scrollToPosition(adapter.getItemCount() - 1);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.e("data.fastech.pk", volleyError.getLocalizedMessage());
                        }
                    });

            queue.add(request);
        } catch (Exception x) {
        }

        adapter = new AdapterChatAdminNotification(this, itemsMessages);
        recyclerViewChatMessages.setAdapter(adapter);

        btn_send = findViewById(R.id.btn_send);
        et_content = findViewById(R.id.text_content);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendChat();
            }
        });
        et_content.addTextChangedListener(contentWatcher);
    }

    // 3. RecyclerView for selecting Recipients from list

    private void initComponentRecipients() {
        recyclerViewSelectingRecipient = findViewById(R.id.recycler_view_recipients);
        recyclerViewSelectingRecipient.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSelectingRecipient.setHasFixedSize(true);

        // Show loader
        progressBarTool.setVisibility(View.VISIBLE);
        lytMessageBox.setVisibility(View.GONE);
        initComponentChat();
        try {
            String url = "";
            if (type == 1) {
                if (!editMode) {
                    url = "http://pushy.fastech.pk/Fill/GetReciept?tID=0&tNumber=" + username + "&_db=" + dbName;
                }
            } else {
                url = "http://pushy.fastech.pk/Fill/GetReciept?tID=1&tNumber=" + username + "&_db=" + dbName;
            }

            Log.d("data.fastech.pk", "Reciepts URL: " + url);
            JsonArrayRequest request = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            recipientNames.clear();
                            Log.d("data.fastech.pk", "Loading Reciepts...");

                            Collection<Reciept> localList = null;
                            try {
                                Gson gson = new Gson();
                                Type collectionType = new TypeToken<Collection<Reciept>>() {
                                }.getType();
                                localList = gson.fromJson(recipient, collectionType);
                            } catch (Exception x) {
                                Log.e("data.fastech.pk", "GSON error: " + x.getLocalizedMessage());
                            }

                            for (int i = 0; i < jsonArray.length(); i++) {
                                Reciept c = new Reciept();
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    c.setId(jsonObject.getInt("id"));
                                    if ((jsonObject.getInt("type") == 2) || (jsonObject.getInt("type") == 3)) {
                                        c.setTitle(jsonObject.getString("title"));
                                    } else
                                        c.setTitle(capitalize(jsonObject.getString("title").toLowerCase()));

                                    c.setDesc(jsonObject.getString("desc"));
                                    if (jsonObject.getInt("type") == 2)
                                        c.setDesc("Student's department");
                                    if (jsonObject.getInt("type") == 6)
                                        c.setDesc("Employee's department");
                                    c.setCls(jsonObject.getString("cls"));
                                    c.setSec(jsonObject.getString("sec"));
                                    c.setType(jsonObject.getInt("type"));
                                    recipientNames.add(c);

                                    // Populate Horizontal Contacts List
                                    if (localList != null) {
                                        for (Reciept rcp : localList) {
                                            try {
                                                if ((rcp.getId().equals(c.getId())) && (rcp.getType().equals(c.getType()))) {
                                                    if (rcp.getType() == 3) //Compare Sec as well
                                                    {
                                                        if (rcp.getSec().equals(c.getSec())) {
                                                            mAdapter.insertItem(c);
                                                            recyclerViewAddedRecipient.requestLayout();
                                                            break;
                                                        }
                                                    } else //Other than classes
                                                    {
                                                        mAdapter.insertItem(c);
                                                        recyclerViewAddedRecipient.requestLayout();
                                                        break;
                                                    }

                                                }
                                            } catch (Exception x) {
                                                Log.e("data.fastech.pk", "Local population error: " + x.getLocalizedMessage());
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    Log.e("data.fastech.pk", e.getLocalizedMessage());
                                }
                            }

                            //Show Adapter
                            adapterRecipient = new AdapterAddRecipientAdminNotification(getApplicationContext(), recipientNames);
                            recyclerViewSelectingRecipient.setAdapter(adapterRecipient);

                            adapterRecipient.setOnItemClickListener(new AdapterAddRecipientAdminNotification.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, Reciept obj, int position) {
                                    if (items.contains(obj)) {
                                        snackBarIconAlreadyExist(obj.getTitle() + " Already exists!");
                                    } else {
                                        mAdapter.insertItem(obj);
                                        searchView.setQuery("", false);
                                        recyclerViewAddedRecipient.requestLayout();
                                        mLayoutManager.scrollToPositionWithOffset(items.size() - 1, 0);
                                    }
                                }
                            });

                            // Hide loader
                            progressBarTool.setVisibility(View.GONE);
                            menu.getItem(0).setVisible(true);
                            lytMessageBox.setVisibility(View.VISIBLE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.e("data.fastech.pk", volleyError.getLocalizedMessage());
                            Toast.makeText(AdminNotification.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    });

            // mEntries = new ArrayList<>();
            queue.add(request);
        } catch (Exception x) {
        }
    }

    private void sendChat() {
        if (mAdapter.getItemCount() > 0) {
            //Show Loader here

            final String msg = et_content.getText().toString();
            if (msg.isEmpty()) return;

            String receipts = "";
            final Thread_Messages m = new Thread_Messages();
            JSONArray array = new JSONArray();
            Integer i = 0, more = 0;
            String desc = "";
            for (Reciept rc : mAdapter.GetReciept()) {
                JSONObject jObj = new JSONObject();
                try {
                    jObj.put("id", rc.getId());
                    //jObj.put("title", rc.getTitle());
                    //jObj.put("desc", rc.getDesc());
                    //jObj.put("cls", rc.getCls());
                    jObj.put("sec", rc.getSec());
                    jObj.put("type", rc.getType());
                    array.put(jObj);
                    if (i == 0)
                        m.setTitle(rc.getTitle());
                    i++;
                    if (i < 5) {
                        if (desc.length() > 0)
                            desc += ", ";

                        desc += rc.getTitle();
                    } else {
                        more++;
                    }

                } catch (JSONException e) {
                    Log.e("data.fastech.pk", e.getLocalizedMessage());
                }
            }

            if (more > 0)
                desc = desc + " +" + more + " more";

            Log.e("data.fastech.pk", "Desc: " + desc);
            receipts = array.toString();
            //Log.e("data.fastech.pk", receipts);
            m.setDesc(desc);
            m.setReciept(receipts);
            m.setBody(msg);
            m.setSendBy(username);
            m.setSenderNumber(username);
            m.setApproved(false);

            //Request time
            SimpleDateFormat formatter = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
            Date d = new Date(System.currentTimeMillis());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                m.setRequestTime(formatter.format(d));
            }

            JSONObject obj_thread = new JSONObject();
            try {
                obj_thread.put("id", 0);
                obj_thread.put("title", m.getTitle());
                obj_thread.put("description", m.getDesc());
                obj_thread.put("body", m.getBody());
                obj_thread.put("reciept", m.getReciept());
                obj_thread.put("sendBy", m.getSendBy());
                obj_thread.put("senderNumber", m.getSenderNumber());
                obj_thread.put("isApproved", m.getApproved());
                obj_thread.put("requestTime", m.getRequestTime());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = "http://pushy.fastech.pk/Notifications/CreateThread";

            JSONObject js = new JSONObject();
            try {
                Log.d("data.fastech.pk", "Putting notification obj..");
                js.put("t", obj_thread);
                js.put("_db", dbName);
                Log.d("data.fastech.pk", js.toString());

            } catch (JSONException e) {
                Log.e("data.fastech.pk", e.getLocalizedMessage());
            }

            //Send API request
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    url, js, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    String time = "";
                    JSONObject jsonObject = response;
                    try {
                        time = jsonObject.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    m.setRequestTime(time);
                    adapter.insertItem(m);
                    et_content.setText("");
                    recyclerViewChatMessages.scrollToPosition(adapter.getItemCount() - 1);

                    //Hide Loader here
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError e) {
                    Log.e("data.fastech.pk", "Error on request");
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
            queue.add(jsonObjReq);
        } else {
            Toast.makeText(AdminNotification.this, "Please select recipient first.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        hideKeyboard();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private TextWatcher contentWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable etd) {
            if (etd.toString().trim().length() == 0) {
                btn_send.setImageResource(R.drawable.ic_mic);
            } else {
                btn_send.setImageResource(R.drawable.ic_send);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        if (!editMode) {
            getMenuInflater().inflate(R.menu.menu_chat_telegram, menu);
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint("Search");

            AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            try {
                Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableRes.setAccessible(true);
                mCursorDrawableRes.set(searchTextView, R.drawable.cursor);
            } catch (Exception e) {

            }

            // SearchView Queries
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    newTextRecipient = newText;
                    adapterRecipient.getFilter().filter(newTextRecipient);
                    return false;
                }
            });

            // SearchView Close
            searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {
                    recyclerViewSelectingRecipient.setVisibility(View.VISIBLE);
                }

                @Override
                public void onViewDetachedFromWindow(View view) {
                    // Search is already visible below
                    recyclerViewSelectingRecipient.setVisibility(View.GONE);

                }
            });
        }

//        menu.getItem(0).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_search) {
            recyclerViewSelectingRecipient.setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    private String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    private void snackBarIconAlreadyExist(String txt) {
        final Snackbar snackbar = Snackbar.make(parent_view, "", Snackbar.LENGTH_SHORT);
        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_icon_text, null);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarView.setPadding(0, 0, 0, 0);

        ((TextView) custom_view.findViewById(R.id.message)).setText(txt);
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
        (custom_view.findViewById(R.id.parent_view)).setBackgroundColor(getResources().getColor(R.color.red_600));
        snackBarView.addView(custom_view, 0);
        snackbar.show();
    }
}
