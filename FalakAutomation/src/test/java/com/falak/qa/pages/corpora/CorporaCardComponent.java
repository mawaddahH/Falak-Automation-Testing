package com.falak.qa.pages.corpora;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.qameta.allure.Step;

public class CorporaCardComponent {

	private final WebElement root; // 🧱 العنصر الجذري للبطاقة | Root element of the card
	private final WebDriverWait wait; // ⏳ انتظار مخصص للعناصر داخل البطاقة | Dedicated wait for this card

	// ==================== Locators ====================
	/* 🏷️ العنوان الرئيسي للبطاقة */
	private final By title = By.xpath(".//span[contains(@class,'title')]");

	/* 📝 وصف البطاقة (الفقرة الأولى داخل <p class=\"description\">) */
	private final By description = By.xpath(".//p[contains(@class,'description')]//span");

	/* ───── إحصاءات عدد النصوص ───── */
	private final By textsIcon = By.xpath(".//img[starts-with(@src,'assets/icon-documents')]");
	private final By textsLabel = By.xpath(
			".//img[starts-with(@src,'assets/icon-documents')]/following-sibling::p[contains(@class,'text-xs')]");
	private final By textsValue = By.xpath(
			".//img[starts-with(@src,'assets/icon-documents')]/following-sibling::p[contains(@class,'text-700')]");

	/* ───── إحصاءات عدد الكلمات ───── */
	private final By wordsIcon = By
			.xpath(".//img[starts-with(@src,'assets/icon-words') and not(contains(@src,'nofreq'))]");
	private final By wordsLabel = By.xpath(
			".//img[starts-with(@src,'assets/icon-words') and not(contains(@src,'nofreq'))]/following-sibling::p[contains(@class,'text-xs')]");
	private final By wordsValue = By.xpath(
			".//img[starts-with(@src,'assets/icon-words') and not(contains(@src,'nofreq'))]/following-sibling::p[contains(@class,'text-700')]");

	/* ───── الكلمات بدون تكرار ───── */
	private final By uniqueIcon = By.xpath(".//img[contains(@src,'icon-words-nofreq')]");
	private final By uniqueLabel = By
			.xpath(".//img[contains(@src,'icon-words-nofreq')]/following-sibling::p[contains(@class,'text-xs')]");
	private final By uniqueValue = By
			.xpath(".//img[contains(@src,'icon-words-nofreq')]/following-sibling::p[contains(@class,'text-700')]");

	/*	🔘 زر «اختر المدوّنة» */
	private final By selectButton = By
			.xpath(".//a[contains(@class,'p-button')][.//span[normalize-space()='اختر المدونة']]");
	/*	🔘 زر «المزيد» */
	private final By moreLink = By.xpath(".//a[normalize-space()='المزيد']");

	// ==================== Constructor ====================

	/**
	 * 🔧 المُنشئ الأساسي للكلاس Main constructor
	 *
	 * @param root   العنصر الجذري للبطاقة | Root element of the card
	 * @param driver WebDriver المستخدم للانتظار | The WebDriver used to initialize
	 *               wait
	 */
	public CorporaCardComponent(WebElement root, WebDriver driver) {
		this.root = root;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	// ==================== Core Element Access ====================

	/**
	 * 🌱 يُرجع العنصر الجذري (Root) لبطاقة المدونة. يُستخدم هذا الميثود عندما نريد
	 * التفاعل مع البطاقة بأكملها.
	 *
	 * 🌱 Returns the root WebElement of the corpora card. Useful for CSS-based
	 * verifications or hover tests.
	 *
	 * @return العنصر الجذري للبطاقة | The root WebElement of the card
	 */
	@Step("Get Corpora card root element")
	public WebElement getRootElement() {
		return this.root;
	}

	/**
	 * 🔠 يُرجع عنوان بطاقة المدونة كنص. العنوان يُعرض عادةً في أعلى البطاقة.
	 *
	 * 🔠 Retrieves the title text displayed at the top of the corpora card.
	 *
	 * @return نص عنوان البطاقة | The title of the card
	 */
	@Step("Get Corpora card title")
	public String getTitle() {
		return getText(title);
	}

	/**
	 * 📝 يُرجع الوصف الموجود في البطاقة. يُستخدم عادةً لتأكيد أن الوصف ظاهر وصحيح.
	 *
	 * 📝 Returns the description text within the card.
	 *
	 * @return نص الوصف | The description of the card
	 */
	@Step("Get Corpora card description")
	public String getDescription() {
		return getText(description);
	}

	/**
	 * ⬇️ ينقر على زر "اختر المدونة" الموجود في البطاقة. يُستخدم غالبًا للانتقال إلى
	 * تفاصيل المدونة.
	 *
	 * ⬇️ Clicks the "Select Corpora" button inside the card.
	 */
	@Step("Click on ‘Select Corpora’ button")
	public void clickSelectButton() {
		clickInside(selectButton);
	}

	/**
	 * ⬇️ ينقر على رابط "المزيد" الموجود في الوصف. يظهر هذا الرابط غالبًا عند وجود
	 * وصف طويل يتم اختصاره.
	 *
	 * ⬇️ Clicks on the "More" link shown in the card description.
	 */
	@Step("Click on ‘More’ link in card description")
	public void clickMoreLink() {
		clickInside(moreLink);
	}

	/**
	 * 🌐 يُرجع رابط زر "اختر المدونة" (قيمة href). يُستخدم للتحقق من الوجهة التي
	 * ينقلك إليها هذا الزر.
	 *
	 * 🌐 Returns the hyperlink (href) of the "Select Corpora" button.
	 *
	 * @return قيمة href | The href value of the button
	 */
	@Step("Get ‘Select Corpora’ button href")
	public String getSelectButtonHref() {
		return getAttribute(selectButton, "href");
	}

	/**
	 * 🌐 يُرجع رابط "المزيد" الموجود في الوصف (قيمة href). إذا لم يظهر العنصر،
	 * يُرجع سلسلة فارغة.
	 *
	 * 🌐 Returns the href value of the "More" link.
	 *
	 * @return قيمة الرابط | The href of the link, or empty if not found
	 */
	@Step("Get ‘More’ link href")
	public String getMoreLinkHref() {
		try {
			return wait.until(d -> root.findElement(moreLink)).getAttribute("href");
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 🔠 يُرجع نص رابط "المزيد" الموجود في البطاقة. يُستخدم للتأكد أن النص الظاهر
	 * هو "المزيد" أو ما يشابهه.
	 *
	 * 🔠 Returns the visible text of the "More" link in the card.
	 *
	 * @return النص الظاهر | The visible link text
	 */
	@Step("Get ‘More’ link text")
	public String getMoreLinkText() {
		return getText(moreLink);
	}

	// ==================== Status Checks ====================

	/**
	 * ✅ التحقق من ظهور عنوان البطاقة. يُستخدم هذا الميثود ضمن اختبارات التحقق
	 * البصري أو المحتوى.
	 *
	 * ✅ Checks whether the card's title is visible.
	 *
	 * @return true إذا كان العنوان ظاهرًا | true if the title is visible
	 */
	@Step("Check if Corpora card title is visible")
	public boolean isTitleVisible() {
		return isDisplayed(title);
	}

	/**
	 * ✅ التحقق من ظهور وصف البطاقة. مفيد في اختبارات المحتوى للتأكد من عرض الوصف.
	 *
	 * ✅ Checks if the description text is visible within the card.
	 *
	 * @return true إذا كان الوصف ظاهرًا | true if the description is visible
	 */
	@Step("Check if Corpora card description is visible")
	public boolean isDescriptionVisible() {
		return isDisplayed(description);
	}

	/**
	 * ✅ التحقق من ظهور زر "اختر المدونة". مفيد لاختبارات التفاعل والروابط.
	 *
	 * ✅ Checks if the “Select Corpora” button is visible in the card.
	 *
	 * @return true إذا كان الزر ظاهرًا | true if the button is visible
	 */
	@Step("Check if ‘Select Corpora’ button is visible")
	public boolean isSelectButtonVisible() {
		return isDisplayed(selectButton);
	}

	/**
	 * ✅ التحقق من ظهور رابط "المزيد". يظهر الرابط عند وجود وصف طويل يتم اختصاره.
	 *
	 * ✅ Checks if the “More” link is visible in the card description.
	 *
	 * @return true إذا كان الرابط ظاهرًا | true if the link is visible
	 */
	@Step("Check if ‘More’ link is visible")
	public boolean isMoreLinkVisible() {
		return isDisplayed(moreLink);
	}

	// ========== Texts Section ==========

	/**
	 * 📄 هل أيقونة "عدد النصوص" مرئية؟ تُستخدم للتحقق من مظهر البطاقة.
	 *
	 * 📄 Checks if the icon for "Texts Count" is visible.
	 */
	@Step("Check if Texts icon is visible")
	public boolean isTextsIconVisible() {
		return isDisplayed(textsIcon);
	}

	/**
	 * 🏷️ هل عنوان "عدد النصوص" ظاهر؟
	 *
	 * 🏷️ Checks if the label for "Texts Count" is visible.
	 */
	@Step("Check if Texts label is visible")
	public boolean isTextsLabelVisible() {
		return isDisplayed(textsLabel);
	}

	/**
	 * 🔢 هل قيمة عدد النصوص ظاهرة؟
	 *
	 * 🔢 Checks if the value showing number of texts is visible.
	 */
	@Step("Check if Texts value is visible")
	public boolean isTextsValueVisible() {
		return isDisplayed(textsValue);
	}

	/**
	 * 🏷️ إرجاع عنوان عدد النصوص (مثل: عدد النصوص).
	 *
	 * 🏷️ Returns the label text for "Texts Count".
	 *
	 * @return نص العنوان | The label of the section
	 */
	@Step("Get Texts label text")
	public String getTextsLabelText() {
		return getText(textsLabel);
	}

	/**
	 * 🔢 إرجاع العدد الظاهر للنصوص.
	 *
	 * 🔢 Returns the numeric value of "Texts Count".
	 *
	 * @return العدد كنص | The text value of the count
	 */
	@Step("Get Texts count value")
	public String getTextsValueText() {
		return getText(textsValue);
	}

	/**
	 * 🖼️ إرجاع رابط أيقونة عدد النصوص.
	 *
	 * 🖼️ Returns the source (src) of the icon for "Texts Count".
	 *
	 * @return رابط الصورة | Image source URL
	 */
	@Step("Get Texts icon image src")
	public String getTextsIconSrc() {
		return getAttribute(textsIcon, "src");
	}

	// ========== Words Section ==========

	/**
	 * 🖼️ التحقق من ظهور أيقونة عدد الكلمات.
	 *
	 * 🖼️ Checks if the icon for "Words Count" is visible.
	 */
	@Step("Check if Words icon is visible")
	public boolean isWordsIconVisible() {
		return isDisplayed(wordsIcon);
	}

	/**
	 * 🏷️ التحقق من ظهور عنوان "عدد الكلمات".
	 *
	 * 🏷️ Checks if the label for "Words Count" is visible.
	 */
	@Step("Check if Words label is visible")
	public boolean isWordsLabelVisible() {
		return isDisplayed(wordsLabel);
	}

	/**
	 * 🔢 التحقق من ظهور العدد الظاهر للكلمات.
	 *
	 * 🔢 Checks if the value for number of words is visible.
	 */
	@Step("Check if Words value is visible")
	public boolean isWordsValueVisible() {
		return isDisplayed(wordsValue);
	}

	/**
	 * 🏷️ إرجاع عنوان قسم عدد الكلمات.
	 *
	 * 🏷️ Returns the label text for the "Words Count" section.
	 */
	@Step("Get Words label text")
	public String getWordsLabelText() {
		return getText(wordsLabel);
	}

	/**
	 * 🔢 إرجاع قيمة عدد الكلمات.
	 *
	 * 🔢 Returns the displayed count of words.
	 */
	@Step("Get Words count value")
	public String getWordsValueText() {
		return getText(wordsValue);
	}

	/**
	 * 🖼️ إرجاع رابط أيقونة عدد الكلمات.
	 *
	 * 🖼️ Returns the image source for the words count icon.
	 */
	@Step("Get Words icon image src")
	public String getWordsIconSrc() {
		return getAttribute(wordsIcon, "src");
	}

	// ========== Unique Words Section ==========

	/**
	 * 🖼️ التحقق من ظهور أيقونة "الكلمات بدون تكرار".
	 *
	 * 🖼️ Checks if the icon for "Unique Words" is visible.
	 *
	 * @return true إذا كانت الأيقونة ظاهرة | true if the icon is visible
	 */
	@Step("Check if Unique Words icon is visible")
	public boolean isUniqueIconVisible() {
		return isDisplayed(uniqueIcon);
	}

	/**
	 * 🏷️ التحقق من ظهور عنوان قسم "الكلمات بدون تكرار".
	 *
	 * 🏷️ Checks if the label for "Unique Words" is visible.
	 *
	 * @return true إذا كان العنوان ظاهرًا | true if the label is visible
	 */
	@Step("Check if Unique Words label is visible")
	public boolean isUniqueLabelVisible() {
		return isDisplayed(uniqueLabel);
	}

	/**
	 * 🔢 التحقق من ظهور قيمة عدد الكلمات غير المكررة.
	 *
	 * 🔢 Checks if the value for "Unique Words" is visible.
	 *
	 * @return true إذا كانت القيمة ظاهرة | true if the value is visible
	 */
	@Step("Check if Unique Words value is visible")
	public boolean isUniqueValueVisible() {
		return isDisplayed(uniqueValue);
	}

	/**
	 * 🏷️ إرجاع نص عنوان "الكلمات بدون تكرار".
	 *
	 * 🏷️ Returns the label of the "Unique Words" section.
	 *
	 * @return نص العنوان | The label text
	 */
	@Step("Get Unique Words label text")
	public String getUniqueLabelText() {
		return getText(uniqueLabel);
	}

	/**
	 * 🔢 إرجاع قيمة عدد الكلمات غير المكررة.
	 *
	 * 🔢 Returns the numeric value of "Unique Words".
	 *
	 * @return العدد كنص | The text value of the count
	 */
	@Step("Get Unique Words value text")
	public String getUniqueValueText() {
		return getText(uniqueValue);
	}

	/**
	 * 🖼️ إرجاع رابط أيقونة "الكلمات بدون تكرار".
	 *
	 * 🖼️ Returns the image source for the "Unique Words" icon.
	 *
	 * @return رابط الصورة | Image source URL
	 */
	@Step("Get Unique Words icon image src")
	public String getUniqueIconSrc() {
		return getAttribute(uniqueIcon, "src");
	}

	// ==================== Validation ====================

	/**
	 * 📦 يُرجع العنصر الجذري للبطاقة. مفيد في اختبارات الـ CSS مثل hover أو قياسات
	 * الأبعاد.
	 *
	 * 📦 Returns the root element of the corpora card. Useful for hover or
	 * CSS-based tests.
	 *
	 * @return العنصر الجذري | The root WebElement
	 */
	@Step("Get root element of corpora card")
	public WebElement getCardRoot() {
		return this.root;
	}

	/**
	 * 🌫️ يحصل على قيمة خاصية "box-shadow" للبطاقة. يُستخدم غالبًا لاختبارات
	 * التحويم (Hover) أو تأثيرات التصميم.
	 *
	 * 🌫️ Retrieves the CSS value of the box-shadow property.
	 *
	 * @return القيمة الحالية لـ box-shadow | The box-shadow CSS value
	 */
	@Step("Get box-shadow value of card")
	public String getBoxShadow() {
		return root.getCssValue("box-shadow");
	}

	/**
	 * 🧾 يحصل على النص الموجود داخل عنصر معين في البطاقة.
	 *
	 * 🧾 Retrieves the visible text of a specific element inside the card.
	 *
	 * @param locator محدد العنصر | The locator of the target element
	 * @return النص الموجود أو سلسلة فارغة إذا لم يُعثر عليه | The trimmed text or
	 *         empty if not found
	 */
	@Step("Get text from element inside card")
	public String getText(By locator) {
		try {
			return wait.until(driver -> root.findElement(locator)).getText().trim();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 🌐 يحصل على قيمة خاصية (Attribute) لعنصر داخل البطاقة. مثل: href, src, title
	 * وغيرها.
	 *
	 * 🌐 Retrieves the value of a specific attribute from an element inside the
	 * card.
	 *
	 * @param locator   محدد العنصر | The locator of the element
	 * @param attribute اسم الخاصية المطلوبة | The attribute name (e.g., "href")
	 * @return قيمة الخاصية أو سلسلة فارغة | The attribute value or empty if not
	 *         found
	 */
	@Step("Get attribute from element inside card")
	public String getAttribute(By locator, String attribute) {
		try {
			return wait.until(driver -> root.findElement(locator)).getAttribute(attribute);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * ✅ التحقّق من اكتمال البيانات داخل البطاقة. يُستخدم هذا الميثود لضمان أن
	 * البطاقة تعرض جميع العناصر المطلوبة وتحتوي على بيانات غير فارغة.
	 *
	 * ✅ Validates that all required elements in the corpora card are visible and
	 * not blank. Useful for sanity and smoke testing of homepage components.
	 *
	 * @return true إذا كانت البطاقة مكتملة | true if the card is valid and complete
	 */
	@Step("Validate Corpora card completeness")
	public boolean isValidCard() {
		try {
			return isTitleVisible() && isDescriptionVisible() && isMoreLinkVisible() && isTextsIconVisible()
					&& isTextsLabelVisible() && !getTextsValueText().isBlank() && isWordsIconVisible()
					&& isWordsLabelVisible() && !getWordsValueText().isBlank() && isUniqueIconVisible()
					&& isUniqueLabelVisible() && !getUniqueValueText().isBlank() && isSelectButtonVisible();
		} catch (Exception e) {
			throw new RuntimeException("❌ فشل في التحقق من صحة بطاقة المدونة", e);
		}
	}

	// ==================== Helpers ====================

	/**
	 * 🔍 يتحقق من أن العنصر ظاهر داخل البطاقة. يُستخدم في اختبارات التحقق البصري
	 * وعند فحص العناصر الحساسة.
	 *
	 * 🔍 Checks if a specific element inside the card is visible. Useful for visual
	 * verification steps.
	 *
	 * @param locator محدد العنصر داخل البطاقة | The locator of the element inside
	 *                the card
	 * @return true إذا كان العنصر ظاهرًا، false إذا لم يظهر | true if visible,
	 *         false otherwise
	 */
	@Step("Check if element is visible inside card")
	public boolean isDisplayed(By locator) {
		try {
			return wait.until(driver -> root.findElement(locator).isDisplayed());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 🖱️ ينقر على عنصر داخل البطاقة (مثل زر أو رابط). يستخدم داخل ميثودات النقر
	 * الخاصة بالبطاقة.
	 *
	 * 🖱️ Clicks a target element inside the card. Used by click methods like
	 * Select or More.
	 *
	 * @param locator محدد العنصر | The locator of the clickable element
	 */
	@Step("Click element inside card")
	public void clickInside(By locator) {
		try {
			WebElement target = wait.until(ExpectedConditions.elementToBeClickable(root.findElement(locator)));
			target.click();
		} catch (TimeoutException te) {
			throw new RuntimeException("❌ فشل النقر على العنصر داخل البطاقة: " + locator);
		}
	}
}
