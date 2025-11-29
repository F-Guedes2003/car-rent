package br.ifsp.vvts.ui.register;

import br.ifsp.vvts.ui.pages.LoginPage;
import br.ifsp.vvts.ui.pages.RegisterPage;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("UiTest")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginPageTest {

    private WebDriver driver;
    private LoginPage loginPage;
    private Faker faker = new Faker();

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        loginPage = new LoginPage(driver).navigateTo();
    }

    @AfterEach
    void tearDown() {
        if (driver != null)
            driver.quit();
    }

    @Test
    @Order(1)
    @DisplayName("Should redirect to register page when click the register button")
    void alreadyHasAccountButton() {
        loginPage.clickRegisterLink();

        assertTrue(driver.getCurrentUrl().contains("/register"),
                "User should has been redirected to register page");
    }
}
