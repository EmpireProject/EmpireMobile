package xyz.pickles.empire;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

public class helper {

    public static class getData extends AsyncTask<String, Void, String> {

        int code;
        String results;
        StringBuilder result;

        @Override
        protected String doInBackground(String... params) {

            //Create new URL
            URL url;
            try {
                url = new URL(params[0]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //Create custom HostnameVerifier to skip hostname checking since IPs may be used instead of domain names
            try {
                HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };

                //Build SSL UrlConnection with headers
                HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
                urlConn.setHostnameVerifier(hostnameVerifier);
                urlConn.setRequestMethod(params[1]);
                if (params[1].contains("DELETE")){
                    urlConn.setDoOutput(true);
                }else {}
                urlConn.setDoInput(true);
                urlConn.setUseCaches(false);
                urlConn.setConnectTimeout(8000);
                urlConn.setRequestProperty("Accept", "application/json");

                code = urlConn.getResponseCode();

                //If response is 200 get InputStream
                if (code == HttpURLConnection.HTTP_OK) {
                    InputStream in = urlConn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();
                    result.append(code);
                    results = result.toString();

                } else {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return results;
        }

        protected void onPostExecute(String results) {

        }
    }

    public static class postData extends AsyncTask<String, Void, String> {

        int code;
        String outputPost;

        @Override
        protected String doInBackground(String... params) {

            //Create new URL
            URL url;
            try {
                url = new URL(params[0]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //Create custom HostnameVerifier to skip hostname checking since IPs may be used instead of domain names
            try {
                HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };

                //Build SSL UrlConnection with headers
                HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
                urlConn.setHostnameVerifier(hostnameVerifier);
                urlConn.setDoInput(true);
                urlConn.setDoOutput(true);
                urlConn.setUseCaches(false);
                urlConn.setConnectTimeout(8000);
                urlConn.setRequestMethod("POST");
                urlConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConn.setRequestProperty("Accept", "application/json");

                //Get JSON Object created by calling method
                String poststuff = params[1];

                //Send POST Output
                OutputStream printout = urlConn.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(printout, "UTF-8");
                osw.write(poststuff);
                osw.flush();
                osw.close();

                code = urlConn.getResponseCode();

                //If response is 200 get InputStream
                if (code == HttpURLConnection.HTTP_OK) {
                    InputStream in = urlConn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result1 = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result1.append(line);
                    }
                    reader.close();
                    result1.append(code);
                    outputPost = result1.toString();
                } else {
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return outputPost;
        }

        @Override
        protected void onPostExecute(String outputPost) {
        }
    }

    public static boolean validateUser(String inputString) {
        final String[] userIllegalChars = {"\\","/","'","{","}","<",">","%","(",")",";",":","~","&","[","]","|","+","-"};
        for (int i = 0; i < userIllegalChars.length; i++) {
            if (inputString.contains(userIllegalChars[i])){
                System.out.print(userIllegalChars[i]);
                return false;
            }
        }
        return true;
    }

    public static boolean validateIP(String inputString) {
        final String IPpattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])" + ":" + "[0-9]{1,5}$";
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(IPpattern);
        matcher = pattern.matcher(inputString);
        return matcher.matches();
    }

    public static String storeCreds(String data) {
        return encrypt(data);
    }

    private static void keyGen() {
        try {

            final KeyGenerator keyGenny = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            final KeyGenParameterSpec paramSpec = new KeyGenParameterSpec.Builder("Empire", KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build();
            keyGenny.init(paramSpec);
            SecretKey secret = keyGenny.generateKey();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static String encrypt(String data) {
        String encryptedString = null;
        try{
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            final KeyStore.SecretKeyEntry keyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry("Empire", null);
            final SecretKey secretKey = keyEntry.getSecretKey();
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
            byte[] encrypted1 = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, encrypted1, 0, iv.length);
            System.arraycopy(encrypted, 0, encrypted1, iv.length, encrypted.length);
            encryptedString = Base64.encodeToString(encrypted1, Base64.DEFAULT);

        } catch (Exception e) {
            System.out.println(e);
        }
        return encryptedString;
    }

    private static String decrypt(String encryptedString) {
        String data = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            final KeyStore.SecretKeyEntry keyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry("Empire", null);
            final SecretKey secretKey = keyEntry.getSecretKey();

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] bytes = Base64.decode(encryptedString, Base64.DEFAULT);
            byte[] iv = new byte[12];
            System.arraycopy(bytes, 0, iv, 0, 12);
            final GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            data = new String(cipher.doFinal(bytes, 12, bytes.length-12));
        } catch (Exception e) {
            System.out.println(e);
        }
        return data;
    }

    public static Map<String, String> getPrefs() {
        String IP, user, passwd;
        Map<String, String> map = null;
        try {
            File prefsFile = new File(MyApplication.getContext().getString(R.string.filePath));
            if (prefsFile.exists()) {
                SharedPreferences prefs = MyApplication.getContext().getSharedPreferences("EmpirePrefs", MODE_PRIVATE);
                IP = prefs.getString(md5(MyApplication.getContext().getString(R.string.IP)), null);
                user = prefs.getString(md5(MyApplication.getContext().getString(R.string.user)), null);
                passwd = prefs.getString(md5(MyApplication.getContext().getString(R.string.pass)), null);

                map = new HashMap<>();
                map.put("IP", decrypt(IP));
                map.put("passwd", decrypt(passwd));
                map.put("user", decrypt(user));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return map;
    }

    public static void writePrefs(String user, String pass, String address) {
        try {
            keyGen();
            SharedPreferences prefs = MyApplication.getContext().getSharedPreferences("EmpirePrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(md5(MyApplication.getContext().getString(R.string.user)), storeCreds(user));
            editor.putString(md5(MyApplication.getContext().getString(R.string.pass)), storeCreds(pass));
            editor.putString(md5(MyApplication.getContext().getString(R.string.IP)), storeCreds(address));
            editor.commit();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static String md5(String keys) {
        String hex = null;
        try {
            final String MD5 = "MD5";
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(keys.getBytes());
            byte messageDigest [] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            hex = hexString.toString();

        } catch (Exception e) {
            System.out.println(e);
        }
        return hex;
    }
}

