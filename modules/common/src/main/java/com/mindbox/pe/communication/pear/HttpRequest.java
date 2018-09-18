package com.mindbox.pe.communication.pear;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

import com.mindbox.pe.communication.pear.Request;
import com.mindbox.pe.communication.pear.Response;
import com.mindbox.pe.communication.pear.Serializer;

public class HttpRequest {

    private static final Logger LOG = Logger.getLogger(HttpRequest.class);

    public static Response post(String urlString, Request<?> request) throws Exception {
        LOG.debug("post(): urlString=" + urlString);
        LOG.debug("post(): request=" + request.toString());
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "PEAR");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        request.serialize(outputStream);
        outputStream.flush();
        outputStream.close();

        int responseCode = connection.getResponseCode();
        if (HttpURLConnection.HTTP_OK != responseCode) {
            StringBuilder builder = new StringBuilder("HTTP response code = ");
            builder.append(responseCode);
            String message = builder.toString();
            LOG.error(message);
            throw new Exception(message);
        }

        InputStream inputStream = connection.getInputStream();
        Response response = (Response) Serializer.deserialize(inputStream);
        LOG.debug("post(): response=" + response.toString());
        return response;
    }
}
