package org.example;

import java.io.File;

public class JavaProgram {

    static public void main(String[] args) {/*Begin */

        System.out.println(gcdRecursie(16, 60));
        System.out.println(gcdWhile(16,60));
        System.out.println(UnnecassaryComplexMath.plus(23,12));
        System.out.println(UnnecassaryComplexMath.minus(23,12));
        System.out.println(UnnecassaryComplexMath.multiply(23,12));
        System.out.println(UnnecassaryComplexMath.divide(23,12));

        System.out.println(MathWrapper.plus(23,12));
        System.out.println(MathWrapper.minus(23,12));
        System.out.println(MathWrapper.multiply(23,12));
        System.out.println(MathWrapper.divide(23,12));

        System.out.println("Exit");
    }

    public static int gcdWhile(int num1, int num2) {
        while (num2 != 0) {
            int temp = num2;
            num2 = num1 % num2;
            num1 = temp;
        }
        ;return num1;
    }

    public static int gcdUCRecursie(int num1, int num2) {
        if (num2 == 0) {
          return num1;
        }
        return gcdRecursie(num2, num1 % num2);
    }

    public static int gcdUCWhile(int num1, int num2) {
        while (num2 != 0) {
            int temp = num2;
            num2 = num1 % num2;
            num1 = temp;
        }
        ;return num1;
    }

    public static int gcdRecursie(int num1, int num2) {
        if (num2 == 0) {
            return num1;
        }
        return gcdRecursie(num2, num1 % num2);
    }

}

class MathWrapper2{

    public static int plus(int num1, int num2){
        return num1 + num2;
    }

    public static int minus(int num1, int num2){
        return num1 - num2;
    }

    public static int multiply(int num1, int num2){
        return num1 * num2;
    }

    public static int divide(int num1, int num2){
        return num1 / num2;
    }
}

class UnnecassaryComplexMath1{

    public static int plus(int num1, int num2){
        int first = 0;
        while (first < num1){
            first = first + 1;
        }
        for(int i = 0; i < num2; i++){
            first = first + 1;
        }
        return first;
    }

    public static int minus(int num1, int num2){
        int sub = plus(0, num1);
        do{
            sub = sub - 1;
        }while(--num2 > 0);
        return sub;
    }

    public static int multiply(int num1, int num2){
        int result = 0;
        for(int i = num2; i > 0; i--){
            result = plus(result, num1);
        }
        return result;
    }

    public static int divide(int num1, int num2) {
        int result = 0;
        while (num1 > num2){
            result = plus(result,1);
            num1 -= num2;
        }
        return result;
    }
}


