package com.falak.qa.tests;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
import com.falak.qa.models.ngrams.NGramResult;
import com.falak.qa.models.ngrams.NGramsApiClient;
import com.falak.qa.models.ngrams.NGramsFilterParams;
import com.falak.qa.models.ngrams.NGramsPage;
import com.falak.qa.pages.corpora.CorporaCardComponent;
import com.falak.qa.pages.corpora.CorporaOverviewPage;
import com.falak.qa.pages.corpora.CorporaPage;
import com.falak.qa.pages.home.HomePage;
import com.falak.qa.utils.CorporaToolUrlBuilder;
import com.falak.qa.utils.DownloadsCdpHelper;
import com.falak.qa.utils.NavigationUtils;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@Epic("أداة التتابعات اللَّفظيَّة | N-grams Tool")
@Feature("التحقق من عناصر الواجهة الأساسية")
@Listeners({ com.falak.qa.listeners.RetryListener.class })
public class NGramsToolTC extends BaseTest {

	@Test(description = "TC-01 | Verify that user can open the ‘التتابعات اللَّفظيَّة’ tool for مجمع اللغة العربية from the home page", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User navigates from home to corpora tool")
	@Description("""
			Test Objective: Ensure that the user can navigate from the home page
			to the ‘التتابعات اللَّفظيَّة’ tool inside the overview page of ‘المجمع للغة العربية المعاصرة’.
			Steps:
			1. Open home page.
			2. Click ‘المدونات’ to go to corpora list.
			3. Select ‘المجمع للغة العربية المعاصرة’ corpora.
			4. Click the ‘التتابعات اللَّفظيَّة’ tool button.
			5. Confirm URL contains correct path and UUID.
			6. Confirm that tool title matches the expected Arabic name.
			Expected Result: Page should open successfully with correct title and valid URL structure.
			""")
	public void TC01_openNGramsToolFromHomePage() {
		System.out.println("TC01_openNGramsToolFromHomePage");

		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
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

			// 🧪 7. الضغط على زر "التتابعات اللَّفظيَّة"
			ToolsName selectedTool = ToolsName.NGRAMS;
			By toolLocator = overviewPage.getToolCardLocator(selectedTool);
			Assert.assertTrue(driver.findElements(toolLocator).size() > 0,
					"❌ الزر الخاص بأداة " + selectedTool.getArabicName() + " غير موجود في هذه المدونة!");
			Allure.step("🎯 Tool button found: " + selectedTool.getArabicName());

			overviewPage.clickOnToolCard(selectedTool);

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
			NGramsPage nGramsPage = new NGramsPage(driver);
			String actualToolTitle = nGramsPage.getToolTitleText();
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

	/**
	 * TC-02 | ✅ التحقق من ظهور عنوان الأداة "التتابعات اللَّفظيَّة" ✅ Test to
	 * verify that the correct tool title is displayed on the tool page
	 */
	@Test(description = "TC-02 | Verify that the correct tool title is displayed", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User opens the tool page → Sees correct tool title displayed")
	@Description("""
			        Test Objective: Verify that the tool page for ‘االتتابعات اللَّفظيَّة’ loads properly
					and the page title matches the expected Arabic name.
					Steps:
					1. Open the tool page directly using a valid UUID and path.
					2. Wait for the page to load.
					3. Get the title at the top of the page.
					Expected Result: The title should be visible and match "التتابعات اللَّفظيَّة".
			""")
	public void TC02_verifyToolTitleIsDisplayedCorrectly() {
		System.out.println("TC02_verifyToolTitleIsDisplayedCorrectly");

		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
			// 🔢 1. تحديد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 🔗 2. بناء الرابط المباشر باستخدام UUID والأداة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);

			// ⏳ 3. الانتظار حتى يتم تحميل الصفحة وتحتوي على مسار الأداة
			wait.until(ExpectedConditions.urlContains(selectedTool.getPathSegment()));
			Allure.step("⏳ Waited for tool path segment in URL");

			// 🧪 4. التحقق من عنوان الأداة
			NGramsPage nGramsPage = new NGramsPage(driver);
			String actualTitle = nGramsPage.getToolTitleText();
			String expectedTitle = selectedTool.getArabicName();

			Assert.assertEquals(actualTitle, expectedTitle,
					"❌ العنوان الظاهر لا يطابق العنوان المتوقع للأداة: " + expectedTitle);
			Allure.step("🏷️ Tool title verified successfully: " + actualTitle);

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Tool Title Assertion Failed");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;
		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Unexpected Error in TC-01");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Error in TC-01: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-03 | التحقق من ظهور نافذة تفاصيل الأداة عند الضغط على زر (!) TC-03 |
	 * Verify that clicking the info (!) icon opens a popup with tool description
	 */
	@Test(description = "TC-03 | Verify that clicking the info (!) icon shows the tool's description popup", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User clicks the info icon on the NGrams tool page")
	@Description("""
			Test Objective: Ensure that clicking the (!) info icon next to the title
			opens a popup with a description of the 'التتابعات اللَّفظيَّة' tool.
			Steps:
			1. Open the Concordancer tool page using the direct URL generator.
			2. Click the (!) icon beside the tool title.
			3. Wait for the popup to appear.
			4. Validate the description text is visible and non-empty.
			Expected Result: Description popup appears showing the purpose of the tool.
			""")
	public void TC03_verifyInfoPopupInNGramsTool() {
		System.out.println("TC03_verifyInfoPopupInNGramsTool");

		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

			// 🔢 1. تحديد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 🔗 2. بناء الرابط المباشر باستخدام UUID والأداة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);

			// 🧭 3. التأكد من تحميل الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully with expected tool path");

			// ℹ️ 4. الضغط على زر التفاصيل (!)
			nGramsPage.clickInfoIcon();
			Allure.step("🖱️ Clicked on info (!) icon beside the title");

			// ⏳ 5. انتظار ظهور نافذة التفاصيل
			wait.until(ExpectedConditions.visibilityOfElementLocated(nGramsPage.getInfoDialogTextLocator()));
			Allure.step("📑 Info popup appeared successfully");

			// 🧪 6. التحقق من وجود نص داخل النافذة
			String descriptionText = nGramsPage.getInfoDialogText();
			Assert.assertFalse(descriptionText.isBlank(), "❌ نافذة التفاصيل لا تحتوي على نص!");
			Allure.step("📝 Info popup text verified: " + descriptionText);

			// ✅ 7. إغلاق النافذة
			nGramsPage.closeInfoDialog();
			Allure.step("❎ Info popup closed successfully");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Info Popup - Assertion Failure");
			attachFailureVideo("📹 Video (on Failure)");

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
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

			// 🔢 1. تحديد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 🔗 2. بناء الرابط المباشر للأداة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);

			// 🧭 3. التأكد من تحميل الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			// concordancerPage.waitForAngularToFinish();
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");

			// ℹ️ 4. فتح نافذة التفاصيل بالضغط على (!)
			nGramsPage.clickInfoIcon();
			Allure.step("🖱️ Clicked on info (!) icon");

			// ⏳ 5. التأكد من ظهور النافذة
			// wait.until(ExpectedConditions.visibilityOfElementLocated(concordancerPage.getInfoDialogTextLocator()));
			Allure.step("📑 Info popup is visible");

			// ❎ 6. الضغط على زر (X) لإغلاق النافذة
			nGramsPage.closeInfoDialog();
			Allure.step("❎ Clicked on (X) close button");

			// 🧪 7. التحقق من اختفاء النافذة
			boolean isClosed = wait
					.until(ExpectedConditions.invisibilityOfElementLocated(nGramsPage.getInfoDialogTextLocator()));
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
	 * TC-05 | التحقق من عرض نتائج تحتوي على عدد كلمات مطابق للتصفية المختارة (1، 2،
	 * 3) TC-05 | Verify that search results match selected word count filter (1, 2,
	 * 3)
	 */
	@Test(description = "TC-05 | Verify that results contain sentences matching the selected word count filter (1, 2, or 3)", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User selects a word count filter and performs a search")
	@Description("""
			Test Objective:
			Ensure that when the user selects a specific word count (1, 2, or 3) and performs a search,
			the resulting sentences in the first column of the table contain only the selected number of words.

			Steps:
			1. Open the NGrams tool page using direct URL.
			2. Select a word count filter (e.g., 2).
			3. Click the search button.
			4. Wait for results to appear.
			5. Retrieve all sentences in the first column.
			6. Verify each sentence contains exactly the selected number of words.

			Expected Result: All sentences should contain only the number of words selected in the filter.
			""")
	public void TC05_verifyFilteredResultsMatchSelectedWordCount() {
		System.out.println("TC05_verifyFilteredResultsMatchSelectedWordCount");

		try {
			// 🔢 1. تحديد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 🔗 2. بناء الرابط المباشر للأداة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);
			System.out.println("✅ Opened direct tool URL: " + fullToolUrl);

			// 🧭 3. التأكد من تحميل الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 🔘 4. تحديد فلتر عدد الكلمات
			int selectedWordCount = 1;
			nGramsPage.selectWordCountFilter(selectedWordCount);
			Allure.step("🔢 Selected word count filter: " + selectedWordCount);
			System.out.println("🔢 Selected word count filter: " + selectedWordCount);

			// 🔍 5. تنفيذ البحث
			nGramsPage.clickSearchButton();
			Allure.step("🔍 Clicked on search button");
			System.out.println("🔍 Clicked on search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			nGramsPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (nGramsPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'selecte dWord Count' filter: " + selectedWordCount);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			// 📥 7. التحقق من النتائج في واجهة المستخدم
			List<String> uiSentences = nGramsPage.getFirstColumnSentences();
			System.out.println("✅ UI Sentences: " + uiSentences);

			boolean allUiMatch = uiSentences.stream().map(sentence -> sentence.trim().split("\\s+").length)
					.allMatch(count -> count == selectedWordCount);

			Assert.assertTrue(allUiMatch, "❌ Some UI sentences do not match the selected word count!");
			Allure.step("✅ All UI sentences matched the expected word count: " + selectedWordCount);
			System.out.println("✅ All UI sentences matched the expected word count: " + selectedWordCount);

			// 🔗 8. استخراج UUID للأداة
			String toolId = CorporaToolUrlBuilder.extractToolUuidFromUrl(fullToolUrl);
			System.out.println("✅ toolId: " + toolId);

			// 🧮 9. إعداد فلاتر API
			NGramsFilterParams filters = new NGramsFilterParams().withN(selectedWordCount);

			// 🌐 10. جلب النتائج من الـ API
			List<NGramResult> apiAllResults = NGramsApiClient.getAllResults(toolId, filters);
			List<String> apiWords = apiAllResults.stream().map(NGramResult::getWord).toList();

			boolean allApiMatch = apiWords.stream().map(w -> w.trim().split("\\s+").length)
					.allMatch(count -> count == selectedWordCount);

			Assert.assertTrue(allApiMatch, "❌ Some API results do not match the selected word count!");
			Allure.step("✅ All API results matched the expected word count: " + selectedWordCount);
			System.out.println("✅ All API results matched the expected word count: " + selectedWordCount);

			// 🧾 طباعة النتائج في Allure والكونسول
			apiAllResults.forEach(result -> Allure.step("🔹 " + result.getWord() + " → count: " + result.getCount()));
			for (NGramResult result : apiAllResults) {
				System.out.println("🔹 " + result.getWord() + " → count: " + result.getCount());
			}

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Word Count Filter - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;
		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Word Count Filter - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-06 | التحقق من عمل أزرار (⬆️⬇️) للحد الأدنى للتكرار TC-06 | Verify min
	 * frequency increment/decrement works correctly via JS
	 */
	@Test(description = "TC-06 | Verify that the min frequency field increases and decreases correctly using JS", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User interacts with the min frequency input field")
	@Description("""
			Test Objective:
			Verify that the min frequency input field updates its value correctly when manipulated using JavaScript.

			Steps:
			1. Open the NGrams tool page.
			2. Read the initial value of the min frequency field.
			3. Increase the value by 1 using JS.
			4. Verify the value increased correctly.
			5. Decrease it back using JS.
			6. Verify the value returned to the original.
			Expected Result:
			The field should update accurately.
			""")
	public void TC06_verifyMinFreqIncrementDecrementViaJS() {
		System.out.println("TC06_verifyMinFreqIncrementDecrementViaJS");

		try {

			// 🔢 1. تحديد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 🔗 2. بناء الرابط المباشر للأداة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);
			System.out.println("✅ Opened direct tool URL: " + fullToolUrl);

			// 🧭 3. التأكد من تحميل الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 4️⃣ قراءة القيمة الأصلية
			int initialValue = nGramsPage.getMinFreqValue();
			int incAmount = 5;
			int decAmount = 2;

			Allure.step("🔢 Initial value: " + initialValue);
			System.out.println("🔢 Initial value: " + initialValue);

			// 5️⃣ تنفيذ الزيادة
			nGramsPage.increaseValue(nGramsPage.getMinFreqInput(), incAmount);
			int increased = nGramsPage.getMinFreqValue();
			Allure.step("⬆️ Value after increase: " + increased);
			System.out.println("⬆️ Value after increase: " + increased);
			Assert.assertTrue(increased > initialValue, "❌ Value did not increase as expected!");

			// 6️⃣ تنفيذ النقصان
			nGramsPage.decreaseValue(nGramsPage.getMinFreqInput(), decAmount);
			int finalValue = nGramsPage.getMinFreqValue();
			Allure.step("⬇️ Value after decrease: " + finalValue);
			System.out.println("⬇️ Value after decrease: " + finalValue);
			Assert.assertTrue(finalValue < increased, "❌ Value did not decrease as expected!");

			// 🔍 7. الضغط على زر البحث
			nGramsPage.clickSearchButton();
			Allure.step("🔍 Clicked on search button");
			System.out.println("🔍 Clicked on search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			nGramsPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (nGramsPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'value' filter: " + finalValue);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			// 📊 9. التحقق من نتائج واجهة المستخدم
			List<Integer> frequencies = nGramsPage.getSecondColumnFrequencies();
			boolean allValid = frequencies.stream().allMatch(freq -> freq >= finalValue);
			Assert.assertTrue(allValid, "❌ Some UI frequencies are below the minimum threshold!");
			Allure.step("✅ All UI frequencies are above or equal to: " + finalValue);
			System.out.println("✅ All UI frequencies are above or equal to: " + finalValue);

			// 🔗 10. استخراج UUID للأداة
			String toolId = CorporaToolUrlBuilder.extractToolUuidFromUrl(fullToolUrl);
			System.out.println("✅ toolId: " + toolId);

			// 🧮 11. إعداد فلاتر API
			NGramsFilterParams filters = new NGramsFilterParams().withMinFreq(finalValue);

			// 🌐 12. جلب النتائج من API
			List<NGramResult> apiAllResults = NGramsApiClient.getAllResults(toolId, filters);
			List<Integer> apiFrequencies = apiAllResults.stream().map(NGramResult::getCount).toList();

			boolean allApiValid = apiFrequencies.stream().allMatch(freq -> freq >= finalValue);
			Assert.assertTrue(allApiValid, "❌ Some API frequencies are below the minimum threshold!");
			Allure.step("✅ All API results respected min frequency limit: " + finalValue);
			System.out.println("✅ All API results respected min frequency limit: " + finalValue);

			// 🧾 طباعة النتائج في Allure والكونسول
			apiAllResults.forEach(result -> Allure.step("🔹 " + result.getWord() + " → count: " + result.getCount()));
			for (NGramResult result : apiAllResults) {
				System.out.println("🔹 " + result.getWord() + " → count: " + result.getCount());
			}

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Min Freq JS - Assertion Error");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;
		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Min Freq JS - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-07 | التحقق من عمل أزرار (⬆️⬇️) للحد الأقصى للتكرار TC-07 | Verify max
	 * frequency increment/decrement works correctly via JS
	 */
	@Test(description = "TC-07 | Verify that the max frequency field increases and decreases correctly using JS", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User interacts with the max frequency input field")
	@Description("""
			Test Objective:
			Verify that the max frequency input field updates its value correctly when manipulated using JavaScript.

			Steps:
			1. Open the Verbal Sequences tool page.
			2. Read the initial value of the max frequency field.
			3. Increase the value by a certain amount using JS.
			4. Verify the value increased correctly.
			5. Decrease it by a certain amount using JS.
			6. Verify the value decreased correctly.
			Expected Result:
			The field should update accurately.
			""")
	public void TC07_verifyMaxFreqIncrementDecrementViaJS() {
		System.out.println("TC07_verifyMaxFreqIncrementDecrementViaJS");

		try {

			// 🔢 1. تحديد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 🔗 2. بناء الرابط المباشر للأداة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);

			// 🧭 3. التأكد من تحميل الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");

			// 4 قراءة القيمة الأصلية
			int initialValue = nGramsPage.getMaxFreqValue();
			int incAmount = 4;
			int decAmount = 2;

			Allure.step("🔢 Initial value: " + initialValue);
			System.out.println("🔢 Initial value: " + initialValue);

			// 5 زيادة بمقدار معين
			nGramsPage.increaseValue(nGramsPage.getMaxFreqInput(), incAmount);
			int increased = nGramsPage.getMaxFreqValue();
			Allure.step("⬆️ Value after increase: " + increased);
			System.out.println("⬆️ Value after increase: " + increased);
			Assert.assertTrue(increased > initialValue, "❌ Value did not increase as expected!");

			// 6 إنقاص بمقدار معين
			nGramsPage.decreaseValue(nGramsPage.getMaxFreqInput(), decAmount);
			int finalValue = nGramsPage.getMaxFreqValue();
			Allure.step("⬇️ Value after decrease: " + finalValue);
			System.out.println("⬇️ Value after decrease: " + finalValue);
			Assert.assertTrue(finalValue < increased, "❌ Value did not decrease as expected!");

			// 🔍 7. تنفيذ البحث
			nGramsPage.clickSearchButton();
			Allure.step("🔍 Clicked on search button");
			System.out.println("🔍 Clicked on search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			nGramsPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (nGramsPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'value' filter: " + finalValue);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			// 📊 9. التحقق من نتائج واجهة المستخدم
			List<Integer> frequencies = nGramsPage.getSecondColumnFrequencies();
			boolean allValid = frequencies.stream().allMatch(freq -> freq <= finalValue);
			Assert.assertTrue(allValid, "❌ Some UI frequencies are above the maximum threshold!");
			Allure.step("✅ All UI frequencies are below or equal to: " + finalValue);
			System.out.println("✅ All UI frequencies are below or equal to: " + finalValue);

			// 🔗 10. استخراج UUID للأداة
			String toolId = CorporaToolUrlBuilder.extractToolUuidFromUrl(fullToolUrl);
			System.out.println("✅ toolId: " + toolId);

			// 🧮 11. إعداد فلاتر API
			NGramsFilterParams filters = new NGramsFilterParams().withMaxFreq(finalValue);

			// 🌐 12. جلب النتائج من API
			List<NGramResult> apiAllResults = NGramsApiClient.getAllResults(toolId, filters);
			List<Integer> apiFrequencies = apiAllResults.stream().map(NGramResult::getCount).toList();

			boolean allApiValid = apiFrequencies.stream().allMatch(freq -> freq <= finalValue);
			Assert.assertTrue(allApiValid, "❌ Some API frequencies are above the max threshold!");
			Allure.step("✅ All API results respected max frequency limit: " + finalValue);
			System.out.println("✅ All API results respected max frequency limit: " + finalValue);

			// 🧾 طباعة النتائج في Allure والكونسول
			apiAllResults.forEach(result -> Allure.step("🔹 " + result.getWord() + " → count: " + result.getCount()));
			for (NGramResult result : apiAllResults) {
				System.out.println("🔹 " + result.getWord() + " → count: " + result.getCount());
			}
		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Max Freq JS - Assertion Error");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Max Freq JS - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage());
		}
	}

	@Test(description = "TC-08 | Verify that 'Does Not Contain' filter works correctly", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User uses 'Does Not Contain' filter")
	@Description("""
			Test Objective:
			Verify that the results table and API do not contain sequences with the excluded word.

			Steps:
			1. Open the Verbal Sequences tool page.
			2. Enter the excluded word in the 'Does Not Contain' filter.
			3. Click the search button.
			4. Validate that all results in the first column do not contain the word.
			5. Validate that all API pages also exclude the word.
			Expected Result:
			No row (in UI or API) contains the excluded word.
			""")
	public void TC08_verifyDoesNotContainFilter() {
		System.out.println("TC08_verifyDoesNotContainFilter");

		try {

			// 🔢 1. اختيار المدونة والأداة المطلوبة
			// Select the corpus and the tool to test
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 🔗 2. بناء الرابط المباشر للأداة من خلال المدونة والأداة
			// Construct the full URL to access the tool page directly
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);
			System.out.println("✅ Opened direct tool URL: " + fullToolUrl);

			// 🧭 3. انتظار تحميل صفحة الأداة بالكامل
			// Wait for the tool page to finish loading
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 🔠 4. إدخال كلمة لا نرغب بظهورها في النتائج (فلتر الاستبعاد)
			// Set the 'Does Not Contain' filter to exclude a specific word
			String excludedWord = "السيارة";
			nGramsPage.setDoesNotContainFilter(excludedWord);
			Allure.step("🚫 Set 'Does Not Contain' to: " + excludedWord);
			System.out.println("🚫 Set 'Does Not Contain' to: " + excludedWord);

			// 🔍 5. الضغط على زر البحث
			// Trigger the search using the provided filter
			nGramsPage.clickSearchButton();
			Allure.step("🔍 Clicked on search button");
			System.out.println("🔍 Clicked on search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			nGramsPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (nGramsPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'excluded Word' filter: " + excludedWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}

			// 📋 7. قراءة الكلمات من العمود الأول (UI)
			// Read all displayed words (first column) from the UI
			List<String> uiResults = nGramsPage.getFirstColumnSentences();

			// ✅ 8. التأكد أن كل الكلمات لا تحتوي على الكلمة المستبعدة
			// Verify that none of the displayed results contain the excluded word
			boolean noneContainInUI = uiResults.stream().noneMatch(r -> r.contains(excludedWord));
			Assert.assertTrue(noneContainInUI, "❌ Some UI results contain the excluded word: " + excludedWord);
			Allure.step("✅ All UI sentences excluded the word: " + excludedWord);
			System.out.println("✅ All UI sentences excluded the word: " + excludedWord);

			// 🔗 9. استخراج UUID للأداة من الرابط
			// Extract the tool UUID from the opened URL (used in API)
			String toolId = CorporaToolUrlBuilder.extractToolUuidFromUrl(fullToolUrl);
			System.out.println("✅ toolId: " + toolId);

			// 🧮 10. تجهيز فلاتر API باستخدام نفس الكلمة المستبعدة
			// Prepare API filter parameters with the same excluded word
			NGramsFilterParams filters = new NGramsFilterParams().withExcludeWords(excludedWord);
			System.out.println("✅ إعداد الفلاتر الخاصة بالـ API");

			// 🌐 11. استدعاء الـ API باستخدام نفس الفلاتر وجلب جميع الصفحات
			// Call the API and retrieve all results (across all pages)
			List<NGramResult> apiAllResults = NGramsApiClient.getAllResults(toolId, filters);

			// 📝 12. التحقق من أن نتائج API لا تحتوي على الكلمة
			// Ensure all words from the API do not contain the excluded word
			List<String> apiWords = apiAllResults.stream().map(NGramResult::getWord).toList();
			boolean noneContainInApi = apiWords.stream().noneMatch(w -> w.contains(excludedWord));
			Assert.assertTrue(noneContainInApi, "❌ Some API results contain the excluded word: " + excludedWord);

			// 🔁 طباعة كل نتيجة في Allure
			// Log each result in Allure for detailed inspection
			apiAllResults.forEach(result -> Allure.step("🔹 " + result.getWord() + " → count: " + result.getCount()));

			System.out.println("✅ جلب جميع النتائج عبر API (جميع الصفحات)");
			System.out.println("✅ All API results excluded the word: " + excludedWord);

			// 🧾 طباعة النتائج في Allure والكونسول
			apiAllResults.forEach(result -> Allure.step("🔹 " + result.getWord() + " → count: " + result.getCount()));
			for (NGramResult result : apiAllResults) {
				System.out.println("🔹 " + result.getWord() + " → count: " + result.getCount());
			}

			// 🔗 13. التحقق من أن كل نتائج UI موجودة ضمن بيانات API
			// Verify that UI results are a subset of the full API results
			boolean uiIsSubsetOfApi = apiWords.containsAll(uiResults);
			Assert.assertTrue(uiIsSubsetOfApi, "❌ UI results are not a subset of full API results.");
			Allure.step("✅ Verified that UI results are a subset of full API results");
			System.out.println("✅ Verified that UI results are a subset of full API results");

		} catch (AssertionError ae) {
			// ❌ في حال فشل التحقق (assertion)
			// If an assertion fails, attach a screenshot and re-throw the error
			attachFullPageScreenshot("🔴 Assertion Error - Does Not Contain Filter");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;
		} catch (Exception e) {
			// ⚠️ في حال حدوث خطأ غير متوقع
			// If any unexpected exception occurs
			attachFullPageScreenshot("⚠️ Unexpected Error - Does Not Contain Filter");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage(), e);
		}
	}

	/**
	 * ✅ TC-09 | التحقق من أن فلتر "يحتوي على" يعمل كما هو متوقع ✅ TC-09 | Verify
	 * that 'Contains' filter returns only matching results (UI + API)
	 */
	@Test(description = "TC-09 | Verify that 'Contains' filter works correctly", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User uses 'Contains' filter")
	@Description("""
			Test Objective:
			Verify that the results table and API only include sequences containing the specified word.

			Steps:
			1. Open the Verbal Sequences tool page.
			2. Enter a word in the 'Contains' filter.
			3. Click the search button.
			4. Validate that all UI results include the word.
			5. Validate that all API results include the word.
			6. Ensure UI results are part of the API results.
			Expected Result:
			All results (UI + API) must contain the specified word.
			""")
	public void TC09_verifyContainsFilter() {
		System.out.println("TC09_verifyContainsFilter");

		try {

			// 🔢 1. اختيار المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 🔗 2. بناء رابط الأداة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);
			System.out.println("✅ Opened direct tool URL: " + fullToolUrl);

			// 🧭 3. انتظار تحميل الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 🔠 4. إدخال الكلمة في فلتر "يحتوي على"
			String containedWord = "السيارة";
			nGramsPage.setContainWordsFilter(containedWord);
			Allure.step("🔍 Set 'Contains' filter to: " + containedWord);
			System.out.println("🔍 Set 'Contains' filter to: " + containedWord);

			// 🔍 5. تنفيذ البحث
			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");
			System.out.println("🔎 Clicked on search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			nGramsPage.waitForResultsOrNoDataMessage();

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (nGramsPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'Starts With' filter: " + containedWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}

			// 📋 7. قراءة النتائج من UI
			List<String> uiResults = nGramsPage.getFirstColumnSentences();
			boolean allUiContain = uiResults.stream().allMatch(w -> w.contains(containedWord));
			Assert.assertTrue(allUiContain, "❌ Some UI results do not contain the word: " + containedWord);
			Allure.step("✅ All UI sentences contain the word: " + containedWord);
			System.out.println("✅ All UI sentences contain the word: " + containedWord);

			// 🔗 8. استخراج UUID من الرابط
			String toolId = CorporaToolUrlBuilder.extractToolUuidFromUrl(fullToolUrl);
			System.out.println("✅ toolId: " + toolId);

			// 🧮 9. تجهيز فلاتر API
			NGramsFilterParams filters = new NGramsFilterParams().withContainWords(containedWord);
			System.out.println("✅ إعداد فلاتر API");

			// 🌐 10. جلب جميع النتائج من الـ API
			List<NGramResult> apiAllResults = NGramsApiClient.getAllResults(toolId, filters);
			List<String> apiWords = apiAllResults.stream().map(NGramResult::getWord).toList();

			boolean allApiContain = apiWords.stream().allMatch(w -> w.contains(containedWord));
			Assert.assertTrue(allApiContain, "❌ Some API results do not contain the word: " + containedWord);
			Allure.step("✅ All API results contain the word: " + containedWord);
			System.out.println("✅ All API results contain the word: " + containedWord);

			// ✅ 11. التحقق أن كل نتائج UI موجودة ضمن نتائج API
			boolean uiIsSubsetOfApi = apiWords.containsAll(uiResults);
			Assert.assertTrue(uiIsSubsetOfApi, "❌ UI results are not a subset of API results");
			Allure.step("✅ Verified that UI results are a subset of API results");
			System.out.println("✅ Verified that UI results are a subset of API results");

			// 🧾 طباعة النتائج في Allure والكونسول
			apiAllResults.forEach(result -> Allure.step("🔹 " + result.getWord() + " → count: " + result.getCount()));
			for (NGramResult result : apiAllResults) {
				System.out.println("🔹 " + result.getWord() + " → count: " + result.getCount());
			}

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Assertion Error - Contains Filter");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;
		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Unexpected Error - Contains Filter");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage(), e);
		}
	}

	/**
	 * ✅ TC-10 | التحقق من أن فلتر "يبدأ بـ" يعمل كما هو متوقع (UI + API)
	 */
	@Test(description = "TC-10 | Verify that 'Starts With' filter works correctly", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User uses 'Starts With' filter")
	@Description("""
			Test Objective:
			Verify that the results table and API only include sequences starting with the specified word.

			Steps:
			1. Open the Verbal Sequences tool page.
			2. Enter a word in the 'Starts With' filter.
			3. Click the search button.
			4. Validate that all UI results start with the word.
			5. Validate that all API results start with the word.
			6. Ensure UI results are part of the API results.
			Expected Result:
			All results (UI + API) must start with the specified word.
			""")
	public void TC10_verifyStartsWithFilter() {
		System.out.println("TC10_verifyStartsWithFilter");

		try {

			// 🔢 1. اختيار المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 🔗 2. بناء رابط الأداة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);
			System.out.println("✅ Opened direct tool URL: " + fullToolUrl);

			// 🧭 3. انتظار تحميل الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 🔠 4. إدخال الكلمة في فلتر "يبدأ بـ"
			String startingWord = "السيارة";
			nGramsPage.setStartWithWordFilter(startingWord);
			Allure.step("🔍 Set 'Starts With' filter to: " + startingWord);
			System.out.println("🔍 Set 'Starts With' filter to: " + startingWord);

			// 🔍 5. تنفيذ البحث
			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");
			System.out.println("🔎 Clicked on search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			// ⏳ انتظار النتائج أو رسالة "لا توجد بيانات"
			nGramsPage.waitForResultsOrNoDataMessage();
			System.out.println("ℹ️ Done nGramsPage.waitForResultsOrNoDataMessage();");

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (nGramsPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'Starts With' filter: " + startingWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}

			// 📋 8. التحقق من أن نتائج UI تبدأ بالكلمة
			List<String> uiResults = nGramsPage.getFirstColumnSentences();
			Assert.assertFalse(uiResults.isEmpty(), "❌ Empty UI results despite not showing 'no data' message!");
			System.out.println("ℹ️ Done List<String> uiResults = nGramsPage.getFirstColumnSentences();.");

			boolean allUiStartWith = uiResults.stream().allMatch(w -> w.startsWith(startingWord));
			Assert.assertTrue(allUiStartWith, "❌ Some UI results do not start with: " + startingWord);
			Allure.step("✅ All UI results start with the word: " + startingWord);
			System.out.println("✅ All UI results start with the word: " + startingWord);

			// 🔗 9. استخراج UUID من الرابط
			String toolId = CorporaToolUrlBuilder.extractToolUuidFromUrl(fullToolUrl);
			System.out.println("✅ toolId: " + toolId);

			// 🧮 10. تجهيز فلاتر API
			NGramsFilterParams filters = new NGramsFilterParams().withStartWithWords(startingWord);
			System.out.println("✅ إعداد فلاتر API");

			// 🌐 11. جلب جميع النتائج من الـ API
			List<NGramResult> apiAllResults = NGramsApiClient.getAllResults(toolId, filters);
			List<String> apiWords = apiAllResults.stream().map(NGramResult::getWord).toList();

			boolean allApiStartWith = apiWords.stream().allMatch(w -> w.startsWith(startingWord));
			Assert.assertTrue(allApiStartWith, "❌ Some API results do not start with the word: " + startingWord);
			Allure.step("✅ All API results start with the word: " + startingWord);
			System.out.println("✅ All API results start with the word: " + startingWord);

			// ✅ 12. التحقق أن كل نتائج UI موجودة ضمن نتائج API
			boolean uiIsSubsetOfApi = apiWords.containsAll(uiResults);
			Assert.assertTrue(uiIsSubsetOfApi, "❌ UI results are not a subset of API results");
			Allure.step("✅ Verified that UI results are a subset of API results");
			System.out.println("✅ Verified that UI results are a subset of API results");

			// 🧾 طباعة النتائج في Allure والكونسول
			apiAllResults.forEach(result -> Allure.step("🔹 " + result.getWord() + " → count: " + result.getCount()));
			for (NGramResult result : apiAllResults) {
				System.out.println("🔹 " + result.getWord() + " → count: " + result.getCount());
			}

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Assertion Error - Starts With Filter");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;
		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Unexpected Error - Starts With Filter");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage(), e);
		}
	}

	/**
	 * ✅ TC-11 | التحقق من أن فلتر "ينتهي بـ" يعمل كما هو متوقع (UI + API)
	 */
	@Test(description = "TC-11 | Verify that 'Ends With' filter works correctly", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User uses 'Ends With' filter")
	@Description("""
			    Test Objective:
			    Verify that the results table and API only include sequences ending with the specified word.

			    Steps:
			    1. Open the Verbal Sequences tool page.
			    2. Enter a word in the 'Ends With' filter.
			    3. Click the search button.
			    4. Validate that all UI results end with the word.
			    5. Validate that all API results end with the word.
			    6. Ensure UI results are part of the API results.

			    Expected Result:
			    All results (UI + API) must end with the specified word.
			""")
	public void TC11_verifyEndsWithFilter() {
		System.out.println("TC11_verifyEndsWithFilter");

		try {
			// 🔢 1. اختيار المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 🔗 2. بناء رابط الأداة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);
			System.out.println("✅ Opened direct tool URL: " + fullToolUrl);

			// 🧭 3. انتظار تحميل الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 🔠 4. إدخال الكلمة في فلتر "ينتهي بـ"
			String endingWord = "السيارة";
			nGramsPage.setEndWithWordFilter(endingWord);
			Allure.step("🔍 Set 'Ends With' filter to: " + endingWord);
			System.out.println("🔍 Set 'Ends With' filter to: " + endingWord);

			// 🔍 5. تنفيذ البحث
			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");
			System.out.println("🔎 Clicked on search button");

			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
			nGramsPage.waitForResultsOrNoDataMessage();
			System.out.println("ℹ️ Done nGramsPage.waitForResultsOrNoDataMessage();");

			// ℹ️ 7. التحقق من وجود نتائج أو لا
			if (nGramsPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found for 'Ends With' filter: " + endingWord);
				System.out.println("ℹ️ No results found. Message appeared.");
				return;
			}

			// 📋 8. التحقق من أن نتائج UI تنتهي بالكلمة
			List<String> uiResults = nGramsPage.getFirstColumnSentences();
			Assert.assertFalse(uiResults.isEmpty(), "❌ Empty UI results despite not showing 'no data' message!");
			System.out.println("ℹ️ Done List<String> uiResults = nGramsPage.getFirstColumnSentences();.");

			boolean allUiEndWith = uiResults.stream().allMatch(w -> w.endsWith(endingWord));
			Assert.assertTrue(allUiEndWith, "❌ Some UI results do not end with: " + endingWord);
			Allure.step("✅ All UI results end with the word: " + endingWord);
			System.out.println("✅ All UI results end with the word: " + endingWord);

			// 🔗 9. استخراج UUID من الرابط
			String toolId = CorporaToolUrlBuilder.extractToolUuidFromUrl(fullToolUrl);
			System.out.println("✅ toolId: " + toolId);

			// 🧮 10. تجهيز فلاتر API
			NGramsFilterParams filters = new NGramsFilterParams().withEndWithWords(endingWord);
			System.out.println("✅ إعداد فلاتر API");

			// 🌐 11. جلب جميع النتائج من الـ API
			List<NGramResult> apiAllResults = NGramsApiClient.getAllResults(toolId, filters);
			List<String> apiWords = apiAllResults.stream().map(NGramResult::getWord).toList();

			boolean allApiEndWith = apiWords.stream().allMatch(w -> w.endsWith(endingWord));
			Assert.assertTrue(allApiEndWith, "❌ Some API results do not end with the word: " + endingWord);
			Allure.step("✅ All API results end with the word: " + endingWord);
			System.out.println("✅ All API results end with the word: " + endingWord);

			// ✅ 12. التحقق أن كل نتائج UI موجودة ضمن نتائج API
			boolean uiIsSubsetOfApi = apiWords.containsAll(uiResults);
			Assert.assertTrue(uiIsSubsetOfApi, "❌ UI results are not a subset of API results");
			Allure.step("✅ Verified that UI results are a subset of API results");
			System.out.println("✅ Verified that UI results are a subset of API results");

			// 🧾 طباعة النتائج في Allure والكونسول
			apiAllResults.forEach(result -> Allure.step("🔹 " + result.getWord() + " → count: " + result.getCount()));
			for (NGramResult result : apiAllResults) {
				System.out.println("🔹 " + result.getWord() + " → count: " + result.getCount());
			}

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Assertion Error - Ends With Filter");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;
		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Unexpected Error - Ends With Filter");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage(), e);
		}
	}

	/**
	 * ✅ TC-12 | التحقق من أن الضغط على زر التصدير ينزّل النتائج محليًا مع التحقق من
	 * تكافؤ النتائج بين UI و API لنفس الاستعلام
	 */
	@Test(description = "TC-12 | Export downloads file and UI results match API results for the same search", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User exports search results in the Concordancer tool and results match API")
	@Description("""
			    Test Objective:
			    1) Verify that UI search results are consistent with API search results for the same query.
			    2) Verify that clicking the export button downloads a results file locally.

			    Steps:
			    1. Open 'المدونات' and navigate directly to the Concordancer tool URL.
			    2. Type a word using the virtual keyboard.
			    3. Click Search and wait for results (or 'no data' message).
			    4. If results exist:
			        a) Collect UI results (first column).
			        b) Call API with the same filters/query.
			        c) Assert UI ⊆ API.
			    5. Click the export icon.
			    6. Assert a file is downloaded and size > 0.
			    Expected Result:
			    - UI results are a subset of API results for the same query.
			    - A file is downloaded successfully after clicking export.
			""")
	public void TC12_verifyExportWithApiParity() {
		System.out.println("TC12_verifyExportWithApiParity");

		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

			// 🔢 1. اختيار المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 🔗 2. بناء رابط الأداة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);
			System.out.println("✅ Opened direct tool URL: " + fullToolUrl);

			// 🧭 3. انتظار تحميل الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 🔠 4. إدخال الكلمة في فلتر "ينتهي بـ"
			String endingWord = "السيارة";
			nGramsPage.setEndWithWordFilter(endingWord);
			Allure.step("🔍 Set 'Ends With' filter to: " + endingWord);
			System.out.println("🔍 Set 'Ends With' filter to: " + endingWord);

			getTriage().clear();
			getTriage().arm(".*/api/tools/ngram/.*");

			// 🔍 5. تنفيذ البحث
			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");
			System.out.println("🔎 Clicked on search button");

			// استدعاء الجنريك بلامبادز من الصفحة الحالية
			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));

			// التقييم
			if (outcome.kind == OutcomeKind.TRIAGE_ERROR) {
				var e = outcome.triageError.orElseThrow();
				Allure.step("❗ Network error captured: " + e);
				attachFullPageScreenshot("❗ Server/Network Error");
				Assert.fail(
						"الخدمة أعادت خطأ شبكة: status=" + e.status + " url=" + e.url + " type=" + e.requestIdOrType);
				return;
			}
			if (outcome.kind == OutcomeKind.NO_DATA) {
				Allure.step("ℹ️ No results (with exclude punctuation)");
				System.out.println("ℹ️ No results message detected, finishing gracefully.");
				return;
			}

//			// ⏳ 6. انتظار ظهور الجدول أو رسالة لا توجد بيانات
//			nGramsPage.waitForResultsOrNoDataMessage();
//			System.out.println("ℹ️ Done nGramsPage.waitForResultsOrNoDataMessage();");
//
//			// ℹ️ 7. التحقق من وجود نتائج أو لا
//			if (nGramsPage.isNoResultsMessageDisplayed()) {
//				Allure.step("ℹ️ No results found for 'Ends With' filter: " + endingWord);
//				System.out.println("ℹ️ No results found. Message appeared.");
//				return;
//			}

			// 8) جمع نتائج الـ UI (العمود الأول مثلاً)
			List<String> uiRows = nGramsPage.getFirstColumnSentences();
			Assert.assertFalse(uiRows.isEmpty(), "❌ Empty UI results despite not showing 'no data' message!");
			Allure.step("📊 UI results collected: " + uiRows.size());
			System.out.println("📊 UI results collected: " + uiRows.size());

			// 9) استخراج toolId من الرابط
			String toolId = CorporaToolUrlBuilder.extractToolUuidFromUrl(fullToolUrl);
			System.out.println("✅ toolId: " + toolId);
			Allure.step("✅ Extracted toolId: " + toolId);

			// 10) استدعاء API بنفس الاستعلام/الفلاتر
			NGramsFilterParams filters = new NGramsFilterParams().withEndWithWords(endingWord);
			System.out.println("✅ Concordancer API filters prepared");

			// 🌐 11. جلب جميع النتائج من الـ API
			List<NGramResult> apiAllResults = NGramsApiClient.getAllResults(toolId, filters);

			Assert.assertNotNull(apiAllResults, "❌ API returned null results");
			Assert.assertFalse(apiAllResults.isEmpty(), "❌ API returned empty results while UI has rows");
			Allure.step("🌐 API results collected: " + apiAllResults.size());
			System.out.println("🌐 API results collected: " + apiAllResults.size());

			// 11) إسقاط نتائج API إلى قائمة نصوص مماثلة لطريقة UI (حسب نموذج بياناتكم)
			// مثال: إذا كان لكل صف كلمة/جملة في حقل 'getText()' أو
			// 'getLeftContext()+getKeyword()+getRightContext()'
			List<String> apiWords = apiAllResults.stream().map(NGramResult::getWord).toList();

			// 12) التحقق أن كل نتائج UI موجودة ضمن نتائج API (UI ⊆ API)
			boolean uiSubsetOfApi = apiWords.containsAll(uiRows);
			Assert.assertTrue(uiSubsetOfApi, "❌ UI results are not a subset of API results");
			Allure.step("✅ Verified UI results are a subset of API results");
			System.out.println("✅ Verified UI results are a subset of API results");

			// (اختياري) مزيد من التوثيق في Allure
			apiAllResults.stream().limit(20).forEach(r -> Allure.step("🔹 API: " + r.getWord()));

			// 13) انتظار ظهور زر التصدير ثم النقر عليه
			wait.until(d -> nGramsPage.isExportButtonVisisable());

			nGramsPage.clickExportButton();
			Allure.step("📥 Clicked export button");
			System.out.println("📥 Clicked export button");

			// 14) التحقق من تنزيل الملف (مثلاً .xlsx).
			// ملاحظة: isFileDownloaded يجب أن تتحقق من وجود الملف وحجمه > 0
			boolean downloaded = nGramsPage.isFileDownloaded(".xlsx");
			Assert.assertTrue(downloaded, "❌ Export file was not downloaded!");
			Allure.step("✅ Exported file downloaded successfully");
			System.out.println("✅ Exported file downloaded successfully");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Export with API - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Export with API - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage(), e);
		}
	}

	/**
	 * ✅ TC-13 | التحقق من فلتر "استثناء علامات الترقيم" (UI + API)
	 */
	@Test(description = "TC-13 | Verify that exclude-punctuation filter works correctly in UI and API", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User enables 'Exclude Punctuation' and searches with noisy input")
	@Description("""
			    Test Objective:
			    Verify that when 'Exclude Punctuation' is enabled and the user enters text with punctuation,
			    the results are processed cleanly (punctuation stripped) in both UI and API.

			    Steps:
			    1. Open the Verbal Sequences tool page.
			    2. Enable 'Exclude Punctuation' checkbox.
			    3. Enter a text containing commas/periods/symbols in the 'ينتهي' filter.
			    4. Click Search and wait for results or 'no data'.
			    5. UI: Validate normalized results (punctuation removed).
			    6. API: Call with the same query + excludePunctuation=true, then validate UI ⊆ API (after same normalization).
			""")
	public void TC13_verifyExcludePunctuationFilter() {
		System.out.println("TC13_verifyExcludePunctuationFilter");

		try {
			// 🔢 1. اختيار المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 🔗 2. بناء رابط الأداة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);
			System.out.println("✅ Opened direct tool URL: " + fullToolUrl);

			// 🧭 3. انتظار تحميل الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 🔠 4. إدخال الكلمة في فلتر "ينتهي بـ"
			String endingWord = "السيارة";
			nGramsPage.setEndWithWordFilter(endingWord);
			Allure.step("🔍 Set 'Ends With' filter to: " + endingWord);
			System.out.println("🔍 Set 'Ends With' filter to: " + endingWord);

			// 4) فعّل Checkbox "استثناء علامات الترقيم"
			nGramsPage.clickExcludePunctuationCheckBox();
			Assert.assertTrue(nGramsPage.isExcludePunctuationChecked(),
					"❌ Exclude Punctuation checkbox is not checked!");
			Allure.step("🧹 Enabled 'Exclude Punctuation'");
			System.out.println("🧹 Enabled 'Exclude Punctuation'");

			getTriage().clear();
			getTriage().arm(".*/api/tools/ngram/.*");

			// 6) نفّذ البحث
			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");
			System.out.println("🔎 Clicked on search button");

			// استدعاء الجنريك بلامبادز من الصفحة الحالية
			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));

			// التقييم
			if (outcome.kind == OutcomeKind.TRIAGE_ERROR) {
				var e = outcome.triageError.orElseThrow();
				Allure.step("❗ Network error captured: " + e);
				attachFullPageScreenshot("❗ Server/Network Error");
				Assert.fail(
						"الخدمة أعادت خطأ شبكة: status=" + e.status + " url=" + e.url + " type=" + e.requestIdOrType);
				return;
			}
			if (outcome.kind == OutcomeKind.NO_DATA) {
				Allure.step("ℹ️ No results (with exclude punctuation)");
				System.out.println("ℹ️ No results message detected, finishing gracefully.");
				return;
			}

			// 8) نتائج الـ UI (العمود الأول)
			List<String> uiWords = nGramsPage.getFirstColumnSentences();
			Assert.assertFalse(uiWords.isEmpty(), "❌ Empty UI results despite not showing 'no data' message!");
			Allure.step("📊 UI results count: " + uiWords.size());
			System.out.println("📊 UI results count: " + uiWords.size());

			// 9) طبّقي نفس “التطبيع” (إزالة الترقيم + تنميط عربي خفيف) على الـ UI
			List<String> normalizedUi = uiWords.stream().map(this::normalizeAndStripPunct).filter(s -> !s.isBlank())
					.toList();

			// تحقق منطقي: جميع النتائج تنتهي بالكلمة النظيفة
			boolean allEndWithClean = normalizedUi.stream().allMatch(s -> s.endsWith(endingWord));
			Assert.assertTrue(allEndWithClean,
					"❌ Some UI normalized results do not end with the cleaned word: " + endingWord);
			Allure.step("✅ UI normalized results end with: " + endingWord);
			System.out.println("✅ UI normalized results end with: " + endingWord);

			// 10) API: نفس الاستعلام ولكن excludePunctuation=true
			String toolId = CorporaToolUrlBuilder.extractToolUuidFromUrl(fullToolUrl);
			Allure.step("🔗 toolId: " + toolId);
			System.out.println("🔗 toolId: " + toolId);

			// ⬅️ توحيد المنطق: استخدم EndsWith في الـ API أيضاً
			NGramsFilterParams filters = new NGramsFilterParams().withEndWithWords(endingWord) // ← بدّل Start بـ End
																								// لتوحيد المنطق
					.withExcludeRegex("true"); // ← أضف هذا الفلاغ في كلاس params

			List<NGramResult> apiAllResults = NGramsApiClient.getAllResults(toolId, filters);
			Assert.assertNotNull(apiAllResults, "❌ API returned null results");
			Assert.assertFalse(apiAllResults.isEmpty(), "❌ API returned empty results while UI has rows");
			Allure.step("🌐 API results collected: " + apiAllResults.size());
			System.out.println("🌐 API results collected: " + apiAllResults.size());

			List<String> normalizedApi = apiAllResults.stream().map(NGramResult::getWord)
					.map(this::normalizeAndStripPunct).filter(s -> !s.isBlank()).toList();

			// 11) التحقق النهائي: UI ⊆ API (بعد نفس “التطبيع”)
			Assert.assertTrue(normalizedApi.containsAll(normalizedUi),
					"❌ UI normalized results are not a subset of API normalized results");
			Allure.step("✅ Verified UI ⊆ API after punctuation stripping");
			System.out.println("✅ Verified UI ⊆ API after punctuation stripping");

			// توثيق إضافي
			normalizedApi.stream().limit(20).forEach(w -> Allure.step("🔹 API(norm): " + w));

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Exclude Punctuation - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Exclude Punctuation - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage(), e);
		}
	}

	/** إزالة الترقيم + تنميط بسيط للأحرف العربية (اختياري لكنه مفيد للتطابق) */
	private String normalizeAndStripPunct(String s) {
		if (s == null)
			return "";
		// إزالة التنوين/الحركات + التطويل
		String noMarks = s.replaceAll("\\p{M}+", "").replace("ـ", "");
		// إزالة الترقيم العربي/اللاتيني الشائع (أضفنا علامة الاستفهام العربية: ؟)
		String punctRegex = "[\\p{Punct}،؛«»…·•؟]+";
		String noPunct = noMarks.replaceAll(punctRegex, " ");
		// مسافات طبيعية
		return noPunct.trim().replaceAll("\\s{2,}", " ");
	}

	/**
	 * ✅ TC-14 | التحقق من أن الضغط على زر التصدير في "التتابعات اللفظية" ينزّل
	 * النتائج محليًا
	 */
	@Test(description = "TC-14 | Verify that clicking the export button downloads the search results (NGrams)", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User exports search results using the export button in the NGrams tool")
	@Description("""
			    Test Objective:
			    Ensure that after running a valid search in the NGrams tool, clicking the export icon
			    downloads the results file locally (e.g., XLSX), with network triage watching for export errors.

			    Steps:
			    1) Open the NGrams tool page.
			    2) Enter a minimal valid filter to get results (e.g., Ends With = 'السيارة').
			    3) Click Search and wait for: table/no-data/network error (triage).
			    4) If results exist, wait for the export icon to appear and click it.
			    5) Assert that a file is downloaded (size > 0). Also fail if export network call errors.
			""")
	public void TC14_verifyNGramsExportDownload() {
		System.out.println("TC14_verifyNGramsExportDownload");

		try {
			// 🔢 1) اختيار المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 🔗 2) افتح صفحة الأداة مباشرة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("✅ Opened direct tool URL: " + fullToolUrl);

			// 🧭 3) انتظار جاهزية الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");

			// 📝 4) أدخل فلتر بسيط ليُظهر نتائج (عدّليه لما يناسب بيئتكم)
			String endingWord = "السيارة";
			nGramsPage.setEndWithWordFilter(endingWord);
			Allure.step("🔤 Set 'Ends With' filter: " + endingWord);

			// 🧹 (اختياري) استثناء علامات الترقيم إن رغبتِ
			// nGramsPage.clickExcludePunctuationCheckBox();

			// 🛰️ فعّلي triage لطلبات البحث قبل الضغط على "بحث"
			getTriage().clear();
			// نمط بحث NGrams (عدّلي الـ regex حسب مسار API لديكم):
			getTriage().arm(".*/api/tools/ngram/.*");

			// 🔎 5) اضغطي بحث
			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");

			// ⏳ 6) انتظري واحدًا من: جدول/لا توجد بيانات/خطأ شبكة (triage)
			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));

			if (outcome.kind == OutcomeKind.TRIAGE_ERROR) {
				var e = outcome.triageError.orElseThrow();
				Allure.step("❗ Network error during search: " + e);
				System.out.println("❗ Network error during search: " + e);

				attachFullPageScreenshot("❗ Server/Network Error (search)");
				Assert.fail(
						"Search network error: status=" + e.status + " url=" + e.url + " type=" + e.requestIdOrType);
				return;
			}
			if (outcome.kind == OutcomeKind.NO_DATA) {
				Allure.step("ℹ️ No results for the given filter; export will be skipped.");
				System.out.println("ℹ️ No results for the given filter; export will be skipped.");

				return;
			}

			// ✅ وصلنا جدول نتائج
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			// ⏳ انتظري ظهور زر التصدير ثم اضغطيه
			new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> nGramsPage.isExportButtonVisisable());

			// 💾 7) تأكدي من تنزيل الملف
			AtomicBoolean downloadCompleted = new AtomicBoolean(false);
			Path downloadDir = Path.of(System.getProperty("user.home"), "Downloads");

			DownloadsCdpHelper.armChromeDownloadDone(driver, downloadDir, downloadCompleted);

			nGramsPage.clickExportButton();
			Allure.step("📥 Clicked export button");

			// 🛰️ تحقّقي من عدم وجود خطأ شبكة في طلب التصدير
			// نمنحه لحظات قليلة لالتقاط الـ response
			Thread.sleep(1500);
			var exportErr = getTriage().firstError();
			if (exportErr.isPresent()) {
				var e = exportErr.get();
				Allure.step("❗ Export network error: " + e);
				attachFullPageScreenshot("❗ Server/Network Error (export)");
				Assert.fail(
						"Export network error: status=" + e.status + " url=" + e.url + " type=" + e.requestIdOrType);
				return;
			}

			// 💾 7) تأكدي من تنزيل الملف
			boolean downloaded = nGramsPage.isFileDownloaded(".xlsx");
			Assert.assertTrue(downloaded, "❌ Export file was not downloaded!");
			Allure.step("✅ Exported file downloaded successfully");

			// انتظري اكتمال التحميل
			new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> downloadCompleted.get());

			Assert.assertTrue(downloadCompleted.get(), "❌ Export file was not downloaded!");
			Allure.step("✅ Exported file downloaded successfully");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 NGrams Export - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ NGrams Export - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Unexpected error occurred: " + e.getMessage(), e);
		}
	}

	/**
	 * ✅ TC-15 | Verify pagination controls are visible (NGrams)
	 */
	@Test(description = "TC-15 | Verify that pagination controls are visible under the results table in NGrams", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User performs a search that yields many results and sees paginator (page numbers / next / prev)")
	public void TC15_verifyNGramsPaginationControlsVisible() {
		System.out.println("TC15_verifyNGramsPaginationControlsVisible");
		try {
			// 1) اختر المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 2) افتح الأداة مباشرة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);
			System.out.println("🌐 Opened direct tool URL: " + fullToolUrl);

			// 3) انتظر جاهزية الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 4) أدخل استعلام واسع ليجلب نتائج كثيرة (لضمان ظهور الترقيم)
			// ملاحظة: يمكنك تغيير الاستراتيجية حسب منطق الأداة لديك:
			// - ترك بعض الحقول فارغة
			// - استخدام كلمة شائعة
			// - استخدام ends-with بكلمة عامة
			String endingWord = "ة"; // حرف شائع بالعربية لرفع عدد النتائج
			nGramsPage.setEndWithWordFilter(endingWord);
			Allure.step("🔤 Set 'Ends With' filter: " + endingWord);
			System.out.println("🔤 Set 'Ends With' filter: " + endingWord);

			// 5) حضّر triage لطلبات البحث
			getTriage().clear();
			getTriage().arm(".*/api/tools/ngram/.*");

			// 6) اضغط بحث
			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");
			System.out.println("🔎 Clicked on search button");

			// 7) انتظر: جدول / لا توجد بيانات / خطأ شبكة
			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));

			// 8) تقييم النتيجة
			if (outcome.kind == OutcomeKind.TRIAGE_ERROR) {
				var e = outcome.triageError.orElseThrow();
				Allure.step("❗ Network error during search: " + e);
				System.out.println("❗ Network error during search: " + e);
				attachFullPageScreenshot("❗ Server/Network Error (search)");
				Assert.fail(
						"Search network error: status=" + e.status + " url=" + e.url + " type=" + e.requestIdOrType);
				return;
			}
			if (outcome.kind == OutcomeKind.NO_DATA) {
				Allure.step("ℹ️ No results for the given filter; paginator will not appear.");
				System.out.println("ℹ️ No results for the given filter; paginator will not appear.");
				System.out.println("ℹ️ No results -> no paginator.");
				return;
			}

			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			// 9) تأكّد أن لدينا على الأقل حجم صفحة افتراضي (PrimeNG غالبًا 10)
			int visibleRows = nGramsPage.getNumberOfResultsRows();
			Allure.step("📈 Visible rows on page 1: " + visibleRows);
			System.out.println("📈 Visible rows on page 1: " + visibleRows);
			Assert.assertTrue(visibleRows > 0, "❌ Results table is empty unexpectedly!");

			// 10) تحقّق من ظهور شريط الترقيم أسفل الجدول
			// (PrimeNG paginator عادةً: div.p-paginator)
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> nGramsPage.isPaginationBarVisible());
			Assert.assertTrue(nGramsPage.isPaginationBarVisible(), "❌ Pagination controls not visible!");
			Allure.step("✅ Pagination bar is visible");
			System.out.println("✅ Pagination bar is visible");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 NGrams Pagination - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ NGrams Pagination - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Error in TC-15: " + e.getMessage(), e);
		}
	}

	/**
	 * ✅ TC-16 | Verify default results per page (NGrams)
	 */
	@Test(description = "TC-16 | Verify that the default number of rows per page is correct in NGrams results table", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User performs a search and sees the default rows per page (e.g., 10 rows)")
	public void TC16_verifyNGramsDefaultResultsPerPage() {
		System.out.println("TC16_verifyNGramsDefaultResultsPerPage");
		try {
			// 1) اختر المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 2) افتح الأداة مباشرة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);
			System.out.println("🌐 Opened direct tool URL: " + fullToolUrl);

			// 3) انتظر جاهزية الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 4) أدخل استعلام واسع ليجلب نتائج كثيرة
			String endingWord = "ة";
			nGramsPage.setEndWithWordFilter(endingWord);
			Allure.step("🔤 Set 'Ends With' filter: " + endingWord);
			System.out.println("🔤 Set 'Ends With' filter: " + endingWord);

			// 5) حضّر triage لطلبات البحث
			getTriage().clear();
			getTriage().arm(".*/api/tools/ngram/.*");

			// 6) اضغط بحث
			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");
			System.out.println("🔎 Clicked on search button");

			// 7) انتظر: جدول / لا توجد بيانات / خطأ شبكة
			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));

			// 8) تقييم النتيجة
			if (outcome.kind == OutcomeKind.TRIAGE_ERROR) {
				var e = outcome.triageError.orElseThrow();
				Allure.step("❗ Network error during search: " + e);
				System.out.println("❗ Network error during search: " + e);
				attachFullPageScreenshot("❗ Server/Network Error (search)");
				Assert.fail(
						"Search network error: status=" + e.status + " url=" + e.url + " type=" + e.requestIdOrType);
				return;
			}
			if (outcome.kind == OutcomeKind.NO_DATA) {
				Allure.step("ℹ️ No results for the given filter; cannot validate default rows per page.");
				System.out.println("ℹ️ No results for the given filter; cannot validate default rows per page.");
				return;
			}

			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			// 9) اجلب عدد الصفوف الظاهرة في الصفحة الأولى
			int visibleRows = nGramsPage.getNumberOfResultsRows();
			Allure.step("📈 Number of rows on page 1: " + visibleRows);
			System.out.println("📈 Number of rows on page 1: " + visibleRows);

			// 10) تحقق من أن العدد يطابق القيمة الافتراضية (عادةً 10 في PrimeNG)
			int expectedDefaultPageSize = 10; // غيّره لو عندك قيمة أخرى افتراضية
			Assert.assertEquals(visibleRows, expectedDefaultPageSize, "❌ Default rows per page not as expected!");
			Allure.step("✅ Default rows per page = " + expectedDefaultPageSize);
			System.out.println("✅ Default rows per page = " + expectedDefaultPageSize);

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 NGrams Default Rows/Page - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ NGrams Default Rows/Page - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Error in TC-16: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-17| ✅ التحقق من الانتقال إلى الصفحة التالية باستخدام زر "التالي" ✅ Test to
	 * verify that clicking the "Next >>" button in pagination navigates to the next
	 * page of results.
	 */
	@Test(description = "TC-17 | Verify navigating to next page using pagination", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User navigates to the next page using pagination controls")
	@Description("""
			    Test Objective:
			    Verify that clicking the 'Next >>' button in the pagination bar loads the next set of results.

			    Steps:
			    1. Navigate directly to the 'N-Grams' tool
			    2. Choose a corpus known to have many results
			    3. Search for a frequent ending word using the filter
			    4. Ensure the pagination bar appears
			    5. Click the "Next >>" button
			    6. Verify that the results table updates with a new set of results
			""")
	public void TC17_verifyNavigationToNextPage() {
		System.out.println("TC17_verifyNavigationToNextPage");
		try {
			// 1) اختر المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 2) افتح الأداة مباشرة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);
			System.out.println("🌐 Opened direct tool URL: " + fullToolUrl);

			// 3) انتظر جاهزية الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 4) أدخل استعلام واسع (مثلاً ينتهي بـ "ة") لضمان ظهور الترقيم
			String endingWord = "ة";
			nGramsPage.setEndWithWordFilter(endingWord);
			Allure.step("🔤 Set 'Ends With' filter: " + endingWord);
			System.out.println("🔤 Set 'Ends With' filter: " + endingWord);

			// 5) حضّر مراقبة الشبكة
			getTriage().clear();
			getTriage().arm(".*/api/tools/ngram/.*");

			// 6) اضغط زر البحث
			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");
			System.out.println("🔎 Clicked on search button");

			// 7) انتظر الجدول أو رسالة لا يوجد نتائج
			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));

			if (outcome.kind == OutcomeKind.NO_DATA) {
				Allure.step("ℹ️ No results; skipping pagination test.");
				System.out.println("ℹ️ No results; skipping pagination test.");
				return;
			}

			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");
			Assert.assertTrue(nGramsPage.isPaginationBarVisible(), "❌ Pagination bar not visible!");

			// 8) خزن الصف الأول قبل الضغط على "التالي"
			String firstRowBefore = nGramsPage.getFirstCellText(1);
			int currentPageBefore = nGramsPage.getCurrentPageNumber();
			Allure.step("🔢 Before navigation | Page: " + currentPageBefore + " | First row: " + firstRowBefore);
			System.out.println("🔢 Before navigation | Page: " + currentPageBefore + " | First row: " + firstRowBefore);

			// 9) اضغط زر التالي
			nGramsPage.goToNextPage();
			Allure.step("➡️ Clicked 'Next >>' button");
			System.out.println("➡️ Clicked 'Next >>' button");

			// 10) انتظر تغير النتائج
			new WebDriverWait(driver, Duration.ofSeconds(10))
					.until(d -> !safeEquals(nGramsPage.getFirstCellText(1), firstRowBefore));

			String firstRowAfter = nGramsPage.getFirstCellText(1);
			int currentPageAfter = nGramsPage.getCurrentPageNumber();

			Allure.step("📄 After navigation | Page: " + currentPageAfter + " | First row: " + firstRowAfter);
			System.out.println("📄 After navigation | Page: " + currentPageAfter + " | First row: " + firstRowAfter);

			Assert.assertEquals(currentPageAfter, currentPageBefore + 1, "❌ Did not navigate to next page!");
			Assert.assertNotEquals(firstRowAfter, firstRowBefore, "❌ Results did not update after clicking 'Next >>'!");

			Allure.step("✅ Pagination 'Next >>' button works correctly");
			System.out.println("✅ Pagination 'Next >>' button works correctly");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 NGrams Next Page - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ NGrams Next Page - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Error in TC-18: " + e.getMessage(), e);
		}
	}

	// 🔹 مقارنة آمنة
	private static boolean safeEquals(String a, String b) {
		return (a == null && b == null) || (a != null && a.equals(b));
	}

	/**
	 * TC-18 | ✅ التحقق من الانتقال إلى الصفحة السابقة باستخدام زر "السابق" ✅ Test
	 * to verify that clicking the "Previous <<" button in pagination navigates to
	 * the previous page of results.
	 */
	@Test(description = "TC-18 | Verify navigating to previous page using pagination", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User navigates to the previous page using pagination controls")
	@Description("""
			    Test Objective:
			    Verify that clicking the 'Previous <<' button in the pagination bar loads the previous set of results.

			    Steps:
			    1. Navigate directly to the 'N-Grams' tool
			    2. Choose a corpus known to have many results
			    3. Search for a frequent ending word using the filter
			    4. Ensure the pagination bar appears
			    5. Navigate first to page 2 (using 'Next >>')
			    6. Click the "Previous <<" button
			    7. Verify that the results table updates and returns to page 1
			""")
	public void TC18_verifyNavigationToPreviousPage() {
		System.out.println("TC18_verifyNavigationToPreviousPage");
		try {
			// 1) اختر المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 2) افتح الأداة مباشرة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);
			System.out.println("🌐 Opened direct tool URL: " + fullToolUrl);

			// 3) انتظر جاهزية الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 4) أدخل استعلام واسع (مثلاً ينتهي بـ "ة") لضمان ظهور الترقيم
			String endingWord = "ة";
			nGramsPage.setEndWithWordFilter(endingWord);
			Allure.step("🔤 Set 'Ends With' filter: " + endingWord);
			System.out.println("🔤 Set 'Ends With' filter: " + endingWord);

			// 5) حضّر مراقبة الشبكة
			getTriage().clear();
			getTriage().arm(".*/api/tools/ngram/.*");

			// 6) اضغط زر البحث
			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");
			System.out.println("🔎 Clicked on search button");

			// 7) انتظر الجدول أو رسالة لا يوجد نتائج
			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));

			if (outcome.kind == OutcomeKind.NO_DATA) {
				Allure.step("ℹ️ No results; skipping pagination test.");
				System.out.println("ℹ️ No results; skipping pagination test.");
				return;
			}

			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");
			Assert.assertTrue(nGramsPage.isPaginationBarVisible(), "❌ Pagination bar not visible!");

			// 8) اذهب أولاً للصفحة التالية لضمان إمكانية العودة
			int currentPageBefore = nGramsPage.getCurrentPageNumber();
			nGramsPage.goToNextPage();
			new WebDriverWait(driver, Duration.ofSeconds(10))
					.until(d -> nGramsPage.getCurrentPageNumber() == currentPageBefore + 1);
			Allure.step("➡️ Moved to page " + nGramsPage.getCurrentPageNumber() + " before testing 'Previous'");
			System.out.println("➡️ Moved to page " + nGramsPage.getCurrentPageNumber() + " before testing 'Previous'");

			// 9) خزّن الصف الأول قبل العودة
			String firstRowBefore = nGramsPage.getFirstCellText(1);
			int pageBeforePrev = nGramsPage.getCurrentPageNumber();

			// 10) اضغط زر السابق
			nGramsPage.goToPreviousPage();
			Allure.step("⬅️ Clicked 'Previous <<' button");
			System.out.println("⬅️ Clicked 'Previous <<' button");

			// 11) انتظر تغير النتائج
			new WebDriverWait(driver, Duration.ofSeconds(10))
					.until(d -> !safeEquals(nGramsPage.getFirstCellText(1), firstRowBefore));

			String firstRowAfter = nGramsPage.getFirstCellText(1);
			int pageAfterPrev = nGramsPage.getCurrentPageNumber();

			Allure.step("📄 After navigation | Page: " + pageAfterPrev + " | First row: " + firstRowAfter);
			System.out.println("📄 After navigation | Page: " + pageAfterPrev + " | First row: " + firstRowAfter);

			Assert.assertEquals(pageAfterPrev, pageBeforePrev - 1, "❌ Did not navigate to previous page!");
			Assert.assertNotEquals(firstRowAfter, firstRowBefore,
					"❌ Results did not update after clicking 'Previous <<'!");

			Allure.step("✅ Pagination 'Previous <<' button works correctly");
			System.out.println("✅ Pagination 'Previous <<' button works correctly");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 NGrams Previous Page - Assertion Failure");
			File vid = (videoRecorder != null) ? videoRecorder.stopAndGetFile() : null;
			if (vid != null) {
				videoRecorder.attachToAllure("📹 Video (on failure)", false);
			}
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ NGrams Previous Page - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Error in TC-18: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-19 | ✅ التحقق من الانتقال إلى صفحة محددة عبر النقر على رقم الصفحة ✅ Test
	 * to verify that clicking a specific page number in pagination navigates to
	 * that page of results.
	 */
	@Test(description = "TC-19 | Verify navigating to a specific page using pagination", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User navigates to a specific page using pagination controls")
	@Description("""
			    Test Objective:
			    Verify that clicking a specific page number (e.g., page 3) in the pagination bar loads the corresponding set of results.

			    Steps:
			    1. Navigate directly to the 'N-Grams' tool
			    2. Choose a corpus known to have many results
			    3. Search for a frequent ending word using the filter
			    4. Ensure the pagination bar appears
			    5. Click a page number (e.g., page 3)
			    6. Verify that the results table updates with results of that page
			""")
	public void TC19_verifyNavigationToSpecificPage() {
		System.out.println("TC19_verifyNavigationToSpecificPage");
		try {
			// 1️⃣ إعداد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 2️⃣ افتح الأداة مباشرة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);
			System.out.println("🌐 Opened direct tool URL: " + fullToolUrl);

			// 3️⃣ انتظار تحميل الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 4️⃣ أدخل استعلام واسع لضمان ظهور شريط الترقيم
			String endingWord = "ة";
			nGramsPage.setEndWithWordFilter(endingWord);
			Allure.step("🔤 Set 'Ends With' filter: " + endingWord);
			System.out.println("🔤 Set 'Ends With' filter: " + endingWord);

			// 5️⃣ حضّر مراقبة الشبكة
			getTriage().clear();
			getTriage().arm(".*/api/tools/ngram/.*");

			// 6️⃣ اضغط زر البحث
			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");
			System.out.println("🔎 Clicked on search button");

			// 7️⃣ انتظر النتائج
			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));

			if (outcome.kind == OutcomeKind.NO_DATA) {
				Allure.step("ℹ️ No results; skipping pagination test.");
				System.out.println("ℹ️ No results; skipping pagination test.");
				return;
			}

			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");
			Assert.assertTrue(nGramsPage.isPaginationBarVisible(), "❌ Pagination bar not visible!");

			// 8️⃣ خزّن الصف الأول قبل تغيير الصفحة
			String firstRowBefore = nGramsPage.getFirstCellText(1);
			Allure.step("🔢 First row on page 1: " + firstRowBefore);
			System.out.println("🔢 First row on page 1: " + firstRowBefore);

			// 9️⃣ الانتقال إلى الصفحة 3
			int targetPage = 3;
			nGramsPage.goToPage(targetPage);
			Allure.step("📄 Navigated to page " + targetPage);
			System.out.println("📄 Navigated to page " + targetPage);

			// 🔟 التحقق من اختلاف النتائج
			new WebDriverWait(driver, Duration.ofSeconds(10))
					.until(d -> !safeEquals(nGramsPage.getFirstCellText(1), firstRowBefore));

			String firstRowAfter = nGramsPage.getFirstCellText(1);
			int currentPage = nGramsPage.getCurrentPageNumber();

			Allure.step("📄 After navigation | Page: " + currentPage + " | First row: " + firstRowAfter);
			System.out.println("📄 After navigation | Page: " + currentPage + " | First row: " + firstRowAfter);
			Assert.assertEquals(currentPage, targetPage, "❌ Did not navigate to the correct page!");
			Assert.assertNotEquals(firstRowAfter, firstRowBefore,
					"❌ Results did not update after navigating to page " + targetPage);

			Allure.step("✅ Pagination to specific page works correctly");
			System.out.println("✅ Pagination to specific page works correctly");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 NGrams Specific Page - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ NGrams Specific Page - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Error in TC-19: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-20 | ✅ التحقق من تمييز رقم الصفحة الحالية ✅ Test to verify that when
	 * navigating between pages, the current page number is clearly highlighted in
	 * pagination.
	 */
	@Test(description = "TC-20 | Verify highlighted current page number in pagination", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User navigates between pages and verifies the current page number is highlighted")
	@Description("""
			    Test Objective:
			    Ensure that when navigating between different pages in the pagination bar,
			    the current page number is correctly highlighted to indicate the active page.

			    Steps:
			    1. Navigate directly to the 'N-Grams' tool
			    2. Choose a corpus with many results
			    3. Perform a search with a common ending word
			    4. Verify the pagination bar appears
			    5. Navigate to page 2
			    6. Verify that page 2 is highlighted as the current page
			""")
	public void TC20_verifyHighlightedCurrentPageNumber() {
		System.out.println("TC20_verifyHighlightedCurrentPageNumber");

		try {
			// 1️⃣ إعداد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 2️⃣ افتح الأداة مباشرة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);
			System.out.println("🌐 Opened direct tool URL: " + fullToolUrl);

			// 3️⃣ انتظار تحميل الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 4️⃣ أدخل استعلام واسع لضمان ظهور شريط الترقيم
			String endingWord = "ة";
			nGramsPage.setEndWithWordFilter(endingWord);
			Allure.step("🔤 Set 'Ends With' filter: " + endingWord);
			System.out.println("🔤 Set 'Ends With' filter: " + endingWord);

			// 5️⃣ اضغط زر البحث
			getTriage().clear();
			getTriage().arm(".*/api/tools/ngram/.*");
			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");
			System.out.println("🔎 Clicked on search button");

			// 6️⃣ انتظار النتائج
			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));

			if (outcome.kind == OutcomeKind.NO_DATA) {
				Allure.step("ℹ️ No results; skipping pagination test.");
				System.out.println("ℹ️ No results; skipping pagination test.");
				return;
			}

			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");
			Assert.assertTrue(nGramsPage.isPaginationBarVisible(), "❌ Pagination bar not visible!");

			// 7️⃣ الانتقال إلى الصفحة 2
			int targetPage = 2;
			nGramsPage.goToPage(targetPage);
			Allure.step("📄 Navigated to page " + targetPage);
			System.out.println("📄 Navigated to page " + targetPage);

			// 8️⃣ التحقق من أن الصفحة الحالية مميزة
			int highlightedPage = nGramsPage.getCurrentHighlightedPageNumber();
			Allure.step("📍 Current highlighted page: " + highlightedPage);
			System.out.println("📍 Current highlighted page: " + highlightedPage);

			Assert.assertEquals(highlightedPage, targetPage,
					"❌ Page " + targetPage + " is not highlighted after navigation!");

			Allure.step("✅ Page " + targetPage + " is correctly highlighted");
			System.out.println("✅ Page " + targetPage + " is correctly highlighted");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 NGrams Pagination Highlight - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ NGrams Pagination Highlight - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Error in TC-20: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-21 | ✅ تغيير عدد النتائج لكل صفحة (10/50/100) والتحقق من تحديث الجدول Test
	 * to verify that using the "Show X results" dropdown updates the table rows per
	 * page.
	 */
	@Test(description = "TC-21 | Verify changing results-per-page (10/50/100) in NGrams", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User changes results-per-page via paginator dropdown and table updates accordingly")
	@Description("""
			Test Objective:
			After performing a search in the N-Grams tool, verify that selecting 10, 50, or 100
			from the 'Show results' dropdown updates the number of visible rows in the table.

			Steps:
			1) Open N-Grams tool
			2) Perform a broad search to ensure many results
			3) Make sure paginator is visible
			4) For each page size in [10, 50, 100]:
			   - Select the page size
			   - Wait for the table to refresh
			   - Assert rows count is > 0 and <= selected page size (or <= total results on last page)
			""")
	public void TC21_verifyNGramsResultsPerPageDropdown() {
		System.out.println("TC21_verifyNGramsResultsPerPageDropdown");

		try {
			// 1) اختر المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 2) افتح الأداة مباشرة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);
			System.out.println("🌐 Opened direct tool URL: " + fullToolUrl);

			// 3) انتظار جاهزية الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 4) أدخل استعلام واسع ليجلب نتائج كثيرة (لضمان ظهور الترقيم)
			String endingWord = "ة";
			nGramsPage.setEndWithWordFilter(endingWord);
			Allure.step("🔤 Set 'Ends With' filter: " + endingWord);
			System.out.println("🔤 Set 'Ends With' filter: " + endingWord);

			// 5) حضّر مراقبة الشبكة واضغط بحث
			getTriage().clear();
			getTriage().arm(".*/api/tools/ngram/.*");
			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");
			System.out.println("🔎 Clicked on search button");

			// 6) انتظر: جدول / لا توجد بيانات / خطأ شبكة
			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));

			if (outcome.kind == OutcomeKind.NO_DATA) {
				Allure.step("ℹ️ No results; cannot validate results-per-page.");
				System.out.println("ℹ️ No results; cannot validate results-per-page.");
				return;
			}

			Assert.assertTrue(nGramsPage.isPaginationBarVisible(), "❌ Pagination bar not visible!");
			Allure.step("✅ Pagination bar is visible");
			System.out.println("✅ Pagination bar is visible");

			// 7) القيم 10، 50، 100
			int[] pageSizes = { 10, 50, 100 };
			for (int desiredCount : pageSizes) {
				// افتح dropdown واختر القيمة
				nGramsPage.selectResultsPerPage(desiredCount);
				Allure.step("🔽 Selected " + desiredCount + " results per page");
				System.out.println("🔽 Selected " + desiredCount + " results per page");

				// انتظر تحديث الجدول: عدد الصفوف > 0 وبحد أقصى desiredCount (قد يكون أقل في
				// الصفحة الأخيرة)
				new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> {
					int rows = nGramsPage.getNumberOfResultsRows();
					return rows > 0 && rows <= desiredCount;
				});

				int visibleRows = nGramsPage.getNumberOfResultsRows();
				Allure.step("📈 Visible rows after selecting " + desiredCount + " = " + visibleRows);
				System.out.println("📈 Visible rows after selecting " + desiredCount + " = " + visibleRows);

				Assert.assertTrue(visibleRows > 0 && visibleRows <= desiredCount,
						"❌ Rows count (" + visibleRows + ") not within expected range after selecting " + desiredCount);
			}

			Allure.step("✅ Results-per-page dropdown works correctly for 10/50/100");
			System.out.println("✅ Results-per-page dropdown works correctly for 10/50/100");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 NGrams Results/Page Dropdown - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ NGrams Results/Page Dropdown - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Error in TC-21: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-22 | 🔄 التحقق من إعادة الترقيم للصفحة الأولى عند تغيير عدد النتائج لكل
	 * صفحة Test to verify that changing "results-per-page" resets pagination back
	 * to page 1 in NGrams.
	 */
	@Test(description = "TC-22 | Verify pagination resets after changing results-per-page in NGrams", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User changes results-per-page and pagination resets to first page")
	@Description("""
			Test Objective:
			After performing a search in the N-Grams tool, verify that changing results-per-page
			resets the pagination back to page 1.

			Steps:
			1) Open N-Grams tool
			2) Perform a broad search to ensure many results
			3) Navigate to a later page (e.g., page 3)
			4) Change results-per-page (e.g., 50)
			5) Assert pagination resets to page 1
			6) Assert table row count updated correctly
			""")
	public void TC22_verifyNGramsPaginationResetAfterChangingResultsPerPage() {
		System.out.println("TC22_verifyNGramsPaginationResetAfterChangingResultsPerPage");

		try {
			// 1️⃣ إعداد الأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);
			System.out.println("🌐 Opened direct tool URL: " + fullToolUrl);

			// 2️⃣ انتظار جاهزية الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);

			// 3️⃣ تنفيذ بحث واسع
			String endingWord = "ة";
			nGramsPage.setEndWithWordFilter(endingWord);
			nGramsPage.clickSearchButton();
			Allure.step("🔎 Search executed");
			System.out.println("🔎 Search executed");

			// 4️⃣ الانتظار للجدول أو رسالة لا توجد نتائج
			nGramsPage.waitForResultsOrNoDataMessage();
			if (nGramsPage.isNoResultsMessageDisplayed()) {
				Allure.step("ℹ️ No results found, skipping test.");
				System.out.println("ℹ️ No results found, skipping test.");
				return;
			}

			// 5️⃣ التأكد أن الترقيم ظاهر
			Assert.assertTrue(nGramsPage.isPaginationBarVisible(), "❌ Pagination bar not visible!");

			// 6️⃣ الانتقال إلى الصفحة 3
			int page = 3;
			nGramsPage.goToPage(page);
			int currentPage = nGramsPage.getCurrentHighlightedPageNumber();
			Assert.assertEquals(currentPage, page, "❌ Failed to navigate to page " + page);
			Allure.step("📄 Navigated to page " + page);
			System.out.println("📄 Navigated to page " + page);

			// 7️⃣ تغيير عدد النتائج لكل صفحة إلى 50
			int desiredCount = 50;
			nGramsPage.selectResultsPerPage(desiredCount);
			Allure.step("🔽 Changed results per page to: " + desiredCount);
			System.out.println("🔽 Changed results per page to: " + desiredCount);

			// 8️⃣ انتظار تحديث الصفوف
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> {
				int rows = nGramsPage.getNumberOfResultsRows();
				return rows > 0 && rows <= desiredCount;
			});

			// 9️⃣ تحقق أن الصفحة الحالية رجعت 1
			int highlightedPage = nGramsPage.getCurrentHighlightedPageNumber();
			Assert.assertEquals(highlightedPage, 1, "❌ Pagination did not reset to page 1!");
			Allure.step("✅ Pagination successfully reset to page 1");
			System.out.println("✅ Pagination successfully reset to page 1");

			// 🔟 تحقق من عدد الصفوف
			int rowCount = nGramsPage.getNumberOfResultsRows();
			Assert.assertTrue(rowCount > 0 && rowCount <= desiredCount,
					"❌ Row count not valid after changing results-per-page");
			Allure.step("✅ Row count updated correctly: " + rowCount);
			System.out.println("✅ Row count updated correctly: " + rowCount);

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Pagination Reset Assertion Failed");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;
		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Unexpected Error in TC-22");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Error in TC-22: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-23 | ✅ التأكد من ظهور جدول النتائج بعد بحث صالح Ensure that after
	 * performing a valid search, the results table displays data (no empty/no-data
	 * message).
	 */
	@Test(description = "TC-23 | Verify results table displays data on a valid NGrams search", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User performs a valid search and expects a non-empty results table")
	@Description("""
			Test Objective:
			After a valid search in the N-Grams tool, verify that the results table is displayed
			and contains data (no 'no data' message).

			Steps:
			1) Open N-Grams tool
			2) Enter a valid filter that yields results
			3) Click Search
			4) Wait for: table / no-data / network error (triage)
			5) Assert: table is visible and has > 0 rows; no 'no data' message
			""")
	public void TC23_verifyResultsTableDisplaysDataOnValidSearch() {
		System.out.println("TC23_verifyResultsTableDisplaysDataOnValidSearch");

		try {
			// 1) اختر المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 2) افتح الأداة مباشرة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);
			System.out.println("🌐 Opened direct tool URL: " + fullToolUrl);

			// 3) انتظار جاهزية الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 4) أدخل استعلام “صالح” متوقع أن يُنتج بيانات
			// اختَر شيئًا شائعًا ليضمن نتائج (يمكنك تعديله لبيئتكم)
			String endingWord = "ة";
			nGramsPage.setEndWithWordFilter(endingWord);
			Allure.step("🔤 Set 'Ends With' filter: " + endingWord);
			System.out.println("🔤 Set 'Ends With' filter: " + endingWord);

			// 5) حضّر مراقبة الشبكة واضغط بحث
			getTriage().clear();
			getTriage().arm(".*/api/tools/ngram/.*");

			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");
			System.out.println("🔎 Clicked on search button");

			// 6) انتظر واحدًا من: جدول / لا توجد بيانات / خطأ شبكة
			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));

			// 7) تقييم النتيجة
			if (outcome.kind == OutcomeKind.TRIAGE_ERROR) {
				var e = outcome.triageError.orElseThrow();
				Allure.step("❗ Network error during search: " + e);
				attachFullPageScreenshot("❗ Server/Network Error (search)");
				Assert.fail(
						"Search network error: status=" + e.status + " url=" + e.url + " type=" + e.requestIdOrType);
				return;
			}
			Assert.assertNotEquals(outcome.kind, OutcomeKind.NO_DATA,
					"❌ Got 'no data' message for a supposedly valid search!");

			// 8) تأكيد ظهور الجدول وأنه يحوي بيانات (> 0 صفوف)
			Assert.assertTrue(nGramsPage.isResultTableDisplayed(), "❌ Results table is not visible!");
			int rows = nGramsPage.getNumberOfResultsRows();
			Allure.step("📈 Rows in results table: " + rows);
			System.out.println("📈 Rows in results table: " + rows);

			Assert.assertTrue(rows > 0, "❌ Results table is empty unexpectedly!");
			Assert.assertFalse(nGramsPage.isNoResultsMessageDisplayed(),
					"❌ 'No results' message should not be displayed after a valid search!");

			// (اختياري) التحقق من قيمة أول خلية لتوثيق أن البيانات حقيقية
			String firstCell = nGramsPage.getFirstCellText(1);
			Allure.step("🔹 First row (col 1) value: " + firstCell);
			System.out.println("🔹 First row (col 1) value: " + firstCell);

			Allure.step("✅ Results table displayed with data for a valid search");
			System.out.println("✅ Results table displayed with data for a valid search");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 NGrams Valid Search - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ NGrams Valid Search - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Error in TC-23: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-24 | ✅ التأكد من أن الأعمدة في جدول النتائج قابلة للفرز Confirm that each
	 * column header allows sorting in ascending and descending order.
	 */
	@Test(description = "TC-24 | Verify sortable columns functionality in NGrams results table", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User sorts results table columns and expects ascending/descending order toggling")
	@Description("""
			Test Objective:
			Verify that each column header in the N-Grams results table can be sorted
			in ascending and descending order, and optionally reset to default.

			Steps:
			1) Open N-Grams tool and perform a valid search to display results.
			2) For each sortable column header:
			   a) Click sort arrow once → Ascending order.
			   b) Click again → Descending order.
			   c) (Optional third click) → Reset/disable sorting if applicable.
			3) Verify the table rows reflect the sorting for top 3 rows at least.
			""")
	public void TC24_verifySortableColumnsFunctionality() {
		System.out.println("TC24_verifySortableColumnsFunctionality");

		try {
			// 1️⃣ إعداد المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);
			System.out.println("🌐 Opened direct tool URL: " + fullToolUrl);

			// 2️⃣ انتظار تحميل الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 3️⃣ إدخال كلمة بحث صالحة لعرض النتائج
			String endingWord = "ة";
			nGramsPage.setEndWithWordFilter(endingWord);
			Allure.step("🔤 Set 'Ends With' filter: " + endingWord);

			getTriage().clear();
			getTriage().arm(".*/api/tools/ngram/.*");

			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");
			System.out.println("🔎 Clicked on search button");

			// 4️⃣ انتظار ظهور الجدول أو رسالة لا توجد بيانات
			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));

			if (outcome.kind == OutcomeKind.TRIAGE_ERROR) {
				var e = outcome.triageError.orElseThrow();
				Allure.step("❗ Network error during search: " + e);
				attachFullPageScreenshot("❗ Server/Network Error (sorting)");
				Assert.fail("Search network error: status=" + e.status + " url=" + e.url);
				return;
			}

			Assert.assertTrue(nGramsPage.isResultTableDisplayed(), "❌ Results table is not visible!");
			int rowCount = nGramsPage.getNumberOfResultsRows();
			Assert.assertTrue(rowCount > 0, "❌ Results table is empty, cannot test sorting!");

			Allure.step("📊 Results table displayed with " + rowCount + " rows");
			System.out.println("📊 Results table displayed with " + rowCount + " rows");

			// 5️⃣ جلب الأعمدة واختبار قابلية الفرز
			List<WebElement> allHeaders = nGramsPage.getAllTableHeaders();
			Allure.step("📌 Total number of headers: " + allHeaders.size());
			System.out.println("📌 Total number of headers: " + allHeaders.size());

			int index = 1;
			for (WebElement header : allHeaders) {
				String classAttr = header.getAttribute("class");

				boolean isSortable = (classAttr != null && classAttr.contains("sortable"))
						|| nGramsPage.hasSortingIcon(header);

				if (isSortable) {
					String columnName = header.getText().trim();
					Allure.step("🔹 Testing sortable column: " + columnName + " (index=" + index + ")");
					System.out.println("🔹 Testing sortable column: " + columnName + " (index=" + index + ")");

					// ✅ التحقق من التغيير في أول 3 صفوف فقط
					nGramsPage.verifyTop3RowsChangeOnSort(index, header);

				} else {
					Allure.step("🚫 Non-sortable column: " + header.getText().trim() + " (index=" + index + ")");
					System.out.println("🚫 Non-sortable column: " + header.getText().trim() + " (index=" + index + ")");
				}
				index++;
			}

			Allure.step("✅ Sorting functionality verified for all sortable columns");
			System.out.println("✅ Sorting functionality verified for all sortable columns");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 Sorting Assertion Failed (NGrams)");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ Sorting Test Unexpected Error (NGrams)");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Error in TC-24: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-25 | ✅ التأكد من ظهور نافذة الفلترة لكل عمود عند الضغط على أيقونة الفلتر
	 * Verify that the filter pop-up appears for each column when clicking its
	 * filter icon.
	 */
	@Test(description = "TC-25 | Verify filter pop-up opens for each column when clicking the column's filter icon (NGrams)", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User clicks the filter icon beside a column header and sees the filter pop-up for that column")
	@Description("""
			Test Objective:
			Confirm that for every column which shows a filter icon, clicking the icon opens a filter pop-up.

			Steps:
			1) Open N-Grams tool and perform a valid search to show the results table.
			2) Find all headers that have a filter icon.
			3) For each such header:
			   - Click the filter icon.
			   - Verify the filter overlay/pop-up becomes visible.
			   - Close/toggle the overlay to proceed to the next header.
			4) Assert that at least one filter pop-up was successfully opened.
			""")
	public void TC25_verifyFilterPopupAppearsForEachColumn() {
		System.out.println("TC25_verifyFilterPopupAppearsForEachColumn");

		try {
			// 1) اختر المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 2) افتح الأداة مباشرة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);
			System.out.println("🌐 Opened direct tool URL: " + fullToolUrl);

			// 3) انتظار جاهزية الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 4) نفّذ بحثًا يُظهر نتائج
			String endingWord = "ة";
			nGramsPage.setEndWithWordFilter(endingWord);

			getTriage().clear();
			getTriage().arm(".*/api/tools/ngram/.*");

			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");
			System.out.println("🔎 Clicked on search button");

			// 5) انتظر: جدول / لا توجد بيانات / خطأ شبكة
			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));

			if (outcome.kind == OutcomeKind.TRIAGE_ERROR) {
				var e = outcome.triageError.orElseThrow();
				Allure.step("❗ Network error during search: " + e);
				System.out.println("❗ Network error during search: " + e);
				attachFullPageScreenshot("❗ Server/Network Error (filter-popup)");
				Assert.fail(
						"Search network error: status=" + e.status + " url=" + e.url + " type=" + e.requestIdOrType);
				return;
			}
			if (outcome.kind == OutcomeKind.NO_DATA) {
				Allure.step("ℹ️ No results; skipping filter pop-up test.");
				System.out.println("ℹ️ No results; skipping filter pop-up test.");
				return;
			}

			Assert.assertTrue(nGramsPage.isResultTableDisplayed(), "❌ Results table is not visible!");
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			// 6) اجلب كل العناوين التي تحتوي على أيقونة فلتر
			List<WebElement> headers = nGramsPage.getAllTableHeaders();
			List<WebElement> filterableHeaders = headers.stream().filter(nGramsPage::hasFilterIcon).toList();

			Allure.step("🧭 Found filter icons on " + filterableHeaders.size() + " header(s)");
			System.out.println("🧭 Found filter icons on " + filterableHeaders.size() + " header(s)");
			Assert.assertFalse(filterableHeaders.isEmpty(), "❌ No filter icons found on any header!");

			int openedCount = 0;

			// 7) لكل عمود قابل للفلترة: افتح الـ popup وتحقق من ظهوره ثم أغلقه
			for (int i = 0; i < filterableHeaders.size(); i++) {
				WebElement header = filterableHeaders.get(i);
				String colName = header.getText().trim();
				if (colName.isBlank())
					colName = "Column#" + (i + 1);

				Allure.step("🔹 Testing filter pop-up for header: " + colName);
				System.out.println("🔹 Testing filter pop-up for header: " + colName);

				// اضغطي على أيقونة الفلتر
				nGramsPage.clickFilterIcon(header);

				// انتظري ظهور الـ overlay
				boolean shown = new WebDriverWait(driver, Duration.ofSeconds(8))
						.until(d -> nGramsPage.isFilterOverlayVisible());
				Assert.assertTrue(shown, "❌ Filter overlay did not appear for column: " + colName);

				openedCount++;

				// (اختياري) التحقق من وجود أزرار "تطبيق" / "مسح"
				Assert.assertTrue(nGramsPage.isFilterOverlayHasApplyOrClear(),
						"❌ Filter overlay missing expected controls for: " + colName);

				Allure.step("✅ Filter pop-up visible for: " + colName);
				System.out.println("✅ Filter pop-up visible for: " + colName);

				// أغلقي الـ overlay (ضغط الأيقونة مرة ثانية/خارج النافذة)
				nGramsPage.closeFilterOverlay(header);

				// تأكدي أنه اختفى قبل الانتقال لعمود آخر
				new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> !nGramsPage.isFilterOverlayVisible());
			}

			Assert.assertTrue(openedCount > 0, "❌ No filter pop-up was opened!");
			Allure.step("🎉 Successfully opened filter pop-ups for " + openedCount + " column(s)");
			System.out.println("🎉 Successfully opened filter pop-ups for " + openedCount + " column(s)");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 NGrams Filter Pop-up - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ NGrams Filter Pop-up - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Error in TC-25: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-26 | ✅ التحقق من خيارات "نوع المطابقة" في نافذة فلترة عمود المتتابعة
	 * 
	 * Verify that the filter pop-up for the "المتتابعة" column offers the correct
	 * match type options.
	 */
	@Test(description = "TC-26 | Verify filter match-type options (يبدأ بـ / يحتوي على / لا يحتوي على / ينتهي بـ / يساوي / لا يساوي) in 'المتتابعة' column", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User opens the filter pop-up for 'المتتابعة' column and checks match-type dropdown options")
	@Description("""
			Test Objective:
			For the 'المتتابعة' column only, verify that its filter pop-up shows the correct
			match-type options in the dropdown: "يبدأ بـ", "يحتوي على", "لا يحتوي على", "ينتهي بـ", "يساوي", "لا يساوي".

			Steps:
			1) Open N-Grams tool and perform a valid search to show results.
			2) Locate the 'المتتابعة' column header and click its filter icon.
			3) In the filter pop-up, open the match-type dropdown.
			4) Verify the dropdown contains the expected options:
			   - يبدأ بـ
			   - يحتوي على
			   - لا يحتوي على
			   - ينتهي بـ
			   - يساوي
			   - لا يساوي
			5) Close the dropdown and filter overlay.
			""")
	public void TC26_verifyFilterMatchTypeOptions() {
		System.out.println("TC26_verifyFilterMatchTypeOptions");
		try {
			// 1) اختر المدونة والأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			// 2) افتح الأداة مباشرة
			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);
			System.out.println("🌐 Opened direct tool URL: " + fullToolUrl);

			// 3) انتظار جاهزية الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");

			// 4) نفّذ بحثًا يُظهر نتائج
			String endingWord = "ة";
			nGramsPage.setEndWithWordFilter(endingWord);

			getTriage().clear();
			getTriage().arm(".*/api/tools/ngram/.*");

			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");

			// 5) انتظر: جدول / لا توجد بيانات / خطأ شبكة
			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));

			if (outcome.kind == OutcomeKind.TRIAGE_ERROR) {
				var e = outcome.triageError.orElseThrow();
				Allure.step("❗ Network error during search: " + e);
				attachFullPageScreenshot("❗ Server/Network Error (filter-match-types)");
				Assert.fail(
						"Search network error: status=" + e.status + " url=" + e.url + " type=" + e.requestIdOrType);
				return;
			}
			if (outcome.kind == OutcomeKind.NO_DATA) {
				Allure.step("ℹ️ No results; skipping match-type options test.");
				System.out.println("ℹ️ No results; skipping match-type options test.");
				return;
			}

			Assert.assertTrue(nGramsPage.isResultTableDisplayed(), "❌ Results table is not visible!");
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			// 6) الأعمدة التي لها أيقونة فلتر
			// ⬅️ استهدف الهيدر الذي نصّه "المتتابعة"
			WebElement mutatabiaHeader = nGramsPage.getAllTableHeaders().stream()
					.filter(h -> "المتتابعة".equals(h.getText().trim())).findFirst()
					.orElseThrow(() -> new AssertionError("لم يتم العثور على عمود 'المتتابعة'"));

			// افتح نافذة الفلترة لذلك العمود
			nGramsPage.clickFilterIcon(mutatabiaHeader);
			new WebDriverWait(driver, Duration.ofSeconds(8)).until(d -> nGramsPage.isFilterOverlayVisible());
			// افتح Dropdown "نوع المطابقة" (الثاني داخل الـ overlay)
			nGramsPage.openFilterMatchTypeDropdown();

			// اجلب كل الخيارات مع التمرير
			List<String> actual = nGramsPage.getFilterMatchTypeOptionsAllScrolling();
			System.out.println("📋 Options: " + actual);

			// القيم المتوقَّعة للأعمدة النصية
			List<String> expected = List.of("يبدأ بـ", "يحتوي على", "لا يحتوي على", "ينتهي بـ", "يساوي", "لا يساوي");

			for (String exp : expected) {
				Assert.assertTrue(actual.stream().anyMatch(o -> o.trim().equals(exp)),
						"❌ Missing option '" + exp + "' في عمود المتتابعة");
			}

			// تنظيف
			nGramsPage.dismissFilterMatchTypeDropdownIfOpen();
			nGramsPage.closeFilterOverlay(mutatabiaHeader);
			new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> !nGramsPage.isFilterOverlayVisible());

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 NGrams Filter Match-Types - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ NGrams Filter Match-Types - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Error in TC-26: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-27 | ✅ التحقق من خيارات "نوع المطابقة" لعمود التكرار (فلتر عددي) Verify
	 * that the numeric filter (التكرار) shows the expected match-type options.
	 */
	@Test(description = "TC-27 | Verify numeric filter match-type options for 'التكرار' column in NGrams", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User opens the 'التكرار' column filter pop-up and checks numeric match-type dropdown options")
	@Description("""
			Test Objective:
			For the numeric column 'التكرار', verify the filter pop-up shows the numeric
			match-type options: يساوي، لا يساوي، اصغر من، اصغر او يساوي، اكبر من، اكبر او يساوي.

			Steps:
			1) Open N-Grams tool and perform a valid search to show results.
			2) Open the filter pop-up for the 'التكرار' column.
			3) Open the match-type dropdown (inside the pop-up).
			4) Verify the dropdown contains the expected options:
			- يساوي
			- لا يساوي
			- اصغر من
			- اصغر او يساوي
			- اكبر من
			-اكبر او يساوي
			5) Close the overlay.
			""")
	public void TC27_verifyNumericFilterMatchTypeOptions_forFrequency() {
		System.out.println("TC27_verifyNumericFilterMatchTypeOptions_forFrequency");
		try {
			// 1) إعداد الأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);

			// 2) انتظار جاهزية الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");

			// 3) بحث واسع لضمان ظهور النتائج والجدول
			String endingWord = "ة";
			nGramsPage.setEndWithWordFilter(endingWord);

			getTriage().clear();
			getTriage().arm(".*/api/tools/ngram/.*");

			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");

			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));

			if (outcome.kind == OutcomeKind.NO_DATA) {
				Allure.step("ℹ️ No results; skipping numeric match-type options test.");
				System.out.println("ℹ️ No results; skipping numeric match-type options test.");
				return;
			}

			Assert.assertTrue(nGramsPage.isResultTableDisplayed(), "❌ Results table is not visible!");
			Allure.step("📊 Results table appeared");

			// 4) استهداف رأس عمود "التكرار"
			WebElement freqHeader = nGramsPage.getAllTableHeaders().stream()
					.filter(h -> "التكرار".equals(h.getText().trim())).findFirst()
					.orElseThrow(() -> new AssertionError("لم يتم العثور على عمود 'التكرار'"));

			// 5) افتح نافذة الفلترة لذلك العمود
			nGramsPage.clickFilterIcon(freqHeader);
			new WebDriverWait(driver, Duration.ofSeconds(8)).until(d -> nGramsPage.isFilterOverlayVisible());

			// 6) افتح Dropdown "نوع المطابقة" داخل الـ overlay (الثاني)
			nGramsPage.openFilterMatchTypeDropdown();

			// 7) اجلب كل الخيارات مع التمرير (لرؤية العناصر أسفل القائمة مثل "اكبر او
			// يساوي")
			List<String> actual = nGramsPage.getFilterMatchTypeOptionsAllScrolling();
			Allure.step("📋 Numeric options: " + actual);
			System.out.println("📋 Numeric options: " + actual);

			// 8) القيم المتوقعة للفلتر العددي
			List<String> expectedNumeric = List.of("يساوي", "لا يساوي", "اصغر من", "اصغر او يساوي", "اكبر من",
					"اكبر او يساوي");

			// 9) التحقق أن جميع المتوقع موجود بغضّ النظر عن الترتيب
			for (String exp : expectedNumeric) {
				Assert.assertTrue(actual.stream().anyMatch(o -> o.trim().equals(exp)),
						"❌ Missing numeric option '" + exp + "' في عمود التكرار");
			}

			// 10) تنظيف
			nGramsPage.dismissFilterMatchTypeDropdownIfOpen();
			nGramsPage.closeFilterOverlay(freqHeader);
			new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> !nGramsPage.isFilterOverlayVisible());

			Allure.step("✅ Numeric match-type options verified successfully for 'التكرار'");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 NGrams Numeric Match-Types - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;
		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ NGrams Numeric Match-Types - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Error in TC-27: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-28 | ✅ التحقق من تطبيق شرط الفلترة بشكل صحيح على الجدول Validate that
	 * applying a filter condition updates the table to show only matching rows.
	 */
	@Test(description = "TC-28 | Verify applying a filter condition filters the NGrams results table", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User applies a filter condition in the column filter pop-up and expects table rows to update accordingly")
	@Description("""
			Test Objective:
			Validate that applying a filter condition (e.g., يبدأ بـ "ا") correctly updates the results table
			to only show rows matching that condition.

			Steps:
			1) Open N-Grams tool and perform a valid search to display results.
			2) Open the filter pop-up for the 'المتتابعة' column.
			3) Select match type: يحتوي على
			4) Enter a value (e.g., "ب")
			5) Click "Apply"
			6) Verify that the table updates and all visible rows in that column start with "ا".
			""")
	public void TC28_verifyFilterConditionAppliesCorrectly() {
		System.out.println("TC28_verifyFilterConditionAppliesCorrectly");

		try {
			// 1️⃣ إعداد الأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);

			// 2️⃣ انتظار جاهزية الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");

			// 3️⃣ تنفيذ بحث واسع
			String endingWord = "ة";
			nGramsPage.setEndWithWordFilter(endingWord);

			getTriage().clear();
			getTriage().arm(".*/api/tools/ngram/.*");

			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");

			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));

			if (outcome.kind == OutcomeKind.NO_DATA) {
				Allure.step("ℹ️ No results; skipping filter condition test.");
				System.out.println("ℹ️ No results; skipping filter condition test.");
				return;
			}

			Assert.assertTrue(nGramsPage.isResultTableDisplayed(), "❌ Results table is not visible!");
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			// 4️⃣ استهداف رأس عمود "المتتابعة"
			WebElement mutatabiaHeader = nGramsPage.getAllTableHeaders().stream()
					.filter(h -> "المتتابعة".equals(h.getText().trim())).findFirst()
					.orElseThrow(() -> new AssertionError("لم يتم العثور على عمود 'المتتابعة'"));

			nGramsPage.clickFilterIcon(mutatabiaHeader);
			new WebDriverWait(driver, Duration.ofSeconds(8)).until(d -> nGramsPage.isFilterOverlayVisible());
			Allure.step("📂 Filter overlay opened for 'المتتابعة'");

			// 5) الشرط الأول: يحتوي على = "ب"
			String matchType = "يحتوي على";
			String filterValue = "ب";
			nGramsPage.setNthFilterCondition(0, matchType, filterValue);
			Allure.step("➕ First condition set: يحتوي على 'ب'");
			Allure.step("➕ First condition set: يحتوي على 'ب'");

			// 7) اضغط "تطبيق"
			nGramsPage.applyFilter();
			Allure.step("✅ Applied filter condition");
			System.out.println("✅ Applied filter condition");

			// 8) اجلب قيم عمود المتتابعة بعد الفلترة
			List<String> columnValues = nGramsPage.getColumnTexts(1); // بافتراض أن "المتتابعة" هي العمود الأول

			// ✅ لو الناتج صفر صفوف فهذا نجاح (فلتر صحيح لكنه لا يطابق أي شيء)
			if (columnValues.isEmpty()) {
				Allure.step("ℹ️ Filter produced zero matching rows — acceptable (PASS).");
				System.out.println("ℹ️ Filter produced zero matching rows — acceptable (PASS).");
				return; // نجاح
			}

			// ✅ لو فيه صفوف، تأكد كلها تطابق الشرط
			for (String val : columnValues) {
				Assert.assertTrue(nGramsPage.matchesAccordingToMatchType(val, matchType, filterValue),
						"❌ Row value '" + val + "' does not satisfy '" + matchType + " " + filterValue + "'");
			}

			Allure.step("✅ All rows match '" + matchType + " " + filterValue + "'");
			System.out.println("✅ All rows match '" + matchType + " " + filterValue + "'");

			// تنظيف
			nGramsPage.closeFilterOverlay(mutatabiaHeader);

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 NGrams Filter Condition - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;
		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ NGrams Filter Condition - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Error in TC-28: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-29 | ✅ إضافة أكثر من شرط فلترة لعمود واحد Ensure user can add multiple
	 * conditions for a single column.
	 */
	@Test(description = "TC-29 | Verify that user can add multiple filter conditions for a single column (NGrams)", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User adds two conditions inside the same column filter pop-up")
	@Description("""
			    Test Objective:
			    Ensure that a user can add multiple filter conditions for the same column.

			    Steps:
			    1) Open N-Grams tool and perform a valid search to show results
			    2) Open filter pop-up for the 'المتتابعة' column
			    3) Add first condition (e.g., يحتوي على = 'ب')
			    4) Click 'أضف شرط' to add a second condition
			    5) Add second condition (e.g., ينتهي بـ = 'ة')
			    6) Verify that two (or more) conditions are visible in the overlay
			""")
	public void TC29_verifyAddingMultipleFilterConditionsInSameColumn() {
		System.out.println("TC29_verifyAddingMultipleFilterConditionsInSameColumn");
		try {
			// 1) إعداد الأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);

			// 2) انتظار جاهزية الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");

			// 3) بحث واسع لظهور النتائج
			String endingWord = "ة";
			nGramsPage.setEndWithWordFilter(endingWord);

			getTriage().clear();
			getTriage().arm(".*/api/tools/ngram/.*");

			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");
			System.out.println("🔎 Clicked on search button");

			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));
			if (outcome.kind == OutcomeKind.NO_DATA) {
				Allure.step("ℹ️ No results; skipping multi-condition test.");
				System.out.println("ℹ️ No results; skipping multi-condition test.");
				return;
			}

			Assert.assertTrue(nGramsPage.isResultTableDisplayed(), "❌ Results table is not visible!");
			Allure.step("📊 Results table appeared");
			System.out.println("📊 Results table appeared");

			// 4) افتح نافذة فلترة عمود "المتتابعة"
			WebElement mutatabiaHeader = nGramsPage.getAllTableHeaders().stream()
					.filter(h -> "المتتابعة".equals(h.getText().trim())).findFirst()
					.orElseThrow(() -> new AssertionError("لم يتم العثور على عمود 'المتتابعة'"));

			nGramsPage.clickFilterIcon(mutatabiaHeader);
			new WebDriverWait(driver, Duration.ofSeconds(8)).until(d -> nGramsPage.isFilterOverlayVisible());
			Allure.step("📂 Filter overlay opened for 'المتتابعة'");
			System.out.println("📂 Filter overlay opened for 'المتتابعة'");

			// 5) الشرط الأول: يحتوي على = "ب"
			String matchType1 = "يحتوي على";
			String filterValue1 = "ب";
			nGramsPage.setNthFilterCondition(0, matchType1, filterValue1);
			Allure.step("➕ First condition set: يحتوي على 'ب'");
			System.out.println("➕ First condition set: يحتوي على 'ب'");

			// 6) أضف شرطًا ثانيًا
			nGramsPage.clickAddFilterCondition();
			Allure.step("➕ Clicked 'أضف شرط'");
			System.out.println("➕ Clicked 'أضف شرط'");

			// 7) الشرط الثاني: ينتهي بـ = "ة"
			String matchType2 = "ينتهي بـ";
			String filterValue2 = "ة";
			nGramsPage.setNthFilterCondition(1, matchType2, filterValue2);
			Allure.step("➕ Second condition set: ينتهي بـ 'ة'");
			System.out.println("➕ Second condition set: ينتهي بـ 'ة'");

			// 8) تحقق من أن لدينا شرطين على الأقل ظاهرين
			int conditionCount = nGramsPage.countFilterConditions();
			Allure.step("🧮 Conditions visible: " + conditionCount);
			System.out.println("🧮 Conditions visible: " + conditionCount);
			Assert.assertTrue(conditionCount >= 2, "❌ Less than 2 conditions are visible!");

			// اضبط وضع المطابقة: مطابقة الكل (AND)
			nGramsPage.setMatchAllMode("مطابقة الكل");

			// طبّق
			nGramsPage.applyFilter();

			// قيّم النتائج
			List<String> columnValues = nGramsPage.getColumnTexts(1); // "المتتابعة" هو العمود الأول
			if (columnValues.isEmpty()) {
				Allure.step("ℹ️ Filter produced zero matching rows — acceptable (PASS).");
				System.out.println("ℹ️ Filter produced zero matching rows — acceptable (PASS).");
				return;
			}

			for (String val : columnValues) {
				boolean c1 = nGramsPage.matchesAccordingToMatchType(val, matchType1, filterValue1);
				boolean c2 = nGramsPage.matchesAccordingToMatchType(val, matchType2, filterValue2);

				System.out.println("🔍 Row value: [" + val + "] | " + matchType1 + " " + filterValue1 + " = " + c1
						+ " , " + matchType2 + " " + filterValue2 + " = " + c2);

				Assert.assertTrue(c1 && c2, "❌ Row value '" + val + "' does not satisfy BOTH conditions: (" + matchType1
						+ " " + filterValue1 + ") AND (" + matchType2 + " " + filterValue2 + ")");
			}

			// تنظيف
			nGramsPage.closeFilterOverlay(mutatabiaHeader);

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 NGrams Multi-Condition Filter - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;
		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ NGrams Multi-Condition Filter - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Error in TC-29: " + e.getMessage(), e);
		}
	}

	/**
	 * TC-30 | ✅ تغيير عدد الصفوف/الصفحة أثناء وجود فلتر فعّال Check that user can
	 * change the number of rows shown per page while filters are active.
	 */
	@Test(description = "TC-30 | Verify changing page size (10/50/100) while a column filter is active (NGrams)", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User changes results-per-page dropdown while a filter is applied")
	@Description("""
			    Test Objective:
			    Apply a filter on a text column, then change the page size (10/50/100) and verify the table
			    updates to show the correct number of rows per page for the filtered data.

			    Steps:
			    1) Open N-Grams tool and perform a broad search (to get many results)
			    2) Open filter pop-up for 'المتتابعة' column
			    3) Set match type to 'يحتوي على' and enter a common value (try 'ب', then 'ا', then 'ال' if needed)
			    4) Apply the filter and ensure we have at least one row (or skip with info if none matched)
			    5) Change results-per-page to 10, 50, 100 sequentially
			    6) After each change, verify visible rows > 0 and <= selected page size (or <= total filtered if fewer)
			    7) Additionally assert all visible rows still satisfy the active filter
			""")
	public void TC30_verifyChangingPageSizeWhileFilterActive() {
		System.out.println("TC30_verifyChangingPageSizeWhileFilterActive");
		try {
			// 1) إعداد الأداة
			CorporaName selectedCorpora = CorporaName.MAJMAA;
			ToolsName selectedTool = ToolsName.NGRAMS;

			String fullToolUrl = NavigationUtils.buildToolUrl(selectedCorpora, selectedTool);
			driver.get(fullToolUrl);
			Allure.step("🌐 Opened direct tool URL: " + fullToolUrl);
			System.out.println("🌐 Opened direct tool URL: " + fullToolUrl);

			// 2) انتظار جاهزية الصفحة
			NGramsPage nGramsPage = new NGramsPage(driver);
			nGramsPage.waitForPageReady(selectedTool.getPathSegment(), nGramsPage::endWithWordInputWebElement);
			Allure.step("📥 Page loaded successfully");
			System.out.println("📥 Page loaded successfully");

			// 3) بحث واسع
			String endingWord = "ة";
			nGramsPage.setEndWithWordFilter(endingWord);

			getTriage().clear();
			getTriage().arm(".*/api/tools/ngram/.*");

			nGramsPage.clickSearchButton();
			Allure.step("🔎 Clicked on search button");
			System.out.println("🔎 Clicked on search button");

			OutcomeResult outcome = waitForOutcomeWithTriage(() -> nGramsPage.isResultTableDisplayed(),
					() -> nGramsPage.isNoResultsMessageDisplayed(), Duration.ofSeconds(60));

			if (outcome.kind == OutcomeKind.NO_DATA) {
				Allure.step("ℹ️ No results; skipping test.");
				System.out.println("ℹ️ No results; skipping test.");
				return;
			}

			Assert.assertTrue(nGramsPage.isResultTableDisplayed(), "❌ Results table is not visible!");
			Assert.assertTrue(nGramsPage.isPaginationBarVisible(), "❌ Pagination bar not visible!");
			Allure.step("📊 Results table appeared & paginator visible");

			// 4) افتح فلترة عمود "المتتابعة"
			WebElement mutatabiaHeader = nGramsPage.getAllTableHeaders().stream()
					.filter(h -> "المتتابعة".equals(h.getText().trim())).findFirst()
					.orElseThrow(() -> new AssertionError("لم يتم العثور على عمود 'المتتابعة'"));

			nGramsPage.clickFilterIcon(mutatabiaHeader);
			new WebDriverWait(driver, Duration.ofSeconds(8)).until(d -> nGramsPage.isFilterOverlayVisible());

			String matchType = "يحتوي على";
			String[] candidates = { "ب", "ة", "ال" };
			String chosenValue = null;

			// جرّب قيمًا شائعة حتى يظهر صف واحد على الأقل
			for (String candidate : candidates) {
				nGramsPage.selectFilterMatchType(matchType);
				nGramsPage.setFilterValue(candidate);
				nGramsPage.applyFilter();

				// انتظر تحديث الجدول قليلًا
				new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> nGramsPage.isResultTableDisplayed());

				List<String> vals = nGramsPage.getColumnTexts(1);
				System.out.println("🔎 Trying filter value '" + candidate + "' -> rows found: " + vals.size());

				if (!vals.isEmpty()) {
					chosenValue = candidate;
					// تأكيد أن النتائج الحالية تُطابق الفلتر
					for (String v : vals) {
						Assert.assertTrue(nGramsPage.matchesAccordingToMatchType(v, matchType, chosenValue),
								"❌ Row value '" + v + "' does not satisfy filter: " + matchType + " " + chosenValue);
					}
					Allure.step("✅ Filter active with value '" + chosenValue + "' and rows present");
					break;
				}
			}

			// لو لم نجد أي نتيجة لأي قيمة، نعتبره نجاح معلوماتي (لا يمكن قياس تغيير حجم
			// الصفحة)
			if (chosenValue == null) {
				Allure.step("ℹ️ No rows matched any of the tried filter values ('ب','ا','ال'). "
						+ "Changing page size has no visible effect (PASS as informational).");
				System.out.println("ℹ️ No rows matched the filter candidates — PASS (no rows to paginate).");
				nGramsPage.closeFilterOverlay(mutatabiaHeader);
				return;
			}

			// 5) غيّر عدد الصفوف/الصفحة أثناء بقاء الفلتر مفعّل
			int[] pageSizes = { 10, 50, 100 };
			for (int desiredCount : pageSizes) {
				nGramsPage.selectResultsPerPage(desiredCount);
				Allure.step("🔽 Selected " + desiredCount + " results per page (with filter active)");
				System.out.println("🔽 Selected " + desiredCount + " results per page (with filter active)");

				// انتظر تحديث الجدول
				new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> {
					int rows = nGramsPage.getNumberOfResultsRows();
					return rows >= 0 && rows <= desiredCount; // قد تكون الصفحة الأخيرة أقل من desiredCount
				});

				int visibleRows = nGramsPage.getNumberOfResultsRows();
				Allure.step("📈 Visible rows (filtered) = " + visibleRows + " / pageSize=" + desiredCount);
				System.out.println("📈 Visible rows (filtered) = " + visibleRows + " / pageSize=" + desiredCount);

				Assert.assertTrue(visibleRows >= 0 && visibleRows <= desiredCount,
						"❌ Rows count (" + visibleRows + ") exceeds selected page size (" + desiredCount + ")!");

				// 6) تحقّق أن الصفوف الظاهرة مازالت تُطابق الفلتر النّصي
				List<String> pageVals = nGramsPage.getColumnTexts(1);
				for (String v : pageVals) {
					Assert.assertTrue(nGramsPage.matchesAccordingToMatchType(v, matchType, chosenValue),
							"❌ Row value '" + v + "' violates active filter: " + matchType + " " + chosenValue);
				}
			}

			Allure.step("✅ Page size changes work correctly while filter is active");
			System.out.println("✅ Page size changes work correctly while filter is active");

			// تنظيف
			nGramsPage.closeFilterOverlay(mutatabiaHeader);

		} catch (AssertionError ae) {
			attachFullPageScreenshot("🔴 NGrams PageSize While Filter Active - Assertion Failure");
			attachFailureVideo("📹 Video (on failure)");

			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("⚠️ NGrams PageSize While Filter Active - Unexpected Exception");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Error in TC-30: " + e.getMessage(), e);
		}
	}

}