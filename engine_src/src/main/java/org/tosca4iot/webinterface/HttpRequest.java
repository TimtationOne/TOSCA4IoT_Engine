package org.tosca4iot.webinterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.stereotype.Service;

@Service
public class HttpRequest {
	public String send(String restUrl, String contentType, String method, String content) {
		String response="";
		try {

			URL url = new URL(restUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(method);// "POST");
			conn.setRequestProperty("Content-Type", contentType);// "application/vnd.onem2m-res+json;
			
			//TODO: Test with Read Timeout
			conn.setReadTimeout(0);
			
			String body = content;

			OutputStream os = conn.getOutputStream();
			os.write(body.getBytes());
			os.flush();
			
			if (conn.getResponseCode() != HttpURLConnection.HTTP_ACCEPTED && conn.getResponseCode() != HttpURLConnection.HTTP_CREATED && conn.getResponseCode() != HttpURLConnection.HTTP_OK ) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			response = "Output from Server .... \n";
			while ((output = br.readLine()) != null) {
				response += output + "\n";
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		return response;
	}

}
