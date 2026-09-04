import java.util.concurrent.*;
public class Evictor implements Runnable
{
    BlockingQueue<SecretLease> DQ= new DelayQueue<SecretLease>();
    ConcurrentMap<String, SecretLease> lmap=new ConcurrentHashMap<>();
    public Evictor(LeaseManager lm)
    {
        DQ=lm.dq;
        lmap=lm.leaseMap;
    }
    @Override
    public void run()
    {
        try{
            while(true)
            {
                SecretLease top=DQ.take();
                lmap.remove(top.secretId());
                top.secret().close();
            }
        }
        catch(InterruptedException e)
        {
            System.err.println("Evictor thread interrupted: "+e.getMessage());
        }
        catch(Exception e)
        {
            System.out.println("Evictor thread exception: "+e.getMessage());
        }
    }
}