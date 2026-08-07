import java.util.concurrent.*;
public class Evictor implements Runnable
{
    BlockingQueue<SecretLease> DQ= new DelayQueue<SecretLease>();
    public void run()
    {
    }
}