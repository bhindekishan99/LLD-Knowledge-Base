package strategies;

public class CardPayment implements PaymentStrategy {
    
    public boolean processPayment(double amount){
        System.out.println("Payment of "+amount+" is successful using card");
        return true;
    }
}
