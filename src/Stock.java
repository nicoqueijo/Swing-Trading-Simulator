import java.util.Date;

/**
 * Encapsulates the date, price, and volume of a stock.
 */
public class Stock {

    private Date date;
    private Double price;
    private Integer volume;

    public Stock(Date date, Double price, Integer volume) {
        this.date = date;
        this.price = price;
        this.volume = volume;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }
}
