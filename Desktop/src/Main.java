import java.awt.*;
import java.awt.event.InputEvent;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main
{

    public static void main(String[] args)
    {
        int port = 11567;
        ServerSocket server;
        Socket client;
        String line = "";
        Robot robot;
        int x = 0;
        int y = 0;

        try
        {
            robot = new Robot();
            server = new ServerSocket(port);
            server.setReuseAddress(true);
            client = server.accept();
            client.setKeepAlive(true);
            client.setReuseAddress(true);

            do
            {
                DataInputStream stream = new DataInputStream(client.getInputStream());
                line = stream.readUTF();
                System.out.println(line);
                if (line.charAt(0)=='M')
                {
                    x = Integer.parseInt(line.substring(1, 7));
                    y = Integer.parseInt(line.substring(8, 14));
                    robot.mouseMove(x, y);
                }
                else if (line.equals("SLCLK"))
                {
                    robot.mousePress(InputEvent.BUTTON1_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_MASK);
                }
            }while (!line.equals("disconnect"));

            //reader.close();
            client.close();
            server.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
