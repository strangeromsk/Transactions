import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Account
{
    private volatile long money;
    private volatile long accNumber;
    private volatile boolean fraudulent;

    public boolean isFraudulent() {
        return fraudulent;
    }

    public void setFraudulent(boolean fraudulent) {
        this.fraudulent = fraudulent;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public long getAccNumber() {
        return accNumber;
    }

    public void setAccNumber(long accNumber) {
        this.accNumber = accNumber;
    }

}
