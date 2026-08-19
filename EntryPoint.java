import com.sun.net.httpserver.*;
import java.io.*;
import java.util.*;
import java.net.*;
public class EntryPoint
{
    static LeaseManager manager=new LeaseManager();
    public EntryPoint(LeaseManager manager)
    {
        EntryPoint.manager=manager;
    }
    public static void main(String[] args) throws IOException
    {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new Handler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }
    static class Handler implements HttpHandler
    {
        @Override
        public void handle(HttpExchange request) throws IOException
        {
            if("POST".equalsIgnoreCase(request.getRequestMethod()))
            {
                byte[] temp=request.getRequestBody().readAllBytes();
                OffHeapSecret phantom=new OffHeapSecret(temp);
                Arrays.fill(temp,(byte)0);
                long time = Long.parseLong(request.getRequestHeaders().get("secret-ttl").get(0));
                String ownerId=request.getRequestHeaders().get("owner-id").get(0);
                String secretId=request.getRequestHeaders().get("secret-id").get(0);
                SecretLease lease=new SecretLease(secretId,ownerId,time,phantom);
                manager.store(lease);
            }

            if("GET"==request.getRequestMethod())
            {
                String q=request.getResponseHeaders().getFirst("Authorization");
                if(authenticate(q))//authenticates the bearer token
                    
            }
        }
        public boolean authenticate(String q)
        {
            if(q==)
        }
    }
}