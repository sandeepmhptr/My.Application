package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements AsyncResponse {
    Button btn_Connect;
    EditText text, stop1, stop2, HTTPResult, busNumberEdit;
    String myServer = "https://swulj.000webhostapp.com/bus.php";
    String myServer2 = "http://swulj.atwebpages.com/hi.php";
    String myResult, token, busNumber;
    //int i =0;

    RegisterActivity obj = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btn_Connect = (Button) findViewById(R.id.button_Connect);
        busNumberEdit = (EditText) findViewById(R.id.Bus_number);
        text = (EditText) findViewById(R.id.text);
        HTTPResult = (EditText) findViewById(R.id.HTTPResult);
        stop1 = (EditText) findViewById(R.id.stop1);
        stop2 = (EditText) findViewById(R.id.stop2);
        //text.setText("Enter Bus Number:");


        btn_Connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // it was the 1st button


                String Command = (String) text.getHint().toString();
                String Command2 = Command;
                //HTTPResult.setText(Command2);
                String stop = (String) busNumberEdit.getText().toString();
                if((Command.contentEquals("Enter Bus Number:")) )
                {
                    Toast.makeText(getApplicationContext(),"You download is resumed2",Toast.LENGTH_LONG).show();
                    HTTPConnection1 conn = new HTTPConnection1();
                    conn.delegate =  obj;
                    busNumber = (String) busNumberEdit.getText().toString();
                    MyTaskParams paramObj = new MyTaskParams(3, myServer, busNumber, Command2, "-1");
                    //paramObj.print();
                    //HTTPResult.setText(Command2);
                    conn.execute(paramObj);
                }
                else if(Command.contentEquals("Enter stops:"))
                {
                    HTTPConnection1 conn = new HTTPConnection1();
                    conn.delegate =  obj;
                    MyTaskParams paramObj = new MyTaskParams(4,myServer, busNumber, Command, stop);
                    conn.execute(paramObj);
                }
                else if(Command.contentEquals("enter fares between stops:"))
                {
                    HTTPConnection1 conn = new HTTPConnection1();
                    conn.delegate =  obj;
                    double fare = Double.valueOf( busNumberEdit.getText().toString());
                    MyTaskParams paramObj = new MyTaskParams(fare,myServer, busNumber, Command, "-1");
                    //paramObj.print();
                    conn.execute(paramObj);
                }
            }
        });
    }

    @Override
    public void processFinish(String output){
        //Here you will receive the result fired from async class
        //of onPostExecute(result) method.
        myResult = output;
        HTTPResult.setText(output);
        int len = output.length();
        String res = null;
        String stopToken = null;
        String[] stops = null;

        String[] tokens = output.split("<br>");

        String token = tokens[0];
        System.out.println(token);
        //HTTPResult.setText("length = " + String.valueOf(tokens.length));
        if (tokens.length == 4) {
            res = tokens[3];
            text.setText(tokens[2]);
            //System.out.println(token);
        }
        else if (tokens.length == 5) {
            res = tokens[4];
            text.setText(tokens[2]);
            stop1.setHint(tokens[3]);
            stop1.setHint(tokens[1]);
            //System.out.println(token);
        }
        else if (tokens.length == 9) {
            res = tokens[2];
            text.setText(tokens[2]);
            stop1.setHint(tokens[3]);
            stop1.setHint(tokens[1]);
            stopToken = tokens[8];
            stops = stopToken.split("::");
            //System.out.println(token);
        }

        /*for(int i = 0, j = 0;j < len; j++ )
        {
            if(output.charAt(j) == '<')
            {
                token = output.substring(i,j);
                if(j != len - 4)
                {
                    text.setText(token);
                }
                else
                {
                    //busNumberEdit.setText(token);
                    res = token;
                }
                j = j + 4;
                i = j;
            }
        }*/
        //res = res + ":";
        if(res!=null)
        if(res.contentEquals("Enter stops:") )
        {
            text.setHint(res);
            busNumberEdit.setHint("Next stop is:");
            //busNumberEdit.setHint(text.getHint().toString());
            //busNumberEdit.setText("token");
        }
        else if(res.contentEquals("enter fares between stops:") )
        {
            text.setHint(res);
            busNumberEdit.setHint("fare between the following stops is:");
            stop1.setText(stops[1]);
            stop2.setText(stops[3]);
            //busNumberEdit.setHint(text.getHint().toString());
            //busNu
            //
            // mberEdit.setText("token");
        }

    }


     class MyTaskParams {
        double fare;
        String url, busNumber, stop, Command;


        MyTaskParams(double fare, String url, String busNumber, String command, String stop) {
            this.fare = fare;
            this.url = url;
            this.busNumber = busNumber;
            this.Command = command;
            this.stop = stop;
        }
        void print()
        {
            HTTPResult.setText(fare + ";" + busNumber + ";" + Command + ";" + stop);
        }
    }


    class HTTPConnection1 extends AsyncTask<MyTaskParams, Void, String> {
        String result;
        double fare;
        String url, stop, command, busNumber;
        public AsyncResponse delegate = null;
        
        @Override
        protected String doInBackground(MyTaskParams... params) {

            fare = params[0].fare;
            url = params[0].url;
            busNumber = params[0].busNumber;
            command = params[0].Command;
            stop = params[0].stop;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            try {
                List<NameValuePair> nameValuePairs;
                nameValuePairs = new ArrayList<NameValuePair>(4);


                nameValuePairs.add(new BasicNameValuePair("fare", String.valueOf(fare)));
                nameValuePairs.add(new BasicNameValuePair("id", busNumber));
                nameValuePairs.add(new BasicNameValuePair("command", command));
                nameValuePairs.add(new BasicNameValuePair("stop", stop));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpclient.execute(httppost);
                InputStream inputStream = httpResponse.getEntity().getContent();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String bufferedStrChunk = null;

                while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                    stringBuilder.append(bufferedStrChunk);
                }


                result = stringBuilder.toString();
                //result= "Sandeep";

            } catch (ClientProtocolException e) {
                result = "ClientProtocolException";
                // TODO Auto-generated catch block
            } catch (IOException e) {
                result = "IOException";
                // TODO Auto-generated catch block
            }
            return result;
        }

        @Override
        protected void onPostExecute(String bitmap) {
            super.onPostExecute(bitmap);
            //HTTPResult.setText(result);
            delegate.processFinish(bitmap);

        }

    }
}