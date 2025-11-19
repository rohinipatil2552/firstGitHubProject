package com.clars;

import java.awt.AWTException;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

public class clarsJR {
	public static void main(String[] args) throws IOException, InterruptedException, AWTException {
		System.out.println("Please enter File path");
		Scanner sc1 = new Scanner(System.in);
		String path = sc1.nextLine();
		LocalDate currentDate = LocalDate.now();
		LocalDate yesterdayDate = currentDate.minusDays(1);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		String formattedDate = yesterdayDate.format(formatter);

		System.out.println("File are downloading for " + formattedDate);

		Properties prop = new Properties();
		String login = path + "\\Login.txt";
		FileInputStream file = new FileInputStream(login);
		prop.load(file);
		String PW = prop.getProperty("password");
		String UN = prop.getProperty("username");
		String URL = prop.getProperty("Url");
		prop.load(file);
		String downloadfolder = path + "\\Daily\\" + formattedDate + "";
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("download.default_directory", downloadfolder);

		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", hm);
		// options.addArguments("--headless");
		WebDriver d = new ChromeDriver(options);

		d.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		d.manage().window().maximize();
		d.get(URL);
		d.findElement(By.id("dnn_ctr362_Login_Login_DNN_txtUsername")).sendKeys(UN);
		d.findElement(By.id("dnn_ctr362_Login_Login_DNN_txtPassword")).sendKeys(PW);
		d.findElement(By.id("dnn_ctr362_Login_Login_DNN_cmdLogin")).click();

		Thread.sleep(20000);
		d.switchTo().parentFrame();

		Set<String> WH1 = d.getWindowHandles();
		d.switchTo().window((String) WH1.toArray()[0]);
		d.switchTo().frame(0);

		WebElement reports = d.findElement(By.xpath("/html/body/form/div[3]/div/div[1]/span[1]/select"));
		Select s1 = new Select(reports);
		s1.selectByIndex(3);
		Thread.sleep(3000);
		Set<String> WH2 = d.getWindowHandles();
		d.switchTo().window((String) WH1.toArray()[0]);
		d.switchTo().frame(0);
		Thread.sleep(3000);

//        d.findElement(By.id("RV1_ctl04_ctl03_txtValue")).clear();
//        Thread.sleep(3000);
//
//		d.findElement(By.id("RV1_ctl04_ctl05_txtValue")).clear();
//        Thread.sleep(3000);
//
//   		d.findElement(By.id("RV1_ctl04_ctl03_txtValue")).sendKeys("12/3/2023");
//   		d.findElement(By.id("RV1_ctl04_ctl05_txtValue")).sendKeys("11/3/2023");

		for (int i = 1; i < 73; i++) {
			Thread.sleep(3000);
			WebElement Stores = d.findElement(By.id("ddlStores"));
			Select s2 = new Select(Stores);
			s2.selectByIndex(i);

			String selectedName = getSelectedOptionText(d, "ddlStores");

			if (!selectedName.contains("CLosed")) {
				d.findElement(By.id("RV1_ctl04_ctl00")).click();
				Thread.sleep(2000);
				d.findElement(By.id("RV1_ctl05_ctl04_ctl00_ButtonLink")).click();
				Thread.sleep(3000);
				d.findElement(By.cssSelector("a[title='Excel']")).click();
				System.out.println("Currently retrieving file " + i);
			}

		}

		Thread.sleep(2000);
		System.out.println("All your files are donwloaded");
		d.quit();
	}

	private static String getSelectedOptionText(WebDriver driver, String dropdownId) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		return (String) js.executeScript("return document.getElementById('" + dropdownId
				+ "').options[document.getElementById('" + dropdownId + "').selectedIndex].text;");
	}

}
