package com.falak.qa.pages.home;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import com.falak.qa.base.BasePage;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;

/** يمثل نافذة (مودال) «التفاصيل» التي تظهر بعد الضغط على زر الأداة. */
public class ToolDetailsModal extends BasePage {



	/* ────────── Locators ────────── */


	private final By dialogContent = By.xpath("//p-dynamicdialog//div[contains(@class,'p-dialog') and contains(@class,'p-dynamic-dialog')]//div[contains(@class,'p-dialog-content')]");
	private final By dialogMask = By.xpath(
			"//p-dynamicdialog//div[contains(@class,'p-dialog-mask') and contains(@class,'p-component-overlay')]");
	private final By dialogRoot = By
			.xpath("//p-dynamicdialog//div[contains(@class,'p-dialog') and contains(@class,'p-dynamic-dialog')]");
	private final By closeBtn = By
			.xpath("//p-dynamicdialog//div[contains(@class,'p-dialog-header')]//button[@aria-label='Close']");
	private final By titleAr = By.xpath(
			"//p-dynamicdialog//div[contains(@class,'tool-desc-overlay')]//span[contains(@class,'text-2xl')][1]");
	private final By titleEn = By.xpath(
			"//p-dynamicdialog//div[contains(@class,'tool-desc-overlay')]//span[contains(@class,'text-2xl')]//span[contains(@class,'text-500')]");
	private final By iconImg = By.xpath(
			"//p-dynamicdialog//div[contains(@class,'tool-desc-overlay')]//img[starts-with(@src,'assets/tool-')]");
	private final By video = By.xpath("//p-dynamicdialog//div[contains(@class,'tool-desc-overlay')]//video");
	private final By paragraph = By.xpath("//p-dynamicdialog//p[contains(@class,'content-desc')]");

	  // ✅ Constructor يرث من BasePage
    public ToolDetailsModal(WebDriver driver) {
        super(driver); // يمرر WebDriver إلى BasePage
        wait.until(ExpectedConditions.visibilityOfElementLocated(dialogRoot)); // انتظار ظهور المودال
    }

	/**
	 * 👁️ يتحقق من ظهور نافذة التفاصيل (المودال)
	 * Checks whether the tool details modal is currently visible
	 *
	 * @return true إذا كانت النافذة ظاهرة، false إذا لم تكن كذلك
	 */
	@Step("Check if tool details modal is visible")
	public boolean isVisible() {
		try {
			return driver.findElement(dialogRoot).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * ❌ يتحقق من زر الإغلاق في النافذة
	 * Checks if the close button is visible in the modal
	 *
	 * @return true إذا كان الزر ظاهرًا، false غير ذلك
	 */
	@Step("Check if close button is visible")
	public boolean isCloseVisible() {
		try {
			return isElementVisible(closeBtn);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 🖼️ يتحقق من عرض صورة الأداة
	 * Checks if the tool icon image is visible in the modal
	 *
	 * @return true إذا كانت الصورة ظاهرة، false غير ذلك
	 */
	@Step("Check if tool icon is visible")
	public boolean isIconVisible() {
		try {
			return isElementVisible(iconImg);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 🎥 يتحقق من ظهور الفيديو داخل المودال
	 * Checks if the video component is visible in the modal
	 *
	 * @return true إذا كان الفيديو ظاهرًا، false غير ذلك
	 */
	@Step("Check if video is visible")
	public boolean isVideoVisible() {
		try {
			return isElementVisible(video);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 📄 يتحقق من عرض الفقرة النصية داخل المودال
	 * Checks if the paragraph text is shown in the modal
	 *
	 * @return true إذا كانت الفقرة ظاهرة، false غير ذلك
	 */
	@Step("Check if paragraph is visible")
	public boolean isParagraphShown() {
		try {
			return isElementVisible(paragraph);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 📝 جلب العنوان العربي (السطر الأول فقط)
	 * Retrieves the Arabic title (first line only) from the modal
	 *
	 * @return النص العربي للعنوان | Arabic title text
	 */
	@Step("Get Arabic title from modal")
	public String getArTitle() {
		try {
			return waitForElement(titleAr).getText().trim().split("\\R")[0].trim();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 📝 جلب العنوان الفرعي الإنجليزي
	 * Retrieves the English subtitle from the modal
	 *
	 * @return العنوان الإنجليزي | English subtitle
	 */
	@Step("Get English subtitle from modal")
	public String getEnTitle() {
		try {
			return waitForElement(titleEn).getText().trim();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 🧾 جلب الفقرة النصية داخل المودال
	 * Retrieves the description paragraph from the modal
	 *
	 * @return نص الفقرة | Paragraph text
	 */
	@Step("Get paragraph description from modal")
	public String getParagraphText() {
		try {
			return waitForElement(paragraph).getText().trim();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 🖼️ جلب رابط صورة الأداة
	 * Retrieves the source URL of the tool icon image
	 *
	 * @return الرابط الكامل للصورة | Image source URL
	 */
	@Step("Get tool icon image src")
	public String getIconSrc() {
		try {
			return waitForElement(iconImg).getAttribute("src");
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 🎬 جلب رابط ملف الفيديو من الوسوم الداخلية
	 * Retrieves the source URL of the video from the <source> tag inside the <video>
	 *
	 * @return الرابط الكامل للفيديو | Video source URL
	 */
	@Step("Get video source URL from modal")
	public String getVideoSrc() {
		try {
			WebElement source = waitForElement(video).findElement(By.tagName("source"));
			return source.getAttribute("src");
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 
	 * 
	 */
	@Step("Get Video Locator")
	public WebElement getVideoElement() {
		return waitForElement(video);
	}
	
	
	/**
	 * 
	 * 
	 */
	@Step("Get dialogContent Locator")
	public WebElement getDialogContentElement() {
		return waitForElement(dialogContent);
	}
	
	
	
	
	
	/**
	 * 📜 يتحقق من وجود شريط تمرير عمودي داخل نافذة التفاصيل
	 * Checks if the modal content is scrollable vertically
	 *
	 * @return true إذا كان المحتوى أطول من الإطار ويحتوي على Scroll عمودي
	 */
	@Step("Check if modal content is vertically scrollable")
	public boolean isVerticallyScrollable() {
	    try {
	        //	🔍 تحديد العنصر الرئيسي الذي يحتوي على محتوى النافذة (الوصف، الفيديو، النصوص...)
	        // هذا العنصر هو الذي يتغير طوله بحسب كمية المحتوى، وقد يحتوي على شريط تمرير
	        WebElement content = waitForElement(dialogContent);

	        //	⚙️ نستخدم JavaScript لتنفيذ أوامر مباشرة على العنصر داخل المتصفح
	        JavascriptExecutor js = (JavascriptExecutor) driver;

	        //	🧮 scrollHeight: الطول الكامل للمحتوى داخل العنصر (يشمل ما لا يُرى إلا بالتمرير)
	        Long scrollHeight = (Long) js.executeScript("return arguments[0].scrollHeight", content);

	        //	🧮 clientHeight: الطول الظاهر من العنصر دون تمرير (فقط ما يُعرض داخل الإطار)
	        Long clientHeight = (Long) js.executeScript("return arguments[0].clientHeight", content);

	        //	✅ إذا كان الطول الكامل أكبر من الطول الظاهر، فهناك محتوى مخفي ويعني وجود Scroll
	        boolean scrollable = scrollHeight > clientHeight;

	        //	🧾 تسجيل القيم في تقرير Allure لمساعدة المطورين في التحليل
	        Allure.step("ScrollHeight: " + scrollHeight + ", ClientHeight: " + clientHeight);

	        //	📤 نُرجع true إذا كان هناك تمرير، أو false إذا لا يوجد تمرير
	        return scrollable;

	    } catch (Exception e) {
	        // ⚠️ في حال فشلنا في إيجاد العنصر أو حدث خطأ غير متوقع، نُسجل ذلك في التقرير
	        Allure.step("❌ تعذر التحقق من قابلية التمرير العمودي");

	        // 🔁 نُرجع false بشكل آمن حتى لا يتوقف الاختبار تمامًا
	        return false;
	    }
	}


	/**
	 * ❌ يغلق نافذة التفاصيل وينتظر اختفاءها
	 * Closes the tool details modal and waits for both dialog and mask to disappear
	 *
	 * @throws RuntimeException في حال فشل الإغلاق
	 */
	@Step("Close the modal and wait for it to disappear")
	public void close() {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(closeBtn)).click();
			 System.out.println("click on closeBtn");
			wait.until(ExpectedConditions.invisibilityOfElementLocated(dialogRoot));
			System.out.println("closed dialogRoot");
			wait.until(ExpectedConditions.invisibilityOfElementLocated(dialogMask));
			System.out.println("closed dialogMask");
		} catch (TimeoutException e) {
			throw new RuntimeException("❌ فشل إغلاق نافذة التفاصيل");
		}
	}


}
