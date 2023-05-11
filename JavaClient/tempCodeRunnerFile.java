import java.io.*;
import java.net.*;
import java.net.http.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;

import org.w3c.dom.Node;

/**
 * This approach uses the java.net.http.HttpClient classes, which
 * were introduced in Java11.
 */
public class Client {
    private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public static void main(String... args) throws Exception {
        System.out.println(add(1, 2));

        // System.out.println(add() == 0);
        // System.out.println(add(1, 2, 3, 4, 5) == 15);
        // System.out.println(add(2, 4) == 6);
        // System.out.println(subtract(12, 6) == 6);
        // System.out.println(multiply(3, 4) == 12);
        // System.out.println(multiply(1, 2, 3, 4, 5) == 120);
        // System.out.println(divide(10, 5) == 2);
        // System.out.println(modulo(10, 5) == 0);
    }
    public static int add(int lhs, int rhs) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        URI uri = new URI("http://" + args[0] + ":" + args[1] + "/RPC");
        String methodName = "add";
        String request = "<?xml version=\"1.0\"?><methodCall><methodName>" + methodName + "</methodName><params><param><value><int>" + param1 + "</int></value></param><param><value><int>" + param2 + "</int></value></param></params></methodCall>";
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(uri)
            .header("Content-Type", "text/xml")
            .POST(HttpRequest.BodyPublishers.ofString(request))
            .build();

        HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        String responseBody = httpResponse.body();
        System.out.println(responseBody);
        InputStream inputStream = new ByteArrayInputStream(responseBody.getBytes(StandardCharsets.UTF_8));        Document document = builder.parse(inputStream);
        NodeList params = document.getElementsByTagName("param");
        int result = Integer.parseInt(params.item(0).getFirstChild().getTextContent());
        return result;
    }
    public static int add(Integer... params) throws Exception {
        return -1;
    }
    public static int subtract(int lhs, int rhs) throws Exception {
        return -1;
    }
    public static int multiply(int lhs, int rhs) throws Exception {
        return -1;
    }
    public static int multiply(Integer... params) throws Exception {
        return -1;
    }
    public static int divide(int lhs, int rhs) throws Exception {
        return -1;
    }
    public static int modulo(int lhs, int rhs) throws Exception {
        return -1;
    }
}
