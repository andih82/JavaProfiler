package cc.hofstadler.examples;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaTests {

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(100L);
        foo();
        Thread.sleep(100L);
        System.out.println("main() expected: 200 ms");
    }

    public static void foo() throws InterruptedException {
        Thread.sleep(100L);
        bar();
        Thread.sleep(100L);
        bas();
        Thread.sleep(100L);
        bas();
        Thread.sleep(100L);
        bar();
        Thread.sleep(100L);
        bas();
        Thread.sleep(100L);
        System.out.println("foo() expected: 600 ms");
    }

    public static void bar() throws InterruptedException {
        Thread.sleep(100L);
        try {
            Thread.sleep(100L);
            baz();        // throws exception
            Thread.sleep(100L);  // Not reached
        } catch (Exception e){
            Thread.sleep(100L);
            System.out.println("exception");

        } finally {
            Thread.sleep(100L);
            System.out.println("bar() expected: 800");

        }
    }

    public static void baz() throws Exception{
        Thread.sleep(100L);
        System.out.println("baz() expected: 200");
        if(true) throw new Exception();
    }

    public static void bas() throws InterruptedException {
        Thread.sleep(100L);
        System.out.println("bas() expected: 300 ms");
    }


}
