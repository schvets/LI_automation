import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverProvider;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.TimerTask;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.refresh;

public class Main {

    @Test
    public void start() throws InterruptedException {
        String email = "admin";
        String password = "admin";
        Configuration.browser = CustomWebDriverProvider.class.getName();
        ChromeDriverManager.getInstance().setup();
        open("https://www.linkedin.com/");
        $x("//input[@class='login-email']").setValue(email);
        $x("//input[@class='login-password']").setValue(password);
        $x("//input[@id='login-submit']").click();
        open("https://www.linkedin.com/psettings/member-data");
        if (!$x("//button[@class='download-btn']").is(Condition.enabled)) {
            $x("//button[@class='request-new-archive-btn']").shouldBe(Condition.enabled).click();
            $x("//label[@for='fast-file-only-plus-other-data']").click();
            $x("//label[@for='fast-file-only']").click();
            $x("//label[@for='file_group_INBOX']").click();
            $x("//button[@id='download-button']").click();
            if ($x("//form[@class='password-prompt active']").is(Condition.visible)) {
                $x("//input[@id='verify-password'").setValue(password);
                $x("//button[@class='submit']").click();
            }
            int times = 8;
            while ((!$x("//button[@class='download-btn']").is(Condition.enabled)) || (times == 0)) {
                refresh();
                Thread.sleep(300000);
                times--;
            }
        }
        $x("//button[@class='download-btn']").click();
    }

    public static class CustomWebDriverProvider implements WebDriverProvider {
        @Override
        public WebDriver createDriver(DesiredCapabilities capabilities) {
            HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
            chromePrefs.put("download.default_directory", System.getProperty("user.dir"));
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("prefs", chromePrefs);
            DesiredCapabilities cap = DesiredCapabilities.chrome();
            return new ChromeDriver(options);
        }
    }

}