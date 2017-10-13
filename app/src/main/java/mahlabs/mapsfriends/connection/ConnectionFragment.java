package mahlabs.mapsfriends.connection;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import mahlabs.mapsfriends.ResponseListener;

/**
 * Created by 13120dde on 2017-10-10.
 */

public class ConnectionFragment extends Fragment {

    public static final String TAG="connectionFragment";
    private static final String LOG="IN_Connection";

    private String ip;
    private int port;
    private InetAddress inetAddress;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ResponseListener callbackListener;
    private ConnectionThread thread;
    private ServerReceiver reciever;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port=port;
    }

    public void setCallbackListener(ResponseListener listener) {
        this.callbackListener =listener;
    }

    public void disconnect() {
        if(thread!=null){
            thread.execute(new Disconnect());
        }
    }

    public void connect(){
        if(thread==null){
            thread = new ConnectionThread();
            thread.start();
            thread.execute(new Connect());
        }
    }

    public void sendToServer(String msg){
        if(thread!=null){
            thread.execute(new SendToServer(msg));
        }
    }



    private class SendToServer implements Runnable{


        private final String msg;

        public SendToServer(String msg){
            this.msg=msg;
        }
        @Override
        public void run() {
            try {
                dos.writeUTF(msg);
                dos.flush();
                Log.d(LOG,"Sent to server: "+msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class ServerReceiver extends Thread{

        String msg;


        @Override
        public void run() {
            Log.d(LOG,"Receiver started");

            while(reciever !=null){
                try {
                    msg = dis.readUTF();
                    Log.d(LOG,"Response from server: "+msg);
                    callbackListener.parseServerMsg(msg);
                } catch (IOException e) {
                   // e.printStackTrace();
                }
            }
        }
    }

    private class Connect implements Runnable{

        @Override
        public void run() {
            try {
                inetAddress = InetAddress.getByName(ip);
                socket = new Socket(inetAddress,port);
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                reciever = new ServerReceiver();
                reciever.start();
                Log.d(LOG,"Connection established");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class Disconnect implements Runnable{

        @Override
        public void run() {
            try {
                if(socket!=null){
                    socket.close();
                }
                if(dos!=null){
                    dos.close();
                }
                if(dis!=null){
                    dis.close();
                }
                thread.stop();
                reciever.interrupt();
                Log.d(LOG,"Disconnected");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
