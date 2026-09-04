import java.util.*;
import java.lang.foreign.*;
//import java.lang.foreign.ValueLayout.OfByte;
public class OffHeapSecret implements AutoCloseable
{
    private MemorySegment secret=null;
    private Arena arena;
    public OffHeapSecret(byte[] secret)
    {
        this.arena=Arena.ofShared();
        try
        {
            this.secret=arena.allocate(secret.length);
            MemorySegment.copy(MemorySegment.ofArray(secret), 0, this.secret, 0, secret.length);
            //accessing any element 
            // ValueLayout.OfByte byteLayout=ValueLayout.JAVA_BYTE;
            // System.out.println(this.secret.get(byteLayout, 3));
            //System.out.println(this.secret.toString());
            //System.out.println("Secret stored off-heap");
        }
        catch(Throwable t)
        {
            if (arena != null && arena.scope().isAlive())
                arena.close(); 
            throw new RuntimeException("Off-heap allocation failed", t);
        }
        finally
        {
            Arrays.fill(secret, (byte)0);
        }
    }
    public MemorySegment getSecret()
    {
        return secret;
    }
    @Override
    public void close()
    {
        if (arena != null && arena.scope().isAlive())
        {
            secret.fill((byte)0);
            arena.close();
        }
    }
}