package enums;

public enum Coin {

    ONE(1), FIVE(5),TEN(5),FIFTY(50);
    private final int value;
    
    private Coin(int value) {
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}