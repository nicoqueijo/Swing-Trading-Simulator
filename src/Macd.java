import java.util.Date;

public class Macd {

    private Date date;
    private Double macdSignal;
    private Double macd;
    private Double macdHist;

    public Macd(Date date, Double macdSignal, Double macd, Double macdHist) {
        this.date = date;
        this.macdSignal = macdSignal;
        this.macd = macd;
        this.macdHist = macdHist;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getMacdSignal() {
        return macdSignal;
    }

    public void setMacdSignal(Double macdSignal) {
        this.macdSignal = macdSignal;
    }

    public Double getMacd() {
        return macd;
    }

    public void setMacd(Double macd) {
        this.macd = macd;
    }

    public Double getMacdHist() {
        return macdHist;
    }

    public void setMacdHist(Double macdHist) {
        this.macdHist = macdHist;
    }
}
