package edu.uw.info314.xmlrpc.server;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import org.xml.sax.InputSource;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import static spark.Spark.*;

class Call {
    public String name;
    public List<Object> args = new ArrayList<Object>();
}

public class App {
    public static final Logger LOG = Logger.getLogger(App.class.getCanonicalName());

    public static void main(String[] args) {
        Calc calc = new Calc();
        LOG.info("Starting up on port 8080");
        port(8080);

        // This is the mapping for POST requests to "/RPC";
        // this is where you will want to handle incoming XML-RPC requests

        before((req, res) -> {
            if (!req.uri().equals("/RPC")) {
                halt(404, "Not Found");
            } else if (!req.requestMethod().equals("POST")) {
                halt(405, "Method Not Supported");
            }
        });

        post("/RPC", (req, res) -> { 
            String body = req.body();
            LOG.info(body);
            Call call = new Call();
            String faultString = null;
            int faultCode = -1;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new ByteArrayInputStream(body.getBytes())));
            
            NodeList methodList = document.getElementsByTagName("methodName");
            call.name = methodList.item(0).getTextContent();
            
            NodeList paramList = document.getElementsByTagName("param");
            ArrayList<Object> params = new ArrayList<Object>();
            for (int i = 0; i < paramList.getLength(); i++) {
                Element valueElem = (Element) paramList.item(i).getFirstChild();
                Element typeElement = (Element) valueElem.getFirstChild();
                if ("i4".equals(typeElement.getTagName())) {
                    int value = Integer.parseInt(valueElem.getTextContent());
                    params.add(value);
                } else {
                    faultCode = 3;
                    faultString = "illegal argument type";
                }
            }
            call.args = params;
            Integer methodResult = null;
            if (faultCode == -1) {
                switch (call.name) {
                    case "add": {
                        int sum = 0;
                        int[] intParams = call.args.stream().mapToInt(n -> (int) n).toArray();
                        for (int param : intParams) {
                            int prevSum = sum;
                            sum = calc.add(sum, param);
                            if ((sum - param) != prevSum | (sum + param) != prevSum) {
                                faultCode = 2;
                                faultString = "overflow/underflow error";
                            }
                        }
                        methodResult = sum;
                        break;
                    }
                    case "subtract": {
                        int lhs = (Integer)call.args.get(0);
                        int rhs = (Integer)call.args.get(1);
                        methodResult = calc.subtract(lhs, rhs);
                        break;
                    }
                    case "multiply": {
                        int product = 1;
                        int[] intParams = call.args.stream().mapToInt(n -> (int) n).toArray();
                        for (int param : intParams) {
                            int prevProduct = product;
                            product = calc.multiply(product, param);
                            if ((product / param) != prevProduct) {
                                faultCode = 2;
                                faultString = "overflow/underflow error";
                            }
                        }
                        methodResult = product;
                        break;
                    }
                    case "divide": {
                        int lhs = (Integer)call.args.get(0);
                        int rhs = (Integer)call.args.get(1);
                        if (rhs == 0) {
                            faultCode = 1;
                            faultString = "divide by zero";
                            break;
                        }
                        methodResult = calc.divide(lhs, rhs);
                        break;
                    }
                    case "modulo": {
                        int lhs = (Integer)call.args.get(0);
                        int rhs = (Integer)call.args.get(1);
                        if (rhs == 0) {
                            faultCode = 1;
                            faultString = "divide by zero";
                            break;
                        }
                        methodResult = calc.modulo(lhs, rhs);
                        break;
                    }
                    default: {
                        faultCode = 4;
                        faultString = "invalid method";
                        break;
                    }
                }
            }
            String payload = "<?xml version=\"1.0\"?><methodResponse>";
            if (faultCode != -1) {
                payload += "<fault><value><struct><member><name><value><i4>" + faultCode + "</i4></value></name></member><member><name><value><string>" + faultString + "</string></value></name></member></struct></value></fault>";
                res.status(400);
            } else {
                payload += "<params><param><value><i4>" + methodResult + "</i4></value></param></params>";
                res.status(200);
            }
            payload += "</methodResponse>";
            return payload;
        });
    }
}
