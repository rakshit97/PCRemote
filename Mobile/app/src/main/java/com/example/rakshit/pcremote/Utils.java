package com.example.rakshit.pcremote;

import android.util.Log;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Calendar;

public class Utils
{
    private static Socket socket;
    private static DataOutputStream writer;
    private static boolean connected = false;

    public static Socket getSocket()
    {
        return socket;
    }

    public static DataOutputStream getWriter()
    {
        return writer;
    }

    public static boolean getConnected()
    {
        return connected;
    }

    public static void setSocket(Socket socket1)
    {
        socket = socket1;
    }

    public static void setWriter(DataOutputStream writer1)
    {
        writer = writer1;
    }

    public static void setConnected(boolean bool)
    {
        connected = bool;
    }

    public static void send(String msg)
    {
        if (socket!=null && writer!=null)
        {
            try
            {
                writer.writeUTF(msg);
                writer.flush();
                if (msg.equals("disconnect"))
                {
                    writer.close();
                    socket.close();
                    setConnected(false);
                }
            }
            catch (Exception e)
            {
                Log.e("UTILS", Log.getStackTraceString(e));
            }
        }
    }

    public static class Timer
    {
        private static long start;
        private static long end;
        private static boolean running = false;

        public static void start()
        {
            start = Calendar.getInstance().getTimeInMillis();
            running = true;
        }

        public static long end()
        {
            end = Calendar.getInstance().getTimeInMillis();
            running = false;
            return end - start;
        }

        public static boolean isRunning()
        {
            return running;
        }
    }
}
