import java.io.*;
import java.net.*;
import java.net.http.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * This approach uses the java.net.http.HttpClient classes, which
 * were introduced in Java11.
 */
public class Client {
    private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    public static HttpClient client = HttpClient.newHttpClient();
    public static URI uri;

    public static void main(String... args) throws Exception {
        uri = new URI("http://" + args[0] + ":" + args[1] + "/RPC");
        System.out.println(add() == 0);
        System.out.println(add(1, 2, 3, 4, 5) == 15);
        System.out.println(add(2, 4) == 6);
        System.out.println(subtract(12, 6) == 6);
        System.out.println(multiply(3, 4) == 12);
        System.out.println(multiply(1, 2, 3, 4, 5) == 120);
        System.out.println(divide(10, 5) == 2);
        System.out.println(modulo(10, 5) == 0);
    }

    // todo: handle fault cases
    public static int add(int lhs, int rhs) throws Exception {
        HttpRequest request = twoParamRequest("add", lhs, rhs);
        HttpResponse httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        int[] result = new int[1];
        handleResponse(result, httpResponse);
        if (result.length == 0) {
            throw new Exception();
        }
        return result[0];
    }
    public static int add(Integer... params) throws Exception {
        HttpRequest request = multipleParamRequest("add", params);
        HttpResponse httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        int[] result = new int[1];
        handleResponse(result, httpResponse);
        if (result.length == 0) {
            throw new Exception();
        }
        return result[0];
    }
    public static int subtract(int lhs, int rhs) throws Exception {
        HttpRequest request = twoParamRequest("subtract", lhs, rhs);
        HttpResponse httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        int[] result = new int[1];
        handleResponse(result, httpResponse);
        if (result.length == 0) {
            throw new Exception();
        }
        return result[0];
    }
    public static int multiply(int lhs, int rhs) throws Exception {
        HttpRequest request = twoParamRequest("multiply", lhs, rhs);
        HttpResponse httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        int[] result = new int[1];
        handleResponse(result, httpResponse);
        if (result.length == 0) {
            throw new Exception();
        }
        return result[0];
        
    }
    public static int multiply(Integer... params) throws Exception {
        HttpRequest request = multipleParamRequest("multiply", params);
        HttpResponse httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        int[] result = new int[1];
        handleResponse(result, httpResponse);
        if (result.length == 0) {
            throw new Exception();
        }
        return result[0];
    }
    public static int divide(int lhs, int rhs) throws Exception {
        HttpRequest request = twoParamRequest("divide", lhs, rhs);
        HttpResponse httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        int[] result = new int[1];
        handleResponse(result, httpResponse);
        if (result.length == 0) {
            throw new Exception();
        }
        return result[0];
    }
    public static int modulo(int lhs, int rhs) throws Exception {
        HttpRequest request = twoParamRequest("modulo", lhs, rhs);
        HttpResponse httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        int[] result = new int[1];
        handleResponse(result, httpResponse);
        if (result.length == 0) {
            throw new Exception();
        }
        return result[0];
    }

    public static HttpRequest twoParamRequest(String methodName, int lhs, int rhs) {
        String request = "<?xml version=\"1.0\"?><methodCall><methodName>" + methodName + "</methodName><params><param><value><i4>" + lhs + "</i4></value></param><param><value><i4>" + rhs + "</i4></value></param></params></methodCall>";
        return HttpRequest.newBuilder()
            .uri(uri)
            .header("Content-Type", "text/xml")
            .POST(HttpRequest.BodyPublishers.ofString(request))
            .build();
    }
    public static HttpRequest multipleParamRequest(String methodName, Integer... params) {
        String xmlParams = "";
        for (Integer param : params) {
            xmlParams += "<param><value><i4>" + param + "</i4></value></param>";
        }
        String request = "<?xml version=\"1.0\"?><methodCall><methodName>" + methodName + "</methodName><params>" + xmlParams + "</params></methodCall>";
        return HttpRequest.newBuilder()
            .uri(uri)
            .header("Content-Type", "text/xml")
            .POST(HttpRequest.BodyPublishers.ofString(request))
            .build();
    }

    public static void handleResponse(int[] result , HttpResponse response) throws Exception {
        String responseBody = response.body().toString();
        InputStream inputStream = new ByteArrayInputStream(responseBody.getBytes());
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document document = builder.parse(inputStream);
        System.out.println(responseBody);

        if (responseBody.contains("<fault>")) {
            String faultString = "";
            int faultCode = -1;

            NodeList faultMembers = document.getElementsByTagName("member");
            for (int i = 0; i < faultMembers.getLength(); i++) {
                Element member = (Element) faultMembers.item(i);
                String name = member.getElementsByTagName("name").item(0).getTextContent();
                String value = member.getElementsByTagName("value").item(0).getTextContent();
                switch (name) {
                    case "faultCode":
                        faultCode = Integer.parseInt(value);
                        break;
                    case "faultString":
                        faultString = value;
                        break;
                }
            }
            System.out.println("Error: \n FaultCode: " + faultCode + "\n Fault Message: " + faultString);
        } else {
            NodeList params = document.getElementsByTagName("param");
            Element valueElem = (Element) params.item(0).getFirstChild();
            Element typeElement = (Element) valueElem.getFirstChild();
            result[0] = Integer.parseInt(typeElement.getTextContent());
        }
    }
}
