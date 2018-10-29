package xyz.pickles.empire;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Agents extends AppCompatActivity {
    EditText edittext1, et;
    Button execute, get_results, rename, remove_agent, delete_results, kill_agent, remove_stale, clear_tasks, get_creds, events_logged, refresh_agents;
    TextView name, hostname, process_id, external_ip, internal_ip, listener, process_name, username, lastseen_time, os_details, checkin_time, delay, result_textview;
    String http2, Data, http3, http4, token, newnameinput, address, selectedItemText;
    String[] Powershellmodules, Pythonmodules, Exfilmodules, Externalmodules;
    List<String> allNames, allHints;
    List<EditText> allEds;
    LinearLayout agentScroll;
    CheckBox cb, b;
    View.OnClickListener MyListener;
    Spinner powershell, python, exfil, external;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_agents);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        MyApplication mApp = ((MyApplication)getApplicationContext());
        token = mApp.getToken();
        address = mApp.getAddress();
        powershell = (Spinner) findViewById(R.id.powershell_spinner);
        python = (Spinner) findViewById(R.id.python_spinner);
        exfil = (Spinner) findViewById(R.id.exfil_spinner);
        external = (Spinner) findViewById(R.id.external_spinner);
        Powershellmodules = new String[]{"Powershell"};
        Pythonmodules = new String[]{"Python"};
        Exfilmodules = new String[]{"Exfiltration"};
        Externalmodules = new String[]{"External"};
        agentScroll = (LinearLayout) findViewById(R.id.agent_Scroll);
        refresh_agents = (Button) findViewById(R.id.refreshAgentsBttn);
        clear_tasks = (Button) findViewById(R.id.cleartask_button);
        delete_results = (Button) findViewById(R.id.delete_results);
        remove_stale = (Button) findViewById(R.id.removestale_button);
        get_creds = (Button) findViewById(R.id.getcreds_button);
        events_logged = (Button) findViewById(R.id.eventslogged_button);
        kill_agent = (Button) findViewById(R.id.kill_button);
        execute = (Button) findViewById(R.id.execute_button);
        rename = (Button) findViewById(R.id.rename_button);
        remove_agent = (Button) findViewById(R.id.removeagent_button);
        get_results = (Button) findViewById(R.id.results_button);
        result_textview = (TextView) findViewById(R.id.results_textview);
        result_textview.setMovementMethod(new ScrollingMovementMethod());
        name = (TextView) findViewById(R.id.textView2);
        hostname = (TextView) findViewById(R.id.textView3);
        process_id = (TextView) findViewById(R.id.textView4);
        external_ip = (TextView) findViewById(R.id.textView5);
        internal_ip = (TextView) findViewById(R.id.textView6);
        listener = (TextView) findViewById(R.id.textView7);
        process_name = (TextView) findViewById(R.id.textView8);
        username = (TextView) findViewById(R.id.textView9);
        lastseen_time = (TextView) findViewById(R.id.textView10);
        os_details = (TextView) findViewById(R.id.textView11);
        os_details.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        os_details.setSelected(true);
        os_details.setSingleLine(true);
        checkin_time = (TextView) findViewById(R.id.textView12);
        checkin_time.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        checkin_time.setSelected(true);
        checkin_time.setSingleLine(true);
        delay = (TextView) findViewById(R.id.textView13);

        //Getting all modules and sorting into ArrayLists
        List<String> PSmoduleNames = new ArrayList<>(Arrays.asList(Powershellmodules));
        List<String> PymoduleNames = new ArrayList<>(Arrays.asList(Pythonmodules));
        List<String> ExfilmoduleNames = new ArrayList<>(Arrays.asList(Exfilmodules));
        List<String> ExternalmoduleNames = new ArrayList<>(Arrays.asList(Externalmodules));
        String http_getModules = "https://".concat(address).concat("/api/modules?token=").concat(token);
        try{
            String method = "GET";
            String results = new helper.getData().execute(http_getModules, method).get();
            JSONObject jObj = new JSONObject(results);
            JSONArray getValues = jObj.getJSONArray("modules");
            for (int i = 0; i < getValues.length(); i++) {
                jObj = getValues.getJSONObject(i);
                String modNames = jObj.getString("Name");
                if(modNames.contains("powershell")){
                    PSmoduleNames.add(modNames.replace("powershell/",""));
                }else if(modNames.contains("python")){
                    PymoduleNames.add(modNames.replace("python/", ""));
                }else if(modNames.contains("exfiltration") && !modNames.contains("powershell") && !modNames.contains("python")){
                    ExfilmoduleNames.add(modNames.replace("exfiltration/", ""));
                }else if(modNames.contains("external")){
                    ExternalmoduleNames.add(modNames);
                }
            }
            Collections.sort(PSmoduleNames);
            Collections.sort(PymoduleNames);
            Collections.sort(ExfilmoduleNames);
            Collections.sort(ExternalmoduleNames);

        }catch (Exception e){
            e.printStackTrace();
        }

        //Create ArrayAdapters for each Spinner
        final ArrayAdapter<String> PSspinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,PSmoduleNames){
            @Override
            public boolean isEnabled(int position){
                if(position == 0){
                    return false;
                }else {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent){
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    tv.setTextColor(Color.GRAY);
                }else{
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        final ArrayAdapter<String> PyspinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,PymoduleNames){
            @Override
            public boolean isEnabled(int position){
                if(position == 0){
                    return false;
                }else {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent){
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    tv.setTextColor(Color.GRAY);
                }else{
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        final ArrayAdapter<String> ExfilspinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,ExfilmoduleNames){
            @Override
            public boolean isEnabled(int position){
                if(position == 0){
                    return false;
                }else {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent){
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    tv.setTextColor(Color.GRAY);
                }else{
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        final ArrayAdapter<String> ExternalspinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,ExternalmoduleNames){
            @Override
            public boolean isEnabled(int position){
                if(position == 0){
                    return false;
                }else {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent){
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    tv.setTextColor(Color.GRAY);
                }else{
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        PSspinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        PyspinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        ExfilspinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        ExternalspinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        powershell.setAdapter(PSspinnerArrayAdapter);
        python.setAdapter(PyspinnerArrayAdapter);
        exfil.setAdapter(ExfilspinnerArrayAdapter);
        external.setAdapter(ExternalspinnerArrayAdapter);

        powershell.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemText = (String) parent.getItemAtPosition(position);
                if(position > 0){

                    Toast.makeText(MyApplication.getContext(), "Selected : ".concat(selectedItemText), Toast.LENGTH_SHORT).show();

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    JSONObject jsonParams = new JSONObject();
                                    try{
                                        //get the values for each hint(key) and the editText(value).  If the editText isn't empty, add the key/value to the jsonObject
                                        for(int i = 0; i < allEds.size(); i++) {
                                            String key = allHints.get(i);
                                            String value = allEds.get(i).getText().toString();
                                            if (!value.isEmpty()){
                                                jsonParams.put(key, value);
                                            }else {}
                                        }
                                        String json = jsonParams.toString();
                                        System.out.println(json);
                                        String http_module = "https://".concat(address).concat("/api/modules/powershell/").concat(selectedItemText).concat("?token=").concat(token);
                                        String outputPost = new helper.postData().execute(http_module, json).get();
                                        if(outputPost.contains("200")){
                                            Toast.makeText(MyApplication.getContext(), "[*] MODULE TASKED SUCCESS!", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(MyApplication.getContext(), "[*] MODULE TASKED FAILED!", Toast.LENGTH_SHORT).show();
                                        }
                                        powershell.setSelection(0);

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    powershell.setSelection(0);
                                    dialogInterface.dismiss();
                                    break;
                            }
                        }
                    };

                    //Create a three layer nested AlertDialog that allows scrolling.  The set sizes keep space for the buttons at the bottom.
                    LinearLayout mainlayout = new LinearLayout(MyApplication.getContext());
                    mainlayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 600));
                    ScrollView scroll = new ScrollView(MyApplication.getContext());
                    scroll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));
                    scroll.setFillViewport(true);
                    LinearLayout PSlayout = new LinearLayout(MyApplication.getContext());
                    PSlayout.setOrientation(LinearLayout.VERTICAL);
                    PSlayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));

                    allEds = new ArrayList<EditText>();
                    allHints = new ArrayList<String>();


                    try {
                        String method = "GET";
                        String http_module = "https://".concat(address).concat("/api/modules/powershell/").concat(selectedItemText).concat("?token=").concat(token);
                        String results = new helper.getData().execute(http_module, method).get();
                        JSONObject jObj1 = new JSONObject(results);
                        JSONArray getValues = jObj1.getJSONArray("modules");
                        //loop over array to get all objects
                        for (int i = 0; i < getValues.length(); i++) {
                            JSONObject jObj = getValues.getJSONObject(i);
                            Data = jObj.getString("options");
                            JSONObject options = new JSONObject(Data);
                            JSONArray keys = options.names();
                            //for each option for that module, dynamically draw an editText and set the hint as that options name
                            for (int k = 0; k < keys.length(); ++k) {
                                String key = keys.getString(k);
                                et = new EditText(MyApplication.getContext());
                                et.setHintTextColor(Color.parseColor("#33b5e5"));
                                et.setTextColor(Color.WHITE);
                                et.setHint(key);
                                allHints.add(key);
                                et.setGravity(Gravity.START);
                                allEds.add(et);
                                PSlayout.addView(et);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    scroll.addView(PSlayout);
                    mainlayout.addView(scroll);

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
                    builder.setView(mainlayout);
                    builder.setMessage("Execute Module");
                    builder.setPositiveButton("Confirm", dialogClickListener).setNegativeButton("Cancel", dialogClickListener);
                    AlertDialog alert = builder.create();
                    alert.setCanceledOnTouchOutside(false);
                    alert.show();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        python.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemText = (String) parent.getItemAtPosition(position);
                if(position > 0){
                    Toast.makeText(MyApplication.getContext(), "Selected : ".concat(selectedItemText), Toast.LENGTH_SHORT).show();

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    JSONObject jsonParams = new JSONObject();
                                    try{
                                        //get the values for each hint(key) and the editText(value).  If the editText isn't empty, add the key/value to the jsonObject
                                        for(int i = 0; i < allEds.size(); i++) {
                                            String key = allHints.get(i);
                                            String value = allEds.get(i).getText().toString();
                                            if (!value.isEmpty()){
                                                jsonParams.put(key, value);
                                            }else {}
                                        }
                                        String json = jsonParams.toString();
                                        String http_module = "https://".concat(address).concat("/api/modules/python/").concat(selectedItemText).concat("?token=").concat(token);
                                        String outputPost = new helper.postData().execute(http_module, json).get();
                                        if(outputPost.contains("200")){
                                            Toast.makeText(MyApplication.getContext(), "[*] MODULE TASKED SUCCESS!", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(MyApplication.getContext(), "[*] MODULE TASKED FAILED!", Toast.LENGTH_SHORT).show();
                                        }
                                        python.setSelection(0);

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    python.setSelection(0);
                                    dialogInterface.dismiss();
                                    break;
                            }
                        }
                    };

                    //Create a three layer nested AlertDialog that allows scrolling.  The set sizes keep space for the buttons at the bottom.
                    LinearLayout mainlayout = new LinearLayout(MyApplication.getContext());
                    mainlayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 600));
                    ScrollView scroll = new ScrollView(MyApplication.getContext());
                    scroll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));
                    scroll.setFillViewport(true);
                    LinearLayout Pylayout = new LinearLayout(MyApplication.getContext());
                    Pylayout.setOrientation(LinearLayout.VERTICAL);
                    Pylayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));

                    allEds = new ArrayList<EditText>();
                    allHints = new ArrayList<String>();


                    try {
                        String method = "GET";
                        String http_module = "https://".concat(address).concat("/api/modules/python/").concat(selectedItemText).concat("?token=").concat(token);
                        String results = new helper.getData().execute(http_module, method).get();
                        JSONObject jObj1 = new JSONObject(results);
                        JSONArray getValues = jObj1.getJSONArray("modules");
                        //loop over array to get all objects
                        for (int i = 0; i < getValues.length(); i++) {
                            JSONObject jObj = getValues.getJSONObject(i);
                            Data = jObj.getString("options");
                            JSONObject options = new JSONObject(Data);
                            JSONArray keys = options.names();
                            //for each option for that module, dynamically draw an editText and set the hint as that options name
                            for (int k = 0; k < keys.length(); ++k) {
                                String key = keys.getString(k);
                                et = new EditText(MyApplication.getContext());
                                et.setHintTextColor(Color.parseColor("#33b5e5"));
                                et.setTextColor(Color.WHITE);
                                et.setHint(key);
                                allHints.add(key);
                                et.setGravity(Gravity.START);
                                allEds.add(et);
                                Pylayout.addView(et);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    scroll.addView(Pylayout);
                    mainlayout.addView(scroll);

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
                    builder.setView(mainlayout);
                    builder.setMessage("Execute Module");
                    builder.setPositiveButton("Confirm", dialogClickListener).setNegativeButton("Cancel", dialogClickListener);
                    AlertDialog alert = builder.create();
                    alert.setCanceledOnTouchOutside(false);
                    alert.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        exfil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemText = (String) parent.getItemAtPosition(position);
                if(position > 0){
                    Toast.makeText(MyApplication.getContext(), "Selected : ".concat(selectedItemText), Toast.LENGTH_SHORT).show();

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    JSONObject jsonParams = new JSONObject();
                                    try{
                                        //get the values for each hint(key) and the editText(value).  If the editText isn't empty, add the key/value to the jsonObject
                                        for(int i = 0; i < allEds.size(); i++) {
                                            String key = allHints.get(i);
                                            String value = allEds.get(i).getText().toString();
                                            if (!value.isEmpty()){
                                                jsonParams.put(key, value);
                                            }else {}
                                        }
                                        String json = jsonParams.toString();
                                        String http_module = "https://".concat(address).concat("/api/modules/exfiltration/").concat(selectedItemText).concat("?token=").concat(token);
                                        String outputPost = new helper.postData().execute(http_module, json).get();
                                        if(outputPost.contains("200")){
                                            Toast.makeText(MyApplication.getContext(), "[*] MODULE TASKED SUCCESS!", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(MyApplication.getContext(), "[*] MODULE TASKED FAILED!", Toast.LENGTH_SHORT).show();
                                        }
                                        exfil.setSelection(0);

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    exfil.setSelection(0);
                                    dialogInterface.dismiss();
                                    break;
                            }
                        }
                    };

                    //Create a three layer nested AlertDialog that allows scrolling.  The set sizes keep space for the buttons at the bottom.
                    LinearLayout mainlayout = new LinearLayout(MyApplication.getContext());
                    mainlayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 600));
                    ScrollView scroll = new ScrollView(MyApplication.getContext());
                    scroll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));
                    scroll.setFillViewport(true);
                    LinearLayout Exfillayout = new LinearLayout(MyApplication.getContext());
                    Exfillayout.setOrientation(LinearLayout.VERTICAL);
                    Exfillayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));

                    allEds = new ArrayList<EditText>();
                    allHints = new ArrayList<String>();


                    try {
                        String method = "GET";
                        String http_module = "https://".concat(address).concat("/api/modules/exfiltration/").concat(selectedItemText).concat("?token=").concat(token);
                        String results = new helper.getData().execute(http_module, method).get();
                        JSONObject jObj1 = new JSONObject(results);
                        JSONArray getValues = jObj1.getJSONArray("modules");
                        //loop over array to get all objects
                        for (int i = 0; i < getValues.length(); i++) {
                            JSONObject jObj = getValues.getJSONObject(i);
                            Data = jObj.getString("options");
                            JSONObject options = new JSONObject(Data);
                            JSONArray keys = options.names();
                            //for each option for that module, dynamically draw an editText and set the hint as that options name
                            for (int k = 0; k < keys.length(); ++k) {
                                String key = keys.getString(k);
                                et = new EditText(MyApplication.getContext());
                                et.setHintTextColor(Color.parseColor("#33b5e5"));
                                et.setTextColor(Color.WHITE);
                                et.setHint(key);
                                allHints.add(key);
                                et.setGravity(Gravity.START);
                                allEds.add(et);
                                Exfillayout.addView(et);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    scroll.addView(Exfillayout);
                    mainlayout.addView(scroll);

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
                    builder.setView(mainlayout);
                    builder.setMessage("Execute Module");
                    builder.setPositiveButton("Confirm", dialogClickListener).setNegativeButton("Cancel", dialogClickListener);
                    AlertDialog alert = builder.create();
                    alert.setCanceledOnTouchOutside(false);
                    alert.show();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        external.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemText = (String) parent.getItemAtPosition(position);
                if(position > 0){
                    Toast.makeText(MyApplication.getContext(), "Selected : ".concat(selectedItemText), Toast.LENGTH_SHORT).show();

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    JSONObject jsonParams = new JSONObject();
                                    try{
                                        //get the values for each hint(key) and the editText(value).  If the editText isn't empty, add the key/value to the jsonObject
                                        for(int i = 0; i < allEds.size(); i++) {
                                            String key = allHints.get(i);
                                            String value = allEds.get(i).getText().toString();
                                            if (!value.isEmpty()){
                                                jsonParams.put(key, value);
                                            }else {}
                                        }
                                        String json = jsonParams.toString();
                                        String http_module = "https://".concat(address).concat("/api/modules/").concat(selectedItemText).concat("?token=").concat(token);
                                        String outputPost = new helper.postData().execute(http_module, json).get();
                                        if(outputPost.contains("200")){
                                            Toast.makeText(MyApplication.getContext(), "[*] MODULE TASKED SUCCESS!", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(MyApplication.getContext(), "[*] MODULE TASKED FAILED!", Toast.LENGTH_SHORT).show();
                                        }
                                        external.setSelection(0);

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    external.setSelection(0);
                                    dialogInterface.dismiss();
                                    break;
                            }
                        }
                    };

                    //Create a three layer nested AlertDialog that allows scrolling.  The set sizes keep space for the buttons at the bottom.
                    LinearLayout mainlayout = new LinearLayout(MyApplication.getContext());
                    mainlayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 600));
                    ScrollView scroll = new ScrollView(MyApplication.getContext());
                    scroll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));
                    scroll.setFillViewport(true);
                    LinearLayout Extlayout = new LinearLayout(MyApplication.getContext());
                    Extlayout.setOrientation(LinearLayout.VERTICAL);
                    Extlayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));

                    allEds = new ArrayList<EditText>();
                    allHints = new ArrayList<String>();


                    try {
                        String method = "GET";
                        String http_module = "https://".concat(address).concat("/api/modules/").concat(selectedItemText).concat("?token=").concat(token);
                        String results = new helper.getData().execute(http_module, method).get();
                        JSONObject jObj1 = new JSONObject(results);
                        JSONArray getValues = jObj1.getJSONArray("modules");
                        //loop over array to get all objects
                        for (int i = 0; i < getValues.length(); i++) {
                            JSONObject jObj = getValues.getJSONObject(i);
                            Data = jObj.getString("options");
                            JSONObject options = new JSONObject(Data);
                            JSONArray keys = options.names();
                            //for each option for that module, dynamically draw an editText and set the hint as that options name
                            for (int k = 0; k < keys.length(); ++k) {
                                String key = keys.getString(k);
                                et = new EditText(MyApplication.getContext());
                                et.setHintTextColor(Color.parseColor("#33b5e5"));
                                et.setTextColor(Color.WHITE);
                                et.setHint(key);
                                allHints.add(key);
                                et.setGravity(Gravity.START);
                                allEds.add(et);
                                Extlayout.addView(et);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    scroll.addView(Extlayout);
                    mainlayout.addView(scroll);

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
                    builder.setView(mainlayout);
                    builder.setMessage("Execute Module");
                    builder.setPositiveButton("Confirm", dialogClickListener).setNegativeButton("Cancel", dialogClickListener);
                    AlertDialog alert = builder.create();
                    alert.setCanceledOnTouchOutside(false);
                    alert.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        MyListener = new View.OnClickListener(){
            public void onClick(View v){
                b = (CheckBox)v;
                String names = b.getText().toString();
                http3 = "https://".concat(address).concat("/api/agents/").concat(names).concat("?token=").concat(token);
                //if checked, getData.  If not, unset all Text
                if (b.isChecked()) {

                    try {
                        String method = "GET";
                        String results = new helper.getData().execute(http3, method).get();
                        JSONObject jObj = new JSONObject(results);
                        JSONArray getValues = jObj.getJSONArray("agents");
                        //loop over array to get all objects
                        for (int i = 0; i < getValues.length(); i++) {
                            jObj = getValues.getJSONObject(i);
                        }
                        //get and set Text for these properties
                        name.setText(jObj.getString("name"));
                        hostname.setText(jObj.getString("hostname"));
                        process_id.setText(jObj.getString("process_id"));
                        external_ip.setText(jObj.getString("external_ip"));
                        internal_ip.setText(jObj.getString("internal_ip"));
                        listener.setText(jObj.getString("listener"));
                        process_name.setText(jObj.getString("process_name"));
                        username.setText(jObj.getString("username"));
                        lastseen_time.setText(jObj.getString("lastseen_time"));
                        os_details.setText(jObj.getString("os_details"));
                        checkin_time.setText(jObj.getString("checkin_time"));
                        delay.setText(jObj.getString("delay"));

                        http4 = "https://".concat(address).concat("/api/agents/stale?token=").concat(token);
                        String results_stale = new helper.getData().execute(http4, method).get();

                        JSONObject jObj1 = new JSONObject(results_stale);
                        JSONArray getValues1 = jObj1.getJSONArray("agents");
                        //loop over array to get all objects
                        for (int i = 0; i < getValues1.length(); i++) {
                            jObj1 = getValues1.getJSONObject(i);
                            String Id = jObj1.getString("name");
                            //get stale and append to checkin time field
                            if (Id.equals(b.getText())) {
                                checkin_time.append(" [*] STALE AGENT");
                            } else {
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    //unsetting all text
                    name.setText("");
                    hostname.setText("");
                    process_id.setText("");
                    external_ip.setText("");
                    internal_ip.setText("");
                    listener.setText("");
                    process_name.setText("");
                    username.setText("");
                    lastseen_time.setText("");
                    os_details.setText("");
                    checkin_time.setText("");
                    delay.setText("");
                }
            }
        };
        refreshAgents();

        execute.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                try{
                                    String Command = edittext1.getText().toString();
                                    String Name = name.getText().toString();
                                    //Create JSON object for Post request
                                    JSONObject jsonParam1 = new JSONObject();
                                    jsonParam1.put("command", Command);
                                    String json = jsonParam1.toString();
                                    String http_execute = "https://".concat(address).concat("/api/agents/").concat(Name).concat("/shell?token=").concat(token);
                                    String outputPost = new helper.postData().execute(http_execute, json).get();

                                    if(outputPost.contains("200")){
                                        Toast.makeText(MyApplication.getContext(), "[*] COMMAND SUCCESS!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(MyApplication.getContext(), "[*] COMMAND FAILED!", Toast.LENGTH_SHORT).show();
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

                LinearLayout layout = new LinearLayout(MyApplication.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                edittext1 = new EditText(MyApplication.getContext());
                edittext1.setHintTextColor(Color.parseColor("#33b5e5"));
                edittext1.setTextColor(Color.WHITE);
                edittext1.setHint("Command");
                edittext1.setGravity(Gravity.START);
                layout.addView(edittext1);

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
                builder.setView(layout);
                builder.setMessage("Run Shell Command").setPositiveButton("Confirm", dialogClickListener).setNegativeButton("Cancel", dialogClickListener).show();
            }
        });
        get_results.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String AgentName = name.getText().toString();
                String http_getResults = "https://".concat(address).concat("/api/agents/").concat(AgentName).concat("/results?token=").concat(token);
                try{
                    String method = "GET";
                    String results = new helper.getData().execute(http_getResults, method).get();
                    JSONObject jObj1 = new JSONObject(results);
                    JSONArray getValues = jObj1.getJSONArray("results");
                    //loop over array to get all objects
                    for (int i = 0; i < getValues.length(); i++) {
                        JSONObject jObj = getValues.getJSONObject(i);
                        Data = jObj.getString("AgentResults");
                    }
                    String agent_results = Data.replace(",", "\n").replace("{", "").replace("}", "").replace("\\\\r\\\\n", "\n").replace('"', ' ').replace("[", "").replace("]", "").replace("\\", "");
                    result_textview.setText("[*] AGENT RESULTS: \n");
                    result_textview.append(agent_results);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        rename.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                try{
                                    newnameinput = edittext1.getText().toString();
                                    String currentname = name.getText().toString();
                                    //Create JSON object for Post request
                                    JSONObject jsonParam1 = new JSONObject();
                                    jsonParam1.put("newname", newnameinput);
                                    String json = jsonParam1.toString();
                                    String http_rename = "https://".concat(address).concat("/api/agents/").concat(currentname).concat("/rename?token=").concat(token);
                                    String outputPost = new helper.postData().execute(http_rename, json).get();
                                    if(outputPost.contains("200")){
                                        Toast.makeText(MyApplication.getContext(), "[*] NAME CHANGE SUCCESS!", Toast.LENGTH_SHORT).show();
                                        b.setText(newnameinput);
                                        name.setText(newnameinput);
                                    }else{
                                        Toast.makeText(MyApplication.getContext(), "[*] NAME CHANGE FAILED!", Toast.LENGTH_SHORT).show();
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

                LinearLayout layout = new LinearLayout(MyApplication.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                edittext1 = new EditText(MyApplication.getContext());
                edittext1.setHintTextColor(Color.parseColor("#33b5e5"));
                edittext1.setTextColor(Color.WHITE);
                edittext1.setHint("New Name");
                edittext1.setGravity(Gravity.START);
                layout.addView(edittext1);

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
                builder.setView(layout);
                builder.setMessage("Rename Agent").setPositiveButton("Confirm", dialogClickListener).setNegativeButton("Cancel", dialogClickListener).show();
            }
        });
        remove_agent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                String currentName = name.getText().toString();
                                String http_remove_agent = "https://".concat(address).concat("/api/agents/").concat(currentName).concat("?token=").concat(token);

                                try{
                                    String method = "DELETE";
                                    String outputPost = new helper.getData().execute(http_remove_agent, method).get();
                                    if(outputPost.contains("200")){
                                        Toast.makeText(MyApplication.getContext(), "[*] AGENT REMOVED SUCCESS!", Toast.LENGTH_SHORT).show();
                                        b.setVisibility(View.GONE);
                                    }else{
                                        Toast.makeText(MyApplication.getContext(), "[*] AGENT REMOVED FAILED!", Toast.LENGTH_SHORT).show();
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

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
                builder.setMessage("Are you Sure?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });
        delete_results.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                String currentName = name.getText().toString();
                                String http_delete_results = "https://".concat(address).concat("/api/agents/").concat(currentName).concat("/results?token=").concat(token);
                                try{
                                    String method = "DELETE";
                                    String outputPost = new helper.getData().execute(http_delete_results, method).get();
                                    if(outputPost.contains("200")){
                                        Toast.makeText(MyApplication.getContext(), "[*] RESULTS DELETED: SUCCESS!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(MyApplication.getContext(), "[*] RESULTS DELETED: FAILED!", Toast.LENGTH_SHORT).show();
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

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
                builder.setMessage("Are you Sure?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });
        kill_agent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                String currentname = name.getText().toString();
                                String http_kill_agent = "https://".concat(address).concat("/api/agents/").concat(currentname).concat("/kill?token=").concat(token);
                                try{
                                    String method = "GET";
                                    String results = new helper.getData().execute(http_kill_agent, method).get();
                                    JSONObject json_obj = new JSONObject(results);
                                    String success = json_obj.getString("success");
                                    if (success.equals("true")) {
                                        Toast.makeText(MyApplication.getContext(), "[*] AGENT EXITING!", Toast.LENGTH_SHORT).show();
                                        b.setVisibility(View.GONE);
                                    }else{
                                        Toast.makeText(MyApplication.getContext(), "[*] KILL TASK UNSUCCESSFUL!", Toast.LENGTH_SHORT).show();
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

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
                builder.setMessage("Are you Sure?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });
        remove_stale.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                String http_removeStale = "https://".concat(address).concat("/api/agents/stale?token=").concat(token);
                                try{
                                    String method = "DELETE";
                                    String outputPost = new helper.getData().execute(http_removeStale, method).get();
                                    if(outputPost.contains("200")){
                                        Toast.makeText(MyApplication.getContext(), "[*] STALE AGENTS REMOVED!", Toast.LENGTH_SHORT).show();
                                        agentScroll.removeAllViews();
                                    }else{
                                        Toast.makeText(MyApplication.getContext(), "[*] COMMAND FAILED!", Toast.LENGTH_SHORT).show();
                                    }
                                    String method1 = "GET";
                                    String results = new helper.getData().execute(http2, method1).get();
                                    //Create ArrayList for storing String names
                                    final List<String> allNames = new ArrayList<String>();
                                    JSONObject jObj = new JSONObject(results);
                                    JSONArray getValues = jObj.getJSONArray("agents");
                                    //loop over jsonArray, creating a jsonObject, then grabbing every value for the key "name"
                                    //and placing into the ArrayList
                                    for (int i = 0; i < getValues.length(); i++) {
                                        jObj = getValues.getJSONObject(i);
                                        String Id = jObj.getString("name");
                                        allNames.add(Id);
                                    }
                                    //loop through ArrayList and draw a checkbox for every "name" value
                                    for (int i = 0; i < allNames.size(); i++) {
                                        cb = new CheckBox(MyApplication.getContext());
                                        cb.setText(allNames.get(i));
                                        cb.setId(i);
                                        cb.setOnClickListener(MyListener);

                                        //Create Params for the checkboxes that go in a linear layout
                                        LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        checkParams.setMargins(10, 10, 10, 10);
                                        checkParams.gravity = Gravity.START;
                                        agentScroll.addView(cb, checkParams);
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

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
                builder.setMessage("Are you Sure").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });
        clear_tasks.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String currentName = name.getText().toString();
                String http_clearTasks = "https://".concat(address).concat("/api/agents/").concat(currentName).concat("/clear?token=").concat(token);
                try{
                    String method = "GET";
                    String results = new helper.getData().execute(http_clearTasks, method).get();
                    JSONObject json_obj = new JSONObject(results);
                    String success = json_obj.getString("success");
                    if (success.equals("true")){
                        Toast.makeText(MyApplication.getContext(), "[*] AGENT TASKS CLEARED!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MyApplication.getContext(), "[*] CLEAR AGENT TASKS FAILED!", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        events_logged.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String currentName = name.getText().toString();
                String http_events = "https://".concat(address).concat("/api/reporting/agent/").concat(currentName).concat("?token=").concat(token);
                try{
                    String method = "GET";
                    String results = new helper.getData().execute(http_events, method).get();
                    JSONObject jObj1 = new JSONObject(results);
                    JSONArray getValues = jObj1.getJSONArray("reporting");
                    result_textview.setText("[*] AGENT LOGGED EVENTS \n");
                    //loop over array to get all objects
                    for (int i = 0; i < getValues.length(); i++) {
                        jObj1 = getValues.getJSONObject(i);
                        String events = jObj1.toString().replace(",", "\n").replace("{", "").replace("}", "").replace("\\\\r\\\\n", "\n").replace('"', ' ').replace("[", "").replace("]", "").replace("\\", "");
                        result_textview.append(events);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        get_creds.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String http_getCreds = "https://".concat(address).concat("/api/creds?token=").concat(token);
                try{
                    String method = "GET";
                    String results = new helper.getData().execute(http_getCreds, method).get();
                    JSONObject jObj1 = new JSONObject(results);
                    JSONArray getValues = jObj1.getJSONArray("creds");
                    result_textview.setText("[*] CREDENTIALS STORED! \n");
                    //loop over array to get all objects
                    for (int i = 0; i < getValues.length(); i++) {
                        jObj1 = getValues.getJSONObject(i);
                        String creds = jObj1.toString().replace(",", "\n").replace("{", "").replace("}", "").replace("\\\\r\\\\n", "\n").replace('"', ' ').replace("[", "").replace("]", "").replace("\\", "");
                        result_textview.append(creds);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        refresh_agents.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                agentScroll.removeAllViews();
                refreshAgents();
            }
        });
    }

    private void refreshAgents(){
        try{
            http2 = "https://".concat(address).concat("/api/agents?token=").concat(token);
            String method = "GET";
            String results = new helper.getData().execute(http2, method).get();
            //Create ArrayList for storing String names
            final List<String> allNames = new ArrayList<String>();
            JSONObject jObj = new JSONObject(results);
            JSONArray getValues = jObj.getJSONArray("agents");
            //loop over jsonArray, creating a jsonObject, then grabbing every value for the key "name"
            //and placing into the ArrayList
            for (int i = 0; i < getValues.length(); i++) {
                jObj = getValues.getJSONObject(i);
                String Id = jObj.getString("name");
                allNames.add(Id);
            }
            //loop through ArrayList and draw a checkbox for every "name" value
            for (int i = 0; i < allNames.size(); i++) {
                cb = new CheckBox(MyApplication.getContext());
                cb.setText(allNames.get(i));
                cb.setTextColor(Color.WHITE);
                cb.setId(i);
                cb.setOnClickListener(MyListener);

                //Create Params for the checkboxes that go in a linear layout
                LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                checkParams.setMargins(10, 10, 10, 10);
                checkParams.gravity = Gravity.START;
                agentScroll.addView(cb, checkParams);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}