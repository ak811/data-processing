public class Transaction {
    public static final int PURCHASE = 1;
    public static final int BALLANCE = 2;
    public static final int TOPUP = 3;
    public static final int CHARGE = 4;
    public static final int BILL_PAYMENT = 5;

    public static int MAX_BILL_PAYMENT_SCORE = 2;
    public static int MAX_BALLANCE_SCORE = 3;
    public static int PER_MAX_BALLANCE_AMOUNT = 5000_000;
    public static float BALLANCE_SCORE = 0.01f;
    public static float CHARGE_COEFFICIENT = 0.01f;
    public static float TOPUP_SCORE_COEFFICIENT = 0.01f;
    public static int MAX_PURCHASE_SCORE = 1;
    public static float PURCHASE_SCORE = 0.01f;
    public static int PER_PURCHASE_AMOUNT = 500000;
    public static int LOTTERY_PERCENTAGE = 10;

    private int RRN, cardAcqCode, terminalId, amount, finTransType;
    private double pan;

    public Transaction(int RRN, int cardAcqCode, int terminalId, int amount, double pan, int finTransType) {
        this.RRN = RRN;
        this.cardAcqCode = cardAcqCode;
        this.terminalId = terminalId;
        this.amount = amount;
        this.finTransType = finTransType;
        this.pan = pan;
    }

    public int getRRN() {
        return RRN;
    }

    public int getCardAcqCode() {
        return cardAcqCode;
    }

    public int getTerminalId() {
        return terminalId;
    }

    public int getAmount() {
        return amount;
    }

    public int getFinTransType() {
        return finTransType;
    }

    public double getPan() {
        return pan;
    }
}
