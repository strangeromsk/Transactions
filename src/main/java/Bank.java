import com.github.javafaker.Faker;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Bank implements Runnable
{
    private static Map<String, Account> accounts;
    private static final Random random = new Random();
    private static final int clientsNumber = 100;
    private static final int processors = Runtime.getRuntime().availableProcessors();

    @Override
    public void run() {
        generator();
    }

    private static synchronized boolean isFraud(long fromAccountNum, long toAccountNum, long amount)
            throws InterruptedException
    {
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    public static void main(String[] args) {
        accounts = new HashMap<>();
        for(int i = 0; i < processors; i++){
            new Thread(new Bank()).start();
        }
        //System.out.println("Balance is " + getBalance(12));
    }

    private static synchronized void generator(){

        int count1 = (int) (clientsNumber*0.95);
        for(int i = 0; i < count1; i++){
            Account accountPoor = new Account();
            accountPoor.setAccNumber(i);
            accountPoor.setMoney((long) (Math.random() * 1000));
            accounts.put(randomIdentifier(), accountPoor);
        }
        int count2 = (int) (clientsNumber*0.05);
        for(int i = 0; i < count2; i++){
            Account accountRich = new Account();
            accountRich.setAccNumber(i);
            accountRich.setMoney((long) (Math.random() * 10000000));
            accounts.put(randomIdentifier(), accountRich);
        }
        //System.out.println(accounts.size());
        if(accounts.size() == clientsNumber){
            System.out.println("First client balance before " + getBalance(77) + " Second client balance before " + getBalance(87));
            transfer(77,87, 60000);
            System.out.println("First client balance after " + getBalance(77) + " Second client balance after " + getBalance(87));
        }
    }
    /**
     * TODO: реализовать метод. Метод переводит деньги между счетами.
     * Если сумма транзакции > 50000, то после совершения транзакции,
     * она отправляется на проверку Службе Безопасности – вызывается
     * метод isFraud. Если возвращается true, то делается блокировка
     * счетов (как – на ваше усмотрение)
     */
    private static synchronized void transfer(long fromAccountNum, long toAccountNum, long amount)
    {
        AtomicBoolean localFraud = new AtomicBoolean(false);
        accounts.values().forEach(e->{
            if(e.getAccNumber()==fromAccountNum && e.isFraudulent()){
                System.out.println("You cannot make money transfers.");
            }
            if (e.getAccNumber()==toAccountNum && e.isFraudulent()){
                System.out.println("You cannot make money transfers.");
            }
            if(amount < 50000){
                if (e.getAccNumber()==fromAccountNum){
                    e.setMoney(e.getMoney()-amount);
                }
                if (e.getAccNumber()==toAccountNum){
                    e.setMoney(e.getMoney()+amount);
                }
            }
            if(amount >= 50000 && e.getAccNumber()==fromAccountNum) {
                try {
                    if(!isFraud(fromAccountNum, toAccountNum, amount)){
                        e.setMoney(e.getMoney()-amount);
                    }
                    else {
                        block(fromAccountNum);
                        localFraud.set(true);
                    }

                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            if(amount >= 50000 && e.getAccNumber()==toAccountNum){
                if(!localFraud.get()){
                    e.setMoney(e.getMoney()+amount);
                }
                else {
                    block(toAccountNum);
                }
            }
        });
    }
    /**
     * TODO: реализовать метод. Возвращает остаток на счёте.
     * @return
     */
    public synchronized static long getBalance(long accountNum)
    {
        long tr = accounts.values().stream().filter(e->e.getAccNumber()==accountNum).mapToLong(Account::getMoney).max().getAsLong();
        return tr;
    }

    private synchronized static void block(long accountNum){
        System.out.println("You cannot make money transfers right now.");
        accounts.values().forEach(e->{
            if(e.getAccNumber()==accountNum){
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
