
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Algorithms from https://github.com/TheAlgorithms/
 *
 */
public class Example {

    public static void main(String[] args) throws Exception {

        KnightsTour.knightsTourMain();
        //Mergesort
        MergeSortRecursive sort = new MergeSortRecursive(new ArrayList<>(Arrays.asList(12,556,393,68909,3,2,44,2,1,887,523,1422,4, 3, 1, 8, 5, 10, 0, 1,967,111,345,21,4,5,88, 4, 11, 8, 9)));
        System.out.println(sort.mergeSort());

        PowerSum ps = new PowerSum();
        ps.powSum(100, 3);

        MonteCarloTreeSearch.main(args);

        Parentclass pc = new Parentclass();
        Childclass cc1 = new Childclass();
        Childclass cc2 = new Childclass("World!");
        Childclass cc3 = new Childclass(false);

        ExceptionHandling.exceptionMain();
        ExceptionHandling.throwingException(false);
        ExceptionHandling.catchingException(false);



    }
}

/**
 * https://github.com/TheAlgorithms/Java/blob/master/src/main/java/com/thealgorithms/backtracking/KnightsTour.java
 */
class KnightsTour {

    private static final int base = 12;
    private static final int[][] moves = {
            {1, -2},
            {2, -1},
            {2, 1},
            {1, 2},
            {-1, 2},
            {-2, 1},
            {-2, -1},
            {-1, -2},
    }; // Possible moves by knight on chess
    private static int[][] grid; // chess grid
    private static int total; // total squares in chess

    public static void knightsTourMain() {
        grid = new int[base][base];
        total = (base - 4) * (base - 4);

        for (int r = 0; r < base; r++) {
            for (int c = 0; c < base; c++) {
                if (r < 2 || r > base - 3 || c < 2 || c > base - 3) {
                    grid[r][c] = -1;
                }
            }
        }

        int row = 2 + (int) (Math.random() * (base - 4));
        int col = 2 + (int) (Math.random() * (base - 4));

        grid[row][col] = 1;

        if (solve(row, col, 2)) {
            printResult();
        } else {
            System.out.println("no result");
        }
    }

    // Return True when solvable
    private static boolean solve(int row, int column, int count) {
        if (count > total) {
            return true;
        }

        List<int[]> neighbor = neighbors(row, column);

        if (neighbor.isEmpty() && count != total) {
            return false;
        }

        neighbor.sort(Comparator.comparingInt(a -> a[2]));

        for (int[] nb : neighbor) {
            row = nb[0];
            column = nb[1];
            grid[row][column] = count;
            if (!orphanDetected(count, row, column) && solve(row, column, count + 1)) {
                return true;
            }
            grid[row][column] = 0;
        }

        return false;
    }

    // Returns List of neighbours
    private static List<int[]> neighbors(int row, int column) {
        List<int[]> neighbour = new ArrayList<>();

        for (int[] m : moves) {
            int x = m[0];
            int y = m[1];
            if (grid[row + y][column + x] == 0) {
                int num = countNeighbors(row + y, column + x);
                neighbour.add(new int[] {row + y, column + x, num});
            }
        }
        return neighbour;
    }

    // Returns the total count of neighbors
    private static int countNeighbors(int row, int column) {
        int num = 0;
        for (int[] m : moves) {
            if (grid[row + m[1]][column + m[0]] == 0) {
                num++;
            }
        }
        return num;
    }

    // Returns true if it is orphan
    private static boolean orphanDetected(int count, int row, int column) {
        if (count < total - 1) {
            List<int[]> neighbor = neighbors(row, column);
            for (int[] nb : neighbor) {
                if (countNeighbors(nb[0], nb[1]) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    // Prints the result grid
    private static void printResult() {
        for (int[] row : grid) {
            for (int i : row) {
                if (i == -1) {
                    continue;
                }
                System.out.printf("%2d ", i);
            }
            System.out.println();
        }
    }
}

/**
 * https://github.com/TheAlgorithms/Java/blob/master/src/main/java/com/thealgorithms/backtracking/PowerSum.java
 */
class PowerSum {

    private int count = 0, sum = 0;

    public int powSum(int N, int X) {
        Sum(N, X, 1);
        return count;
    }

    // here i is the natural number which will be raised by X and added in sum.
    public void Sum(int N, int X, int i) {
        // if sum is equal to N that is one of our answer and count is increased.
        if (sum == N) {
            count++;
            return;
        } // we will be adding next natural number raised to X only if on adding it in sum the
        // result is less than N.
        else if (sum + power(i, X) <= N) {
            sum += power(i, X);
            Sum(N, X, i + 1);
            // backtracking and removing the number added last since no possible combination is
            // there with it.
            sum -= power(i, X);
        }
        if (power(i, X) < N) {
            // calling the sum function with next natural number after backtracking if when it is
            // raised to X is still less than X.
            Sum(N, X, i + 1);
        }
    }

    // creating a separate power function so that it can be used again and again when required.
    private int power(int a, int b) {
        return (int) Math.pow(a, b);
    }
}

/**
 * https://github.com/TheAlgorithms/Java/blob/master/src/main/java/com/thealgorithms/sorts/MergeSortRecursive.java
 */
class MergeSortRecursive {

    List<Integer> arr;

    public MergeSortRecursive(List<Integer> arr) {
        this.arr = arr;
    }

    public List<Integer> mergeSort() {
        return merge(arr);
    }

    private static List<Integer> merge(List<Integer> arr) {
        // base condition
        if (arr.size() <= 1) {
            return arr;
        }

        int arrLength = arr.size();
        int half = arrLength / 2;
        List<Integer> arrA = arr.subList(0, half);
        List<Integer> arrB = arr.subList(half, arr.size());

        // recursion
        arrA = merge(arrA);
        arrB = merge(arrB);

        return sort(arrA, arrB);
    }

    private static List<Integer> sort(List<Integer> unsortedA, List<Integer> unsortedB) {
        if (unsortedA.size() <= 0 && unsortedB.size() <= 0) {
            return new ArrayList<>();
        }
        if (unsortedA.size() <= 0) {
            return unsortedB;
        }
        if (unsortedB.size() <= 0) {
            return unsortedA;
        }
        if (unsortedA.get(0) <= unsortedB.get(0)) {
            List<Integer> newAl = new ArrayList<Integer>() {
                { add(unsortedA.get(0)); }
            };
            newAl.addAll(sort(unsortedA.subList(1, unsortedA.size()), unsortedB));
            return newAl;
        } else {
            List<Integer> newAl = new ArrayList<Integer>() {
                { add(unsortedB.get(0)); }
            };
            newAl.addAll(sort(unsortedA, unsortedB.subList(1, unsortedB.size())));
            return newAl;
        }
    }
}



class Parentclass{

    Parentclass() {
        waiting();
        System.out.println("Parentclass constructor call");
    }

    Parentclass(String tag){
        waiting();
        System.out.println("Parentclass constructor call tag: " + tag);
    }

    protected void waiting(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Childclass extends Parentclass{

    Childclass(){
        super();
        waiting();
        System.out.println("Childclass consructor call");
    }

    Childclass(String hello)  {
        this();
        waiting();
        System.out.println("Hello " + hello);
    }

    Childclass(boolean throwing) throws InterruptedException, IOException {
        super("throwing : " + throwing);
        if(throwing){
            throw new IOException("throwing in child constrctor");
        }
        Thread.sleep(100);
    }
}

class ExceptionHandling{

    public static void exceptionMain() throws RuntimeException, Exception{
        Thread.sleep(100);
    }

    public static void throwingException(boolean throwing) throws Exception {
        Thread.sleep(100);
        throwing(throwing);
    }

    public static void catchingException(boolean throwing) throws Exception {
        Thread.sleep(100);
        try {
            throwing(throwing);
        }catch (Exception e){
            System.out.println("caught");
        }
    }

    private static void throwing(boolean throwing) throws Exception {
        Thread.sleep(100);
        if(throwing){
            throw new Exception("Throwing");
        }
        Thread.sleep(100);
    }



}