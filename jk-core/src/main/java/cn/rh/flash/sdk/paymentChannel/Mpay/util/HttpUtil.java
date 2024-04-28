package cn.rh.flash.sdk.paymentChannel.Mpay.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

    public static String sendHttpRequest(final String address) {
        HttpURLConnection connection = null;
        try {
            StringBuilder response =new StringBuilder();
            URL url =new URL(address);
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(8000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            connection.setReadTimeout(8000);
            InputStream input = connection.getInputStream();
            BufferedReader reader =new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            connection.disconnect();
        }

    }
}
