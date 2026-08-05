import java.util.concurrent.*;
public record SecretLease (String secretId,String ownerId,long expirationTimeEpoch,OffHeapSecret secret) implements Delayed
{
    @Override
    public long getDelay(TimeUnit unit)
    {
        return unit.convert(this.expirationTimeEpoch - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }
    @Override
    public int compareTo(Delayed o)
    {
        return Long.compare(this.expirationTimeEpoch, ((SecretLease)o).expirationTimeEpoch);
    }
}