package com.falak.qa.pages.home;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;

import java.time.Duration;

/**
 * 🧩 يمثل بطاقة أداة واحدة داخل قسم «أدوات المنصة» This class represents a
 * single tool card inside the tools section.
 */
public class ToolsCardComponent {

	private final WebElement root; // 🧱 العنصر الجذري للبطاقة | Root element of the card
	private final WebDriverWait wait; // ⏳ الانتظار المخصص للبطاقة | Explicit wait for this card
	private final WebDriver driver; // 🧭 المتصفح المرتبط بالبطاقة | WebDriver instance

	/*
	 * ─────────────────────── عناصر داخل البطاقة | Elements inside the card
	 * ───────────────────────
	 */
	private final By icon = By.xpath(".//img[starts-with(@src,'assets/tool-')]"); // 🖼️ أيقونة الأداة
	private final By titleAr = By.xpath(".//p[contains(@class,'label')]"); // 🅰️ العنوان العربي
	private final By titleEn = By.xpath(".//p[contains(@class,'text-500')]"); // 🇬🇧 العنوان الإنجليزي
	private final By detailsBtn = By.xpath(".//button[span[normalize-space()='التفاصيل']]"); // 📎 زر التفاصيل

	/**
	 * 🔧 المُنشئ الذي يربط العنصر الجذري مع WebDriver Constructs the
	 * ToolsCardComponent and initializes wait.
	 *
	 * @param root   العنصر الجذري للبطاقة | Root WebElement of the card
	 * @param driver المتصفح الحالي | WebDriver instance
	 */
	public ToolsCardComponent(WebElement root, WebDriver driver) {
		this.root = root;
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	// ─────── Getters / Visibility Checks ───────

	/**
	 * 👁️ يتحقق من ظهور أيقونة الأداة داخل البطاقة. Checks if the tool icon is
	 * visible inside the card.
	 *
	 * @return true إذا كانت الأيقونة مرئية، false إذا لم تكن كذلك
	 */
	@Step("Check if tool icon is visible")
	public boolean isIconVisible() {
		try {
			return isDisplayed(icon);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 📌 يتحقق من ظهور العنوان العربي. Checks if the Arabic title is visible.
	 *
	 * @return true إذا كان العنوان مرئي، false إذا لم يكن كذلك
	 */
	@Step("Check if Arabic title is visible")
	public boolean isArTitleVisible() {
		try {
			return isDisplayed(titleAr);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 🌐 يتحقق من ظهور العنوان الإنجليزي. Checks if the English title is visible.
	 *
	 * @return true إذا كان مرئيًا، false غير ذلك
	 */
	@Step("Check if English title is visible")
	public boolean isEnTitleVisible() {
		try {
			return isDisplayed(titleEn);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 📝 يتحقق من ظهور زر التفاصيل. Checks if the 'تفاصيل' button is visible.
	 *
	 * @return true إذا كان الزر مرئيًا، false غير ذلك
	 */
	@Step("Check if 'تفاصيل' button is visible")
	public boolean isDetailsVisible() {
		try {
			return isDisplayed(detailsBtn);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 🅰️ يُرجع نص العنوان العربي. Returns the Arabic title text.
	 *
	 * @return نص العنوان | Arabic title text
	 * @throws RuntimeException إذا فشل في جلب النص
	 */
	@Step("Get Arabic title text")
	public String getArTitle() {
		return getText(titleAr);
	}

	/**
	 * 🇬🇧 يُرجع نص العنوان الإنجليزي. Returns the English title text.
	 *
	 * @return نص العنوان | English title text
	 * @throws RuntimeException إذا فشل في جلب النص
	 */
	@Step("Get English title text")
	public String getEnTitle() {
		return getText(titleEn);
	}

	/**
	 * 🖼️ يُرجع رابط صورة الأداة. Returns the icon image source URL.
	 *
	 * @return رابط الصورة | Icon src attribute
	 * @throws RuntimeException إذا فشل في جلب الخاصية
	 */
	@Step("Get tool icon src")
	public String getIconSrc() {
		return getAttribute(icon, "src");
	}

	/**
	 * 📎 يُرجع النص الظاهر داخل زر "تفاصيل". Returns the text of the "تفاصيل"
	 * button.
	 *
	 * @return نص الزر | Button text
	 * @throws RuntimeException إذا فشل في جلب النص
	 */
	@Step("Get 'تفاصيل' button text")
	public String getDetailsText() {
		return getText(detailsBtn);
	}

	/**
	 * 🔘 ينقر على زر "تفاصيل" لفتح المودال. Clicks the "تفاصيل" button inside the
	 * tool card.
	 *
	 * @throws RuntimeException إذا فشل النقر
	 */
	@Step("Click on 'تفاصيل' button")
	public void clickDetailsButton() {
		clickInside(detailsBtn);
	}

	/**
	 * 🧱 يُرجع العنصر الجذري للبطاقة. Returns the root WebElement of the card.
	 *
	 * @return العنصر الجذري | Root WebElement
	 */
	@Step("Get root element of the card")
	public WebElement getRootElement() {
		return this.root;
	}

	/**
	 * ✅ يتحقق من صلاحية البطاقة ككل (جميع العناصر الأساسية موجودة). Validates that
	 * the tool card is complete and functional.
	 *
	 * @return true إذا كانت البطاقة صالحة، false إذا كانت ناقصة
	 */
	@Step("Validate tool card completeness")
	public boolean isValidCard() {
		return isIconVisible() && isArTitleVisible() && !getArTitle().isBlank() && isEnTitleVisible()
				&& !getEnTitle().isBlank() && isDetailsVisible();
	}

	/* ========== helpers ========== */

	/**
	 * 👁️ يتحقق من أن عنصرًا داخل البطاقة مرئي على الصفحة. Checks if a specific
	 * element inside the card is displayed.
	 *
	 * @param loc المحدد الخاص بالعنصر | Locator of the element
	 * @return true إذا كان العنصر مرئيًا، false إن لم يكن كذلك
	 */
	@Step("Check if element is displayed: {loc}")
	private boolean isDisplayed(By loc) {
		try {
			return wait.until(d -> root.findElement(loc).isDisplayed());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 🧾 يُرجِّع النص الخاص بعنصر داخل البطاقة. Retrieves the visible text from an
	 * element inside the card.
	 *
	 * @param loc محدد العنصر | Locator of the element
	 * @return نص العنصر، أو سلسلة فارغة في حال الفشل
	 */
	@Step("Get text from element: {loc}")
	private String getText(By loc) {
		try {
			return wait.until(d -> root.findElement(loc)).getText().trim();
		} catch (Exception e) {
			Allure.step("⚠️ فشل في جلب النص من العنصر: " + loc);
			return "";
		}
	}

	/**
	 * 🌐 يُرجع قيمة خاصية Attribute من عنصر داخل البطاقة. Returns the attribute
	 * value of a specific element inside the card.
	 *
	 * @param loc  محدد العنصر | Locator of the element
	 * @param attr اسم الخاصية | Attribute name
	 * @return قيمة الخاصية، أو سلسلة فارغة إذا فشل
	 */
	@Step("Get attribute [{attr}] from element: {loc}")
	private String getAttribute(By loc, String attr) {
		try {
			return wait.until(d -> root.findElement(loc)).getAttribute(attr);
		} catch (Exception e) {
			Allure.step("⚠️ فشل في جلب الخاصية: " + attr + " من العنصر: " + loc);
			return "";
		}
	}

	/**
	 * 🖱️ ينقر على عنصر داخل البطاقة باستخدام WebDriver. Clicks a specific element
	 * inside the card.
	 *
	 * @param loc محدد العنصر المراد النقر عليه | Locator of the element
	 * @throws RuntimeException إذا لم يكن العنصر قابلاً للنقر
	 */
	@Step("Click inside element: {loc}")
	private void clickInside(By loc) {
		try {
			WebElement el = wait.until(ExpectedConditions.elementToBeClickable(root.findElement(loc)));
			el.click();
		} catch (TimeoutException ex) {
			throw new RuntimeException("❌ فشل النقر على زر «التفاصيل» داخل بطاقة الأداة.");
		}
	}

	/**
	 * 🔘 يُرجّع كائن WebElement لزر "التفاصيل". Returns the WebElement representing
	 * the "تفاصيل" button.
	 *
	 * @return العنصر الجذري للزر | WebElement of the details button
	 */
	@Step("Get WebElement of the 'تفاصيل' button")
	public WebElement getDetailsButtonElement() {
		return root.findElement(detailsBtn);
	}

	/**
	 * 🎨 يُرجع قيمة background-color لزر "التفاصيل". Returns the current background
	 * color of the details button.
	 *
	 * @return اللون كقيمة CSS | CSS color string
	 */
	@Step("Get background color of 'تفاصيل' button")
	public String getDetailsBackground() {
		return getDetailsButtonElement().getCssValue("background-color");
	}

	/**
	 * 🪟 ينقر على زر "التفاصيل" ويفتح نافذة المودال الخاصة بالأداة. Clicks on the
	 * "تفاصيل" button and opens the ToolDetails modal.
	 *
	 * @return كائن ToolDetailsModal المفتوح بعد النقر | Instance of
	 *         ToolDetailsModal
	 * @throws RuntimeException إذا فشل النقر أو فتح المودال
	 */
	@Step("Open tool details modal")
	public ToolDetailsModal openDetailsModal() {
		try {
			// تمرير الزر إلى منتصف الشاشة قبل النقر
			((JavascriptExecutor) ((WrapsDriver) root).getWrappedDriver())
					.executeScript("arguments[0].scrollIntoView({block:'center'});", root.findElement(detailsBtn));

			// تنفيذ النقر لفتح المودال
			root.findElement(detailsBtn).click();

			// إرجاع المودال الجديد
			return new ToolDetailsModal(((WrapsDriver) root).getWrappedDriver());

		} catch (Exception e) {
			throw new RuntimeException("❌ فشل في فتح مودال تفاصيل الأداة.");
		}
	}

}
