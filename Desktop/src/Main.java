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
        int px;
        int py;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)screenSize.getWidth();
        int height = (int)screenSize.getHeight();

        try
        {
            robot = new Robot();
            server = new ServerSocket(port);
            server.setReuseAddress(true);
            client = server.accept();
            client.setKeepAlive(true);
            client.setReuseAddress(true);
            px = width/2;
            py = height/2;
            robot.mouseMove(px, py);

            do
            {
                DataInputStream stream = new DataInputStream(client.getInputStream());
                line = stream.readUTF();
                System.out.println(line);

                if (line.charAt(0) == 'M')
                {
                    int dx = Integer.parseInt(line.substring(1, 6));
                    int dy = Integer.parseInt(line.substring(6, 11));
                    x = px + dx;
                    y = py + dy;
                    if (x > width)
                        x = width;
                    if (x < 0)
                        x = 0;
                    if (y > height)
                        y = height;
                    if (y < 0)
                        y = 0;
                    robot.mouseMove(x, y);
                }

                else if (line.charAt(0) == 'S')
                {
                    robot.mouseWheel(Integer.parseInt(line.substring(1, 5)));
                }
                else if (line.equals("ENDM"))
                {
                    px = x;
                    py = y;
                }
                else if (line.equals("LDOWN"))
                {
                    robot.mousePress(InputEvent.BUTTON1_MASK);
                }
                else if (line.equals("LUP"))
                {
                    robot.mouseRelease(InputEvent.BUTTON1_MASK);
                }
                else if (line.equals("LCLK"))
                {
                    robot.mousePress(InputEvent.BUTTON1_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_MASK);
                }
                else if (line.equals("RCLK"))
                {
                    robot.mousePress(InputEvent.BUTTON3_MASK);
                    robot.mouseRelease(InputEvent.BUTTON3_MASK);
                }
            }while (!line.equals("disconnect"));

            client.close();
            server.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
