package enums;

public enum CashType {
    FIVE(5),
    TEN(10),
    FIFTY(50),
    HUNDRED(100);

    private int value;

    CashType(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
