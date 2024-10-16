package activity;

import org.junit.Before;
import org.junit.Test;

import activity.FraudDetectionSystem.FraudCheckResult;
import activity.FraudDetectionSystem.Transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class FraudDetectionSystemTest {
    private FraudDetectionSystem detectionSystem;

    @Before
    public void setUp(){
        detectionSystem = new FraudDetectionSystem();
    }

    @Test
    public void shouldBeAbleToDetectPotentiallyFraudIfTheActualTransactionAmountIsGreaterThan10000(){
        Transaction transaction = new Transaction(
            110000,
            LocalDateTime.now(),
            "São Paulo - Brazil"
        );

        FraudCheckResult result = detectionSystem.checkForFraud(transaction, new ArrayList<>(), List.of("Rio Branco - Acre"));

        assertEquals(result.isFraudulent, true);
        assertEquals(result.verificationRequired, true);
        assertEquals(result.riskScore, 50);
    }

    @Test
    public void shouldBeAbleToMarkAsBlockedIfThereAreMoreThan10TransactionsInTheLastHour(){
        List<Transaction> transactions = new ArrayList<>();
        for(int i = 1; i < 12; i++){
            transactions.add(new Transaction(
                i * 50,
                LocalDateTime.now(),
                "São Paulo - Brazil")
            );
        }

        Transaction transaction = new Transaction(
            500,
            LocalDateTime.now(),
            "São Paulo - Brazil"
        );

        FraudCheckResult result = detectionSystem.checkForFraud(transaction, transactions, List.of("Rio Branco - Brazil"));

        assertEquals(result.isBlocked, true);
        assertEquals(result.riskScore, 30);
    }

    @Test
    public void shouldBeAbleToDetectPotentiallyFraudIfActualTransactionOccursInADifferentLocationThanThePreviousOne(){
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(
            50, LocalDateTime.now(), "Rio de Janeiro - Brazil"));

        Transaction transaction = new Transaction(
            500,
            LocalDateTime.now(),
            "São Paulo - Brazil"
        );

        FraudCheckResult result = detectionSystem.checkForFraud(transaction, transactions, List.of("Rio Branco - Brazil"));

        assertEquals(result.isFraudulent, true);
        assertEquals(result.verificationRequired, true);
        assertEquals(result.riskScore, 20);
    }

    @Test
    public void shouldBeAbleToMarkAsBlockedIfTransactionLocationIsBlacklisted(){
        Transaction transaction = new Transaction(
            500,
            LocalDateTime.now(),
            "Rio Branco - Brazil"
        );

        FraudCheckResult result = detectionSystem.checkForFraud(transaction, new ArrayList<>(), List.of("Rio Branco - Brazil"));

        assertEquals(result.isBlocked, true);
        assertEquals(result.riskScore, 100);
    }

    @Test
    public void shouldNotBeAbleToDetectPotentiallyFraudOrMarkAsBlocked(){}

    public static Transaction generateTransaction(
        double amount,
        LocalDateTime timestamp,
        String location
    ){
        return new Transaction(amount, timestamp, location);
    }

    @Test
    public void shouldNotDetectFraudWhenTransactionAmountIsExactly10000() {
        Transaction transaction = new Transaction(
                10000,
                LocalDateTime.now(),
                "São Paulo - Brazil"
        );

        FraudCheckResult result = detectionSystem.checkForFraud(
                transaction,
                new ArrayList<>(),
                List.of("Rio Branco - Brazil")
        );

        assertFalse(result.isFraudulent);
        assertFalse(result.verificationRequired);
        assertEquals(0, result.riskScore);
    }

    @Test
    public void shouldNotBlockWhenTransactionsInLastHourAreExactly10() {
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            transactions.add(new Transaction(
                    50,
                    LocalDateTime.now().minusMinutes(59 - i),
                    "São Paulo - Brazil"
            ));
        }

        Transaction transaction = new Transaction(
                500,
                LocalDateTime.now(),
                "São Paulo - Brazil"
        );

        FraudCheckResult result = detectionSystem.checkForFraud(
                transaction,
                transactions,
                List.of("Rio Branco - Brazil")
        );

        assertFalse(result.isBlocked);
        assertEquals(0, result.riskScore);
    }

    @Test
    public void shouldNotDetectFraudWhenLocationChangedAfter30Minutes() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(
                50,
                LocalDateTime.now().minusMinutes(30),
                "Rio de Janeiro - Brazil"
        ));

        Transaction transaction = new Transaction(
                500,
                LocalDateTime.now(),
                "São Paulo - Brazil"
        );

        FraudCheckResult result = detectionSystem.checkForFraud(
                transaction,
                transactions,
                List.of("Rio Branco - Brazil")
        );

        assertFalse(result.isFraudulent);
        assertFalse(result.verificationRequired);
        assertEquals(0, result.riskScore);
    }

    @Test
    public void shouldNotBlockTransactionWhenLocationIsNotBlacklisted() {
        Transaction transaction = new Transaction(
                500,
                LocalDateTime.now(),
                "São Paulo - Brazil"
        );

        FraudCheckResult result = detectionSystem.checkForFraud(
                transaction,
                new ArrayList<>(),
                List.of("Rio Branco - Brazil")
        );

        assertFalse(result.isBlocked);
        assertEquals(0, result.riskScore);
    }

    @Test
    public void shouldBlockWhenTransactionsIncludeOneExactly60MinutesAgo() {
        List<Transaction> transactions = new ArrayList<>();

        transactions.add(new Transaction(
                50,
                LocalDateTime.now().minusMinutes(60),
                "São Paulo - Brazil"
        ));

        for (int i = 1; i <= 10; i++) {
            transactions.add(new Transaction(
                    50,
                    LocalDateTime.now().minusMinutes(60 - i),
                    "São Paulo - Brazil"
            ));
        }

        Transaction currentTransaction = new Transaction(
                500,
                LocalDateTime.now(),
                "São Paulo - Brazil"
        );

        FraudCheckResult result = detectionSystem.checkForFraud(
                currentTransaction,
                transactions,
                List.of("Rio Branco - Brazil")
        );

        assertTrue(result.isBlocked);
        assertEquals(30, result.riskScore);
    }

}
