package cc.hofstadler.examples;

import java.util.*;

public class JavaTests {

    public static Map<java.lang.String, Integer> map = new HashMap<String, Integer>(){
      public Integer put(String key, Integer value){
          System.out.println("Adding: " + key + " -> " + value);
          return super.put(key, value);
      }
    };

    public static void main(String[] args) throws InterruptedException {

        List list = new ArrayList() {

            @Override
            public boolean add(Object o) {
                System.out.println("Adding: " + o);
                Runnable action = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Hello world!");
                    }
                };
                action.run();
                Runnable action2 = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Hello world!");
                    }
                };
                action2.run();
                return super.add(o);
            }
        };

        Runnable action = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello world!");
            }
        };

        action.run();
        list.add("Hello world!");

        map.put("Hello world!",1);
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
