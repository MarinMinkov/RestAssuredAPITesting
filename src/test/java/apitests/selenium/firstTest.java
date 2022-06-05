package apitests.selenium;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.util.List;

public class firstTest {

    public static void main(String[] args) {

/*
        //setting chrome driver manually.
        System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe");
*/


        // Using WebDriverManager library
        WebDriverManager.chromedriver().setup();

        ChromeDriver driver = new ChromeDriver();
        driver.get("http://training.skillo-bg.com/posts/all");
        driver.manage().window().maximize();

        WebElement loginButton = driver.findElement(By.id("nav-link-login"));
        WebElement homeButton = driver.findElement(By.linkText("Home"));

        //driver.findElement(By.id("nav-link-login")).click();
        //List<WebElement> loginButtons = driver.findElements(By.id("nav-link-login"));

        // Test
        loginButton.click();
        WebElement userNameField = driver.findElement(By.id("defaultLoginFormUsername"));
        userNameField.sendKeys("user51");
        homeButton.click();

        //Thread.sleep(5000); // do not use. Needs: throws InterruptedException
        driver.close();
    }
}
