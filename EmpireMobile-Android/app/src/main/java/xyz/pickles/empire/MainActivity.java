package xyz.pickles.empire;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONObject;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;


public class MainActivity extends AppCompatActivity {
    EditText IP,user,passw;
    Button auth;
    int code;
    TextView alertTView;
    String serial;
    Boolean mCert = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);
        user = (EditText)findViewById(R.id.input_user);
        IP = (EditText)findViewById(R.id.input_server);
        passw = (EditText)findViewById(R.id.input_password);
        auth = (Button)findViewById(R.id.button);

        auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get Text from textViews, and check input validation for username and address
                final String username = user.getText().toString();
                boolean validate = helper.validateUser(username);
                if (!validate) {
                    user.setError("Error: Illegal Characters Used!");
                }
                final String address = IP.getText().toString();
                boolean validateIP = helper.validateIP(address);
                if (!validateIP) {
                    IP.setError("Error: Not IP:port address format!");
                }
                final String password = passw.getText().toString();
                if (validate && validateIP) {
                    MyApplication mApp1 = ((MyApplication)getApplicationContext());
                    mApp1.setAddress(address);
                    final String http = "https://".concat(address).concat("/api/admin/login");

                    try
                    {
                        KeyStore ks = KeyStore.getInstance("AndroidCAStore");
                        if (ks != null)
                        {
                            ks.load(null, null);
                            Enumeration<String> aliases = ks.aliases();
                            while (aliases.hasMoreElements())
                            {
                                String alias = aliases.nextElement();
                                java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) ks.getCertificate(alias);

                                //loop through User installed certs and check if one contains Empire
                                if(cert.getIssuerDN().getName().contains("Empire")){
                                    System.out.println("0x".concat(cert.getSerialNumber().toString(16)));
                                    mCert = true;
                                    serial = "0x" + cert.getSerialNumber().toString(16);
                                }
                            }
                            //If the cert exists, display Serial # for approval, if yes, pass creds into getToken AsyncTask
                            if(mCert){
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                try {
                                                    JSONObject jsonParam1 = new JSONObject();
                                                    jsonParam1.put("username", username);
                                                    jsonParam1.put("password", password);
                                                    String params = jsonParam1.toString();
                                                    String results = new helper.postData().execute(http, params).get();

                                                    if(results.contains("200")) {
                                                        JSONObject jObj1 = new JSONObject(results);
                                                        String mToken = jObj1.getString("token");
                                                        MyApplication mApp = ((MyApplication)getApplicationContext());
                                                        mApp.setToken(mToken);
                                                        Toast.makeText(MyApplication.getContext(),
                                                                "Authentication Successful", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(MyApplication.getContext(), postLoginActivity.class);
                                                        startActivity(intent);
                                                    }else{
                                                        Toast.makeText(MyApplication.getContext(),"Authentication Failed!", Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (Exception e) {
                                                    System.out.println(e);
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

                                alertTView = new TextView(MyApplication.getContext());
                                alertTView.setTextColor(Color.GREEN);
                                alertTView.setTextSize(20);
                                alertTView.setGravity(Gravity.CENTER);
                                alertTView.setText(serial);
                                layout.addView(alertTView);

                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth);
                                builder.setView(layout);
                                builder.setMessage("Is this Empire certificate serial # correct? \r\n").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                            } else {
                                Toast.makeText(MyApplication.getContext(), "Please Install Proper Empire Certificate!", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (java.security.cert.CertificateException e) {
                        e.printStackTrace();
                    }
                } else {}
            }
        });
    }
}

