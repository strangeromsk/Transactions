import java.util.Map;
public class Threads extends Bank implements Runnable
{
    private Map<String, Account> accounts = new Bank().getAccounts();

    @Override
    public void run() {
        for (int i = 0; i < accounts.size() / 3; i++) {
            long localAmount = (long) (Math.random() * 70000);
            checkAccount(i, i + 2, localAmount);
        }
    }
}
