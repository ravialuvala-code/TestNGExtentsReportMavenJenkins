package hms.tests;

import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import org.testng.annotations.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;

public class BaseTest {

	public ExtentHtmlReporter htmlReporter;
	public ExtentReports extent;
	public ExtentTest extentTest;

	WebDriver driver;

	@BeforeClass
	public void beforeClass() {

		// Initialize all extents report configurations here

		htmlReporter = new ExtentHtmlReporter("./reports/extent.html");
		htmlReporter.config().setEncoding("utf-8");
		htmlReporter.config().setDocumentTitle("Automation Reports");
		htmlReporter.config().setReportName("Automation Test Results");
		htmlReporter.config().setTheme(Theme.STANDARD);

		extent = new ExtentReports();
		extent.setSystemInfo("Organization", "RaviAutomationLab");
		extent.setSystemInfo("Browser", "Chrome");
		extent.attachReporter(htmlReporter);

		System.setProperty("webdriver.chrome.driver",
				"E:\\soft\\RestAssuredJars\\chromedriver_win32_81_44_69\\chromedriver.exe");

		driver = new ChromeDriver();

		driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);

		driver.get("http://www.seleniumbymahesh.com/HMS/");

	}

	@AfterClass
	public void afterClass() {
		driver.close();
		extent.flush();
	}

	@Test
	public void testSuccessfull() {
		extentTest = extent.createTest("Successfull Test");
	}

	@Test
	public void testFailed() {

		extentTest = extent.createTest("Failed Test");
		Assert.fail("Executing Fail Method");

	}

	@Test
	public void testSkipped() {

		extentTest = extent.createTest("Skipped Test");
		throw new SkipException("Executing Skip Method");

	}

	@AfterMethod
	public void afterMethod(ITestResult result) {

		String methodName = result.getMethod().getMethodName();
		if (result.getStatus() == ITestResult.FAILURE) {
			String exceptionMessage = Arrays.toString(result.getThrowable().getStackTrace());

			extentTest.fail("<details><summary>" + "<b><font color =red>Exception occured, Click to see details:</font>"
					+ "</b></summary>" + exceptionMessage.replaceAll(",", "<br>") + "</details> \n");

			String path = takeScreenshot(methodName);
			try {
				extentTest.fail("<b><font color=red>" + "Screenshot of failure" + "</font></b>",
						MediaEntityBuilder.createScreenCaptureFromPath(path).build());

			} catch (IOException e) {
				extentTest.fail("Test Failed, Cannot attach screenshot");
			}

			String logText = "<b>Test Method " + methodName + " Failed</b>";
			Markup m = MarkupHelper.createLabel(logText, ExtentColor.RED);
			extentTest.log(Status.FAIL, m);
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			String logText = "<b>Test Method " + methodName + " Successfull</b>";
			Markup m = MarkupHelper.createLabel(logText, ExtentColor.GREEN);
			extentTest.log(Status.PASS, m);

		} else if (result.getStatus() == ITestResult.SKIP) {
			String logText = "<b>Test Method " + methodName + " Skipped</b>";
			Markup m = MarkupHelper.createLabel(logText, ExtentColor.YELLOW);
			extentTest.log(Status.SKIP, m);

		}

	}

	public String takeScreenshot(String methodName) {
		String fileName = getScreenshotName(methodName);
		String directory = System.getProperty("user.dir") + "/screenshots/";
		new File(directory).mkdir();
		String path = directory + fileName;

		try {
			File screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenShot, new File(path));
			System.out.println("================================");
			System.out.println("Screenshot stored at: " + path);
			System.out.println("================================");

		} catch (Exception e) {
			e.printStackTrace();

		}

		return path;
	}

	public static String getScreenshotName(String methodName) {
		Date d = new Date();
		String fileName = methodName + "_" + d.toString().replace(":", "_").replace(" ", "_") + ".png";
		return fileName;
	}

}
