package com.falak.qa.pages.home;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.falak.qa.base.BasePage;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;

//	✅ هذا الكلاس يمثل صفحة "الرئيسية" في الموقع ويحتوي على كل الإجراءات التي يمكن تنفيذها فيها.
// This class represents the "Home Page" of the website and includes all actions and elements within it.
public class HomePage extends BasePage {

	private final String baseUrl; // 🌍 رابط الصفحة الرئيسية – يُعاد عند الطلب
	// The base URL of the home page, useful for navigation and validations.

	// 🔝 عنصر الهيدر الرئيسي في الصفحة | Header element at top of page
	private final By pageHeader = By.tagName("app-header");

	// 🌐 عناصر رأس الصفحة (Navigation Bar buttons)
	// These locators identify the top navigation links.
	private By homePageHeaderButton = By.xpath("//a[span[normalize-space(text())='الرئيسة']]");
	private By aboutFalakHeaderButton = By.xpath("//a[span[normalize-space(text())='عن فلك']]");
	private By corporaHeaderButton = By.xpath("//a[span[normalize-space(text())='المدونات']]");
	private By voiceWallHeaderButton = By.xpath("//a[span[normalize-space(text())='الجدارية الصوتية']]");
	private By frequencyListsHeaderButton = By.xpath("//a[span[normalize-space(text())='قوائم الشيوع']]");
	private By developersHeaderButton = By.xpath("//a[span[normalize-space(text())='المطورون']]");
	private By contactUsHeaderButton = By.xpath("//a[span[normalize-space(text())='اتصل بنا']]");

	// 🃏 بطاقات وأقسام الصفحة
	// These represent clickable cards on the homepage
	private By corporaCard = By.xpath("//div[@class='p-card-content']/a[@href='/corpora']");
	private By addCorporaCards = By.xpath(
			"//div[contains(@class, 'p-card')]//a[contains(@href, '/request-adding-corpus') and .//h5[normalize-space()='أضف مدونتك']]");
	private By featuresCard = By.xpath(
			"//div[contains(@class, 'p-card') and contains(@class, 'p-component')][.//h5[normalize-space()='المـــزايــــا']]");
	private By toolsCard = By.xpath(
			"//p-card[contains(@class, 'theme-card') and contains(@class, 'cursor-pointer')][.//h5[normalize-space()='الأدوات']]");

	// 📚 أقسام معينة في الصفحة
	private By featuresSection = By
			.xpath("//section[contains(@class, 'section-features')][.//h2[normalize-space()='مزايا المنصة']]");
	/* جميع بطاقات المزايا داخل section-features */
	private final By featureCardsLocator = By
			.xpath("//section[contains(@class,'section-features')]//div[contains(@class,'surface-card')]");
	private By registrationSection = By
			.xpath("//section[contains(@class, 'promo-banner')]//a[video/source[contains(@src, 'video-banner.mp4')]]");

	// 📌 قسم "فلك في أرقام"
	private final By falakStatsSection = By
			.xpath("//section[contains(@class,'surface-section') and .//h2[normalize-space()='فلك في أرقام']]");
	private final By falakStatsTitle = By
			.xpath("//section[contains(@class,'surface-section') and .//h2[normalize-space()='فلك في أرقام']]//h2");
	private final By falakStatsDescription = By.xpath(
			"//section[contains(@class,'surface-section') and .//h2[normalize-space()='فلك في أرقام']]//header/p");
	private final By falakStatsImage = By.xpath(
			"//section[contains(@class,'surface-section') and .//h2[normalize-space()='فلك في أرقام']]//img[contains(@alt,'فلك في أرقام')]");

	// 🧩 بطاقات محتوى المدونات
	private By corporaContentCard = By.cssSelector("div.col-12.md\\:col-6.lg\\:col-4.xl\\:col-4.p-2");

	// 🔝 زر العودة إلى أعلى الصفحة | Back-to-top button
	private final By backToTopButton = By
			.xpath("//div[contains(@class,'back-to-top-btn')]//button[span[contains(@class,'pi-angle-up')]]");

	// ⚫ الفوتر (نهاية الصفحة)
	private By footerDivision = By.cssSelector("div.layout-footer");
	private By moreCorporaButton = By
			.xpath("//a[@href='/corpora' and contains(@class,'p-button') and span[normalize-space()='المزيد']]");

	// ✅ محدد عناصر بطاقات المدونات داخل قسم "المدونات"
	// Selector for corpus cards inside the "section-best-corpuses" section
	private final By corporaItemDetails = By.cssSelector(".section-best-corpuses .item-details");
	/* ◀️ رأس «مدونة الشهر» فقط (header) */
	private final By corporaOfMonthHeader = By.cssSelector("section.section-the-month-corpus");
	// بطاقة «مدونة الشهر» داخل القسم
	private final By corporaOfMonthCard = By.cssSelector("section.section-the-month-corpus div.item-details");

	/* ============== أدوات المنصة ============== */
	private final By toolsSectionRoot = By
			.xpath("//div[contains(@class,'section-tools') and .//h2[normalize-space()='أدوات المنصة']]");
	private final By toolsSectionTitle = By
			.xpath("//div[contains(@class,'section-tools')]//h2[normalize-space()='أدوات المنصة']");
	private final By toolsSectionDesc = By.xpath("//div[contains(@class,'section-tools')]//header/p");
	private final By toolsCards = By
			.xpath("//div[contains(@class,'section-tools')]//div[contains(@class,'surface-card')]");

	// 📦 المُنشئ (Constructor)
	// يُستخدم لتمرير الـ WebDriver إلى الكلاس الأب BasePage.
	public HomePage(WebDriver driver) {
		super(driver);
		this.baseUrl = com.falak.qa.config.EnvironmentConfigLoader.getUrl("baseUrl");
	}

	// ====================== Navigation Button Methods ======================

	/**
	 * 🔘 ينقر على زر "الرئيسية" في الشريط العلوي Clicks the "Home" button from the
	 * top navigation bar.
	 *
	 * @throws RuntimeException في حال فشل النقر على زر الرئيسية if clicking the
	 *                          Home button fails
	 */
	@Step("Click top navigation: Home")
	public void clickHomePageHeaderButton() {
		try {
			waitAndClick(homePageHeaderButton);
		} catch (Exception e) {
			throw new RuntimeException("فشل في النقر على زر الرئيسية", e);
		}
	}

	/**
	 * 🔘 ينقر على زر "عن فلك" في الشريط العلوي Clicks the "About Falak" button from
	 * the top navigation bar.
	 *
	 * @throws RuntimeException في حال فشل النقر على زر عن فلك if clicking the About
	 *                          Falak button fails
	 */
	@Step("Click top navigation: About Falak")
	public void clickAboutFalakHeaderButton() {
		try {
			waitAndClick(aboutFalakHeaderButton);
		} catch (Exception e) {
			throw new RuntimeException("فشل في النقر على زر عن فلك", e);
		}
	}

	/**
	 * 🔘 ينقر على زر "المدونات" في الشريط العلوي Clicks the "Corpora" (Blogs)
	 * button from the top navigation bar.
	 *
	 * @throws RuntimeException في حال فشل النقر على زر المدونات if clicking the
	 *                          Corpora button fails
	 */
	@Step("Click top navigation: Corpora (Blogs)")
	public void clickCorporaHeaderButton() {
		try {
			waitAndClick(corporaHeaderButton);
		} catch (Exception e) {
			throw new RuntimeException("فشل في النقر على زر المدونات", e);
		}
	}

	/**
	 * 🔘 ينقر على زر "الجدارية الصوتية" في الشريط العلوي Clicks the "Voice Wall"
	 * button from the top navigation bar.
	 *
	 * @throws RuntimeException في حال فشل النقر على زر الجدارية الصوتية if clicking
	 *                          the Voice Wall button fails
	 */
	@Step("Click top navigation: Voice Wall")
	public void clickVoiceWallHeaderButton() {
		try {
			waitAndClick(voiceWallHeaderButton);
		} catch (Exception e) {
			throw new RuntimeException("فشل في النقر على زر الجدارية الصوتية", e);
		}
	}

	/**
	 * 🔘 ينقر على زر "قوائم الشيوع" في الشريط العلوي Clicks the "Frequency Lists"
	 * button from the top navigation bar.
	 *
	 * @throws RuntimeException في حال فشل النقر على زر قوائم الشيوع if clicking the
	 *                          Frequency Lists button fails
	 */
	@Step("Click top navigation: Frequency Lists")
	public void clickFrequencyListsHeaderButton() {
		try {
			waitAndClick(frequencyListsHeaderButton);
		} catch (Exception e) {
			throw new RuntimeException("فشل في النقر على زر قوائم الشيوع", e);
		}
	}

	/**
	 * 🔘 ينقر على زر "المطورون" في الشريط العلوي Clicks the "Developers" button
	 * from the top navigation bar.
	 *
	 * @throws RuntimeException في حال فشل النقر على زر المطورون if clicking the
	 *                          Developers button fails
	 */
	@Step("Click top navigation: Developers")
	public void clickDevelopersHeaderButton() {
		try {
			waitAndClick(developersHeaderButton);
		} catch (Exception e) {
			throw new RuntimeException("فشل في النقر على زر المطورون", e);
		}
	}

	/**
	 * 🔘 ينقر على زر "اتصل بنا" في الشريط العلوي Clicks the "Contact Us" button
	 * from the top navigation bar.
	 *
	 * @throws RuntimeException في حال فشل النقر على زر اتصل بنا if clicking the
	 *                          Contact Us button fails
	 */
	@Step("Click top navigation: Contact Us")
	public void clickContactUsHeaderButton() {
		try {
			waitAndClick(contactUsHeaderButton);
		} catch (Exception e) {
			throw new RuntimeException("فشل في النقر على زر اتصل بنا", e);
		}
	}

	// ============= Clickable Cards on Homepage =============

	/**
	 * 🟥 ينقر على بطاقة "المدونات" Clicks the "Corpora" card on the homepage to
	 * navigate to the corpora section.
	 *
	 * @throws RuntimeException في حال فشل النقر على البطاقة if the click action
	 *                          fails
	 */
	@Step("Click ‘Corpora’ card")
	public void clickCorporaCard() {
		try {
			waitAndClick(corporaCard);
		} catch (Exception e) {
			throw new RuntimeException("فشل في النقر على بطاقة المدونات", e);
		}
	}

	/**
	 * 🟦 ينقر على بطاقة "الميزات" Clicks the "Features" card to scroll or navigate
	 * to the features section on the homepage.
	 *
	 * @throws RuntimeException في حال فشل النقر على البطاقة if the click action
	 *                          fails
	 */
	@Step("Click ‘Features’ card")
	public void clickFeaturesCard() {
		try {
			waitAndClick(featuresCard);
		} catch (Exception e) {
			throw new RuntimeException("فشل في النقر على بطاقة الميزات", e);
		}
	}

	/**
	 * 🟨 ينقر على بطاقة "الأدوات" Clicks the "Tools" card to navigate or scroll to
	 * the tools section.
	 *
	 * @throws RuntimeException في حال فشل النقر على البطاقة if the click action
	 *                          fails
	 */
	@Step("Click ‘Tools’ card")
	public void clickToolsCard() {
		try {
			waitAndClick(toolsCard);
		} catch (Exception e) {
			throw new RuntimeException("فشل في النقر على بطاقة الأدوات", e);
		}
	}

	/**
	 * 🟩 ينقر على بطاقة "إضافة مدونات" Clicks the "Add Corpora" card to open the
	 * add-corpora feature or page.
	 *
	 * @throws RuntimeException في حال فشل النقر على البطاقة if the click action
	 *                          fails
	 */
	@Step("Click ‘Add Corpora’ card")
	public void clickAddCorporaCards() {
		try {
			waitAndClick(addCorporaCards);
		} catch (Exception e) {
			throw new RuntimeException("فشل في النقر على بطاقة إضافة مدونات", e);
		}
	}

	/**
	 * 🟪 ينقر على قسم "ترويج التسجيل" Clicks the promotional section that
	 * encourages registration.
	 *
	 * @throws RuntimeException في حال فشل النقر على القسم if the click action fails
	 */
	@Step("Click ‘Registration Promo’ section")
	public void clickRegistrationSection() {
		try {
			waitAndClick(registrationSection);
		} catch (Exception e) {
			throw new RuntimeException("فشل في النقر على قسم الترويج للتسجيل", e);
		}
	}

	/**
	 * ⬇️ ينقر على زر "المزيد من المدونات" Clicks the "More Corpora" button to load
	 * or navigate to additional corpora.
	 *
	 * @throws RuntimeException في حال فشل النقر على الزر if the click action fails
	 */
	@Step("Click ‘More Corpora’ button")
	public void clickMoreCorporaButton() {
		try {
			waitAndClick(moreCorporaButton);
		} catch (Exception e) {
			throw new RuntimeException("فشل في النقر على زر المزيد من المدونات", e);
		}
	}

	// ========== Assertion & Visibility Checks ==========

	/**
	 * 🔼 النقر على زر العودة للأعلى
	 *
	 * Clicks the back-to-top button to scroll the page to the top.
	 */
	@Step("Click Back-to-Top button")
	public void clickBackToTopButton() {
		try {
			waitAndClick(backToTopButton);
		} catch (Exception e) {
			throw new RuntimeException("❌ تعذر النقر على زر العودة للأعلى");
		}
	}

	/**
	 * ✅ التحقّق من تحميل الصفحة الرئيسية Verifies that the Home page has
	 * successfully loaded by checking the title.
	 *
	 * @return true إذا كان عنوان الصفحة يحتوي على "الرئيسية"، false في حال وجود خطأ
	 *         true if the page title contains "الرئيسية"; false otherwise
	 */
	@Step("Verify Home page loaded")
	public boolean isHomePageLoaded() {
		try {
			return getPageTitle().contains("الرئيسية");
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 👁️ التحقّق من ظهور قسم "الميزات" Checks whether the "Features" section is
	 * visible to the user on the homepage.
	 *
	 * @return true إذا كان القسم ظاهرًا على الصفحة، false إذا لم يكن ظاهرًا أو حدث
	 *         خطأ true if the "Features" section is visible; false otherwise
	 */
	@Step("Is ‘Features’ section visible")
	public boolean isFeaturesSectionVisible() {
		try {
			return isElementVisible(featuresSection);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 🛠️ التحقّق من ظهور قسم "الأدوات" Checks if the "Tools" section is visible by
	 * verifying both the title and description elements.
	 *
	 * @return true إذا كانت العناصر الأساسية لقسم الأدوات ظاهرة، false إذا لم تكن
	 *         أو حدث خطأ true if tools section elements are visible; false
	 *         otherwise
	 */
	@Step("Is ‘Tools’ section visible")
	public boolean isToolsSectionVisible() {
		try {
			return isElementVisible(toolsSectionTitle) && isElementVisible(toolsSectionDesc);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 👁️ التحقّق من ظهور قسم "مدونة الشهر" Checks whether the "Corpora of the
	 * Month" section is currently visible on the page.
	 *
	 * @return true إذا كان القسم ظاهرًا، false إذا لم يكن ظاهرًا أو حدث خطأ true if
	 *         the section is visible; false otherwise
	 */
	@Step("Is ‘Corpora of the Month’ section visible")
	public boolean isCorporaOfMonthSectionVisible() {
		try {
			return isElementVisible(corporaOfMonthHeader);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 👁️ التحقق من ظهور عنصر الهيدر
	 *
	 * Checks whether the header element is currently visible on the page.
	 *
	 * @return true إذا كان ظاهرًا في الصفحة | true if header is visible; false
	 *         otherwise
	 */
	@Step("Is header element visible on page?")
	public boolean isHeaderVisible() {
		try {
			return isElementVisible(pageHeader);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 🔍 التحقق من ظهور زر العودة للأعلى
	 *
	 * Checks whether the back-to-top button is currently visible on the page.
	 *
	 * @return true إذا كان الزر ظاهرًا | true if visible; false otherwise
	 */
	@Step("Is Back-to-Top button visible?")
	public boolean isBackToTopButtonVisible() {
		try {
			return isElementVisible(backToTopButton);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 🖼️ يُرجع حالة ظهور صورة الإحصائيات Checks if the statistics image is visible
	 * in the "Falak in Numbers" section.
	 */
	public boolean isFalakStatsImageVisible() {
		return isElementVisible(falakStatsImage);
	}

	/**
	 * 📊 التحقق من ظهور قسم "فلك في أرقام" Checks whether the "Falak in Numbers"
	 * section is visible on the homepage.
	 */
	@Step("Is ‘Falak in Numbers’ section visible")
	public boolean isFalakStatsSectionVisible() {
		try {
			return isElementVisible(falakStatsSection);
		} catch (Exception e) {
			return false;
		}
	}

	// ========== Count / Retrieve Elements ==========

	/**
	 * 🔢 الحصول على عدد بطاقات محتوى المدونات Returns the total number of corpora
	 * content cards (main blog cards).
	 *
	 * @return عدد البطاقات من النوع "محتوى المدونات"، أو -1 في حال حدوث خطأ Number
	 *         of corpora content cards; returns -1 if an error occurs
	 */
	@Step("Get number of Corpora content cards")
	public int getNumberOfCorporaContentCard() {
		try {
			return countElements(corporaContentCard);
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * 🔢 الحصول على عدد بطاقات "مدونة الشهر" Returns the number of visible "Corpora
	 * of the Month" cards.
	 *
	 * @return عدد البطاقات الظاهرة، أو -1 إذا لم يتمكن من جلب العدد Number of cards
	 *         shown; returns -1 if unable to count
	 */
	@Step("Get count of ‘Corpora of the Month’ cards")
	public int getCorporaOfMonthCardsCount() {
		try {
			return countElements(corporaOfMonthCard);
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * 🔢 الحصول على عدد بطاقات الأدوات في الصفحة Returns the number of tool cards
	 * visible in the homepage tools section.
	 *
	 * @return عدد بطاقات الأدوات، أو -1 إذا حدث خطأ Number of tools cards; returns
	 *         -1 in case of failure
	 */
	@Step("Get count of Tools cards")
	public int getToolsCardsCount() {
		try {
			return countElements(toolsCards);
		} catch (Exception e) {
			return -1;
		}
	}

	// ========== Component Wrappers ==========

	/**
	 * 🧱 الحصول على مكوّن بطاقة "مدونة الشهر" Retrieves the component wrapper for
	 * the main "Corpora of the Month" card.
	 *
	 * @return {@link CorporaOfMonthCardComponent} - مكون البطاقة المغلف الذي يمكن
	 *         استخدامه للتفاعل والاختبار
	 *
	 * @throws RuntimeException إذا لم يتم العثور على العنصر
	 */
	@Step("Get ‘Corpora of the Month’ card component")
	public CorporaOfMonthCardComponent getCorporaOfMonthCard() {
		try {
			WebElement root = waitForElement(corporaOfMonthCard);
			return new CorporaOfMonthCardComponent(root, driver);
		} catch (Exception e) {
			throw new RuntimeException("Failed to retrieve CorporaOfMonthCardComponent", e);
		}
	}

	/**
	 * 📚 الحصول على جميع مكونات بطاقات المدونات Retrieves all visible Corpora cards
	 * and wraps them in {@link HomePageCorporaCardComponent} objects.
	 *
	 * @return List of {@link HomePageCorporaCardComponent} - قائمة مكونات يمكن
	 *         التفاعل معها بسهولة
	 *
	 * @throws RuntimeException إذا فشل في العثور على العناصر
	 */
	@Step("Get all Corpora card components")
	public List<HomePageCorporaCardComponent> getAllCorporaCards() {
		try {
			waitForElement(corporaContentCard);
			List<WebElement> cards = driver.findElements(corporaContentCard);
			List<HomePageCorporaCardComponent> comps = new ArrayList<>();
			for (WebElement card : cards) {
				comps.add(new HomePageCorporaCardComponent(card, driver));
			}
			return comps;
		} catch (Exception e) {
			throw new RuntimeException("Failed to retrieve CorporaCardComponents", e);
		}
	}

	/**
	 * 🖱️ الحصول على مكونات بطاقات التفاصيل (للاختبارات التي تشمل التحويم)
	 * Retrieves detail components of each corpora card, typically used for
	 * hover-based UI tests.
	 *
	 * @return List of {@link HomePageCorporaCardComponent} - مكونات تحتوي على
	 *         تفاصيل إضافية للبطاقات
	 *
	 * @throws RuntimeException إذا لم يتم العثور على المكونات
	 */
	@Step("Get all Corpora item-detail components (for hover-tests)")
	public List<HomePageCorporaCardComponent> getAllCorporaItemDetailComponents() {
		try {
			waitForElement(corporaItemDetails);
			List<WebElement> raws = driver.findElements(corporaItemDetails);
			List<HomePageCorporaCardComponent> comps = new ArrayList<>();
			for (WebElement e : raws) {
				comps.add(new HomePageCorporaCardComponent(e, driver));
			}
			return comps;
		} catch (Exception e) {
			throw new RuntimeException("Failed to retrieve CorporaItemDetailComponents", e);
		}
	}

	/**
	 * 🌐 الحصول على العناصر الخام لبطاقات المدونات Retrieves the underlying
	 * {@link WebElement} list for all Corpora cards.
	 *
	 * @return List of {@link WebElement} - العناصر الأصلية بدون تغليف (Raw
	 *         elements)
	 *
	 * @throws RuntimeException إذا فشل في جلب العناصر
	 */
	@Step("Get raw Corpora card elements")
	public List<WebElement> getAllCorporaCardElements() {
		try {
			waitForElement(corporaContentCard);
			return driver.findElements(corporaContentCard);
		} catch (Exception e) {
			throw new RuntimeException("Failed to retrieve raw corpora card elements", e);
		}
	}

	/**
	 * 🌐 الحصول على عناصر التفاصيل الأفضل لبطاقات المدونات Retrieves best-detail
	 * versions of corpora cards, usually containing richer data or layout.
	 *
	 * @return List of {@link WebElement} - تفاصيل محسّنة للبطاقات
	 *
	 * @throws RuntimeException إذا فشل في العثور عليها
	 */
	@Step("Get best corpora-item-detail card elements")
	public List<WebElement> getAllCorporaItemDetailsCards() {
		try {
			waitForElement(corporaItemDetails);
			return driver.findElements(corporaItemDetails);
		} catch (Exception e) {
			throw new RuntimeException("Failed to retrieve best corpora item-detail card elements", e);
		}
	}

	/**
	 * 🛠️ الحصول على مكونات بطاقات الأدوات
	 * 
	 * Retrieves all tool cards and wraps them in {@link ToolsCardComponent} objects
	 * for structured interaction.
	 *
	 * @return List of {@link ToolsCardComponent} - بطاقات الأدوات مغلفة للتفاعل
	 *         والاختبار
	 *
	 * @throws RuntimeException في حال الفشل في التهيئة
	 */
	@Step("Get all Tools card components")
	public List<ToolsCardComponent> getAllToolsCards() {
		try {
			waitForElement(toolsCards);
			List<WebElement> cards = driver.findElements(toolsCards);
			List<ToolsCardComponent> comps = new ArrayList<>();
			for (WebElement card : cards) {
				comps.add(new ToolsCardComponent(card, driver));
			}
			return comps;
		} catch (Exception e) {
			throw new RuntimeException("Failed to retrieve ToolsCardComponents", e);
		}
	}

	/**
	 * 🌐 الحصول على العناصر الأصلية لبطاقات الأدوات Retrieves the raw
	 * {@link WebElement} list of all tool cards without wrapping.
	 *
	 * @return List of {@link WebElement} - العناصر الخام لبطاقات الأدوات
	 *
	 * @throws RuntimeException إذا لم تُجلب العناصر بنجاح
	 */
	@Step("Get raw Tools card elements")
	public List<WebElement> getAllToolsCardElements() {
		try {
			waitForElement(toolsCards);
			return driver.findElements(toolsCards);
		} catch (Exception e) {
			throw new RuntimeException("Failed to retrieve raw tools card elements", e);
		}
	}

	// ========== Utility Methods ==========

	/**
	 * 📦 جلب جميع بطاقات "مزايا المنصة" Retrieves all feature cards on the
	 * homepage.
	 *
	 * @return List<FeatureCardComponent> قائمة تحتوي على جميع البطاقات
	 */
	@Step("Get all feature cards")
	public List<FeatureCardComponent> getAllFeatureCards() {
		waitForElement(featureCardsLocator);
		List<WebElement> cardElements = driver.findElements(featureCardsLocator);
		return cardElements.stream().map(FeatureCardComponent::new).collect(Collectors.toList());
	}

	/**
	 * 🌐 إرجاع الرابط الأساسي للموقع Returns the base URL of the website as defined
	 * in the environment configuration.
	 *
	 * @return String - الرابط الأساسي للموقع (Base URL of the application)
	 */
	@Step("Get base URL")
	public String getBaseUrl() {
		return this.baseUrl;
	}

	/**
	 * 🧾 يُرجع نص عنوان قسم "فلك في أرقام" Returns the title text of the "Falak in
	 * Numbers" section.
	 */
	public String getFalakStatsTitleText() {
		return waitForElement(falakStatsTitle).getText().trim();
	}

	/**
	 * 📄 يُرجع النص الوصفي للقسم Returns the description paragraph of the "Falak in
	 * Numbers" section.
	 */
	public String getFalakStatsDescriptionText() {
		return waitForElement(falakStatsDescription).getText().trim();
	}

	/**
	 * 🖼️ إرجاع رابط صورة فلك في أرقام.
	 *
	 * 🖼️ Returns the source (src) of Falak Stats Image.
	 *
	 * @return رابط الصورة | Image source URL
	 */
	@Step("Get Falak Stats Image src")
	public String getFalakStatsImageSrc() {
		return getAttribute(falakStatsImage, "src");
	}

	/**
	 * 🔍 الحصول على زر الانتقال إلى الأعلى (WebElement)
	 *
	 * Gets the WebElement of the Back To Top Button section for advanced
	 * operations.
	 *
	 * @return عنصر WebElement الخاص بزر الانتقال إلى الأعلى | WebElement of the
	 *         Back To Top Button
	 */
	@Step("Get Back To Top Button WebElement")
	public WebElement getBackToTopButtonElement() {
		try {
			return waitForElement(pageHeader);
		} catch (Exception e) {
			throw new RuntimeException("❌ لم يتم العثور على عنصر الهيدر");
		}
	}

	/**
	 * 🔍 الحصول على عنصر الهيدر (WebElement)
	 *
	 * Gets the WebElement of the header section for advanced operations.
	 *
	 * @return عنصر WebElement الخاص بالهيدر | WebElement of the header
	 */
	@Step("Get header WebElement")
	public WebElement getHeaderElement() {
		try {
			return waitForElement(pageHeader);
		} catch (Exception e) {
			throw new RuntimeException("❌ لم يتم العثور على عنصر الهيدر");
		}
	}

	/**
	 * 🔽 التمرير إلى قسم "مدونة الشهر" في الصفحة الرئيسية
	 * 
	 * 
	 * This method scrolls the page down to the "Corpora of the Month" section using
	 * JavaScript. It is useful when the section is not visible and needs to be
	 * brought into view before interaction.
	 *
	 * @throws RuntimeException إذا فشل العثور على القسم أو التمرير إليه Throws
	 *                          RuntimeException if the section cannot be found or
	 *                          scrolled to.
	 *
	 *                          📌 يُستخدم هذا الإجراء لضمان ظهور القسم قبل التفاعل
	 *                          معه أثناء الاختبار.
	 */
	@Step("Scroll into view: Corpora of the Month section")
	public void scrollToCorporaOfMonth() {
		try {
			WebElement section = waitForElement(corporaOfMonthHeader);
			scrollToElement(section);
			waitForElement(corporaOfMonthHeader);
		} catch (Exception e) {
			throw new RuntimeException("Failed to scroll to Corpora of the Month section", e);
		}
	}

	/**
	 * 🔽 التمرير إلى قسم "أدوات المنصة" في الصفحة الرئيسية
	 * 
	 * 
	 * This method scrolls to the "Tools" section of the home page using JavaScript.
	 * Useful when the section is below the fold and not visible upon page load.
	 *
	 * @throws RuntimeException إذا فشل في التمرير أو لم يتم العثور على القسم Throws
	 *                          RuntimeException if the element is not found or
	 *                          scrolling fails.
	 *
	 *                          📌 يُفضل استخدام هذا النوع من التمرير قبل فحص ظهور
	 *                          أدوات المنصة أو اختبار التفاعل معها.
	 */
	@Step("Scroll into view: Tools section")
	public void scrollToToolsSection() {
		try {
			WebElement section = waitForElement(toolsSectionRoot);
			scrollToElement(section);
			waitForElement(toolsSectionRoot);
		} catch (Exception e) {
			throw new RuntimeException("Failed to scroll to Tools section", e);
		}
	}

	/**
	 * 🔽 التمرير إلى قسم "المزايا" في الصفحة الرئيسية
	 * 
	 * This method scrolls to the "Features" section of the home page using
	 * JavaScript. Useful when the section is below the fold and not visible upon
	 * page load.
	 *
	 * @throws RuntimeException إذا فشل في التمرير أو لم يتم العثور على القسم Throws
	 *                          RuntimeException if the element is not found or
	 *                          scrolling fails.
	 *
	 *                          📌 يُفضل استخدام هذا النوع من التمرير قبل فحص ظهور
	 *                          أدوات المنصة أو اختبار التفاعل معها.
	 */
	@Step("Scroll into view: Features section")
	public void scrollToFeaturesSection() {
		try {
			WebElement section = waitForElement(featuresSection);
			scrollToElement(section);
			waitForElement(featuresSection);
		} catch (Exception e) {
			throw new RuntimeException("Failed to scroll to Tools section", e);
		}
	}

	/**
	 * 🔻 الانتظار حتى يتم تحميل تذييل الصفحة
	 * 
	 * (Footer) Waits for the footer element to become visible. This ensures the
	 * page has fully loaded.
	 *
	 * @throws RuntimeException في حال لم يظهر العنصر خلال المهلة المحددة Throws
	 *                          exception if the footer does not load within timeout
	 */
	@Step("Wait for footer to load")
	public void waitForFooterToLoad() {
		try {
			waitForElement(footerDivision);
		} catch (Exception e) {
			throw new RuntimeException("Footer did not load", e);
		}
	}

	/**
	 * ✅ انتظار ذكي حتى يظهر قسم "Corpora" وتصبح البطاقات قابلة للعرض
	 *
	 * ✅ Smart wait until the Corpora section is ready and cards are visible
	 *
	 * @param timeout  المدة القصوى للانتظار (Maximum wait duration)
	 * @param minCards الحد الأدنى من البطاقات المتوقع ظهورها (Minimum number of
	 *                 cards expected)
	 */
	@Step("✅ Wait for Corpora section to be ready with at least {1} cards")
	public void waitForCorporaSectionReady(Duration timeout, int minCards) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, timeout);

			// 1️⃣ ظهور القسم
			// Wait until the Corpora section becomes visible
			WebElement section = wait.until(ExpectedConditions.visibilityOfElementLocated(corporaContentCard));
			Allure.step("📌 Corpora section became visible");

			// 2️⃣ التمرير إلى القسم (منتصف الشاشة) لتفعيل تأثيرات الواجهة
			// Scroll the section into view to activate hover effects smoothly
			((JavascriptExecutor) driver)
					.executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", section);
			Allure.step("📌 Scrolled into Corpora section");

			// 3️⃣ تحقق من وجود العدد الأدنى من البطاقات
			// Ensure that at least 'minCards' cards are present
			wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(corporaContentCard, Math.max(0, minCards - 1)));
			Allure.step("📌 At least " + minCards + " cards are present");

			// 4️⃣ تأكيد أن أول بطاقة مرئية فعلًا (ليست مخفية أو خارج الشاشة)
			// Confirm that the first card is actually visible and rendered
			wait.until(d -> {
				List<WebElement> cards = d.findElements(corporaContentCard);
				return !cards.isEmpty() && cards.get(0).isDisplayed() && cards.get(0).getSize().getHeight() > 0;
			});
			Allure.step("📌 First card is properly visible and rendered");

			System.out.println("🟢 Corpora section is ready with at least " + minCards + " cards");

		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to wait for Corpora section readiness", e);
		}
	}

}
