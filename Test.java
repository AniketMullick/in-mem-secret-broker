import java.util.ArrayList;
import java.lang.foreign.*;
public class Test
{
    void test()
    {
        // List<Integer> a=new ArrayList<>();
        // a.add(1);
        // a.add(2);
        // a.add(3);
        // a.add(4);
        // a.add(5);
        // a.add(6);
        // a.add(7);
        // a.remove(0);
        // System.out.println(a);
        Linker linker = Linker.nativeLinker();
        
        System.out.println(linker);
    }
    public static void main(String[] args)
    {
        Test t=new Test();
        t.test();
        System.out.println("Hello, World!");
    }
}