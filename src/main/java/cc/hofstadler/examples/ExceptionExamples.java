package cc.hofstadler.examples;

public class ExceptionExamples {

    public static void main(String[] args) {
        try {
            normalTryCatchFinallyBlock();
            normalTryCatchBlock();
            normalTryFinallyBlock();
            nestedTryCatchFinallyBlock();
            nestedTryCatchBlock();
            nestedTryFinallyBlock();
        } catch (InterruptedException ire){
            throw new RuntimeException(ire);
        }
    }

    public static void normalTryCatchFinallyBlock() throws InterruptedException {
        System.out.println("ExceptionExamples.normalTryCatchFinallyBlock");
        System.out.println("enter...");
        try {
            Thread.sleep(100);   //100
            System.out.println("try");
            throwing();
        } catch (Exception e) {
            Thread.sleep(100);  //100
            System.out.println("catch");
        } finally {
            Thread.sleep(100);  //100
            System.out.println("finally");
        }
        System.out.println("exit..."); //300
        System.out.println("ExceptionExamples.normalTryCatchFinallyBlock  ... 300 ms");
    }

    public static void normalTryCatchBlock() throws InterruptedException {
        System.out.println("ExceptionExamples.normalTryCatchBlock");
        System.out.println("enter...");
        Thread.sleep(100);  // 100
        try {
            System.out.println("try");
            Thread.sleep(100);  // 100
            throwing();
        } catch (Exception e) {
            System.out.println("catch");
            Thread.sleep(100); // 100
        }
        Thread.sleep(100);  // 100
        System.out.println("exit...");
        System.out.println("ExceptionExamples.normalTryCatchBlock  ...  400 ms");
    }

    public static void normalTryFinallyBlock() throws InterruptedException {
        System.out.println("ExceptionExamples.normalTryFinallyBlock");
        System.out.println("enter...");
        Thread.sleep(100);  // 100
        try {
            Thread.sleep(100);  // 100
            System.out.println("try");
        } finally {
            Thread.sleep(100);  // 100
            System.out.println("finally");
        }
        Thread.sleep(100);  // 100
        System.out.println("exit...");
        System.out.println("ExceptionExamples.normalTryFinallyBlock  ... 400 ms");
    }

    public static void nestedTryCatchFinallyBlock() throws InterruptedException {
        System.out.println("ExceptionExamples.nestedTryCatchFinallyBlock");
        System.out.println("enter...");
        Thread.sleep(100);  // 100
        try {
            System.out.println("try");
            Thread.sleep(100);  // 100
            try {
                System.out.println("  nested try");
                Thread.sleep(100);  // 100
            } catch (Exception e) {
                System.out.println("  nested catch");
                Thread.sleep(100);  //  0  not reached
            } finally {
                System.out.println("  nested finally");
                Thread.sleep(100);  // 100
            }
        } catch (Exception e) {
            System.out.println("catch");
            Thread.sleep(100);    //  0  not reached
            try {
                throwing();
                System.out.println("  nested try");
                Thread.sleep(100); //  0  not reached
            } catch (Exception e1) {
                System.out.println("  nested catch");
                Thread.sleep(100); //  0  not reached
            } finally {
                System.out.println("  nested finally");
                Thread.sleep(100); //  0  not reached
            }
        } finally {
            System.out.println("finally");
            Thread.sleep(100);  // 100
            try {
                System.out.println("  nested try");
                Thread.sleep(100);  // 100
            } catch (Exception e) {
                System.out.println("  nested catch");
                Thread.sleep(100);   //  0  not reached
            } finally {
                System.out.println("  nested finally");
                Thread.sleep(100);  // 100
            }
        }
        System.out.println("exit...");
        System.out.println("ExceptionExamples.nestedTryCatchFinallyBlock ... 700 ms ");

    }

    public static void nestedTryCatchBlock() throws InterruptedException {
        System.out.println("enter...");
        Thread.sleep(100);
        try {
            System.out.println("try");
            Thread.sleep(100);
        } catch (Exception e) {
            System.out.println("catch");
            Thread.sleep(100);
            try {
                System.out.println("  nested try");
                Thread.sleep(100);
                try {
                    System.out.println("  multi nested try");
                    Thread.sleep(100);
                } catch (Exception e1) {
                    System.out.println("  multi nested catch");
                    Thread.sleep(100);
                } finally {
                    System.out.println("  multi nested finally");
                    Thread.sleep(100);
                }
            } catch (Exception e1) {
                System.out.println("  nested catch");
                Thread.sleep(100);
            } finally {
                System.out.println("  nested finally");
                Thread.sleep(100);
            }
        }
        System.out.println("exit...");
    }

    public static void nestedTryFinallyBlock() throws InterruptedException {
        System.out.println("enter...");
        Thread.sleep(100);
        try {
            System.out.println("try");
            Thread.sleep(100);
        } finally {
            System.out.println("finally");
            Thread.sleep(100);
            try {
                System.out.println("  nested try");
                Thread.sleep(100);
            } catch (Exception e) {
                System.out.println("  nested catch");
                Thread.sleep(100);
            } finally {
                System.out.println("  nested finally");
                Thread.sleep(100);
            }
        }
        System.out.println("exit...");
    }

    public static void throwing() throws Exception {
        if (true)throw new Exception();
    }
    public static void notCatching() throws Exception {
        Thread.sleep(100);
        throwing();
    }

}
