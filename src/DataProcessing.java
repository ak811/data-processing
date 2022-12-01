import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataProcessing {

    private static final String DB_DRIVER = "org.sqlite.JDBC";
    private static final String DB_CONNECTION = "jdbc:sqlite:data.sqlite";

    private static ArrayList<Transaction> transactionList;
    private static ArrayList<Holder> holders;

    public static void main(String[] args) {

        transactionList = new ArrayList<>();
        holders = new ArrayList<>();

        long databaseTime = System.currentTimeMillis();
        fillArrayFromCompanyTable();
        fillArrayFromShortendTable();
        System.out.println("database time: " + String.valueOf(System.currentTimeMillis() - databaseTime));

        long startTime = System.currentTimeMillis();
        Holder[] sortedHolders = mergeSort(holders, 0, holders.size());
        for (int i = 0; i < sortedHolders.length; i++)
            holders.set(i, sortedHolders[i]);
        for (int i = 0; i < transactionList.size(); i++) {
            Transaction currTransaction = transactionList.get(i);
            Holder currHolder = binarySearch(holders, currTransaction.getCardAcqCode());
            if (currHolder.getCardAcqCode() != -1)
                if (currTransaction.getCardAcqCode() == currHolder.getCardAcqCode()) {
                    switch (currTransaction.getFinTransType()) {
                        case Transaction.PURCHASE:
                            //noinspection IntegerDivisionInFloatingPointContext
                            float result = (currTransaction.getAmount() / Transaction.PER_PURCHASE_AMOUNT) * Transaction.PURCHASE_SCORE;
                            currHolder.setPurchaseScore(currHolder.getPurchaseScore() + result);
                            break;
                        case Transaction.BALLANCE:
                            currHolder.setBalanceScore(currHolder.getBalanceScore() + Transaction.BALLANCE_SCORE);
                            break;
                        case Transaction.TOPUP:
                            result = currTransaction.getAmount() * Transaction.TOPUP_SCORE_COEFFICIENT;
                            currHolder.setTopuupScore(currHolder.getTotalScore() + result);
                            break;
                        case Transaction.CHARGE:
                            result = currTransaction.getAmount() * Transaction.TOPUP_SCORE_COEFFICIENT;
                            currHolder.setChargeScore(currHolder.getChargeScore() + result);
                            break;
                        case Transaction.BILL_PAYMENT:
                            result = 0;
                            if (currTransaction.getAmount() == Transaction.PER_MAX_BALLANCE_AMOUNT)
                                result += 2;
                            else
                                result += currTransaction.getAmount() / (float) Transaction.PER_MAX_BALLANCE_AMOUNT;
                            currHolder.setBalanceScore(currHolder.getBalanceScore() + result);
                            break;
                    }
                }
        }

        for (int i = 0; i < holders.size(); i++) {
            holders.get(i).calculateTotalScore();
//            System.out.println(holders.get(i).getTotalScore());
        }

        mergeSortBasedOnPoint(holders, 0, holders.size());
        List<Holder> tenPercentHolders = getTenPercentOfBests(holders);

        System.out.println("winner CardAcqCode: " + doLottery(tenPercentHolders).getCardAcqCode());
        System.out.println("total time: " + (System.currentTimeMillis() - startTime));
    }

    private static void fillArrayFromCompanyTable() {
        String sql = "SELECT NationalCode, CardAcqCode FROM company";

        try (Connection conn = getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
                /* System.out.println(rs.getInt("NationalCode") + "\t" +
                rs.getInt("CardAcqCode")); */

                holders.add(new Holder(rs.getInt("NationalCode"),
                        rs.getInt("CardAcqCode")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void fillArrayFromShortendTable() {
        String sql = "SELECT RRN, CardAcqCode, TerminalId, Amount, Pan, FinTransType FROM shortend";

        try (Connection conn = getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
                int RRN = rs.getInt("RRN");
                int cardAcqCode = rs.getInt("CardAcqCode");
                int terminalId = rs.getInt("TerminalId");
                int amount = rs.getInt("Amount");
                int pan = rs.getInt("Pan");
                String finTransTypeStr = rs.getString("FinTransType");

                int finTransType;
                switch (finTransTypeStr.toUpperCase()) {
                    case "PURCHASE":
                        finTransType = 1;
                        break;
                    case "BALLANCE":
                        finTransType = 2;
                        break;
                    case "TOPUP":
                        finTransType = 3;
                        break;
                    case "CHARGE":
                        finTransType = 4;
                        break;
                    case "BILL_PAYMENT":
                        finTransType = 5;
                        break;
                    default:
                        finTransType = 0;
                }

//                System.out.println(
//                        RRN + "\t" +
//                                cardAcqCode + "\t" +
//                                terminalId + "\t" +
//                                amount + "\t" +
//                                pan + "\t" +
//                                finTransType + "\t");

                transactionList.add(new Transaction(RRN, cardAcqCode, terminalId, amount, pan, finTransType));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static Connection getDBConnection() {

        Connection dbConnection = null;

        try {

            Class.forName(DB_DRIVER);

        } catch (ClassNotFoundException e) {

            System.out.println(e.getMessage());

        }

        try {

            dbConnection = DriverManager.getConnection(DB_CONNECTION);
            return dbConnection;

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }

        return dbConnection;

    }

    private static Holder doLottery(List<Holder> tenPercentHolders) {
        Random random = new Random();
        int winnerIndex = random.nextInt(tenPercentHolders.size());
        return tenPercentHolders.get(winnerIndex);
    }

    private static List<Holder> getTenPercentOfBests(List<Holder> holders) {
        List<Holder> tenPercentOfPercents = new ArrayList<>();
        for (int i = holders.size() - holders.size() / 10; i < holders.size(); i++)
            tenPercentOfPercents.add(holders.get(i));
        return tenPercentOfPercents;
    }

    private static Holder[] mergeSort(List<Holder> holders, int start, int end) {
        Holder[] sorted = new Holder[end - start];
        if (start == end - 1) {
            sorted[0] = holders.get(start);
            return sorted;
        }
        int mid = (end + start) / 2;
        Holder[] left = mergeSort(holders, start, mid);
        Holder[] right = mergeSort(holders, mid, end);

        int i = 0;
        int j = 0;
        for (int k = 0; k < sorted.length; k++) {
            if (i >= left.length)
                sorted[k] = right[j++];
            else if (j >= right.length)
                sorted[k] = left[i++];
            else if (left[i].getCardAcqCode() <= right[j].getCardAcqCode())
                sorted[k] = left[i++];
            else
                sorted[k] = right[j++];
        }
        return sorted;
    }

    private static Holder[] mergeSortBasedOnPoint(List<Holder> holders, int start, int end) {
        Holder[] sorted = new Holder[end - start];
        if (start == end - 1) {
            sorted[0] = holders.get(start);
            return sorted;
        }
        int mid = (end + start) / 2;
        Holder[] left = mergeSort(holders, start, mid);
        Holder[] right = mergeSort(holders, mid, end);

        int i = 0;
        int j = 0;
        for (int k = 0; k < sorted.length; k++) {
            if (i >= left.length)
                sorted[k] = right[j++];
            else if (j >= right.length)
                sorted[k] = left[i++];
            else if (left[i].getTotalScore() <= right[j].getTotalScore())
                sorted[k] = left[i++];
            else
                sorted[k] = right[j++];
        }
        return sorted;
    }

    public static Holder binarySearch(List<Holder> holders, int key) {
        int firstIndex = 0;
        int lastIndex = holders.size() - 1;

        while (firstIndex <= lastIndex) {
            int mid = (firstIndex + lastIndex) / 2;
            if (key > holders.get(mid).getCardAcqCode()) {
                firstIndex = mid + 1;
            } else if (key < holders.get(mid).getCardAcqCode()) {
                lastIndex = mid - 1;
            } else
                return holders.get(mid);
        }
        return new Holder(-1, -1);

    }
}
