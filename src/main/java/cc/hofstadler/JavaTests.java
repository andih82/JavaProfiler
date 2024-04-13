package cc.hofstadler;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaTests {

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(100);
        foo();
        Thread.sleep(100);
        System.out.println("main() expected: 200 ms");
    }

    public static void foo() throws InterruptedException {
        Thread.sleep(100);
        bar();
        Thread.sleep(100);
        bas();
        Thread.sleep(100);
        bas();
        Thread.sleep(100);
        bar();
        Thread.sleep(100);
        bas();
        Thread.sleep(100);
        System.out.println("foo() expected: 600 ms");
    }

    public static void bar() throws InterruptedException {
        Thread.sleep(100);
        try {
            Thread.sleep(100);
            baz();
            Thread.sleep(100);  // not counted
        } catch (Exception e){
            Thread.sleep(100);  // not counted
            System.out.println("exception");

        } finally {
            Thread.sleep(100); // not counted
            System.out.println("bar() expected: 400");

        }
    }

    public static void baz() throws Exception{
        Thread.sleep(100);
        System.out.println("baz() expected: 200");
        if(true) throw new Exception();
    }

    public static void bas() throws InterruptedException {
        Thread.sleep(100);
        System.out.println("baz() expected: 300 ms");
    }

    class NestedClass{

        void someMethod(){
            int i = 1+1;
        }

        class DoublNestedClass{

        }
    }
}
