package dtp.commission;

public class CommissionHandler {

    // zlecenie transportowe
    private Commission commission;

    // czas naplyniecia do systemu
    private int incomeTime;

    public CommissionHandler(Commission commission, int incomeTime) {

        this.commission = commission;
        this.incomeTime = incomeTime;
    }

    public void setCommission(Commission commission) {

        this.commission = commission;
    }

    public Commission getCommission() {

        return commission;
    }

    public void setIncomeTime(int incomeTime) {

        this.incomeTime = incomeTime;
    }

    public int getIncomeTime() {

        return incomeTime;
    }

    public String toString() {

        return "<" + incomeTime + "> " + commission.toString();
    }
}
