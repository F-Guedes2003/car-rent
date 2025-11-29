package br.ifsp.vvts.ui.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private By emailInput = By.cssSelector("input[formControlName='username']");
    private By passwordInput = By.cssSelector("input[formControlName='password']");
    private By submitButton = By.cssSelector("button[type='submit']");
    private By togglePasswordVisibility = By.cssSelector("button[mat-icon-button][type='button']");
    private By registerLink = By.cssSelector("a[routerLink='/register']");

    private By emailRequiredError = By.xpath("//mat-error[contains(text(), 'Email é obrigatório')]");
    private By emailInvalidError = By.xpath("//mat-error[contains(text(), 'Email inválido')]");
    private By passwordRequiredError = By.xpath("//mat-error[contains(text(), 'Senha é obrigatória')]");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public LoginPage navigateTo() {
        driver.get("http://localhost:4200/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        return this;
    }

    public LoginPage fillEmail(String email) {
        WebElement el = driver.findElement(emailInput);
        el.clear();
        el.sendKeys(email);
        return this;
    }

    public LoginPage fillPassword(String password) {
        WebElement el = driver.findElement(passwordInput);
        el.clear();
        el.sendKeys(password);
        return this;
    }

    public String getEmailValue() {
        return driver.findElement(emailInput).getAttribute("value");
    }

    public String getPasswordValue() {
        return driver.findElement(passwordInput).getAttribute("value");
    }

    public LoginPage clickSubmit() {
        driver.findElement(submitButton).click();
        return this;
    }

    public LoginPage togglePassword() {
        driver.findElement(togglePasswordVisibility).click();
        return this;
    }

    public RegisterPage clickRegisterLink() {
        driver.findElement(registerLink).click();
        return new RegisterPage(driver);
    }

    public boolean isEmailRequiredErrorVisible() {
        return isVisible(emailRequiredError);
    }

    public boolean isEmailInvalidErrorVisible() {
        return isVisible(emailInvalidError);
    }

    public boolean isPasswordRequiredErrorVisible() {
        return isVisible(passwordRequiredError);
    }

    private boolean isVisible(By locator) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public LoginPage clearEmail() {
        driver.findElement(emailInput).clear();
        return this;
    }

    public LoginPage clearPassword() {
        driver.findElement(passwordInput).clear();
        return this;
    }
}
