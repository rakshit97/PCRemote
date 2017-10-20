package com.example.rakshit.pcremote;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity
{
    public static final int port = 11567;
    private Button btn_connect;
    private Button btn_send;
    private Button btn_disconnect;
    private TextView tv_error;
    private EditText edit_ip;
    Socket socket = null;
    DataOutputStream writer = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_connect = (Button) findViewById(R.id.btn_connect);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
        tv_error = (TextView) findViewById(R.id.tv_error);
        edit_ip = (EditText) findViewById(R.id.edit_ip);

        btn_disconnect.setEnabled(false);
        btn_send.setEnabled(false);

        btn_connect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    String ip = edit_ip.getText().toString();

                    if (checkIP(ip))
                    {
                        tv_error.setText("");
                        connect(ip);
                    }
                } catch (Exception e)
                {
                    Log.e("Main", Log.getStackTraceString(e));
                }
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                send("Hello");
            }
        });

        btn_disconnect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                send("disconnect");
                btn_disconnect.setEnabled(false);
                btn_send.setEnabled(false);
                btn_connect.setEnabled(true);
            }
        });
    }

    private void connect(final String ip)
    {
        new AsyncTask<Void, Void, Boolean>()
        {
            @Override
            protected Boolean doInBackground(Void... params)
            {
                boolean complete = false;
                try
                {
                    socket = new Socket(InetAddress.getByName(ip), port);
                    socket.setKeepAlive(true);
                    socket.setReuseAddress(true);
                    writer = new DataOutputStream(socket.getOutputStream());
                    writer.writeUTF("PCRemote connecting...");
                    writer.flush();
                    Log.e("Async", "Connection successful");
                    complete = true;
                } catch (Exception e)
                {
                    Log.e("Async", Log.getStackTraceString(e));
                    complete = false;
                }
                return complete;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean)
            {
                if (aBoolean)
                {
                    btn_connect.setEnabled(false);
                    btn_disconnect.setEnabled(true);
                    btn_send.setEnabled(true);
                    tv_error.setText("");
                }
                else
                    tv_error.setText("Error connecting to PC");
            }
        }.execute();
    }

    private void send(final String msg)
    {
        if (socket == null || writer == null)
            tv_error.setText("Please connect first");
        else
            new AsyncTask<Void, Void, Void>()
            {
                @Override
                protected Void doInBackground(Void... params)
                {
                    try
                    {
                        writer.writeUTF(msg);
                        writer.flush();
                        Log.e("Send", "message sent: " + msg);
                        if (msg.equals("disconnect"))
                        {
                            writer.close();
                            socket.close();
                        }
                    } catch (Exception e)
                    {
                        Log.e("Send", Log.getStackTraceString(e));
                    }
                    return null;
                }
            }.execute();
    }

    private boolean checkIP(String ip)
    {
        boolean valid = true;
        String[] parts = ip.split("\\.");
        if (parts.length != 4)
        {
            tv_error.setText("Invalid IP address");
            Log.e("Main", "Incorrect parts");
            valid = false;
        }

        for (String part : parts)
        {
            int i = Integer.parseInt(part);
            if (i < 0 || i > 255)
            {
                tv_error.setText("Invalid IP address");
                Log.e("Main", "Incorrect number");
                valid = false;
            }
        }
        return valid;
    }
}
