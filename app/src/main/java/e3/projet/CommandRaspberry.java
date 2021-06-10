package e3.projet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class CommandRaspberry {


    public static boolean sendCommandRaspberry(Context context, String command) {
        SharedPreferences pref = context.getSharedPreferences("SSH", 0); // 0 - for private mode
        String ip = pref.getString("ip", null); // getting String
        String user = pref.getString("user", null); // getting String
        String password = pref.getString("password", null); // getting String

        int port = 22;
        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);

            JSch jsch = new JSch();
            Session session = jsch.getSession(user, ip, port);
            session.setPassword(password);

            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);
            session.connect();

            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            channel.setInputStream(null);

            ((ChannelExec)channel).setErrStream(System.err);

            channel.connect();

            channel.disconnect();
            session.disconnect();



            return true;
        } catch(Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();

            return false;
        }
    }
    // https://www.youtube.com/watch?v=77sGWNGmLhw
    // http://www.jcraft.com/jsch/examples/Exec.java.html
    public static void sendCommandOrangeRaspberry(Context context, String nbOranges){

        String command="python3 /home/pi/Documents/fonctionne.py "+nbOranges;
        Log.d("ProjetE3", command);
        sendCommandRaspberry(context,command);

    }

    public static String readFile(Context context,String fileName){
        SharedPreferences pref = context.getSharedPreferences("SSH", 0); // 0 - for private mode
        String ip = pref.getString("ip", null); // getting String
        String user = pref.getString("user", null); // getting String
        String password = pref.getString("password", null); // getting String

        Log.d("Projet",password);

        int port = 22;


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession(user, ip, port);
            session.setPassword(password);

            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            Log.d("Projet","avant");
            session.setConfig(prop);
            session.connect();
            Log.d("Projet","connect");
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            //InputStream stream = sftpChannel.get("/home/pi/Documents/oranges.txt");
            InputStream stream = sftpChannel.get(fileName+".txt");
            Log.d("Projet", "Execute");
            String line = null;
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));

                line = br.readLine();



            } catch (IOException io) {
                Log.d("MyAlarmBelal", "Exception occurred during reading file from SFTP server due to " + io.getMessage());
                io.getMessage();

            } catch (Exception e) {
                Log.d("MyAlarmBelal", "Exception occurred during reading file from SFTP server due to " + e.getMessage());
                e.getMessage();
            }

            sftpChannel.exit();
            session.disconnect();
            return line;
        } catch (JSchException e) {

            Log.d("Projet","erreur 2");
            return null;
        } catch (SftpException e) {
            Log.d("Projet","erreur1");;
            return null;
        }
    }
}
