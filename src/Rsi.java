import java.util.Date;

public class Rsi {

    private Date date;
    private Double rsi;

    public Rsi(Date date, Double rsi) {
        this.date = date;
        this.rsi = rsi;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getRsi() {
        return rsi;
    }

    public void setRsi(Double rsi) {
        this.rsi = rsi;
    }
}
