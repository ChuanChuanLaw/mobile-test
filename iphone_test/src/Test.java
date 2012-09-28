import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;

import org.apache.commons.io.FileUtils;
import org.junit.*;


public class Test
{
   public static void main ( String[] args ) throws Exception
   {   
	   //Initialize the webdriver. It should be smart enough to detect that it is a mobile app
	   WebDriver driver = new RemoteWebDriver(new URL("http://10.12.2.18:3001/wd/hub"), DesiredCapabilities.iphone());
	   driver.get("http://careers.pageuppeople.com/218/caw/en/listing");
	   	   
	   //Copy the existing main page as old image and change it into BufferedImage file type
	   FileUtils.copyFile(new File("/Users/chuan/mainPage.png"),new File("/Users/chuan/oldMainPage.png"));
	   BufferedImage oldMainPage = ImageIO.read(new File("/Users/chuan/oldMainPage.png"));
       	   
	   //Take a screen shot of the main page
	   WebDriver augmentedDriver = new Augmenter().augment(driver);
       File screenshot1 = ((TakesScreenshot)augmentedDriver).
                           getScreenshotAs(OutputType.FILE);
       
       FileUtils.copyFile(screenshot1, new File("/Users/chuan/mainPage.png"));       
       BufferedImage newMainPage = ImageIO.read(new File("/Users/chuan/mainPage.png"));
	   
       //Pass the old image to compare against the new image
       compareImage(oldMainPage,newMainPage);
      
	   //Search by keyword
	   WebElement search = driver.findElement(By.name("search-keyword"));
	   search.sendKeys("test analyst");
	   
	   //Enter the location. This is not a filter.
	   WebElement location = driver.findElement(By.name("search-location"));
	   location.sendKeys("Melbourne, VIC");
	   
	   WebDriverWait wait = new WebDriverWait(driver, 10);
	   WebElement locationLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Melbourne, VIC")));
	   locationLink.click();
	   
	   search.submit();
	   
	   //Looking for search keyword returned
	   boolean searchResult=driver.getPageSource().contains("test"); 
	   Assert.assertEquals(searchResult, true);
	   System.out.println("Job found");
	   
	   //Click into the job details
	   driver.findElement(By.className("job-link")).click();	   
	   boolean jobNo=driver.getPageSource().contains("Job no:"); 
	   Assert.assertEquals(jobNo, true);
	   System.out.println("Job no found");
	   
	   //Copy the existing main page as old image and change it into BufferedImage file type
	   FileUtils.copyFile(new File("/Users/chuan/jobDetails.png"),new File("/Users/chuan/oldJobDetails.png"));
	   BufferedImage oldJobDetails = ImageIO.read(new File("/Users/chuan/oldJobDetails.png"));
	   
	   //Take a screen shot of the job details page
	   File screenshot2 = ((TakesScreenshot)augmentedDriver).
               getScreenshotAs(OutputType.FILE);

       FileUtils.copyFile(screenshot2, new File("/Users/chuan/jobDetails.png"));
       BufferedImage newJobDetails = ImageIO.read(new File("/Users/chuan/jobDetails.png"));
       
       compareImage(oldJobDetails,newJobDetails);
	   
	   //Grab the job title
	   String jobTitle1=driver.findElement(By.xpath("//span[@id='page-heading']")).getText();
	   System.out.println(jobTitle1);
	   
	   //Apply for the job
	   WebElement email=driver.findElement(By.name("mobile-apply-email"));
	   email.sendKeys("chuanl@pageuppeople.com");	   
	   driver.findElement(By.xpath("//input[@value='Send']")).click();
	   
	   boolean msg=driver.getPageSource().contains("Thank you! You will receive an email");
	   Assert.assertEquals(msg, true);
	   System.out.println("Email sent");
	   
	   //Test integration with Facebook
	   driver.findElement(By.linkText("Facebook")).click();
	   WebElement fbEmail = wait.until(ExpectedConditions.elementToBeClickable(By.name("email")));
	   fbEmail.sendKeys("qa@pageuppeople.com");
	   
	   WebElement fbPass = driver.findElement(By.name("pass"));
	   fbPass.sendKeys("pagetestup");
	   
	   driver.findElement(By.name("login")).click();
	   
	   boolean fbMsg=driver.getPageSource().contains("Post a link to your profile");
	   Assert.assertEquals(fbMsg, true);
	   System.out.println("Facebook message");
	   
	   //Navigate back to mobile site
	   driver.navigate().back();
	   driver.navigate().back();
	   
	   //Back to search results
	   driver.findElement(By.linkText("Back to list")).click();	   
	   boolean backBtn = driver.getPageSource().contains("Search Results");
	   Assert.assertEquals(backBtn, true);
	   System.out.println("Back to search results");
	   
	   //Send me more jobs like this
	   driver.findElement(By.linkText("Send me jobs like these")).click();	   
	   WebElement subscription = driver.findElement(By.name("job-mail-subscribe-email"));
	   subscription.sendKeys("test@test.com");
	   
	   driver.findElement(By.name("job-mail-subscribe-button")).click();
	   
	   boolean similarJobsMsg = driver.getPageSource().contains("Ok, we will send you jobs like this");
	   Assert.assertEquals(similarJobsMsg, true);
	   System.out.println("Similar jobs email");
	   
	   //More jobs
	   driver.findElement(By.xpath("//a[@title='More Jobs']")).click();
	   driver.findElement(By.className("job-link")).click();	 
	   
	   //Check that the job returned is different
	   String jobTitle2=driver.findElement(By.xpath("//span[@id='page-heading']")).getText();
	   System.out.println(jobTitle2);
	   
	   //Making sure that jobs returned are different
	   boolean jobReturned=jobTitle1.equals(jobTitle2);
	   Assert.assertEquals(jobReturned, false);
	   
	   System.out.println("TEST ALL PASSED");
	   
	   driver.quit();
   }
 
   public static void compareImage(BufferedImage oldImage,BufferedImage newImage) throws IOException{        
       boolean ret=true;
       
       Raster ras1 = oldImage.getData();
       Raster ras2 = newImage.getData();
       //Comparing the the two images for number of bands,width & height.
       if (ras1.getNumBands() != ras2.getNumBands()
         || ras1.getWidth() != ras2.getWidth()
         || ras1.getHeight() != ras2.getHeight()) {
          ret=false;
       }
       else{
    	   // Once the band ,width & height matches, comparing the images.  	 
    	   search: for (int i = 0; i < ras1.getNumBands(); ++i) {
    	    for (int x = 0; x < ras1.getWidth(); ++x) {
    	     for (int y = 0; y < ras1.getHeight(); ++y) {
    	      if (ras1.getSample(x, y, i) != ras2.getSample(x, y, i)) {
    	     // If one of the result is false setting the result as false and breaking the loop.
    	       ret = false;
    	       break search;
    	      }
    	     }
    	    }
    	   }
       }
       if (ret == true) {
    	    System.out.println("Image matches");
    	   } else {
    	    System.out.println("Image does not matches");
       }           
   }
}	   