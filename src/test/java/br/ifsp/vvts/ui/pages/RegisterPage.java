package br.ifsp.vvts.ui.pages;

import com.github.javafaker.Faker;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegisterPage {

    private WebDriver driver;
    private WebDriverWait wait;
    private Faker faker = new Faker();

    private By nameInput = By.cssSelector("input[formControlName='name']");
    private By lastnameInput = By.cssSelector("input[formControlName='lastname']");
    private By emailInput = By.cssSelector("input[formControlName='email']");
    private By passwordInput = By.cssSelector("input[formControlName='password']");
    private By submitButton = By.cssSelector("button[type='submit']");
    private By entreAquiButton = By.cssSelector("a[routerLink='/login']");
    private By snackbar = By.cssSelector("simple-snack-bar");

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public RegisterPage navigateTo() {
        driver.get("http://localhost:4200/register");
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameInput));
        return this;
    }

    // INSERIR VALORES

    public RegisterPage fillName(String name) {
        driver.findElement(nameInput).clear();
        driver.findElement(nameInput).sendKeys(name);
        return this;
    }

    public RegisterPage fillLastname(String lastname) {
        driver.findElement(lastnameInput).clear();
        driver.findElement(lastnameInput).sendKeys(lastname);
        return this;
    }

    public RegisterPage fillEmail(String email) {
        driver.findElement(emailInput).clear();
        driver.findElement(emailInput).sendKeys(email);
        return this;
    }

    public RegisterPage fillPassword(String password) {
        driver.findElement(passwordInput).clear();
        driver.findElement(passwordInput).sendKeys(password);
        return this;
    }

    //  OBTER VALORES

    public String getNameValue() {
        return driver.findElement(nameInput).getAttribute("value");
    }

    public String getLastnameValue() {
        return driver.findElement(lastnameInput).getAttribute("value");
    }

    public String getEmailValue() {
        return driver.findElement(emailInput).getAttribute("value");
    }

    public String getPasswordValue() {
        return driver.findElement(passwordInput).getAttribute("value");
    }

    public String getSnackbarMessage() {
        WebElement bar = wait.until(ExpectedConditions.visibilityOfElementLocated(snackbar));
        return bar.getText();
    }

    // MÉTODOS DE INTERAÇÃO

    public RegisterPage clickSubmit() {
        driver.findElement(submitButton).click();
        return this;
    }

    public RegisterPage clickEntreAqui() {
        driver.findElement(entreAquiButton).click();
        return this;
    }

    // MÉTODOS DE CHECKAGEM

    public boolean isSnackBarVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(snackbar));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSubmitButtonClickable() {
        WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(submitButton));

        return button.getAttribute("disabled") == null;
    }
}
