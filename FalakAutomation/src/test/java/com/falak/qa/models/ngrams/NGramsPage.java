package com.falak.qa.models.ngrams;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.falak.qa.base.BasePage;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;

public class NGramsPage extends BasePage {

	/* 🧭 اسم الأداة (العنوان الرئيسي) */
	private final By toolTitle = By
			.xpath("//div[contains(@class,'surface-50')]//span[normalize-space()='التتابعات اللَّفظيَّة']");

	/* ℹ️ أيقونة التفاصيل الخاصة بأداة «االتتابعات اللَّفظيَّة» فقط */
	private final By infoIcon = By.xpath(
			"//div[contains(@class,'surface-50') and .//span[normalize-space()='التتابعات اللَّفظيَّة']]//img[contains(@src,'icon-info') and @ptooltip='التفاصيل']");

	/* 🪟 نص نافذة التفاصيل بعد فتحها */
	private final By infoDialogText = By.xpath("//div[contains(@class,'p-dialog-content')]//p[normalize-space()]");

	/* ❌ زر إغلاق نافذة التفاصيل */
	private final By closeDialogButton = By.xpath("//button[contains(@class,'p-dialog-header-close')]");

	/* 🔢 أزرار «عدد الكلمات» 1 – 2 – 3 */
	private final By oneWordFilterButton = By
			.xpath("//div[@data-pc-name='selectbutton']//span[normalize-space()='1']/ancestor::*[@role='radio']");
	private final By twoWordsFilterButton = By
			.xpath("//div[@data-pc-name='selectbutton']//span[normalize-space()='2']/ancestor::*[@role='radio']");
	private final By threeWordsFilterButton = By
			.xpath("//div[@data-pc-name='selectbutton']//span[normalize-space()='3']/ancestor::*[@role='radio']");

	/* 🔢 حقل ادخال الحد الأدنى للتكرار */
	private final By minFreqInput = By.xpath("//input[@formcontrolname='selectedMinFreq']");

	/* 🔢 حقل ادخال الحد الأقصى للتكرار */
	private final By maxFreqInput = By.xpath("//input[@formcontrolname='selectedMaxFreq']");

	/* 📝 خلايا العمود الأول فقط (جُمل / نصوص النتائج) */
	private final By firstColumnCells = By.xpath("//div[contains(@class,'tool-results')]//table//tbody/tr/td[1]");

	/* 📝 خلايا العمود الثاني فقط (ارقام عدد التكرار / نصوص النتائج) */
	private final By secondColumnCells = By.xpath("//div[contains(@class,'tool-results')]//table//tbody/tr/td[2]");

	/* 📋 كل صفّ بيانات داخل جدول النتائج */
	private final By resultRow = By.xpath("//div[contains(@class,'tool-results')]//table//tbody/tr");

	/* ⌨️ حقل إدخال لا يحتوي على (االتتابعات اللَّفظيَّة */
	private final By excludeWordsInput = By
			.xpath("//input[@placeholder='لا يحتوي على' and contains(@class,'p-inputtext')]");

	/* ⌨️ حقل إدخال يحتوي على (االتتابعات اللَّفظيَّة */
	private final By containWordsInput = By
			.xpath("//input[@placeholder='يحتوي على' and contains(@class,'p-inputtext')]");

	/* ⌨️ حقل إدخال يبدأ (االتتابعات اللَّفظيَّة */
	private final By startWithWordInput = By.xpath("//input[@placeholder='يبدأ' and contains(@class,'p-inputtext')]");

	/* ⌨️ حقل إدخال ينتهي (االتتابعات اللَّفظيَّة */
	private final By endWithWordInput = By.xpath("//input[@placeholder='ينتهي' and contains(@class,'p-inputtext')]");

	// Checkbox “استثناء علامات الترقيم”
	private final By excludePunctCheckBox = By.cssSelector("p-checkbox[formcontrolname='isExcludeRegex']");

	// ✅ الـ input المخفي (للقراءة/التحقق من الحالة)
	private final By excludePunctInput = By.cssSelector(
			"p-checkbox[formcontrolname='isExcludeRegex'] input[type='checkbox'][data-pc-section='hiddenInput']");

	private final By excludePunctRootChecked = By.cssSelector(
			"p-checkbox[formcontrolname='isExcludeRegex'] [data-pc-section='input'][data-p-highlight='true']");

	private final By excludePunctBoxHighlighted = By.cssSelector(
			"p-checkbox[formcontrolname='isExcludeRegex'] [data-pc-section='input'][data-p-highlight='true']");

	/* 🔍 زر البحث */
	private final By searchButton = By.xpath("//button[@type='submit' and .//span[normalize-space()='بحث']]");

	/* 📭 رسالة «لا توجد بيانات» بعد البحث */
	private final By noDataMessage = By.xpath("//p[contains(@class,'text-sm') and normalize-space()='لا توجد بيانات']");

	// 🏷️ عنوان قسم «النتائج»
	private final By resultsHeader = By.cssSelector("h5.tool-results-header"); // <h5 class="tool-results-header
																				// …">النتائج</h5>
	/* 🔳 جدول نتائج «ااالتتابعات اللَّفظيَّة» */
	private final By resultsTable = By.xpath(
			"//div[contains(@class,'tool-results')]//table[@role='table' and contains(@class,'p-datatable-table')]");

	// 📌 قالب XPath لخلايا الجدول، يحتوي على متغيرين: ROW_INDEX وCOLUMN_INDEX
	private static final String COLUMN_CELL_XPATH_TEMPLATE = "//tbody[contains(@class,'p-datatable-tbody')]/tr[{ROW_INDEX}]/td[{COLUMN_INDEX}]";

	/** قالب XPath لخلايا عمود معيّن (1 = أول عمود) */
	private static final String COLUMN_CELLS_XPATH_TMPL = "//table[@role='table']//tbody[@role='rowgroup']//tr/td[%d]";

	private static final String RESULTS_PER_PAGE_OPTION_XPATH_TEMPLATE = "//div[contains(@class,'p-dropdown-panel') and not(contains(@style,'display: none'))]//li[@role='option' and @aria-label='%s']";

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
	// ✅ كل الصفوف داخل جسم الجدول (PrimeNG p-table)
	private final By tableRows = By.xpath(
			"//p-table//table[@role='table' and contains(@class,'p-datatable-table')]//tbody[@role='rowgroup' and contains(@class,'p-datatable-tbody')]/tr");

	// 🔤 الأعمدة داخل الصف الأول
	private final By tableColumnsInFirstRow = By
			.cssSelector("div.conconrdancer-table tbody[role='rowgroup'] > tr:first-of-type > td");
	// 🔻 عناوين الأعمدة التي تدعم الترتيب
	private final By sortableColumnHeaders = By.xpath("//th[contains(@class,'sortable-column')]");

	/* 📥 زرّ التصدير داخل شريط أدوات البحث */
	private final By exportButton = By.xpath(
			"//div[contains(@class,'grid')] //button[@ptooltip='تصدير' and .//img[contains(@src,'icon-export')]]");

	// 🧱 جذر أي Toast ظاهر (أي نوع رسالة)
	private final By toastRoot = By
			.xpath("//div[@role='alert' and @data-pc-section='root' and contains(@class,'p-toast-message')]");

	// ❌ جذر Toast من نوع خطأ فقط
	private final By errorToastRoot = By
			.xpath("//div[@role='alert' and @data-pc-section='root' and contains(@class,'p-toast-message-error')]");

	// 📛 عنوان رسالة الخطأ (summary)
	private final By errorTitle = By.xpath(
			"//div[@role='alert' and @data-pc-section='root' and contains(@class,'p-toast-message-error')]//div[@data-pc-section='summary']");

	// 🧾 نص تفاصيل رسالة الخطأ (detail)
	// (لاحظ: سمِّ المتغير errorDetail بدل تكرار اسم errorMessage)
	private final By errorDetail = By.xpath(
			"//div[@role='alert' and @data-pc-section='root' and contains(@class,'p-toast-message-error')]//div[@data-pc-section='detail']");

	// 🔒 (اختياري) تقييد بنطاق Toast محدد حسب الموضع
	private final By errorDetailTopLeft = By.xpath(
			"//p-toast[@position='top-left']//div[@role='alert' and contains(@class,'p-toast-message-error')]//div[@data-pc-section='detail']");

	// 🔒 (اختياري) تقييد بنطاق Toast محدد حسب المفتاح (key)
	private final By errorTitleForInterceptorKey = By.xpath(
			"//p-toast[@key='interceptor']//div[@role='alert' and contains(@class,'p-toast-message-error')]//div[@data-pc-section='summary']");

	/* 🔽 أيقونة الفرز داخل العمود */
	private final By sortingIcon = By.xpath(".//p-sorticon[contains(@class,'p-element')]");

	/* 🧾 كل رؤوس الأعمدة في الجدول */
	private final By allTableHeaders = By.xpath("//table//thead//th");

	/** 🧾 رؤوس الجدول */
	private final By tableHeaders = By.cssSelector("table[role='table'] thead.p-datatable-thead > tr > th");

	/** 🔽 زر قائمة الفلترة داخل رأس أي عمود */
	private final By headerFilterButton = By.cssSelector("th button.p-column-filter-menu-button[aria-haspopup='true']");

	/**
	 * أقرب Overlay مفتوح (يدعم p-overlay / p-overlaypanel /
	 * p-column-filter-overlay)
	 */
	private final By openOverlayRoot = By.xpath(
			"(//div[contains(@class,'p-column-filter-overlay') and not(contains(@style,'display: none'))] | //div[contains(@class,'p-overlaypanel') and not(contains(@style,'display: none'))] | //div[contains(@class,'p-overlay') and contains(@class,'p-component') and not(contains(@style,'display: none'))])[last()]");

	/**
	 * 🧰 الـ Overlay الخاص بالفلترة (المفتوح حاليًا) — يعتمد على aria-controls للزر
	 * المفتوح
	 */
	private final By filterOverlay = By.xpath(
			"//div[contains(@class,'p-column-filter-overlay') and @id=//button[contains(@class,'p-column-filter-menu-button') and @aria-expanded='true']/@aria-controls]");

	/** حقول الإدخال النصية/الرقمية داخل الـ Overlay المفتوح (نص/بحث/رقم) */
	private final By overlayTextLikeInputs = By.xpath(
			".//input[(not(@type) or @type='text' or @type='search' or @type='number') and not(@disabled) and not(@readonly)]");

	/** أي مدخل ظاهر داخل الـ Overlay (بديل احتياطي) */
	private final By overlayAnyVisibleInputs = By
			.xpath(".//input[not(@type='hidden') and not(@disabled) and not(contains(@style,'display: none'))]");

	/** ✅ زر «تطبيق» داخل الـ overlay */
	private final By overlayApplyButton = By.xpath(
			"//div[@role='dialog' and contains(@class,'p-column-filter-overlay')]//button[.//span[@class='p-button-label' and normalize-space()='تطبيق']]");

	/** 🧹 زر «مسح» داخل الـ overlay */
	private final By overlayClearButton = By.xpath(
			"//div[@role='dialog' and contains(@class,'p-column-filter-overlay')]//button[.//span[@class='p-button-label' and normalize-space()='مسح']]");

	/* 🔽 Dropdown "مطابقة الكل / البعض" — يُعرّف عبر النص الظاهر في قيمة الحقل */
	private final By matchAllDropdown = By.xpath(
			"//div[contains(@class,'p-overlaypanel') or contains(@class,'p-column-filter')]//div[contains(@class,'p-dropdown')][.//span[contains(@class,'p-dropdown-label')][normalize-space()='مطابقة الكل' or normalize-space()='مطابقة البعض' or normalize-space()='Match all' or normalize-space()='Match any']]");

	/* 🔽 Dropdown "نوع المطابقة" — يُعرّف عبر النص الظاهر في قيمة الحقل */
	private final By matchTypeDropdown = By.xpath(
			"//div[contains(@class,'p-overlaypanel') or contains(@class,'p-column-filter')]//div[contains(@class,'p-dropdown')][.//span[contains(@class,'p-dropdown-label')][normalize-space()='يبدأ بـ' or normalize-space()='يحتوي على' or normalize-space()='لا يحتوي على' or normalize-space()='ينتهي بـ' or normalize-space()='يساوي' or normalize-space()='لا يساوي' or normalize-space()='Starts with' or normalize-space()='Contains' or normalize-space()='Not contains' or normalize-space()='Ends with' or normalize-space()='Equals' or normalize-space()='Not equals']]");

	/* لوحة خيارات "مطابقة الكل/البعض" المفتوحة */
	private final By matchAllDropdownPanel = By.xpath(
			"//div[contains(@class,'p-dropdown-panel') and .//ul[@role='listbox']//li[.//span[normalize-space()='مطابقة الكل' or normalize-space()='مطابقة البعض' or normalize-space()='Match all' or normalize-space()='Match any']]]");

	/* لوحة خيارات "نوع المطابقة" المفتوحة */
	private final By matchTypeDropdownPanel = By.xpath(
			"//div[contains(@class,'p-dropdown-panel') and .//ul[@role='listbox']//li[.//span[normalize-space()='يبدأ بـ' or normalize-space()='يحتوي على' or normalize-space()='لا يحتوي على' or normalize-space()='ينتهي بـ' or normalize-space()='يساوي' or normalize-space()='لا يساوي' or normalize-space()='Starts with' or normalize-space()='Contains' or normalize-space()='Not contains' or normalize-space()='Ends with' or normalize-space()='Equals' or normalize-space()='Not equals']]]");

	/* 📋 لوحة عناصر الدروب داون الظاهرة حالياً (الـ panel) */
	private final By dropdownPanel = By
			.xpath("(//div[contains(@class,'p-dropdown-panel') and not(contains(@style,'display: none'))])[last()]");

	// ✅ حاوية التمرير لقائمة الـ dropdown (الأحدث/المفتوحة الآن)
	private final By dropdownScroller = By.xpath(
			"(//div[contains(@class,'p-overlay-content')]//div[contains(@class,'p-dropdown-panel')]//div[contains(@class,'p-dropdown-items-wrapper')])[last()]");

	/* 📜 كل العناصر داخل الدروب داون الظاهر حالياً */
	private final By dropdownItems = By.xpath(
			"(//div[contains(@class,'p-dropdown-panel') and not(contains(@style,'display: none'))])[last()]//li[@role='option']");

	/* 🪟 صفوف شروط الفلترة داخل الـ overlay المفتوح */
	private final By conditionRows = By.cssSelector(
			"div.p-column-filter-overlay[aria-modal='true'] .p-column-filter-constraint, div.p-overlaypanel[aria-modal='true'] .p-column-filter-constraint");

	/*
	 * ➕ زر «أضف شرط» داخل الـ overlay المفتوح (يعتمد على الكلاس أو الـ aria-label
	 * أو نص زر PrimeNG)
	 */
	private final By addConditionButton = By.xpath(
			"//div[(contains(@class,'p-column-filter-overlay') or contains(@class,'p-overlaypanel')) and @aria-modal='true']//button[contains(@class,'p-column-filter-add-button') or @aria-label='أضف شرط' or .//span[normalize-space()='أضف شرط']]");

	/* 🔤 حقل الإدخال داخل صفّ شرط معيّن (نسخة عامّة وآمنة) */
	private By valueInputInside(WebElement row) {
		return By.xpath(
				".//p-columnfilterformelement//input[not(@disabled) and (@type='text' or @type='search' or @type='number' or not(@type))]");
	}

	/* 🎯 اختيار عنصر بالاسم (قالب جاهز) */
	private static final String DROPDOWN_ITEM_BY_TEXT_XPATH = "(//div[contains(@class,'p-dropdown-panel') and not(contains(@style,'display: none'))])[last()]//li[@role='option']//span[normalize-space()='%s']/ancestor::li[@role='option']";

	/**
	 * 📦 المُنشئ الأساسي لصفحة NGrams
	 *
	 * 🔹 يقوم بتمرير كائن WebDriver إلى الصنف الأب (BasePage) لتمكين التفاعل مع
	 * عناصر الصفحة.
	 *
	 * 📦 Constructor for the NGramsPage class.
	 *
	 * 🔹 Passes the WebDriver instance to the parent class (BasePage) to enable
	 * interaction with web elements on the page.
	 *
	 * @param driver كائن WebDriver المستخدم للتحكم في المتصفح | The WebDriver
	 *               instance used for browser automation
	 *
	 *               📌 الهدف: ضمان أن الصفحة مهيأة للعمل مع المتصفح الحالي في جميع
	 *               الاختبارات.
	 */
	public NGramsPage(WebDriver driver) {
		super(driver);
	}

	/**
	 * 🏷️ إرجاع النص الظاهر لعنوان الأداة الرئيسي
	 *
	 * 🔹 يُستخدم هذا الميثود للتحقق من عرض عنوان الأداة بشكل صحيح في واجهة
	 * المستخدم، وغالبًا في حالات التحقق من تحميل الصفحة أو التنقل الصحيح.
	 *
	 * 🏷️ Gets the visible text of the tool's main title.
	 *
	 * 🔹 This method is typically used to validate that the tool title is displayed
	 * correctly in the UI, often in assertions for page load or navigation
	 * verification.
	 *
	 * @return نص عنوان الأداة | The trimmed text of the tool title element
	 * @throws RuntimeException إذا لم يتم العثور على العنوان | Throws
	 *                          RuntimeException if the title element is not found
	 *
	 *                          📌 الهدف: ضمان ظهور العنوان الصحيح للأداة قبل
	 *                          التفاعل معها أثناء الاختبار.
	 */
	@Step("Get tool title")
	public String getToolTitleText() {
		try {
			return waitForElement(toolTitle).getText().trim();
		} catch (Exception e) {
			throw new RuntimeException("⚠️ لم يتم العثور على عنوان الأداة في الصفحة", e);
		}
	}

	/**
	 * 🔎 استرجاع عنصر WebElement الخاص بحقل الإدخال "لا يحتوي على"
	 *
	 * 🔹 يُستخدم هذا الميثود للتفاعل مع حقل الإدخال المخصص لإدخال الكلمات التي يجب
	 * استثناؤها من نتائج البحث (Exclude Words).
	 *
	 * 🔎 Retrieves the WebElement for the "Exclude Words" input field.
	 *
	 * 🔹 This method is used to interact with the input field where words to be
	 * excluded from the search results are entered.
	 *
	 * @return عنصر WebElement لحقل الإدخال | The WebElement for the exclude words
	 *         input field
	 * @throws RuntimeException إذا فشل العثور على الحقل | Throws RuntimeException
	 *                          if the field cannot be located
	 *
	 *                          📌 الهدف: توفير مرجع مباشر لحقل الإدخال ليسهل إدخال
	 *                          أو تعديل الكلمات المستثناة أثناء الاختبار.
	 */
	@Step("Retrieve 'Exclude Words' input WebElement")
	public WebElement excludeWordsInputWebElement() {
		try {
			return waitForElement(excludeWordsInput);
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to retrieve exclude Words input WebElement", e);
		}
	}

	/**
	 * 🔎 يسترجع عنصر WebElement الخاص بحقل إدخال "يحتوي على"
	 *
	 * 🔹 يُستخدم هذا الميثود للتفاعل مع الحقل الذي يتم إدخال الكلمات فيه بهدف البحث
	 * عن النتائج التي تحتوي على تلك الكلمات.
	 *
	 * 🔎 Retrieves the WebElement for the "Contain Words" input field.
	 *
	 * 🔹 This method is used to interact with the input field where words are
	 * entered to filter results that contain those words.
	 *
	 * @return عنصر WebElement لحقل "يحتوي على" | The WebElement for the contain
	 *         words input field
	 * @throws RuntimeException إذا فشل العثور على الحقل | Throws RuntimeException
	 *                          if the field cannot be located
	 *
	 *                          📌 الهدف: تمكين إدخال كلمات البحث للتحقق من ظهور
	 *                          النتائج التي تحتوي عليها أثناء الاختبار.
	 */
	@Step("Retrieve 'Contain Words' input WebElement")
	public WebElement containWordsInputWebElement() {
		try {
			return waitForElement(containWordsInput);
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to retrieve Contain Words input WebElement", e);
		}
	}

	/**
	 * 🔎 يسترجع عنصر WebElement الخاص بحقل إدخال "يبدأ بـ"
	 *
	 * 🔹 يُستخدم هذا الميثود لإدخال كلمة أو جذر محدد بحيث تظهر النتائج التي تبدأ
	 * بهذه الكلمة فقط.
	 *
	 * 🔎 Retrieves the WebElement for the "Start With Word" input field.
	 *
	 * 🔹 This method is used to input a word or prefix to filter results that begin
	 * with the given word.
	 *
	 * @return عنصر WebElement لحقل "يبدأ بـ" | The WebElement for the start with
	 *         word input field
	 * @throws RuntimeException إذا فشل العثور على الحقل | Throws RuntimeException
	 *                          if the field cannot be located
	 *
	 *                          📌 الهدف: تقييد النتائج بحيث تبدأ بالكلمة المحددة
	 *                          للتحقق من دقة البحث أثناء الاختبار.
	 */
	@Step("Retrieve 'Start With Word' input WebElement")
	public WebElement startWithWordInputWebElement() {
		try {
			return waitForElement(startWithWordInput);
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to retrieve Start With Word input WebElement", e);
		}
	}

	/**
	 * 🔎 يسترجع عنصر WebElement الخاص بحقل إدخال "ينتهي بـ"
	 *
	 * 🔹 يُستخدم هذا الميثود لإدخال كلمة أو لاحقة بحيث تظهر النتائج التي تنتهي بهذه
	 * الكلمة فقط.
	 *
	 * 🔎 Retrieves the WebElement for the "End With Word" input field.
	 *
	 * 🔹 This method is used to input a word or suffix to filter results that end
	 * with the given word.
	 *
	 * @return عنصر WebElement لحقل "ينتهي بـ" | The WebElement for the end with
	 *         word input field
	 * @throws RuntimeException إذا فشل العثور على الحقل | Throws RuntimeException
	 *                          if the field cannot be located
	 *
	 *                          📌 الهدف: تقييد النتائج بحيث تنتهي بالكلمة المحددة
	 *                          لضمان دقة الاستعلام أثناء الاختبار.
	 */
	@Step("Retrieve 'End With Word' input WebElement")
	public WebElement endWithWordInputWebElement() {
		try {
			return waitForElement(endWithWordInput);
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to retrieve End With Word input WebElement", e);
		}
	}

	/**
	 * ⓘ ينفذ الضغط على أيقونة المعلومات (علامة التعجب !) بجانب اسم الأداة
	 *
	 * 🔹 يُستخدم هذا الميثود للتفاعل مع أيقونة المعلومات التي عادةً تُظهر تفاصيل
	 * إضافية أو نافذة منبثقة عند الضغط عليها. يقوم الميثود بالخطوات التالية: 1.
	 * ينتظر حتى تظهر الأيقونة. 2. يسجل خصائصها لمساعدة المطورين عند التصحيح. 3.
	 * يحرك مؤشر الفأرة فوقها لتفعيل أي مؤثرات بصرية. 4. ينقر عليها باستخدام
	 * JavaScript لتجاوز مشاكل التفاعل التقليدي.
	 *
	 * ⓘ Clicks the info (!) icon beside the tool title.
	 *
	 * 🔹 This method is designed to interact with the info icon that usually shows
	 * additional details or a popup when clicked. Steps performed: 1. Wait for the
	 * icon to be visible. 2. Log its details for debugging. 3. Hover over the icon
	 * to trigger visual effects if present. 4. Click using JavaScript to bypass UI
	 * interaction issues.
	 *
	 * @throws RuntimeException إذا فشل العثور على الأيقونة أو النقر عليها Throws
	 *                          RuntimeException if the icon cannot be found or
	 *                          clicked
	 *
	 *                          📌 الهدف: ضمان فتح نافذة المعلومات المرتبطة بالأداة
	 *                          بغض النظر عن مشاكل واجهة المستخدم.
	 */
	@Step("Click on info (!) icon beside tool title")
	public void clickInfoIcon() {
		try {
			WebElement iconElement = waitForElement(infoIcon);
			logIconDetails(iconElement);

			Actions actions = new Actions(driver);
			actions.moveToElement(iconElement).pause(Duration.ofMillis(300)).perform();

			jsClick(infoIcon);
			Allure.step("✅ Clicked on info (!) icon successfully");

		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to click info (!) icon", e);
		}
	}

	/**
	 * 📑 يستخرج النص الظاهر في نافذة المعلومات المنبثقة
	 *
	 * 🔹 يُستخدم هذا الميثود لقراءة النصوص أو التفاصيل التي تظهر داخل نافذة
	 * المعلومات بعد الضغط على الأيقونة، بهدف التحقق من أن المعلومات المعروضة صحيحة.
	 *
	 * 📑 Retrieves the text displayed in the info popup dialog.
	 *
	 * 🔹 This method reads the text content displayed in the info popup dialog
	 * after clicking the info icon, ensuring the displayed details are correct.
	 *
	 * @return النص الموجود في النافذة | The popup text displayed
	 * @throws RuntimeException إذا لم يتم العثور على النافذة أو النص Throws
	 *                          RuntimeException if the popup or text cannot be
	 *                          located
	 *
	 *                          📌 الهدف: التحقق من أن نافذة المعلومات تعرض المحتوى
	 *                          المتوقع أثناء الاختبار.
	 */
	@Step("Get text from the info popup dialog")
	public String getInfoDialogText() {
		try {
			waitForElement(infoDialogText);
			return driver.findElement(infoDialogText).getText().trim();
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to retrieve text from info popup", e);
		}
	}

	/**
	 * ❎ يغلق نافذة المعلومات المنبثقة
	 *
	 * 🔹 يُستخدم هذا الميثود للنقر على زر الإغلاق الخاص بنافذة التفاصيل بعد
	 * الانتهاء من قراءة أو التحقق من محتواها.
	 *
	 * ❎ Closes the info popup dialog.
	 *
	 * 🔹 This method clicks on the close button of the info popup dialog once the
	 * test is done reading or verifying its content.
	 *
	 * @throws RuntimeException إذا لم يتم العثور على زر الإغلاق أو فشل التفاعل معه
	 *                          Throws RuntimeException if the close button cannot
	 *                          be found or clicked
	 *
	 *                          📌 الهدف: التأكد من إغلاق النافذة لإعادة الصفحة إلى
	 *                          حالتها الطبيعية أثناء الاختبار.
	 */
	@Step("Close the info popup dialog")
	public void closeInfoDialog() {
		try {
			waitAndClick(closeDialogButton);
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to close the info popup dialog", e);
		}
	}

	/**
	 * 🧹 يتحقق مما إذا كان مربع اختيار "استثناء علامات الترقيم" مُفعلًا
	 *
	 * 🔹 يُستخدم هذا الميثود لمعرفة حالة الـ Checkbox بعد النقر عليه، حيث أن قيمته
	 * يمكن أن تتغير عبر الخاصية `aria-checked` أو عبر كلاس الـ CSS. يفحص أولًا قيمة
	 * `aria-checked`، وإذا لم تكن واضحة، يستخدم بدائل مثل التحقق من الكلاسات
	 * المرئية (`p-highlight` أو `p-checkbox-checked`).
	 *
	 * 🧹 Checks whether the "Exclude Punctuation" checkbox is enabled.
	 *
	 * 🔹 This method determines whether the checkbox is active after clicking. It
	 * inspects the `aria-checked` attribute first, and if not reliable, it checks
	 * visual class states (`p-highlight`, `p-checkbox-checked`).
	 *
	 * @return true إذا كان المربع مفعّلًا، false إذا لم يكن كذلك true if checkbox
	 *         is checked, false otherwise
	 * 
	 *         📌 الهدف: التأكد من أن تفعيل خيار "استثناء علامات الترقيم" تم بنجاح
	 *         قبل متابعة الاختبار.
	 */
	public boolean isExcludePunctuationChecked() {
		try {
			WebElement input = driver.findElement(excludePunctInput);
			String aria = input.getAttribute("aria-checked");
			if ("true".equalsIgnoreCase(aria))
				return true;

			boolean hasCheckedOnRoot = !driver.findElements(excludePunctRootChecked).isEmpty();
			boolean hasHighlightOnBox = !driver.findElements(excludePunctBoxHighlighted).isEmpty();
			return hasCheckedOnRoot || hasHighlightOnBox;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	/**
	 * 🧹 ينقر على مربع اختيار "استثناء علامات الترقيم" وينتظر حتى يتم تفعيله
	 *
	 * 🔹 يقوم الميثود بالخطوات التالية: 1. انتظار أن يصبح العنصر قابلاً للنقر. 2.
	 * تمرير الصفحة إلى مكانه. 3. محاولة النقر بشكل مباشر، أو باستخدام JavaScript
	 * إذا كان هناك حجب. 4. انتظار أن يصبح محددًا بالفعل (aria-checked / CSS
	 * classes).
	 *
	 * 🧹 Clicks on the "Exclude Punctuation" checkbox and waits for it to become
	 * checked.
	 *
	 * 🔹 Steps performed: 1. Wait until the element is clickable. 2. Scroll into
	 * view. 3. Click directly or via JavaScript fallback. 4. Wait until checkbox
	 * state is marked as checked.
	 *
	 * @throws RuntimeException إذا لم يتم تفعيل المربع بعد النقر أو عند حدوث خطأ
	 *                          Throws RuntimeException if checkbox fails to become
	 *                          checked
	 *
	 *                          📌 الهدف: ضمان تفعيل خيار استثناء علامات الترقيم
	 *                          أثناء الاختبار.
	 */
	@Step("🧹 Enable 'Exclude Punctuation' checkbox")
	public void clickExcludePunctuationCheckBox() {
		WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			WebElement box = w.until(ExpectedConditions.elementToBeClickable(excludePunctCheckBox));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", box);
			try {
				box.click();
			} catch (WebDriverException e) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", box);
			}
			w.until(d -> isExcludePunctuationChecked());

			Allure.step("🧹 'Exclude Punctuation' is now checked");
			System.out.println("🧹 'Exclude Punctuation' is now checked");
		} catch (TimeoutException te) {
			throw new RuntimeException("❌ Checkbox did not become checked after clicking", te);
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to click the Exclude Punctuation CheckBox", e);
		}
	}

	/**
	 * 🎯 يُعيد اللوكيتر الخاص بنص نافذة المعلومات
	 *
	 * 🔹 هذا الميثود لا يتعامل مع العنصر مباشرة، وإنما يُعيد كائن By للعنصر ليمكن
	 * استخدامه في الانتظار أو التحقق في ميثودات أخرى.
	 *
	 * 🎯 Returns the By locator for the info popup text element.
	 *
	 * 🔹 This method does not interact with the element itself but returns its By
	 * locator for use in waits or other checks.
	 *
	 * @return كائن By الخاص بالعنصر | The By locator of the info dialog text
	 *
	 *         📌 الهدف: توفير مرجع Locator لإعادة استخدامه في أماكن مختلفة دون
	 *         تكرار.
	 */
	public By getInfoDialogTextLocator() {
		return infoDialogText;
	}

	/**
	 * 🔢 يضغط على زر فلتر عدد الكلمات (1 أو 2 أو 3)
	 *
	 * 🔹 يقوم هذا الميثود باختيار زر الفلترة المناسب بناءً على العدد المطلوب: - 1 →
	 * كلمة واحدة - 2 → كلمتان - 3 → ثلاث كلمات ثم ينفذ الضغط باستخدام الميثود
	 * المساعد `waitAndClick`.
	 *
	 * 🔢 Clicks the word count filter button (1, 2, or 3).
	 *
	 * 🔹 This method selects the appropriate filter button based on the count: - 1
	 * → One word - 2 → Two words - 3 → Three words Then performs a click using the
	 * helper method `waitAndClick`.
	 *
	 * @param count عدد الكلمات المطلوب (1، 2، 3) | Word count filter (1, 2, or 3)
	 * @throws RuntimeException إذا كان العدد غير مدعوم أو فشل الضغط Throws
	 *                          RuntimeException if count is unsupported or click
	 *                          fails
	 *
	 *                          📌 الهدف: التحكم في فلترة النتائج حسب عدد الكلمات
	 *                          المطلوبة في البحث.
	 */
	@Step("🔢 Clicked word count filter: {count}")
	public void selectWordCountFilter(int count) {
		try {
			By filterButton;
			switch (count) {
			case 1 -> filterButton = oneWordFilterButton;
			case 2 -> filterButton = twoWordsFilterButton;
			case 3 -> filterButton = threeWordsFilterButton;
			default -> throw new IllegalArgumentException("Unsupported count: " + count);
			}
			waitAndClick(filterButton);
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to select word count filter: " + count, e);
		}
	}

	/**
	 * 📝 يجلب جميع الجُمل الظاهرة في العمود الأول (كما تظهر في الواجهة)
	 *
	 * 🔹 يُستخدم هذا الميثود عند التحقق من نصوص النتائج (مثل بداية/نهاية الجملة أو
	 * التطبيع)، وينتظر أولًا حتى تظهر أي خلية تحمل نصًا غير فارغ، ثم يعيد قائمة
	 * النصوص منظّفة (trim).
	 *
	 * 📝 Fetches all visible sentences from the first column (as shown in UI).
	 *
	 * 🔹 The method waits until at least one non-empty cell is visible, then
	 * returns a trimmed list of the texts for assertions/validations.
	 *
	 * @return قائمة النصوص من العمود الأول | A list of sentences from the first
	 *         column
	 * @throws RuntimeException إذا لم يُعثر على أي خلايا أو فشل الاستخراج Throws
	 *                          RuntimeException if no cells found or extraction
	 *                          fails
	 *
	 *                          📌 الهدف: توفير مصدر موثوق لنصوص النتائج لعمليات
	 *                          التحقق اللاحقة.
	 */
	@Step("📥 Fetched all visible sentences from first column")
	public List<String> getFirstColumnSentences() {
		try {
			// ⏳ انتظر حتى تظهر أي خلية تحتوي نصًا غير فارغ
			new WebDriverWait(driver, Duration.ofSeconds(15)).until(driver -> {
				List<WebElement> cells = driver.findElements(firstColumnCells);
				return cells.stream().map(WebElement::getText).map(String::trim).anyMatch(text -> !text.isEmpty());
			});

			List<WebElement> cells = driver.findElements(firstColumnCells);
			if (cells.isEmpty())
				throw new RuntimeException("❌ No first-column cells were found!");

			List<String> texts = cells.stream().map(WebElement::getText).map(String::trim)
					.filter(text -> !text.isEmpty()).toList();

			Allure.step("🔎 First-column sentences count: " + texts.size());
			System.out.println("🔎 Fetched texts from first column:");
			texts.forEach(System.out::println);

			return texts;

		} catch (Exception e) {
			Allure.step("❌ Failed to retrieve first column texts: " + e.getMessage());
			throw new RuntimeException("❌ Failed to retrieve first column texts", e);
		}
	}

	/**
	 * 🔢 يجلب كل التكرارات الظاهرة من العمود الثاني (بعد تطبيع الأرقام)
	 *
	 * 🔹 ينتظر ظهور أي خلية تحتوي رقمًا (مع دعم الأرقام العربية/الفارسية)، ثم يطبع
	 * الأرقام المستخرجة كقائمة أعداد صحيحة. يتجاهل أي خلية لا يمكن تحويلها بأمان.
	 *
	 * 🔢 Fetches all visible frequencies from the second column (with digits
	 * normalization).
	 *
	 * 🔹 Waits until at least one numeric cell appears (Arabic/Eastern digits
	 * supported), then returns the parsed integers. Any unparsable cells are
	 * skipped safely.
	 *
	 * @return قائمة الأعداد (التكرارات) | A list of integer frequencies
	 * @throws RuntimeException إذا لم تُعثر خلايا أو لم تُستخلص أرقام قابلة للتحويل
	 *                          Throws RuntimeException if no cells or no parseable
	 *                          digits are found
	 *
	 *                          📌 الهدف: توفير قيم تكرار صحيحة لاستخدامها في التحقق
	 *                          من الحدود (min/max) والفلترة.
	 */
	@Step("📥 Fetched all visible frequencies from second column")
	public List<Integer> getSecondColumnFrequencies() {
		try {
			// ⏳ انتظر حتى تحتوي أي خلية في العمود الثاني على أرقام (بعد التطبيع)
			new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> {
				List<WebElement> cells = driver.findElements(secondColumnCells);
				return cells.stream().map(WebElement::getText).map(this::normalizeDigitsAndTrim)
						.anyMatch(t -> t.matches(".*\\d+.*"));
			});

			List<WebElement> cells = driver.findElements(secondColumnCells);
			if (cells.isEmpty())
				throw new RuntimeException("❌ No second-column cells were found!");

			List<Integer> numbers = new ArrayList<>();
			for (WebElement cell : cells) {
				String raw = cell.getText();
				String normalized = normalizeDigitsAndTrim(raw); // تطبيع الأرقام والمسافات
				String digitsOnly = extractDigits(normalized); // إبقاء الأرقام فقط

				if (!digitsOnly.isEmpty()) {
					try {
						numbers.add(Integer.parseInt(digitsOnly));
					} catch (NumberFormatException nfe) {
						System.out.println("⚠️ Unable to parse cell text: [" + raw + "] -> [" + digitsOnly + "]");
					}
				}
			}

			if (numbers.isEmpty()) {
				throw new RuntimeException("❌ Found second-column cells but none contained parseable digits.");
			}

			Allure.step("🔢 Second-column frequencies count: " + numbers.size());
			System.out.println("🔢 Fetched frequencies from second column:");
			numbers.forEach(System.out::println);
			return numbers;

		} catch (Exception e) {
			Allure.step("❌ Failed to retrieve second column frequencies: " + e.getMessage());
			throw new RuntimeException("❌ Failed to retrieve second column frequencies", e);
		}
	}

	/**
	 * 🔧 يطبع المسافات ويحوّل الأرقام العربية/الفارسية إلى (0-9) ويزيل NBSP
	 * والفواصل
	 *
	 * 🔹 يقوم بتطبيع النص بحيث تُزال المسافة غير القابلة للكسر (NBSP) ورموز الآلاف
	 * العربية/اللاتينية، كما تُحوّل الأرقام العربية/الفارسية إلى أرقام لاتينية 0-9،
	 * ثم يُعاد النص بعد trim.
	 *
	 * 🔧 Normalizes text by trimming, removing NBSP & thousands separators, and
	 * converting Arabic/Eastern Arabic digits to Latin (0-9).
	 *
	 * @param s النص الأصلي | The raw string value
	 * @return النص بعد التطبيع | The normalized string
	 *
	 *         📌 الهدف: تسهيل تحليل الأرقام القادمة من الـ UI بصيغ متعددة.
	 */
	private String normalizeDigitsAndTrim(String s) {
		if (s == null)
			return "";
		String t = s.replace('\u00A0', ' ') // NBSP → space
				.replace("٬", "") // Arabic thousands separator
				.replace(",", "") // Latin comma (just in case)
				.trim();

		StringBuilder sb = new StringBuilder(t.length());
		for (int i = 0; i < t.length(); i++) {
			char c = t.charAt(i);
			// Arabic-Indic digits ٠١٢٣٤٥٦٧٨٩
			if (c >= '٠' && c <= '٩') {
				sb.append((char) ('0' + (c - '٠')));
			}
			// Eastern Arabic-Indic digits ۰۱۲۳۴۵۶۷۸۹
			else if (c >= '۰' && c <= '۹') {
				sb.append((char) ('0' + (c - '۰')));
			} else {
				sb.append(c);
			}
		}
		return sb.toString().trim();
	}

	/**
	 * 🔢 يستخرج الأرقام فقط من النص بعد التطبيع
	 *
	 * 🔹 يُستخدم هذا الميثود لتنظيف النصوص المُستخرجة من الأعمدة أو الحقول وإبقاء
	 * الأرقام اللاتينية (0–9) فقط، للتأكد من أن عمليات التحويل إلى Integer أو
	 * التحقق من القيم ستتم بنجاح.
	 *
	 * 🔢 Extracts only numeric digits from a string after normalization.
	 *
	 * 🔹 This method ensures that only Latin digits (0–9) remain, making the text
	 * safe for integer parsing and validations.
	 *
	 * @param s النص الخام (Raw string input)
	 * @return النص المحتوي على الأرقام فقط | String containing only digits
	 *
	 *         📌 الهدف: دعم الميثودات الأخرى مثل getSecondColumnFrequencies عند
	 *         تحليل القيم العددية من واجهة المستخدم.
	 */
	private String extractDigits(String s) {
		return s.replaceAll("[^0-9]", "");
	}

	/**
	 * 🔍 الضغط على زر البحث مع منع إعادة تحميل الصفحة عند إرسال الفورم
	 *
	 * 🔹 يُستخدم هذا الميثود لضمان تنفيذ البحث في واجهة الأداة بدون إعادة تحميل
	 * الصفحة، وذلك عبر حقن JavaScript لمنع سلوك form الافتراضي.
	 *
	 * 🔍 Clicks the search button while preventing the form submission from
	 * reloading the page.
	 *
	 * 🔹 The method injects JS to stop form reload, waits until the button is
	 * clickable, and clicks using JavaScript (fallbacks to WebDriver click if
	 * needed).
	 *
	 * @throws RuntimeException إذا فشل النقر باستخدام جميع الطرق Throws
	 *                          RuntimeException if all click attempts fail
	 *
	 *                          📌 الهدف: تحسين استقرار الاختبار عند الضغط على زر
	 *                          البحث.
	 */
	@Step("🔍 Click search button with form submission prevention")
	public void clickSearchButton() {
		try {
			WebElement button = waitForElement(searchButton);

			((JavascriptExecutor) driver).executeScript(
					"if(arguments[0].form) arguments[0].form.addEventListener('submit', function(e){ e.preventDefault(); });",
					button);

			wait.until(ExpectedConditions.elementToBeClickable(button));
			jsClick(searchButton);

		} catch (Exception jsClickError) {
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
	 * 🔹 ينتظر حتى تظهر النتائج في الجدول وتحتوي على نصوص غير فارغة، ثم يعيد قائمة
	 * بكل الكلمات/النصوص المستخرجة بعد التصفية من الفراغات.
	 *
	 * 📑 Retrieves the list of result words displayed in the search table.
	 *
	 * 🔹 Waits until the results are visible with non-empty text, then collects and
	 * returns them as a list of strings.
	 *
	 * @return قائمة نصوص النتائج | A list of result words from the search table
	 * @throws RuntimeException إذا فشل في استخراج النتائج Throws RuntimeException
	 *                          if extraction fails
	 *
	 *                          📌 الهدف: التحقق من أن البحث أعاد نتائج صحيحة يمكن
	 *                          مقارنتها في الاختبارات.
	 */
	@Step("📑 Get search result words")
	public List<String> getSearchResultWords() {
		try {
			wait.until(driver -> {
				List<WebElement> results = driver.findElements(resultRow);
				boolean hasValidText = results.stream().anyMatch(el -> !el.getText().trim().isEmpty());
				System.out.println("🔁 Polling results - Size: " + results.size() + ", Valid Text: " + hasValidText);
				return hasValidText;
			});

			List<WebElement> resultElements = driver.findElements(resultRow);
			System.out.println("📌 Number of results found: " + resultElements.size());

			return resultElements.stream().map(WebElement::getText)
					.peek(text -> System.out.println("📎 Result text: " + text)).filter(text -> !text.isBlank())
					.collect(Collectors.toList());

		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to extract search result words", e);
		}
	}

	/**
	 * 📈 زيادة قيمة الحقل العددي باستخدام JavaScript
	 *
	 * 🔹 ينتظر ظهور عنصر الإدخال (input)، يجلب قيمته الحالية، ثم يزيدها بمقدار محدد
	 * ويضبطها مجددًا باستخدام JavaScript.
	 *
	 * 📈 Increases the numeric input value using JavaScript.
	 *
	 * 🔹 Waits for the input element to be visible, retrieves its current value,
	 * increments it by the specified amount, and sets the updated value back using
	 * JS.
	 *
	 * @param inputLocator محدد عنصر الإدخال | Locator of the input element
	 * @param amount       مقدار الزيادة المطلوب | Amount to increase
	 * @throws RuntimeException إذا فشل تحديث القيمة Throws RuntimeException if
	 *                          update fails
	 *
	 *                          📌 الهدف: محاكاة ضغط زر (+) في الحقول الرقمية ضمن
	 *                          واجهة الأداة.
	 */
	@Step("📈 Increase value by {1} using JavaScript")
	public void increaseValue(By inputLocator, int amount) {
		try {
			WebElement input = waitForElement(inputLocator);
			int current = Integer.parseInt(input.getAttribute("value"));
			int updated = current + amount;
			setValueViaJS(input, updated);
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to increase value", e);
		}
	}

	/**
	 * 📉 تقليل قيمة الحقل العددي باستخدام JavaScript
	 *
	 * 🔹 ينتظر ظهور عنصر الإدخال (input)، يجلب قيمته الحالية، ثم ينقصها بمقدار محدد
	 * ويضبطها مجددًا باستخدام JavaScript.
	 *
	 * 📉 Decreases the numeric input value using JavaScript.
	 *
	 * 🔹 Waits for the input element, retrieves its current value, subtracts the
	 * specified amount, and applies it back via JS.
	 *
	 * @param inputLocator محدد عنصر الإدخال | Locator of the input element
	 * @param amount       مقدار النقص المطلوب | Amount to decrease
	 * @throws RuntimeException إذا فشل تحديث القيمة Throws RuntimeException if
	 *                          update fails
	 *
	 *                          📌 الهدف: محاكاة ضغط زر (–) في الحقول الرقمية ضمن
	 *                          واجهة الأداة.
	 */
	@Step("📉 Decrease value by {1} using JavaScript")
	public void decreaseValue(By inputLocator, int amount) {
		try {
			WebElement input = waitForElement(inputLocator);
			int current = Integer.parseInt(input.getAttribute("value"));
			int updated = current - amount;
			setValueViaJS(input, updated);
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to decrease value", e);
		}
	}

	/**
	 * 📝 ضبط قيمة الحقل العددي باستخدام JavaScript
	 *
	 * 🔹 يُستخدم لتحديث قيمة حقل إدخال رقمي مباشرةً عبر JavaScript مع إطلاق أحداث
	 * `input` و `change` لضمان استجابة الواجهة.
	 *
	 * 📝 Sets the numeric input value via JavaScript.
	 *
	 * 🔹 Updates the input element directly with the provided value and triggers
	 * `input` and `change` events to ensure UI reacts properly.
	 *
	 * @param input عنصر الإدخال المستهدف | The WebElement input field
	 * @param value القيمة الجديدة المطلوب ضبطها | The new value to set
	 *
	 *              📌 الهدف: محاكاة إدخال المستخدم بشكل أدق من مجرد setAttribute.
	 */
	@Step("📝 Set numeric input value via JavaScript")
	public void setValueViaJS(WebElement input, int value) {
		((JavascriptExecutor) driver).executeScript("""
				    arguments[0].value = arguments[1];
				    arguments[0].dispatchEvent(new Event('input', { bubbles: true }));
				    arguments[0].dispatchEvent(new Event('change', { bubbles: true }));
				""", input, String.valueOf(value));
	}

	/**
	 * 🔢 جلب قيمة حقل "الحد الأدنى للتكرار"
	 *
	 * 🔹 ينتظر حتى يظهر الحقل، يستخرج قيمة خاصية `value`، ويحوّلها إلى عدد صحيح
	 * (int).
	 *
	 * 🔢 Retrieves the value of the "Minimum Frequency" input field.
	 *
	 * 🔹 Waits for the input element, extracts its `value` attribute, and parses it
	 * into an integer.
	 *
	 * @return القيمة العددية للحقل | The integer value of the minimum frequency
	 *         input
	 *
	 *         📌 الهدف: التحقق من أن قيمة الحد الأدنى للتكرار تتغير حسب التفاعل
	 *         المطلوب.
	 */
	@Step("🔢 Retrieved min frequency input value")
	public int getMinFreqValue() {
		String value = waitForElement(minFreqInput).getAttribute("value");
		return Integer.parseInt(value.trim());
	}

	/**
	 * 🔢 جلب قيمة الحد الأقصى للتكرار من حقل الإدخال
	 *
	 * 🔹 ينتظر ظهور الحقل، يستخرج القيمة من خاصية `value`، ويحوّلها إلى عدد صحيح.
	 *
	 * 🔢 Retrieves the maximum frequency value from the input field.
	 *
	 * 🔹 Waits for the element, extracts its `value` attribute, and parses it into
	 * an integer.
	 *
	 * @return القيمة العددية للحد الأقصى للتكرار | The integer value of max
	 *         frequency input
	 * @throws RuntimeException إذا فشل في قراءة القيمة أو تحويلها Throws
	 *                          RuntimeException if parsing fails
	 *
	 *                          📌 الهدف: التحقق من أن المستخدم أو الاختبار غيّر
	 *                          الحد الأقصى للتكرار بشكل صحيح.
	 */
	@Step("🔢 Retrieved max frequency input value")
	public int getMaxFreqValue() {
		String value = waitForElement(maxFreqInput).getAttribute("value");
		return Integer.parseInt(value.trim());
	}

	/**
	 * 🎯 يُعيد لوكيتر الجدول
	 *
	 * 🔹 يوفر كائن `By` المستخدم لتحديد الجدول الرئيسي لنتائج البحث.
	 *
	 * 🎯 Returns the locator for the results table.
	 *
	 * 🔹 Provides the `By` object representing the search results table element.
	 *
	 * @return لوكيتر الجدول | The By locator for the results table
	 */
	public By getResultsTable() {
		return resultsTable;
	}

	/**
	 * 🔢 يُعيد لوكيتر حقل "الحد الأدنى للتكرار"
	 *
	 * 🔢 Returns the locator for the minimum frequency input field.
	 *
	 * @return الكائن By الخاص بالعنصر | The By locator of min frequency input
	 */
	public By getMinFreqInput() {
		return minFreqInput;
	}

	/**
	 * 🔢 يُعيد لوكيتر حقل "الحد الأقصى للتكرار"
	 *
	 * 🔢 Returns the locator for the maximum frequency input field.
	 *
	 * @return الكائن By الخاص بالعنصر | The By locator of max frequency input
	 */
	public By getMaxFreqInput() {
		return maxFreqInput;
	}

	/**
	 * ✏️ ضبط قيمة فلتر "لا يحتوي على"
	 *
	 * 🔹 ينتظر الحقل، يفرغه من أي قيمة سابقة، ثم يكتب القيمة الجديدة.
	 *
	 * ✏️ Sets the "Does Not Contain" filter value.
	 *
	 * 🔹 Waits for the input field, clears any existing value, and types the new
	 * one.
	 *
	 * @param value النص الذي يجب استبعاده | The text value to exclude
	 * @throws RuntimeException إذا فشل إدخال القيمة Throws RuntimeException if
	 *                          setting fails
	 *
	 *                          📌 الهدف: تطبيق فلتر يمنع ظهور كلمات أو نتائج تحتوي
	 *                          على النص المحدد.
	 */
	@Step("✏️ Set 'Does Not Contain' filter to: {0}")
	public void setDoesNotContainFilter(String value) {
		try {
			WebElement input = waitForElement(excludeWordsInput);
			input.clear();
			input.sendKeys(value);
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to set 'Does Not Contain' filter", e);
		}
	}

	/**
	 * ✏️ ضبط قيمة فلتر "يحتوي على"
	 *
	 * 🔹 ينتظر الحقل، يفرغه من أي قيمة سابقة، ثم يكتب القيمة الجديدة.
	 *
	 * ✏️ Sets the "Contain" filter value.
	 *
	 * 🔹 Waits for the input field, clears any existing value, and types the new
	 * one.
	 *
	 * @param value النص المطلوب أن تحتويه النتائج | The text value that results
	 *              must contain
	 * @throws RuntimeException إذا فشل إدخال القيمة Throws RuntimeException if
	 *                          setting fails
	 *
	 *                          📌 الهدف: تطبيق فلتر لإظهار النتائج التي تحتوي على
	 *                          النص المحدد فقط.
	 */
	@Step("✏️ Set 'Contain' filter to: {0}")
	public void setContainWordsFilter(String value) {
		try {
			WebElement input = waitForElement(containWordsInput);
			input.clear();
			input.sendKeys(value);
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to set 'Contain' filter", e);
		}
	}

	/**
	 * ✏️ ضبط قيمة فلتر "يبدأ بـ"
	 *
	 * 🔹 ينتظر الحقل، يفرغه من أي قيمة سابقة، ثم يكتب القيمة الجديدة.
	 *
	 * ✏️ Sets the "Start With Word" filter value.
	 *
	 * 🔹 Waits for the input field, clears any existing value, and types the new
	 * one.
	 *
	 * @param value النص المطلوب أن تبدأ به النتائج | The text value that results
	 *              must start with
	 * @throws RuntimeException إذا فشل إدخال القيمة Throws RuntimeException if
	 *                          setting fails
	 *
	 *                          📌 الهدف: حصر النتائج بحيث تبدأ بالكلمة أو المقطع
	 *                          المحدد.
	 */
	@Step("✏️ Set 'Start With Word' filter to: {0}")
	public void setStartWithWordFilter(String value) {
		try {
			WebElement input = waitForElement(startWithWordInput);
			input.clear();
			input.sendKeys(value);
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to set 'Start With Word' filter", e);
		}
	}

	/**
	 * ✏️ ضبط قيمة فلتر "ينتهي بـ"
	 *
	 * 🔹 ينتظر الحقل، يفرغه من أي قيمة سابقة، ثم يكتب القيمة الجديدة.
	 *
	 * ✏️ Sets the "End With Word" filter value.
	 *
	 * 🔹 Waits for the input field, clears any existing value, and types the new
	 * one.
	 *
	 * @param value النص المطلوب أن تنتهي به النتائج | The text value that results
	 *              must end with
	 * @throws RuntimeException إذا فشل إدخال القيمة Throws RuntimeException if
	 *                          setting fails
	 *
	 *                          📌 الهدف: حصر النتائج بحيث تنتهي بالكلمة أو المقطع
	 *                          المحدد.
	 */
	@Step("✏️ Set 'End With Word' filter to: {0}")
	public void setEndWithWordFilter(String value) {
		try {
			WebElement input = waitForElement(endWithWordInput);
			input.clear();
			input.sendKeys(value);
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to set 'End With Word' filter", e);
		}
	}

	/**
	 * ℹ️ التحقق من ظهور رسالة "لا توجد بيانات"
	 *
	 * 🔹 يحاول العثور على عنصر رسالة "لا توجد بيانات" مباشرة بدون انتظار. إذا وُجد
	 * وتبيّن أنه ظاهر → يرجع true. 🔹 إذا لم يتم العثور على العنصر، يرجع false.
	 *
	 * ℹ️ Checks if the "No data" message is displayed.
	 *
	 * 🔹 Attempts to locate the "No data" message immediately without waiting.
	 * Returns true if visible, false otherwise.
	 *
	 * @return true إذا ظهرت الرسالة | true if the "No data" message is displayed,
	 *         false otherwise
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
	 * ℹ️ التحقق من ظهور الجدول
	 *
	 * 🔹 ينتظر ظهور عنصر الجدول (resultsTable) ثم يرجع true إذا كان ظاهرًا. 🔹 إذا
	 * لم يظهر خلال المهلة المحددة → يرجع false.
	 *
	 * ℹ️ Checks if the results table is displayed.
	 *
	 * 🔹 Waits for the table element (resultsTable) to be visible and returns true
	 * if it is. 🔹 Returns false if it does not appear within the timeout.
	 *
	 * @return true إذا ظهر الجدول | true if the results table is displayed, false
	 *         otherwise
	 */
	public boolean isResultTableDisplayed() {
		try {
			return waitForElement(resultsTable).isDisplayed();
		} catch (TimeoutException e) {
			return false; // لم يظهر الجدول
		}
	}

	/**
	 * ⏳ الانتظار حتى تظهر واحدة من الحالات التالية: 1️⃣ الجدول يحتوي على نتائج. 2️⃣
	 * رسالة "لا توجد بيانات". 3️⃣ رسالة خطأ (Error Toast).
	 *
	 * ⏳ Waits until one of the following appears: 1️⃣ Results table is displayed.
	 * 2️⃣ "No data" message is shown. 3️⃣ Error toast message is visible.
	 *
	 * @return true إذا تحقق أي شرط من الشروط السابقة | true if any of the above
	 *         conditions is met
	 *
	 *         📌 الهدف: ضمان أن النظام يعرض نتيجة واضحة (نتائج/لا بيانات/خطأ) بدل
	 *         البقاء في حالة انتظار.
	 */
	public boolean waitForResultsOrNoDataMessage() {
		installToastSpyIfNeeded();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
		wait.pollingEvery(Duration.ofMillis(200)).ignoring(NoSuchElementException.class)
				.ignoring(StaleElementReferenceException.class).ignoring(ElementNotInteractableException.class);

		return wait.until(d -> {
			System.out.println("⏳ Polling... table / no-data / error-toast");

			// (1) تحقق من رسالة الخطأ عبر "spy" إذا ظهرت ثم اختفت
			try {
				String toast = getLastErrorToastWithinMs(5000); // آخر 5 ثوانٍ
				if (toast != null && !toast.isBlank()) {
					Allure.step("🚨 Error toast (spy): " + toast);
					System.out.println("🚨 Error toast (spy): " + toast);
					return true;
				}
			} catch (Exception e) {
				System.out.println("🔸 spy check: " + e.getMessage());
			}

			// (2) تحقق من وجود رسالة الخطأ مباشرة (قد تكون مازالت معروضة)
			try {
				List<WebElement> now = d.findElements(By.cssSelector("p-toast .p-toast-message-error"));
				if (!now.isEmpty() && now.get(0).isDisplayed())
					return true;
			} catch (Exception ignored) {
			}

			// (3) تحقق من الجدول
			try {
				if (isResultTableDisplayed())
					return true;
			} catch (Exception e) {
				System.out.println("🔸 table check: " + e.getMessage());
			}

			// (4) تحقق من رسالة "لا توجد بيانات"
			try {
				if (isNoResultsMessageDisplayed())
					return true;
			} catch (Exception e) {
				System.out.println("🔸 no-data check: " + e.getMessage());
			}

			return false; // استمر في الانتظار
		});
	}

	/**
	 * 🚨 التحقق من ظهور رسالة خطأ (Error Toast)
	 *
	 * 🔹 يحاول العثور على عنصر رسالة الخطأ (errorToastRoot) مباشرة. 🔹 يرجع true
	 * إذا كانت مرئية، و false إذا لم يتم العثور عليها.
	 *
	 * 🚨 Checks if an error message (Error Toast) is displayed.
	 *
	 * 🔹 Locates the error toast element (errorToastRoot) and returns true if
	 * visible. 🔹 Returns false if not found.
	 *
	 * @return true إذا ظهرت رسالة الخطأ | true if the error message is displayed,
	 *         false otherwise
	 */
	public boolean isErrorMessageDisplayed() {
		try {
			WebElement errorMsg = driver.findElement(errorToastRoot);
			return errorMsg.isDisplayed();
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	/**
	 * 🚨 جلب آخر رسالة خطأ (Error Toast) خلال فترة زمنية محددة
	 *
	 * 🔹 يستخدم كود JavaScript للوصول إلى النص المُلتقط بواسطة "toastSpy". 🔹 إذا
	 * كان آخر Toast من النوع "خطأ" وظهر خلال maxAgeMs → يرجع نصه. 🔹 إذا لم يوجد →
	 * يرجع null.
	 *
	 * 🚨 Retrieves the last error toast message within a given timeframe.
	 *
	 * 🔹 Executes JavaScript to read the stored "toastSpy" state. 🔹 If the last
	 * toast is of type "error" and appeared within maxAgeMs → returns its text. 🔹
	 * Returns null if none is found.
	 *
	 * @param maxAgeMs الحد الأقصى لعمر الرسالة بالمللي ثانية | Maximum allowed age
	 *                 of the toast in milliseconds
	 * @return نص رسالة الخطأ إذا وُجدت | The error toast text, or null if none
	 */
	public String getLastErrorToastWithinMs(long maxAgeMs) {
		Object res = ((JavascriptExecutor) driver).executeScript(
				"return (window.__toastSpy && (Date.now()-window.__toastSpy.ts) <= arguments[0] "
						+ "        && window.__toastSpy.lastType==='error') ? window.__toastSpy.lastText : null;",
				maxAgeMs);
		return (res == null) ? null : res.toString();
	}

	/**
	 * 👀 تثبيت "Toast Spy" لمراقبة رسائل الخطأ
	 *
	 * 🔹 يضيف كائن JavaScript (window.__toastSpy) لمراقبة تغييرات عنصر p-toast. 🔹
	 * عند ظهور رسالة خطأ جديدة → يخزن نوعها، نصها، والتوقيت. 🔹 يُستخدم لاحقًا
	 * لاكتشاف رسائل الخطأ حتى بعد اختفائها.
	 *
	 * 👀 Installs a "Toast Spy" in the browser to watch for error messages.
	 *
	 * 🔹 Adds a JavaScript object (window.__toastSpy) and a MutationObserver to
	 * track error toasts. 🔹 Captures the last error type, text, and timestamp. 🔹
	 * Useful for detecting transient error toasts that may disappear quickly.
	 *
	 * 📌 الهدف: تمكين الاختبارات من اكتشاف الأخطاء حتى لو اختفى الـ toast بسرعة.
	 */
	public void installToastSpyIfNeeded() {
		String script = "if (!window.__toastSpy) {" + "  window.__toastSpy = { lastType:null, lastText:null, ts:0 };"
				+ "  const root = document.querySelector('p-toast');" + "  if (root) {"
				+ "    const obs = new MutationObserver(() => {"
				+ "      const err = root.querySelector('.p-toast-message-error .p-toast-detail');"
				+ "      if (err && err.textContent && err.offsetParent !== null) {" + // مرئي الآن
				"        window.__toastSpy.lastType = 'error';"
				+ "        window.__toastSpy.lastText = err.textContent.trim();"
				+ "        window.__toastSpy.ts = Date.now();" + "      }" + "    });"
				+ "    obs.observe(root, { childList:true, subtree:true });" + "    window.__toastSpyObserver = obs;"
				+ "  }" + "}";
		((JavascriptExecutor) driver).executeScript(script);
		Allure.step("👀 Toast spy installed");
		System.out.println("👀 Toast spy installed");
	}

	/**
	 * 📥 الضغط على زر التصدير (Export) لتنزيل نتائج البحث
	 *
	 * 🔹 ينفذ نقرة على زر التصدير باستخدام الميثود waitAndClick. 🔹 في حال النجاح →
	 * يطبع رسالة نجاح. 🔹 في حال الفشل → يرمي RuntimeException.
	 *
	 * 📥 Clicks the export button to download search results.
	 *
	 * 🔹 Uses waitAndClick to ensure the button is clickable before clicking. 🔹
	 * Logs success if clicked. 🔹 Throws RuntimeException if the action fails.
	 *
	 * @throws RuntimeException إذا فشل النقر على زر التصدير | Throws
	 *                          RuntimeException if export button click fails
	 */
	@Step("📥 Click on the export button")
	public void clickExportButton() {
		try {
			waitAndClick(exportButton);
			System.out.println("📥 Export button clicked");
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to click export button", e);
		}
	}

	/**
	 * 👀 التحقق من ظهور زر التصدير في الصفحة
	 *
	 * 🔹 يُستخدم هذا الميثود للتأكد من أن زر التصدير (Export) موجود ومرئي قبل
	 * محاولة التفاعل معه.
	 *
	 * 👀 Checks whether the export button is currently visible on the page.
	 *
	 * 🔹 Useful to confirm that the export functionality is available before
	 * interacting with it.
	 *
	 * @return true إذا كان الزر ظاهرًا | true if the export button is visible
	 * @throws RuntimeException إذا فشل التحقق من الظهور | Throws RuntimeException
	 *                          if visibility check fails
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
	 * 📁 التحقق من تنزيل ملف بتنسيق محدد داخل مجلد التنزيلات
	 *
	 * 🔹 يتحقق مما إذا كان هناك ملف في مجلد التنزيلات ينتهي بالامتداد المطلوب
	 * (.xlsx أو .csv).
	 *
	 * 📁 Verifies whether a file with the given extension exists in the Downloads
	 * folder.
	 *
	 * 🔹 Useful for validating that an export/download action has successfully
	 * produced a file.
	 *
	 * @param expectedExtension الامتداد المتوقع مثل (.xlsx أو .csv) | The expected
	 *                          file extension
	 * @return true إذا تم العثور على ملف مطابق | true if a matching file exists
	 */
	public boolean isFileDownloaded(String expectedExtension) {
		File downloadFolder = new File(System.getProperty("user.home") + "/Downloads");
		File[] files = downloadFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(expectedExtension));
		return files != null && files.length > 0;
	}

	/**
	 * 📥 جلب آخر ملف تم تنزيله بامتداد معيّن من مجلد التنزيلات
	 *
	 * 🔹 ينتظر حتى 20 ثانية ليجد أحدث ملف تم تنزيله ويضمن أن حجمه ثابت (ليس قيد
	 * الكتابة).
	 *
	 * 📥 Retrieves the latest downloaded file with the given extension.
	 *
	 * 🔹 Waits up to 20 seconds to ensure file exists and is stable in size.
	 *
	 * @param extension الامتداد المطلوب مثل xlsx أو csv | The file extension
	 * @return مسار الملف الذي تم تنزيله | Path of the downloaded file
	 * @throws RuntimeException إذا لم يتم العثور على الملف | Throws
	 *                          RuntimeException if no file is found
	 */
	public Path getLatestDownloadedFile(String extension) {
		Path downloadDir = getDownloadDir();
		String ext = extension.startsWith(".") ? extension : "." + extension;

		long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(20);
		Path latest = null;

		while (System.currentTimeMillis() < deadline) {
			try {
				latest = Files.list(downloadDir).filter(p -> !Files.isDirectory(p))
						.filter(p -> p.getFileName().toString().toLowerCase().endsWith(ext))
						.max(Comparator.comparingLong(p -> p.toFile().lastModified())).orElse(null);

				if (latest != null && Files.size(latest) > 0 && isStableSize(latest)) {
					return latest;
				}
			} catch (IOException ignore) {
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
		}
		throw new RuntimeException("❌ لم يتم العثور على ملف " + extension + " جاهز في مجلد التنزيل: " + downloadDir);
	}

	/**
	 * 🛡️ التحقق من أن حجم الملف استقر
	 *
	 * 🔹 يساعد على التأكد من أن الملف لم يعد قيد الكتابة قبل استخدامه.
	 *
	 * 🛡️ Ensures that the file size has stabilized (not still being written).
	 *
	 * @param file مسار الملف | Path of the file
	 * @return true إذا استقر الحجم | true if file size is stable
	 */
	private boolean isStableSize(Path file) {
		try {
			long s1 = Files.size(file);
			Thread.sleep(300);
			long s2 = Files.size(file);
			return s1 == s2;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 📂 إرجاع مسار مجلد التنزيلات
	 *
	 * 🔹 يُستخدم للحصول على موقع مجلد التنزيلات سواء من system property أو
	 * الافتراضي (Downloads).
	 *
	 * 📂 Returns the download directory path.
	 *
	 * 🔹 Either taken from system property "download.dir" or defaults to
	 * ~/Downloads.
	 *
	 * @return مسار مجلد التنزيلات | Path to the download directory
	 */
	private Path getDownloadDir() {
		String dir = System.getProperty("download.dir", System.getProperty("user.home") + "/Downloads");
		return Paths.get(dir);
	}

	/**
	 * 🔢 التحقق من ظهور شريط الترقيم أسفل الجدول
	 *
	 * 🔹 يُستخدم هذا الميثود للتأكد من أن شريط الترقيم (Pagination Bar) ظاهر أسفل
	 * الجدول. يفيد في اختبارات تتعلق بالتنقل بين الصفحات.
	 *
	 * 🔢 Verifies that the pagination bar is visible under the results table.
	 *
	 * 🔹 Useful to confirm that pagination controls are available for navigating
	 * result pages.
	 *
	 * @return true إذا كان شريط الترقيم ظاهرًا | true if the pagination bar is
	 *         visible
	 * @throws RuntimeException إذا لم يتم العثور على شريط الترقيم | Throws
	 *                          RuntimeException if pagination is missing
	 */
	public boolean isPaginationBarVisible() {
		try {
			return isElementVisible(pagination);
		} catch (Exception e) {
			throw new RuntimeException("❌ Pagination bar not found", e);
		}
	}

	/**
	 * 🧪 جلب النص من أول خلية في عمود معيّن
	 *
	 * 🔹 هذا الميثود يُرجع النص الموجود في الصف الأول لعمود محدد (حسب رقمه).
	 * يُستخدم في التحقق من البيانات المعروضة داخل الجدول.
	 *
	 * 🧪 Retrieves the text content of the first cell in a specific column.
	 *
	 * 🔹 Useful for asserting the first value displayed in a particular column of
	 * the results table.
	 *
	 * @param columnIndex رقم العمود (يبدأ من 1) | Column index (1-based)
	 * @return النص داخل أول خلية في العمود | The text content of the first cell in
	 *         the given column
	 */
	public String getFirstCellText(int columnIndex) {
		String xpath = getColumnCellXpath(1, columnIndex);
		return waitForElement(By.xpath(xpath)).getText().trim();
	}

	/**
	 * 📦 توليد XPath لخلية معينة داخل الجدول
	 *
	 * 🔹 يبني XPath كامل للخلية المطلوبة بناءً على رقم الصف ورقم العمود. يُستخدم
	 * للوصول المباشر إلى خلايا معينة داخل الجدول.
	 *
	 * 📦 Returns the full XPath for a table cell at a given row and column index.
	 *
	 * 🔹 Useful to dynamically locate specific cells in the results table.
	 *
	 * @param rowIndex    رقم الصف (ابتداءً من 1) | The row index (1-based)
	 * @param columnIndex رقم العمود (ابتداءً من 1) | The column index (1-based)
	 * @return مسار XPath كسلسلة نصية | The generated XPath string
	 */
	public String getColumnCellXpath(int rowIndex, int columnIndex) {
		return COLUMN_CELL_XPATH_TEMPLATE.replace("{ROW_INDEX}", String.valueOf(rowIndex)).replace("{COLUMN_INDEX}",
				String.valueOf(columnIndex));
	}

	/**
	 * 🔢 يعيد عدد الصفوف داخل جدول النتائج
	 *
	 * 🔹 يُستخدم هذا الميثود لحساب عدد الصفوف في جدول النتائج، وهو مفيد لاختبارات
	 * التحقق من حجم البيانات أو عند التبديل بين الصفحات.
	 *
	 * 🔢 Returns the total number of rows in the results table.
	 *
	 * 🔹 Useful for verifying the number of data rows, e.g., after pagination or
	 * filtering.
	 *
	 * @return عدد الصفوف داخل الجدول | The total number of result rows
	 * @throws RuntimeException إذا فشل في عد الصفوف | Throws RuntimeException if
	 *                          counting fails
	 */
	@Step("🔢 Get number of rows in results table")
	public int getNumberOfResultsRows() {
		try {
			return waitForElements(tableRows).size();
		} catch (Exception e) {
			throw new RuntimeException("❌ Could not count result rows!", e);
		}
	}

	/**
	 * 📖 الحصول على رقم الصفحة الحالية
	 *
	 * 🔹 يستخرج النص الموجود داخل زر الصفحة النشط (Active Page Button) ويحوّله إلى
	 * رقم صحيح.
	 *
	 * 📖 Gets the current active page number from the pagination controls.
	 *
	 * @return رقم الصفحة الحالي | The active page number
	 */
	@Step("📖 Get current page number")
	public int getCurrentPageNumber() {
		WebElement activePage = waitForElement(activePageButton);
		return Integer.parseInt(activePage.getText());
	}

	/**
	 * ⏭️ الانتقال إلى الصفحة التالية
	 *
	 * 🔹 ينقر على زر الصفحة التالية في شريط الترقيم.
	 *
	 * ⏭️ Navigates to the next page in pagination.
	 */
	@Step("⏭️ Go to next page")
	public void goToNextPage() {
		WebElement nextBtn = waitForElement(paginationNextButton);
		nextBtn.click();
	}

	/**
	 * ⏮️ الانتقال إلى الصفحة السابقة
	 *
	 * 🔹 ينقر على زر الصفحة السابقة في شريط الترقيم.
	 *
	 * ⏮️ Navigates to the previous page in pagination.
	 */
	@Step("⏮️ Go to previous page")
	public void goToPreviousPage() {
		WebElement prevBtn = waitForElement(paginationPreviousButton);
		prevBtn.click();
	}

	/**
	 * 🧭 الانتقال إلى صفحة معينة عبر النقر على رقم الصفحة
	 *
	 * 🔹 يبحث بين أزرار أرقام الصفحات في شريط الترقيم ويضغط على الصفحة المطلوبة.
	 *
	 * 🧭 Navigates directly to a specific page by clicking its page number.
	 *
	 * @param pageNumber رقم الصفحة المراد الانتقال إليها | Target page number
	 * @throws RuntimeException إذا لم يتم العثور على رقم الصفحة | Throws
	 *                          RuntimeException if page number not found
	 */
	@Step("🧭 Navigate to page number: {0}")
	public void goToPage(int pageNumber) {
		try {
			List<WebElement> pageButtons = waitForElements(paginationPageNumbers);
			for (WebElement button : pageButtons) {
				if (button.getText().trim().equals(String.valueOf(pageNumber))) {
					button.click();
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
	 * 🔹 يُستخدم للتحقق من الصفحة النشطة حاليًا في شريط الترقيم، وهي الصفحة التي
	 * يتم تمييزها عادةً بخلفية أو لون مختلف.
	 *
	 * 🔢 Gets the currently highlighted (active) page number from the pagination
	 * bar.
	 *
	 * @return رقم الصفحة الحالي | The active page number
	 * @throws RuntimeException إذا فشل في جلب الرقم | Throws RuntimeException if
	 *                          extraction fails
	 *
	 *                          📌 الهدف: التأكد من أن التنقل بين الصفحات يعمل بشكل
	 *                          صحيح وأن الصفحة المطلوبة نشطة.
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
	 * 🔢 اختيار عدد النتائج المعروضة في كل صفحة
	 *
	 * 🔹 يفتح القائمة المنسدلة الخاصة بالنتائج ويحدد العدد المطلوب من الصفوف
	 * المعروضة (مثل 10، 50، 100).
	 *
	 * 🔢 Selects how many results to display per page using the pagination
	 * dropdown.
	 *
	 * @param count العدد المراد عرضه في الصفحة | The desired number of results per
	 *              page
	 * @throws RuntimeException إذا فشل تحديد العدد | Throws RuntimeException if
	 *                          selection fails
	 *
	 *                          📌 الهدف: التحكم في حجم البيانات المعروضة في الجدول
	 *                          لكل صفحة أثناء الاختبار.
	 */
	@Step("🔢 Select results per page: {0}")
	public void selectResultsPerPage(int count) {
		try {
			// افتح القائمة المنسدلة
			waitAndClick(rowsPerPageDropdown);

			// أنشئ اللوكيتر بناءً على العدد المطلوب
			By option = By.xpath(String.format(RESULTS_PER_PAGE_OPTION_XPATH_TEMPLATE, count));

			// اختر العدد المطلوب
			waitAndClick(option);
			Allure.step("✅ Selected results per page: " + count);
		} catch (Exception e) {
			Allure.step("❌ Failed to select results per page: " + count);
			throw new RuntimeException("❌ Failed to select results per page: " + count, e);
		}
	}

	/**
	 * 🧾 إحضار جميع رؤوس الأعمدة في الجدول
	 *
	 * 🔹 يجمع جميع عناصر WebElement التي تمثل رؤوس الأعمدة في جدول النتائج.
	 *
	 * 🧾 Retrieves all table header elements from the results table.
	 *
	 * @return قائمة بعناصر WebElement تمثل رؤوس الأعمدة | List of WebElements for
	 *         all headers
	 *
	 *         📌 الهدف: استخدام رؤوس الأعمدة للتحقق من هيكل الجدول أو أسماء الأعمدة
	 *         أثناء الاختبار.
	 */
	@Step("🧾 Get all table headers")
	public List<WebElement> getAllTableHeaders() {
		return driver.findElements(allTableHeaders);
	}

	/**
	 * 🔽 التحقق من وجود أيقونة الفرز داخل العمود
	 *
	 * 🔹 تُستخدم هذه الدالة للتأكد مما إذا كان رأس العمود يحتوي على أيقونة فرز
	 * (تصاعدي/تنازلي). هذا يساعد في تحديد الأعمدة القابلة للفرز قبل محاولة التفاعل
	 * معها.
	 *
	 * 🔽 Checks whether a given table header contains a sorting icon.
	 *
	 * @param headerElement عنصر رأس العمود | The WebElement of the table header
	 * @return true إذا وُجدت الأيقونة، false خلاف ذلك | true if sorting icon
	 *         exists, false otherwise
	 *
	 *         📌 الهدف: ضمان أن العمود قابل للفرز قبل تنفيذ أي عملية اختبار مرتبطة
	 *         بالترتيب.
	 */
	public boolean hasSortingIcon(WebElement headerElement) {
		return !headerElement.findElements(sortingIcon).isEmpty();
	}

	/**
	 * ✅ التحقق من تأثير ترتيب عمود محدد على أول 3 صفوف في الجدول
	 *
	 * 🔹 تنفذ هذه الدالة عملية الفرز (تصاعدي ثم تنازلي) على عمود معين، وتتحقق من أن
	 * ترتيب الصفوف قد تغير فعلاً على الأقل في الصفوف الثلاثة الأولى. هذا اختبار
	 * خفيف يركز فقط على أول 3 صفوف، ويمكن تطويره لاحقًا لمقارنة الترتيب الكامل
	 * للعمود.
	 *
	 * ✅ Verifies that sorting a given column affects at least the top 3 rows. The
	 * method performs ascending and descending sort operations and checks whether
	 * the data in the first 3 rows has changed.
	 *
	 * @param columnIndex ترتيب العمود (1-based index) | The index of the column to
	 *                    test
	 * @param header      عنصر رأس العمود | The WebElement of the target column
	 *                    header
	 * @throws RuntimeException إذا فشل التحقق من الفرز | Throws RuntimeException if
	 *                          validation fails
	 *
	 *                          📌 الهدف: التأكد من أن وظيفة الترتيب تعمل بشكل صحيح
	 *                          وأنها تؤثر فعلاً على بيانات الجدول.
	 */
	@Step("🔍 Verify sorting effect on top 3 rows for column at index {0}")
	public void verifyTop3RowsChangeOnSort(int columnIndex, WebElement header) {

		// 🏷️ استخراج اسم العمود للتوثيق
		String columnName = header.getText().trim();
		Allure.step("✅ التحقق من عمود: " + columnName + " (index = " + columnIndex + ")");

		// 📋 جلب أول 3 صفوف قبل الفرز
		List<String> originalTop3 = getTopNColumnValues(columnIndex, 3);
		Allure.step("🔍 أول 3 صفوف قبل الفرز: " + originalTop3);

		try {
			// 🔼 تنفيذ الترتيب التصاعدي
			String beforeAsc = getFirstCellText(columnIndex);
			Allure.step("🔼 الضغط لترتيب تصاعدي للعمود: " + columnName);

			header.click();
			waitForColumnToBeSorted(columnIndex, beforeAsc);

			List<String> ascTop3 = getTopNColumnValues(columnIndex, 3);
			int ascChanges = countDifferences(originalTop3, ascTop3);
			Allure.step("🔼 أول 3 صفوف بعد الفرز التصاعدي: " + ascTop3);
			Allure.step("🔼 تغيّر عدد الصفوف: " + ascChanges);

			Assert.assertTrue(ascChanges >= 1, "❌ الفرز التصاعدي لم يؤثر على أول 3 صفوف (index: " + columnIndex + ")");

			// 🔽 تنفيذ الترتيب التنازلي
			String beforeDesc = getFirstCellText(columnIndex);
			Allure.step("🔽 الضغط لترتيب تنازلي للعمود: " + columnName);

			header.click();
			waitForColumnToBeSorted(columnIndex, beforeDesc);

			List<String> descTop3 = getTopNColumnValues(columnIndex, 3);
			int descChanges = countDifferences(ascTop3, descTop3);
			Allure.step("🔽 أول 3 صفوف بعد الفرز التنازلي: " + descTop3);
			Allure.step("🔽 تغيّر عدد الصفوف: " + descChanges);

			Assert.assertTrue(descChanges >= 1, "❌ الفرز التنازلي لم يؤثر على أول 3 صفوف (index: " + columnIndex + ")");

		} catch (Exception e) {
			Allure.step("⚠️ فشل التحقق من ترتيب العمود: " + columnName + " - السبب: " + e.getMessage());
			throw new RuntimeException("⚠️ Error in sorting column index: " + columnIndex, e);
		}
	}

	/**
	 * ⏳ الانتظار حتى يتم تغيير أول خلية في عمود محدد بعد الضغط على عنوان العمود
	 *
	 * 🔹 تُستخدم هذه الدالة للتأكد من أن البيانات في العمود قد تغيّرت فعلاً بعد
	 * النقر على رأس العمود (أي بعد عملية الفرز).
	 *
	 * ⏳ Waits for the first cell value in a specific column to change after sorting
	 * by clicking the column header.
	 *
	 * @param columnIndex        رقم العمود (يبدأ من 1) | The index of the column
	 *                           (1-based)
	 * @param previousFirstValue القيمة السابقة لأول خلية | The value of the first
	 *                           cell before sorting
	 *
	 *                           📌 الهدف: ضمان حدوث تغيير فعلي في البيانات بعد
	 *                           عملية الفرز وعدم بقاء الجدول كما هو.
	 */
	public void waitForColumnToBeSorted(int columnIndex, String previousFirstValue) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			wait.until(driver -> {
				String currentFirst = getFirstCellText(columnIndex);
				return currentFirst != null && !currentFirst.trim().isEmpty()
						&& !currentFirst.equals(previousFirstValue);
			});
		} catch (TimeoutException te) {
			Allure.step("⚠️ Sorting wait timed out. Continuing test anyway...");
		}
	}

	/**
	 * 📥 استرجاع أول N صفوف من عمود معين
	 *
	 * 🔹 تُستخدم هذه الدالة لجلب القيم النصية لأول N صفوف في عمود محدد داخل جدول
	 * النتائج. مفيدة لاختبارات الترتيب أو التحقق من البيانات.
	 *
	 * 📥 Retrieves the top N cell texts from a given column index.
	 *
	 * @param columnIndex رقم العمود (يبدأ من 1) | The index of the column (1-based)
	 * @param count       عدد الصفوف المطلوب جلبها | The number of rows to fetch
	 * @return قائمة بالنصوص المستخرجة | A list of extracted values from the top N
	 *         rows
	 *
	 *         📌 الهدف: التحقق من ترتيب البيانات أو مطابقتها مع المتوقع.
	 */
	public List<String> getTopNColumnValues(int columnIndex, int count) {
		List<String> values = new ArrayList<>();
		List<WebElement> rows = waitForElements(tableRows);

		int limit = Math.min(count, rows.size());

		for (int i = 1; i <= limit; i++) {
			String xpath = getColumnCellXpath(i, columnIndex);
			String cellText = waitForElement(By.xpath(xpath)).getText().trim();
			values.add(cellText);
		}

		return values;
	}

	/**
	 * 🔍 يحسب عدد الصفوف المختلفة بين قائمتين
	 *
	 * 🔹 تُستخدم هذه الدالة للمقارنة بين قائمتين من النصوص (قبل وبعد الترتيب) بهدف
	 * حساب عدد الصفوف التي تغيّرت فعلاً.
	 *
	 * 🔍 Compares two lists of strings (row values) and returns the number of
	 * differences between them.
	 *
	 * @param list1 القائمة الأولى (قبل الترتيب) | The first list (before sorting)
	 * @param list2 القائمة الثانية (بعد الترتيب) | The second list (after sorting)
	 * @return عدد الصفوف المختلفة | Number of changed rows
	 *
	 *         📌 الهدف: التحقق من أن عملية الترتيب أثرت فعليًا على البيانات في
	 *         الجدول.
	 */
	private int countDifferences(List<String> list1, List<String> list2) {
		int changes = 0;
		int size = Math.min(list1.size(), list2.size());

		for (int i = 0; i < size; i++) {
			String a = list1.get(i).trim().replaceAll("\\s+", "");
			String b = list2.get(i).trim().replaceAll("\\s+", "");
			if (!a.equals(b)) {
				changes++;
			}
		}

		return changes;
	}

	/**
	 * 🔎 التحقق مما إذا كان رأس العمود يحتوي على أيقونة فلتر
	 *
	 * 🔹 تُستخدم هذه الدالة لمعرفة إن كان عنصر رأس العمود يحتوي على أيقونة فلترة.
	 * مفيدة في اختبارات التحقق من وجود أدوات تصفية على الجدول.
	 *
	 * 🔎 Checks whether the given column header contains a filter icon.
	 *
	 * @param headerTh عنصر رأس العمود | The WebElement representing the table
	 *                 header
	 * @return true إذا وُجدت أيقونة الفلتر، false خلاف ذلك | true if filter icon
	 *         exists, false otherwise
	 *
	 *         📌 الهدف: ضمان وجود خيارات الفلترة ضمن واجهة الجدول.
	 */
	public boolean hasFilterIcon(WebElement headerTh) {
		try {
			return !headerTh.findElements(headerFilterButton).isEmpty();
		} catch (Exception ignore) {
			return false;
		}
	}

	/**
	 * 🖱️ النقر على أيقونة الفلتر داخل رأس العمود
	 *
	 * 🔹 تُستخدم هذه الدالة لمحاكاة الضغط على أيقونة الفلترة في رأس الجدول لفتح
	 * قائمة الفلترة المرتبطة.
	 *
	 * 🖱️ Clicks the filter icon inside the given column header.
	 *
	 * @param headerTh عنصر رأس العمود | The WebElement of the target column header
	 *
	 *                 📌 الهدف: تفعيل واجهة الفلترة لعمود محدد.
	 */
	public void clickFilterIcon(WebElement headerTh) {
		WebElement btn = headerTh.findElement(headerFilterButton);
		btn.click();
	}

	/**
	 * 📋 استرجاع النصوص الظاهرة في قائمة نوع المطابقة
	 *
	 * 🔹 تُستخدم هذه الدالة لجلب جميع خيارات نوع المطابقة (Match Type) التي تظهر
	 * عند فتح قائمة الفلترة.
	 *
	 * 📋 Retrieves the visible text options from the filter match type dropdown.
	 *
	 * @return قائمة نصوص خيارات المطابقة | A list of available match type options
	 *
	 *         📌 الهدف: التحقق من أن جميع خيارات الفلترة المتوقعة موجودة وتظهر بشكل
	 *         صحيح.
	 */
	public List<String> getFilterMatchTypeOptions() {
		List<WebElement> items = new WebDriverWait(driver, Duration.ofSeconds(5))
				.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(dropdownItems));
		return items.stream().map(e -> e.getText().trim()).filter(s -> !s.isBlank()).toList();
	}

	/**
	 * 👁️‍🗨️ التحقق مما إذا كان الـ Overlay الخاص بالفلترة ظاهرًا
	 *
	 * 🔹 تُستخدم هذه الدالة لمعرفة إن كانت واجهة الفلترة (Filter Overlay) مفتوحة
	 * حاليًا ومرئية للمستخدم.
	 *
	 * 👁️‍🗨️ Checks whether the filter overlay is currently visible.
	 *
	 * @return true إذا كان الـ Overlay ظاهرًا، false إذا لم يكن ظاهرًا | true if
	 *         visible, false otherwise
	 *
	 *         📌 الهدف: ضمان التفاعل الصحيح مع نافذة الفلترة قبل البدء في اختيار
	 *         الخيارات.
	 */
	public boolean isFilterOverlayVisible() {
		try {
			List<WebElement> overlays = driver.findElements(filterOverlay);
			if (overlays.isEmpty())
				return false;
			for (WebElement p : overlays) {
				if (p.isDisplayed() && p.getSize().getHeight() > 0 && p.getSize().getWidth() > 0)
					return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * ✅ التحقق مما إذا كان الـ Overlay يحتوي على أزرار "تطبيق" أو "مسح"
	 *
	 * 🔹 تُستخدم هذه الدالة لاكتشاف وجود أزرار التحكم داخل نافذة الفلترة.
	 *
	 * ✅ Checks if the filter overlay contains "Apply" or "Clear" buttons.
	 *
	 * @return true إذا كان أحد الأزرار موجودًا | true if either button is present
	 *
	 *         📌 الهدف: التحقق من اكتمال واجهة الفلترة وإمكانية التفاعل معها.
	 */
	public boolean isFilterOverlayHasApplyOrClear() {
		try {
			return !driver.findElements(overlayApplyButton).isEmpty()
					|| !driver.findElements(overlayClearButton).isEmpty();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * ❎ إغلاق نافذة الفلترة (Overlay)
	 *
	 * 🔹 تُستخدم هذه الدالة لإغلاق قائمة الفلترة إما بالنقر على نفس أيقونة الرأس،
	 * أو عبر النقر في مكان فارغ داخل الصفحة.
	 *
	 * ❎ Closes the filter overlay either by clicking the same header icon or by
	 * clicking outside of the overlay.
	 *
	 * @param sameHeaderTh عنصر رأس العمود الذي يحتوي على أيقونة الفلتر | The
	 *                     WebElement of the header containing the filter icon
	 *
	 *                     📌 الهدف: ضمان إغلاق الـ Overlay عند الانتهاء من التفاعل
	 *                     معه.
	 */
	public void closeFilterOverlay(WebElement sameHeaderTh) {
		try {
			WebElement btn = sameHeaderTh.findElement(headerFilterButton);
			btn.click();
			return;
		} catch (Exception ignore) {
		}

		try {
			new org.openqa.selenium.interactions.Actions(driver).moveByOffset(5, 5).click().perform();
		} catch (Exception ignore) {
		}
	}

	/**
	 * 📂 فتح قائمة نوع المطابقة داخل نافذة الفلترة
	 *
	 * 🔹 تُستخدم هذه الدالة للنقر على الـ Dropdown الخاص بنوع المطابقة وانتظار ظهور
	 * عناصر القائمة.
	 *
	 * 📂 Opens the match type dropdown inside the filter overlay.
	 *
	 * 📌 الهدف: تمكين المستخدم من رؤية خيارات المطابقة المتاحة.
	 */
	public void openFilterMatchTypeDropdown() {
		WebElement dd = new WebDriverWait(driver, Duration.ofSeconds(5))
				.until(ExpectedConditions.elementToBeClickable(matchTypeDropdown));
		dd.click();
		new WebDriverWait(driver, Duration.ofSeconds(5))
				.until(ExpectedConditions.visibilityOfElementLocated(dropdownPanel));
	}

	/**
	 * 📋 استرجاع جميع عناصر نوع المطابقة (مع التمرير حتى النهاية)
	 *
	 * 🔹 تُستخدم هذه الدالة لجمع كل الخيارات المتاحة داخل قائمة المطابقة، حتى لو
	 * كانت تُحمَّل تدريجيًا (lazy render).
	 *
	 * 📋 Retrieves all available match type options by scrolling through the panel
	 * to handle lazy rendering.
	 *
	 * @return قائمة النصوص لكل خيارات المطابقة | A list of all match type option
	 *         texts
	 *
	 *         📌 الهدف: ضمان الحصول على القائمة الكاملة لخيارات المطابقة عند
	 *         الاختبار.
	 */
	public List<String> getFilterMatchTypeOptionsAllScrolling() {
		WebElement panel = new WebDriverWait(driver, Duration.ofSeconds(5))
				.until(ExpectedConditions.visibilityOfElementLocated(dropdownPanel));

		WebElement scroller = panel.findElements(dropdownScroller).isEmpty() ? panel
				: panel.findElement(dropdownScroller);

		JavascriptExecutor js = (JavascriptExecutor) driver;
		LinkedHashSet<String> seen = new LinkedHashSet<>();

		int guard = 0;
		long lastScrollTop = -1;

		while (guard++ < 20) {
			for (WebElement li : panel.findElements(dropdownItems)) {
				String t = li.getText().trim();
				if (!t.isBlank())
					seen.add(t);
			}

			long scrollTop = ((Number) js.executeScript("return arguments[0].scrollTop;", scroller)).longValue();
			long clientHeight = ((Number) js.executeScript("return arguments[0].clientHeight;", scroller)).longValue();
			long scrollHeight = ((Number) js.executeScript("return arguments[0].scrollHeight;", scroller)).longValue();

			if (scrollTop + clientHeight >= scrollHeight - 1)
				break;
			if (scrollTop == lastScrollTop)
				break;
			lastScrollTop = scrollTop;

			js.executeScript("arguments[0].scrollTop = arguments[0].scrollTop + arguments[0].clientHeight;", scroller);
			try {
				Thread.sleep(120);
			} catch (InterruptedException ignored) {
			}
		}

		return new ArrayList<>(seen);
	}

	/**
	 * ❎ إغلاق قائمة نوع المطابقة إذا كانت مفتوحة
	 *
	 * 🔹 تُستخدم هذه الدالة للتحقق مما إذا كان Dropdown الخاص بنوع المطابقة
	 * مفتوحًا، ثم إغلاقه بالنقر خارج القائمة.
	 *
	 * ❎ Dismisses the match type dropdown if it is open by simulating a body click.
	 *
	 * 📌 الهدف: إعادة واجهة المستخدم إلى وضعها الطبيعي قبل الاستمرار في التفاعل.
	 */
	public void dismissFilterMatchTypeDropdownIfOpen() {
		List<WebElement> panels = driver.findElements(dropdownPanel);
		if (!panels.isEmpty() && panels.get(0).isDisplayed()) {
			((JavascriptExecutor) driver).executeScript("document.body.click();");
			new WebDriverWait(driver, Duration.ofSeconds(3))
					.until(ExpectedConditions.invisibilityOfElementLocated(dropdownPanel));
		}
	}

	/**
	 * ✏️ إدخال قيمة الفلترة داخل الـ Overlay
	 *
	 * 🔹 تبحث هذه الدالة عن حقل إدخال نصي أو عددي داخل نافذة الفلترة المفتوحة، ثم
	 * تقوم بتمرير القيمة المطلوبة إليه.
	 *
	 * ✏️ Sets a filter value in the open filter overlay (supports text/number
	 * inputs).
	 *
	 * @param value النص أو الرقم الذي سيتم إدخاله | The filter value to input
	 *
	 *              📌 الهدف: ملء حقل الفلترة بالقيمة المطلوبة لتحديث نتائج الجدول.
	 */
	public void setFilterValue(String value) {
		WebElement overlay = new WebDriverWait(driver, Duration.ofSeconds(5))
				.until(ExpectedConditions.visibilityOfElementLocated(openOverlayRoot));

		List<WebElement> inputs = overlay.findElements(overlayTextLikeInputs);
		WebElement input = inputs.isEmpty() ? null : inputs.get(0);

		if (input == null) {
			List<WebElement> anyInputs = overlay.findElements(overlayAnyVisibleInputs);
			if (!anyInputs.isEmpty())
				input = anyInputs.get(0);
		}

		if (input == null) {
			throw new RuntimeException("❌ لم أتمكّن من العثور على حقل إدخال مناسب داخل نافذة الفلترة المفتوحة.");
		}

		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", input);
		input.clear();
		input.sendKeys(value);
		Allure.step("✏️ Set filter value: " + value);
	}

	/**
	 * ✅ تطبيق الفلتر بالنقر على زر «تطبيق» داخل الـ Overlay
	 *
	 * 🔹 تضغط هذه الدالة على زر Apply وتنتظر اختفاء نافذة الفلترة، مما يشير إلى أن
	 * الفلتر قد تم تطبيقه على الجدول.
	 *
	 * ✅ Applies the filter by clicking the "Apply" button and waits for the overlay
	 * to close.
	 *
	 * 📌 الهدف: التأكد من أن الفلتر قد تم تنفيذه وتحديث البيانات في الجدول.
	 */
	public void applyFilter() {
		WebElement overlay = waitForOpenFilterOverlay(Duration.ofSeconds(5));
		WebElement applyBtn = overlay.findElement(overlayApplyButton);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", applyBtn);
		applyBtn.click();

		new WebDriverWait(driver, Duration.ofSeconds(10))
				.until(ExpectedConditions.invisibilityOfElementLocated(filterOverlay));

		Allure.step("✅ Filter applied successfully");
	}

	/**
	 * 📑 استخراج النصوص من عمود معيّن في جدول النتائج
	 *
	 * 🔹 تُعيد هذه الدالة قائمة تحتوي على النصوص الظاهرة داخل عمود محدد.
	 *
	 * 📑 Retrieves all visible text values from a given column index.
	 *
	 * @param colIndex رقم العمود المطلوب (1 = أول عمود) | Column index (1-based)
	 * @return قائمة نصوص من العمود المطلوب | List of strings from the target column
	 *
	 *         📌 الهدف: التحقق من القيم الظاهرة في عمود محدد لأغراض الاختبار.
	 */
	public List<String> getColumnTexts(int colIndex) {
		String xpath = String.format(COLUMN_CELLS_XPATH_TMPL, colIndex);
		List<WebElement> cells = driver.findElements(By.xpath(xpath));
		List<String> values = cells.stream().map(e -> e.getText().trim()).toList();
		Allure.step("📑 Extracted " + values.size() + " texts from column index: " + colIndex);
		return values;
	}

	/**
	 * ⏳ الانتظار حتى يفتح Overlay الفلترة ويصبح مرئيًا
	 *
	 * ⏳ Waits until the filter overlay is visible and returns it.
	 *
	 * @param timeout مدة الانتظار قبل الفشل | The maximum wait time before timeout
	 * @return WebElement يمثل الـ Overlay المفتوح | The visible filter overlay
	 *         element
	 *
	 *         📌 الهدف: التأكد من أن نافذة الفلترة جاهزة للتفاعل.
	 */
	public WebElement waitForOpenFilterOverlay(Duration timeout) {
		return new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(filterOverlay));
	}

	/**
	 * 📌 الحصول على العنصر الجذري للـ Overlay المفتوح حاليًا
	 *
	 * 📌 Retrieves the root element of the currently open filter overlay.
	 *
	 * @return WebElement الخاص بالـ Overlay المفتوح | The root overlay element
	 *
	 *         📌 الهدف: استخدامه للوصول إلى عناصر التحكم داخل نافذة الفلترة.
	 */
	public WebElement getOpenOverlayRoot() {
		return driver.findElement(filterOverlay);
	}

	/**
	 * ⏳ الانتظار حتى تُفتح لوحة Dropdown معينة
	 *
	 * ⏳ Waits until a dropdown panel (by locator) becomes visible.
	 *
	 * @param panelLocator لوكيتر اللوحة المستهدفة | The locator of the dropdown
	 *                     panel
	 * @param timeout      مدة الانتظار القصوى | Maximum wait time
	 * @return WebElement يمثل لوحة الـ Dropdown المفتوحة | The visible dropdown
	 *         panel
	 *
	 *         📌 الهدف: ضمان أن القائمة المنسدلة قد ظهرت قبل التفاعل معها.
	 */
	public WebElement waitForDropdownPanelOpen(By panelLocator, Duration timeout) {
		return new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(panelLocator));
	}

	/**
	 * 🇸🇦 اختيار نوع المطابقة من القائمة داخل Overlay الفلترة
	 *
	 * 🇺🇸 Selects a specific match type (e.g., يبدأ بـ, ينتهي بـ, يحتوي على,
	 * يساوي...) from the filter match type dropdown inside the overlay.
	 *
	 * @param matchType نوع المطابقة المطلوب اختياره | The desired match type option
	 *
	 *                  📌 الهدف: تحديد منطق الفلترة (مثل: يحتوي على/لا يحتوي على)
	 *                  لتطبيقه على نتائج الجدول.
	 */
	public void selectFilterMatchType(String matchType) {
		WebElement overlay = waitForOpenFilterOverlay(Duration.ofSeconds(5));

		WebElement typeDd = overlay.findElement(matchTypeDropdown);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", typeDd);
		typeDd.click();

		WebElement panel = waitForDropdownPanelOpen(matchTypeDropdownPanel, Duration.ofSeconds(5));
		WebElement scroller = panel.findElement(dropdownScroller);

		List<WebElement> options = panel.findElements(dropdownItems);
		boolean clicked = false;

		for (WebElement opt : options) {
			String txt = opt.getText().trim();
			if (txt.equals(matchType)) {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'nearest'});", opt);
				opt.click();
				clicked = true;
				break;
			}
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop = arguments[0].scrollTop + 60;",
					scroller);
		}

		if (!clicked) {
			options = panel.findElements(dropdownItems);
			for (WebElement opt : options) {
				if (opt.getText().trim().equals(matchType)) {
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'nearest'});", opt);
					opt.click();
					clicked = true;
					break;
				}
			}
		}

		if (!clicked) {
			throw new RuntimeException("❌ Match type option not found: " + matchType);
		}

		new WebDriverWait(driver, Duration.ofSeconds(3))
				.until(ExpectedConditions.invisibilityOfElementLocated(matchTypeDropdownPanel));

		Allure.step("✅ Selected filter match type: " + matchType);
	}

	/**
	 * 🧮 يطبّق منطق الفلترة بناءً على نوع المطابقة
	 *
	 * 🧮 Applies the filtering logic according to the given match type and filter
	 * value.
	 *
	 * @param value       القيمة الأصلية (من الجدول) | The actual value from the
	 *                    table
	 * @param matchType   نوع المطابقة (مثلاً: يحتوي على، يبدأ بـ...) | The match
	 *                    type
	 * @param filterValue القيمة المستخدمة للفلترة | The filter value
	 * @return true إذا كانت القيمة مطابقة للشرط | true if the value matches the
	 *         filter
	 *
	 *         📌 الهدف: التحقق برمجيًا من منطق الفلترة المستخدم ومطابقته للنتائج.
	 */
	public boolean matchesAccordingToMatchType(String value, String matchType, String filterValue) {
		if (value == null)
			value = "";
		value = value.trim();
		filterValue = (filterValue == null) ? "" : filterValue.trim();

		switch (matchType) {
		case "يبدأ بـ":
			return value.startsWith(filterValue);
		case "ينتهي بـ":
			return value.endsWith(filterValue);
		case "يحتوي على":
			return value.contains(filterValue);
		case "لا يحتوي على":
			return !value.contains(filterValue);
		case "يساوي":
			return value.equals(filterValue);
		case "لا يساوي":
			return !value.equals(filterValue);
		default:
			switch (matchType) {
			case "Starts with":
				return value.startsWith(filterValue);
			case "Ends with":
				return value.endsWith(filterValue);
			case "Contains":
				return value.contains(filterValue);
			case "Not contains":
				return !value.contains(filterValue);
			case "Equals":
				return value.equals(filterValue);
			case "Not equals":
				return !value.equals(filterValue);
			}
			throw new IllegalArgumentException("Unknown match type: " + matchType);
		}
	}

	/**
	 * 🔢 حساب عدد شروط الفلترة الظاهرة حاليًا
	 *
	 * 🔹 يُستخدم للتحقق من إضافة/حذف الشروط داخل واجهة الفلاتر.
	 *
	 * 🔢 Returns the number of visible filter conditions in the overlay.
	 *
	 * @return عدد صفوف الشروط الظاهرة | The count of visible condition rows
	 *
	 *         📌 الهدف: التأكد من أن عمليات إضافة أو حذف الشروط انعكست في الواجهة.
	 */
	@Step("🔢 Count visible filter conditions")
	public int countFilterConditions() {
		try {
			List<WebElement> rows = driver.findElements(conditionRows);
			int count = rows.size();
			Allure.step("📊 Visible condition rows: " + count);
			return count;
		} catch (Exception e) {
			String msg = "❌ Failed to count filter conditions.";
			Allure.attachment("Count Filter Conditions Error", msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * ➕ الضغط على زر «أضف شرط» ثم انتظار ظهور صف جديد
	 *
	 * 🔹 Useful to dynamically add a new filter condition row and wait until it
	 * appears.
	 *
	 * ➕ Clicks the 'Add Condition' button and waits for a new row to appear.
	 */
	@Step("➕ Click 'Add Condition' and wait for a new condition row")
	public void clickAddFilterCondition() {
		try {
			WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(5))
					.until(ExpectedConditions.elementToBeClickable(addConditionButton));
			btn.click();

			// ⏳ انتظر زيادة عدد الصفوف (على الأقل يصبح 2 إن كان واحدًا)
			new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> driver.findElements(conditionRows).size() >= 2);

			Allure.step("✅ New condition row appeared.");
		} catch (TimeoutException te) {
			String msg = "❌ Timed out waiting for a new condition row after clicking 'Add Condition'.";
			Allure.attachment("Add Condition Timeout", msg);
			throw new RuntimeException(msg, te);
		} catch (Exception e) {
			String msg = "❌ Failed to add a new filter condition.";
			Allure.attachment("Add Condition Error", msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * 🔽 فتح قائمة «نوع المطابقة» داخل صف شرط محدد (ترقيم 0-أساس)
	 *
	 * 🔽 Opens the 'match type' dropdown inside a specific condition row (0-based).
	 *
	 * @param index فهرس صف الشرط (0 = الصف الأول) | The 0-based index of the
	 *              condition row
	 *
	 *              📌 الهدف: تجهيز اختيار نوع المطابقة لهذا الصف قبل تحديد القيمة.
	 */
	@Step("🔽 Open match-type dropdown in condition row index: {0}")
	public void openMatchTypeDropdownInRow(int index) {
		try {
			List<WebElement> rows = new WebDriverWait(driver, Duration.ofSeconds(5))
					.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(conditionRows));

			if (index < 0 || index >= rows.size()) {
				throw new IllegalArgumentException("Invalid condition row index: " + index);
			}

			WebElement row = rows.get(index);
			WebElement dd = row.findElement(matchTypeDropdownInside(row));
			dd.click();

			new WebDriverWait(driver, Duration.ofSeconds(5))
					.until(ExpectedConditions.visibilityOfElementLocated(dropdownPanel));

			Allure.step("✅ Match-type dropdown opened for row index: " + index);
		} catch (IllegalArgumentException iae) {
			Allure.attachment("Open Match-Type Error", iae.getMessage());
			throw iae;
		} catch (TimeoutException te) {
			String msg = "❌ Match-type dropdown panel did not appear for row index: " + index;
			Allure.attachment("Dropdown Open Timeout", msg);
			throw new RuntimeException(msg, te);
		} catch (Exception e) {
			String msg = "❌ Failed to open match-type dropdown for row index: " + index;
			Allure.attachment("Dropdown Open Error", msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * ✅ اختيار عنصر من القائمة المنسدلة المفتوحة حاليًا بالنص المطابق تمامًا
	 *
	 * ✅ Picks an option from the currently open dropdown by exact visible text.
	 *
	 * @param text النص الذي سيتم اختياره | The exact visible text to pick
	 *
	 *             📌 الهدف: توحيد اختيار العناصر من القوائم التي تعمل
	 *             بالتمرير/التحميل الكسول.
	 */
	@Step("✅ Pick option from open dropdown by text: {0}")
	public void pickFromOpenDropdownByExactText(String text) {
		try {
			String itemXpath = String.format(DROPDOWN_ITEM_BY_TEXT_XPATH, text);
			WebElement item = new WebDriverWait(driver, Duration.ofSeconds(5))
					.until(ExpectedConditions.elementToBeClickable(By.xpath(itemXpath)));

			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", item);
			item.click();

			// ⏳ انتظر اختفاء اللوحة للتأكد من تطبيق الاختيار
			new WebDriverWait(driver, Duration.ofSeconds(5))
					.until(ExpectedConditions.invisibilityOfElementLocated(dropdownPanel));

			Allure.step("✅ Picked dropdown option: " + text);
		} catch (TimeoutException te) {
			String msg = "❌ Timed out picking dropdown option: " + text;
			Allure.attachment("Pick From Dropdown Timeout", msg);
			throw new RuntimeException(msg, te);
		} catch (Exception e) {
			String msg = "❌ Failed to pick dropdown option: " + text;
			Allure.attachment("Pick From Dropdown Error", msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * 🧩 تعيين «نوع المطابقة» داخل صف شرط محدد (باستخدام فتح القائمة ثم الاختيار)
	 *
	 * 🧩 Sets the match type for a specific condition row by opening its dropdown
	 * and picking the option.
	 *
	 * @param index     فهرس صف الشرط (0-أساس) | The 0-based index of the condition
	 *                  row
	 * @param matchType النص الحرفي لخيار نوع المطابقة | The exact match type text
	 *                  to select
	 *
	 *                  📌 الهدف: أتمتة اختيار نوع المطابقة لعدة شروط متسلسلة في
	 *                  سيناريوهات الفلترة المتقدمة.
	 */
	@Step("🧩 Set match type '{1}' in condition row index: {0}")
	public void setNthFilterMatchType(int index, String matchType) {
		try {
			// افتح القائمة في الصف المطلوب
			openMatchTypeDropdownInRow(index);

			// تمرير بسيط للأعلى قبل محاولة الاختيار (لبعض القوائم الطويلة)
			try {
				WebElement scroller = new WebDriverWait(driver, Duration.ofSeconds(3))
						.until(ExpectedConditions.visibilityOfElementLocated(dropdownScroller));
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop = 0;", scroller);
			} catch (Exception ignore) {
				// لا مشكلة إن لم نجد سكروول — تعتمد على نسخة الـ UI
			}

			// اختر الخيار بالنص الحرفي
			pickFromOpenDropdownByExactText(matchType);

			Allure.step("✅ Match type set for row " + index + ": " + matchType);
		} catch (Exception e) {
			String msg = "❌ Failed to set match type '" + matchType + "' in condition row index: " + index;
			Allure.attachment("Set Match Type Error", msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * ✏️ يعيّن قيمة الإدخال داخل صف شرط معيّن (0-أساس)
	 *
	 * ✏️ Sets the text value inside a specific condition row (0-based index).
	 *
	 * @param index فهرس صف الشرط (0 = الصف الأول) | The 0-based index of the
	 *              condition row
	 * @param value القيمة المُراد إدخالها | The value to type into the row input
	 *
	 *              📌 الهدف: تعبئة قيمة الشرط بعد تحديد نوع المطابقة لهذا الصف.
	 */
	@Step("✏️ Set filter value in condition row index: {0} → '{1}'")
	public void setNthFilterValue(int index, String value) {
		try {
			List<WebElement> rows = new WebDriverWait(driver, Duration.ofSeconds(5))
					.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(conditionRows));

			if (index < 0 || index >= rows.size()) {
				throw new IllegalArgumentException("Invalid condition row index: " + index);
			}

			WebElement row = rows.get(index);
			WebElement input = row.findElement(valueInputInside(row));

			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", input);
			input.clear();
			input.sendKeys(value);

			Allure.step("✅ Value set in row " + index + ": '" + value + "'");
		} catch (IllegalArgumentException iae) {
			Allure.attachment("Set Filter Value Error", iae.getMessage());
			throw iae;
		} catch (TimeoutException te) {
			String msg = "❌ Timed out locating condition rows to set value at index: " + index;
			Allure.attachment("Set Filter Value Timeout", msg);
			throw new RuntimeException(msg, te);
		} catch (Exception e) {
			String msg = "❌ Failed to set filter value in row index: " + index;
			Allure.attachment("Set Filter Value Error", msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * 🧩 ميثود مختصر لضبط الشرط (نوع المطابقة + القيمة) في صف معيّن
	 *
	 * 🧩 Convenience method to set both match type and value in a given condition
	 * row.
	 *
	 * @param index     فهرس صف الشرط (0-أساس) | The 0-based index of the condition
	 *                  row
	 * @param matchType النص الحرفي لنوع المطابقة | The exact match type label to
	 *                  select
	 * @param value     قيمة الشرط | The value to type in the row input
	 *
	 *                  📌 الهدف: تبسيط كتابة السيناريوهات عند التعامل مع شروط
	 *                  متعددة.
	 */
	@Step("🧩 Set filter condition in row {0}: matchType='{1}', value='{2}'")
	public void setNthFilterCondition(int index, String matchType, String value) {
		try {
			setNthFilterMatchType(index, matchType);
			setNthFilterValue(index, value);
			Allure.step("✅ Condition set for row " + index + " → [" + matchType + "] = '" + value + "'");
		} catch (Exception e) {
			String msg = "❌ Failed to set filter condition at row " + index + " (matchType='" + matchType + "', value='"
					+ value + "')";
			Allure.attachment("Set Filter Condition Error", msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * 🔽 لوكيتر Dropdown «نوع المطابقة» داخل صف شرط معيّن
	 *
	 * 🔽 Returns a locator for the match-type dropdown within a given condition
	 * row.
	 *
	 * @param row عنصر صف الشرط | The row WebElement that contains the dropdown
	 * @return كائن By يشير للدروبداون داخل الصف | A By locator for the row's
	 *         match-type dropdown
	 *
	 *         📌 الهدف: مساعد داخلي لإيجاد الدروبداون الصحيح بحسب الترجمات
	 *         المدعومة.
	 */
	public By matchTypeDropdownInside(WebElement row) {
		// أي p-dropdown يحمل واحدة من تسميات نوع المطابقة المعروفة (عربي/إنجليزي)
		return By.xpath(
				".//div[contains(@class,'p-dropdown')][.//span[contains(@class,'p-dropdown-label')][normalize-space()='يبدأ بـ' or normalize-space()='يحتوي على' or normalize-space()='لا يحتوي على' or normalize-space()='ينتهي بـ' or normalize-space()='يساوي' or normalize-space()='لا يساوي' or normalize-space()='Starts with' or normalize-space()='Contains' or normalize-space()='Not contains' or normalize-space()='Ends with' or normalize-space()='Equals' or normalize-space()='Not equals']]");
	}

	/**
	 * 🔽 فتح Dropdown «مطابقة الكل/البعض» داخل نافذة الفلترة المفتوحة
	 *
	 * 🔽 Opens the 'Match All/Any' dropdown inside the currently open filter
	 * overlay.
	 *
	 * 📌 الهدف: اختيار ما إذا كانت الشروط تُطبّق كلها (AND) أم أيٌّ منها (OR).
	 */
	@Step("🔽 Open 'Match All/Any' dropdown inside filter overlay")
	private void openMatchAllDropdown() {
		try {
			WebElement dd = new WebDriverWait(driver, Duration.ofSeconds(5))
					.until(ExpectedConditions.elementToBeClickable(matchAllDropdown));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", dd);
			dd.click();

			new WebDriverWait(driver, Duration.ofSeconds(5))
					.until(ExpectedConditions.visibilityOfElementLocated(dropdownPanel));

			Allure.step("✅ 'Match All/Any' dropdown opened.");
		} catch (TimeoutException te) {
			String msg = "❌ Timed out opening 'Match All/Any' dropdown.";
			Allure.attachment("Open Match-All Dropdown Timeout", msg);
			throw new RuntimeException(msg, te);
		} catch (Exception e) {
			String msg = "❌ Failed to open 'Match All/Any' dropdown.";
			Allure.attachment("Open Match-All Dropdown Error", msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * 🔀 يضبط وضع المطابقة في شروط الفلترة: - "مطابقة الكل" (AND / Match all) -
	 * "مطابقة البعض" (OR / Match any)
	 *
	 * 🔀 Sets the filter match mode: - "مطابقة الكل" = Match all conditions (AND) -
	 * "مطابقة البعض" = Match any condition (OR)
	 *
	 * @param modeText النص الظاهر للخيار في القائمة (مثال: "مطابقة الكل" أو "مطابقة
	 *                 البعض") The exact visible text of the option (e.g., "Match
	 *                 all" / "Match any")
	 *
	 *                 📌 الهدف: التحكم في منطق الدمج بين الشروط عند تطبيق الفلترة.
	 */
	@Step("🔀 Set filter match mode to: {0}")
	public void setMatchAllMode(String modeText) {
		try {
			// افتح الدروبداون الخاص بوضع المطابقة
			openMatchAllDropdown();

			// كوّن Xpath ديناميكي للعنصر المطلوب
			String itemXpath = String.format(DROPDOWN_ITEM_BY_TEXT_XPATH, modeText);

			// انتظر حتى يصبح العنصر قابلاً للنقر
			WebElement item = new WebDriverWait(driver, Duration.ofSeconds(5))
					.until(ExpectedConditions.elementToBeClickable(By.xpath(itemXpath)));

			// مرّر العنصر إلى الوسط ليتضح للنقر
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", item);

			// انقر على الخيار
			item.click();

			// انتظر اختفاء اللوحة للتأكيد على أن التحديد تم تطبيقه
			new WebDriverWait(driver, Duration.ofSeconds(5))
					.until(ExpectedConditions.invisibilityOfElementLocated(dropdownPanel));

			Allure.step("✅ Match mode set to: " + modeText);

		} catch (TimeoutException te) {
			String msg = "❌ Timed out while selecting match mode option: " + modeText;
			Allure.attachment("Set Match Mode Timeout", msg);
			throw new RuntimeException(msg, te);
		} catch (Exception e) {
			String msg = "❌ Failed to set match mode option: " + modeText;
			Allure.attachment("Set Match Mode Error", msg);
			throw new RuntimeException(msg, e);
		}
	}

}
