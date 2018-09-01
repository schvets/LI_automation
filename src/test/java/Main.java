import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.refresh;

public class Main {
    final String email = "admin";
    final String password = "admin";
    // you must set up your own credential from Linkedin account

    @BeforeTest
    public void setupDriver() {
        Configuration.baseUrl = "https://www.linkedin.com";
        Configuration.browser = CustomWebDriverProvider.class.getName();
        Configuration.startMaximized = true;
        ChromeDriverManager.getInstance().setup();
    }

    @Test
    public void testDownload() throws InterruptedException {
        open("/");
        loginUser();

        open("/psettings/member-data");
        if (requestButtonVisible()) {
            requestNewArchive();
            setDownloadDataParam();
            tryDownloadExistedArchive();
            submitPasswordPromptIfItVisible();
            tryToDownloadArchiveWithRefresh();
        }
        else if(downloadExistedArchiveButtonEnabled()){
            tryDownloadExistedArchive();
        }
    }

    private void loginUser() {
        $x("//input[@class='login-email']").setValue(email);
        $x("//input[@class='login-password']").setValue(password);
        $x("//input[@id='login-submit']").click();
    }

    private void setDownloadDataParam() {
        $x("//label[@for='fast-file-only-plus-other-data']").click();
        $x("//label[@for='fast-file-only']").click();
        $x("//label[@for='file_group_INBOX']").click();
    }

    private boolean requestButtonVisible() {
        return $x("//button[contains(@class,'request')]").is(Condition.visible);
    }

    private void requestNewArchive() {
        $x("//button[contains(@class,'request')]").click();
    }

    private boolean requestButtonEnable() {
        return $x("//button[contains(@class,'request')]").is(Condition.enabled);
    }

    private void tryDownloadExistedArchive() throws InterruptedException {
        $x("//button[contains(@class,'download')]").click();
        Thread.sleep(90000);
    }

    private boolean downloadExistedArchiveButtonEnabled() {
        return $x("//button[contains(@class,'download')]").is(Condition.enabled);
    }

    private void submitPasswordPromptIfItVisible() {
        if ($x("//form[@class='password-prompt active']").is(Condition.visible)) {
            $x("//input[@id='verify-password']").setValue(password);
            $x("//button[@class='submit']").click();
        }
    }

    private void tryToDownloadArchiveWithRefresh() throws InterruptedException {
        int times = 8;
        while ((!$x("//button[contains(@class,'download')]").is(Condition.enabled)) || (times == 0)) {
            refresh();
            Thread.sleep(300000);
            times--;
        }
        tryDownloadExistedArchive();
    }


    public static class CustomWebDriverProvider implements WebDriverProvider {
        @Override
        public WebDriver createDriver(DesiredCapabilities capabilities) {
            HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
            chromePrefs.put("download.default_directory", System.getProperty("user.dir"));
            ChromeOptions options = new ChromeOptions();
//            options.addArguments("--headless");
//            options.addArguments("user-data-dir=/home/alex/.config/google-chrome/Default");
            options.setExperimentalOption("prefs", chromePrefs);
            ChromeDriverService driverService = ChromeDriverService.createDefaultService();
            return new ChromeDriver(driverService, options);
        }
    }

}


