import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        Scanner input=new Scanner(System.in);
        SavingAccount savingAccount= new SavingAccount(1000.0,1.25);
        System.out.println("Saving Account : \n Desposit Balance : "+savingAccount.getBalance()+ " \n Interest Rate :1.25 ");

        CurrentAccount currentAccount=new CurrentAccount(5000.0,1000.0);
        System.out.println("Current Account : \n Desposit Balance : $5000 \n Overdravt Limit  :1000 ");

        bank.addAcount(savingAccount);
        bank.addAcount(currentAccount);
        System.out.println("Write the sum you want to deposit in SA");
        double amount =input.nextDouble();
        System.out.println("depositing "+amount+" into Savings Account");
        bank.deposit(savingAccount,amount);
        System.out.println("Write the sum you want to deposit in CA");
        double amount2 =input.nextDouble();
        System.out.println("depositing "+amount2+"into Current Account");
        bank.deposit(currentAccount,amount2);


        System.out.println("Withdraw $100 into Savings Account");
        bank.withdraw(savingAccount,100);
        System.out.println("Withdraw $200 into Current Account");
        bank.withdraw(currentAccount,200);

        System.out.println("Balance in both acc is ");
        bank.printAccountBalance();
        savingAccount.applyInterest();
        System.out.println("Balance after applying interst is ");
        System.out.println("Balance in both acc is ");
        bank.printAccountBalance();

    }
}