package e3.projet;

import android.content.Context;
import android.content.SharedPreferences;

import android.os.StrictMode;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import org.json.JSONObject;

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
            session.connect(3000);

            Channel channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            channel.setInputStream(null);

            ((ChannelExec)channel).setErrStream(System.err);

            channel.connect();

            channel.disconnect();
            session.disconnect();

            return true;
        } catch(Exception e){
            Log.d("ProjetE3","Erreur : "+e);


            return false;
        }
    }
    // https://www.youtube.com/watch?v=77sGWNGmLhw
    // http://www.jcraft.com/jsch/examples/Exec.java.html
    public static void sendCommandOrangeRaspberry(Context context, String nbOranges){
        String command="python3 /home/pi/Documents/Code_gestion_pressage.py "+nbOranges;
        sendCommandRaspberry(context,command);
    }

    public static String readFile(Context context,String fileName){
        SharedPreferences pref = context.getSharedPreferences("SSH", 0); // 0 - for private mode
        String ip = pref.getString("ip", null); // getting String
        String user = pref.getString("user", null); // getting String
        String password = pref.getString("password", null); // getting String

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
            session.setConfig(prop);
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            //InputStream stream = sftpChannel.get("/home/pi/Documents/oranges.txt");
            InputStream stream = sftpChannel.get(fileName);
            String line = null;
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                line = br.readLine();
            } catch (IOException io) {
                Log.d("ProjetE3", "Exception occurred during reading file from SFTP server due to " + io.getMessage());
                io.getMessage();

            } catch (Exception e) {
                Log.d("ProjetE3", "Exception occurred during reading file from SFTP server due to " + e.getMessage());
                e.getMessage();
            }

            sftpChannel.exit();
            session.disconnect();
            return line;
        } catch (JSchException e) {
            Log.d("ProjetE3","Erreur JSCH : "+e);
            return null;
        } catch (SftpException e) {
            Log.d("ProjetE3","Erreur SFTP : "+e);;
            return null;
        }
    }

    public static JSONObject getJSON(Context context){
        SharedPreferences pref = context.getSharedPreferences("SSH", 0); // 0 - for private mode
        String ip = pref.getString("ip", null); // getting String
        String user = pref.getString("user", null); // getting String
        String password = pref.getString("password", null); // getting String

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
            session.setConfig(prop);
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect(3000);
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            InputStream stream = sftpChannel.get("/home/pi/Documents/data.json");
            String line = null;
            JSONObject obj = null;
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));

                StringBuilder sb = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                obj = new JSONObject(sb.toString());

            } catch (IOException io) {
                Log.d("ProjetE3", "Exception io  occurred during reading file from SFTP server due to " + io.getMessage());
                io.getMessage();

            } catch (Exception e) {
                Log.d("ProjetE3", "Exception occurred during reading file from SFTP server due to " + e.getMessage());
                e.getMessage();
            }

            sftpChannel.exit();
            session.disconnect();
            return obj;
        } catch (JSchException e) {
            Log.d("ProjetE3","Erreur JSCH : "+e);
            return null;
        } catch (SftpException e) {
            Log.d("ProjetE3","Erreur SFTP : "+e);;
            return null;
        }
    }
}
