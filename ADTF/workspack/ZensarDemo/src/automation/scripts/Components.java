/*******************************************************************
 * A user specified class, which removes all the dependencies 
 * that may associate with code or tool used
 *******************************************************************/

package automation.scripts;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import com.adaequare.testng.adtf.util.AdtfDateUtil;
import com.sun.jna.Platform;
import com.thoughtworks.selenium.SeleneseTestBase;
import com.thoughtworks.selenium.webdriven.*;   //selenium.SeleneseTestBase;

@SuppressWarnings("deprecation")
public class Components extends SeleneseTestBase {


	public static final String TIMEOUT = "120000";
	public Logger logger = Logger.getRootLogger();

	public static Map<Thread, WebDriverBackedSelenium> selenium = new HashMap<Thread, WebDriverBackedSelenium>();
	// public static ThreadLocal<RemoteWebDriver> driver = new
	// ThreadLocal<RemoteWebDriver>();

	public static Map<Thread, RemoteWebDriver> driver = new HashMap<Thread, RemoteWebDriver>();
	ChromeDriverService service;
	InternetExplorerDriverService ieservice;
	public boolean chromeflag;
	public boolean ieflag;
	RemoteWebDriver driverInstance;
	DesiredCapabilities capabilities;
	WebDriverBackedSelenium seleniumInstance;
	private int pageInnerX;
	private int pageInnerY;
	public static Map<String, String> randomStringMap = new HashMap<String, String>();
	public static Map<Thread, String> originalHandle = new HashMap<Thread, String>();

	@Parameters({ "username", "hostname", "portnumber", "browser", "version",
		"platform", "URL" })
	@BeforeSuite
	public void startUp(String user, String hostname, String portnumber,
			String browser, String version, String platform, String URL)
					throws IOException {

		if (browser.equalsIgnoreCase("FIREFOX")) {
			capabilities = DesiredCapabilities.firefox();
			logger.info("started firefox browser @host  :   " + hostname);
		} else if (browser.equalsIgnoreCase("ANDROID")) {
			capabilities = DesiredCapabilities.android();
			logger.info("started Android Emulator @host   :   " + hostname);
		} else if (browser.equalsIgnoreCase("CHROME")) {
			chromeflag = true;

			File chromeDriverPath = new File(System.getProperty("user.home")
					+ File.separator + "chromedriver");
			if (System.getProperty("os.name").contains("Windows")) {
				chromeDriverPath = new File(System.getProperty("user.home")
						+ File.separator + "chromedriver.exe");

			}

			if (chromeDriverPath.exists()) {

				service = new ChromeDriverService.Builder()
				.usingDriverExecutable(chromeDriverPath)
				.usingAnyFreePort().build();
				if (!service.isRunning()) {
					service.start();
					logger.info("	chrome selenium service for  started");
				} else {
					logger.info("	chrome selenium service already started");
				}
			} else {
				logger.warn("	please place chrome driver @  "
						+ System.getProperty("user.home")
						+ "or  start selenium server using command>  java -Dwebdriver.chrome.driver=C:\\chromedriver.exe -jar selenium-server-standalone-2.23.1.jar");
				logger.warn("download chrome driver from    http://code.google.com/p/chromedriver/downloads/list ");

				chromeflag = false;
			}

			capabilities = DesiredCapabilities.chrome();

			logger.info("	started Google Chrome @host   :   " + hostname);

		} else {// IE as default browser

			capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setBrowserName("internet explorer");
			capabilities.setCapability( InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			logger.info("	started IE browser @host   :   " + hostname);
			System.out.println("	started IE browser @host   :   " + hostname);


		}

		try {
			
			Integer.parseInt(portnumber);
			capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			capabilities.setCapability(CapabilityType.VERSION, version);
			if (platform.contains("Mac")) {
				capabilities.setCapability(CapabilityType.PLATFORM,
						Platform.MAC);
			} else if (platform.contains("Solaries")) {
				capabilities.setCapability(CapabilityType.PLATFORM,
						Platform.SOLARIS);
			} else if (platform.contains("Linux")) {
				capabilities.setCapability(CapabilityType.PLATFORM,
						Platform.LINUX);
			} else {
				System.out.println("Window plat	");
				capabilities.setJavascriptEnabled(true);
				capabilities.setCapability(CapabilityType.VERSION, version);
				//capabilities.setCapability(CapabilityType.PLATFORM, Platform.WINDOWS );
				capabilities.setPlatform(org.openqa.selenium.Platform.ANY);
				
			}

			logger.info("	 Browser Configuration :::  " + capabilities);
		} catch (Exception e) {
			portnumber = "4444";
			logger.warn("	port number reset to 4444 @host:  " + hostname);
		}

		if (chromeflag) {
			logger.info(" Chrome Service URL :::  " + service.getUrl());
			driverInstance = new RemoteWebDriver(service.getUrl(), capabilities);
			
		} else  {
			System.out.println("	IE browser ");
			driverInstance = new RemoteWebDriver(new URL("http://" + hostname
					+ ":" + portnumber+"/wd/hub" ), capabilities);
			
		}

		String threadName = Thread.currentThread().getName();

		if (!threadName.contains(user)) {
			Thread.currentThread().setName(user + "_" + threadName);
		}

		driver.put(Thread.currentThread(), driverInstance);

		seleniumInstance = new WebDriverBackedSelenium(driver.get(Thread
				.currentThread()), URL);

		selenium.put(Thread.currentThread(), seleniumInstance);
		logger.info(" created WebDriverBackedSelenium instance "
				+ selenium.get(Thread.currentThread()));

		logger.info("  OENING APPLICATION URL :::    " + URL);
		selenium.get(Thread.currentThread()).setTimeout("60000");

	}

	@AfterSuite
	public void shutdown() throws IOException {
		try {
			logger.info("	shutdown command executing....	");
			logger.info("  WebDriverBackedSelenium Instance "
					+ selenium.get(Thread.currentThread()));
			logger.info("  RemoteWebDriver Instance   "
					+ driver.get(Thread.currentThread()));

			if ((service != null) && (service.isRunning())) {
				service.stop();
			}

			driver.get(Thread.currentThread()).close();
			logger.info("	shutdown command executed	");
		} catch (Exception e) {
			logger.error("	shutdown command error	" + e.getMessage());
		}
	}

	public void getPage(String name) throws InterruptedException {
		selenium.get(Thread.currentThread()).open(name);

		pauseExecution("2000");

		logger.info("getPage " + name + " invoked");
	}

	public void pressEnter(String obj) throws InterruptedException {

		selenium.get(Thread.currentThread()).keyPress(obj, "\\13");
		pauseExecution("2000");

	}

	public void verifyEditable(String name, String Flag) {

		if (Flag.equalsIgnoreCase("True")) {
			verifyTrue(selenium.get(Thread.currentThread()).isEditable(name));
		} else if (Flag.equalsIgnoreCase("False")) {
			verifyTrue(!selenium.get(Thread.currentThread()).isEditable(name));
		}
	}

	public void verifyNotChecked(String name) {
		Assert.assertFalse(selenium.get(Thread.currentThread()).isChecked(name));
	}

	public void waitForPopUp(String obj, String timeout) {

		selenium.get(Thread.currentThread()).waitForPopUp(obj, timeout);
	}

	public void storeCurrentWindow(String test) {

		originalHandle.put(Thread.currentThread(),
				driver.get(Thread.currentThread()).getWindowHandle());

	}

	public void selectWindow(String obj) throws InterruptedException  {
		String timeout="1000";
		if (obj.equalsIgnoreCase("ParentWindow")) {
			logger.info(" inside the select parent window ");
			driver.get(Thread.currentThread()).switchTo()
			.window(originalHandle.get(Thread.currentThread()));
			pauseExecution(timeout);
		} else {
			selenium.get(Thread.currentThread()).selectWindow(obj);
			pauseExecution(timeout);
			logger.info(" inside the select child window ");
		}

	}

	public void windowFocus(String obj) {
		selenium.get(Thread.currentThread()).windowFocus();
	}

	public void injectJS() {

		String script = "window.onbeforeunload = vtr;" + "function vtr()" + "{"
				+ "}";
		selenium.get(Thread.currentThread()).runScript(script);
	}

	public void runScript(String script) {
		selenium.get(Thread.currentThread()).runScript(script);
	}

	public void click(String name) {
		injectJS();
		selenium.get(Thread.currentThread()).click(name);
	}

	public void doMouseOver(String locator) throws InterruptedException, ExecutionException, TimeoutException, AWTException{

		injectJS();
		int leftOffset = selenium.get(Thread.currentThread()).getElementPositionLeft(locator).intValue();			
		int topOffset = selenium.get(Thread.currentThread()).getElementPositionTop(locator).intValue();
		int width = selenium.get(Thread.currentThread()).getElementWidth(locator).intValue();
		int height = selenium.get(Thread.currentThread()).getElementHeight(locator).intValue();

		int elementXPosition = this.pageInnerX + leftOffset + new Integer(width/2).intValue();
		int elementYPosition = this.pageInnerY + topOffset + new Integer(height/2).intValue();

		Robot robot = new Robot();
		robot.mouseMove(elementXPosition, elementYPosition);

	}

	public void clickAndWait(String name, String Expected)
			throws InterruptedException {
		injectJS();
		selenium.get(Thread.currentThread()).click(name);
		pauseExecution(Expected);
	}

	public void clickAndVerifyTitle(String name, String title) {
		injectJS();
		selenium.get(Thread.currentThread()).click(name);
		waitForPageToLoad("120000");
		Assert.assertEquals(selenium.get(Thread.currentThread()).getTitle(),
				title);
	}

	public void clickAndVerifyElement(String name, String objName) {
		injectJS();
		selenium.get(Thread.currentThread()).click(name);
		waitForPageToLoad("120000");
		Assert.assertTrue(selenium.get(Thread.currentThread())
				.isElementPresent(objName));
	}

	public void clickAndVerifyAlert(String name, String text)
			throws InterruptedException {
		injectJS();
		selenium.get(Thread.currentThread()).click(name);
		pauseExecution("10000");
		Assert.assertEquals(selenium.get(Thread.currentThread()).getAlert(),
				text);
	}

	public void enterValue(String name, String text) {
		injectJS();
		if ("Blank".equalsIgnoreCase(text)) {
			selenium.get(Thread.currentThread()).type(name, "");
		} else if ("RandomID".equalsIgnoreCase(text)) {
			selenium.get(Thread.currentThread()).type(name,
					"tester" + AdtfDateUtil.getDateString() + "@adtf.com");
		} else if ("RandomString".equalsIgnoreCase(text)) {
			String random = "tester" + AdtfDateUtil.getDateString();
			logger.info("Saving  RandomString   " + random);
			randomStringMap.put(Thread.currentThread().getName(), random);
			selenium.get(Thread.currentThread()).type(name, random);
		} else if ("RandomStringSpl".equalsIgnoreCase(text)) {
			selenium.get(Thread.currentThread()).type(name,
					"tester" + AdtfDateUtil.getDateString()+ "#@");
		} else if ("RandomStringSpace".equalsIgnoreCase(text)) {
			selenium.get(Thread.currentThread()).type(name,
					"tester  " + AdtfDateUtil.getDateString());
		} else if ("StoredString".equalsIgnoreCase(text)) {
			String stored = randomStringMap.get(Thread.currentThread()
					.getName());
			logger.info(" using  StoreString " + stored);
			selenium.get(Thread.currentThread()).type(name, stored);

		} else {
			selenium.get(Thread.currentThread()).type(name, text);
		}

	}

	public void verifyValue(String name, String Text) {
		Assert.assertEquals(
				selenium.get(Thread.currentThread()).getValue(name), Text);
	}

	public void waitForPageToLoad(String Time) {
		selenium.get(Thread.currentThread()).waitForPageToLoad(Time);
	}

	public void pauseExecution(String Expected) throws InterruptedException {
		Thread.sleep(Integer.parseInt(Expected));
	}

	public void verifyText(String text) {

		if ("StoredString".equalsIgnoreCase(text)) {
			Assert.assertTrue(selenium.get(Thread.currentThread())
					.isTextPresent(
							randomStringMap.get(Thread.currentThread()
									.getName())));

		} else {
			Assert.assertTrue(selenium.get(Thread.currentThread())
					.isTextPresent(text));
		}
	}

	public boolean elementExists(String obj) {

		return (selenium.get(Thread.currentThread()).isElementPresent(obj));
	}

	public void verifyElement(String Text) throws InterruptedException {
		Assert.assertTrue(selenium.get(Thread.currentThread())
				.isElementPresent(Text));
		pauseExecution("3000");
	}

	public void getAllElements() {

	}

	public void Check(String xpath1) {
		if (!driver.get(Thread.currentThread()).findElement(By.xpath(xpath1))
				.isSelected()) {
			driver.get(Thread.currentThread()).findElement(By.xpath(xpath1))
			.click();

		}
	}

	public void selectFrame(String obj) {

		if (obj.equalsIgnoreCase("NA")) {
			selenium.get(Thread.currentThread()).selectFrame(null);

		} else {
			selenium.get(Thread.currentThread()).selectFrame(obj);
		}

	}

	public void highlight(String locator) {
		selenium.get(Thread.currentThread()).highlight(locator);
	}

	public void Uncheck(String xpath1) {
		if (driver.get(Thread.currentThread()).findElement(By.xpath(xpath1))
				.isSelected()) {
			driver.get(Thread.currentThread()).findElement(By.xpath(xpath1))
			.click();
		}
	}

	public void verifyChecked(String name) {
		Assert.assertTrue(selenium.get(Thread.currentThread()).isChecked(name));
	}

	public void close() {
		selenium.get(Thread.currentThread()).close();
		System.out.println("inclose");
	}

	public void select(String selectLocator, String optionLocator) {

		if ("StoredString".equalsIgnoreCase(optionLocator)) {

			Select select = new Select(driver.get(Thread.currentThread())
					.findElement(By.xpath(selectLocator)));
			select.selectByVisibleText(randomStringMap.get(Thread
					.currentThread().getName()));

		} else {

			Select select = new Select(driver.get(Thread.currentThread())
					.findElement(By.xpath(selectLocator)));
			select.selectByVisibleText(optionLocator);
		}
	}

	public void waitForElementPresent(String objName)
			throws InterruptedException {

		for (int second = 0;; second++) {
			if (second >= 60)
				Assert.fail("timeout");
			try {
				if (selenium.get(Thread.currentThread()).isElementPresent(
						objName))
					break;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}
		pauseExecution("1000");

	}

	public void waitForTextPresent(String Objid, String name)
			throws InterruptedException {

		for (int second = 0;; second++) {
			if (second >= 60)
				Assert.fail("timeout");
			try {
				if (name.equals(selenium.get(Thread.currentThread()).getText(
						Objid)))
					break;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}

		pauseExecution("1000");
	}

	public void answerOnNextPrompt(String descr) {

		Alert alert = driver.get(Thread.currentThread()).switchTo().alert();
		alert.sendKeys(descr);
		alert.accept();
	}

	public void getPrompt(String Text) {

		Assert.assertEquals(selenium.get(Thread.currentThread()).getPrompt(),
				Text);
	}

	public void chooseCancelOnNextConfirmation(String name, String Expected)
			throws InterruptedException {
		selenium.get(Thread.currentThread()).chooseCancelOnNextConfirmation();
		selenium.get(Thread.currentThread()).click(name);

		Assert.assertTrue(selenium.get(Thread.currentThread())
				.getConfirmation().matches(Expected));
		pauseExecution("5000");
	}

	public void chooseOkOnNextConfirmation(String name, String Expected)
			throws InterruptedException {

		selenium.get(Thread.currentThread()).chooseOkOnNextConfirmation();
		selenium.get(Thread.currentThread()).click(name);

		Assert.assertTrue(selenium.get(Thread.currentThread())
				.getConfirmation().matches(Expected));
		pauseExecution("20000");
	}

	public void chooseOkOnNextConfirmation1(String name, String Expected)
			throws InterruptedException {

		selenium.get(Thread.currentThread()).click(name);
		selenium.get(Thread.currentThread()).chooseOkOnNextConfirmation();

		Assert.assertEquals(Expected, selenium.get(Thread.currentThread())
				.getConfirmation());

	}

	public void assertText(String obj, String Text) {

		Assert.assertEquals(selenium.get(Thread.currentThread()).getText(obj),
				Text);
	}

	public void assertEditable(String objName, String flag)
			throws InterruptedException {

		if (flag.equalsIgnoreCase("true"))
			Assert.assertTrue(selenium.get(Thread.currentThread()).isEditable(
					objName));
		else if (flag.equalsIgnoreCase("false"))
			Assert.assertFalse(selenium.get(Thread.currentThread()).isEditable(
					objName));
		pauseExecution("3000");
	}



	public void assertElementNotPresent(String elementLocator) {
		Assert.assertFalse(selenium.get(Thread.currentThread())
				.isElementPresent(elementLocator));

	}

	public void verifyTextNotPresent(String elementLocator) throws Exception {
		verifyFalse(selenium.get(Thread.currentThread()).isTextPresent(
				elementLocator));

	}

	public void captureScreen(String imageFolder) {
		try {

			logger.info(" Screen Caputing ....");
			// RemoteWebDriver does not implement the TakesScreenshot class
			// if the driver does have the Capabilities to take a screenshot
			// then Augmenter will add the TakesScreenshot methods to the
			// instance
			WebDriver augmentedDriver = new Augmenter().augment(driver
					.get(Thread.currentThread()));
			byte[] screenshotBytes = ((TakesScreenshot) augmentedDriver)
					.getScreenshotAs(OutputType.BYTES);

			File imageFolderFile = new File(imageFolder);

			if (!imageFolderFile.exists()) {
				imageFolderFile.mkdir();
			}

			File cauptureImage = new File(imageFolder + File.separator
					+ System.currentTimeMillis() + ".png");

			FileOutputStream fos = new FileOutputStream(cauptureImage);
			fos.write(screenshotBytes);
			fos.flush();
			fos.close();
			logger.info(" Screen Captured @  : "
					+ cauptureImage.getAbsolutePath());
		} catch (Exception e) {
			logger.error(" error Screen Captured ");
		}

	}

}
