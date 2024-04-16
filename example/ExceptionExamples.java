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
        } catch (Exception e){

        }
        System.out.println("ExceptionExamples.notCatching  ... 100 ms");
    }

    public static void normalTryCatchFinallyBlock() throws InterruptedException {
        try {
            Thread.sleep(100);   //100
            throwing();
        } catch (Exception e) {
            Thread.sleep(100);  //100
        } finally {
            Thread.sleep(100);  //100
        }
        System.out.println("ExceptionExamples.normalTryCatchFinallyBlock  ... 300 ms");
    }

    public static void normalTryCatchBlock() throws InterruptedException {
        Thread.sleep(100);  // 100
        try {
            Thread.sleep(100);  // 100
            throwing();
        } catch (Exception e) {
            Thread.sleep(100); // 100
        }
        Thread.sleep(100);  // 100
        System.out.println("ExceptionExamples.normalTryCatchBlock  ...  400 ms");
    }

    public static void normalTryFinallyBlock() throws InterruptedException {
        Thread.sleep(100);  // 100
        try {
            Thread.sleep(100);  // 100
        } finally {
            Thread.sleep(100);  // 100
        }
        Thread.sleep(100);  // 100
        System.out.println("ExceptionExamples.normalTryFinallyBlock  ... 400 ms");
    }

    public static void nestedTryCatchFinallyBlock() throws InterruptedException {
        Thread.sleep(100);  // 100
        try {
            Thread.sleep(100);  // 100
            try {
                Thread.sleep(100);  // 100
            } catch (Exception e) {
                Thread.sleep(100);  //  0  not reached
            } finally {
                Thread.sleep(100);  // 100
            }
        } catch (Exception e) {
            Thread.sleep(100);    //  0  not reached
            try {
                throwing();
                Thread.sleep(100); //  0  not reached
            } catch (Exception e1) {
                Thread.sleep(100); //  0  not reached
            } finally {
                Thread.sleep(100); //  0  not reached
            }
        } finally {
            Thread.sleep(100);  // 100
            try {
                Thread.sleep(100);  // 100
            } catch (Exception e) {
                Thread.sleep(100);   //  0  not reached
            } finally {
                Thread.sleep(100);  // 100
            }
        }
        System.out.println("ExceptionExamples.nestedTryCatchFinallyBlock ... 700 ms ");

    }

    public static void nestedTryCatchBlock() throws InterruptedException {
        Thread.sleep(100);  // 100
        try {
            Thread.sleep(100); // 100
            throwing();
        } catch (Exception e) {
            Thread.sleep(100);  // 100
            try {
                Thread.sleep(100); // 100
                try {
                    throwing();
                    Thread.sleep(100);  //no
                } catch (Exception e1) {
                    Thread.sleep(100);  // 100
                } finally {
                    Thread.sleep(100);  // 100
                }
            } catch (Exception e1) {
                Thread.sleep(100);  // no
            } finally {
                Thread.sleep(100);  // 100
            }
        }
        System.out.println("ExceptionExamples.nestedTryCatchBlock ... 700 ms");
    }

    public static void nestedTryFinallyBlock() throws Exception {
        Thread.sleep(100);  // 100
        try {
            notCatching();
            Thread.sleep(100);  // no
        } finally {
            Thread.sleep(100);  // 100
            try {
                Thread.sleep(100);   // 100
            } catch (Exception e) {
                Thread.sleep(100);  // no
            } finally {
                Thread.sleep(100);  // 100
            }
            System.out.println("ExceptionExamples.nestedTryFinallyBlock ... 400 ms ");
        }
        System.out.println("unreached");
    }

    public static void throwing() throws Exception {
        if (true)throw new Exception();
    }
    public static void notCatching() throws Exception {
        Thread.sleep(100);
        throwing();
    }

}
