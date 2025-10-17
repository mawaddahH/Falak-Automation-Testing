package com.falak.qa.pages.home;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.qameta.allure.Step;
import java.time.Duration;

/**
 * ✅ هذا الكلاس يمثل بطاقة «مدونة الشهر» الظاهرة مرة واحدة في الصفحة الرئيسية.
 * This class models the single “corpora-of-the-Month” card on the Home page.
 */
public class CorporaOfMonthCardComponent {

	private final WebElement root; // 🧱 العنصر الجذري للبطاقة
	private final WebDriverWait wait; // ⏳ انتظار مخصص لهذه البطاقة

	// 🏷️ محدد العنصر الجذري | Locator for the section root
	private final By corporaOfMonthRoot = By.xpath(
			"//section[contains(@class,'section-the-month-corpus') and .//h2[normalize-space()='مدونـة الشـهر']]");

	// ❶ العنوان والوصف | Title & Description
	private final By title = By
			.xpath("//section[contains(@class,'section-the-month-corpus')]//p[contains(@class,'text-xl')]");
	private final By description = By
			.xpath("//section[contains(@class,'section-the-month-corpus')]//p[contains(@class,'text-md')]//span");

	// ❷ عدادات الإحصاءات | Stats section
	private final By textsIcon = By
			.xpath("//section[contains(@class,'section-the-month-corpus')]//img[contains(@src,'icon-documents')]");
	private final By textsLabel = By.xpath(
			"//section[contains(@class,'section-the-month-corpus')]//img[contains(@src,'icon-documents')]/following-sibling::p");
	private final By textsValue = By.xpath(
			"//section[contains(@class,'section-the-month-corpus')]//img[contains(@src,'icon-documents')]/following-sibling::p[2]");

	private final By wordsIcon = By
			.xpath("//section[contains(@class,'section-the-month-corpus')]//img[contains(@src,'icon-words.svg')]");
	private final By wordsLabel = By.xpath(
			"//section[contains(@class,'section-the-month-corpus')]//img[contains(@src,'icon-words.svg')]/following-sibling::p");
	private final By wordsValue = By.xpath(
			"//section[contains(@class,'section-the-month-corpus')]//img[contains(@src,'icon-words.svg')]/following-sibling::p[2]");

	private final By uniqueIcon = By
			.xpath("//section[contains(@class,'section-the-month-corpus')]//img[contains(@src,'icon-words-nofreq')]");
	private final By uniqueLabel = By.xpath(
			"//section[contains(@class,'section-the-month-corpus')]//img[contains(@src,'icon-words-nofreq')]/following-sibling::p");
	private final By uniqueValue = By.xpath(
			"//section[contains(@class,'section-the-month-corpus')]//img[contains(@src,'icon-words-nofreq')]/following-sibling::p[2]");

	// ❸ زر «عرض» | View button
	private final By viewButton = By.xpath(
			"//section[contains(@class,'section-the-month-corpus')]//a[contains(@class,'p-button')][span[normalize-space()='عرض']]");

	/**
	 * 🔧 المُنشئ | Constructor
	 * 
	 * @param root   العنصر الجذري للبطاقة | The root WebElement of the card
	 * @param driver كائن WebDriver المستخدم في التفاعل | The WebDriver instance
	 */
	public CorporaOfMonthCardComponent(WebElement root, WebDriver driver) {
		this.root = root;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	/** 🌿 إرجاع العنصر الجذري للبطاقة | Returns the card’s root element */
	@Step("Get card root element")
	public WebElement getRootElement() {
		return this.root;
	}

	// ─────── Getters / Visibility Checks ───────

	/**
	 * ✅ التحقق من ظهور عنوان البطاقة Checks if the title of the card is visible.
	 *
	 * @return true إذا كان العنوان ظاهرًا | true if visible
	 */
	@Step("Check if title is visible")
	public boolean isTitleVisible() {
		return isDisplayed(title);
	}

	/**
	 * ✅ جلب نص العنوان الظاهر في البطاقة Returns the title text of the card.
	 *
	 * @return نص العنوان | The title text
	 */
	@Step("Get card title text")
	public String getTitle() {
		return getText(title);
	}

	/**
	 * ✅ التحقق من ظهور وصف البطاقة Checks if the description is visible on the
	 * card.
	 *
	 * @return true إذا كان الوصف ظاهرًا | true if visible
	 */
	@Step("Check if description is visible")
	public boolean isDescriptionVisible() {
		return isDisplayed(description);
	}

	/**
	 * ✅ جلب نص الوصف الظاهر Returns the description text of the card.
	 *
	 * @return نص الوصف | The description text
	 */
	@Step("Get card description text")
	public String getDescription() {
		return getText(description);
	}

	/**
	 * ✅ التحقق من ظهور أيقونة عدد النصوص Checks if the texts icon is visible.
	 *
	 * @return true إذا كانت الأيقونة ظاهرة | true if visible
	 */
	@Step("Check if texts icon is visible")
	public boolean isTextsIconVisible() {
		return isDisplayed(textsIcon);
	}

	/**
	 * ✅ التحقق من ظهور تسمية عدد النصوص Checks if the label for the number of texts
	 * is visible.
	 *
	 * @return true إذا كانت التسمية ظاهرة | true if visible
	 */
	@Step("Check if texts label is visible")
	public boolean isTextsLabelVisible() {
		return isDisplayed(textsLabel);
	}

	/**
	 * ✅ التحقق من ظهور قيمة عدد النصوص Checks if the value of texts count is
	 * visible.
	 *
	 * @return true إذا كانت القيمة ظاهرة | true if visible
	 */
	@Step("Check if texts value is visible")
	public boolean isTextsValueVisible() {
		return isDisplayed(textsValue);
	}

	/**
	 * ✅ جلب عدد النصوص المعروض Returns the value of the texts count.
	 *
	 * @return عدد النصوص كنص | Texts count as string
	 */
	@Step("Get value of texts count")
	public String getTextsValueText() {
		return getText(textsValue);
	}

	/**
	 * ✅ التحقق من ظهور أيقونة عدد الكلمات Checks if the words icon is visible.
	 *
	 * @return true إذا كانت الأيقونة ظاهرة | true if visible
	 */
	@Step("Check if words icon is visible")
	public boolean isWordsIconVisible() {
		return isDisplayed(wordsIcon);
	}

	/**
	 * ✅ التحقق من ظهور تسمية عدد الكلمات Checks if the words label is visible.
	 *
	 * @return true إذا كانت التسمية ظاهرة | true if visible
	 */
	@Step("Check if words label is visible")
	public boolean isWordsLabelVisible() {
		return isDisplayed(wordsLabel);
	}

	/**
	 * ✅ التحقق من ظهور قيمة عدد الكلمات Checks if the words value is visible.
	 *
	 * @return true إذا كانت القيمة ظاهرة | true if visible
	 */
	@Step("Check if words value is visible")
	public boolean isWordsValueVisible() {
		return isDisplayed(wordsValue);
	}

	/**
	 * ✅ جلب عدد الكلمات المعروضة Returns the value of the words count.
	 *
	 * @return عدد الكلمات كنص | Words count as string
	 */
	@Step("Get value of words count")
	public String getWordsValueText() {
		return getText(wordsValue);
	}

	/**
	 * ✅ التحقق من ظهور أيقونة الكلمات غير المكررة Checks if the unique words icon
	 * is visible.
	 *
	 * @return true إذا كانت الأيقونة ظاهرة | true if visible
	 */
	@Step("Check if unique icon is visible")
	public boolean isUniqueIconVisible() {
		return isDisplayed(uniqueIcon);
	}

	/**
	 * ✅ التحقق من ظهور تسمية الكلمات الفريدة Checks if the label of the unique
	 * words is visible.
	 *
	 * @return true إذا كانت التسمية ظاهرة | true if visible
	 */
	@Step("Check if unique label is visible")
	public boolean isUniqueLabelVisible() {
		return isDisplayed(uniqueLabel);
	}

	/**
	 * ✅ التحقق من ظهور قيمة الكلمات الفريدة Checks if the value of the unique words
	 * is visible.
	 *
	 * @return true إذا كانت القيمة ظاهرة | true if visible
	 */
	@Step("Check if unique value is visible")
	public boolean isUniqueValueVisible() {
		return isDisplayed(uniqueValue);
	}

	/**
	 * ✅ جلب عدد الكلمات غير المكررة Returns the value of the unique words count.
	 *
	 * @return عدد الكلمات غير المكررة كنص | Unique words count as string
	 */
	@Step("Get value of unique words")
	public String getUniqueValueText() {
		return getText(uniqueValue);
	}

	/**
	 * ✅ التحقق من ظهور زر "عرض" Checks if the 'عرض' (View) button is visible.
	 *
	 * @return true إذا كان الزر ظاهرًا | true if visible
	 */
	@Step("Check if 'عرض' button is visible")
	public boolean isViewButtonVisible() {
		return isDisplayed(viewButton);
	}

	/**
	 * ✅ جلب رابط زر "عرض" Returns the href value of the 'عرض' button.
	 *
	 * @return رابط الزر كنص | The href link as string
	 */
	@Step("Get ‘عرض’ button href")
	public String getViewButtonHref() {
		return getAttribute(viewButton, "href");
	}

	/**
	 * ✅ ينقر على زر "عرض" Clicks the 'عرض' (View) button to navigate to details.
	 *
	 * @throws RuntimeException إذا فشل النقر | if click fails
	 */
	@Step("Click on 'عرض' button")
	public void clickViewButton() {
		try {
			clickInside(viewButton);
		} catch (Exception e) {
			throw new RuntimeException("❌ فشل النقر على زر عرض داخل بطاقة مدونة الشهر");
		}
	}

	/**
	 * ✅ التحقق الكامل من صلاحية البطاقة Validates that all required elements in the
	 * card are visible and contain data.
	 *
	 * @return true إذا كانت البطاقة مكتملة وصحيحة | true if the card is complete
	 *         and valid
	 */
	@Step("Validate completeness of Corpora-of-the-Month card")
	public boolean isValidCard() {
		return isTitleVisible() && isDescriptionVisible() && isTextsLabelVisible() && !getTextsValueText().isBlank()
				&& isWordsLabelVisible() && !getWordsValueText().isBlank() && isUniqueLabelVisible()
				&& !getUniqueValueText().isBlank() && isViewButtonVisible();
	}

	// ───────────── Helpers ─────────────

	/**
	 * 🔍 يتحقق من ظهور عنصر معين داخل البطاقة Checks if a given element is visible
	 * inside the card.
	 *
	 * @param loc المحدد المطلوب التحقق منه | The locator of the element
	 * @return true إذا كان العنصر ظاهرًا، false غير ذلك | true if element is
	 *         visible, false otherwise
	 */
	private boolean isDisplayed(By loc) {
		try {
			return wait.until(d -> root.findElement(loc).isDisplayed());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 🧾 يحصل على نص من عنصر داخل البطاقة Retrieves trimmed text content from an
	 * element inside the card.
	 *
	 * @param loc محدد العنصر | Element locator
	 * @return نص العنصر أو سلسلة فارغة إذا فشل | Text content or empty string on
	 *         failure
	 */
	private String getText(By loc) {
		try {
			return wait.until(d -> root.findElement(loc)).getText().trim();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 🌐 يحصل على قيمة خاصية Attribute من عنصر داخل البطاقة Gets a specific
	 * attribute value from an element inside the card.
	 *
	 * @param loc  محدد العنصر | Element locator
	 * @param attr اسم الخاصية المطلوبة | Attribute name
	 * @return قيمة الخاصية أو سلسلة فارغة إذا فشل | Attribute value or empty string
	 *         on failure
	 */
	private String getAttribute(By loc, String attr) {
		try {
			return wait.until(d -> root.findElement(loc)).getAttribute(attr);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 🖱️ ينقر على عنصر داخل البطاقة بعد التأكد من قابليته للنقر Clicks an element
	 * inside the card after confirming it's clickable.
	 *
	 * @param loc محدد العنصر | Element locator
	 * @throws RuntimeException إذا فشل النقر | if clicking fails due to timeout
	 */
	private void clickInside(By loc) {
		try {
			WebElement el = wait.until(ExpectedConditions.elementToBeClickable(root.findElement(loc)));
			el.click();
		} catch (TimeoutException ex) {
			throw new RuntimeException("❌ فشل النقر على العنصر داخل بطاقة «مدونة الشهر»: " + loc);
		}
	}

	/**
	 * 🌱 يُرجع العنصر الجذري للبطاقة (مفيد لاختبارات CSS أو Hover) Returns the root
	 * WebElement of the card (useful for CSS or hover tests).
	 *
	 * @return العنصر الجذري للبطاقة | Root WebElement of the card
	 */
	public WebElement getCardRoot() {
		return this.root;
	}

	/**
	 * 🌫️ يُرجع قيمة خاصية box-shadow الخاصة بالبطاقة Retrieves the current value
	 * of the card's box-shadow CSS property.
	 *
	 * @return قيمة box-shadow | The current box-shadow value
	 */
	public String getBoxShadow() {
		return root.getCssValue("box-shadow");
	}

}
