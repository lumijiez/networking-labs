package org.lumijiez.network;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TCPClient {
    public static String getHttps(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            String path = uri.getRawPath();
            String query = uri.getRawQuery();
            int port = uri.getPort() == -1 ? 443 : uri.getPort();

            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
            socket.setEnabledProtocols(socket.getSupportedProtocols());
            socket.startHandshake();

            String requestPath = path + (query != null ? "?" + query : "");

            String request =
                    "GET " + requestPath + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "Connection: close\r\n\r\n";

            return socketIo(socket, request);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String checkJson(String jsonData) {
        return sendData("application/json", jsonData);
    }

    public static String checkXml(String xmlData) {
        return sendData("application/xml", xmlData);
    }

    private static String sendData(String contentType, String data) {
        try {
            URI uri = new URI("http://localhost:8000/upload");
            String host = uri.getHost();
            int port = uri.getPort() == -1 ? 80 : uri.getPort();

            Socket socket = new Socket(host, port);

            String username = "500";
            String password = "408";
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

            String request =
                            "POST " + uri.getPath() + " HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            "Content-Type: " + contentType + "\r\n" +
                            "Content-Length: " + data.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                            "Authorization: Basic " + encodedAuth + "\r\n" +
                            "Connection: close\r\n\r\n" +
                            data;

            String response = socketIo(socket, request);

            return "Status Code: " + response.split("\n")[0].split(" ")[1];
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static String socketIo(Socket socket, String request) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(request.getBytes());
        outputStream.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line).append("\n");
        }
        reader.close();
        socket.close();

        return response.toString();
    }
}