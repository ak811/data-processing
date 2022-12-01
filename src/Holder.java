public class Holder {
    private int nationalCode;
    private int cardAcqCode;
    private float totalScore;

    private float purchaseScore, balanceScore, topuupScore, chargeScore, billPaymentScore;

    public Holder(int nationalCode, int cardAcqCode) {
        this.nationalCode = nationalCode;
        this.cardAcqCode = cardAcqCode;
    }

    public void calculateTotalScore() {
//        System.out.println(purchaseScore + " " + balanceScore + " " + topuupScore + " " + chargeScore + " " + billPaymentScore);
        this.totalScore = purchaseScore + balanceScore + topuupScore + chargeScore + billPaymentScore;
    }

    public int getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(int nationalCode) {
        this.nationalCode = nationalCode;
    }

    public int getCardAcqCode() {
        return cardAcqCode;
    }

    public void setCardAcqCode(int cardAcqCode) {
        this.cardAcqCode = cardAcqCode;
    }

    public float getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(float totalScore) {
        this.totalScore = totalScore;
    }

    public float getPurchaseScore() {
        return purchaseScore;
    }

    public void setPurchaseScore(float purchaseScore) {
        if (purchaseScore > Transaction.MAX_PURCHASE_SCORE)
            purchaseScore = Transaction.MAX_PURCHASE_SCORE;
        this.purchaseScore = purchaseScore;
    }

    public float getBalanceScore() {
        return balanceScore;
    }

    public void setBalanceScore(float balanceScore) {
        if (balanceScore > Transaction.MAX_BALLANCE_SCORE)
            balanceScore = Transaction.MAX_BALLANCE_SCORE;
        this.balanceScore = balanceScore;
    }

    public float getTopuupScore() {
        return topuupScore;
    }

    public void setTopuupScore(float topuupScore) {
        this.topuupScore = topuupScore;
    }

    public float getChargeScore() {
        return chargeScore;
    }

    public void setChargeScore(float chargeScore) {
        this.chargeScore = chargeScore;
    }

    public float getBillPaymentScore() {
        return billPaymentScore;
    }

    public void setBillPaymentScore(float billPaymentScore) {
        this.billPaymentScore = billPaymentScore;
    }
}
