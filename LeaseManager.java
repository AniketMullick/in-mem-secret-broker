import java.util.concurrent.*;
public class LeaseManager
{
    ConcurrentMap<String, SecretLease> leaseMap=new ConcurrentHashMap<>();
    DelayQueue<SecretLease> dq=new DelayQueue<>();
    public void store(SecretLease secret)
    {
        leaseMap.put(secret.secretId(), secret);
        dq.add(secret);
    }
    public SecretLease retrieve(String secretId,String ownerId) throws SecurityException,IllegalArgumentException,Exception
    {
        SecretLease secret=leaseMap.get(secretId);
        if(secret!=null)
            if(secret.ownerId().equals(ownerId))
                return leaseMap.get(secretId);
            else
                throw new SecurityException("Owner mismatch.");
        else
            throw new IllegalArgumentException("Lease not found");
    }
}