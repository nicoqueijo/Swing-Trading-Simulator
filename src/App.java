import com.google.common.collect.Lists;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class App {

    private static final String API_KEY = "DVR62X1BU59QHX58";
    private static String ticker;

    private static double swingTradeTotalReturns = 0.0;
    private static double buyAndHoldTotalReturns = 0.0;
    private static double swingTradeAnnualReturns = 0.0;
    private static double buyAndHoldAnnualReturns = 0.0;

    private static GetRequest getRequestPrice;
    private static GetRequest getRequestMacd;
    private static GetRequest getRequestRsi;

    private static List<Stock> stockDataset = Lists.newArrayList();
    private static List<Price> prices = Lists.newArrayList();
    private static List<Macd> macds = Lists.newArrayList();
    private static List<Rsi> rsis = Lists.newArrayList();
    private static int shortestLength;

    public static void main(String[] args) {

        try {
            if (args.length != 1) {
                throw new Exception();
            }
            ticker = args[0].toUpperCase();
            getRequestPrice = Unirest.get("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="
                    + ticker + "&outputsize=full&apikey=" + API_KEY);
            getRequestMacd = Unirest.get("https://www.alphavantage.co/query?function=MACD&symbol="
                    + ticker + "&interval=daily&series_type=close&apikey=" + API_KEY);
            getRequestRsi = Unirest.get("https://www.alphavantage.co/query?function=RSI&symbol="
                    + ticker + "&interval=daily&time_period=14&series_type=close&apikey=" + API_KEY);
            initPrices();
            initMacds();
            initRsis();
        } catch (Exception exception) {
            System.out.println("An error has occurred.");
            System.out.println("Either ticker symbol is invalid or API call limit has been reached.");
            return;
        }

        trimLists();
        initDataset();
        simulateRsiStrategy();
        System.out.println("\n\n");
        resetReturns();
        simulateMacdStrategy();
    }

    private static void printTicker() {
        System.out.println("TICKER: " + ticker);
    }

    private static void resetReturns() {
        swingTradeTotalReturns = 0.0;
        buyAndHoldTotalReturns = 0.0;
        swingTradeAnnualReturns = 0.0;
        buyAndHoldAnnualReturns = 0.0;
    }

    private static void trimLists() {
        shortestLength = minOfThree(prices.size(), macds.size(), rsis.size());
        prices = prices.subList(prices.size() - shortestLength, prices.size());
        macds = macds.subList(macds.size() - shortestLength, macds.size());
        rsis = rsis.subList(rsis.size() - shortestLength, rsis.size());
    }

    private static void initDataset() {
        for (int i = 0; i < shortestLength; i++) {
            stockDataset.add(new Stock(prices.get(i).getDate(),
                    prices.get(i),
                    macds.get(i),
                    rsis.get(i)));
        }
    }

    private static void initPrices() throws UnirestException {
        JSONObject rsiJsonObject = getRequestRsi.asJson().getBody().getObject()
                .getJSONObject("Technical Analysis: RSI");
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
    }

    private static void initMacds() throws UnirestException {
        JSONObject macdJsonObject = getRequestMacd.asJson().getBody().getObject()
                .getJSONObject("Technical Analysis: MACD");
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
    }

    private static void initRsis() throws UnirestException {
        JSONObject priceJsonObject = getRequestPrice.asJson().getBody().getObject()
                .getJSONObject("Time Series (Daily)");
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
    }

    private static void simulateRsiStrategy() {
        final double RSI_OVERSOLD = 30.0;
        final double RSI_OVERBOUGHT = 70.0;
        boolean holding = true;
        Calendar startDate = stockDataset.get(0).getDate();
        Calendar endDate = stockDataset.get(stockDataset.size() - 1).getDate();
        double simulationTimeframeInYears = round(ChronoUnit.DAYS.between(startDate.toInstant(),
                endDate.toInstant()) / 365.0);
        double initialPrice = stockDataset.get(0).getPrice().getPrice();
        double finalPrice = stockDataset.get(stockDataset.size() - 1).getPrice().getPrice();
        double boughtPrice = initialPrice;

        printTicker();
        System.out.println("STRATEGY: RSI");
        System.out.println("Swing trade based on RSI indicator. Buy when an oversold level (30)" +
                " is reached and sell when overbought level (70) is reached.");
        System.out.println();
        System.out.println("TRADE HISTORY:");
        System.out.println();
        System.out.println(startDate.get(Calendar.YEAR) + "/" + (startDate.get(Calendar.MONTH) + 1)
                + "/" + startDate.get(Calendar.DATE) + " \nBUY: " + round(boughtPrice));
        System.out.println();

        for (int i = 1; i < stockDataset.size(); i++) {
            Stock stock = stockDataset.get(i);
            Calendar date = stock.getDate();
            double price = stock.getPrice().getPrice();
            double rsi = stock.getRsi().getRsi();
            if (rsi <= RSI_OVERSOLD && !holding) {
                holding = true;
                boughtPrice = price;
                System.out.println(date.get(Calendar.YEAR) + "/" + (date.get(Calendar.MONTH) + 1)
                        + "/" + date.get(Calendar.DATE) + " \nBUY: " + round(boughtPrice));
                System.out.println();
            } else if (rsi >= RSI_OVERBOUGHT && holding) {
                holding = false;
                double gains = percentageChange(boughtPrice, price);
                swingTradeTotalReturns += gains;
                System.out.println(date.get(Calendar.YEAR) + "/" + (date.get(Calendar.MONTH) + 1)
                        + "/" + date.get(Calendar.DATE) + " \nSELL: " + round(price) + " RETURN: "
                        + round(gains) + "%");
                System.out.println();
            }
        }
        if (holding) {
            double gains = percentageChange(boughtPrice, finalPrice);
            swingTradeTotalReturns += gains;
            System.out.println(endDate.get(Calendar.YEAR) + "/" + (endDate.get(Calendar.MONTH) + 1)
                    + "/" + endDate.get(Calendar.DATE) + " \nSELL: " + round(finalPrice) +
                    " RETURN: " + round(gains) + "%");
            System.out.println();
        }

        buyAndHoldTotalReturns = percentageChange(initialPrice, finalPrice);
        swingTradeAnnualReturns = swingTradeTotalReturns / simulationTimeframeInYears;
        buyAndHoldAnnualReturns = buyAndHoldTotalReturns / simulationTimeframeInYears;
        System.out.println("SUMMARY:");
        System.out.println("TIME OF SIMULATION: " + simulationTimeframeInYears + " years");
        System.out.println("TOTAL RETURNS:");
        System.out.println("\tBUY & HOLD:  " + round(buyAndHoldTotalReturns) + "%");
        System.out.println("\tSWING TRADE: " + round(swingTradeTotalReturns) + "%");
        System.out.println("ANNUAL RETURNS:");
        System.out.println("\tBUY & HOLD:  " + round(buyAndHoldAnnualReturns) + "%");
        System.out.println("\tSWING TRADE: " + round(swingTradeAnnualReturns) + "%");
    }

    private static void simulateMacdStrategy() {
        boolean holding = true;
        Calendar startDate = stockDataset.get(0).getDate();
        Calendar endDate = stockDataset.get(stockDataset.size() - 1).getDate();
        double simulationTimeframeInYears = round(ChronoUnit.DAYS.between(startDate.toInstant(),
                endDate.toInstant()) / 365.0);
        double initialPrice = stockDataset.get(0).getPrice().getPrice();
        double finalPrice = stockDataset.get(stockDataset.size() - 1).getPrice().getPrice();
        double boughtPrice = initialPrice;

        printTicker();
        System.out.println("STRATEGY: MACD");
        System.out.println("Swing trade based on MACD indicator. Buy when the MACD line crosses " +
                "above the Signal line and sell when the opposite happens.");
        System.out.println();
        System.out.println("TRADE HISTORY:");
        System.out.println();
        System.out.println(startDate.get(Calendar.YEAR) + "/" + (startDate.get(Calendar.MONTH) + 1)
                + "/" + startDate.get(Calendar.DATE) + " \nBUY: " + round(boughtPrice));
        System.out.println();

        for (int i = 1; i < stockDataset.size(); i++) {
            Stock stock = stockDataset.get(i);
            Calendar date = stock.getDate();
            double price = stock.getPrice().getPrice();
            double macdHist = stock.getMacd().getMacdHist();

            if (macdHist >= 0.0 && !holding) {
                holding = true;
                boughtPrice = price;
                System.out.println(date.get(Calendar.YEAR) + "/" + (date.get(Calendar.MONTH) + 1)
                        + "/" + date.get(Calendar.DATE) + " \nBUY: " + round(boughtPrice));
                System.out.println();
            } else if (macdHist < 0.0 && holding) {
                holding = false;
                double gains = percentageChange(boughtPrice, price);
                swingTradeTotalReturns += gains;
                System.out.println(date.get(Calendar.YEAR) + "/" + (date.get(Calendar.MONTH) + 1)
                        + "/" + date.get(Calendar.DATE) + " \nSELL: " + round(price) + " RETURN: "
                        + round(gains) + "%");
                System.out.println();
            }
        }
        if (holding) {
            double gains = percentageChange(boughtPrice, finalPrice);
            swingTradeTotalReturns += gains;
            System.out.println(endDate.get(Calendar.YEAR) + "/" + (endDate.get(Calendar.MONTH) + 1)
                    + "/" + endDate.get(Calendar.DATE) + " \nSELL: " + round(finalPrice) +
                    " RETURN: " + round(gains) + "%");
            System.out.println();
        }

        buyAndHoldTotalReturns = percentageChange(initialPrice, finalPrice);
        swingTradeAnnualReturns = swingTradeTotalReturns / simulationTimeframeInYears;
        buyAndHoldAnnualReturns = buyAndHoldTotalReturns / simulationTimeframeInYears;
        System.out.println("SUMMARY:");
        System.out.println("TIME OF SIMULATION: " + simulationTimeframeInYears + " years");
        System.out.println("TOTAL RETURNS:");
        System.out.println("\tBUY & HOLD:  " + round(buyAndHoldTotalReturns) + "%");
        System.out.println("\tSWING TRADE: " + round(swingTradeTotalReturns) + "%");
        System.out.println("ANNUAL RETURNS:");
        System.out.println("\tBUY & HOLD:  " + round(buyAndHoldAnnualReturns) + "%");
        System.out.println("\tSWING TRADE: " + round(swingTradeAnnualReturns) + "%");
    }

    private static int minOfThree(int num1, int num2, int num3) {
        int min = Math.min(num1, num2);
        min = Math.min(min, num3);
        return min;
    }

    private static double percentageChange(double oldPrice, double newPrice) {
        return (newPrice - oldPrice) / oldPrice * 100;
    }

    private static double round(double value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}
