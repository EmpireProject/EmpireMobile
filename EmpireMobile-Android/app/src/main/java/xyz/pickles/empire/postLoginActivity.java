package xyz.pickles.empire;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

public class postLoginActivity extends AppCompatActivity {
    Button getListeners, getAgents, createListeners, killListener, logout;
    String address, token;
    TextView display;
    EditText edittext, edittext1, edittext2, edittext3, type, listener_edittext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_post_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        display = (TextView) findViewById(R.id.textView4);
        display.setMovementMethod(new ScrollingMovementMethod());
        getListeners = (Button) findViewById(R.id.listeners_button);
        killListener = (Button) findViewById(R.id.kill_listeners_button);
        createListeners = (Button) findViewById(R.id.create_listeners_button);
        getAgents = (Button) findViewById(R.id.agents_button);
        logout = (Button) findViewById(R.id.logoutButton);
        final MyApplication mApp = ((MyApplication)getApplicationContext());
        token = mApp.getToken();
        address = mApp.getAddress();
        display.setText("[*] LISTENERS INFO: \n");
        /*String http = "https://" + address + "/api/config?token=" + token;
        try{
            String method = "GET";
            String results = new helper.getData().execute(http, method).get();
            //take StringBuilder result and convert to jsonObject, then move config data to jsonArray
            JSONObject jObj = new JSONObject(results);
            JSONArray getValues = jObj.getJSONArray("config");
            //loop over array to get all objects
            for (int i = 0; i < getValues.length(); i++) {
                jObj = getValues.getJSONObject(i);
            }
            String config = jObj.toString().replace(",", "\n").replace("{", "").replace("}", "").replace("\\\\r\\\\n", "\n").replace('"', ' ').replace("[", "").replace("]", "").replace("\\", "");
            display.setText("[*] CONFIG: \n");
            display.append(config);
        }catch (Exception e){
            e.printStackTrace();
        }*/

        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mApp.setToken("");
                mApp.setAddress("");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        getListeners.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String http1 = "https://".concat(address).concat("/api/listeners?token=").concat(token);
                try {
                    String method = "GET";
                    String results = new helper.getData().execute(http1, method).get();
                    JSONObject jObj = new JSONObject(results);
                    JSONArray getValues = jObj.getJSONArray("listeners");
                    //loop over array to get all objects
                    for (int i = 0; i < getValues.length(); i++) {
                        jObj = getValues.getJSONObject(i);
                        String listeners = jObj.toString().replace(",", "\n").replace("{", "").replace("}", "").replace("\\\\r\\\\n", "\n").replace('"', ' ').replace("[", "").replace("]", "").replace("\\", "");
                        display.append("\n[*] LISTENER: \n".concat(listeners));
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        getAgents.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //if the screen size is in the normal group, use the AgentsNormal java class file, otherwise use the Agents java class file
                //normal size phones get a compacted layout and an alertDialog that displays results in the Agents Activity
                System.out.println(getResources().getConfiguration().screenLayout);
                if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL){
                    Intent intent = new Intent(getApplicationContext(), AgentsNormal.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getApplicationContext(), Agents.class);
                    startActivity(intent);
                }

            }
        });
        createListeners.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                try{
                                    String name = edittext.getText().toString();
                                    String Host = edittext1.getText().toString();
                                    String Port = edittext2.getText().toString();
                                    String delay = edittext3.getText().toString();
                                    String list_type = type.getText().toString();

                                    //Create JSON object for Post request
                                    JSONObject jsonParam1 = new JSONObject();
                                    jsonParam1.put("Name", name);
                                    jsonParam1.put("Host", Host);
                                    jsonParam1.put("Port", Port);
                                    jsonParam1.put("DefaultDelay", delay);
                                    String json = jsonParam1.toString();
                                    System.out.println(json);
                                    String http_create_listener = "https://".concat(address).concat("/api/listeners/").concat(list_type).concat("?token=").concat(token);
                                    String outputPost = new helper.postData().execute(http_create_listener, json).get();
                                    if(outputPost.contains("200")){
                                        Toast.makeText(MyApplication.getContext(), "[*] LISTENER SUCCESSFULLY CREATED!", Toast.LENGTH_SHORT).show();

                                    }else{
                                        Toast.makeText(MyApplication.getContext(), "[*] SOMETHING WENT WRONG!!", Toast.LENGTH_SHORT).show();
                                    }

                            }catch (Exception e){
                                    e.printStackTrace();
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialogInterface.dismiss();
                                break;
                        }
                    }
                };

                Context context = MyApplication.getContext();
                LinearLayout mainlayout = new LinearLayout(MyApplication.getContext());
                mainlayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 600));
                ScrollView scroll = new ScrollView(MyApplication.getContext());
                scroll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));
                scroll.setFillViewport(true);
                LinearLayout innerLayout = new LinearLayout(MyApplication.getContext());
                innerLayout.setOrientation(LinearLayout.VERTICAL);
                innerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));

                type = new EditText(context);
                type.setHintTextColor(Color.parseColor("#33b5e5"));
                type.setTextColor(Color.WHITE);
                type.setHint("listener type");
                type.setGravity(Gravity.START);
                innerLayout.addView(type);

                edittext = new EditText(context);
                edittext.setHintTextColor(Color.parseColor("#33b5e5"));
                edittext.setTextColor(Color.WHITE);
                edittext.setHint("name");
                edittext.setGravity(Gravity.START);
                innerLayout.addView(edittext);

                edittext1 = new EditText(context);
                edittext1.setHintTextColor(Color.parseColor("#33b5e5"));
                edittext1.setTextColor(Color.WHITE);
                edittext1.setHint("Host");
                edittext1.setGravity(Gravity.START);
                innerLayout.addView(edittext1);

                edittext2 = new EditText(context);
                edittext2.setHintTextColor(Color.parseColor("#33b5e5"));
                edittext2.setTextColor(Color.WHITE);
                edittext2.setHint("Port");
                edittext2.setGravity(Gravity.START);
                innerLayout.addView(edittext2);

                edittext3 = new EditText(context);
                edittext3.setHintTextColor(Color.parseColor("#33b5e5"));
                edittext3.setTextColor(Color.WHITE);
                edittext3.setHint("delay");
                edittext3.setGravity(Gravity.START);
                innerLayout.addView(edittext3);

                scroll.addView(innerLayout);
                mainlayout.addView(scroll);

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
                builder.setView(mainlayout);
                builder.setMessage("Create Listener").setPositiveButton("Confirm", dialogClickListener).setNegativeButton("Cancel", dialogClickListener).show();
            }
        });
        killListener.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                String listenerName = listener_edittext.getText().toString();
                                String http_kill_listener = "https://".concat(address).concat("/api/listeners/").concat(listenerName).concat("?token=").concat(token);
                                try{
                                    String method = "DELETE";
                                    String outputPost = new helper.getData().execute(http_kill_listener, method).get();
                                    if(outputPost.contains("200")){
                                        Toast.makeText(MyApplication.getContext(), "[*] LISTENER KILLED!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(MyApplication.getContext(), "[*] LISTENER KILL FAILED!", Toast.LENGTH_SHORT).show();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialogInterface.dismiss();
                                break;
                        }
                    }
                };
                Context context = MyApplication.getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                listener_edittext = new EditText(context);
                listener_edittext.setHintTextColor(Color.parseColor("#33b5e5"));
                listener_edittext.setTextColor(Color.WHITE);
                listener_edittext.setHint("Listener Name");
                listener_edittext.setGravity(Gravity.START);
                layout.addView(listener_edittext);

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
                builder.setView(layout);
                builder.setMessage("Are you Sure?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });
    }
}
