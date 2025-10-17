package com.falak.qa.pages.corpora.tools;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.text.Collator;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.openqa.selenium.TimeoutException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.ElementClickInterceptedException;

import org.testng.Assert;

import com.falak.qa.base.BasePage;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;

/**
 * 📄 كلاس يمثل صفحة الكشاف السياقي Represents the Concordancer Tool Page in
 * Falak Platform
 */
public class ConcordancerPage extends BasePage {

	/* 🧭 اسم الأداة (العنوان الرئيسي) */
	private final By toolTitle = By
			.xpath("//div[contains(@class,'surface-50')]//span[normalize-space()='الكشاف السياقي']");

	/* ℹ️ أيقونة التفاصيل الخاصة بأداة «الكشاف السياقي» فقط */
	private final By infoIcon = By.xpath(
			"//div[contains(@class,'surface-50') and .//span[normalize-space()='الكشاف السياقي']]//img[contains(@src,'icon-info') and @ptooltip='التفاصيل']");

	/* 🪟 نص نافذة التفاصيل بعد فتحها */
	private final By infoDialogText = By.xpath("//div[contains(@class,'p-dialog-content')]//p[normalize-space()]");

	/* ❌ زر إغلاق نافذة التفاصيل */
	private final By closeDialogButton = By.xpath("//button[contains(@class,'p-dialog-header-close')]");

	/*
	 * 🎹 زر فتح لوحة المفاتيح الافتراضية – للنموذج الذي يحتوي على حقل «كلمة البحث»
	 * فقط
	 */
	private final By keyboardButton = By.xpath(
			"//input[@placeholder='كلمة البحث']/preceding::app-virtual-keyboard[1]//img[contains(@src,'icon-keyboard')]");

	private final By keySpace = By.cssSelector("button.space-key");
	private final By keyDelete = By.cssSelector("button.delete-key");

	/* ⌨️ حقل إدخال كلمة البحث (لنموذج الكشاف السياقي فقط) */
	private final By searchInput = By.xpath("//input[@placeholder='كلمة البحث' and contains(@class,'p-inputtext')]");

	/* 🔍 زر البحث */
	private final By searchButton = By.xpath("//button[@type='submit' and .//span[normalize-space()='بحث']]");

	/* 📭 رسالة «لا توجد بيانات» بعد البحث */
	private final By noDataMessage = By.xpath("//p[contains(@class,'text-sm') and normalize-space()='لا توجد بيانات']");

	/* 🎯 زرّ طي/توسيع قسم «المحددات» */
	private final By filtersSectionToggle = By
			.xpath("//a[@role='button' and .//img[contains(@src,'icon-filters.svg')]]");

	/* 📋 عناصر الخيارات داخل قائمة الـ MultiSelect */
	private final By multiSelectOptions =
			// By.cssSelector("ul.p-multiselect-items li.p-multiselect-item");
			By.xpath(
					"//div[contains(@class,'p-multiselect-panel') and not(contains(@style,'display: none'))]//li[@role='option']");

	/* ✔︎ خانة «تحديد الكل» ضمن فلتر */
	private final By FilterSelectAll = By.xpath(
			"//div[@role='checkbox' and contains(@class,'p-checkbox-box') and not(contains(@class,'p-disabled'))]");

	// الجذر العام لآخر Overlay مفتوح (PrimeNG)
	private final By openOverlayRoot = By.xpath("(//div[contains(@class,'p-overlay-content')])[last()]");

	// آخر Panel لمولتي-سيلكت ظاهر الآن
	private final By visibleMultiSelectPanel = By.xpath(
			"(//div[contains(@class,'p-overlay-content')]//div[contains(@class,'p-multiselect-panel')])[last()]");

	// حقل البحث داخل الهيدر للـ Multi-Select المفتوح
	private final By panelFilterInput = By.xpath(
			"(//div[contains(@class,'p-overlay-content')]//div[contains(@class,'p-multiselect-panel')])[last()]//input[@role='searchbox' or contains(@class,'p-multiselect-filter')]");

	// قائمة العناصر (li role='option') داخل آخر Panel ظاهر
	private final By panelItems = By.xpath(
			"(//div[contains(@class,'p-overlay-content')]//div[contains(@class,'p-multiselect-panel')])[last()]//li[@role='option' and contains(@class,'p-multiselect-item')]");

	// حاوية التمرير لعناصر القائمة
	private final By panelItemsScroller = By.xpath(
			"(//div[contains(@class,'p-overlay-content')]//div[contains(@class,'p-multiselect-panel')])[last()]//div[contains(@class,'p-multiselect-items-wrapper')]");

	// اختيار عنصر بالنص الحرفي (يراعي span النصّي و/أو aria-label على <li>)
	private static final String PANEL_OPTION_BY_TEXT = "(//div[contains(@class,'p-overlay-content')]//div[contains(@class,'p-multiselect-panel')])[last()]"
			+ "//li[@role='option' and contains(@class,'p-multiselect-item')][@aria-label=normalize-space('%s') or .//span[normalize-space()='%s']]";

	// اختيار عنصر بنمط contains (في aria-label أو النص)
	private static final String PANEL_OPTION_BY_CONTAINS = "(//div[contains(@class,'p-overlay-content')]//div[contains(@class,'p-multiselect-panel')])[last()]"
			+ "//li[@role='option' and contains(@class,'p-multiselect-item')][contains(@aria-label,'%s') or .//span[contains(normalize-space(),'%s')]]";

	/* 🔽 المحدد: اختر المجال | Domain Filter */
	private final By domainFilter = By.xpath(
			"//p-multiselect[@placeholder='-اختر المجال -' or .//div[@class='p-multiselect-label p-placeholder' and normalize-space()='-اختر المجال -']]");

	/* 🔎 حقل البحث داخل قائمة «اختر المجال» */
	private final By domainSearchInput = By.xpath(
			"//p-multiselect[starts-with(normalize-space(@placeholder),'-اختر المجال -')] //input[contains(@class,'p-multiselect-filter')]");

	private final By selectedDomainValuesDisplay = By.xpath(
			"(//p-multiselect[.//div[contains(@class,'p-multiselect-label')][normalize-space()='-اختر المجال -']    or .//input[@role='combobox' and @value='-اختر المجال -']]//input[@role='combobox'])[1]");

	/* 🔽 المحدد: اختر الموضوع | Topic Filter */
	private final By topicFilter = By.xpath(
			"//div[contains(@class,'p-multiselect')][.//div[contains(@class,'p-multiselect-label') and contains(normalize-space(),'اختر الموضوع')]]//div[contains(@class,'p-multiselect-trigger')]");

	/* 🔎 حقل البحث داخل قائمة «اختر الموضوع» */
	private final By topicSearchInput = By.xpath(
			"//p-multiselect[starts-with(normalize-space(@placeholder),'-اختر الموضوع')]//input[contains(@class,'p-multiselect-filter')]");

	private final By selectedTopcValuesDisplay = By
			.xpath("//p-multiselect[@placeholder='-اختر الموضوع -']//div[contains(@class,'p-multiselect-label')]");

	/* 🔽 المحدد: اختر المكان | Place Filter */
	private final By placeFilter = By.xpath(
			"//div[contains(@class,'p-multiselect')][.//div[contains(@class,'p-multiselect-label') and contains(normalize-space(),'اختر المكان')]]//div[contains(@class,'p-multiselect-trigger')]");

	private final By selectedPlaceValuesDisplay = By
			.xpath("//p-multiselect[@placeholder='-اختر المكان -']//div[contains(@class,'p-multiselect-label')]");

	/* 🔎 حقل البحث داخل قائمة «اختر المكان» */
	private final By placeSearchInput = By.xpath(
			"//p-multiselect[starts-with(normalize-space(@placeholder),'-اختر المكان')]//input[contains(@class,'p-multiselect-filter')]");

	/* 🔽 المحدد: اختر الفترة | Time Period Filter */
	private final By timeFilter = By.xpath(
			"//div[contains(@class,'p-multiselect')][.//div[contains(@class,'p-multiselect-label') and contains(normalize-space(),'اختر الفترة')]]//div[contains(@class,'p-multiselect-trigger')]");

	private final By selectedTimeValuesDisplay = By
			.xpath("//p-multiselect[@placeholder='-اختر الفترة -']//div[contains(@class,'p-multiselect-label')]");

	/* 🔎 حقل البحث داخل قائمة «اختر الفترة» */
	private final By timeSearchInput = By.xpath(
			"//p-multiselect[starts-with(normalize-space(@placeholder),'-اختر الفترة')]//input[contains(@class,'p-multiselect-filter')]");

	/* 🔽 المحدد: اختر الوعاء | Container Filter */
	private final By containerFilter = By.xpath(
			"//div[contains(@class,'p-multiselect')][.//div[contains(@class,'p-multiselect-label') and contains(normalize-space(),'اختر الوعاء')]]//div[contains(@class,'p-multiselect-trigger')]");

	private final By selectedContainerValuesDisplay = By
			.xpath("//p-multiselect[@placeholder='-اختر الوعاء -']//div[contains(@class,'p-multiselect-label')]");

	/* 🔎 حقل البحث داخل قائمة «اختر الوعاء» */
	private final By containerSearchInput = By.xpath(
			"//p-multiselect[starts-with(normalize-space(@placeholder),'-اختر الوعاء')]//input[contains(@class,'p-multiselect-filter')]");

	// 🏷️ عنوان قسم «النتائج»
	private final By resultsHeader = By.cssSelector("h5.tool-results-header"); // <h5 class="tool-results-header
																				// …">النتائج</h5>

	/* ✳️ الكلمات المطابِقة داخل جدول نتائج الكشاف السياقي */
	private final By resultWords = By.xpath("//td[contains(@class, 'text-blue-500')]");

//	// 💬 عمود الكلمة في كل صف
//	private final By resultWords = By.cssSelector("div.conconrdancer-table td.text-center.text-blue-500");

	// 🔳 الجدول الكامل
	private final By resultsTable = By.cssSelector("div.conconrdancer-table table.p-datatable-table");

	// 🔢 شريط الترقيم (Paginator) الظاهر أسفل نتائج الجدول
	private final By pagination = By
			.xpath("//p-paginator//div[@data-pc-section='root' and contains(@class,'p-paginator')]");

	/* 🔳 الجدول (حاوية الـ paginator) */
	private final By paginatorRoot = By.xpath("//div[contains(@class,'p-paginator') and @data-pc-section='root']");

	/* ⏭️ زر الصفحة التالية */
	private final By paginationNextButton = By.cssSelector("p-paginator button[aria-label='Next Page']");

	/* ⏮️ زر الصفحة السابقة */
	private final By paginationPreviousButton = By.cssSelector("p-paginator button[aria-label='Previous Page']");

	/* 🔤 زر الصفحة النشطة (المُظلَّل) */
	private final By activePageButton = By.cssSelector("p-paginator button.p-paginator-page.p-highlight");

	/* 🔢 كل أزرار أرقام الصفحات */
	private final By paginationPageNumbers = By.cssSelector("p-paginator button.p-paginator-page");

	/* 🔢 مُكوّن «عدد النتائج لكل صفحة» (rows‑per‑page) */
	private final By rowsPerPageDropdown = By.xpath(
			"//p-paginator//div[contains(@class,'p-paginator-rpp-options')]//span[@role='combobox' and contains(@class,'p-dropdown-label')]");

	// 🧾 كل صف من الصفوف
	private final By tableRows = By.cssSelector("div.conconrdancer-table tbody[role='rowgroup'] > tr");

	// 🔤 الأعمدة داخل الصف الأول
	private final By tableColumnsInFirstRow = By
			.cssSelector("div.conconrdancer-table tbody[role='rowgroup'] > tr:first-of-type > td");
	// 🔻 عناوين الأعمدة التي تدعم الترتيب
	private final By sortableColumnHeaders = By.xpath("//th[contains(@class,'sortable-column')]");

	/* 📥 زرّ التصدير داخل شريط أدوات البحث */
	private final By exportButton = By.xpath(
			"//div[contains(@class,'grid')] //button[@ptooltip='تصدير' and .//img[contains(@src,'icon-export')]]");

	// 📌 ثابت XPath لجميع خيارات الفلاتر متعددة الاختيار (يعتمد على aria-label)
	private static final String MULTI_SELECT_OPTION_XPATH_TEMPLATE = "//li[@role='option' and @aria-label='{OPTION}']";
	private static final String COPY_ICON_XPATH_TEMPLATE = "//tbody[contains(@class,'p-datatable-tbody')]/tr[{ROW_INDEX}]//span[@ptooltip='نسخ' and contains(@class,'pi-copy')]";
	private static final String ROW_XPATH_TEMPLATE = "//tbody[contains(@class,'p-datatable-tbody')]/tr[{ROW_INDEX}]";

	// أيقونة التفاصيل في صف معين
	private static final String DETAILS_ICON_XPATH_TEMPLATE = "//tbody[contains(@class,'p-datatable-tbody')]/tr[{ROW_INDEX}]//span[contains(@ptooltip,'تفاصيل') and contains(@class,'pi-info-circle')]";
	// 📌 قالب XPath لخلايا الجدول، يحتوي على متغيرين: ROW_INDEX وCOLUMN_INDEX
	private static final String COLUMN_CELL_XPATH_TEMPLATE = "//tbody[contains(@class,'p-datatable-tbody')]/tr[{ROW_INDEX}]/td[{COLUMN_INDEX}]";

	/* 📜 خيار داخل قائمة «عدد النتائج لكل صفحة» ‑ يُستخدم بصيغة ‎String.format */
	private static final String RESULTS_PER_PAGE_OPTION_XPATH_TEMPLATE = "//div[contains(@class,'p-dropdown-panel') and not(contains(@style,'display: none'))]//li[@role='option' and @aria-label='%s']";

	/* ✅ عنوان التوست الذي يُـظهر عبارة «تم نسخ النص …» */
	private final By copyToastMessage = By
			.xpath("//div[@data-pc-section='summary' and contains(normalize-space(),'تم نسخ النص')]");

	private final By copiedTextContent = By.xpath(
			"//p-toastitem[not(contains(@class,'ng-leave'))] //div[@data-pc-section='summary' and contains(normalize-space(.),'تم نسخ النص')] /following-sibling::div[@data-pc-section='detail']");

	/* ✅ الجُمل (السياق السابق واللاحق) داخل صفوف نتائج الكشّاف السياقي */
	private final By fullResultSentences = By.xpath(
			"//table[contains(@class,'p-datatable-table')]//tbody //td[(contains(@class,'right-contnet') or contains(@class,'left-contnet')) and not(contains(@class,'text-blue-500'))]");

	// نافذة التفاصيل نفسها
	private final By detailsPopup = By.cssSelector("div.p-overlaypanel[role='dialog']");

	// زر إغلاق النافذة ( X الأحمر )
	private final By closeDetailsPopup = By
			.cssSelector("div.p-overlaypanel[role='dialog'] button.p-overlaypanel-close");

	// ───────── بيانات السطرين (عرض: «عنوان» : «قيمة») ─────────

	// الوعاء
	private final By containerDetailsPopup = By
			.xpath("//div[@role='dialog']//span[normalize-space()='الوعاء']/parent::div");

	// المجال
	private final By fieldDetailsPopup = By
			.xpath("//div[@role='dialog']//span[normalize-space()='المجال']/parent::div");

	// الموقع
	private final By locationDetailsPopup = By
			.xpath("//div[@role='dialog']//span[normalize-space()='الموقع']/parent::div");

	// المادة
	private final By sourceDetailsPopup = By
			.xpath("//div[@role='dialog']//span[normalize-space()='المادة']/parent::div");

	// السياق (يأتى فى صف بعرض كامل)
	private final By contextDetailsPopup = By
			.xpath("//div[@role='dialog']//span[normalize-space()='السياق']/parent::div");

	/* 🧾 كل رؤوس الأعمدة في الجدول */
	private final By allTableHeaders = By.xpath("//table//thead//th");

	/* 🔽 أيقونة الفرز داخل العمود */
	private final By sortingIcon = By.xpath(".//p-sorticon[contains(@class,'p-element')]");

	// 📦 المُنشئ
	public ConcordancerPage(WebDriver driver) {
		super(driver);
	}

	/**
	 * 🏷️ استرجاع النص الظاهر لعنوان الأداة الرئيسي
	 *
	 * 🔹 يُستخدم للتحقق من وجود وعرض العنوان الأساسي للأداة في واجهة الصفحة. 🔹
	 * يُفيد في اختبارات التحقق من واجهة المستخدم (UI) لضمان ظهور العنوان المتوقع.
	 *
	 * 🏷️ Retrieves the visible text of the main tool title
	 *
	 * 🔹 Used to verify the presence and visibility of the tool’s main title in the
	 * page. 🔹 Useful for UI tests to ensure the correct title is displayed.
	 *
	 * @return نص عنوان الأداة الرئيسي | The visible text of the tool title
	 * @throws RuntimeException إذا لم يتم العثور على عنوان الأداة في الصفحة |
	 *                          Throws RuntimeException if the tool title is not
	 *                          found
	 *
	 *                          📌 الهدف | Purpose: التأكد من أن واجهة المستخدم تعرض
	 *                          العنوان الأساسي للأداة بشكل صحيح
	 */
	@Step("🏷️ Get tool title")
	public String getToolTitleText() {
		try {
			String title = waitForElement(toolTitle).getText().trim();
			Allure.step("✅ Tool title retrieved successfully: " + title);
			return title;
		} catch (Exception e) {
			String message = "⚠️ لم يتم العثور على عنوان الأداة في الصفحة";
			Allure.step(message + " | Failed to retrieve tool title");
			throw new RuntimeException(message, e);
		}
	}

	/**
	 * ⓘ النقر على أيقونة المعلومات بجانب اسم الأداة
	 *
	 * 🔹 ينتظر حتى تظهر الأيقونة. 🔹 يسجل خصائصها لأغراض التصحيح. 🔹 يمرر المؤشر
	 * فوقها لتنشيط أي تأثيرات بصرية. 🔹 ينقر باستخدام JavaScript لتجاوز أي مشاكل في
	 * واجهة المستخدم.
	 *
	 * ⓘ Clicks the info (!) icon beside the tool title
	 *
	 * 🔹 Waits until the icon is visible. 🔹 Logs its properties for debugging. 🔹
	 * Hovers over it to trigger visual effects. 🔹 Clicks it using JavaScript to
	 * bypass UI restrictions.
	 *
	 * @throws RuntimeException إذا فشل النقر على الأيقونة | Throws RuntimeException
	 *                          if the click fails
	 *
	 *                          📌 الهدف | Purpose: محاكاة تفاعل المستخدم مع أيقونة
	 *                          المساعدة بجانب اسم الأداة
	 */
	@Step("ⓘ Click on info (!) icon beside tool title")
	public void clickInfoIcon() {
		try {
			WebElement iconElement = waitForElement(infoIcon);
			logIconDetails(iconElement);

			Actions actions = new Actions(driver);
			actions.moveToElement(iconElement).pause(Duration.ofMillis(300)).perform();

			jsClick(infoIcon);
			Allure.step("✅ Successfully clicked on info (!) icon");

		} catch (Exception e) {
			String msg = "❌ Failed to click info (!) icon beside tool title";
			Allure.step(msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * 👁️ التحقق من ظهور أيقونة المعلومات بجانب اسم الأداة
	 *
	 * 👁️ Checks if the info (!) icon is visible beside the tool title
	 *
	 * @return true إذا كانت الأيقونة مرئية | true if the info icon is visible
	 * @throws RuntimeException إذا فشل التحقق من ظهور الأيقونة | Throws
	 *                          RuntimeException if visibility check fails
	 *
	 *                          📌 الهدف | Purpose: ضمان ظهور أيقونة المعلومات
	 *                          لمساعدة المستخدم
	 */
	@Step("👁️ Check visibility of info (!) icon beside tool title")
	public boolean isInfoIconVisisable() {
		try {
			boolean visible = isElementVisible(infoIcon);
			Allure.step("ℹ️ Info (!) icon visibility: " + visible);
			return visible;
		} catch (Exception e) {
			String msg = "❌ Failed to check visibility of info (!) icon";
			Allure.step(msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * 🔍 التحقق من ظهور حقل البحث
	 *
	 * 🔍 Checks if the search input field is currently visible
	 *
	 * @return true إذا كان الحقل ظاهرًا | true if search input is visible
	 * @throws RuntimeException إذا فشل التحقق من ظهور الحقل | Throws
	 *                          RuntimeException if visibility check fails
	 *
	 *                          📌 الهدف | Purpose: التأكد من جاهزية واجهة البحث
	 *                          للتفاعل
	 */
	@Step("🔍 Check visibility of search input field")
	public boolean isSearchInputVisisable() {
		try {
			boolean visible = isElementVisible(searchInput);
			Allure.step("ℹ️ Search input visibility: " + visible);
			return visible;
		} catch (Exception e) {
			String msg = "❌ Failed to check visibility of search input field";
			Allure.step(msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * 🔎 استرجاع عنصر WebElement لحقل البحث
	 *
	 * 🔹 ينتظر حتى يصبح عنصر حقل البحث ظاهرًا في الصفحة. 🔹 يُستخدم لاحقًا في إدخال
	 * الكلمات أو التحقق من خصائص الحقل.
	 *
	 * 🔎 Retrieves the WebElement for the search input field
	 *
	 * 🔹 Waits until the search input field element is visible. 🔹 Used later to
	 * type queries or check its properties.
	 *
	 * @return عنصر WebElement لحقل البحث | The WebElement representing the search
	 *         input field
	 * @throws RuntimeException إذا فشل في العثور على العنصر | Throws
	 *                          RuntimeException if the element cannot be found
	 *
	 *                          📌 الهدف | Purpose: ضمان وجود عنصر البحث للتفاعل معه
	 */
	@Step("🔎 Retrieve search input WebElement")
	public WebElement searchInputWebElement() {
		try {
			WebElement input = waitForElement(searchInput);
			Allure.step("✅ Search input field retrieved successfully");
			return input;
		} catch (Exception e) {
			String msg = "❌ Failed to retrieve search input WebElement";
			Allure.step(msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * 📑 استرجاع النص من نافذة التفاصيل (Popup)
	 *
	 * 🔹 ينتظر ظهور نافذة المعلومات (Dialog). 🔹 يستخرج النصوص الظاهرة داخل
	 * النافذة.
	 *
	 * 📑 Retrieves the text displayed in the info popup dialog
	 *
	 * 🔹 Waits until the info dialog is visible. 🔹 Extracts the text content
	 * inside the popup.
	 *
	 * @return نص النافذة | The popup text content
	 * @throws RuntimeException إذا فشل استخراج النص | Throws RuntimeException if
	 *                          text retrieval fails
	 *
	 *                          📌 الهدف | Purpose: استخدام النصوص الظاهرة في التحقق
	 *                          أو التوثيق
	 */
	@Step("📑 Retrieve text from info popup dialog")
	public String getInfoDialogText() {
		try {
			waitForElement(infoDialogText);
			String text = driver.findElement(infoDialogText).getText().trim();
			Allure.step("✅ Info popup text retrieved: " + text);
			return text;
		} catch (Exception e) {
			String msg = "❌ Failed to retrieve text from info popup dialog";
			Allure.step(msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * ❎ إغلاق نافذة التفاصيل (Popup)
	 *
	 * 🔹 ينفذ الضغط على زر الإغلاق الموجود داخل النافذة. 🔹 يضمن إغلاق الـ Dialog
	 * بشكل سليم.
	 *
	 * ❎ Closes the info popup dialog
	 *
	 * 🔹 Clicks the close button inside the popup dialog. 🔹 Ensures the dialog is
	 * closed properly.
	 *
	 * @throws RuntimeException إذا فشل في إغلاق النافذة | Throws RuntimeException
	 *                          if closing fails
	 *
	 *                          📌 الهدف | Purpose: تنظيف واجهة المستخدم بعد استخدام
	 *                          نافذة التفاصيل
	 */
	@Step("❎ Close info popup dialog")
	public void closeInfoDialog() {
		try {
			waitAndClick(closeDialogButton);
			Allure.step("✅ Info popup dialog closed successfully");
		} catch (Exception e) {
			String msg = "❌ Failed to close the info popup dialog";
			Allure.step(msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * 🎯 يُعيد مُحدِّد نص نافذة التفاصيل
	 *
	 * 🔹 يُستخدم هذا اللوكيتر في شروط الانتظار (wait conditions) أو لجلب عنصر النص
	 * داخل نافذة المعلومات.
	 *
	 * 🎯 Returns the locator for the info popup text element
	 *
	 * 🔹 Useful for explicit waits or to fetch the dialog's text element.
	 *
	 * @return الكائن By الخاص بعنصر نص النافذة | The By locator for the info dialog
	 *         text
	 *
	 *         📌 الهدف | Purpose: توحيد الوصول للّوكيتر لاستخدامه في أكثر من موضع
	 */
	@Step("🎯 Get locator for info popup text")
	public By getInfoDialogTextLocator() {
		Allure.step("📌 Returning info dialog text locator");
		return infoDialogText;
	}

	/**
	 * ⌨️ إدخال كلمة البحث ديناميكيًا حرفًا بحرف مع التحقق
	 *
	 * 🔹 يمرّر للعنصر، يمنع إرسال الفورم تلقائيًا، ينتظر ظهور لوحة المفاتيح والقيمة
	 * الفارغة، ثم يكتب الكلمة حرفًا بحرف مع تحقق بعد كل حرف، وأخيرًا يُطلق أحداث
	 * input/blur.
	 *
	 * ⌨️ Enter the search keyword dynamically (character by character) and verify
	 * each step
	 *
	 * 🔹 Scrolls into view, prevents form auto-submit, waits for keyboard & empty
	 * value, types the keyword char-by-char with verification, then dispatches
	 * input/blur events.
	 *
	 * @param keyword الكلمة المطلوب إدخالها | The keyword to enter
	 * @throws RuntimeException إذا فشل إدخال أي حرف أو تحقق القيمة | Throws
	 *                          RuntimeException on failure
	 *
	 *                          📌 الهدف | Purpose: محاكاة كتابة المستخدم بدقّة
	 *                          وإجبار التحديثات/الاستماعات الأمامية
	 */
	@Step("⌨️ Enter search keyword dynamically and verify it")
	public void enterSearchKeyword(String keyword) {
		try {
			// 📌 العثور على الحقل
			WebElement input = waitForElement(searchInput);
			Allure.step("🔎 Search input located");

			// 🖱️ تمرير للعنصر
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", input);
			Allure.step("🧭 Scrolled input into view");

			// 🛡️ منع إرسال الفورم تلقائيًا
			((JavascriptExecutor) driver).executeScript(
					"if(arguments[0].form) arguments[0].form.addEventListener('submit', function(e){ e.preventDefault(); });",
					input);
			Allure.step("🛡️ Prevented form auto-submit on Enter");

			// ⏳ الانتظار: لوحة مفاتيح مرئية + القيمة فارغة
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			wait.until(d -> {
				boolean keyboardVisible = !d.findElements(By.cssSelector("app-virtual-keyboard")).isEmpty();
				String value = input.getAttribute("value") == null ? "" : input.getAttribute("value").trim();
				return keyboardVisible && value.isEmpty();
			});
			Allure.step("⌛ Virtual keyboard visible and input empty");

			// 🧹 تفريغ الحقل كبداية نظيفة
			input.clear();
			Allure.step("🧹 Cleared input before typing");

			// ⌨️ كتابة حرفًا بحرف مع التحقق بعد كل حرف
			StringBuilder currentText = new StringBuilder();
			for (char ch : keyword.toCharArray()) {
				String nextChar = String.valueOf(ch);
				input.sendKeys(nextChar);
				currentText.append(nextChar);

				boolean confirmed = wait.until(d -> {
					String actual = input.getAttribute("value");
					actual = (actual == null) ? "" : actual.trim();
					return actual.equals(currentText.toString());
				});

				if (!confirmed) {
					String msg = "❌ Failed to confirm character: " + nextChar;
					Allure.step(msg);
					throw new RuntimeException(msg);
				}
				Allure.step("✅ Character accepted: '" + nextChar + "' → current: '" + currentText + "'");
			}

			// 📌 إطلاق أحداث front-end لإجبار التحديث
			((JavascriptExecutor) driver)
					.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", input);
			((JavascriptExecutor) driver)
					.executeScript("arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));", input);
			Allure.step("📡 Dispatched 'input' and 'blur' events");

			// 🟢 تسجيل الكلمة المدخلة
			Allure.step("📥 Keyword entered dynamically: " + keyword);
			System.out.println("📥 Keyword entered dynamically: " + keyword);

		} catch (Exception e) {
			String msg = "❌ Failed to enter keyword dynamically";
			Allure.step(msg + " → " + e.getMessage());
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * 🔍 الضغط على زر البحث مع منع إعادة تحميل الصفحة عند إرسال الفورم
	 *
	 * 🔹 هذا الميثود يضمن أن الضغط على زر البحث لن يُعيد تحميل الصفحة بسبب إرسال
	 * الفورم. يحاول أولًا النقر باستخدام JavaScript، وإذا فشل، يستخدم نقرة
	 * WebDriver كخطة بديلة.
	 *
	 * 🔍 Click the search button while preventing page reload due to form
	 * submission
	 *
	 * 🔹 Ensures that clicking the search button does not reload the page by form
	 * submission. Tries JS click first; falls back to WebDriver click if needed.
	 *
	 * 📌 الهدف | Purpose: محاكاة ضغط زر البحث بشكل آمن في كل الظروف
	 */
	@Step("🔍 Click search button with form submission prevention")
	public void clickSearchButton() {
		try {
			// 🔎 العثور على زر البحث
			WebElement button = waitForElement(searchButton);
			Allure.step("🔎 Search button located");

			// 🛡️ منع الفورم من إعادة تحميل الصفحة
			((JavascriptExecutor) driver).executeScript(
					"if(arguments[0].form) arguments[0].form.addEventListener('submit', function(e){ e.preventDefault(); });",
					button);
			Allure.step("🛡️ Prevented form auto-submit on click");

			// ⏳ انتظار أن يصبح قابلاً للنقر
			wait.until(ExpectedConditions.elementToBeClickable(button));

			// 🖱️ محاولة النقر عبر JavaScript
			jsClick(searchButton);
			Allure.step("✅ Search button clicked via JavaScript");

		} catch (Exception jsClickError) {
			// ⚠️ عند فشل JS، استخدام نقرة WebDriver
			try {
				WebElement fallback = waitForElement(searchButton);
				wait.until(ExpectedConditions.elementToBeClickable(fallback));
				fallback.click();
				Allure.step("🖱️ Search button clicked via WebDriver fallback");
			} catch (Exception e) {
				throw new RuntimeException("❌ Failed to click search button", e);
			}
		}
	}

	/**
	 * 📑 استخراج الكلمات الظاهرة في نتائج البحث
	 *
	 * 🔹 ينتظر حتى تظهر نتائج ذات نصوص غير فارغة، ثم يجلب جميع الكلمات الظاهرة من
	 * الجدول.
	 *
	 * 📑 Get the list of result words displayed in the search table
	 *
	 * 🔹 Waits until at least one non-empty result is visible, then collects all
	 * result texts.
	 *
	 * @return قائمة النصوص الظاهرة | List of result words
	 */
	@Step("📑 Get search result words")
	public List<String> getSearchResultWords() {
		try {
			// ⏳ انتظار نتائج غير فارغة
			wait.until(driver -> {
				List<WebElement> results = driver.findElements(resultWords);
				boolean hasValidText = results.stream().anyMatch(el -> !el.getText().trim().isEmpty());
				System.out.println("🔁 Polling results - Size: " + results.size() + ", Valid Text: " + hasValidText);
				return hasValidText;
			});

			// 📋 جلب العناصر
			List<WebElement> resultElements = driver.findElements(resultWords);
			System.out.println("📌 Number of results found: " + resultElements.size());

			// 🔄 تحويل العناصر إلى نصوص
			return resultElements.stream().map(WebElement::getText)
					.peek(text -> System.out.println("📎 Result text: " + text)).filter(text -> !text.isBlank())
					.collect(Collectors.toList());

		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to extract search result words", e);
		}
	}

	/**
	 * 📌 التحقق من أن حقل الإدخال يحتوي على الكلمة المفتاحية
	 *
	 * 🔹 يفحص ما إذا كان النص داخل حقل البحث يطابق الكلمة المتوقعة.
	 *
	 * 📌 Verify if the search input contains the expected keyword
	 *
	 * 🔹 Checks whether the search input value matches the given keyword.
	 *
	 * @param expectedKeyword الكلمة المتوقع إدخالها | Expected keyword
	 * @return true إذا تطابقت القيمة، false إذا لم تتطابق
	 */
	@Step("📌 Verify if search input contains the entered keyword")
	public boolean isSearchInputContainsKeyword(String expectedKeyword) {
		try {
			WebElement input = waitForElement(searchInput);
			Allure.step("🔎 Search input located for verification");

			boolean matched = wait.until(driver -> {
				String current = input.getAttribute("value").trim();
				if (current.equals(expectedKeyword)) {
					System.out.println("🔍 Confirmed input value: " + expectedKeyword);
					return true;
				}
				return false;
			});

			Allure.step("📌 Input field value matched expected keyword: " + expectedKeyword);
			return matched;

		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to verify keyword in input field", e);
		}
	}

	/**
	 * ⌨️ إدخال نص باستخدام لوحة المفاتيح الافتراضية
	 *
	 * 🔹 هذا الميثود يحاكي الكتابة عبر الضغط على أزرار الكيبورد الظاهري (Virtual
	 * Keyboard) ويكتب النص المطلوب حرفًا بحرف مع التأكد من ظهوره في حقل الإدخال.
	 *
	 * ⌨️ Type the given string using the virtual keyboard by clicking buttons
	 *
	 * 🔹 Simulates typing via the on-screen virtual keyboard buttons, verifying
	 * each character is entered correctly into the input field.
	 *
	 * @param text الكلمة أو النص المطلوب إدخاله | The word or text to type
	 */
	@Step("⌨️ Type using virtual keyboard: {text}")
	public void typeUsingVirtualKeyboard(String text) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
			WebElement input = waitForElement(searchInput);

			// 🛡️ منع الفورم من الإرسال التلقائي
			((JavascriptExecutor) driver).executeScript(
					"if(arguments[0].form) arguments[0].form.addEventListener('submit', function(e){ e.preventDefault(); });",
					input);

			// ✅ انتظار الكيبورد ليكون ظاهرًا
			wait.until(ExpectedConditions.visibilityOfElementLocated(keySpace));

			// 🧼 مسح الحقل قبل الكتابة
			input.clear();

			// 🧠 تعيين خريطة للأرقام العربية وبعض الرموز
			Map<String, String> keyboardMapping = new HashMap<>();
			keyboardMapping.put("1", "١");
			keyboardMapping.put("2", "٢");
			keyboardMapping.put("3", "٣");
			keyboardMapping.put("4", "٤");
			keyboardMapping.put("5", "٥");
			keyboardMapping.put("6", "٦");
			keyboardMapping.put("7", "٧");
			keyboardMapping.put("8", "٨");
			keyboardMapping.put("9", "٩");
			keyboardMapping.put("0", "٠");
			keyboardMapping.put(" ", " "); // Space
			keyboardMapping.put("⌫", "مسح"); // Delete

			// 🧾 تتبع النص المكتوب
			StringBuilder currentText = new StringBuilder();

			for (char c : text.toCharArray()) {
				String character = String.valueOf(c);
				String actualChar = keyboardMapping.getOrDefault(character, character);

				By keyLocator = getLocatorForKey(actualChar);

				String before = input.getAttribute("value");
				int previousLength = before == null ? 0 : before.trim().length();

				WebElement keyBtn = wait.until(ExpectedConditions.elementToBeClickable(keyLocator));
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", keyBtn);

				try {
					keyBtn.click();
				} catch (WebDriverException e) {
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", keyBtn);
				}

				// ✅ تحقق أن الحرف تمت إضافته أو حُذف حسب نوعه
				boolean isDelete = "مسح".equals(actualChar);
				wait.until(d -> {
					String val = input.getAttribute("value").trim();
					if (isDelete)
						return val.length() < previousLength;
					else
						return val.length() >= previousLength + 1 || val.endsWith(actualChar);
				});

				// ✍️ تحديث النص المتراكم
				if (!isDelete)
					currentText.append(actualChar);
				else if (currentText.length() > 0)
					currentText.deleteCharAt(currentText.length() - 1);

				isSearchInputContainsKeyword(currentText.toString());
			}

			// 📌 إطلاق أحداث input/blur
			((JavascriptExecutor) driver)
					.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", input);
			((JavascriptExecutor) driver)
					.executeScript("arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));", input);

			Allure.step("🎹 Virtual keyboard input completed: " + text);

		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to type using virtual keyboard: " + text, e);
		}
	}

	/**
	 * 🔑 إرجاع اللوكيتر المناسب لمفتاح معين في الكيبورد الافتراضي
	 *
	 * 🔑 Returns the locator for the given key in the virtual keyboard
	 *
	 * @param actualChar الحرف أو الرمز المطلوب | The actual key character
	 * @return By locator لمفتاح الكيبورد | The By locator for the key
	 */
	private By getLocatorForKey(String actualChar) {
		if (" ".equals(actualChar)) {
			return keySpace;
		} else if ("مسح".equals(actualChar)) {
			return keyDelete;
		} else {
			return By.xpath("//div[contains(@class,'virtual-keyboard')]//button[normalize-space(.)='" + actualChar
					+ "' or .='" + actualChar + "']");
		}
	}

	/**
	 * ⌫ ينفذ الضغط على زر الحذف في لوحة المفاتيح الافتراضية
	 *
	 * ⌫ Clicks the "Delete" key on the virtual keyboard
	 */
	@Step("⌫ Click virtual keyboard delete key")
	public void clickVirtualKeyDelete() {
		try {
			waitAndClick(keyDelete);
			Allure.step("⌫ Virtual delete key clicked");
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to click delete key", e);
		}
	}

	/**
	 * 📥 ينفذ الضغط على زر التصدير لتحميل نتائج البحث
	 *
	 * 📥 Clicks on the export button to download the search results
	 */
	@Step("📥 Click on the export button")
	public void clickExportButton() {
		try {
			waitAndClick(exportButton);
			Allure.step("📥 Export button clicked successfully");
			System.out.println("📥 Export button clicked");
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to click export button", e);
		}
	}

	/**
	 * 👀 يتحقق من ظهور زر التصدير (Export)
	 *
	 * 👀 Checks whether the export button is currently visible
	 *
	 * @return true إذا كان الزر ظاهرًا، false خلاف ذلك true if the export button is
	 *         visible, false otherwise
	 */
	@Step("👀 Check if Export Button is visible")
	public boolean isExportButtonVisisable() {
		try {
			return isElementVisible(exportButton);
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to check export button visibility", e);
		}
	}

	/**
	 * 📁 يتحقق من أن الملف تم تحميله في مجلد التنزيلات
	 *
	 * 📁 Verifies that a file with the expected extension has been downloaded
	 *
	 * @param expectedExtension الامتداد المتوقع للملف (مثل .xlsx أو .csv) The
	 *                          expected file extension (e.g., .xlsx, .csv)
	 * @return true إذا وُجد ملف مطابق، false خلاف ذلك true if a matching file
	 *         exists, false otherwise
	 */
	@Step("📁 Verify file download with extension: {expectedExtension}")
	public boolean isFileDownloaded(String expectedExtension) {
		File downloadFolder = new File(System.getProperty("user.home") + "/Downloads");
		File[] files = downloadFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(expectedExtension));

		boolean found = files != null && files.length > 0;
		Allure.step(found ? "✅ File with extension '" + expectedExtension + "' found in Downloads"
				: "❌ No file with extension '" + expectedExtension + "' found in Downloads");
		return found;
	}

	/**
	 * 📂 يوسّع قسم "المحددات" إذا كان مغلقًا
	 * 
	 * 📂 Expands the filters section if it's currently collapsed
	 */
	@Step("📂 Expand filters section (المحددات)")
	public void expandFiltersSection() {
		try {
			WebElement header = waitForElement(filtersSectionToggle);
			if ("false".equals(header.getAttribute("aria-expanded"))) {
				header.click(); // ⬇️ تنفيذ النقر لفتح القسم | Click to expand
				Allure.step("📂 Filters section expanded");
			}
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to expand filters section", e);
		}
	}

	/**
	 * 🔧 نقرتان آمنتان (Element.click ثم JS click)
	 * 
	 * 🔧 Safe click method (tries normal click, then falls back to JS click)
	 */
	private void safeClick(WebElement el) {
		try {
			el.click();
		} catch (Exception ignore) {
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
		}
	}

	/**
	 * 🎯 اختيار خيار أو أكثر من فلتر متعدد الخيارات (Multi-Select Filter)
	 *
	 * 🎯 Select one or more options from a multi-select filter
	 *
	 * @param filterButtonLocator زر لفتح الفلتر | Locator of the filter button
	 * @param searchInputLocator  حقل البحث داخل الفلتر | Locator of the filter's
	 *                            search input
	 * @param options             قائمة الخيارات المراد تحديدها (يمكن أن تشمل "ALL")
	 *                            List of options to select; may include "ALL"
	 */
	@Step("🎯 Select one or more options from Multi-Select Filter: {options}")
	public void selectFromMultiSelectFilter(By filterButtonLocator, By searchInputLocator, List<String> options) {
		try {
			// 🔽 فتح قائمة الفلتر | Open the filter dropdown
			WebElement button = new WebDriverWait(driver, Duration.ofSeconds(10))
					.until(ExpectedConditions.elementToBeClickable(filterButtonLocator));
			safeClick(button);

			// 📋 استخراج جميع الخيارات المتاحة | Extract all available options
			List<WebElement> optionElements = waitForElements(multiSelectOptions);
			List<String> allOptions = new ArrayList<>();
			for (WebElement element : optionElements) {
				String text = element.getText().trim();
				if (!text.isEmpty())
					allOptions.add(text);
			}

			// 📝 توثيق الخيارات | Log extracted options
			System.out.println("📋 All available options in filter: " + allOptions.size());
			allOptions.forEach(opt -> System.out.println("✅ " + opt));
			Allure.step("🧾 Extracted Options: " + allOptions);

			// ✅ إذا كانت "ALL" مطلوبة | If "ALL" is requested
			if (options.size() == 1 && "ALL".equalsIgnoreCase(options.get(0))) {
				waitAndClick(FilterSelectAll);
				Allure.step("🗂️ Selected all options using 'Select All' checkbox");
				return;
			}

			// 🎯 اختيار كل خيار محدد | Select each requested option
			for (String option : options) {
				WebElement input = waitForElement(panelFilterInput);
				input.clear();
				input.sendKeys(option);

				new WebDriverWait(driver, Duration.ofSeconds(5))
						.until(ExpectedConditions.visibilityOfElementLocated(panelItems));

				By optionLocator = By.xpath(MULTI_SELECT_OPTION_XPATH_TEMPLATE.replace("{OPTION}", option));
				waitAndClick(optionLocator);

				System.out.println("📌 Selected option with locator: " + optionLocator);
				Allure.step("✅ Selected option: " + option);
			}

		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to select from multi-select filter: " + options, e);
		}
	}

	/**
	 * 🧾 استخراج جميع الخيارات المتاحة من فلتر متعدد الخيارات (مثل قائمة اختيار
	 * تحتوي على Checkbox) 🧾 Extract all available options from a multi-select
	 * filter component
	 *
	 * @param filterButtonLocator محدد زر فتح الفلتر (Locator for the filter
	 *                            dropdown button)
	 * @param optionsLocator      محدد العناصر التي تمثل الخيارات داخل القائمة
	 *                            (Locator for the filter option elements)
	 * @return قائمة بالنصوص الظاهرة لجميع الخيارات المتاحة
	 */
	@Step("🧾 Extract all available options from multi-select filter")
	public List<String> getAllOptionsFromMultiSelectFilter(By filterButtonLocator, By optionsLocator) {
		try {
			// 🔽 1️⃣ فتح قائمة الفلتر
			// Step 1: Click the filter button to open the dropdown
			waitAndClick(filterButtonLocator);

			// 🧾 2️⃣ جلب جميع عناصر الخيارات داخل القائمة
			// Step 2: Locate all option elements within the dropdown
			List<WebElement> optionElements = waitForElements(optionsLocator);
			List<String> options = new ArrayList<>();

			// 🔁 3️⃣ تكرار على كل عنصر واستخراج نصه (إن لم يكن فارغًا)
			// Step 3: Loop through each option and collect its visible text
			for (WebElement element : optionElements) {
				String text = element.getText().trim();
				if (!text.isEmpty()) {
					options.add(text);
				}
			}

			// 🖨️ 4️⃣ طباعة النتائج في الكونسول للمراجعة
			// Step 4: Log the results to the console
			System.out.println("📋 Extracted Options: " + options.size());
			for (String option : options) {
				System.out.println("✅ " + option);
			}

			// 📝 5️⃣ تسجيل النتيجة في Allure Report
			// Step 5: Add extracted options to the Allure report
			Allure.step("Extracted Options: " + options.toString());

			// 🔒 6️⃣ يمكن إغلاق القائمة إذا كانت تبقى مفتوحة (اختياري)
			// Step 6: Optionally re-click the button to close the dropdown
			waitAndClick(filterButtonLocator);

			// 🎯 7️⃣ إرجاع قائمة الخيارات
			// Step 7: Return the list of extracted option texts
			return options;

		} catch (Exception e) {
			// ⚠️ التعامل مع أي خطأ أثناء الاستخراج
			// Handle and log any exception during the process
			System.out.println("❌ Error while extracting options: " + e.getMessage());
			throw new RuntimeException("❌ Failed to extract options from multi-select filter", e);
		}
	}

	/**
	 * 🎯 إرجاع محدد زر فلتر "اختر المجال" 🎯 Returns the locator for the domain
	 * filter button ("اختر المجال")
	 *
	 * @return By locator for the domain filter
	 */
	public By getDomainFilterLocator() {
		return domainFilter;
	}

	/**
	 * 🔍 إرجاع محدد حقل البحث داخل فلتر "اختر المجال" 🔍 Returns the locator for
	 * the search input inside the domain filter
	 *
	 * @return By locator for the domain filter's search input
	 */
	public By getDomainSearchInputLocator() {
		return domainSearchInput;
	}

	/**
	 * ℹ️ التحقق من ظهور رسالة "لا توجد بيانات" Check if the "No data" message is
	 * displayed (without waiting)
	 */
	public boolean isNoResultsMessageDisplayed() {
		try {
			WebElement message = driver.findElement(noDataMessage);
			return message.isDisplayed();
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	/**
	 * 📍 يُرجع اللوكيتر الخاص بجميع الخيارات داخل قائمة Multi-Select 📍 Returns the
	 * locator for all options within a multi-select dropdown
	 *
	 * @return By locator to identify multi-select options
	 */
	public By getMultiSelectOptionsLocator() {
		return multiSelectOptions;
	}

	/**
	 * 🎯 يُرجع اللوكيتر الخاص بفلتر "اختر الموضوع" 🎯 Returns the locator for the
	 * "Topic Filter" dropdown
	 *
	 * @return By locator for the topic filter button
	 */
	public By getTopicFilterLocator() {
		return topicFilter;
	}

	/**
	 * 🔎 يُرجع اللوكيتر الخاص بحقل البحث داخل فلتر "اختر الموضوع" 🔎 Returns the
	 * locator for the search input inside the "Topic Filter"
	 *
	 * @return By locator for the topic filter's search input
	 */
	public By getTopicSearchInputLocator() {
		return topicSearchInput;
	}

	/**
	 * 🟦 يُرجع اللوكيتر الذي يعرض القيم المحددة لفلتر المجال 🟦 Returns locator for
	 * selected values in the domain filter
	 *
	 * @return By locator of selected domain values
	 */
	public By getSelectedDomainValuesDisplay() {
		return selectedDomainValuesDisplay;
	}

	/**
	 * 🟨 يُرجع اللوكيتر الذي يعرض القيم المحددة لفلتر الموضوع 🟨 Returns locator
	 * for selected values in the topic filter
	 *
	 * @return By locator of selected topic values
	 */
	public By getSelectedTopcValuesDisplay() {
		return selectedTopcValuesDisplay;
	}

	/**
	 * 🗺️ يُرجع اللوكيتر الخاص بزر فلتر "اختر المكان" 🗺️ Returns locator for the
	 * "Place" filter button
	 *
	 * @return By locator for the place filter
	 */
	public By getPlaceFilterLocator() {
		return placeFilter;
	}

	/**
	 * 🌍 يُرجع اللوكيتر الذي يعرض القيم المحددة لفلتر المكان 🌍 Returns locator for
	 * selected values in the place filter
	 *
	 * @return By locator of selected place values
	 */
	public By getSelectedPlaceValuesDisplay() {
		return selectedPlaceValuesDisplay;
	}

	/**
	 * 🔎 يُرجع اللوكيتر الخاص بحقل البحث داخل فلتر "اختر المكان" 🔎 Returns locator
	 * for search input inside the "Place" filter
	 *
	 * @return By locator for place search input field
	 */
	public By getPlaceSearchInputLocator() {
		return placeSearchInput;
	}

	/**
	 * 🕒 يُرجع اللوكيتر الخاص بزر فلتر "اختر الفترة" 🕒 Returns locator for the
	 * "Time" filter button
	 *
	 * @return By locator for time filter
	 */
	public By getTimeFilter() {
		return timeFilter;
	}

	/**
	 * 🔍 يُرجع اللوكيتر الخاص بحقل البحث داخل فلتر الفترة 🔍 Returns locator for
	 * the search input in the time filter
	 *
	 * @return By locator for time search input
	 */
	public By getTimeSearchInput() {
		return timeSearchInput;
	}

	/**
	 * ⏳ يُرجع اللوكيتر الذي يعرض القيم المحددة لفلتر الفترة ⏳ Returns locator for
	 * selected values in the time filter
	 *
	 * @return By locator for selected time values
	 */
	public By getSelectedTimeValuesDisplay() {
		return selectedTimeValuesDisplay;
	}

	/**
	 * 📦 يُرجع اللوكيتر الخاص بزر فلتر "اختر الوعاء" 📦 Returns locator for the
	 * "Container" filter button
	 *
	 * @return By locator for container filter
	 */
	public By getContainerFilter() {
		return containerFilter;
	}

	/**
	 * 🔍 يُرجع اللوكيتر الخاص بحقل البحث داخل فلتر الوعاء 🔍 Returns locator for
	 * the search input in the container filter
	 *
	 * @return By locator for container search input
	 */
	public By getContainerSearchInput() {
		return containerSearchInput;
	}

	/**
	 * 🧾 يُرجع اللوكيتر الذي يعرض القيم المحددة لفلتر الوعاء 🧾 Returns locator for
	 * selected values in the container filter
	 *
	 * @return By locator for selected container values
	 */
	public By getSelectedContainerValuesDisplay() {
		return selectedContainerValuesDisplay;
	}

	/**
	 * 🔔 يتحقق من ظهور رسالة التأكيد بعد النسخ (Toast) 🔔 Verifies that the copy
	 * confirmation toast message appears
	 */
	@Step("🔔 Verify copy toast message appears")
	public void verifyCopyToastAppeared() {
		try {
			Assert.assertTrue(isElementVisible(copyToastMessage), "❌ Copy confirmation message not displayed");
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to verify copy toast message", e);
		}
	}

	/**
	 * 📎 يستخرج النص المنسوخ من رسالة Toast بعد النسخ 📎 Retrieves copied text from
	 * the toast message
	 *
	 * @return The copied text content
	 */
	@Step("📎 Get copied text content from the toast message")
	public String getCopiedTextFromToast() {
		try {
			return waitForElement(copiedTextContent).getText().trim();
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to extract copied text", e);
		}
	}

	/**
	 * 📝 يجلب الجملة الكاملة من صف محدد في جدول النتائج (مؤشر يبدأ من 1)
	 *
	 * 🔹 يدمج نصوص الأعمدة (2 + 3 + 4) لتكوين الجملة كما تظهر في الجدول.
	 *
	 * 📝 Retrieves the full sentence from a specific row in the results table
	 * (1-based index). 🔹 Concatenates columns 2, 3, and 4 to produce the full
	 * sentence as displayed.
	 *
	 * @param rowIndex رقم الصف كما يظهر في الجدول (يبدأ من 1) | Row index (1-based)
	 * @return الجملة الكاملة الناتجة عن دمج الأعمدة | The full sentence (columns
	 *         2+3+4)
	 * @throws RuntimeException إذا فشل استخراج الجملة من الصف المطلوب | If
	 *                          extraction fails
	 *
	 *                          📌 الهدف: توفير طريقة موحّدة لقراءة الجمل المركّبة
	 *                          من الجدول.
	 */
	@Step("🔍 Get full sentence from row index: {rowIndex}")
	public String getFullSentenceByRowIndex(int rowIndex) {
		try {
			String xpath = ROW_XPATH_TEMPLATE.replace("{ROW_INDEX}", String.valueOf(rowIndex));
			WebElement row = waitForElement(By.xpath(xpath));

			String col2 = row.findElement(By.xpath("./td[2]")).getAttribute("textContent").trim();
			String col3 = row.findElement(By.xpath("./td[3]")).getAttribute("textContent").trim();
			String col4 = row.findElement(By.xpath("./td[4]")).getAttribute("textContent").trim();

			String fullSentence = col2 + " " + col3 + " " + col4;
			Allure.step("🟢 Full sentence [row " + rowIndex + "]: " + fullSentence);
			return fullSentence;
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to extract full sentence from row " + rowIndex, e);
		}
	}

	/**
	 * 📎 استخراج النص من الحافظة (Clipboard) مباشرة من نظام التشغيل
	 *
	 * 🔹 مفيد للتحقق من عمليات النسخ داخل الواجهة (مثل زر «نسخ» في الصفوف).
	 *
	 * 📎 Retrieves plain text directly from the system clipboard. 🔹 Useful to
	 * assert copy-to-clipboard actions triggered in the UI.
	 *
	 * @return النص الموجود في الحافظة | The current clipboard text
	 * @throws RuntimeException إذا تعذّر الوصول للحافظة أو لم تكن نصية | If
	 *                          clipboard access fails
	 *
	 *                          📌 ملاحظة: يتطلب بيئة سطح مكتب مع صلاحية الوصول
	 *                          للحافظة.
	 */
	@Step("📎 Extract copied text from system clipboard")
	public String getCopiedTextFromClipboard() {
		try {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Clipboard clipboard = toolkit.getSystemClipboard();
			String text = (String) clipboard.getData(DataFlavor.stringFlavor);
			Allure.step("📎 Clipboard text fetched: " + (text == null ? "<null>" : text));
			return text;
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to retrieve clipboard content", e);
		}
	}

	/**
	 * 🖱️ النقر على أيقونة «نسخ» داخل صف محدد (مؤشر يبدأ من 1)
	 *
	 * 🔹 يستخدم XPath ديناميكي للوصول إلى زر النسخ في الصف الهدف ثم ينقره بأمان.
	 *
	 * 🖱️ Clicks the copy icon within a specific row (1-based index). 🔹 Uses a
	 * dynamic XPath to locate the row’s copy button and clicks it safely.
	 *
	 * @param rowIndex رقم الصف المراد تنفيذ النسخ فيه | Target row index (1-based)
	 * @throws RuntimeException إذا فشل العثور على الأيقونة أو النقر عليها | If
	 *                          click fails
	 *
	 *                          📌 بعد الاستدعاء يمكن التحقق من الحافظة عبر
	 *                          getCopiedTextFromClipboard().
	 */
	@Step("🖱️ Click copy icon in row {rowIndex}")
	public void clickCopyIconInRow(int rowIndex) {
		try {
			String xpath = COPY_ICON_XPATH_TEMPLATE.replace("{ROW_INDEX}", String.valueOf(rowIndex));
			waitAndClick(By.xpath(xpath));
			Allure.step("✅ Copy icon clicked in row: " + rowIndex);
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to click copy icon in row " + rowIndex, e);
		}
	}

	/**
	 * 🛈 الضغط على أيقونة «التفاصيل» بجانب نتيجة محددة
	 *
	 * 🔹 يحدد صفّ النتيجة المطلوب (1-based) ويبني XPath ديناميكيًا للوصول إلى
	 * أيقونة التفاصيل ثم ينقرها بأمان.
	 *
	 * 🛈 Clicks the “details” icon next to a specific result row. 🔹 Uses a dynamic
	 * (1-based) row index to locate the icon and performs a safe click.
	 *
	 * @param rowIndex رقم الصف المطلوب النقر على أيقونة التفاصيل فيه | Target row
	 *                 index (1-based)
	 * @throws RuntimeException إذا فشل العثور على الأيقونة أو لم ينجح النقر | If
	 *                          locating or clicking fails
	 *
	 *                          📌 الهدف: فتح نافذة تفاصيل النتيجة من الصف المحدد
	 *                          لبدء عمليات تحقق لاحقة.
	 */
	@Step("🛈 Click details icon in row: {rowIndex}")
	public void clickDetailsIconInRow(int rowIndex) {
		try {
			String xpath = DETAILS_ICON_XPATH_TEMPLATE.replace("{ROW_INDEX}", String.valueOf(rowIndex));
			waitAndClick(By.xpath(xpath));
			Allure.step("✅ Details icon clicked in row " + rowIndex);
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to click on details icon in row: " + rowIndex, e);
		}
	}

	/**
	 * ✅ التحقق من ظهور نافذة تفاصيل النتيجة
	 *
	 * 🔹 يتحقق بسرعة من أن نافذة التفاصيل أصبحت مرئية بعد الضغط على أيقونة
	 * التفاصيل.
	 *
	 * ✅ Verifies that the result details popup is displayed. 🔹 Fast visibility
	 * check for the popup after triggering details.
	 *
	 * @return true إذا ظهرت النافذة، وإلا false | true if the popup is visible,
	 *         otherwise false
	 * @throws RuntimeException إذا فشل التحقق أو حدث استثناء غير متوقع | If
	 *                          verification fails unexpectedly
	 *
	 *                          📌 الهدف: تأكيد فتح نافذة التفاصيل قبل متابعة خطوات
	 *                          الفحص التالية.
	 */
	@Step("✅ Verify details popup is displayed")
	public boolean verifyDetailsPopupIsDisplayed() {
		try {
			boolean visible = isElementVisible(detailsPopup);
			Allure.step("📌 Details popup visible: " + visible);
			return visible;
		} catch (Exception e) {
			throw new RuntimeException("❌ Details popup did not appear!", e);
		}
	}

	/**
	 * 📦 الحصول على نص «الوعاء/المجموعة المصدرية» من نافذة التفاصيل
	 *
	 * 🔹 يقرأ النص المعروض لحقل الوعاء في نافذة التفاصيل لاستخدامه في التحقق.
	 *
	 * 📦 Gets the “container / source collection” text from the details popup. 🔹
	 * Reads the container field text for assertions.
	 *
	 * @return نص حقل الوعاء داخل النافذة | The container text from the popup
	 * @throws RuntimeException إذا تعذّر إيجاد العنصر أو قراءة نصه | If the element
	 *                          is missing or unreadable
	 */
	@Step("📦 Get 'Container' text from details popup")
	public String getResultContainerText() {
		try {
			String text = waitForElement(containerDetailsPopup).getText().trim();
			Allure.step("📦 Container: " + text);
			return text;
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to get 'Container' text from details popup", e);
		}
	}

	/**
	 * 🏷️ الحصول على نص «المجال/الفئة» من نافذة التفاصيل
	 *
	 * 🔹 يقرأ النص المعروض لحقل المجال (Domain/Field) في نافذة التفاصيل.
	 *
	 * 🏷️ Gets the “field / domain” text from the details popup. 🔹 Reads the field
	 * text for assertions.
	 *
	 * @return نص حقل المجال داخل النافذة | The field/domain text from the popup
	 * @throws RuntimeException إذا تعذّر إيجاد العنصر أو قراءة نصه | If the element
	 *                          is missing or unreadable
	 */
	@Step("🏷️ Get 'Field/Domain' text from details popup")
	public String getResultFieldText() {
		try {
			String text = waitForElement(fieldDetailsPopup).getText().trim();
			Allure.step("🏷️ Field/Domain: " + text);
			return text;
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to get 'Field/Domain' text from details popup", e);
		}
	}

	/**
	 * 📍 الحصول على نص «الموقع» من نافذة التفاصيل
	 *
	 * 🔹 يقرأ النص المعروض لحقل الموقع في نافذة التفاصيل (إن وجد).
	 *
	 * 📍 Gets the “location” text from the details popup. 🔹 Reads the location
	 * field for assertions.
	 *
	 * @return نص حقل الموقع داخل النافذة | The location text from the popup
	 * @throws RuntimeException إذا تعذّر إيجاد العنصر أو قراءة نصه | If the element
	 *                          is missing or unreadable
	 */
	@Step("📍 Get 'Location' text from details popup")
	public String getResultLocationText() {
		try {
			String text = waitForElement(locationDetailsPopup).getText().trim();
			Allure.step("📍 Location: " + text);
			return text;
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to get 'Location' text from details popup", e);
		}
	}

	/**
	 * 📰 الحصول على نص "المادة" من نافذة التفاصيل
	 *
	 * 🔹 يقرأ النص من خانة المادة (Source Material) داخل نافذة التفاصيل بعد عرض
	 * النتيجة.
	 *
	 * 📰 Get the "Source Material" text from the details popup. 🔹 Reads the text
	 * content of the source field in the popup after a result is opened.
	 *
	 * @return نص المادة الظاهر | The text content of the "Source" field
	 * @throws RuntimeException إذا لم يتم العثور على العنصر أو فشل الاستخراج
	 */
	@Step("📰 Get 'Source Material' text from details popup")
	public String getResultSourceText() {
		try {
			String text = waitForElement(sourceDetailsPopup).getText().trim();
			Allure.step("📰 Source Material: " + text);
			return text;
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to get 'Source Material' text from details popup", e);
		}
	}

	/**
	 * 📄 الحصول على نص "السياق" من نافذة التفاصيل
	 *
	 * 🔹 يستخرج نص السياق (Context) المرتبط بالنتيجة المعروضة.
	 *
	 * 📄 Get the "Context" text from the details popup. 🔹 Retrieves the context
	 * text associated with the selected result.
	 *
	 * @return نص السياق الظاهر | The text content of the "Context" field
	 * @throws RuntimeException إذا لم يتم العثور على العنصر أو فشل الاستخراج
	 */
	@Step("📄 Get 'Context' text from details popup")
	public String getResultContextText() {
		try {
			String text = waitForElement(contextDetailsPopup).getText().trim();
			Allure.step("📄 Context: " + text);
			return text;
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to get 'Context' text from details popup", e);
		}
	}

	/**
	 * ❎ إغلاق نافذة التفاصيل
	 *
	 * 🔹 ينفذ الضغط على زر الإغلاق المخصص داخل نافذة التفاصيل، وينهي عرضها.
	 *
	 * ❎ Close the detail popup dialog. 🔹 Clicks the close button inside the detail
	 * popup to dismiss it.
	 *
	 * @throws RuntimeException إذا فشل إغلاق النافذة أو لم يُعثر على زر الإغلاق
	 */
	@Step("❎ Close the detail popup dialog")
	public void closeDetailDialog() {
		try {
			waitAndClick(closeDetailsPopup);
			Allure.step("❎ Detail popup closed successfully");
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to close the detail popup dialog", e);
		}
	}

	/**
	 * ✅ التحقق من ظهور جدول النتائج بعد البحث
	 *
	 * 🔹 يفحص ما إذا كان جدول النتائج ظاهرًا بعد تنفيذ عملية البحث.
	 *
	 * ✅ Verify that the results table is visible after search. 🔹 Checks whether
	 * the results table is displayed following a search action.
	 *
	 * @return true إذا كان الجدول ظاهرًا، false خلاف ذلك
	 * @throws RuntimeException إذا فشل التحقق أو حدث خطأ غير متوقع
	 */
	@Step("✅ Verify results table is visible after search")
	public boolean isResultsTableVisible() {
		try {
			boolean visible = isElementVisible(resultsTable);
			Allure.step("✅ Results table visible: " + visible);
			return visible;
		} catch (Exception e) {
			throw new RuntimeException("❌ Results table is not visible!", e);
		}
	}

	/**
	 * 🎯 يُعيد لوكيتر الجدول
	 *
	 * 🎯 Returns the locator for the results table element (used for wait
	 * conditions)
	 *
	 * @return الكائن By الخاص بالعنصر (The By locator for results table)
	 */
	@Step("🎯 Get locator for results table")
	public By getResultsTable() {
		return resultsTable;
	}

	/**
	 * ✅ التحقق من ظهور عنوان جدول النتائج في الصفحة
	 *
	 * ✅ Verifies that the results header is visible on the page.
	 *
	 * @return true إذا كان العنوان ظاهرًا | true if the header is visible
	 * @throws RuntimeException إذا فشل التحقق أو لم يُعثر على العنصر
	 */
	@Step("✅ Verify results header is visible")
	public boolean isResultsHeaderVisible() {
		try {
			boolean visible = isElementVisible(resultsHeader);
			Allure.step("✅ Results header visible: " + visible);
			return visible;
		} catch (Exception e) {
			throw new RuntimeException("❌ Results header is not visible!", e);
		}
	}

	/**
	 * 🔢 يعيد عدد الصفوف داخل جدول النتائج
	 *
	 * 🔢 Returns the total number of result rows in the table.
	 *
	 * @return عدد الصفوف في الجدول | Total number of rows in the results table
	 * @throws RuntimeException إذا فشل التحقق أو لم يُعثر على العنصر
	 */
	@Step("🔢 Get number of rows in results table")
	public int getNumberOfResultsRows() {
		try {
			int rowCount = waitForElements(tableRows).size();
			Allure.step("🔢 Number of result rows: " + rowCount);
			return rowCount;
		} catch (Exception e) {
			throw new RuntimeException("❌ Could not count result rows!", e);
		}
	}

	/**
	 * 📊 يعيد عدد الأعمدة داخل أول صف في جدول النتائج
	 *
	 * 📊 Returns the number of columns in the first result row.
	 *
	 * @return عدد الأعمدة | Number of columns in the first row
	 * @throws RuntimeException إذا فشل التحقق أو لم يُعثر على العنصر
	 */
	@Step("📊 Get number of columns in first result row")
	public int getNumberOfColumnsInFirstRow() {
		try {
			int colCount = waitForElements(tableColumnsInFirstRow).size();
			Allure.step("📊 Number of columns in first row: " + colCount);
			return colCount;
		} catch (Exception e) {
			throw new RuntimeException("❌ Could not count columns in first result row!", e);
		}
	}

	/**
	 * 📦 استخراج XPath لخلية معينة داخل الجدول بناءً على رقم الصف والعمود
	 *
	 * 📦 Returns the full XPath for a cell at a given row and column index.
	 *
	 * @param rowIndex    رقم الصف (ابتداءً من 1) | Row index (starts at 1)
	 * @param columnIndex رقم العمود (ابتداءً من 1) | Column index (starts at 1)
	 * @return XPath كسلسلة نصية | XPath as String
	 */
	@Step("📦 Get XPath for cell at row {rowIndex}, column {columnIndex}")
	public String getColumnCellXpath(int rowIndex, int columnIndex) {
		return COLUMN_CELL_XPATH_TEMPLATE.replace("{ROW_INDEX}", String.valueOf(rowIndex)).replace("{COLUMN_INDEX}",
				String.valueOf(columnIndex));
	}

	/**
	 * 📥 استخراج جميع القيم من عمود معين داخل جدول النتائج 📥 Retrieves all cell
	 * values from a specific column in the results table
	 *
	 * @param columnIndex رقم العمود (يبدأ من 1) | Column index (1-based)
	 * @return قائمة النصوص داخل العمود المطلوب | List of cell texts from the column
	 */
	@Step("📥 Retrieved column values for column index: {0}")
	public List<String> getColumnValues(int columnIndex) {
		try {
			List<String> values = new ArrayList<>();
			List<WebElement> rows = waitForElements(tableRows);
			for (int i = 1; i <= rows.size(); i++) {
				String xpath = getColumnCellXpath(i, columnIndex);
				values.add(waitForElement(By.xpath(xpath)).getText().trim());
			}
			return values;
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to retrieve column values for index: " + columnIndex, e);
		}
	}

	/**
	 * ⬆️⬇️ ينفذ الضغط على عنوان عمود للفرز حسب الترتيب
	 *
	 * ⬆️⬇️ Clicks on the header of a sortable column to trigger sorting
	 *
	 * @param columnIndex رقم العمود (يبدأ من 1) | Column index (1-based)
	 */
	@Step("🖱 Click column header at index: {0}")
	public void clickColumnHeader(int columnIndex) {
		try {
			waitForElements(sortableColumnHeaders).get(columnIndex - 1).click();
			Allure.step("🖱 Column header clicked at index: " + columnIndex);
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to click column header at index: " + columnIndex, e);
		}
	}

	/**
	 * ⏳ الانتظار حتى يتم تغيير أول خلية في عمود محدد بعد الضغط على عنوان العمود
	 *
	 * ⏳ Waits for the first cell value in a specific column to change after sorting
	 *
	 * @param columnIndex        رقم العمود المطلوب التحقق منه (يبدأ من 1) | Column
	 *                           index (1-based)
	 * @param previousFirstValue القيمة السابقة لأول خلية قبل الضغط | Previous first
	 *                           cell value
	 * @throws InterruptedException 
	 */
	@Step("⏳ Wait for column {0} to be sorted (first cell value changes)")
	public void waitForColumnToBeSorted(int columnIndex, String previousFirstValue) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		Thread.sleep(5000);
		try {
			wait.until(driver -> {
				String currentFirst = getFirstCellText(columnIndex);
				return currentFirst != null && !currentFirst.trim().isEmpty()
						&& !currentFirst.equals(previousFirstValue);
			});
			Allure.step("✅ Column " + columnIndex + " was sorted successfully.");
			System.out.println("✅ Column " + columnIndex + " was sorted successfully.");
		} catch (TimeoutException te) {
			Allure.step("⚠️ Sorting wait timed out for column: " + columnIndex);
		}
	}

	/**
	 * 🧪 الحصول على نص أول خلية في عمود محدد
	 *
	 * 🧪 Retrieves the text content of the first cell in a specific column
	 *
	 * @param columnIndex رقم العمود (يبدأ من 1) | Column index (1-based)
	 * @return نص أول خلية | Text of the first cell in the specified column
	 */
	@Step("🧪 Get first cell text of column {columnIndex}")
	public String getFirstCellText(int columnIndex) {
		try {
			String xpath = getColumnCellXpath(1, columnIndex);
			String text = waitForElement(By.xpath(xpath)).getText().trim();
			return text;
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to get first cell text in column: " + columnIndex, e);
		}
	}

	/**
	 * 📥 استرجاع أول N صفوف من عمود معين
	 *
	 * 📥 Retrieves the top N cell texts from a given column index. ✅ يستخدم للحصول
	 * على أول قيم من عمود داخل جدول النتائج.
	 *
	 * @param columnIndex رقم العمود (يبدأ من 1) | Column index (1-based)
	 * @param count       عدد الصفوف المراد استرجاعها | Number of rows to fetch
	 * @return قائمة تحتوي على أول N قيم من العمود | List of top N values from the
	 *         column
	 */
	@Step("📥 Get top {1} values from column {0}")
	public List<String> getTopNColumnValues(int columnIndex, int count) {
		try {
			List<String> values = new ArrayList<>();
			List<WebElement> rows = waitForElements(tableRows);

			int limit = Math.min(count, rows.size());

			for (int i = 1; i <= limit; i++) {
				String xpath = getColumnCellXpath(i, columnIndex);
				String cellText = waitForElement(By.xpath(xpath)).getText().trim();
				values.add(cellText);
			}

			Allure.step("📥 Extracted top " + count + " values from column " + columnIndex + ": " + values);
			return values;

		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to get top " + count + " values from column: " + columnIndex, e);
		}
	}

	/**
	 * 🔍 يحسب عدد الأسطر المختلفة بين قائمتين من النصوص
	 *
	 * ✅ This method compares two lists of strings (row values) and returns the
	 * number of differences found between them.
	 *
	 * 🚨 Used mainly to detect visible changes after sorting table columns.
	 *
	 * @param list1 القائمة الأولى (قبل الترتيب) | First list (before sorting)
	 * @param list2 القائمة الثانية (بعد الترتيب) | Second list (after sorting)
	 * @return عدد الصفوف المختلفة | Number of changed rows
	 */
	private int countDifferences(List<String> list1, List<String> list2) {
		int changes = 0;

		// 🧮 استخدم الحجم الأصغر لتفادي تجاوز الفهرسة | Use smaller size to avoid OOB
		int size = Math.min(list1.size(), list2.size());

		for (int i = 0; i < size; i++) {
			// 🧼 إزالة المسافات لتكون المقارنة أدق | Normalize whitespace before compare
			String a = list1.get(i).trim().replaceAll("\\s+", "");
			String b = list2.get(i).trim().replaceAll("\\s+", "");

			if (!a.equals(b)) {
				changes++;
			}
		}

		// (اختياري) لوج داخلي مبسّط | Optional lightweight log
		Allure.step("📊 Row differences counted: " + changes + " (of " + size + ")");
		return changes;
	}

	/**
	 * ✅ التحقق من تأثير ترتيب عمود محدد على أول 3 صفوف في الجدول
	 *
	 * 🔍 Verifies that sorting a specific column (by clicking its header) causes a
	 * visible change in the top 3 rows (ascending then descending).
	 *
	 * 📝 Notes: - Lightweight check on the TOP 3 rows only (not full-order
	 * validation). - Checks both ascending and descending effects.
	 *
	 * @param columnIndex ترتيب العمود (1-based) | Column index (starts from 1)
	 * @param header      عنصر رأس العمود | Header WebElement of the target column
	 */
	@Step("🔍 Verify sorting effect on top 3 rows for column index: {0}")
	public void verifyTop3RowsChangeOnSort(int columnIndex, WebElement header) {
		// 🏷️ اسم العمود للتقارير | Column name for reporting
		String columnName = header.getText().trim();
		Allure.step("🧭 Column under test: " + columnName + " (index=" + columnIndex + ")");
		System.out.println("🧭 Column under test: " + columnName + " (index=" + columnIndex + ")");

		// 📋 أول 3 قيم قبل الفرز | Top 3 before sort
		List<String> originalTop3 = getTopNColumnValues(columnIndex, 3);
		Allure.step("📋 Top-3 BEFORE sort: " + originalTop3);
		System.out.println("📋 Top-3 BEFORE sort: " + originalTop3);

		try {
			// 🔼 Ascending
			String beforeAsc = getFirstCellText(columnIndex);
			Allure.step("🔼 Click header to sort ascending: " + columnName);
			System.out.println("🔼 Click header to sort ascending: " + columnName);
			clickToSort(header);
			//header.click();

			waitForColumnToBeSorted(columnIndex, beforeAsc);

			List<String> ascTop3 = getTopNColumnValues(columnIndex, 3);
			int ascChanges = countDifferences(originalTop3, ascTop3);
			Allure.step("🔼 Top-3 AFTER ascending: " + ascTop3 + " | Δ=" + ascChanges);
			System.out.println("🔼 Top-3 AFTER ascending: " + ascTop3 + " | Δ=" + ascChanges);

			Assert.assertTrue(ascChanges >= 1,
					"❌ Ascending sort did not affect top 3 rows (index: " + columnIndex + ")");

			// 🔽 Descending
			String beforeDesc = getFirstCellText(columnIndex);
			Allure.step("🔽 Click header to sort descending: " + columnName);
			clickToSort(header);
			//header.click();

			waitForColumnToBeSorted(columnIndex, beforeDesc);

			List<String> descTop3 = getTopNColumnValues(columnIndex, 3);
			int descChanges = countDifferences(ascTop3, descTop3);
			Allure.step("🔽 Top-3 AFTER descending: " + descTop3 + " | Δ=" + descChanges);

			Assert.assertTrue(descChanges >= 1,
					"❌ Descending sort did not affect top 3 rows (index: " + columnIndex + ")");

		} catch (Exception e) {
			Allure.step("⚠️ Sorting verification failed for column '" + columnName + "': " + e.getMessage());
			throw new RuntimeException("⚠️ Error while verifying sorting for column index: " + columnIndex, e);
		}
	}

	/**
	 * 🧾 إحضار جميع رؤوس الأعمدة في الجدول
	 *
	 * 🧾 Retrieves all table header elements (th) from the results table.
	 *
	 * @return قائمة بعناصر WebElement تمثل رؤوس الأعمدة | List of header
	 *         WebElements
	 */
	@Step("🧾 Get all table headers")
	public List<WebElement> getAllTableHeaders() {
		List<WebElement> headers = driver.findElements(allTableHeaders);
		Allure.step("🧾 Retrieved " + headers.size() + " table headers");
		return headers;
	}

	/**
	 * 🔽 التحقق من وجود أيقونة الفرز داخل العمود
	 *
	 * Checks whether the sorting icon exists inside a given column header.
	 *
	 * @param headerElement عنصر رأس العمود | Column header WebElement
	 * @return true إذا وُجدت أيقونة الفرز | true if sorting icon exists
	 */
	@Step("🔽 Check if sorting icon exists in column header")
	public boolean hasSortingIcon(WebElement headerElement) {
		try {
			boolean exists = !headerElement.findElements(sortingIcon).isEmpty();
			Allure.step("🔽 Sorting icon present: " + exists);
			return exists;
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to check sorting icon existence", e);
		}
	}
	
	
	private void clickToSort(WebElement header) {
	    ((JavascriptExecutor) driver).executeScript(
	        "arguments[0].scrollIntoView({block:'center', inline:'center'});", header);
	    try {
	        // لو فيه أيقونة فرز داخل الهيدر اضغطها
	        List<WebElement> icons = header.findElements(sortingIcon);
	        WebElement target = icons.isEmpty() ? header : icons.get(0);
	        target.click();
	    } catch (WebDriverException e) {
	        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", header);
	    }
	}


	/**
	 * 🎹 ينفذ الضغط على أيقونة لوحة المفاتيح الظاهرة بجانب حقل البحث
	 *
	 * 🎹 Clicks the virtual keyboard icon next to the search input field.
	 */
	@Step("🎹 Click on virtual keyboard icon beside search input")
	public void clickVirtualKeyboardIcon() {
		try {
			waitAndClick(keyboardButton);
			Allure.step("🎹 Virtual keyboard icon clicked successfully");
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to click the virtual keyboard icon", e);
		}
	}

	/**
	 * 🧪 يسترجع القيمة الحالية داخل حقل البحث
	 *
	 * 🧪 Retrieves the current value inside the search input field.
	 *
	 * @return النص الحالي في الحقل | Current input value as String
	 */
	@Step("🧪 Get current value from search input field")
	public String getSearchInputValue() {
		try {
			WebElement input = searchInputWebElement();
			String value = input.getAttribute("value").trim();
			Allure.step("🧪 Current search input value: " + value);
			return value;
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to retrieve search input value", e);
		}
	}

	/**
	 * 🔢 التحقق من ظهور شريط الترقيم أسفل الجدول
	 *
	 * 🔢 Verifies whether the pagination bar is visible under the results table.
	 *
	 * @return true إذا ظهر الشريط | true if pagination bar is visible
	 */
	@Step("🔢 Check if pagination bar is visible under results table")
	public boolean isPaginationBarVisible() {
		try {
			boolean visible = isElementVisible(pagination);
			Allure.step("🔢 Pagination bar visible: " + visible);
			return visible;
		} catch (Exception e) {
			throw new RuntimeException("❌ Pagination bar not found", e);
		}
	}

	/**
	 * ⬇️ الانتقال إلى صفحة معينة عبر النقر على رقم الصفحة
	 *
	 * ⬇️ Navigate to a specific page by clicking its number in the pagination
	 * controls.
	 *
	 * @param pageNumber رقم الصفحة | Page number to navigate to
	 */
	@Step("🧭 Navigate to page number: {0}")
	public void goToPage(int pageNumber) {
		try {
			List<WebElement> pageButtons = waitForElements(paginationPageNumbers);

			for (WebElement button : pageButtons) {
				if (button.getText().trim().equals(String.valueOf(pageNumber))) {
					button.click();
					Allure.step("🧭 Navigated to page: " + pageNumber);
					return;
				}
			}

			throw new RuntimeException("❌ Page number " + pageNumber + " not found in pagination controls");

		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to navigate to page number: " + pageNumber, e);
		}
	}

	/**
	 * 🔢 الحصول على رقم الصفحة الحالية المميزة بلون مختلف
	 * 
	 * @return رقم الصفحة النشطة حاليًا
	 */
	@Step("📍 Get currently highlighted page number")
	public int getCurrentHighlightedPageNumber() {
		try {
			WebElement activeButton = waitForElement(activePageButton);
			return Integer.parseInt(activeButton.getText().trim());
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to get highlighted page number!", e);
		}
	}

	/**
	 * 🔢 اختيار عدد النتائج المعروضة في كل صفحة من القائمة المنسدلة
	 *
	 * 🔢 Select how many results to display per page using the dropdown.
	 *
	 * @param count العدد المراد تحديده (مثل 10، 50، 100) | The desired number of
	 *              rows per page
	 */
	@Step("🔢 Select results per page: {0}")
	public void selectResultsPerPage(int count) {
		try {
			// ⬇️ افتح القائمة المنسدلة
			// Open the rows-per-page dropdown
			waitAndClick(rowsPerPageDropdown);

			// 📌 أنشئ لوكيتر ديناميكي للعدد المطلوب
			// Build a dynamic locator for the given count
			By option = By.xpath(String.format(RESULTS_PER_PAGE_OPTION_XPATH_TEMPLATE, count));

			// ✅ اختر العدد المطلوب من القائمة
			// Select the desired option
			waitAndClick(option);

			Allure.step("✅ Selected results per page: " + count);
		} catch (Exception e) {
			Allure.step("❌ Failed to select results per page: " + count);
			throw new RuntimeException("❌ Failed to select results per page: " + count, e);
		}
	}

	/**
	 * ⏳ انتظر حتى تظهر نتائج البحث أو رسالة "لا توجد بيانات"
	 *
	 * ⏳ Waits until either the results table is displayed or the "no data" message
	 * appears.
	 */
	@Step("⏳ Wait until results table or 'No Data' message is displayed")
	public void waitForResultsOrNoDataMessage() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(180));
			wait.pollingEvery(Duration.ofMillis(100)).ignoring(NoSuchElementException.class)
					.ignoring(StaleElementReferenceException.class).ignoring(ElementNotInteractableException.class);

			wait.until(driver -> {
				System.out.println("⏳ Polling... checking for table or no results message...");

				boolean tableVisible = false;
				boolean noResultsVisible = false;

				// تحقق من ظهور الجدول
				try {
					tableVisible = isResultTableDisplayed();
				} catch (Exception e) {
					System.out.println("🔸 Table check failed: " + e.getMessage());
				}

				// تحقق من ظهور رسالة "لا توجد بيانات"
				try {
					noResultsVisible = isNoResultsMessageDisplayed();
				} catch (Exception e) {
					System.out.println("🔸 No Results check failed: " + e.getMessage());
				}

				return tableVisible || noResultsVisible;
			});
		} catch (Exception e) {
			throw new RuntimeException("❌ Timed out waiting for results or 'No Data' message", e);
		}
	}

	/**
	 * ℹ️ التحقق من ظهور الجدول
	 *
	 * ℹ️ Checks whether the results table is displayed on the page.
	 *
	 * @return true إذا ظهر الجدول | true if the table is displayed
	 */
	@Step("ℹ️ Check if results table is displayed")
	public boolean isResultTableDisplayed() {
		try {
			boolean displayed = wait.until(ExpectedConditions.visibilityOfElementLocated(resultsTable)).isDisplayed();
			Allure.step("ℹ️ Results table displayed: " + displayed);
			return displayed;
		} catch (TimeoutException e) {
			Allure.step("⚠️ Results table not displayed (timeout)");
			return false;
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to check if results table is displayed", e);
		}
	}

}