package de.kit.esmac.xml;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Xml;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created by saglakh on 02/05/16.
 */

public class XMLRetriever extends AsyncTask<String, Void, String> {

    String result;
    public XMLInterface delegate = null;

    public XMLRetriever(XMLInterface response) {
        delegate = response;//Assigning call back interface through constructor
    }

    protected String doInBackground(String ... url) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            URI website = new URI(url[0]);
            System.out.println(url[0]);
            request.setURI(website);
            HttpResponse response = httpclient.execute(request);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));

            // NEW CODE
            result = in.readLine();
            // END OF NEW CODE
            System.out.println(" Connected ");
            System.out.println("response is: " + response);
            System.out.println("line is: " + result);
            return "ok";
        } catch(Exception e){
            //Log.e("log_tag", "Error in http connection "+e.toString());
            System.out.println("error " + e.toString());
            return "no";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        System.out.println("on post execute");
        java.io.FileWriter fw = null;
        if (result != null) {
            try {
                fw = new java.io.FileWriter("sdcard/de.kit.esmdummy/masterarbeit.xml");
                fw.write(result);
                fw.close();
                delegate.processFinish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("network error");
        }
    }
}
