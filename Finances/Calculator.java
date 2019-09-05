import java.util.*;


public class Calculator {
    private static HashMap<String, Integer> map = new HashMap<>();
    private final static int IAN_RENT = 1700;
    private final static int JAE_RENT = 1300;
    private final static int JIN_RENT = 1000;
    private final static int INTERNET = 20;

    public static void main(String args[]) {
        map.put("Ian", IAN_RENT);
        map.put("Jae", JAE_RENT);
        map.put("Jin", JIN_RENT);

        Scanner s = new Scanner(System.in);
        int electric = costAdder("Total Electric Fee: ", s);
        int grocery = costAdder("Total Grocery Fee: ", s);
        int miscellaneous = costAdder("Miscellaneous Fee: ", s);
        for (String key : map.keySet()) {
            finalCost(key, electric, grocery, miscellaneous, INTERNET);
        }


    }

    private static int costAdder(String phrase, Scanner s) {
        System.out.println(phrase);
        return s.nextInt();
    }

    private static void finalCost(String person, int e, int g, int m, int i) {
        double total = map.get(person) + (e / 3.0) + (g / 3.0) + (m / 3.0) + i;
        total = Math.round(total * 100.0) / 100.0;
        System.out.print(person + "'s Total Amount Due:  " + total + "           ");
    }
}
