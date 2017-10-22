package com.example.rakshit.pcremote;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectFragment extends Fragment
{
    public static final String TAG = "CONNECT";

    public static final int port = 11567;
    private Button btn_connect;
    private Button btn_send;
    private Button btn_disconnect;
    private TextView tv_error;
    private EditText edit_ip;
    private View rootView;

    Socket socket = null;
    DataOutputStream writer = null;

    public ConnectFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_connect, container, false);

        btn_connect = (Button) rootView.findViewById(R.id.btn_connect);
        btn_send = (Button) rootView.findViewById(R.id.btn_send);
        btn_disconnect = (Button) rootView.findViewById(R.id.btn_disconnect);
        tv_error = (TextView) rootView.findViewById(R.id.tv_error);
        edit_ip = (EditText) rootView.findViewById(R.id.edit_ip);

        if (!Utils.getConnected())
        {
            btn_disconnect.setEnabled(false);
            btn_send.setEnabled(false);
        }
        else
            btn_connect.setEnabled(false);

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
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Utils.send("SLCLK");
            }
        });

        btn_disconnect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Utils.send("disconnect");
                btn_disconnect.setEnabled(false);
                btn_send.setEnabled(false);
                btn_connect.setEnabled(true);
            }
        });

        return rootView;
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
                    Log.e(TAG, "Connection successful");
                    complete = true;
                    Utils.setSocket(socket);
                    Utils.setWriter(writer);
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
                    Utils.setConnected(true);

                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new MouseFragment(), MouseFragment.TAG).addToBackStack(TAG).commit();
                }
                else
                    tv_error.setText("Error connecting to PC");
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
            Log.e(TAG, "Incorrect parts");
            valid = false;
        }

        for (String part : parts)
        {
            int i = Integer.parseInt(part);
            if (i < 0 || i > 255)
            {
                tv_error.setText("Invalid IP address");
                Log.e(TAG, "Incorrect number");
                valid = false;
            }
        }
        return valid;
    }
}
