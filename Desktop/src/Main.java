import java.io.BufferedReader;
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
        BufferedReader reader;
        String line = "";

        try
        {
            server = new ServerSocket(port);
            server.setReuseAddress(true);
            client = server.accept();
            client.setKeepAlive(true);
            client.setReuseAddress(true);
            //reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            do
            {
                DataInputStream stream = new DataInputStream(client.getInputStream());
                line = stream.readUTF();
                System.out.println(line);
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
