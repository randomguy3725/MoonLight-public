package net.optifine.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.src.Config;

public class HttpPipeline
{
    private static final Map mapConnections = new HashMap();
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_HOST = "Host";
    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_LOCATION = "Location";
    public static final String HEADER_KEEP_ALIVE = "Keep-Alive";
    public static final String HEADER_CONNECTION = "Connection";
    public static final String HEADER_VALUE_KEEP_ALIVE = "keep-alive";
    public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String HEADER_VALUE_CHUNKED = "chunked";

    public static void addRequest(String urlStr, HttpListener listener) throws IOException
    {
        addRequest(urlStr, listener, Proxy.NO_PROXY);
    }

    public static void addRequest(String urlStr, HttpListener listener, Proxy proxy) throws IOException
    {
        HttpRequest httprequest = makeRequest(urlStr, proxy);
        HttpPipelineRequest httppipelinerequest = new HttpPipelineRequest(httprequest, listener);
        addRequest(httppipelinerequest);
    }

    public static HttpRequest makeRequest(String urlStr, Proxy proxy) throws IOException
    {
        URL url = new URL(urlStr);

        if (!url.getProtocol().equals("http"))
        {
            throw new IOException("Only protocol http is supported: " + url);
        }
        else
        {
            String s = url.getFile();
            String s1 = url.getHost();
            int i = url.getPort();

            if (i <= 0)
            {
                i = 80;
            }

            String s2 = "GET";
            String s3 = "HTTP/1.1";
            Map<String, String> map = new LinkedHashMap();
            map.put("User-Agent", "Java/" + System.getProperty("java.version"));
            map.put("Host", s1);
            map.put("Accept", "text/html, image/gif, image/png");
            map.put("Connection", "keep-alive");
            byte[] abyte = new byte[0];
            HttpRequest httprequest = new HttpRequest(s1, i, proxy, s2, s, s3, map, abyte);
            return httprequest;
        }
    }

    public static void addRequest(HttpPipelineRequest pr)
    {
        HttpRequest httprequest = pr.getHttpRequest();

        for (HttpPipelineConnection httppipelineconnection = getConnection(httprequest.getHost(), httprequest.getPort(), httprequest.getProxy()); !httppipelineconnection.addRequest(pr); httppipelineconnection = getConnection(httprequest.getHost(), httprequest.getPort(), httprequest.getProxy()))
        {
            removeConnection(httprequest.getHost(), httprequest.getPort(), httprequest.getProxy(), httppipelineconnection);
        }
    }

    private static synchronized HttpPipelineConnection getConnection(String host, int port, Proxy proxy)
    {
        String s = makeConnectionKey(host, port, proxy);
        HttpPipelineConnection httppipelineconnection = (HttpPipelineConnection)mapConnections.get(s);

        if (httppipelineconnection == null)
        {
            httppipelineconnection = new HttpPipelineConnection(host, port, proxy);
            mapConnections.put(s, httppipelineconnection);
        }

        return httppipelineconnection;
    }

    private static synchronized void removeConnection(String host, int port, Proxy proxy, HttpPipelineConnection hpc)
    {
        String s = makeConnectionKey(host, port, proxy);
        HttpPipelineConnection httppipelineconnection = (HttpPipelineConnection)mapConnections.get(s);

        if (httppipelineconnection == hpc)
        {
            mapConnections.remove(s);
        }
    }

    private static String makeConnectionKey(String host, int port, Proxy proxy)
    {
        String s = host + ":" + port + "-" + proxy;
        return s;
    }

    public static byte[] get(String urlStr) throws IOException
    {
        return get(urlStr, Proxy.NO_PROXY);
    }

    public static byte[] get(String urlStr, Proxy proxy) throws IOException
    {
        if (urlStr.startsWith("file:"))
        {
            URL url = new URL(urlStr);
            InputStream inputstream = url.openStream();
            byte[] abyte = Config.readAll(inputstream);
            return abyte;
        }
        else
        {
            HttpRequest httprequest = makeRequest(urlStr, proxy);
            HttpResponse httpresponse = executeRequest(httprequest);

            if (httpresponse.getStatus() / 100 != 2)
            {
                throw new IOException("HTTP response: " + httpresponse.getStatus());
            }
            else
            {
                return httpresponse.getBody();
            }
        }
    }

    public static HttpResponse executeRequest(HttpRequest req) throws IOException
    {
        final Map<String, Object> map = new HashMap();
        String s = "Response";
        String s1 = "Exception";
        HttpListener httplistener = new HttpListener()
        {
            public void finished(HttpRequest req, HttpResponse resp)
            {
                synchronized (map)
                {
                    map.put("Response", resp);
                    map.notifyAll();
                }
            }
            public void failed(HttpRequest req, Exception e)
            {
                synchronized (map)
                {
                    map.put("Exception", e);
                    map.notifyAll();
                }
            }
        };

        synchronized (map)
        {
            HttpPipelineRequest httppipelinerequest = new HttpPipelineRequest(req, httplistener);
            addRequest(httppipelinerequest);

            try
            {
                map.wait();
            }
            catch (InterruptedException var10)
            {
                throw new InterruptedIOException("Interrupted");
            }

            Exception exception = (Exception)map.get("Exception");

            if (exception != null)
            {
                if (exception instanceof IOException)
                {
                    throw(IOException)exception;
                }
                else if (exception instanceof RuntimeException)
                {
                    throw(RuntimeException)exception;
                }
                else
                {
                    throw new RuntimeException(exception.getMessage(), exception);
                }
            }
            else
            {
                HttpResponse httpresponse = (HttpResponse)map.get("Response");

                if (httpresponse == null)
                {
                    throw new IOException("Response is null");
                }
                else
                {
                    return httpresponse;
                }
            }
        }
    }

    public static boolean hasActiveRequests()
    {
        for (Object o : mapConnections.values())
        {
            HttpPipelineConnection httppipelineconnection = (HttpPipelineConnection) o;
            if (httppipelineconnection.hasActiveRequests())
            {
                return true;
            }
        }

        return false;
    }
}
