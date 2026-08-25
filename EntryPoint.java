import com.sun.net.httpserver.*;
import java.io.*;
import java.lang.foreign.ValueLayout;
import java.util.*;
import java.net.*;
public class EntryPoint
{
    LeaseManager manager=new LeaseManager();
    public EntryPoint(LeaseManager manager)
    {
        this.manager=manager;
    }
    public void executor(String[] args) throws IOException
    {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new Handler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }
    class Handler implements HttpHandler
    {
        @Override
        public void handle(HttpExchange request) throws IOException
        {
            if("POST".equalsIgnoreCase(request.getRequestMethod()))
            {
                
                if(!request.getRequestHeaders().containsKey("secret-ttl")||!request.getRequestHeaders().containsKey("owner-id"))
                {
                    String message="Bad request: Missing important header(s)";
                    byte[] bytes=message.getBytes();
                    request.sendResponseHeaders(400,bytes.length);
                    try(OutputStream os=request.getResponseBody()){
                        os.write(bytes, 0, bytes.length);
                    }
                    return;
                }
                byte[] temp=request.getRequestBody().readAllBytes();
                OffHeapSecret phantom;
                try{phantom=new OffHeapSecret(temp);}
                catch(Throwable t)
                {
                    System.err.println("Allocation failed [" + t.getClass().getSimpleName() + "]: " + t.getMessage());
                    t.printStackTrace();
                    request.sendResponseHeaders(500, -1);
                    return;
                }
                finally{Arrays.fill(temp,(byte)0);}
                long time = Long.parseLong(request.getRequestHeaders().get("secret-ttl").get(0))*1000+System.currentTimeMillis();
                String ownerId=request.getRequestHeaders().get("owner-id").get(0);
                String secretId=UUID.randomUUID().toString();
                SecretLease lease=new SecretLease(secretId,ownerId,time,phantom);
                manager.store(lease);
                byte[] bytes=secretId.getBytes();
                request.sendResponseHeaders(201,bytes.length);
                try(OutputStream os=request.getResponseBody()){
                os.write(bytes, 0,bytes.length);
                }
            }
            else if("GET".equalsIgnoreCase(request.getRequestMethod()))
            {
                byte[] result={};
                try{
                    String path=request.getRequestURI().getPath();
                    String secretId=path.substring(path.lastIndexOf('/')+1);
                    String ownerId="";
                    try{ownerId=request.getRequestHeaders().get("owner-id").get(0);}
                    catch(NullPointerException e)
                    {
                        String message="Bad Request: Missing header component 'owner-id'";
                        byte[] bytes=message.getBytes();
                        request.sendResponseHeaders(400,bytes.length);
                        try(OutputStream os=request.getResponseBody()){
                            os.write(bytes, 0, bytes.length);
                        }
                        return;
                    }
                    SecretLease record;
                    try{record=manager.retrieve(secretId, ownerId);}
                    catch(SecurityException e)
                    {
                        String message="Forbidden: "+e.getMessage();
                        byte[] bytes=message.getBytes();
                        request.sendResponseHeaders(403,bytes.length);
                        try(OutputStream os=request.getResponseBody()){
                            os.write(bytes, 0, bytes.length);
                        }
                        return;
                    }
                    catch(IllegalArgumentException e)
                    {
                        String message="Not found: "+e.getMessage();
                        byte[] bytes=message.getBytes();
                        request.sendResponseHeaders(404,bytes.length);
                        try(OutputStream os=request.getResponseBody()){
                            os.write(bytes, 0, bytes.length);
                        }
                        return;
                    }
                    OffHeapSecret secret=record.secret();
                    result=secret.secret.toArray(ValueLayout.JAVA_BYTE);
                    request.sendResponseHeaders(200,result.length);
                    try(OutputStream os=request.getResponseBody()){
                        os.write(result, 0, result.length);
                    }
                }
                finally{Arrays.fill(result,(byte)0);}
            }
        }
    }
}