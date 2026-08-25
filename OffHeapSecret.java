import java.util.*;
import java.lang.foreign.*;
//import java.lang.foreign.ValueLayout.OfByte;
public class OffHeapSecret implements AutoCloseable
{
    MemorySegment secret=null;
    Arena arena=Arena.ofConfined();
    OffHeapSecret(byte[] secret)
    {
        try
        {
            this.secret=arena.allocate(secret.length);
            MemorySegment.copy(MemorySegment.ofArray(secret), 0, this.secret, 0, secret.length);
            //accessing any element 
            // ValueLayout.OfByte byteLayout=ValueLayout.JAVA_BYTE;
            // System.out.println(this.secret.get(byteLayout, 3));
            //System.out.println(this.secret.toString());
            Arrays.fill(secret, (byte)0);
            //System.out.println("Secret stored off-heap");
        }
        catch(Throwable t)
        {
            if (arena != null && arena.scope().isAlive())
                arena.close(); 
            throw new RuntimeException("Off-heap allocation failed", t);
        }
    }
    @Override
    public void close() throws Exception
    {
        secret.fill((byte)0);
        arena.close();
    }
    public static void main(String args[]) throws Exception
    {
        byte arr[]={1,3,8,127};
        OffHeapSecret obj=new OffHeapSecret(arr);
        obj.close();
    }
}