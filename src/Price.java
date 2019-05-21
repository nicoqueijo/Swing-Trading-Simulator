import java.util.Calendar;

public class Price {

    private Calendar date;
    private Double price;
    private Integer volume;

    public Price(Calendar date, Double price, Integer volume) {
        this.date = date;
        this.price = price;
        this.volume = volume;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
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

    @Override
    public String toString() {
        return "Price{" +
                "date=" + date.get(Calendar.YEAR) + "/" + (date.get(Calendar.MONTH) + 1) + "/" + date.get(Calendar.DATE) +
                ", price=" + price +
                ", volume=" + volume +
                '}';
    }
}
