import com.sun.net.httpserver.*;
import java.io.*;
import java.util.*;
import java.net.*;
public class EntryPoint
{
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