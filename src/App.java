import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class App {

    // Merged the three datasets into one.
    // Now I can start traversing it and buying/selling conditionally and tracking the balance/returns.

    public static void main(String[] args) throws UnirestException {

        final Double STARTING_BALANCE = 100000.00;
        final String ticker = "SPY";

        Double balance = new Double(STARTING_BALANCE);
        Double returns = 0.0;
        Double averageAnnualReturn = 0.0;

        GetRequest getRequestPrice = Unirest.get("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + ticker + "&outputsize=full&apikey=DVR62X1BU59QHX58");
        GetRequest getRequestMacd = Unirest.get("https://www.alphavantage.co/query?function=MACD&symbol=" + ticker + "&interval=daily&series_type=close&apikey=DVR62X1BU59QHX58");
        GetRequest getRequestRsi = Unirest.get("https://www.alphavantage.co/query?function=RSI&symbol=" + ticker + "&interval=daily&time_period=14&series_type=close&apikey=DVR62X1BU59QHX58");

        List<Stock> stockDataset = new ArrayList<>();
        List<Price> prices = new ArrayList<>();
        List<Macd> macds = new ArrayList<>();
        List<Rsi> rsis = new ArrayList<>();

        JSONObject priceJsonObject = getRequestPrice.asJson().getBody().getObject().getJSONObject("Time Series (Daily)");
        List<String> priceDates = new ArrayList<>(priceJsonObject.keySet());
        Collections.sort(priceDates);
        for (String date : priceDates) {
            prices.add(new Price(
                    new GregorianCalendar(
                            Integer.parseInt(date.split("-")[0]),
                            Integer.parseInt(date.split("-")[1]) - 1,
                            Integer.parseInt(date.split("-")[2])
                    ),
                    Double.parseDouble(priceJsonObject.getJSONObject(date).getString("4. close")),
                    Integer.parseInt(priceJsonObject.getJSONObject(date).getString("5. volume")))
            );
        }

        JSONObject macdJsonObject = getRequestMacd.asJson().getBody().getObject().getJSONObject("Technical Analysis: MACD");
        List<String> macdDates = new ArrayList<>(macdJsonObject.keySet());
        Collections.sort(macdDates);
        for (String date : macdDates) {
            macds.add(new Macd(
                    new GregorianCalendar(
                            Integer.parseInt(date.split("-")[0]),
                            Integer.parseInt(date.split("-")[1]) - 1,
                            Integer.parseInt(date.split("-")[2])
                    ),
                    Double.parseDouble(macdJsonObject.getJSONObject(date).getString("MACD_Signal")),
                    Double.parseDouble(macdJsonObject.getJSONObject(date).getString("MACD")),
                    Double.parseDouble(macdJsonObject.getJSONObject(date).getString("MACD_Hist")))
            );
        }

        JSONObject rsiJsonObject = getRequestRsi.asJson().getBody().getObject().getJSONObject("Technical Analysis: RSI");
        List<String> rsiDates = new ArrayList<>(rsiJsonObject.keySet());
        Collections.sort(rsiDates);
        for (String date : rsiDates) {
            rsis.add(new Rsi(
                    new GregorianCalendar(
                            Integer.parseInt(date.split("-")[0]),
                            Integer.parseInt(date.split("-")[1]) - 1,
                            Integer.parseInt(date.split("-")[2])
                    ),
                    Double.parseDouble(rsiJsonObject.getJSONObject(date).getString("RSI")))
            );
        }

        int shortestLength = minOfThree(prices.size(), macds.size(), rsis.size());
        prices = prices.subList(prices.size() - shortestLength, prices.size());
        macds = macds.subList(macds.size() - shortestLength, macds.size());
        rsis = rsis.subList(rsis.size() - shortestLength, rsis.size());

        for (int i = 0; i < shortestLength; i++) {
            stockDataset.add(new Stock(prices.get(i).getDate(),
                    prices.get(i),
                    macds.get(i),
                    rsis.get(i)));
        }
    }

    private static int minOfThree(int num1, int num2, int num3) {
        int min = Math.min(num1, num2);
        min = Math.min(min, num3);
        return min;
    }
}
