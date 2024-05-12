public class ClassesTest {

    InnerClass innerClass;
    ClassesTest() throws InterruptedException {
        System.out.println("ClassesTest constructor");
        Thread.sleep(100L);
    }

    ClassesTest(int i) throws InterruptedException {
        System.out.println("ClassesTest constructor with int");
        Thread.sleep(100L);
        innerClass = new InnerClass();
    }


    void outterMethod() throws InterruptedException {
        System.out.println("ClassesTest outterMethod");
        Thread.sleep(100L);
    }

    class InnerClass {
        InnerClass() throws InterruptedException {
            System.out.println("InnerClass constructor");
            Thread.sleep(100L);
        }

        void innerMethod() throws InterruptedException {
            System.out.println("InnerClass innerMethod");
            Thread.sleep(100L);
            InnerInnerClass innerInnerClass = new InnerInnerClass();
            innerInnerClass.innerInnerMethod();
        }

        class InnerInnerClass {
            InnerInnerClass() throws InterruptedException {
                System.out.println("InnerInnerClass constructor");
                Thread.sleep(100L);
            }

            void innerInnerMethod() throws InterruptedException {
                System.out.println("InnerInnerClass innerInnerMethod");
                Thread.sleep(100L);
            }
        }

        void innerMethod2() throws InterruptedException {
            System.out.println("InnerClass innerMethod2");
            Thread.sleep(100L);
            InnerInnerClass innerInnerClass = new InnerInnerClass();
            innerInnerClass.innerInnerMethod();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ClassesTest classesTest = new ClassesTest();
        classesTest.innerClass = classesTest.new InnerClass();
        classesTest.innerClass.innerMethod();
        ClassesTest classesTest2 = new ClassesTest(1);
        classesTest2.innerClass.innerMethod();
    }

    void outterMethod2() throws InterruptedException {
        System.out.println("ClassesTest outterMethod");
        Thread.sleep(100L);
    }

}
