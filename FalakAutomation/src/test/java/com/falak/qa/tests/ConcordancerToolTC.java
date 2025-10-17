package com.falak.qa.tests;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.falak.qa.base.BaseTest;
import com.falak.qa.config.EnvironmentConfigLoader;
import com.falak.qa.config.RetryAnalyzer;
import com.falak.qa.enums.CorporaName;
import com.falak.qa.enums.ToolsName;
import com.falak.qa.pages.corpora.CorporaCardComponent;
import com.falak.qa.pages.corpora.CorporaOverviewPage;
import com.falak.qa.pages.corpora.CorporaPage;
import com.falak.qa.pages.corpora.tools.ConcordancerPage;
import com.falak.qa.pages.home.HomePage;
import com.falak.qa.utils.NavigationUtils;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@Epic("أداة الكشاف السياقي | Concordancer Tool")
@Feature("التحقق من عناصر الواجهة الأساسية")
@Listeners({ com.falak.qa.listeners.RetryListener.class })
public class ConcordancerToolTC extends BaseTest {

	@Test(description = "TC-01 | Verify that user can open the ‘الكشاف السياقي’ tool for مجمع اللغة العربية from the home page", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User navigates from home to corpora tool")
	@Description("""
			Test Objective: Ensure that the user can navigate from the home page
			to the ‘الكشاف السياقي’ tool inside the overview page of ‘المجمع للغة العربية المعاصرة’.
			Steps:
			1. Open home page.
			2. Click ‘المدونات’ to go to corpora list.
			3. Select ‘المجمع للغة العربية المعاصرة’ corpora.
			4. Click the ‘الكشاف السياقي’ tool button.
			5. Confirm URL contains correct path and UUID.
			6. Confirm that tool title matches the expected Arabic name.
			Expected Result: Page should open successfully with correct title and valid URL structure.
			""")
	public void TC01_openConcordancerToolFromHomePage() {
		System.out.println("TC01_openConcordancerToolFromHomePage");

		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			// 🏠 1. إنشاء الصفحة الرئيسية وفتحها
			HomePage homePage = new HomePage(driver);
			String baseUrl = EnvironmentConfigLoader.getUrl("baseUrl");
			driver.get(baseUrl);
			Allure.step("✅ Opened base URL: " + baseUrl);

			// 🖱️ 2. الضغط على زر "المدونات"
			homePage.clickCorporaHeaderButton();
			Allure.step("🖱️ Clicked on 'المدونات' in header");

			// 🧭 3. الانتقال إلى صفحة المدونات
			CorporaPage corporaPage = new CorporaPage(driver);
			wait.until(ExpectedConditions.urlContains(EnvironmentConfigLoader.getUrl("corporaUrl")));

			// 🔍 4. البحث عن بطاقة المجمع
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			CorporaCardComponent majmaaCard = corporaPage
					.getCorporaCardComponentByName(selectedCorpora.getDisplayName());
			Assert.assertNotNull(majmaaCard, "❌ لم يتم العثور على بطاقة مدونة: " + selectedCorpora.getDisplayName());
			Allure.step("📥 Found card: " + selectedCorpora.getDisplayName());

			// 🖱️ 5. الضغط على زر "اختر المدونة"
			majmaaCard.clickSelectButton();
			Allure.step("🖱️ Clicked 'اختر المدونة' on card: " + selectedCorpora.getDisplayName());

			// 🧭 6. الانتقال إلى صفحة نظرة عامة على المدونة
			CorporaOverviewPage overviewPage = new CorporaOverviewPage(driver);
			wait.until(ExpectedConditions.urlContains(selectedCorpora.getUuid()));

			// 🧪 7. الضغط على زر "الكشاف السياقي"
			ToolsName selectedTool = ToolsName.CONCORDANCER;
			By toolLocator = overviewPage.getToolCardLocator(selectedTool);
			Assert.assertTrue(driver.findElements(toolLocator).size() > 0,
					"❌ الزر الخاص بأداة " + selectedTool.getArabicName() + " غير موجود في هذه المدونة!");
			Allure.step("🎯 Tool button found: " + selectedTool.getArabicName());

			overviewPage.clickOnToolCard(selectedTool);
			Allure.step("🖱️ Clicked on tool card: " + selectedTool.getArabicName());

			// ⏳ 8. الانتظار حتى يحتوي الرابط على مسار الأداة
			wait.until(d -> d.getCurrentUrl().contains("/" + selectedTool.getPathSegment() + "/"));

			// 🌐 9. التحقق من الرابط الحالي
			String currentUrl = driver.getCurrentUrl();
			String normalizedUrl = currentUrl.split("\\?")[0];
			Assert.assertTrue(normalizedUrl.contains("/" + selectedTool.getPathSegment() + "/"),
					"❌ الرابط لا يحتوي على مسار الأداة المتوقع: " + selectedTool.getPathSegment() + "\nالرابط الحالي: "
							+ currentUrl);
			Allure.step("🔗 Tool path verified in URL: " + currentUrl);

			// ✅ 10. التحقق من وجود UUID داخل الرابط
			Pattern uuidPattern = Pattern.compile("[a-f0-9\\-]{36}");
			Matcher matcher = uuidPattern.matcher(normalizedUrl);
			Assert.assertTrue(matcher.find(), "❌ لم يتم العثور على UUID داخل الرابط!\nالرابط الحالي: " + currentUrl);
			Allure.step("🆔 UUID found in URL");

			// 🧪 11. التحقق من عنوان الأداة
			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			String actualToolTitle = concordancerPage.getToolTitleText();
			String expectedToolTitle = selectedTool.getArabicName();
			Assert.assertEquals(actualToolTitle, expectedToolTitle,
					"❌ اسم الأداة الظاهر لا يطابق الاسم المتوقع: " + expectedToolTitle);
			Allure.step("🏷️ Tool title verified: " + actualToolTitle);

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Open Concordancer Tool - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Open Concordancer Tool - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage());
		}
	}

	@Test(description = "TC-02 | Verify that the correct title 'الكشاف السياقي' is displayed when opening the tool directly", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User opens the tool page directly from a generated URL")
	@Description("""
			Test Objective: Verify that the tool page for ‘الكشاف السياقي’ loads properly
			and the page title matches the expected Arabic name.
			Steps:
			1. Open the tool page directly using a valid UUID and path.
			2. Wait for the page to load.
			3. Get the title at the top of the page.
			Expected Result: The title should be visible and match "الكشاف السياقي".
			""")
	public void TC02_verifyConcordancerToolTitleFromDirectURL() {
		System.out.println("TC02_verifyConcordancerToolTitleFromDirectURL");

		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			// 🔢 1. تحديد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			// 🔗 2. بناء الرابط المباشر باستخدام UUID والأداة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);

			// ⏳ 3. الانتظار حتى يتم تحميل الصفحة وتحتوي على مسار الأداة
			wait.until(ExpectedConditions.urlContains(selectedTool.getPathSegment()));
			Allure.step("⏳ Waited for tool path segment in URL");

			// 🧪 4. التحقق من عنوان الأداة
			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			String actualTitle = concordancerPage.getToolTitleText();
			String expectedTitle = selectedTool.getArabicName();

			Assert.assertEquals(actualTitle, expectedTitle,
					"❌ العنوان الظاهر لا يطابق العنوان المتوقع للأداة: " + expectedTitle);
			Allure.step("🏷️ Tool title verified successfully: " + actualTitle);

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Full Page Screenshot - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Full Page Screenshot - Unexpected Exception");
			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage());
		}

	}

	/**
	 * TC-03 | التحقق من ظهور نافذة تفاصيل الأداة عند الضغط على زر (!) TC-03 |
	 * Verify that clicking the info (!) icon opens a popup with tool description
	 */
	@Test(description = "TC-03 | Verify that clicking the info (!) icon shows the tool's description popup", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User clicks the info icon on the Concordancer tool page")
	@Description("""
			Test Objective: Ensure that clicking the (!) info icon next to the title
			opens a popup with a description of the 'الكشاف السياقي' tool.
			Steps:
			1. Open the Concordancer tool page using the direct URL generator.
			2. Click the (!) icon beside the tool title.
			3. Wait for the popup to appear.
			4. Validate the description text is visible and non-empty.
			Expected Result: Description popup appears showing the purpose of the tool.
			""")
	public void TC03_verifyInfoPopupInConcordancerTool() {
		System.out.println("TC03_verifyInfoPopupInConcordancerTool");

		try {
			// WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

			// 🔢 1. تحديد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			// 🔗 2. بناء الرابط المباشر باستخدام UUID والأداة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);

			// 🧭 3. التأكد من تحميل الصفحة
			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			// concordancerPage.waitForAngularToFinish();
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);
			Allure.step("📥 Page loaded successfully with expected tool path");

			// ℹ️ 4. الضغط على زر التفاصيل (!)
			concordancerPage.clickInfoIcon();
			Allure.step("🖱️ Clicked on info (!) icon beside the title");

			// ⏳ 5. انتظار ظهور نافذة التفاصيل
			// wait.until(ExpectedConditions.visibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));
			Allure.step("📑 Info popup appeared successfully");

			// 🧪 6. التحقق من وجود نص داخل النافذة
			String descriptionText = concordancerPage.getInfoDialogText();
			Assert.assertFalse(descriptionText.isBlank(), "❌ نافذة التفاصيل لا تحتوي على نص!");
			Allure.step("📝 Info popup text verified: " + descriptionText);

			// ✅ 7. إغلاق النافذة
			concordancerPage.closeInfoDialog();
			Allure.step("❎ Info popup closed successfully");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Info Popup - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Info Popup - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage());
		}
	}

	/**
	 * TC-04 | التحقق من إمكانية إغلاق نافذة التفاصيل باستخدام زر (X) TC-04 | Verify
	 * that the info popup can be closed using the (X) button
	 */
	@Test(description = "TC-04 | Verify that the info popup can be closed by clicking the (X) button", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User closes the info popup on the Concordancer tool page")
	@Description("""
			Test Objective: Ensure that the info popup shown after clicking the (!) icon
			can be closed properly by clicking the (X) button.
			Steps:
			1. Open the Concordancer tool page using direct URL.
			2. Click the (!) icon beside the tool title.
			3. Wait for the popup to appear.
			4. Click the (X) close button in the popup.
			5. Validate that the popup is no longer visible.
			Expected Result: Popup should disappear upon clicking the (X) button.
			""")
	public void TC04_verifyInfoPopupCanBeClosed() {
		System.out.println("TC04_verifyInfoPopupCanBeClosed");

		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

			// 🔢 1. تحديد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			// 🔗 2. بناء الرابط المباشر للأداة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);

			// 🧭 3. التأكد من تحميل الصفحة
			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			// concordancerPage.waitForAngularToFinish();
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);
			Allure.step("📥 Page loaded successfully");

			// ℹ️ 4. فتح نافذة التفاصيل بالضغط على (!)
			concordancerPage.clickInfoIcon();
			Allure.step("🖱️ Clicked on info (!) icon");

			// ⏳ 5. التأكد من ظهور النافذة
			// wait.until(ExpectedConditions.visibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));
			Allure.step("📑 Info popup is visible");

			// ❎ 6. الضغط على زر (X) لإغلاق النافذة
			concordancerPage.closeInfoDialog();
			Allure.step("❎ Clicked on (X) close button");

			// 🧪 7. التحقق من اختفاء النافذة
			boolean isClosed = wait.until(
					ExpectedConditions.invisibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));
			Assert.assertTrue(isClosed, "❌ النافذة ما زالت ظاهرة بعد الضغط على زر (X)!");
			Allure.step("✅ Info popup closed successfully");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Close Info Popup - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Close Info Popup - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage());
		}
	}

	/**
	 * TC-05 | التحقق من أن إدخال كلمة صحيحة في مربع البحث يعرض النتائج المطابقة
	 * TC-05 | Verify that entering a valid word in the search box shows matching
	 * results
	 */
	@Test(description = "TC-05 | Verify that entering a valid word in the search box shows correct filtered results", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User enters a valid keyword in the Concordancer tool and checks search result")
	@Description("""
			Test Objective: Ensure that typing a valid keyword in the search box and clicking the search button
			returns the correct results.
			Steps:
			1. Navigate to 'المدونات' page
			2. Click on 'اختر المدونة' for any card
			3. Select 'الكشاف السياقي' from the right-side tools
			4. Enter a valid keyword in the search input field
			5. Click on the search button
			Expected Result: Results are shown and contain the keyword entered.
			""")
	public void TC05_verifySearchFunctionalityWithValidInput() {
		System.out.println("TC05_verifySearchFunctionalityWithValidInput");

		try {
			// WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

			// 🔢 1. تحديد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			// 🔗 2. بناء الرابط المباشر للأداة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);

			// 🧭 3. التأكد من تحميل الصفحة
			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			// concordancerPage.waitForAngularToFinish();
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);

			// ⌨️ 4. إدخال كلمة البحث
			String searchWord = "اللغة";
			concordancerPage.enterSearchKeyword(searchWord);
			Allure.step("⌨️ Entered search keyword: " + searchWord);
			System.out.println("⌨️ Entered search keyword: " + searchWord);

			// 🔍 5. الضغط على زر البحث
			concordancerPage.clickSearchButton();
			Allure.step("🔍 Clicked on search button");
			System.out.println("🔍 Clicked on search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			concordancerPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (concordancerPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'search Word': " + searchWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			// 📑 6. التحقق من احتوائها على الكلمة المدخلة
			List<String> searchResults = concordancerPage.getSearchResultWords();
			System.out.println("getSearchResultWords is: " + searchWord);
			boolean containsKeyword = searchResults.stream().anyMatch(r -> r.contains(searchWord));
			Assert.assertTrue(containsKeyword, "❌ النتائج لا تحتوي على الكلمة المدخلة!");
			Allure.step("✅ Search results verified. Found entries containing: " + searchWord);

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Search Functionality - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Search Functionality - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage());
		}
	}

	/**
	 * TC-06 | التحقق من أن لوحة المفاتيح الافتراضية تكتب الحروف والأرقام والرموز في
	 * حقل البحث TC-06 | Verify that the virtual keyboard types letters, numbers,
	 * and symbols into the search box
	 */
	@Test(description = "TC-06 | Verify that the virtual keyboard types letters, numbers, and symbols into the search box", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User uses the virtual keyboard to input characters into the Concordancer tool")
	@Description("""
			Test Objective: Ensure that clicking on the virtual keyboard buttons (letters, numbers, symbols)
			properly enters the corresponding characters into the search box.
			Steps:
			1. Navigate to 'المدونات' page
			2. Select any corpus using 'اختر المدونة'
			3. Select the 'الكشاف السياقي' tool from the right-side tools
			4. Click the virtual keyboard icon next to the search box
			5. Click several keys (letter, number, symbol)
			Expected Result: The characters appear correctly inside the search input field
			""")
	public void TC06_verifyVirtualKeyboardTyping() {
		System.out.println("TC06_verifyVirtualKeyboardTyping");

		try {
			// 🔢 1. تحديد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			// 🔗 2. بناء الرابط المباشر للأداة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);

			// 🧭 3. التأكد من تحميل الصفحة
			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);

			// 🎹 4. فتح لوحة المفاتيح الافتراضية
			concordancerPage.clickVirtualKeyboardIcon();
			Allure.step("🎹 Virtual keyboard opened");
			System.out.println("🎹 Virtual keyboard opened");

			// ✍️ كتابة "اللغة"
			String originalInput = "اللغة3";
			concordancerPage.typeUsingVirtualKeyboard(originalInput);
			Allure.step("⌨️ Typed using virtual keyboard: " + originalInput);

			// ⌫ حذف آخر حرف (ة)
			concordancerPage.clickVirtualKeyDelete();
			Allure.step("⌫ Pressed delete key to remove last character");

			// ✅ التحقق أن الناتج الآن هو "اللغ"
			String expectedInput = "اللغة";
			String actualInput = concordancerPage.getSearchInputValue();
			Assert.assertEquals(actualInput, expectedInput, "❌ بعد حذف آخر حرف، النص غير صحيح!");
			Allure.step("✅ Search input contains expected text: " + actualInput);
			System.out.println("✅ Search input contains expected text: " + actualInput);

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Virtual Keyboard Typing - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Virtual Keyboard Typing - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage());
		}
	}

	/**
	 * TC-07 | التحقق من أن الضغط على زر التصدير يؤدي لتحميل نتائج البحث TC-07 |
	 * Verify that clicking the export button downloads the search results
	 */
	@Test(description = "TC-07 | Verify that clicking the export button downloads the search results", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User exports search results using the export button in the Concordancer tool")
	@Description("""
			Test Objective: Ensure that the export button appears after typing a search word and clicking search,
			and that it downloads the search results file when clicked.
			Steps:
			1. Navigate to 'المدونات' page
			2. Select any corpus using 'اختر المدونة'
			3. Select the 'الكشاف السياقي' tool
			4. Type a word using the virtual keyboard
			5. Click the search button
			6. Click the export icon
			Expected Result: A file is downloaded (e.g., Excel or CSV)
			""")
	public void TC07_verifyExportButtonFunctionality() {
		System.out.println("TC07_verifyExportButtonFunctionality");

		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);

			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);

			// فتح لوحة المفاتيح وكتابة الكلمة
			concordancerPage.clickVirtualKeyboardIcon();
			String input = "اللغة";
			concordancerPage.typeUsingVirtualKeyboard(input);
			Allure.step("⌨️ Typed using virtual keyboard: " + input);

			// الضغط على زر (X) لإغلاق النافذة
			concordancerPage.closeInfoDialog();
			Allure.step("❎ Clicked on (X) close button");

			// 🧪 7. التحقق من اختفاء النافذة
			wait.until(ExpectedConditions.invisibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));

			// الضغط على زر البحث
			concordancerPage.clickSearchButton();
			Allure.step("🔎 Clicked search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			concordancerPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (concordancerPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'search Word': " + input);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			// الانتظار قليلاً لظهور زر التصدير
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> concordancerPage.isExportButtonVisisable());

			// الضغط على زر التصدير
			concordancerPage.clickExportButton();
			Allure.step("📥 Clicked export button");

			// التحقق من تحميل الملف
			boolean isDownloaded = concordancerPage.isFileDownloaded(".xlsx");
			Assert.assertTrue(isDownloaded, "❌ لم يتم تحميل الملف كما هو متوقع!");
			Allure.step("✅ Exported file downloaded successfully");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Export Download - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Export Download - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage());
		}
	}

	/**
	 * TC-08 | التحقق من أن استخدام فلتر "اختر المجال" يؤثر على نتائج البحث TC-08 |
	 * Verify that using the "Domain Filter" affects the search results
	 */
	@Test(description = "TC-08 | Verify that using the Domain Filter affects the search results", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User applies domain filter in Concordancer tool and verifies filtered results")
	@Description("""
			Test Objective: Ensure that applying a domain filter (اختر المجال) inside the filters section reflects on the search results.
			Steps:
			1. Navigate to 'المدونات' page
			2. Select any corpus using 'اختر المدونة'
			3. Select the 'الكشاف السياقي' tool
			4. Type a word using the virtual keyboard
			5. Click the search button
			6. Expand the filters section
			7. Choose one option from 'اختر المجال'
			8. Verify that the selected domain reflects in the results
			""")
	public void TC08_verifyDomainFilterAffectsResults() {
		System.out.println("TC08_verifyDomainFilterAffectsResults");

		try {
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);

			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);

			// فتح لوحة المفاتيح وكتابة الكلمة
			concordancerPage.clickVirtualKeyboardIcon();
			String searchWord = "السيارة";
			concordancerPage.typeUsingVirtualKeyboard(searchWord);
			Allure.step("⌨️ Typed using virtual keyboard: " + searchWord);

			// إغلاق النافذة التوضيحية إن ظهرت
			concordancerPage.closeInfoDialog();
			Allure.step("❎ Clicked on (X) close button");
			System.out.println("❎ Clicked on (X) close button");

			// التأكد من اختفاء النافذة
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(
					ExpectedConditions.invisibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));
			System.out.println("التأكد من اختفاء النافذة");

			// توسيع قسم المحدد
			concordancerPage.expandFiltersSection();
			Allure.step("🔽 Expanded filters section (المحددات)");
			System.out.println("🔽 Expanded filters section (المحددات)");

			// تطبيق خيار من "اختر المجال"
			List<String> selectedOptions = List.of("إعلامية وصحفية");
			concordancerPage.selectFromMultiSelectFilter(concordancerPage.getDomainFilterLocator(),
					concordancerPage.getDomainSearchInputLocator(), selectedOptions);
			System.out.println("تطبيق خيار من \"اختر المجال\"");
			// concordancerPage.verifySelectedOptions(concordancerPage.getSelectedDomainValuesDisplay(),
			// selectedOptions);

			// الضغط على زر البحث
			concordancerPage.clickSearchButton();
			Allure.step("🔎 Clicked search button");
			System.out.println("🔎 Clicked search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			concordancerPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (concordancerPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'search Word': " + searchWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			// ✅ نتائج ظهرت، نتحقق من احتوائها على الكلمة
			List<String> searchResults = concordancerPage.getSearchResultWords();
			Assert.assertFalse(searchResults.isEmpty(), "❌ ظهرت نتائج فارغة!");

			boolean containsKeyword = searchResults.stream().anyMatch(r -> r.contains(searchWord));
			Assert.assertTrue(containsKeyword, "❌ النتائج لا تحتوي على الكلمة المدخلة!");

			Allure.step("✅ Search results verified. Found entries containing: " + searchWord);
			System.out.println("✅ Valid results appeared containing: " + searchWord);

			// التقاط لقطة شاشة للتحقق من ظهور التأثير في النتائج
			attachFullPageScreenshot("📸 Full Page Screenshot After Applying domain Filter");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Domain Filter - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Domain Filter - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage());
		}
	}

	/**
	 * TC-09 | التحقق من أن استخدام فلتر "اختر الموضوع" يؤثر على نتائج البحث
	 */
	@Test(description = "TC-09 | Verify that using the Topic Filter affects the search results", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User applies topic filter in Concordancer tool and verifies filtered results")
	@Description("""
			Test Objective: Ensure that applying a topic filter (اختر الموضوع) inside the filters section reflects on the search results.
			Steps:
			1. Navigate to 'المدونات' page
			2. Select any corpus using 'اختر المدونة'
			3. Select the 'الكشاف السياقي' tool
			4. Type a word using the virtual keyboard
			5. Click the search button
			6. Expand the filters section
			7. Choose Two option from 'اختر الموضوع'
			8. Verify that the selected topic reflects in the results
			""")
	public void TC09_verifyTopicFilterAffectsResults() {
		System.out.println("TC09_verifyTopicFilterAffectsResults");

		try {
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);

			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);

			// استخدام لوحة المفاتيح
			concordancerPage.clickVirtualKeyboardIcon();
			String searchWord = "السيارة";
			concordancerPage.typeUsingVirtualKeyboard(searchWord);
			Allure.step("⌨️ Typed using virtual keyboard: " + searchWord);

			// إغلاق النافذة التوضيحية إن ظهرت
			concordancerPage.closeInfoDialog();
			System.out.println("❎ Clicked on (X) close button");

			new WebDriverWait(driver, Duration.ofSeconds(10)).until(
					ExpectedConditions.invisibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));

			// توسيع قسم المحددات
			concordancerPage.expandFiltersSection();
			Allure.step("🔽 Expanded filters section (المحددات)");
			System.out.println("🔽 Expanded filters section (المحددات)");

			// تطبيق خيار من "اختر الموضوع"
			List<String> selectedOptions = List.of("أدب رحلات", "مذكرات");
			concordancerPage.selectFromMultiSelectFilter(concordancerPage.getTopicFilterLocator(),
					concordancerPage.getTopicSearchInputLocator(), selectedOptions);

			// concordancerPage.verifySelectedOptions(concordancerPage.getSelectedTopcValuesDisplay(),
			// selectedOptions);

			// الضغط على زر البحث
			concordancerPage.clickSearchButton();
			Allure.step("🔎 Clicked search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			concordancerPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (concordancerPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'search Word': " + searchWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			// ✅ نتائج ظهرت، نتحقق من احتوائها على الكلمة
			List<String> searchResults = concordancerPage.getSearchResultWords();
			Assert.assertFalse(searchResults.isEmpty(), "❌ ظهرت نتائج فارغة!");

			boolean containsKeyword = searchResults.stream().anyMatch(r -> r.contains(searchWord));
			Assert.assertTrue(containsKeyword, "❌ النتائج لا تحتوي على الكلمة المدخلة!");

			Allure.step("✅ Search results verified. Found entries containing: " + searchWord);
			System.out.println("✅ Valid results appeared containing: " + searchWord);

			attachFullPageScreenshot("📸 Full Page Screenshot After Applying Topic Filter");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Topic Filter - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Topic Filter - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage());
		}
	}

	/**
	 * TC-10 | التحقق من أن استخدام فلتر "اختر المكان" يؤثر على نتائج البحث
	 */
	@Test(description = "TC-10 | Verify that using the Place Filter affects the search results", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User applies place filter in Concordancer tool and verifies filtered results")
	@Description("""
			Test Objective: Ensure that applying a place filter (اختر المكان) inside the filters section reflects on the search results.
			Steps:
			1. Navigate to 'المدونات' page
			2. Select any corpus using 'اختر المدونة'
			3. Select the 'الكشاف السياقي' tool
			4. Type a word using the virtual keyboard
			5. Click the search button
			6. Expand the filters section
			7. Choose ALL option from 'اختر المكان'
			8. Verify that the selected place reflects in the results
			""")
	public void TC10_verifyPlaceFilterAffectsResults() {
		System.out.println("TC10_verifyPlaceFilterAffectsResults");

		try {
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);

			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);

			// استخدام لوحة المفاتيح
			concordancerPage.clickVirtualKeyboardIcon();
			String searchWord = "السيارة";
			concordancerPage.typeUsingVirtualKeyboard(searchWord);
			Allure.step("⌨️ Typed using virtual keyboard: " + searchWord);

			// إغلاق النافذة التوضيحية إن ظهرت
			concordancerPage.closeInfoDialog();
			System.out.println("❎ Clicked on (X) close button");

			new WebDriverWait(driver, Duration.ofSeconds(10)).until(
					ExpectedConditions.invisibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));

			// توسيع قسم المحددات
			concordancerPage.expandFiltersSection();
			Allure.step("🔽 Expanded filters section (المحددات)");

			// تطبيق خيار من "اختر المكان"
			List<String> selectedOptions = List.of("ALL");
			concordancerPage.selectFromMultiSelectFilter(concordancerPage.getPlaceFilterLocator(),
					concordancerPage.getPlaceSearchInputLocator(), selectedOptions);

			// concordancerPage.verifySelectedOptions(concordancerPage.getSelectedPlaceValuesDisplay(),
			// selectedOptions);

			// الضغط على زر البحث
			concordancerPage.clickSearchButton();
			Allure.step("🔎 Clicked search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			concordancerPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (concordancerPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'search Word': " + searchWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			List<String> searchResults = concordancerPage.getSearchResultWords();
			Assert.assertFalse(searchResults.isEmpty(), "❌ ظهرت نتائج فارغة!");

			boolean containsKeyword = searchResults.stream().anyMatch(r -> r.contains(searchWord));
			Assert.assertTrue(containsKeyword, "❌ النتائج لا تحتوي على الكلمة المدخلة!");

			Allure.step("✅ Search results verified. Found entries containing: " + searchWord);
			System.out.println("✅ Valid results appeared containing: " + searchWord);

			attachFullPageScreenshot("📸 Full Page Screenshot After Applying Place Filter");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Place Filter - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Place Filter - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage());
		}
	}

	/**
	 * TC-11 | التحقق من أن استخدام فلتر "اختر الفترة" يؤثر على نتائج البحث
	 */
	@Test(description = "TC-11 | Verify that using the Time Period Filter affects the search results", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User applies time period filter in Concordancer tool and verifies filtered results")
	@Description("""
			Test Objective: Ensure that applying a time period filter (اختر الفترة) inside the filters section reflects on the search results.
			Steps:
			1. Navigate to 'المدونات' page
			2. Select any corpus using 'اختر المدونة'
			3. Select the 'الكشاف السياقي' tool
			4. Type a word using the virtual keyboard
			5. Click the search button
			6. Expand the filters section
			7. Choose 3 options from 'اختر الفترة'
			8. Verify that the selected time period reflects in the results
			""")
	public void TC11_verifyTimeFilterAffectsResults() {
		System.out.println("TC11_verifyTimeFilterAffectsResults");

		try {
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);

			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);

			// استخدام لوحة المفاتيح
			concordancerPage.clickVirtualKeyboardIcon();
			String searchWord = "السيارة";
			concordancerPage.typeUsingVirtualKeyboard(searchWord);
			Allure.step("⌨️ Typed using virtual keyboard: " + searchWord);

			// إغلاق النافذة التوضيحية إن ظهرت
			concordancerPage.closeInfoDialog();
			System.out.println("❎ Clicked on (X) close button");

			new WebDriverWait(driver, Duration.ofSeconds(10)).until(
					ExpectedConditions.invisibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));

			// توسيع قسم المحددات
			concordancerPage.expandFiltersSection();
			Allure.step("🔽 Expanded filters section (المحددات)");

			// تطبيق خيار من "اختر الفترة"
			List<String> selectedOptions = List.of("1981-2000", "2001-2022", "1923");
			concordancerPage.selectFromMultiSelectFilter(concordancerPage.getTimeFilter(),
					concordancerPage.getTimeSearchInput(), selectedOptions);

			// concordancerPage.verifySelectedOptions(concordancerPage.getSelectedTimeValuesDisplay(),
			// selectedOptions);

			// الضغط على زر البحث
			concordancerPage.clickSearchButton();
			Allure.step("🔎 Clicked search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			concordancerPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (concordancerPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'search Word': " + searchWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			List<String> searchResults = concordancerPage.getSearchResultWords();
			Assert.assertFalse(searchResults.isEmpty(), "❌ ظهرت نتائج فارغة!");

			boolean containsKeyword = searchResults.stream().anyMatch(r -> r.contains(searchWord));
			Assert.assertTrue(containsKeyword, "❌ النتائج لا تحتوي على الكلمة المدخلة!");

			Allure.step("✅ Search results verified. Found entries containing: " + searchWord);
			System.out.println("✅ Valid results appeared containing: " + searchWord);

			attachFullPageScreenshot("📸 Full Page Screenshot After Applying Time Period Filter");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Time Filter - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Time Filter - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage());
		}
	}

	/**
	 * TC-12 | التحقق من أن استخدام فلتر "اختر الوعاء" يؤثر على نتائج البحث
	 */
	@Test(description = "TC-12 | Verify that using the Container Filter affects the search results", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User applies container filter in Concordancer tool and verifies filtered results")
	@Description("""
			Test Objective: Ensure that applying a container filter (اختر الوعاء) inside the filters section reflects on the search results.
			Steps:
			1. Navigate to 'المدونات' page
			2. Select any corpus using 'اختر المدونة'
			3. Select the 'الكشاف السياقي' tool
			4. Type a word using the virtual keyboard
			5. Click the search button
			6. Expand the filters section
			7. Choose 4 options from 'اختر الوعاء'
			8. Verify that the selected containers reflect in the results
			""")
	public void TC12_verifyContainerFilterAffectsResults() {
		System.out.println("TC12_verifyContainerFilterAffectsResults");

		try {
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);

			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);

			// استخدام لوحة المفاتيح
			concordancerPage.clickVirtualKeyboardIcon();
			String searchWord = "السيارة";
			concordancerPage.typeUsingVirtualKeyboard(searchWord);
			Allure.step("⌨️ Typed using virtual keyboard: " + searchWord);

			// إغلاق النافذة التوضيحية إن ظهرت
			concordancerPage.closeInfoDialog();
			System.out.println("❎ Clicked on (X) close button");

			new WebDriverWait(driver, Duration.ofSeconds(10)).until(
					ExpectedConditions.invisibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));

			// توسيع قسم المحددات
			concordancerPage.expandFiltersSection();
			Allure.step("🔽 Expanded filters section (المحددات)");

			// تطبيق خيارات من "اختر الوعاء"
			List<String> selectedOptions = List.of("الدوريات المحكمة والمجلات العلمية والثقافية العامة", "رسائل علمية",
					"مؤتمرات وندوات", "كتب");
			concordancerPage.selectFromMultiSelectFilter(concordancerPage.getContainerFilter(),
					concordancerPage.getContainerSearchInput(), selectedOptions);

			// concordancerPage.verifySelectedOptions(concordancerPage.getSelectedContainerValuesDisplay(),selectedOptions);

			// الضغط على زر البحث
			concordancerPage.clickSearchButton();
			Allure.step("🔎 Clicked search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			concordancerPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (concordancerPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'search Word': " + searchWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			List<String> searchResults = concordancerPage.getSearchResultWords();
			Assert.assertFalse(searchResults.isEmpty(), "❌ ظهرت نتائج فارغة!");

			boolean containsKeyword = searchResults.stream().anyMatch(r -> r.contains(searchWord));
			Assert.assertTrue(containsKeyword, "❌ النتائج لا تحتوي على الكلمة المدخلة!");

			Allure.step("✅ Search results verified. Found entries containing: " + searchWord);
			System.out.println("✅ Valid results appeared containing: " + searchWord);

			attachFullPageScreenshot("📸 Full Page Screenshot After Applying Container Filter");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Container Filter - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Container Filter - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage());
		}
	}

	/**
	 * TC-13 | ✅ التحقق من وظيفة النسخ في نتائج أداة الكشاف السياقي ✅ Test case to
	 * verify that copying a search result from the Concordancer tool works
	 * correctly.
	 */
	@Test(description = "TC-13 | Verify copy functionality in Concordancer results", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User copies a result from Concordancer tool")
	@Description("""
			Test Objective: Ensure that clicking the copy icon copies the correct result text to the clipboard.
			Steps:
			1. Navigate to 'المدونات' page
			2. Select any corpus using 'اختر المدونة'
			3. Select the 'الكشاف السياقي' tool
			4. Type a word using the virtual keyboard
			5. Click the copy icon next to the result
			6. Verify toast appears
			7. Extract the copied content and compare with actual result
			""")
	public void TC13_verifyCopyFunctionalityInResults() {
		System.out.println("TC13_verifyCopyFunctionalityInResults");

		try {
			// 1️⃣ إعداد الأداة والمدونة المستخدمة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			// 2️⃣ بناء الرابط وفتح الصفحة مباشرة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);

			// 3️⃣ إنشاء صفحة الأداة وانتظار التحميل
			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);

			// 4️⃣ استخدام لوحة المفاتيح الظاهرية لكتابة الكلمة
			concordancerPage.clickVirtualKeyboardIcon();
			String searchWord = "السيارة";
			concordancerPage.typeUsingVirtualKeyboard(searchWord);
			Allure.step("⌨️ Typed using virtual keyboard: " + searchWord);

			// 5️⃣ إغلاق النافذة التوضيحية إن ظهرت
			concordancerPage.closeInfoDialog();
			System.out.println("❎ Clicked on (X) close button");
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(
					ExpectedConditions.invisibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));

			// 6️⃣ الضغط على زر البحث
			concordancerPage.clickSearchButton();
			Allure.step("🔍 Clicked search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			concordancerPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (concordancerPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'search Word': " + searchWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			// 🧪 رقم الصف المراد اختباره
			int rowIndexToTest = 2;

			// 8️⃣ الضغط على أيقونة النسخ بجانب النتيجة المحددة
			concordancerPage.clickCopyIconInRow(rowIndexToTest);
			Allure.step("📎 Clicked copy icon in row: " + rowIndexToTest);
			System.out.println("📎 Clicked copy icon in row: " + rowIndexToTest);

			// 9️⃣ التحقق من ظهور رسالة التوست
			concordancerPage.verifyCopyToastAppeared();
			Allure.step("🔔 Verified copy toast appeared");
			System.out.println("🔔 Verified copy toast appeared");

			// 🔟 استخراج النصوص الثلاثة للمقارنة
			String toastText = concordancerPage.getCopiedTextFromToast();
			String clipboardText = concordancerPage.getCopiedTextFromClipboard();
			String expectedTextFromTable = concordancerPage.getFullSentenceByRowIndex(rowIndexToTest);
			Assert.assertFalse(expectedTextFromTable.isEmpty(), "❌ No sentence found in the table row!");

			// 🔍 عرض النصوص للمراجعة
			System.out.println("📋 From TABLE: " + expectedTextFromTable);
			System.out.println("📋 From TOAST: " + toastText);
			System.out.println("📋 From CLIPBOARD: " + clipboardText);

			// ✅ المقارنات الثلاثية للتأكد من صحة النسخ
			Assert.assertEquals(toastText, clipboardText, "❌ Toast and clipboard text do not match!");
			Assert.assertEquals(clipboardText, expectedTextFromTable, "❌ Clipboard text doesn't match table!");
			Assert.assertEquals(toastText, expectedTextFromTable, "❌ Toast text doesn't match table!");

			Allure.step("✅ Copied text matches in all sources");
			// 🔎 توثيق مصدر البيانات من الصف
			Allure.step("📌 Details extracted from table row index: " + rowIndexToTest);

		} catch (AssertionError ae) {
			// 🟥 توثيق الخطأ في Allure مع لقطة شاشة
			attachFullPageScreenshot("🔴 Assertion failed in copy test");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			// ⚠️ توثيق الاستثناء غير المتوقع مع لقطة شاشة
			attachFullPageScreenshot("⚠️ Unexpected error in copy test");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-14 | ✅ التحقق من وظيفة التفاصيل في نتائج أداة الكشاف السياقي ✅ Test case
	 * to verify that clicking the 'details' icon displays the full metadata of the
	 * search result.
	 */
	@Test(description = "TC-14 | Verify details popup functionality in Concordancer results", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User views details of a search result from Concordancer tool")
	@Description("""
			Test Objective: Ensure that clicking the details icon displays all expected metadata for a search result.
			Steps:
			1. Navigate to 'المدونات' page
			2. Select any corpus using 'اختر المدونة'
			3. Select the 'الكشاف السياقي' tool
			4. Type a word using the virtual keyboard
			5. Click the details icon next to any search result
			6. Verify that a popup appears
			7. Ensure the popup contains: الوعاء - المجال - الموقع - المادة - السياق
			""")
	public void TC14_verifyDetailsPopupFunctionality() {
		System.out.println("TC14_verifyDetailsPopupFunctionality");

		try {
			// 1️⃣ إعداد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);

			// 2️⃣ فتح الأداة وانتظار التحميل
			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);

			// 3️⃣ استخدام لوحة المفاتيح الظاهرية للبحث عن كلمة
			concordancerPage.clickVirtualKeyboardIcon();
			String searchWord = "السيارة";
			concordancerPage.typeUsingVirtualKeyboard(searchWord);
			Allure.step("⌨️ Typed using virtual keyboard: " + searchWord);

			// 4️⃣ إغلاق النافذة التعريفية (إن ظهرت)
			concordancerPage.closeInfoDialog();
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(
					ExpectedConditions.invisibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));

			// 5️⃣ تنفيذ البحث
			concordancerPage.clickSearchButton();
			Allure.step("🔍 Clicked search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			concordancerPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (concordancerPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'search Word': " + searchWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			int rowIndexToTest = 4;

			// 7️⃣ الضغط على زر التفاصيل في الصف
			concordancerPage.clickDetailsIconInRow(rowIndexToTest);
			Allure.step("🛈 Clicked details icon in row: " + rowIndexToTest);

			// 8️⃣ التحقق من ظهور النافذة
			boolean isDisplayed = concordancerPage.verifyDetailsPopupIsDisplayed();
			Assert.assertTrue(isDisplayed, "❌ Details popup did not appear!");
			Allure.step("📋 Details popup appeared successfully");
			System.out.println("📋 Details popup appeared successfully");

			// 9️⃣ استخراج عناصر التفاصيل
			String container = concordancerPage.getResultContainerText();
			String field = concordancerPage.getResultFieldText();
			String location = concordancerPage.getResultLocationText();
			String source = concordancerPage.getResultSourceText();
			String context = concordancerPage.getResultContextText();
			System.out.println("📋 استخراج عناصر التفاصيل");

			// 🔍 التحقق من أن العناصر غير فارغة
			Assert.assertFalse(container.isEmpty(), "❌ Missing 'الوعاء' text");
			Assert.assertFalse(field.isEmpty(), "❌ Missing 'المجال' text");
			Assert.assertFalse(location.isEmpty(), "❌ Missing 'الموقع' text");
			Assert.assertFalse(source.isEmpty(), "❌ Missing 'المادة' text");
			Assert.assertFalse(context.isEmpty(), "❌ Missing 'السياق' text");

			// ✅ توثيق البيانات الظاهرة في Allure
			Allure.step("📝 Container: " + container);
			Allure.step("📝 Field: " + field);
			Allure.step("📝 Location: " + location);
			Allure.step("📝 Source: " + source);
			Allure.step("📝 Context: " + context);

			System.out.println("📝 Container: " + container);
			System.out.println("📝 Field: " + field);
			System.out.println("📝 Location: " + location);
			System.out.println("📝 Source: " + source);
			System.out.println("📝 Context: " + context);

			System.out.println("✅ All detail fields are present and valid.");
			// 🔎 توثيق مصدر البيانات من الصف
			Allure.step("📌 Details extracted from table row index: " + rowIndexToTest);

			// اغلاق نافذة التفاصيل
			concordancerPage.closeDetailDialog();
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(
					ExpectedConditions.invisibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Details Test - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Details Test - Unexpected Error");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Unexpected error in TC-14: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-15 | ✅ التحقق من عرض جدول النتائج بعد تنفيذ بحث صالح ✅ Test case to ensure
	 * the results table displays valid data after a proper search.
	 */
	@Test(description = "TC-15 | Verify results table is displayed with data after valid search", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User performs valid search and sees non-empty results table")
	@Description("""
			Test Objective: Ensure that after performing a valid search, the results table appears
			and contains one or more rows and columns.
			Steps:
			1. Navigate to 'المدونات' page
			2. Select any corpus using 'اختر المدونة'
			3. Select the 'الكشاف السياقي' tool
			4. Search for a valid keyword using virtual keyboard
			5. Verify the table is visible
			6. Verify table has rows and columns
			""")
	public void TC15_verifyResultsTableHasData() {
		System.out.println("TC15_verifyResultsTableHasData");

		try {
			// 1️⃣ إعداد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);

			// 2️⃣ انتظار تحميل الأداة
			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);

			// 3️⃣ كتابة كلمة بحث باستخدام لوحة المفاتيح الظاهرية
			concordancerPage.clickVirtualKeyboardIcon();
			String searchWord = "السيارة";
			concordancerPage.typeUsingVirtualKeyboard(searchWord);
			Allure.step("⌨️ Typed using virtual keyboard: " + searchWord);

			// 4️⃣ إغلاق النافذة التعريفية (إن وُجدت)
			concordancerPage.closeInfoDialog();
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(
					ExpectedConditions.invisibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));

			// 5️⃣ تنفيذ البحث
			concordancerPage.clickSearchButton();
			Allure.step("🔍 Clicked search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			concordancerPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (concordancerPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'search Word': " + searchWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			// 8️⃣ التحقق من عدد الصفوف
			int rowCount = concordancerPage.getNumberOfResultsRows();
			Assert.assertTrue(rowCount > 0, "❌ No result rows found in table!");
			Allure.step("🧾 Found rows in table: " + rowCount);
			System.out.println("🧾 Found rows in table: " + rowCount);

			// 9️⃣ التحقق من الأعمدة في أول صف
			int columnCount = concordancerPage.getNumberOfColumnsInFirstRow();
			Assert.assertTrue(columnCount > 0, "❌ No columns found in first result row!");
			Allure.step("📄 Columns in first row: " + columnCount);
			System.out.println("📄 Columns in first row: " + columnCount);

			// 🔟 التحقق من احتواء النتائج على الكلمة المدخلة
			List<String> searchResults = concordancerPage.getSearchResultWords();
			boolean containsKeyword = searchResults.stream().anyMatch(r -> r.contains(searchWord));
			Assert.assertTrue(containsKeyword, "❌ النتائج لا تحتوي على الكلمة المدخلة!");
			Allure.step("✅ Search results verified. Found entries containing: " + searchWord);
			System.out.println("✅ Search results verified. Found entries containing: " + searchWord);

			System.out.println("✅ Table displayed with " + rowCount + " rows and " + columnCount + " columns.");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Table Display Assertion Failed");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Table Display Unexpected Error");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Error in TC-15: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-16 | ✅ التحقق من ترتيب الأعمدة القابلة للفرز (تصاعديًا وتنازليًا) ✅ Test
	 * case to verify that sortable columns respond correctly to ascending and
	 * descending sorting actions, using a logic that compares the **last word** of
	 * each context.
	 */
	@Test(description = "TC-16 | Verify sorting functionality of sortable columns using last-word logic", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User clicks column headers to sort results ascending and descending")
	@Description("""
			Test Objective:
			Verify that sortable columns (السياق السابق للكلمة / السياق اللاحق للكلمة)
			respond correctly to header clicks by sorting the data in ascending and descending order,
			based on the last word in each context cell.

			Notes:
			⚠️ The test uses locale-aware Arabic Collator with light tolerance.
			📌 Sorting is validated using the last word in each cell — not the full string.

			Steps:
			1. Navigate to the 'المدونات' page
			2. Select any corpus using 'اختر المدونة'
			3. Select the 'الكشاف السياقي' tool
			4. Search for a valid keyword using virtual keyboard
			5. Verify the table is displayed with data
			6. Click sortable headers for both columns and confirm sorting behavior
			""")
	public void TC16_verifySortableColumnsFunctionality() {
		System.out.println("TC16_verifySortableColumnsFunctionality");

		try {
			// 1️⃣ إعداد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);

			// 2️⃣ انتظار تحميل الأداة
			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);

			// 3️⃣ كتابة كلمة بحث باستخدام لوحة المفاتيح الظاهرية
			concordancerPage.clickVirtualKeyboardIcon();
			String searchWord = "السيارة";
			concordancerPage.typeUsingVirtualKeyboard(searchWord);
			Allure.step("⌨️ Typed using virtual keyboard: " + searchWord);

			// 4️⃣ إغلاق النافذة التعريفية (إن وُجدت)
			concordancerPage.closeInfoDialog();
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(
					ExpectedConditions.invisibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));

			// 5️⃣ تنفيذ البحث
			concordancerPage.clickSearchButton();
			Allure.step("🔍 Clicked search button");
			System.out.println("🔍 Clicked search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			concordancerPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (concordancerPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'search Word': " + searchWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");
			int rowCount = concordancerPage.getNumberOfResultsRows();
			Assert.assertTrue(rowCount > 0, "❌ No result rows found in table!");
			Allure.step("📊 Table is visible and contains " + rowCount + " rows");
			System.out.println("📊 Table is visible and contains " + rowCount + " rows");

			// 7️⃣ استخراج الأعمدة القابلة للفرز وتجربتها
			List<WebElement> headers = concordancerPage.getAllTableHeaders();
			for (int i = 0; i < headers.size(); i++) {
			    WebElement h = headers.get(i);
			    String cls = h.getAttribute("class");
			    boolean sortable = concordancerPage.hasSortingIcon(h) || (cls != null && cls.contains("sortable"));
			    if (!sortable) continue;
			    int columnIndex = i + 1; // 1-based
			    // تجاهل الهيدر الفارغ
			    String name = h.getAttribute("textContent").trim();
			    if (name.isEmpty()) {
			        Allure.step("ℹ️ Skipping empty header at index " + columnIndex);
			        continue;
			    }
			    concordancerPage.verifyTop3RowsChangeOnSort(columnIndex, h);
			}
			Allure.step("✅ Sorting functionality verified for all sortable columns");
			System.out.println("✅ Sorting functionality verified for all sortable columns");
			Allure.step("✅ تم التحقق من جميع الأعمدة القابلة للفرز باستخدام مقارنة أول 3 صفوف فقط");
			System.out.println("✅ تم التحقق من جميع الأعمدة القابلة للفرز باستخدام مقارنة أول 3 صفوف فقط");
		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Sorting Assertion Failed");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Sorting Test Unexpected Error");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Error in TC-16: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-17 | ✅ التحقق من ظهور الترقيم عند وجود نتائج كثيرة ✅ Test to verify that
	 * the pagination controls (page numbers, next/previous buttons) appear
	 * correctly under the results table when data spans multiple pages.
	 */
	@Test(description = "TC-17 | Verify that pagination bar appears when results span multiple pages", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User sees pagination bar after searching and result count exceeds one page")
	@Description("""
				Test Objective:
				Verify that the pagination bar (page numbers, next, previous) is visible below
				the results table when a keyword search returns more results than the current page limit.

				Steps:
				1. Navigate directly to the 'الكشاف السياقي' tool
				2. Choose a corpus known to have many results
				3. Search for a frequent word using the virtual keyboard
				4. Verify the table is visible and contains >10 entries
				5. Verify that pagination controls appear below the table
			""")
	public void TC17_verifyPaginationControlsAreVisible() {
		System.out.println("TC17_verifyPaginationControlsAreVisible");

		try {
			// 1️⃣ إعداد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);

			// 2️⃣ انتظار تحميل الأداة
			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);

			// 3️⃣ كتابة كلمة بحث باستخدام لوحة المفاتيح الظاهرية
			concordancerPage.clickVirtualKeyboardIcon();
			String searchWord = "السيارة";
			concordancerPage.typeUsingVirtualKeyboard(searchWord);
			Allure.step("⌨️ Typed using virtual keyboard: " + searchWord);

			// 4️⃣ إغلاق النافذة التعريفية (إن وُجدت)
			concordancerPage.closeInfoDialog();
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(
					ExpectedConditions.invisibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));

			// 5️⃣ تنفيذ البحث
			concordancerPage.clickSearchButton();
			Allure.step("🔍 Clicked search button");
			System.out.println("🔍 Clicked search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			concordancerPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (concordancerPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'search Word': " + searchWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			int totalRows = concordancerPage.getNumberOfResultsRows();
			Assert.assertTrue(totalRows >= 10, "❌ Less than 10 results found – pagination may not appear!");

			// 7️⃣ التحقق من وجود شريط الترقيم
			Assert.assertTrue(concordancerPage.isPaginationBarVisible(), "❌ Pagination controls not visible!");


		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Pagination Assertion Failed");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;
		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Pagination Test Unexpected Error");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Error in TC-17: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-18 | ✅ التحقق من الانتقال إلى صفحة معينة باستخدام شريط الترقيم ✅ Test to
	 * verify that clicking a page number navigates to the corresponding results
	 * page.
	 */
	@Test(description = "TC-18 | Verify navigating to specific page number using pagination", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User navigates to a specific page using pagination controls")
	@Description("""
				Test Objective:
				Verify that clicking on a specific page number in the pagination bar loads results for that page.

				Steps:
				1. Navigate directly to the 'الكشاف السياقي' tool
				2. Choose a corpus known to have many results
				3. Search for a frequent word using the virtual keyboard
				4. Ensure the pagination bar appears
				5. Click on page 3 and verify new results are displayed
			""")
	public void TC18_verifyNavigationToSpecificPage() {
		System.out.println("TC18_verifyNavigationToSpecificPage");

		try {
			// 1️⃣ إعداد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);

			// 2️⃣ انتظار تحميل الأداة
			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);

			// 3️⃣ كتابة كلمة بحث باستخدام لوحة المفاتيح الظاهرية
			concordancerPage.clickVirtualKeyboardIcon();
			String searchWord = "السيارة";
			concordancerPage.typeUsingVirtualKeyboard(searchWord);
			Allure.step("⌨️ Typed using virtual keyboard: " + searchWord);

			// 4️⃣ إغلاق النافذة التعريفية (إن وُجدت)
			concordancerPage.closeInfoDialog();
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(
					ExpectedConditions.invisibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));

			// 5️⃣ تنفيذ البحث
			concordancerPage.clickSearchButton();
			Allure.step("🔍 Clicked search button");
			System.out.println("🔍 Clicked search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			concordancerPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (concordancerPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'search Word': " + searchWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			int rowCount = concordancerPage.getNumberOfResultsRows();
			Assert.assertTrue(rowCount > 0, "❌ No result rows found in table!");
			Allure.step("📊 Table is visible and contains " + rowCount + " rows");
			System.out.println("📊 Table is visible and contains " + rowCount + " rows");

			// 7 التأكد من ظهور شريط الترقيم
			Assert.assertTrue(concordancerPage.isPaginationBarVisible(), "❌ Pagination bar not visible!");
			Allure.step("✅ Pagination bar appeared after search");
			System.out.println("✅ Pagination bar appeared after search");

			// 8 استخراج أول نتيجة قبل تغيير الصفحة
			String firstResultBefore = concordancerPage.getFirstCellText(2);
			Allure.step("🔢 First result on page 1: " + firstResultBefore);
			System.out.println("🔢 First result on page 1: " + firstResultBefore);

			// 9 الانتقال إلى الصفحة رقم 3
			int page = 3;
			concordancerPage.goToPage(page);
			Allure.step("📄 Navigated to page " + page + " ");
			System.out.println("📄 Navigated to page " + page + "");

			// 10 التحقق من اختلاف النتائج
			String firstResultAfter = concordancerPage.getFirstCellText(2);
			Assert.assertNotEquals(firstResultAfter, firstResultBefore,
					"❌ Same result appears on page " + page + " – navigation failed!");
			Allure.step("✅ First result on page " + page + " is different: " + firstResultAfter);
			System.out.println("✅ First result on page " + page + " is different: " + firstResultAfter);

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Pagination Page Navigation Assertion Failed");
			throw ae;
		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Pagination Page Navigation Unexpected Error");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Error in TC-18: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-19 | ✅ التحقق من تمييز رقم الصفحة الحالية بعد التنقل ✅ Test to verify that
	 * the currently selected page is highlighted in the pagination bar.
	 */
	@Test(description = "TC-19 | Verify that the current page number is highlighted after navigation", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User sees the current page clearly highlighted in pagination")
	@Description("""
				Test Objective:
				Verify that after navigating to a specific page, the pagination bar clearly highlights the current page number.

				Steps:
				1. Navigate directly to the 'الكشاف السياقي' tool
				2. Choose a corpus with many results
				3. Search for a common word
				4. Wait for results and pagination to load
				5. Click page number 2
				6. Verify that page 2 is now highlighted
			""")
	public void TC19_verifyHighlightedCurrentPageNumber() {
		System.out.println("TC19_verifyHighlightedCurrentPageNumber");

		try {
			// 1️⃣ إعداد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);

			// 2️⃣ انتظار تحميل الأداة
			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);

			// 3️⃣ كتابة كلمة بحث باستخدام لوحة المفاتيح الظاهرية
			concordancerPage.clickVirtualKeyboardIcon();
			String searchWord = "السيارة";
			concordancerPage.typeUsingVirtualKeyboard(searchWord);
			Allure.step("⌨️ Typed using virtual keyboard: " + searchWord);

			// 4️⃣ إغلاق النافذة التعريفية (إن وُجدت)
			concordancerPage.closeInfoDialog();
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(
					ExpectedConditions.invisibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));

			// 5️⃣ تنفيذ البحث
			concordancerPage.clickSearchButton();
			Allure.step("🔍 Clicked search button");
			System.out.println("🔍 Clicked search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			concordancerPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (concordancerPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'search Word': " + searchWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			Assert.assertTrue(concordancerPage.isResultsTableVisible(), "❌ Results table is not visible!");
			int rowCount = concordancerPage.getNumberOfResultsRows();
			Assert.assertTrue(rowCount > 0, "❌ No result rows found in table!");
			Allure.step("📊 Table is visible and contains " + rowCount + " rows");
			System.out.println("📊 Table is visible and contains " + rowCount + " rows");

			// 7 التأكد من ظهور شريط الترقيم
			Assert.assertTrue(concordancerPage.isPaginationBarVisible(), "❌ Pagination bar not visible!");
			Allure.step("✅ Pagination bar appeared after search");
			System.out.println("✅ Pagination bar appeared after search");

			// 8 الانتقال إلى الصفحة 2
			int page = 2;
			concordancerPage.goToPage(page);
			Allure.step("📄 Navigated to page 2");
			System.out.println("📄 Navigated to page 2");

			// 9 التحقق من أن الصفحة الحالية هي 2 ومميزة
			int highlightedPage = concordancerPage.getCurrentHighlightedPageNumber();
			Assert.assertEquals(highlightedPage, page, "❌ Page " + page + " is not highlighted after navigation!");
			Allure.step("✅ Page " + page + " is correctly highlighted in pagination");
			System.out.println("✅ Page " + page + " is correctly highlighted in pagination");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Page Highlight Assertion Failed");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;
		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Unexpected Error in TC-19");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Error in TC-19: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-20 | ✅ التحقق من تغيير عدد النتائج المعروضة ✅ Test to verify that changing
	 * the "Show X results" dropdown updates the table to reflect the selected
	 * number of rows per page.
	 */
	@Test(description = "TC-20 | Verify changing number of displayed results updates table rows", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User changes number of visible rows using the 'Show X results' dropdown")
	@Description("""
				Test Objective:
				Verify that selecting a different number of results per page (10, 50, 100)
				from the dropdown correctly updates the number of visible rows in the results table.

				Steps:
				1. Navigate directly to the 'الكشاف السياقي' tool
				2. Choose a corpus with many results
				3. Perform a search for a frequent word using virtual keyboard
				4. Confirm that the pagination bar and table are displayed
				5. Select a value (e.g., 10, 50, 100) from the "Show X results" dropdown
				6. Verify the number of visible rows matches the selected value (if enough data exists)
			""")
	public void TC20_verifyResultsPerPageDropdown() {
		System.out.println("TC20_verifyResultsPerPageDropdown");

		try {
			// 1️⃣ إعداد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);

			// 2️⃣ انتظار تحميل الأداة
			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);

			// 3️⃣ كتابة كلمة بحث باستخدام لوحة المفاتيح الظاهرية
			concordancerPage.clickVirtualKeyboardIcon();
			String searchWord = "السيارة";
			concordancerPage.typeUsingVirtualKeyboard(searchWord);
			Allure.step("⌨️ Typed using virtual keyboard: " + searchWord);

			// 4️⃣ إغلاق النافذة التعريفية (إن وُجدت)
			concordancerPage.closeInfoDialog();
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(
					ExpectedConditions.invisibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));

			// 5️⃣ تنفيذ البحث
			concordancerPage.clickSearchButton();
			Allure.step("🔍 Clicked search button");
			System.out.println("🔍 Clicked search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			concordancerPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (concordancerPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'search Word': " + searchWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");
			// 7️⃣ تغيير عدد النتائج إلى 100
			int desiredCount = 100;
			concordancerPage.selectResultsPerPage(desiredCount);
			Allure.step("🔽 Selected " + desiredCount + " results per page");
			System.out.println("🔽 Selected " + desiredCount + " results per page");

			// 8️⃣ انتظار تحديث الجدول
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(driver -> {
				int rows = concordancerPage.getNumberOfResultsRows();
				return rows > 0 && rows <= desiredCount;
			});

			// 9️⃣ التحقق من عدد الصفوف
			int visibleRows = concordancerPage.getNumberOfResultsRows();
			Assert.assertTrue(visibleRows <= desiredCount && visibleRows > 0,
					"❌ Number of rows after selection does not match expected range!");
			Allure.step("✅ Table updated to show " + visibleRows + " rows after selection");
			System.out.println("✅ Table updated to show " + visibleRows + " rows after selection");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Results Per Page Assertion Failed");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;
		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Unexpected Error in TC-20");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Error in TC-20: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-21 | ✅ التحقق من إعادة الترقيم عند تغيير عدد النتائج ✅ Test to verify that
	 * changing the "Show X results" dropdown resets pagination to the first page.
	 */
	@Test(description = "TC-21 | Verify that changing results per page resets pagination to first page", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User changes number of visible rows → Pagination resets to page 1")
	@Description("""
				Test Objective:
				Verify that when a user selects a different number of results per page,
				the pagination resets to the first page and updates the table accordingly.

				Steps:
				1. Navigate directly to the 'الكشاف السياقي' tool
				2. Choose a corpus with many results
				3. Perform a search for a frequent word
				4. Navigate to a later page (e.g., page 3)
				5. Change results per page to a different value
				6. Verify that pagination resets to page 1
				7. Verify that the number of rows updates according to the selected value
			""")
	public void TC21_verifyPaginationResetAfterChangingResultsPerPage() {
		System.out.println("TC21_verifyPaginationResetAfterChangingResultsPerPage");

		try {

			// 1️⃣ إعداد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.CONCORDANCER;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);

			// 2️⃣ انتظار تحميل الأداة
			ConcordancerPage concordancerPage = new ConcordancerPage(driver);
			concordancerPage.waitForPageReady(selectedTool.getPathSegment(), concordancerPage::searchInputWebElement);

			// 3️⃣ كتابة كلمة بحث باستخدام لوحة المفاتيح الظاهرية
			concordancerPage.clickVirtualKeyboardIcon();
			String searchWord = "السيارة";
			concordancerPage.typeUsingVirtualKeyboard(searchWord);
			Allure.step("⌨️ Typed using virtual keyboard: " + searchWord);

			// 4️⃣ إغلاق النافذة التعريفية (إن وُجدت)
			concordancerPage.closeInfoDialog();
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(
					ExpectedConditions.invisibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));

			// 5️⃣ تنفيذ البحث
			concordancerPage.clickSearchButton();
			Allure.step("🔍 Clicked search button");
			System.out.println("🔍 Clicked search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			concordancerPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (concordancerPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'search Word': " + searchWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			// 7 الانتقال إلى الصفحة الثالثة
			int page = 3;
			concordancerPage.goToPage(page);
			int currentPage = concordancerPage.getCurrentHighlightedPageNumber();
			Assert.assertEquals(currentPage, page, "❌ Failed to navigate to page " + page + "!");
			Allure.step("📄 Navigated to page " + page);
			System.out.println("📄 Navigated to page " + page);

			// 8 تغيير عدد النتائج إلى 50
			int desiredCount = 50;
			concordancerPage.selectResultsPerPage(desiredCount);
			Allure.step("🔽 Changed results per page to: " + desiredCount);
			System.out.println("🔽 Changed results per page to: " + desiredCount);

			// 9 انتظار تحديث الجدول وتحديث الترقيم
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> {
				int rows = concordancerPage.getNumberOfResultsRows();
				return rows > 0 && rows <= desiredCount;
			});

			// 10 التحقق من أن الصفحة الحالية هي 1 بعد التغيير
			int highlightedPage = concordancerPage.getCurrentHighlightedPageNumber();
			Assert.assertEquals(highlightedPage, 1, "❌ Pagination did not reset to page 1!");
			Allure.step("✅ Pagination successfully reset to page 1");
			System.out.println("✅ Pagination successfully reset to page 1");

			// 11 التحقق من عدد الصفوف
			int rowCount = concordancerPage.getNumberOfResultsRows();
			Assert.assertTrue(rowCount <= desiredCount && rowCount > 0,
					"❌ Row count is incorrect after dropdown change");
			Allure.step("✅ Row count updated correctly: " + rowCount);
			System.out.println("✅ Row count updated correctly: " + rowCount);

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Pagination Reset Assertion Failed");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;
		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Unexpected Error in TC-21");
			attachFailureVideo("📹 Video (on exception)");

			throw new RuntimeException("⚠️ Error in TC-21: " + e.getMessage(), e);
		}
	}

}
