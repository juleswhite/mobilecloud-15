package org.magnum.dataup;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Genius on 8/1/2015.
 */
public class Utils {
   private Utils(){
        throw new AssertionError("you cannot instantiate this class");
    }


    public static String getIpUsingInetAddress(){
        String http="http://";
        try {
            InetAddress ip = InetAddress.getLocalHost();
            return  http+ip.getHostAddress()+":8080";
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return http+"127.0.0.1:8080";
    }
}
