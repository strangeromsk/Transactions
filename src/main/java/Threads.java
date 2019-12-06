import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Threads implements Runnable
{
    private Map<String, Account> accounts = new Bank().getAccounts();
    public static final int clientsNumber = 1000;
    private static final Random random = new Random();

    @Override
    public void run() {

        for (int i = 0; i < accounts.size() / 3; i++) {
            long localAmount = (long) (Math.random() * 70000);
            checkAccount(i, i + 2, localAmount);
        }
    }

    private void checkAccount(long fromAccNum, long toAccNum, long amount) {
        if (accounts.size() >= clientsNumber) {
            System.out.println("HashMap size : " + accounts.size() + "\t" + "------------------------------------------------------------");
            System.out.println("First client № " + fromAccNum + " balance before " + getBalance(fromAccNum) + " Second client № " + toAccNum + " balance before " + getBalance(toAccNum) + " Amount is: " + amount);
            transfer(fromAccNum, toAccNum, amount);
            System.out.println("First client № " + fromAccNum + " balance after " + getBalance(fromAccNum) + " Second client № " + toAccNum + " balance after " + getBalance(toAccNum) + " Amount is: " + amount);
        }
    }
    private void transfer(long fromAccountNum, long toAccountNum, long amount) {
        AtomicBoolean localFraud = new AtomicBoolean(false);
        accounts.values().stream().forEachOrdered(e -> {
            if ((e.getAccNumber() == fromAccountNum && e.isFraudulent()) || (e.getAccNumber() == toAccountNum && e.isFraudulent())) {
                System.out.println("You cannot make money transfers.");
            } else {
                if (amount < 50000) {
                    if (e.getAccNumber() == fromAccountNum) {
                        e.setMoney(e.getMoney() - amount);
                    }
                    if (e.getAccNumber() == toAccountNum) {
                        e.setMoney(e.getMoney() + amount);
                    }
                }
                if (amount >= 50000 && e.getAccNumber() == fromAccountNum) {
                    try {
                        if (!isFraud(fromAccountNum, toAccountNum, amount)) {
                            e.setMoney(e.getMoney() - amount);
                        } else {
                            block(fromAccountNum);
                            localFraud.set(true);
                        }

                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                if (amount >= 50000 && e.getAccNumber() == toAccountNum) {
                    if (!localFraud.get()) {
                        e.setMoney(e.getMoney() + amount);
                    } else {
                        block(toAccountNum);
                    }
                }
            }

        });
    }

    /**
     * TODO: реализовать метод. Возвращает остаток на счёте.
     *
     * @return
     */
    private long getBalance(long accountNum) {
        long tr = accounts.values().stream().filter(e -> e.getAccNumber() == accountNum).mapToLong(Account::getMoney).max().getAsLong();
        return tr;
    }

    private void block(long accountNum) {
        System.out.println("You cannot make money transfers right now.");
        accounts.values().forEach(e -> {
            if (e.getAccNumber() == accountNum) {
                e.setFraudulent(true);
            }
        });
    }
    private static boolean isFraud(long fromAccountNum, long toAccountNum, long amount)
            throws InterruptedException {
        Thread.sleep(1000);
        return random.nextBoolean();
    }
}
