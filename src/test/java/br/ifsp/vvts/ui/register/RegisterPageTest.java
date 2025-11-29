package br.ifsp.vvts.ui.register;

import br.ifsp.vvts.ui.pages.RegisterPage;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Tag;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Tag("UiTest")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegisterPageTest {

    private WebDriver driver;
    private RegisterPage registerPage;
    private Faker faker = new Faker();

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        registerPage = new RegisterPage(driver).navigateTo();
    }

    @AfterEach
    void tearDown() {
        if (driver != null)
            driver.quit();
    }

    @Test
    @Order(1)
    @DisplayName("Successfull form fill should redirect to login and show a success snackbar")
    void testSuccessfulRegistrationShowsSnackbarAndRedirectsToLogin() {

        String randomName = faker.name().firstName();
        String randomLastname = faker.name().lastName();
        String randomEmail = faker.internet().emailAddress();
        String randomPassword = faker.internet().password(6, 10);

        registerPage
                .fillName(randomName)
                .fillLastname(randomLastname)
                .fillEmail(randomEmail)
                .fillPassword(randomPassword)
                .clickSubmit();

        assertTrue(registerPage.isSnackBarVisible(), "Snackbar should be visible");

        String snackMsg = registerPage.getSnackbarMessage();
        assertTrue(snackMsg.contains("Registro efetuado com sucesso! Faça o login."),
                "Incorrect snackbar message!");

        assertTrue(driver.getCurrentUrl().contains("/login"),
                "User should has been redirected to login page");
    }

    @Test
    @Order(3)
    @DisplayName("Button should be disabled when form is empty")
    void testValidationErrorsAppearWhenSubmittingEmptyForm() {

        assertFalse(registerPage.isSubmitButtonClickable());
    }

    @Test
    @Order(4)
    @DisplayName("Incomplete form should provide disabled submit button")
    void checkingSubmitFormWithIncompleteForm() {
        registerPage.fillName("Cuca");
        assertFalse(registerPage.isSubmitButtonClickable(),
                "Button should be disabled when form is incomplete");

        registerPage.fillLastname("Beludo");
        assertFalse(registerPage.isSubmitButtonClickable(),
                "Button should be disabled when form is incomplete");

        registerPage.fillEmail("blob@gmail.com");
        assertFalse(registerPage.isSubmitButtonClickable(),
                "Button should be disabled when form is incomplete");

        registerPage.fillPassword("abcd123@");
        assertTrue(registerPage.isSubmitButtonClickable(),
                "Button should be disabled when form is incomplete");
    }

    @Test
    @Order(5)
    @DisplayName("Should redirect to login page when click the already has an account button")
    void alreadyHasAccountButton() {
        registerPage.clickEntreAqui();

        assertTrue(driver.getCurrentUrl().contains("/login"),
                "User should has been redirected to login page");
    }
}
