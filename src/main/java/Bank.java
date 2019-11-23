import com.github.javafaker.Faker;

import java.util.*;

public class Bank implements Runnable
{
    private static Map<String, Account> accounts;
    private static final Random random = new Random();
    private static final int clientsNumber = 100000;
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
        for(int i = 0; i < processors; i++){
            new Thread(new Bank()).start();
        }
        System.out.println("Balance is " + getBalance(12));
        if(accounts.size() == clientsNumber){
            transfer(1004,876, 50000);
            System.out.println("First client balance " + getBalance(1004) + "Second client balance " + getBalance(876));
        }
    }

    private static synchronized void generator(){
        accounts = new HashMap<>();
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
        System.out.println(accounts.size());
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
        accounts.values().stream().map(e->{
            if(e.isFraudulent()){
                System.out.println("You cannot make money transfers.");
            }else{
                if(amount > 50000){
                    try {
                        if(!isFraud(fromAccountNum, toAccountNum,amount)){
                            e.setAccNumber(fromAccountNum);
                            e.setMoney(e.getMoney()-amount);
                            e.setAccNumber(toAccountNum);
                            e.setMoney(e.getMoney()+amount);
                        }
                        else{
                            block(fromAccountNum, toAccountNum);
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                else{
                    e.setAccNumber(fromAccountNum);
                    e.setMoney(e.getMoney()-amount);
                    e.setAccNumber(toAccountNum);
                    e.setMoney(e.getMoney()+amount);
                }
            }
            return -1;
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

    private synchronized static void block(long fromAccountNum, long toAccountNum){
        accounts.values().stream().forEachOrdered(e->{
            e.setAccNumber(fromAccountNum);
            e.setFraudulent(true);
            e.setAccNumber(toAccountNum);
            e.setFraudulent(true);
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
