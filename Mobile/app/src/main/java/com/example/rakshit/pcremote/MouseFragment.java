package com.example.rakshit.pcremote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Locale;

public class MouseFragment extends Fragment
{
    public static final String TAG = "MOUSE";

    View trackpad;
    Button left;
    Button right;
    View rootView;

    Socket socket;
    DataOutputStream writer;

    boolean doubleClickFlag = false;
    boolean scroll = false;

    int ox = 9999, oy = 9999, sy = 9999;
    int x, y;

    public MouseFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_mouse, container, false);
        trackpad = rootView.findViewById(R.id.trackpad);
        left = (Button) rootView.findViewById(R.id.mouse_left);
        right = (Button) rootView.findViewById(R.id.mouse_right);

        socket = Utils.getSocket();
        writer = Utils.getWriter();

        left.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    Utils.send("LDOWN");
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    Utils.send("LUP");
                return true;
            }
        });

        right.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Utils.send("RCLK");
            }
        });

        trackpad.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_MOVE && event.getAction() != MotionEvent.ACTION_OUTSIDE)
                {
                    x = (int)event.getX();
                    y = (int)event.getY();
                    StringBuilder s = new StringBuilder("");
                    if (event.getPointerCount()==1)
                    {
                        s.append('M');
                        if (x < ox)
                            s.append('-');
                        else
                            s.append('+');
                        s.append(String.format(Locale.getDefault(), "%04d", Math.abs(ox - x)));

                        if (y < oy)
                            s.append('-');
                        else
                            s.append('+');
                        s.append(String.format(Locale.getDefault(), "%04d", Math.abs(oy - y)));

                        Log.e(TAG, s.toString());
                        Utils.send(s.toString());
                    }

                    else if (event.getPointerCount()==2)
                    {
                        s.append('S');
                        if (y < sy)
                            s.append('-');
                        else
                            s.append('+');
                        s.append(String.format(Locale.getDefault(), "%04d", Math.abs(sy - y)));
                        sy = y;

                        Log.e(TAG, s.toString());
                        Utils.send(s.toString());
                        scroll = true;
                    }
                }

                else if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    if (event.getPointerCount()==1)
                    {
                        ox = (int) event.getX();
                        oy = (int) event.getY();
                        if (Utils.Timer.isRunning())
                            if (Utils.Timer.end() < 200)
                                doubleClickFlag = true;
                            else
                                doubleClickFlag = false;
                        else
                            doubleClickFlag = false;
                        if (!doubleClickFlag)
                            Utils.Timer.start();
                        else
                            Utils.send("LDOWN");
                        sy = oy;
                        scroll = false;
                    }
                    else if (event.getPointerCount() == 2)
                    {
                        scroll = true;
                        sy = (int) event.getY();
                    }
                }

                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    Utils.send("ENDM");
                    if (event.getPointerCount()==1 && !scroll)
                    {
                        if (!doubleClickFlag)
                        {
                            if (Utils.Timer.end() < 200 && Math.abs(event.getX() - ox) < 10 && Math.abs(event.getY() - oy) < 10)
                            {
                                Utils.send("LCLK");
                                Utils.Timer.start();
                            }
                        }
                        else
                            Utils.send("LUP");
                    }
                    scroll = false;
                    ox = (int) event.getX();
                    oy = (int) event.getY();
                }
                return true;
            }
        });

        return rootView;
    }
}
