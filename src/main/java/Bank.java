import com.github.javafaker.Faker;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Bank implements Runnable {
    private static Map<String, Account> accounts;
    private static final Random random = new Random();
    private static final int clientsNumber = 1000;
    private static final int processors = Runtime.getRuntime().availableProcessors();

    private static boolean isFraud(long fromAccountNum, long toAccountNum, long amount)
            throws InterruptedException {
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    public static void main(String[] args) {
        accounts = new ConcurrentHashMap<>();
        for (int i = 0; i < clientsNumber; i++) {
            Account account = new Account();
            account.setAccNumber(i);
            account.setMoney((long) (Math.random() * 10000));
            accounts.put(randomIdentifier(), account);
        }
        for (int i = 0; i < processors; i++) {
            new Thread(new Bank()).start();
        }
//        ExecutorService executorService =
//                Executors.newFixedThreadPool(processors);
//        executorService.execute(() -> {
//            for (int i = 0; i < processors; i++) {
//                new Thread(new Bank()).start();
//            }
//        });
        //System.out.println("Balance is " + getBalance(12));
    }

    @Override
    public void run() {
        for (int i = 0; i < accounts.size() / 3; i++) {
            long localAmount = (long) (Math.random() * 70000);
            checkAccount(i, i + 2, localAmount);
        }
    }

    private static void checkAccount(long fromAccNum, long toAccNum, long amount) {
        if (accounts.size() >= clientsNumber) {
            System.out.println("HashMap size : " + accounts.size() + "\t" + "------------------------------------------------------------");
            System.out.println("First client № " + fromAccNum + " balance before " + getBalance(fromAccNum) + " Second client № " + toAccNum + " balance before " + getBalance(toAccNum) + " Amount is: " + amount);
            transfer(fromAccNum, toAccNum, amount);
            System.out.println("First client № " + fromAccNum + " balance after " + getBalance(fromAccNum) + " Second client № " + toAccNum + " balance after " + getBalance(toAccNum) + " Amount is: " + amount);
        }
    }

    /**
     * TODO: реализовать метод. Метод переводит деньги между счетами.
     * Если сумма транзакции > 50000, то после совершения транзакции,
     * она отправляется на проверку Службе Безопасности – вызывается
     * метод isFraud. Если возвращается true, то делается блокировка
     * счетов (как – на ваше усмотрение)
     */
    private static void transfer(long fromAccountNum, long toAccountNum, long amount) {
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
    private static long getBalance(long accountNum) {
        long tr = accounts.values().stream().filter(e -> e.getAccNumber() == accountNum).mapToLong(Account::getMoney).max().getAsLong();
        return tr;
    }

    private static void block(long accountNum) {
        System.out.println("You cannot make money transfers right now.");
        accounts.values().forEach(e -> {
            if (e.getAccNumber() == accountNum) {
                e.setFraudulent(true);
            }
        });
    }

    private static String randomIdentifier() {
        Faker faker = new Faker();

        String name = faker.name().fullName(); // Miss Samanta Schmidt
        String firstName = faker.name().firstName(); // Emory
        String lastName = faker.name().lastName(); // Barton

        return name + " " + firstName + " " + lastName;
    }
}
