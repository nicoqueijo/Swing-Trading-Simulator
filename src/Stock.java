import java.util.Calendar;

public class Stock {

    private Calendar date;
    private Price price;
    private Macd macd;
    private Rsi rsi;

    public Stock(Calendar date, Price price, Macd macd, Rsi rsi) {
        this.date = date;
        this.price = price;
        this.macd = macd;
        this.rsi = rsi;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Macd getMacd() {
        return macd;
    }

    public void setMacd(Macd macd) {
        this.macd = macd;
    }

    public Rsi getRsi() {
        return rsi;
    }

    public void setRsi(Rsi rsi) {
        this.rsi = rsi;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "date=" + date.get(Calendar.YEAR) + "/" + (date.get(Calendar.MONTH) + 1) + "/" + date.get(Calendar.DATE) +
                ", price=" + price +
                ", macd=" + macd +
                ", rsi=" + rsi +
                '}';
    }
}
