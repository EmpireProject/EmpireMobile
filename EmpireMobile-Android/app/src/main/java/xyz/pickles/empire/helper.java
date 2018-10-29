package xyz.pickles.empire;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}