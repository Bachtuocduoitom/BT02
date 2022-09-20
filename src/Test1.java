import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Test1 {
    //Replace Magic Number with Symbolic Constant
    public static final int TRAGEDYAMOUNT = 40000;
    public static final int COMEDYAMOUNT = 30000;

    record Performance(String playID, int audience) {
    }

    record Invoice(String customer, List<Performance> performances) {
    }

    record Play(String name, String type) {
    }

    public static final List<Invoice> invoices = List.of(
            new Invoice(
                    "BigCo",
                    List.of(new Performance("hamlet", 55),
                            new Performance("as-like", 35),
                            new Performance("othello", 40))));

    public static final Map<String, Play> plays = Map.of(
            "hamlet", new Play("Hamlet", "tragedy"),
            "as-like", new Play("As You Like It", "comedy"),
            "othello", new Play("Othello", "tragedy")
    );

    //
    public static int amountCal(Play play, Performance perf) {
        int result;
        switch (play.type()) {
            case "tragedy" -> {
                result = TRAGEDYAMOUNT;//Replace Magic Number with Symbolic Constant
                if (perf.audience() > 30) {
                    result += 1000 * (perf.audience() - 30);
                }
            }

            case "comedy" -> {
                result = COMEDYAMOUNT;//Replace Magic Number with Symbolic Constant
                if (perf.audience > 20) {
                    result += 10000 + 500 * (perf.audience() - 20);
                }
                result += 300 * perf.audience();
            }
            default -> throw new IllegalArgumentException("unknown type" + play.type());
        }
        return result;
    }

    //
    public static String format(long amount) {
        final NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        formatter.setCurrency(Currency.getInstance(Locale.US));
        return formatter.format(amount / 100);
    }

    //
    public static int volumeCreditsCal (Play play, Performance perf) {
        int result = 0;
        result += Math.max(perf.audience() - 30, 0);

        if (play.type().equals("comedy")) {
            result += Math.floor(perf.audience() / 5);
        }
        return result;
    }

    public static String statement(Invoice invoice, Map<String, Play> plays) {
        int totalAmount = 0;
        int volumeCredits = 0;
        String result = "Statement for " + invoice.customer() + "\n";

        for (Performance perf : invoice.performances()) {
            final Play play = plays.get(perf.playID());

            totalAmount += amountCal(play, perf); //inline temp

            volumeCredits += volumeCreditsCal(play, perf); //extract method

            result += " " + play.name() + ": " + format(amountCal(play, perf)) + " (" + perf.audience() + "seats)\n";
        }

        result += "Amount owed is " + format(totalAmount) + "\n";
        result += "You earned " + volumeCredits + " credits\n";
        return result;
    }



    public static void main(String[] args) {
        System.out.println(Test1.statement(invoices.get(0), plays));
    }


}
