import java.util.Map;

public class Threads implements Runnable
{
    private Map<String, Account> accounts = new Bank().getAccounts();

    @Override
    public void run() {
        for (int i = 0; i < accounts.size() / 3; i++) {
            long localAmount = (long) (Math.random() * 70000);
            Bank.checkAccount(i, i + 2, localAmount);
        }
    }
}
