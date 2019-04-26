import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Main {

    // Synchronize the three lists someway so they all start at the same date.
    // Combine all three lists into one list where there is one date, the price, the MACDs, and the RSI.

    public static void main(String[] args) throws UnirestException {

        double returns;

        GetRequest spyDaily = Unirest.get("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=SPY&outputsize=full&apikey=DVR62X1BU59QHX58");
        GetRequest spyMacd = Unirest.get("https://www.alphavantage.co/query?function=MACD&symbol=SPY&interval=daily&series_type=close&apikey=DVR62X1BU59QHX58");
        GetRequest spyRsi = Unirest.get("https://www.alphavantage.co/query?function=RSI&symbol=SPY&interval=daily&time_period=14&series_type=close&apikey=DVR62X1BU59QHX58");

        JSONObject spyStockObject = spyDaily.asJson().getBody().getObject().getJSONObject("Time Series (Daily)");
        List<String> spyStockSortedKeySet = new ArrayList<>(spyStockObject.keySet());
        Collections.sort(spyStockSortedKeySet);
        List<Stock> spyStockPrices = new ArrayList<>();
        for (String key : spyStockSortedKeySet) {
            spyStockPrices.add(new Stock(
                    new Date(
                            Integer.parseInt(key.split("-")[0]),
                            Integer.parseInt(key.split("-")[1]) - 1,
                            Integer.parseInt(key.split("-")[2])
                    ),
                    Double.parseDouble(spyStockObject.getJSONObject(key).getString("4. close")),
                    Integer.parseInt(spyStockObject.getJSONObject(key).getString("5. volume")))
            );
        }

        JSONObject spyMacdObject = spyMacd.asJson().getBody().getObject().getJSONObject("Technical Analysis: MACD");
        List<String> spyMacdSortedKeySet = new ArrayList<>(spyMacdObject.keySet());
        Collections.sort(spyMacdSortedKeySet);
        List<Macd> spyMacdValues = new ArrayList<>();
        for (String key : spyMacdSortedKeySet) {
            spyMacdValues.add(new Macd(
                    new Date(
                            Integer.parseInt(key.split("-")[0]),
                            Integer.parseInt(key.split("-")[1]) - 1,
                            Integer.parseInt(key.split("-")[2])
                    ),
                    Double.parseDouble(spyMacdObject.getJSONObject(key).getString("MACD_Signal")),
                    Double.parseDouble(spyMacdObject.getJSONObject(key).getString("MACD")),
                    Double.parseDouble(spyMacdObject.getJSONObject(key).getString("MACD_Hist")))
            );
        }

        JSONObject spyRsiObject = spyRsi.asJson().getBody().getObject().getJSONObject("Technical Analysis: RSI");
        List<String> spyRsiSortedKeySet = new ArrayList<>(spyRsiObject.keySet());
        Collections.sort(spyRsiSortedKeySet);
        List<Rsi> spyRsiValues = new ArrayList<>();
        for (String key : spyRsiSortedKeySet) {
            spyRsiValues.add(new Rsi(
                    new Date(
                            Integer.parseInt(key.split("-")[0]),
                            Integer.parseInt(key.split("-")[1]) - 1,
                            Integer.parseInt(key.split("-")[2])
                    ),
                    Double.parseDouble(spyRsiObject.getJSONObject(key).getString("RSI")))
            );
        }


    }
}
