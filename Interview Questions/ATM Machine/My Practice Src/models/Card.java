package models;

public class Card{
    private String cardNumber = null;
    private String name = null; 

    Card(String cardNumber, String name){
        this.cardNumber = cardNumber;
        this.name = name;
    }

    public String getCardNumber(){
        return cardNumber;
    }
}