package ru.netology.bdd.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.bdd.data.DataHelper;
import ru.netology.bdd.page.DashboardPage;
import ru.netology.bdd.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.bdd.data.DataHelper.*;

public class TransferTest {
    DashboardPage dashboardPage;

    @BeforeEach
    void setup() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = getVerificationCode();
        dashboardPage = verificationPage.validVerify(verificationCode);

    }

    @Test
    void shouldTransferFromFirstToSecond() {
        var firstCardInfo = getFirstCardInfo();
        var secondCardInfo = getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        var amount = generateValidAmount(firstCardBalance);
        var expectedBalanceFirstCard = firstCardBalance - amount;
        var expectedBalanceSecondCard = secondCardBalance + amount;
        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
        var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
        var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard);
        assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard);


    }

   @Test
    void shouldGetErrorMessageIfAmountExceedsBalance() {
       var firstCardInfo = getFirstCardInfo();
       var secondCardInfo = getSecondCardInfo();
       var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
       var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
       var amount = generateInvalidAmount(secondCardBalance);
       var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
       transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);
       transferPage.findErrorMessage("Введена сумма, превышающая остаток на карте списания");
       var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
       var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);


    }

}