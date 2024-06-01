public class SimpleLoop {

    static boolean withNested = false;
    static boolean withNested2 = false;
    static int nMthode = 0;
    static int nNested = 0;
    static int nNested2 = 0;

    public static void main(String[] args) {
        long start = System.nanoTime();
        int n = Integer.parseInt(args[0]);
        if (args.length > 1) withNested = Boolean.parseBoolean(args[1]);
        if (args.length > 2) withNested2 = Boolean.parseBoolean(args[2]);
        for (int i = 0; i < n; i++) methode(i);
        long end = System.nanoTime();
        System.out.println("SimpleLoop.main  " + (end - start) / 1000000 + " ms");
        System.out.println("nMthode: " + nMthode + " nNested: " + nNested + " nNested2: " + nNested2);
    }

    private static int methode(int i) {
        nMthode++;
        int k = i * 5;
        if(withNested) k += nested(i); else k += i;
        return k;
    }

    private static int nested(int i) {
        nNested++;
        int l = i * 5;
        l += i;
        if(withNested2) l += nested2(l); else l += i;
        return l;
    }

    private static int nested2(int i) {
        nNested2++;
        int l = i * 5;
        long m = l *l;
        return l;
    }

}
