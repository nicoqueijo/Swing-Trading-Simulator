import java.util.Calendar;

public class Rsi {

    private Calendar date;
    private Double rsi;

    public Rsi(Calendar date, Double rsi) {
        this.date = date;
        this.rsi = rsi;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Double getRsi() {
        return rsi;
    }

    public void setRsi(Double rsi) {
        this.rsi = rsi;
    }

    @Override
    public String toString() {
        return "Rsi{" +
                ", rsi=" + rsi +
                '}';
    }
}
