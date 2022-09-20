import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Test {
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

    public static String statement(Invoice invoice, Map<String, Play> plays) {
        int totalAmount = 0;
        int volumeCredits = 0;
        String result = "Statement for " + invoice.customer() + "\n";
        final NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        formatter.setCurrency(Currency.getInstance(Locale.US));

        for (Performance perf : invoice.performances()) {
            final Play play = plays.get(perf.playID());
            int thisAmount = 0;

            switch (play.type()) {
                case "tragedy" -> {
                    thisAmount = 40000;
                    if (perf.audience() > 30) {
                        thisAmount += 1000 * (perf.audience() - 30);
                    }
                }

                case "comedy" -> {
                    thisAmount = 30000;
                    if (perf.audience > 20) {
                        thisAmount += 10000 + 500 * (perf.audience() - 20);
                    }
                    thisAmount += 300 * perf.audience();
                }
                default -> throw new IllegalArgumentException("unknown type" + play.type());
            }
            // add volume credits
            volumeCredits += Math.max(perf.audience() - 30, 0);
            // add extra credit for every ten comedy attendees
            if ("comedy" == play.type()) {
                volumeCredits += Math.floor(perf.audience() / 5);
            }
            // print line for this order
            result += " " + play.name() + ": " + formatter.format(thisAmount / 100) + " (" + perf.audience() + "seats)\n";

            totalAmount += thisAmount;
        }
        result += "Amount owed is " + formatter.format(totalAmount / 100) + "\n";
        result += "You earned " + volumeCredits + " credits\n";
        return result;
    }



    public static void main(String[] args) {
        System.out.println(Test.statement(invoices.get(0), plays));
    }


}
