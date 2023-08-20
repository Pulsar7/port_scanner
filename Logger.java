package port_scanner;


public class Logger {
    public static String white  = "\u001B[37m";
    public static String reset  = "\u001B[0m";
    public static String green  = "\u001B[32m";
    public static String red    = "\u001B[31m";
    public static String yellow = "\u001B[33m";
    public static String hellow = "\u001B[93m";
    public static String lred   = "\u001B[91m";


    public static void progress(String text) {
        System.out.print(white+"["+hellow+"INFO"+white+"] "+reset+text+"...");
    }

    public static void info(String text) {
        System.out.println(white+"["+yellow+"INFO"+white+"] "+reset+text);
    }

    public static void error(String text) {
        System.out.println(white+"["+red+"ERROR"+white+"] "+lred+text+reset);
    }

    public static void ok(String text) {
        System.out.print(green+text+reset+"\n");
    }

    public static void failed(String text) {
        System.out.print(lred+text+reset+"\n");
    }
}
