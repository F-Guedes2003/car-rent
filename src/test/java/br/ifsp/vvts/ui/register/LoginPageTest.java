package br.ifsp.vvts.ui.register;

import br.ifsp.vvts.security.user.JpaUserRepository;
import br.ifsp.vvts.security.user.Role;
import br.ifsp.vvts.security.user.User;
import br.ifsp.vvts.ui.pages.LoginPage;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@Tag("UiTest")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginPageTest {

    private WebDriver driver;
    private LoginPage loginPage;
    private Faker faker = new Faker();
    private static final String TEST_EMAIL = "test.user@gmail.com";
    private static final String TEST_PASSWORD = "Test@123";
    private static final String DB_URL = "jdbc:sqlite:database.db";

    @BeforeAll
    static void setUpTestData() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String deleteSql = "DELETE FROM app_user WHERE email = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, TEST_EMAIL);
                deleteStmt.executeUpdate();
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            String insertSql = "INSERT INTO app_user (name, email, password, lastName, role, id) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, "Test");
                insertStmt.setString(2, TEST_EMAIL);
                insertStmt.setString(3, encoder.encode(TEST_PASSWORD));
                insertStmt.setString(4, "User");
                insertStmt.setString(5, Role.USER.name());
                insertStmt.setString(6, UUID.randomUUID().toString());
                insertStmt.executeUpdate();
            }

            System.out.println("successfully created test user!");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed on creating test user: " + e.getMessage());
        }
    }

    @AfterAll
    static void cleanUpTestData() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String deleteSql = "DELETE FROM app_user WHERE email = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, TEST_EMAIL);
                deleteStmt.executeUpdate();
            }
            System.out.println("Test user removed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    @Test
    @Order(2)
    @DisplayName("Submit Button should be disabled when login form is incomplete")
    void submitButtonShouldBeDisabledWhenLoginFormIsIncomplete() {
        loginPage.fillEmail(faker.internet().emailAddress());
        assertFalse(loginPage.isSubmitButtonEnabled());

        loginPage.fillPassword(faker.internet().password(0, 6));
        assertTrue(loginPage.isSubmitButtonEnabled());
    }

    @Test
    @Order(3)
    @DisplayName("Submit button Should be Disabled when login form is empty")
    void submitButtonShouldBeDisabledWhenLoginFormIsEmpty() {
        assertFalse(loginPage.isSubmitButtonEnabled());
    }

    @Test
    @Order(3)
    @DisplayName("Successfull submit should redirect to rental list page")
    void successfullSubmitShouldRedirectToRentalListPage() {
        loginPage.fillEmail(TEST_EMAIL)
                .fillPassword(TEST_PASSWORD)
                .clickSubmit();

        assertTrue(driver.getCurrentUrl().contains("/login"),
                "User should has been redirected to login page");
    }
}
