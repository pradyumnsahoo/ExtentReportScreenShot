import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class FreeCRMTest {

	public WebDriver driver;
	public ExtentReports extent;
	public ExtentTest logger;


	@BeforeTest
	public void setExtent() {
	
		extent= new ExtentReports(System.getProperty("user.dir")+"\\test-output\\ExtentReport.html",true);
	//	extent.addSystemInfo("Host Name","My Computer");
		extent.addSystemInfo("Environment","QA");
	}
	
	@BeforeMethod
	public void setUp() {

		System.setProperty("webdriver.gecko.driver", "D:\\Automation\\Java\\geckodriver.exe");
		driver = new FirefoxDriver();
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get("https://www.freecrm.com/");

	}

	@Test
	public void freeCRMTitleTest() {
		logger = extent.startTest("freeCRMTitleTest"); //This line must be added for every @Test
		String title = driver.getTitle();
		System.out.println(title);
		Assert.assertEquals(title, "Free CRM #1 cloud software for any business large or small123");
	}


	@Test
	public void freeCRMLogoTest() {
		logger = extent.startTest("freeCRMLogoTest"); //This line must be added for every @Test
		boolean b = driver.findElement(By.xpath("//div[@class='icon icon-lg-round icon-yellow-round icon-shadow mdi-shield-outline']")).isDisplayed();
		Assert.assertTrue(b);
	}
	
	
	@AfterMethod
	public void tearDown(ITestResult result) throws IOException {
		if(result.getStatus()==ITestResult.FAILURE) {
			logger.log(LogStatus.FAIL, "Test case failed is "+result.getName());//to add name in extent report
			logger.log(LogStatus.FAIL, "Test case failed is "+result.getThrowable());//to add error/exception in extent report
			
			String screenshotPath= getScreenshot(driver,result.getName());
			logger.log(LogStatus.FAIL, logger.addScreenCapture(screenshotPath));
//			logger.log(LogStatus.FAIL,logger.addScreencast(screenshotPath));//to add screenshot in extent report
		}
		else if(result.getStatus()==ITestResult.SKIP) {
			logger.log(LogStatus.SKIP, "Test case skipped is " +result.getName());
			
		}
		else if (result.getStatus()==ITestResult.SUCCESS) {
			logger.log(LogStatus.PASS, "Test case passed is "+result.getName());
		}
		extent.endTest(logger); //ending test and ends the current test and prepare to create html report
		driver.quit();
	}


	@AfterTest
	public void endReport() {
		extent.flush();
		extent.close();
	}
	
	public static String getScreenshot(WebDriver driver, String screenshotName) throws IOException {
		String dateName = new SimpleDateFormat("yyyymmddhhmmss").format(new Date());
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		String destination = System.getProperty("user.dir")+"/FailedTestsScreenshots/" + screenshotName + dateName + ".png";
		File finalDestination= new File(destination);
		FileUtils.copyFile(source, finalDestination);
		return destination;


	}
}
