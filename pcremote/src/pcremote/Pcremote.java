/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcremote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Thread.sleep;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author User
 */
public class Pcremote {

    boolean status = true;

    public static void main(String[] args) {
        Pcremote pc = new Pcremote();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (pc.status) {
                    pc.check();
                    try {
                        sleep(5000);
                    } catch (InterruptedException ex) {

                    }

                }
            }
        });

        t.start();
        try {
            t.join();
        } catch (InterruptedException ex) {

        }
    }

    public void check() {
        String res = http("select");
        if (res.equals("1")) {
            http("update");
            status = false;
            shutdown();
        }
    }

    public void shutdown() {
        String shutdownCommand = null;
        String osName = System.getProperty("os.name");

        if (osName.startsWith("Win")) {
            shutdownCommand = "shutdown.exe -s -t 3600";
        } else if (osName.startsWith("Linux") || osName.startsWith("Mac")) {
            shutdownCommand = "shutdown -h now";
        } else {
            System.err.println("Shutdown unsupported operating system ...");
        }

        try {
            Runtime.getRuntime().exec(shutdownCommand);
        } catch (IOException ex) {
            System.out.println("Shutdown Error!");
        }
    }

    public String http(String command) {
        String url = "http://donghyuk.esy.es/bedperson/pcremote/pc2server.php?action=" + command;

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(response.toString());
            return response.toString();
        } catch (IOException e) {
            System.out.println("Error!");
        }
        return null;
    }
}
