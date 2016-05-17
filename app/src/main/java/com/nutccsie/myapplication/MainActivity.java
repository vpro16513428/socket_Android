package com.nutccsie.myapplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    TextView textResponse,txtSend;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear,buttonSend;
    Socket socket = null;
    ByteArrayOutputStream byteArrayOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAddress = (EditText)findViewById(R.id.address);
        editTextPort = (EditText)findViewById(R.id.port);
        buttonConnect = (Button)findViewById(R.id.connect);
        buttonClear = (Button)findViewById(R.id.clear);
        textResponse = (TextView)findViewById(R.id.response);
        buttonSend = (Button) findViewById(R.id.send_btn);
        txtSend = (TextView) findViewById(R.id.txtSend);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        buttonSend.setOnClickListener(buttonSendOnClickListener);

        buttonClear.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                textResponse.setText("");
            }});
    }

    OnClickListener buttonConnectOnClickListener =
            new OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    MyClient_connect_Task myClientTask = new MyClient_connect_Task(editTextAddress.getText().toString(), Integer.parseInt(editTextPort.getText().toString()));
                    myClientTask.execute();
                }};

    OnClickListener buttonSendOnClickListener =
            new OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    MyClient_send_Task myClient_send_task =new MyClient_send_Task();
                    myClient_send_task.execute(txtSend.getText().toString());
                }};

    public class MyClient_connect_Task extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";

        MyClient_connect_Task(String addr, int port){
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Log.d("connectTask","connectTask");
                socket = new Socket(dstAddress, dstPort);

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            textResponse.setText(response);
            super.onPostExecute(result);
        }

    }


    public class MyClient_send_Task extends AsyncTask<String, Void, Void> {
        String response = "";

        @Override
        protected Void doInBackground(String... arg0) {
            try {
                Log.d("sendTask","sendTask");
                byte[] data = arg0[0].getBytes("UTF-8");
                OutputStream os = socket.getOutputStream();
                os.write(data);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();

    /*
     * notice:
     * inputStream.read() will block if no data return
     */
                while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            textResponse.setText(response);
            super.onPostExecute(result);
        }

    }
}

