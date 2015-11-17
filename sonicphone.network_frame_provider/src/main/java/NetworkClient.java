/**
 * Created by andy on 16.11.15.
 */

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class NetworkClient {
    private DataInputStream dataInputStream;

    //IP-Adresse vom Server (Linux-Gerät)
    private String serverAddress;


    private Socket socket;

    public NetworkClient() throws IOException
    {
        serverAddress = "192.168.178.44";
        //Portnummer beliebig wählbar. Darf nicht von anderen Diensten genutzt werden
        socket = new Socket(serverAddress, 10003);
        dataInputStream = new DataInputStream(socket.getInputStream());
    }

    // Diese Methode anpassen
    // receivedData enthält die vom Server kommenden Bytes
    public void readBytes() throws IOException
    {
        //OutputStream muss angepasst werden
        //OutputStream fout = new FileOutputStream("out.mp4");
        byte[] receivedData = new byte[1024];
        while (true)
        {
            dataInputStream.read(receivedData);
            //fout.write(readData);
        }
        //return dataInputStream.read
    }
}


