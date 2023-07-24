package com.cst438;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.junit.jupiter.api.Test;

public class EndToEndNewAssignment {

	@Test
	public void testEndToEndAssignment() {
		
		// Set the path to the ChromeDriver executable
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");

		// Create a new instance of the ChromeDriver
		WebDriver driver = new ChromeDriver();

		// Navigate to the home page
		driver.get("http://localhost:3000/");

		// Click on the 'ADD NEW ASSIGNMENT' button
		WebElement addAssignmentButton = driver.findElement(By.id("create-new-assignment"));
		addAssignmentButton.click();

		// Enter the name of the assignment
		WebElement assignmentNameInput = driver.findElement(By.id("name"));
		assignmentNameInput.sendKeys("Sample Assignment");

		// Enter the date in yyyy-mm-dd format
		WebElement dueDateInput = driver.findElement(By.id("dueDate"));
		dueDateInput.sendKeys("2023-07-10");

		// Select a course from the list of selectors
		String courseTitle = "cst438-software engineering"; // Replace with the desired course title
        WebElement courseElement = driver.findElement(By.xpath("//label[text()='" + courseTitle + "']"));
        courseElement.click();

		// Click on the 'CREATE ASSIGNMENT' button
		WebElement createAssignmentButton = driver.findElement(By.id("create-assignment"));
		createAssignmentButton.click();

		 // Go back to the home page to verify the added assignment
        driver.navigate().back();

        // Refresh the page to see the added assignment
        driver.navigate().refresh();

		// Locate the assignment you added
		WebElement assignmentElement = driver.findElement(By.xpath("//div[contains(text(), 'Sample Assignment')]"));

		// Verify if the name, course, and due date match what was added
		String assignmentName = assignmentElement.getText();
		String course = driver.findElement(By.xpath("//div[contains(text(), 'cst438-software engineering')]")).getText();
		String dueDate = driver.findElement(By.xpath("//div[contains(text(), '2023-07-10')]")).getText();

		if (assignmentName.equals("Sample Assignment") && course.equals("cst438-software engineering")
				&& dueDate.equals("2023-07-10")) {
			System.out.println("Assignment added!");
		} else {
			System.out.println("Failed to add assignment");
		}
		// Close the browser
		driver.quit();
	}
}