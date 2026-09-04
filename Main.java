import java.io.IOException;
public class Main
{
    public static void main(String args[]) throws IOException
    {
        LeaseManager lm=new LeaseManager();//creating the shared secrets manager
        //starting background evictor thread to remove expired secrets
        Evictor ev=new Evictor(lm);
        Thread.ofVirtual().name("evictor-worker").start(ev);
        //initializing the HTTP server with manager to handle requests
        EntryPoint ep=new EntryPoint(lm);
        ep.executor(args);
        System.out.println("[+] Zero-trust Secret Broker active on port 8000");
    }
}