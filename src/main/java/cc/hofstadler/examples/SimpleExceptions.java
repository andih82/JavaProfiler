package cc.hofstadler.examples;

public class SimpleExceptions {

    public static void main(String[] args) throws InterruptedException {

        tryCatch();
        tryCatchNested();
        tryCatchRecursive();
        try {
            tryFinally();
        }catch (Exception e){
            Thread.sleep(100);  // 100
        }
        System.out.println("SimpleExceptions.main  100 ms");
    }

    public static void tryCatch() throws InterruptedException {
        try {
            Thread.sleep(100);   // 100
            exceptionAtZero(0);
        } catch (Exception e){
  //          System.out.println("caught... ");
        } finally {
            Thread.sleep(100);   // 100
        }
        System.out.println("SimpleExceptions.tryCatch  200 ms");
    }

    public static void tryCatchNested() throws InterruptedException {
        try {
            Thread.sleep(100);   // 100
            exceptionAtZero(0);
        } catch (Exception e){
            try {
                Thread.sleep(100); // 100
                recursive(5);
            }catch (Exception e1){
        //        System.out.println("caught nested...");
            } finally {
                Thread.sleep(100);  // 100
            }
        } finally {
            Thread.sleep(100);   // 100
        }
        System.out.println("SimpleExceptions.tryCatchNested  400 ms");
    }

    public static void tryCatchRecursive() throws InterruptedException {
        try {
            Thread.sleep(100);   // 100
            recursive(5);
        } catch (Exception e){
   //         System.out.println("caught... ");
        } finally {
            Thread.sleep(100);   // 100
        }
        System.out.println("SimpleExceptions.tryCatchRecursive  200 ms");
    }

    public static void tryFinally() throws Exception {
        Thread.sleep(100);  //100
        try {
            recursive(1);
        } finally {
            Thread.sleep(100); //100
            System.out.println("SimpleExceptions.tryFinally  200 ms");
        }
    }

    public static void recursive(int n) throws Exception {
        exceptionAtZero(n);
        recursive(--n);
    }


    public static void exceptionAtZero (int n) throws Exception {
        if( n == 0 ){
            throw new Exception("reached zero");
        }
    }


}
