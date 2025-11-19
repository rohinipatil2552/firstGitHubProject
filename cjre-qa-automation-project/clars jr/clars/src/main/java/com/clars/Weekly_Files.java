package com.clars;

import java.awt.AWTException;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Weekly_Files {
	static String path;
	static String fileName;
	static String startDate;
	static String endDate;
	static String answer;
	static WebDriver d;
	static WebElement getStartDate;
	static WebElement getEndDate;

	public static void main(String[] args) throws IOException, InterruptedException, AWTException {
		// Menu Mix for Major-Minor Details.
		path = getInputFromUser("Please enter the file path:");
		fileName = getInputFromUser("Please enter the folder name for 'Menu Mix for Major-Minor Details':");
		startDate = getInputFromUser("Please enter the start date in MM/DD/YYYY format:");
		endDate = getInputFromUser("Please enter the end date in MM/DD/YYYY format:");
		getInitialiseDriver(fileName);
		getDownloadReports(3);
		d.quit();
		System.out.println("All your 'Menu Mix for Major-Minor Details' files have been downloaded.");

		// Employee Hours Summary
		fileName = getInputFromUser("Please enter the folder name for 'Employee Hours Summary':");
		while (true) {
			answer = getInputFromUser("Do you want to use the same dates? Press (Y/N):");
			if (answer.equalsIgnoreCase("N")) {
				startDate = getInputFromUser("Please enter the start date in MM/DD/YYYY format:");
				endDate = getInputFromUser("Please enter the end date in MM/DD/YYYY format:");
				break;
			} else if (answer.equalsIgnoreCase("Y")) {
				System.out.println("Start Date = " + startDate + " & End Date = " + endDate);
				break;
			} else {
				System.out.println("Invalid input. Please enter 'Y' or 'N' only.");
			}
		}
		getInitialiseDriver(fileName);
		getDownloadReports(1);
		Thread.sleep(1000);
		d.quit();
		System.out.println("All your Employee Hours Summary Files are donwloaded");
	}

	private static String getInputFromUser(String message) {
		System.out.println(message);
		Scanner scanner = new Scanner(System.in);
		return scanner.nextLine();
	}

	private static String getSelectedOptionText(WebDriver driver, String dropdownId) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		return (String) js.executeScript("return document.getElementById('" + dropdownId
				+ "').options[document.getElementById('" + dropdownId + "').selectedIndex].text;");
	}

	private static void getInitialiseDriver(String fileName) throws IOException, InterruptedException {
		Properties prop = new Properties();
		String login = path + "\\Login.txt";
		FileInputStream file = new FileInputStream(login);
		prop.load(file);
		String PW = prop.getProperty("password");
		String UN = prop.getProperty("username");
		String URL = prop.getProperty("Url");
		prop.load(file);
		String downloadfolder = path + "\\Weekly\\" + fileName;
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("download.default_directory", downloadfolder);

		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", hm);
		// options.addArguments("--headless");
		d = new ChromeDriver(options);
		d.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		d.manage().window().maximize();
		d.get(URL);
		d.findElement(By.id("dnn_ctr362_Login_Login_DNN_txtUsername")).sendKeys(UN);
		d.findElement(By.id("dnn_ctr362_Login_Login_DNN_txtPassword")).sendKeys(PW);
		d.findElement(By.id("dnn_ctr362_Login_Login_DNN_cmdLogin")).click();
	}

	private static void getDownloadReports(int index) throws InterruptedException {
		d.switchTo().parentFrame();
		Set<String> WH1 = d.getWindowHandles();
		d.switchTo().window((String) WH1.toArray()[0]);
		d.switchTo().frame("rptFrame");
		WebDriverWait wait = new WebDriverWait(d, Duration.ofSeconds(10));
		WebElement reports = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//select[@id='ddlReports']")));
		Select s1 = new Select(reports);
		s1.selectByIndex(index);
		Thread.sleep(3000);
		Set<String> WH2 = d.getWindowHandles();
		d.switchTo().window((String) WH1.toArray()[0]);
		d.switchTo().frame(0);

		// Clear and set the start date
		Thread.sleep(2000);
		getStartDate = d.findElement(By.id("RV1_ctl04_ctl03_txtValue"));
		getStartDate.clear();
		Thread.sleep(2000);
		getStartDate.sendKeys(startDate);

		// Clear and set the end date
		Thread.sleep(2000);
		getEndDate = d.findElement(By.id("RV1_ctl04_ctl05_txtValue"));
		getEndDate.clear();
		Thread.sleep(2000);
		getEndDate = d.findElement(By.id("RV1_ctl04_ctl05_txtValue")); // Re-locate the element
		if (!endDate.equals(getEndDate.getAttribute("value"))) {
			getEndDate.clear();
			getEndDate.sendKeys(endDate);
		}
		WebElement viewReportBtn = d.findElement(By.cssSelector("#RV1_ctl04_ctl00"));
		viewReportBtn.click();

		for (int i = 1; i < 73; i++) {
			WebElement stores = wait.until(ExpectedConditions.elementToBeClickable(By.id("ddlStores")));
			Select s2 = new Select(stores);
			Thread.sleep(2000);
			getEndDate = d.findElement(By.id("RV1_ctl04_ctl05_txtValue"));
			if (!endDate.equals(getEndDate.getAttribute("value"))) {
				getEndDate.clear();
				getEndDate.sendKeys(endDate);
			}
//			viewReportBtn.click();
			s2.selectByIndex(i);
			String selectedName = getSelectedOptionText(d, "ddlStores");
			if (!selectedName.contains("CLosed") && !selectedName.contains("Closed")) {
				Thread.sleep(2000);
				d.findElement(By.id("RV1_ctl04_ctl00")).click();
				Thread.sleep(2000);
				try {
					Alert alert = d.switchTo().alert();
					alert.accept();
					System.out.println("Error Observed While Downloading The File : " + i);
				} catch (NoAlertPresentException e) {
					d.findElement(By.id("RV1_ctl05_ctl04_ctl00_ButtonLink")).click();
					Thread.sleep(2000);
					d.findElement(By.cssSelector("a[title='Excel']")).click();
					System.out.println("Currently retrieving file " + i);
					Thread.sleep(2000);
				}
			}
//			wait = new WebDriverWait(d, Duration.ofSeconds(10));
//			if (!selectedName.contains("CLosed") && !selectedName.contains("Closed")) {
//			    d.findElement(By.id("RV1_ctl04_ctl00")).click();
//			    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("RV1_ctl05_ctl04_ctl00_ButtonLink")));
//			    d.findElement(By.id("RV1_ctl05_ctl04_ctl00_ButtonLink")).click();
//			    Thread.sleep(2000);
//			    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[title='Excel']")));
//			    d.findElement(By.cssSelector("a[title='Excel']")).click();
//			    System.out.println("Currently retrieving file " + i);
//			}
		}
	}

}
