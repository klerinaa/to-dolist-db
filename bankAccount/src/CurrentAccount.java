public class CurrentAccount implements Account {

    public double balance;
    public double overdraftLImit;

    public CurrentAccount(double balance, double overdraftLImit) {
        this.balance = balance;
        this.overdraftLImit = overdraftLImit;
    }


    @Override
    public void deposit(double amount) {
        balance += amount;
    }

    @Override
    public void withdraw(double amount) {
        if (balance + overdraftLImit >= amount) {
            balance -= amount;
        }
    }

    @Override
    public double getBalance() {
        return balance;
    }

    public void setOverdraftLimit(double overdraftLImit){
        this.overdraftLImit=overdraftLImit;
    }
}
