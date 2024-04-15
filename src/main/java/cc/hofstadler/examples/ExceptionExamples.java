package cc.hofstadler.examples;

public class ExceptionExamples {

    public static void main(String[] args) {
        normalTryCatchFinallyBlock();
        normalTryCatchBlock();
        normalTryFinallyBlock();
        nestedTryCatchFinallyBlock();
        nestedTryCatchBlock();
        nestedTryFinallyBlock();
    }

    public static void throwing() throws Exception {
        if (true)throw new Exception();
    }
    public static void normalTryCatchFinallyBlock() {
        System.out.println("ExceptionExamples.normalTryCatchFinallyBlock");
        System.out.println("enter...");
        try {
            waiting();
            System.out.println("try");
        } catch (Exception e) {
            waiting();
            System.out.println("catch");
        } finally {
            waiting();
            System.out.println("finally");
        }
        System.out.println("exit...");
    }

    public static void normalTryCatchBlock() {
        System.out.println("ExceptionExamples.normalTryCatchBlock");
        System.out.println("enter...");
        waiting();
        try {
            System.out.println("try");
            waiting();
        } catch (Exception e) {
            System.out.println("catch");
            waiting();
        }
        System.out.println("exit...");
    }

    public static void normalTryFinallyBlock() {
        System.out.println("ExceptionExamples.normalTryFinallyBlock");
        System.out.println("enter...");
        try {
            waiting();
            System.out.println("try");
        } finally {
            waiting();
            System.out.println("finally");
        }
        waiting();
        System.out.println("exit...");
    }

    public static void nestedTryCatchFinallyBlock() {
        System.out.println("enter...");
        waiting();
        try {
            System.out.println("try");
            waiting();
            try {
                System.out.println("  nested try");
                waiting();
            } catch (Exception e) {
                System.out.println("  nested catch");
                waiting();
            } finally {
                System.out.println("  nested finally");
                waiting();
            }
        } catch (Exception e) {
            System.out.println("catch");
            waiting();
            try {
                throwing();
                System.out.println("  nested try");
                waiting();
            } catch (Exception e1) {
                System.out.println("  nested catch");
                waiting();
            } finally {
                System.out.println("  nested finally");
                waiting();
            }
        } finally {
            System.out.println("finally");
            waiting();
            try {
                System.out.println("  nested try");
                waiting();
            } catch (Exception e) {
                System.out.println("  nested catch");
                waiting();
            } finally {
                System.out.println("  nested finally");
                waiting();
            }
        }
        System.out.println("exit...");
    }

    public static void nestedTryCatchBlock() {
        System.out.println("enter...");
        waiting();
        try {
            System.out.println("try");
            waiting();
        } catch (Exception e) {
            System.out.println("catch");
            waiting();
            try {
                System.out.println("  nested try");
                waiting();
                try {
                    System.out.println("  multi nested try");
                    waiting();
                } catch (Exception e1) {
                    System.out.println("  multi nested catch");
                    waiting();
                } finally {
                    System.out.println("  multi nested finally");
                    waiting();
                }
            } catch (Exception e1) {
                System.out.println("  nested catch");
                waiting();
            } finally {
                System.out.println("  nested finally");
                waiting();
            }
        }
        System.out.println("exit...");
    }

    public static void nestedTryFinallyBlock() {
        System.out.println("enter...");
        waiting();
        try {
            System.out.println("try");
            waiting();
        } finally {
            System.out.println("finally");
            waiting();
            try {
                System.out.println("  nested try");
                waiting();
            } catch (Exception e) {
                System.out.println("  nested catch");
                waiting();
            } finally {
                System.out.println("  nested finally");
                waiting();
            }
        }
        System.out.println("exit...");
    }

    public static void waiting(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
