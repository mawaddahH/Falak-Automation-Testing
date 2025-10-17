package com.falak.qa.tests;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.Assert;
import com.falak.qa.base.BaseTest;
import com.falak.qa.config.EnvironmentConfigLoader;

import io.qameta.allure.*;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import com.falak.qa.config.RetryAnalyzer;
import com.falak.qa.pages.home.CorporaOfMonthCardComponent;
import com.falak.qa.pages.home.FeatureCardComponent;
import com.falak.qa.pages.home.HomePageCorporaCardComponent;
import com.falak.qa.pages.home.ToolDetailsModal;
import com.falak.qa.pages.home.ToolsCardComponent;

/**
 * HomePageTC
 * 
 * This test class verifies that the top navigation buttons on the homepage
 * correctly redirect the user to their respective URLs.
 * 
 * يقوم هذا الكلاس باختبار أزرار التنقل في الصفحة الرئيسية، والتأكد من أنها تنقل
 * المستخدم للروابط الصحيحة.
 */
@Epic("Home Page")
@Feature("ALL")
@Listeners({ com.falak.qa.listeners.RetryListener.class })
public class HomePageTC extends BaseTest {

	@Test(description = "TC-01 | Verify 'الرئيسية' button redirects correctly", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User clicks on 'الرئيسية' in the top nav")
	public void TC01_testHomeButtonRedirect() {
		System.out.println("TC01_testHomeButtonRedirect");
		runNavTest("الرئيسية", () -> homePage.clickHomePageHeaderButton(), EnvironmentConfigLoader.getUrl("baseUrl"));
	}

	@Test(description = "TC-02 | Verify 'عن فلك' button redirects correctly", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User clicks on 'عن فلك' in the top nav")
	public void TC02_testAboutFalakButtonRedirect() {
		System.out.println("TC02_testAboutFalakButtonRedirect");
		runNavTest("عن فلك", () -> homePage.clickAboutFalakHeaderButton(),
				EnvironmentConfigLoader.getUrl("aboutUsUrl"));
	}

	@Test(description = "TC-03 | Verify 'المدونات' button redirects correctly", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User clicks on 'المدونات' in the top nav")
	public void TC03_testCorporaButtonRedirect() {
		System.out.println("TC03_testCorporaButtonRedirect");
		runNavTest("المدونات", () -> homePage.clickCorporaHeaderButton(), EnvironmentConfigLoader.getUrl("corporaUrl"));
	}

	@Test(description = "TC-04 | Verify 'الجدارية الصوتية' button redirects correctly", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User clicks on 'الجدارية الصوتية' in the top nav")
	public void TC04_testVoicewallButtonRedirect() {
		System.out.println("TC04_testVoicewallButtonRedirect");
		runNavTest("الجدارية الصوتية", () -> homePage.clickVoiceWallHeaderButton(),
				EnvironmentConfigLoader.getUrl("voiceWallUrl"));
	}

	@Test(description = "TC-05 | Verify 'قوائم الشيوع' button redirects correctly", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User clicks on 'قوائم الشيوع' in the top nav")
	public void TC05_testFrequencyListsButtonRedirect() {
		System.out.println("TC05_testFrequencyListsButtonRedirect");
		runNavTest("قوائم الشيوع", () -> homePage.clickFrequencyListsHeaderButton(),
				EnvironmentConfigLoader.getUrl("frequencyListsUrl"));
	}

	@Test(description = "TC-06 | Verify 'المطورون' button redirects correctly", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User clicks on 'المطورون' in the top nav")
	public void TC06_testDevelopersButtonRedirect() {
		System.out.println("TC06_testDevelopersButtonRedirect");
		runNavTest("المطورون", () -> homePage.clickDevelopersHeaderButton(),
				EnvironmentConfigLoader.getUrl("developersUrl"));
	}

	@Test(description = "TC-07 | Verify 'اتصل بنا' button redirects correctly", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User clicks on 'اتصل بنا' in the top nav")
	public void TC07_testContactUsButtonRedirect() {
		System.out.println("TC07_testContactUsButtonRedirect");
		runNavTest("اتصل بنا", () -> homePage.clickContactUsHeaderButton(),
				EnvironmentConfigLoader.getUrl("contactUsUrl"));
	}

	@Test(description = "TC-08 | Verify that clicking the 'المدونات' button in the main content redirects the user to the 'المدونات' page of the website.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User clicks on 'المدونات' Card")
	public void TC08_testCorporaCard() {
		System.out.println("TC08_testCorporaCard");
		runNavTest("المدونات", () -> homePage.clickCorporaCard(), EnvironmentConfigLoader.getUrl("corporaUrl"));
	}

	@Test(description = "TC-09 | Verify that clicking the 'الأدوات' button scrolls the page to the 'الأدوات' section on the current page.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User clicks on 'الأدوات' Card")
	public void TC09_testToolsCard() {
		System.out.println("TC09_testToolsCard");
		runSectionTest("أدوات المنصة", () -> homePage.clickToolsCard(), () -> homePage.isToolsSectionVisible());

	}

	@Test(description = "TC-10 | Verify that clicking the 'المزايا' button scrolls the page to the 'المزايا' section on the current page.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User clicks on 'المزايا' Card")
	public void TC10_testFeatureCard() {
		System.out.println("TC10_testFeatureCard");
		runSectionTest("أدوات المنصة", () -> homePage.clickFeaturesCard(), () -> homePage.isFeaturesSectionVisible());

	}

	@Test(description = "TC-11 | Verify that clicking the 'أضف مدونتك' button redirects the user to the page for adding a new مدونة.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User clicks on 'أضف مدونتك' Card")
	public void TC11_testaddCorporaCard() {
		System.out.println("TC11_testaddCorporaCard");
		runNavTest("أضف مدونتك", () -> homePage.clickAddCorporaCards(), EnvironmentConfigLoader.getUrl("addCorpusUrl"));
	}

	@Test(description = "TC-12 | Verify that clicking the 'استخدم منصة فلك والتسجيل' section redirects the user to the 'لقاء تعريفي بمنصَّة فلك' registration page where the user can submit a registration request.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User clicks on 'التسجيل للقاء تعريفي بمنصَّة فلك' section")
	public void TC12_testRegistrationSection() {
		System.out.println("TC12_testRegistrationSection");
		runNavTest("التسجيل للقاء تعريفي بمنصَّة فلك", () -> homePage.clickRegistrationSection(),
				EnvironmentConfigLoader.getUrl("trainingUrl"));
	}

	@Test(description = "TC-13 | Verify that the 'المدونات' section is displayed correctly on the home page with 1 card at least", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User sees 'المدونات' section on the home page")
	public void TC13_testNumberOfCorporaContentCard() {
		System.out.println("TC13_testNumberOfCorporaContentCard");

		Allure.description(
				"""
						Test Objective: Verify that the 'المدونات' section on the home page displays exactly 1 at least Corpora cards.
						Steps:
						1) Navigate to the base URL (handled in BaseTest)
						2) Scroll to the 'المدونات' section.
						3) Count the number of Corpora Content cards displayed.
						Expected Result: The section should contain exactly 1 card at least.
						""");

		try {
			// Scroll down (في حال كان القسم بعيد عن أول الصفحة)
			((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 600)");

			// Step: Count the cards
			int actualCount = homePage.getNumberOfCorporaContentCard();
			Allure.step("🧮 عدد بطاقات المدونات الظاهرة: " + actualCount);

			// Step: Assert the count
			Assert.assertTrue(actualCount >= 1 && actualCount <= 3,
					"❌ عدد البطاقات في قسم المدونات يجب أن يكون بين 1 و3. العدد الحالي: " + actualCount);

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Error - Corpora Content Cards Count");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;
		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Error - Corpora Content Cards Count");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("Exception in test case: " + e.getMessage());
		}
	}

	@Test(description = "TC-14 | Verify that each مدونة card displays the correct elements (Title, Description, عدد نصوص, عدد كلمات, عدد كلمات بدون تكرار, اختر المدونة ).", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User sees on 'المدونات' card destails on the home page")
	public void TC14_verifyEachCorporaCard_HasCompleteData() {
		System.out.println("TC14_verifyEachCorporaCard_HasCompleteData");
		Allure.description("""
				Test Objective: Verify that each مدونة card displays the correct elements.
				Steps:
				1) Navigate to the website URL (handled in BaseTest).
				2) Scroll to the "المدونات" section.
				3) Verify the following on each card:
				✦ Title is displayed.
				✦ Description is displayed.
				✦ "عدد نصوص" label is displayed + number.
				✦ "عدد كلمات" label is displayed + number.
				✦ "عدد كلمات بدون تكرار" label is displayed + number.
				✦ "اختر المدونة" button is displayed.
				Expected Result: Each card should contain all required elements.
				""");

		try {
			// Step 1: Scroll to section
			((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 600)");
			Allure.step("🔽 Scroll to 'المدونات' section");

			// Step 2: Get all cards
			List<HomePageCorporaCardComponent> cards = homePage.getAllCorporaCards();
			Allure.step("📦 Found " + cards.size() + " cards");

			// Step 3: Validate each element of each card
			for (int i = 0; i < cards.size(); i++) {
				HomePageCorporaCardComponent card = cards.get(i);
				int cardNumber = i + 1;

				Allure.step("🧪 Checking card #" + cardNumber, () -> {
					Assert.assertTrue(card.isTitleVisible(), "❌ العنوان غير ظاهر في البطاقة رقم " + cardNumber);
					Assert.assertTrue(card.isDescriptionVisible(), "❌ الوصف غير ظاهر في البطاقة رقم " + cardNumber);
					Allure.step("	📘 Title: " + card.getTitle() + "	📘 Description: " + card.getDescription());

					// 🔗 رابط «المزيد»
					Assert.assertTrue(card.isMoreLinkVisible(),
							"❌ رابط «المزيد» غير ظاهر في البطاقة رقم " + cardNumber);
					Allure.step(
							"🔗 رابط المزيد | نص: " + card.getMoreLinkText() + " | href: " + card.getMoreLinkHref());

					// 🔢 عدد النصوص
					Assert.assertTrue(card.isTextsIconVisible(),
							"❌ أيقونة عدد النصوص غير ظاهرة في البطاقة رقم " + cardNumber);
					Assert.assertTrue(card.isTextsLabelVisible(),
							"❌ عنوان عدد النصوص غير ظاهر في البطاقة رقم " + cardNumber);
					Assert.assertTrue(card.isTextsValueVisible(),
							"❌ قيمة عدد النصوص غير ظاهرة في البطاقة رقم " + cardNumber);
					Allure.step("📘 عدد النصوص | Label: " + card.getTextsLabelText() + " | Value: "
							+ card.getTextsValueText() + " | Icon: " + card.getTextsIconSrc());

					// 🔤 عدد الكلمات
					Assert.assertTrue(card.isWordsIconVisible(),
							"❌ أيقونة عدد الكلمات غير ظاهرة في البطاقة رقم " + cardNumber);
					Assert.assertTrue(card.isWordsLabelVisible(),
							"❌ عنوان عدد الكلمات غير ظاهر في البطاقة رقم " + cardNumber);
					Assert.assertTrue(card.isWordsValueVisible(),
							"❌ قيمة عدد الكلمات غير ظاهرة في البطاقة رقم " + cardNumber);
					Allure.step("📗 عدد الكلمات | Label: " + card.getWordsLabelText() + " | Value: "
							+ card.getWordsValueText() + " | Icon: " + card.getWordsIconSrc());

					// 🆔 عدد الكلمات بدون تكرار
					Assert.assertTrue(card.isUniqueIconVisible(),
							"❌ أيقونة عدد الكلمات بدون تكرار غير ظاهرة في البطاقة رقم " + cardNumber);
					Assert.assertTrue(card.isUniqueLabelVisible(),
							"❌ عنوان عدد الكلمات بدون تكرار غير ظاهر في البطاقة رقم " + cardNumber);
					Assert.assertTrue(card.isUniqueValueVisible(),
							"❌ قيمة عدد الكلمات بدون تكرار غير ظاهرة في البطاقة رقم " + cardNumber);
					Allure.step("📕 عدد الكلمات بدون تكرار | Label: " + card.getUniqueLabelText() + " | Value: "
							+ card.getUniqueValueText() + " | Icon: " + card.getUniqueIconSrc());

					// 🟩 زر اختر المدونة
					Assert.assertTrue(card.isSelectButtonVisible(),
							"❌ زر 'اختر المدونة' غير ظاهر في البطاقة رقم " + cardNumber);
					Allure.step("🎯 زر اختر المدونة | href: " + card.getSelectButtonHref());

					// ✅ التحقق النهائي باستخدام isValidCard()
					Assert.assertTrue(card.isValidCard(),
							"❌ البطاقة رقم " + cardNumber + " غير مكتملة بالرغم من تحقق العناصر بشكل فردي.");
					Allure.step("✅ البطاقة رقم " + cardNumber + " مكتملة البيانات (Valid Card)");
				});
			}

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - Card Details Missing");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - Card Details Test");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("⚠️ Unexpected error: " + e.getMessage());
		}
	}

	@Test(description = "TC-15 | Verify that a hover effect (corner shading) is applied when the mouse pointer hovers over any مدونة card.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User moves the pointer over every ‘مدونة’ card (section: أفضل المدونات)")
	public void TC15_verifyHoverEffectOnAllCorporaCards() {
		System.out.println("TC15_verifyHoverEffectOnAllCorporaCards");
		Allure.description(
				"""
						Test Objective: Each corpora-card (in ‘أفضل المدونات’) should change its
						box-shadow on hover.
						Per-card Steps:
						1) Scroll once to the section.
						2) Read box-shadow BEFORE hover.
						3) Hover.
						4) Read AFTER hover.
						5) Assert values differ.
						Expected Result: When the mouse pointer hovers over a مدونة card, the card should display a shaded visual effect from the corners to indicate hover state.
						""");

		try {
			// ✅ 1) انتظار ذكي لظهور القسم والبطاقات
			homePage.waitForCorporaSectionReady(Duration.ofSeconds(15), /* minCards */ 1);
			Allure.step("🔎 Corpora section is visible & scrolled into view");

			/* 🔽 1. الانتقال للقسم مرة واحدة -------------------------------- */
			((JavascriptExecutor) driver).executeScript("window.scrollBy(0,600)");
			Allure.step("🔽 Scrolled to ‘المدونات’ section");
			System.out.println("🔽 Scrolled to ‘المدونات’ section");

			/* 📦 2. جلب البطاقات كمكوّنات ----------------------------------- */
			List<HomePageCorporaCardComponent> cards = homePage.getAllCorporaItemDetailComponents(); // الميثود الجديد
			Allure.step("📦 Found " + cards.size() + " cards for hover test");
			System.out.println("📦 Found " + cards.size() + " cards for hover test");
			Assert.assertTrue(cards.size() > 0, "❌ No corpora cards found!");

			Actions actions = new Actions(driver);

			/* 🔄 3. اللوب على كل بطاقة -------------------------------------- */
			for (int i = 0; i < cards.size(); i++) {
				final int cardNo = i + 1;
				HomePageCorporaCardComponent card = cards.get(i);

				Allure.step("🧪 Hover test for card #" + cardNo, () -> {
					// 🎨 قراءة الـ box-shadow قبل التحويم
					String before = card.getBoxShadow();
					Allure.step("🎨 box-shadow BEFORE: " + before);
					System.out.println("🎨 box-shadow BEFORE: " + before);

					// 🖱️ تنفيذ التحويم
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});",
							card.getCardRoot());
					actions.moveToElement(card.getCardRoot()).perform();
					Allure.step("🖱 Pointer hovered on card #" + cardNo);
					System.out.println("🖱 Pointer hovered on card #" + cardNo);

					// ⏳ انتظار حتى تتغير قيمة الـ box-shadow
					new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> {
						String afterShadow = card.getBoxShadow();
						return !afterShadow.equals(before);
					});

					// 🎨 قراءة بعد التحويم
					String after = card.getBoxShadow();
					Allure.step("🎨 box-shadow AFTER : " + after);
					System.out.println("🎨 box-shadow AFTER : " + after);

					// ✅ التحقق من وجود التأثير
					Assert.assertNotEquals(after, before, "❌ لم يظهر تأثير الظل عند التحويم على البطاقة #" + cardNo);

					// 🧭 إزالة المؤشر حتى لا يؤثر على البطاقة التالية
					actions.moveByOffset(-60, -60).perform();
				});
			}

			Allure.step("✅ جميع البطاقات أظهرت تأثير الظل عند التحويم بنجاح");
			Allure.step("✅ جميع البطاقات أظهرت تأثير الظل عند التحويم بنجاح");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - Hover Cards");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - Hover Cards");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("⚠️ Hover effect loop failed: " + e.getMessage());
		}
	}

	@Test(description = "TC-16-Loop | Verify that clicking the 'اختر المدونة' button on a مدونة card redirects the user to the corresponding مدونة details page.")
	@Severity(SeverityLevel.NORMAL)
	@Story("User clicks 'اختر المدونة' on every corpora card, one-by-one")
	public void TC16_verifySelectButtonsRedirect_AllCards() {
		System.out.println("TC16_verifySelectButtonsRedirect_AllCards");

		Allure.description(
				"""
						Test Objective: Make sure **every** 'اختر المدونة' button navigates to the correct details page.
						Per-card steps :
						1) Scroll to 'المدونات'.
						2) Read the button's href + card title.
						3) Click the button.
						4) Wait until the new URL contains that href.
						5) Verify a header appears and includes the same title.
						6) Navigate back to home and repeat.
						Expected Result: Clicking "اختر المدونة" button should redirect the user to the details page of the corresponding مدونة. The page content should load correctly.
						""");

		try {
			/* 🔽 (1) scroll once to section ---------------------------------- */
			((JavascriptExecutor) driver).executeScript("window.scrollBy(0,600)");
			Allure.step("🔽 Scrolled to 'المدونات' section");

			/* 📦 (2) fetch all cards ----------------------------------------- */
			List<HomePageCorporaCardComponent> cards = homePage.getAllCorporaCards();
			Allure.step("📦 Found " + cards.size() + " cards");

			for (int i = 0; i < cards.size(); i++) {

				final int index = i;
				final int cardNumber = i + 1;

				Allure.step("🧪 Checking redirect for card #" + cardNumber, () -> {

					/* —— ❶ re-grab card after each back ———————————— */
					HomePageCorporaCardComponent card = homePage.getAllCorporaCards().get(index);

					Assert.assertTrue(card.isSelectButtonVisible(),
							"❌ زر 'اختر المدونة' غير ظاهر في البطاقة رقم " + cardNumber);

					String expectedHref = card.getSelectButtonHref(); // ex: /corpora/{id}
					String expectedTitle = card.getTitle(); // العنوان داخل البطاقة
					Allure.step("🔗 Href: " + expectedHref);
					Allure.step("📘 Title: " + expectedTitle);

					/* —— ❷ click button ———————————————————————— */
					card.clickSelectButton();
					Allure.step("🖱️ Clicked 'اختر المدونة' (card #" + cardNumber + ")");

					/* —— ❸ wait for navigation ———————————————— */
					new WebDriverWait(driver, Duration.ofSeconds(10))
							.until(d -> d.getCurrentUrl().contains(expectedHref));

					String actualUrl = driver.getCurrentUrl();
					Allure.step("🌍 Landed on: " + actualUrl);

					Assert.assertTrue(actualUrl.contains(expectedHref),
							"❌ لم تتم إعادة التوجيه الصحيحة للبطاقة رقم " + cardNumber);

					/* —— ❹ التحقق من عنوان صفحة التفاصيل ————————————— */
					By headerLocator = By
							.cssSelector("nav.p-breadcrumb ol.p-breadcrumb-list li:last-child span.p-menuitem-text");

					String detailsHeaderText = new WebDriverWait(driver, Duration.ofSeconds(10))
							.until(d -> d.findElement(headerLocator)).getText().trim();

					Allure.step("📖 تفاصيل الصفحة | Header text: " + detailsHeaderText);

					Assert.assertTrue(detailsHeaderText.contains(expectedTitle),
							"❌ عنوان صفحة التفاصيل لا يحتوي على عنوان البطاقة (" + expectedTitle + ")");

					/* —— ❺ back to home & scroll again ————————— */
					driver.navigate().back();
					new WebDriverWait(driver, Duration.ofSeconds(10))
							.until(d -> d.getCurrentUrl().equals(homePage.getBaseUrl()));

					// أعد التمرير لأن الصفحة عادت لأعلى
					((JavascriptExecutor) driver).executeScript("window.scrollBy(0,600)");
				});
			}

			Allure.step("🎉 جميع أزرار 'اختر المدونة' تعمل وتصل إلى الصفحات الصحيحة");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - Select Buttons Loop");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - Select Buttons Loop");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("⚠️ Unexpected error: " + e.getMessage());
		}
	}

	@Test(description = "TC-17-Loop | Verify that clicking the 'المزيد' button redirects the user to the full 'المدونات' page.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User clicks 'المزيد' on every corpora card, one-by-one")
	public void TC17_verifyMoreLinksRedirect_AllCards() {
		System.out.println("TC17_verifyMoreLinksRedirect_AllCards");

		Allure.description(
				"""
						Test Objective: For **every** مدونة card, clicking the 'المزيد' link within
						the description should navigate to the correct corpora-details page.
						Per-card steps:
						1) Scroll to 'المدونات' section (once at the start).
						2) Read the link's href + card title.
						3) Click the 'المزيد' link.
						4) Wait until the new URL contains that href.
						5) (Optional) Verify a page header appears and includes the same title.
						6) Navigate back to home, scroll again, and repeat for next card.
						Expected Result: Clicking "المزيد" button should redirect the user to the "المدونات" page. The page content should load correctly.
						""");

		try {
			/* 🔽 (1) scroll once to section ---------------------------------- */
			((JavascriptExecutor) driver).executeScript("window.scrollBy(0,800)");
			Allure.step("🔽 Scrolled to 'المدونات' section");

			/* 📦 (2) fetch all cards count ------------------------------------ */
			int totalCards = homePage.getAllCorporaCards().size();
			Allure.step("📦 Found " + totalCards + " cards");
			System.out.println("📦 Found " + totalCards + " cards");

			for (int i = 0; i < totalCards; i++) {
				System.out.println("I'm inside For Loop " + totalCards);

				final int idx = i; // ثابت لاستخدامه داخل الـ lambda
				final int cardNumber = i + 1;

				Allure.step("🧪 Verifying 'المزيد' link for card #" + cardNumber, () -> {
					System.out.println("I'm inside Allure.step " + totalCards);

					/* —— ❶ إعادة جلب البطاقة ——————— */
					HomePageCorporaCardComponent card = homePage.getAllCorporaCards().get(idx);

					Assert.assertTrue(card.isMoreLinkVisible(),
							"❌ رابط 'المزيد' غير ظاهر في البطاقة رقم " + cardNumber);

					String expectedHref = card.getMoreLinkHref(); // مثال: /corpora/{id}
					String expectedTitle = card.getTitle(); // عنوان البطاقة
					Allure.step("🔗 href: " + expectedHref);
					Allure.step("📘 Title: " + expectedTitle);
					System.out.println("🔗 href: " + expectedHref);
					System.out.println("📘 Title: " + expectedTitle);

					/* —— ❷ Click the link ——————— */
					card.clickMoreLink();
					Allure.step("🖱️ Clicked 'المزيد' (card #" + cardNumber + ")");
					System.out.println("🖱️ Clicked 'المزيد' (card #" + cardNumber + ")");

					/* —— ❸ wait for navigation ———————————————— */
					new WebDriverWait(driver, Duration.ofSeconds(10))
							.until(d -> d.getCurrentUrl().contains(expectedHref));

					String actualUrl = driver.getCurrentUrl();
					Allure.step("🌍 Landed on: " + actualUrl);
					System.out.println("🌍 Landed on: " + actualUrl);

					Assert.assertTrue(actualUrl.contains(expectedHref),
							"❌ لم تتم إعادة التوجيه الصحيحة للبطاقة رقم " + cardNumber);

					/* —— ❹ التحقق من عنوان صفحة التفاصيل ————————————— */
					By headerLocator = By
							.cssSelector("nav.p-breadcrumb ol.p-breadcrumb-list li:last-child span.p-menuitem-text");

					String detailsHeaderText = new WebDriverWait(driver, Duration.ofSeconds(10))
							.until(d -> d.findElement(headerLocator)).getText().trim();

					Allure.step("📖 تفاصيل الصفحة | Header text: " + detailsHeaderText);

					Assert.assertTrue(detailsHeaderText.contains(expectedTitle),
							"❌ عنوان صفحة التفاصيل لا يحتوي على عنوان البطاقة (" + expectedTitle + ")");

					/* —— ❺ العودة للصفحة الرئيسة واستئناف اللوب —— */
					driver.navigate().back();
					new WebDriverWait(driver, Duration.ofSeconds(10))
							.until(d -> d.getCurrentUrl().equals(homePage.getBaseUrl()));

					// إعادة التمرير لأن الصفحة عادت لأعلى
					((JavascriptExecutor) driver).executeScript("window.scrollBy(0,600)");
				});
			}

			Allure.step("🎉 جميع روابط 'المزيد' تعمل وتصل إلى الصفحات الصحيحة");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - More Links Loop");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - More Links Loop");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("⚠️ Unexpected error: " + e.getMessage());
		}
	}

	@Test(description = "TC-18 | Verify that clicking the 'المزيد button redirects the user to the full 'المدونات' page.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User clicks on 'المزيد' Button to go to the Fuall 'المدونات' Pages")
	public void TC18_testMoreCorporaButton() {
		System.out.println("TC18_testMoreCorporaButton");
		runNavTest("المزيد", () -> homePage.clickMoreCorporaButton(), EnvironmentConfigLoader.getUrl("corporaUrl"));
	}

	@Test(description = "TC-19 | Verify that the 'مدونة الشهر' section is displayed correctly on the home page with one مدونة card.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User sees the 'مدونة الشهر' section on the home page")
	public void TC19_verifyCorporaOfMonthSection() {
		System.out.println("TC19_verifyCorporaOfMonthSection");
		Allure.description(
				"""
						Test Objective: Confirm that the 'مدونة الشهر' section is displayed and contains exactly **one** Corpora card.
						Steps:
						1) Navigate to the base URL   (done in BaseTest).
						2) Scroll to 'مدونة الشهر' section.
						3) Assert the section is visible.
						4) Assert that it holds one (and only one) card.
						Expected Result: The "مدونة الشهر" section should be visible and display exactly one مدونة card.
						""");

		try {
			/* —— ❶ Scroll حتى يصبح القسم داخل الـ viewport ——————— */
			/* 🔽 scroll once to Corpora-of-Month */
			homePage.scrollToCorporaOfMonth();
			Allure.step("🔽 Scrolled & waited until 'مدونة الشهر' section is visible");

			/* —— ❷ Assertions ———————————————————————————————— */
			Assert.assertTrue(homePage.isCorporaOfMonthSectionVisible(), "❌ قسم «مدونة الشهر» غير ظاهر على الصفحة!");

			int cardCount = homePage.getCorporaOfMonthCardsCount();
			Allure.step("📦 Corpora-of-the-Month cards found: " + cardCount);

			Assert.assertEquals(cardCount, 1, "❌ يجب أن يحتوي قسم «مدونة الشهر» على بطاقة واحدة بالضبط");

			Allure.step("✅ تم التحقق: القسم ظاهر ويحتوي بطاقة واحدة فقط");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - CorporaOfMonth");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - CorporaOfMonth");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("⚠️ Unexpected error: " + e.getMessage());
		}
	}

	@Test(description = "TC-20 | Verify that the 'مدونة الشهر' card displays the correct elements (Title, Description, عدد نصوص, عدد كلمات, عدد كلمات بدون تكرار, عرض button).", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User checks the 'مدونة الشهر' card on the home page")
	public void TC20_verifyCorporaOfMonthCard_HasCompleteData() {
		System.out.println("TC20_verifyCorporaOfMonthCard_HasCompleteData");
		Allure.description(
				"""
						Test Objective: Verify that the **single** 'مدونة الشهر' card displays all required elements:
						✦ Title
						✦ Description
						✦ عدد نصوص (icon + label + number)
						✦ عدد كلمات (icon + label + number)
						✦ عدد كلمات بدون تكرار (icon + label + number)
						✦'عرض' button
						Steps:
						1) Navigate to home page   (done in BaseTest).
						2) Scroll to the 'مدونة الشهر' section.
						3) Assert visibility / values of every element listed above.
						Expected Result: The "مدونة الشهر" card should display all required elements (Title, Description, عدد نصوص with number, عدد كلمات with number, عدد كلمات بدون تكرار with number, and عرض button).
						""");

		try {
			/* ❶ Scroll to section & fetch the card --------------------------- */
			homePage.scrollToCorporaOfMonth(); // (يستعمل BasePage.scrollToElement)
			Allure.step("🔽 Scrolled & waited until 'مدونة الشهر' section is visible");

			CorporaOfMonthCardComponent monthCard = homePage.getCorporaOfMonthCard();
			Allure.step("📦 Retrieved Corpora-of-Month card component");

			/* ❷ Assertions & logging ---------------------------------------- */
			Allure.step("🧪 Validating Corpora-of-Month card", () -> {

				// Title & description
				Assert.assertTrue(monthCard.isTitleVisible(), "❌ العنوان غير ظاهر");
				Assert.assertTrue(monthCard.isDescriptionVisible(), "❌ الوصف غير ظاهر");
				Allure.step("📘 Title: " + monthCard.getTitle());
				Allure.step("📄 Description: " + monthCard.getDescription());

				// عدد النصوص
				Assert.assertTrue(monthCard.isTextsIconVisible(), "❌ أيقونة عدد النصوص غير ظاهرة");
				Assert.assertTrue(monthCard.isTextsLabelVisible(), "❌ عنوان عدد النصوص غير ظاهر");
				Assert.assertTrue(monthCard.isTextsValueVisible(), "❌ رقم عدد النصوص غير ظاهر");
				Allure.step("🔢 عدد النصوص: " + monthCard.getTextsValueText());

				// عدد الكلمات
				Assert.assertTrue(monthCard.isWordsIconVisible(), "❌ أيقونة عدد الكلمات غير ظاهرة");
				Assert.assertTrue(monthCard.isWordsLabelVisible(), "❌ عنوان عدد الكلمات غير ظاهر");
				Assert.assertTrue(monthCard.isWordsValueVisible(), "❌ رقم عدد الكلمات غير ظاهر");
				Allure.step("🔤 عدد الكلمات: " + monthCard.getWordsValueText());

				// عدد الكلمات بدون تكرار
				Assert.assertTrue(monthCard.isUniqueIconVisible(), "❌ أيقونة الكلمات بدون تكرار غير ظاهرة");
				Assert.assertTrue(monthCard.isUniqueLabelVisible(), "❌ عنوان الكلمات بدون تكرار غير ظاهر");
				Assert.assertTrue(monthCard.isUniqueValueVisible(), "❌ رقم الكلمات بدون تكرار غير ظاهر");
				Allure.step("🆔 الكلمات بدون تكرار: " + monthCard.getUniqueValueText());

				// زر «عرض»
				Assert.assertTrue(monthCard.isViewButtonVisible(), "❌ زر «عرض» غير ظاهر");
				Allure.step("🎯 زر «عرض» | href: " + monthCard.getViewButtonHref());

				// تحقُّق شامل
				Assert.assertTrue(monthCard.isValidCard(),
						"❌ بطاقة «مدونة الشهر» غير مكتملة بالرغم من التحقّقات الفردية");
			});

			Allure.step("✅ Corpora-of-Month card is complete and correct");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - CorporaOfMonth Card");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - CorporaOfMonth Card");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("⚠️ Unexpected error: " + e.getMessage());
		}
	}

	@Test(description = "TC-21 | Verify that a hover effect (corner shading) is applied when the mouse pointer hovers over the 'مدونة الشهر' card.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User hovers over the 'مدونة الشهر' card on the home page")
	public void TC21_verifyHoverEffectOnBlogOfMonthCard() {
		System.out.println("TC21_verifyHoverEffectOnBlogOfMonthCard");
		Allure.description(
				"""
						Test Objective: Ensure the **single** 'مدونة الشهر' card shows a visual hover effect (box-shadow) when the pointer is on it.
						Steps:
						1) Scroll to the 'مدونة الشهر' section.
						2) Read box-shadow BEFORE hover.
						3) Hover on the card.
						4) Read box-shadow AFTER  hover.
						5) Assert the values differ.
						Expected Result: When the mouse pointer hovers over the "مدونة الشهر" card, the card should display a shaded visual effect from the corners to indicate hover state.
						""");

		try {
			/* 🔽 1. الانتقال إلى القسم --------------------------------------- */
			homePage.scrollToCorporaOfMonth();
			Allure.step("🔽 Scrolled & waited for 'مدونة الشهر' section");

			/* 📦 2. الحصول على المكوّن والـ box-shadow قبل التحويم ------------ */
			CorporaOfMonthCardComponent card = homePage.getCorporaOfMonthCard();
			String before = card.getBoxShadow();
			Allure.step("🎨 box-shadow BEFORE hover: " + before);

			/* 🖱 3. تنفيذ التحويم -------------------------------------------- */
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});",
					card.getCardRoot());
			new Actions(driver).moveToElement(card.getCardRoot()).perform();
			Allure.step("🖱 Pointer hovered on the Blog-of-Month card");

			// انتظار تغير box-shadow بدلًا من Thread.sleep
			new WebDriverWait(driver, Duration.ofSeconds(2)).until(d -> {
				String newShadow = card.getBoxShadow();
				return !newShadow.equals(before);
			});

			/* 🎨 4. قراءة الـ box-shadow بعد التحويم ------------------------- */
			String after = card.getBoxShadow();
			Allure.step("🎨 box-shadow AFTER  hover: " + after);

			/* ✅ 5. التحقق ---------------------------------------------------- */
			Assert.assertNotEquals(after, before, "❌ لم يظهر تأثير الظل عند تحويم الماوس على بطاقة «مدونة الشهر»");

			Allure.step("✅ Hover shadow verified successfully on Blog-of-Month card");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - BlogOfMonth Hover");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - BlogOfMonth Hover");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("⚠️ Hover effect test failed: " + e.getMessage());
		}
	}

	@Test(description = "TC-22 | Verify that the 'ادوات المنصة' section is displayed correctly on the home page with section title, description, and 13 cards.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User sees the 'أدوات المنصة' section on the home page")
	public void TC22_verifyToolsSection() {
		System.out.println("TC22_verifyToolsSection");
		Allure.description(
				"""
						Test Objective: Confirm that the 'أدوات المنصة' section is visible and contains its title, description, and exactly **13** tool-cards.
						Steps:
						1) Navigate to base URL  (done in BaseTest).
						2) Scroll to 'أدوات المنصة'.
						3) Assert section title + description are visible.
						4) Assert card count == 13.
						Expected Result: The "ادوات المنصة" section should be visible and display the section title, description, and exactly 13 cards.
						""");

		try {
			/* 🔽 1-Scroll to section ------------------------------------------ */
			homePage.scrollToToolsSection();
			Allure.step("🔽 Scrolled to 'أدوات المنصة' section");

			/* 👀 2-Visibility assertions -------------------------------------- */
			Assert.assertTrue(homePage.isToolsSectionVisible(), "❌ قسم «أدوات المنصة» (العنوان أو الوصف) غير ظاهر!");
			Allure.step("👁️ Title & description are visible.");

			/* 📊 3-Card-count assertion --------------------------------------- */
			int toolsCount = homePage.getToolsCardsCount();
			Allure.step("📦 Tool cards found: " + toolsCount);

			Assert.assertEquals(toolsCount, 13, "❌ يجب أن يحتوي قسم «أدوات المنصة» على 13 بطاقة بالضبط");

			Allure.step("✅ Section visible and contains exactly 13 cards.");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - Tools Section");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - Tools Section");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("⚠️ Unexpected error: " + e.getMessage());
		}
	}

	@Test(description = "TC-23 | Verify that each 'اداة' card displays the correct elements: icon image, title (Arabic + English), 'التفاصيل' button.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User reviews every tool card in 'أدوات المنصة' section")
	public void TC23_verifyEachToolCardElements() {
		System.out.println("TC23_verifyEachToolCardElements");
		Allure.description(
				"""
						Test Objective: Verify that **all 13** tool-cards display:
						✦ Icon image
						✦ Arabic title
						✦ English title
						 ✦ 'التفاصيل' button
						Steps:
						 1) Navigate to home page   (handled in BaseTest)
						 2) Scroll to 'أدوات المنصة' section
						 3) Assert the section is visible and contains exactly 13 cards
						 4) Loop through each card and verify every required element
						Expected Result: Each "اداة" card should display all required elements (icon image, Arabic title, English title, "التفاصيل" button).
						""");

		try {
			/* ❶ الانتقال إلى القسم ------------------------------------------------- */
			homePage.scrollToToolsSection(); // يستخدم BasePage.scrollToElement
			Allure.step("🔽 Scrolled & waited until 'أدوات المنصة' section is visible");

			// تأكيد ظهور العنوان نفسه
			Assert.assertTrue(homePage.isToolsSectionVisible(), "❌ قسم «أدوات المنصة» غير ظاهر على الصفحة");

			/* ❷ جلب بطاقات الأدوات كمكوّنات ---------------------------------------- */
			List<ToolsCardComponent> toolCards = homePage.getAllToolsCards();
			Allure.step("📦 Tool-cards retrieved: " + toolCards.size());

			Assert.assertEquals(toolCards.size(), 13, "❌ يجب أن يحتوي القسم على 13 بطاقة أداة بالضبط");

			/* ❸ التحقّق من كل بطاقة ------------------------------------------------ */
			for (int i = 0; i < toolCards.size(); i++) {
				final int cardNo = i + 1;
				ToolsCardComponent card = toolCards.get(i);

				Allure.step("🧪 Validating tool-card #" + cardNo, () -> {

					/* — أيقونة — */
					Assert.assertTrue(card.isIconVisible(), "❌ الأيقونة غير ظاهرة في البطاقة #" + cardNo);

					/* — العناوين — */
					Assert.assertTrue(card.isArTitleVisible(), "❌ العنوان العربي غير ظاهر في البطاقة #" + cardNo);
					Assert.assertTrue(card.isEnTitleVisible(), "❌ العنوان الإنجليزي غير ظاهر في البطاقة #" + cardNo);
					/* — زر التفاصيل — */
					Assert.assertTrue(card.isDetailsVisible(), "❌ زر «التفاصيل» غير ظاهر في البطاقة #" + cardNo);

					Allure.step("🏷️ AR Title: " + card.getArTitle());
					Allure.step("🏷️ EN Title: " + card.getEnTitle());
					Allure.step("🏷️ Icon: " + card.getIconSrc());
					Allure.step("🏷️ Button: " + card.getDetailsText());

				});
			}

			Allure.step("✅ جميع بطاقات الأدوات مكتملة العناصر بنجاح");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - Tools Cards");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - Tools Cards");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("⚠️ Unexpected error: " + e.getMessage());
		}
	}

	@Test(description = "TC-24 | Verify that hovering over 'التفاصيل' button changes its color to dark blue. ", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User moves the pointer over every 'التفاصيل' button in the tools section")
	public void TC24_verifyHoverEffectOnAllDetailsButtons() {
		System.out.println("TC24_verifyHoverEffectOnAllDetailsButtons");
		Allure.description(
				"""
						Test Objective ▸ Every 'التفاصيل' button inside the 13 tool-cards should change its background-colour on hover.
						Per-card Steps:
						1) Scroll once to 'أدوات المنصة'.
						2) Read button background BEFORE hover.
						3) Hover.
						4) Read background AFTER hover.
						5) Assert values differ.
						Expected Result: When hovering over "التفاصيل" button, its color should change to dark blue.
						""");

		try {
			/* 🔽 1. الانتقال إلى القسم مرة واحدة -------------------------------- */
			homePage.scrollToToolsSection();
			Allure.step("🔽 Scrolled to 'أدوات المنصة' section");

			Assert.assertTrue(homePage.isToolsSectionVisible(), "❌ قسم «أدوات المنصة» غير ظاهر!");

			/* 📦 2. جلب البطاقات كمكوّنات --------------------------------------- */
			List<ToolsCardComponent> cards = homePage.getAllToolsCards();
			Allure.step("📦 Tool-cards found: " + cards.size());
			Assert.assertEquals(cards.size(), 13, "❌ يجب أن يكون عدد بطاقات الأدوات = 13 بالضبط!");

			Actions actions = new Actions(driver);

			/* 🔄 3. اللوب على كل بطاقة / زر ------------------------------------ */
			for (int i = 0; i < cards.size(); i++) {
				final int cardNo = i + 1;
				ToolsCardComponent card = cards.get(i);

				Allure.step("🧪 Hover-colour test for card #" + cardNo, () -> {

					Assert.assertTrue(card.isDetailsVisible(), "❌ زر «التفاصيل» غير ظاهر في البطاقة #" + cardNo);

					WebElement btn = card.getDetailsButtonElement();

					/* 🎨 BEFORE hover */
					String before = card.getDetailsBackground(); // مثال: rgb(255, 255, 255)
					Allure.step("🎨 background BEFORE: " + before);

					/* 🖱 Hover */
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
					actions.moveToElement(btn).perform();

					// مهلة قصيرة لتطبيق الـ CSS
					new WebDriverWait(driver, Duration.ofSeconds(2)).until(d -> {
						String newShadow = card.getDetailsBackground();
						return !newShadow.equals(before);
					});

					/* 🎨 AFTER hover */
					String after = card.getDetailsBackground();
					Allure.step("🎨 background  AFTER: " + after);

					/* ✅ Assertion */
					Assert.assertNotEquals(after, before,
							"❌ لم يتغيّر لون الخلفية عند التحويم على زر «التفاصيل» للبطاقة #" + cardNo);

					/* 🔄 حرّك المؤشر بعيدًا حتى لا يؤثر على البطاقة التالية */
					actions.moveByOffset(-60, -60).perform();
				});
			}

			Allure.step("✅ جميع أزرار «التفاصيل» تُغيّر لونها عند التحويم بنجاح");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - Details Buttons Hover");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - Details Buttons Hover");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("⚠️ Hover effect loop failed: " + e.getMessage());
		}
	}

	@Test(description = "TC-25 | Verify that clicking the 'التفاصيل' button opens a popup screen (modal window) with correct content.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User opens the details-modal for each tool in 'أدوات المنصة'")
	public void TC25_verifyToolDetailsModal_AllCards() {
		System.out.println("TC25_verifyToolDetailsModal_AllCards");
		Allure.description("""
				Test Objective: For **each** of the 13 tool-cards should apperas all content
				Per-card Steps:
				1) Click the 'التفاصيل' button
				2) Ensure a modal window appears and shows:
				✦ Arabic title
				✦ English title
				✦ Icon image
				✦ Auto-playing video
				✦ Description paragraph
				✦ Close (×) button
				3) Close the modal and continue to the next card.
				Expected Result: Clicking "التفاصيل" button should open a popup screen displaying:
				✦ Title of the tool
				✦ Icon image of the tool
				✦ Auto-playing video explaining how to use the tool
				✦ Description of the tool
				✦ Side vertical scroll should be available (if content is long)
				✦ Close button should be visible on top left of the popup.
				""");

		try {
			/* ── ❶ الانتقال إلى القسم ------------------------------------------------ */
			homePage.scrollToToolsSection();
			Allure.step("🔽 Scrolled to 'أدوات المنصة' section");

			/* ── ❷ جلب القائمة مرة واحدة -------------------------------------------- */
			List<ToolsCardComponent> cards = homePage.getAllToolsCards();

			JavascriptExecutor js = (JavascriptExecutor) driver; // للعدّ فقط
			Allure.step("📦 Tool-cards retrieved: " + cards.size());
			Assert.assertEquals(cards.size(), 13, "❌ يجب أن يكون عدد البطاقات 13 بالضبط");
			System.out.println("📦 Tool-cards retrieved: " + cards.size());

			/* ── ❸ التكرار على كل بطاقة --------------------------------------------- */
			for (int i = 0; i < cards.size(); i++) {
				final int cardNo = i + 1;
				ToolsCardComponent card = cards.get(i); // نفس الكائن طوال الاختبار

				Allure.step("🧪 Modal test for tool-card #" + cardNo, () -> {
					System.out.println("🧪 Modal test for tool-card #" + cardNo);
					/* ⚠️ أعد جلب البطاقة كل دورة + مرِّر إليها لضمان قابليّة النقر */
					js.executeScript("arguments[0].scrollIntoView({block:'center'});", card.getRootElement());
					js.executeScript("arguments[0].click();", card.getDetailsButtonElement());

					ToolDetailsModal modal = new ToolDetailsModal(driver);
					Allure.step("🖱️ Clicked 'التفاصيل' → modal should appear");
					System.out.println("🖱️ Clicked 'التفاصيل' → modal should appear");

					/* ❸-2 تأكيد ظهور المودال --------------------------------------- */
					Assert.assertTrue(modal.isVisible(), "❌ المودال لم يظهر (card #" + cardNo + ")");

					/* ❸-3 التحقُّق من العناصر بداخل المودال ------------------------ */
					Assert.assertTrue(modal.isCloseVisible(), "❌ زر الإغلاق غير ظاهر (card #" + cardNo + ")");
					Assert.assertTrue(modal.isIconVisible(), "❌ الأيقونة غير ظاهرة   (card #" + cardNo + ")");
					Assert.assertTrue(modal.isVideoVisible(), "❌ الفيديو غير ظاهر     (card #" + cardNo + ")");
					Assert.assertTrue(modal.isParagraphShown(), "❌ الوصف غير ظاهر       (card #" + cardNo + ")");

					/* ❸-4 طباعة العناوين في التقرير -------------------------------- */
					Allure.step("🏷️ AR Title: " + modal.getArTitle());
					Allure.step("🏷️ EN Title: " + modal.getEnTitle());
					Allure.step("🏷️ Icon: " + modal.getIconSrc());
					Allure.step("🏷️ Video: " + modal.getVideoSrc());
					Allure.step("🏷️ Description: " + modal.getParagraphText());
					System.out.println("🏷️ AR Title: " + modal.getArTitle());
					System.out.println("🏷️ EN Title: " + modal.getEnTitle());
					System.out.println("🏷️ Icon: " + modal.getIconSrc());
					System.out.println("🏷️ Description: " + modal.getParagraphText());

					/* ❸-5 أغلق المودال وانتظر زوال الـ overlay --------------------- */
					modal.close();
					Allure.step("❎ Modal closed successfully");

				});
			}

			Allure.step("🎉 جميع المودالات فُتحت وأُغلقت بمحتوى صحيح");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - Tool Details Modal");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - Tool Details Modal");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("⚠️ Unexpected error: " + e.getMessage());
		}
	}

	@Test(description = "TC-26 | Verify that the video inside the popup starts playing automatically.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User opens the details-modal for each tool and verifies the video auto-plays")
	public void TC26_verifyVideoAutoPlayInEachToolModal() {
		System.out.println("TC26_verifyVideoAutoPlayInEachToolModal");
		Allure.description(
				"""
						Test Objective: For **each** of the 13 tool-cards The video inside should start playing automatically without user interaction.
						Per-card Steps:
						1) Click the 'التفاصيل' button
						2) Ensure a modal window appears and shows:
						✦ Arabic title
						✦ English title
						✦ Icon image
						✦ Description paragraph
						✦ Auto-playing video (⚠️ Must start automatically)
						3) Close the modal and continue to the next card.
						Expected Result: The video inside the popup should start playing automatically without user interaction.
								""");

		try {
			/* ── ❶ الانتقال إلى القسم ------------------------------------------------ */
			homePage.scrollToToolsSection();
			Allure.step("🔽 Scrolled to 'أدوات المنصة' section");

			/* ── ❷ جلب القائمة مرة واحدة -------------------------------------------- */
			List<ToolsCardComponent> cards = homePage.getAllToolsCards();

			JavascriptExecutor js = (JavascriptExecutor) driver;
			Allure.step("📦 Tool-cards retrieved: " + cards.size());
			Assert.assertEquals(cards.size(), 13, "❌ يجب أن يكون عدد البطاقات 13 بالضبط");

			/* ── ❸ التكرار على كل بطاقة --------------------------------------------- */
			for (int i = 0; i < cards.size(); i++) {
				final int cardNo = i + 1;
				ToolsCardComponent card = cards.get(i);

				Allure.step("🧪 Modal test for tool-card #" + cardNo, () -> {
					System.out.println("🧪 Modal test for tool-card #" + cardNo);

					// ✅ Scroll and click the details button (with real click)
					js.executeScript("arguments[0].scrollIntoView({block:'center'});", card.getRootElement());
					card.getDetailsButtonElement().click(); // ⬅️ تغيّر أساسي: استخدام .click() الحقيقي

					ToolDetailsModal modal = new ToolDetailsModal(driver);
					Allure.step("🖱️ Clicked 'التفاصيل' → modal should appear");

					// ✅ التحقق من ظهور المودال
					Assert.assertTrue(modal.isVisible(), "❌ المودال لم يظهر (card #" + cardNo + ")");

					// ✅ التحقق من ظهور الفيديو داخل المودال
					Assert.assertTrue(modal.isVideoVisible(), "❌ الفيديو غير ظاهر     (card #" + cardNo + ")");

					// 🏷️ طباعة عناصر المودال في التقرير
					Allure.step("🏷️ AR Title: " + modal.getArTitle());
					Allure.step("🏷️ EN Title: " + modal.getEnTitle());
					Allure.step("🏷️ Video: " + modal.getVideoSrc());

					// ✅ التقاط العنصر الفعلي للفيديو لاستخدامه في JavaScript
					WebElement videoElement = modal.getVideoElement();

					// ✅ الانتظار الذكي حتى يبدأ الفيديو تشغيله فعليًا
					// ✅ Smart wait for video to auto-play
					Boolean isPlaying = new WebDriverWait(driver, Duration.ofSeconds(6)).until(driver1 -> {
						return (Boolean) ((JavascriptExecutor) driver1).executeScript("""
								const video = arguments[0];
								if (!video) return false;
								return !video.paused && !video.ended && video.readyState > 2 && video.currentTime > 0.5;
								""", videoElement);
					});

					// ✅ تأكيد أن الفيديو بدأ التشغيل فعليًا
					Assert.assertTrue(isPlaying, "❌ الفيديو لم يبدأ التشغيل تلقائيًا (card #" + cardNo + ")");
					Allure.step("🎥 Video started playing automatically ✅");

					// ✅ إغلاق المودال
					modal.close();
					Allure.step("❎ Modal closed successfully");
				});
			}

			Allure.step("🎉 جميع المودالات فُتحت وتم تشغيل الفيديو تلقائيًا وأُغلقت بنجاح");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - Video AutoPlay Modal");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - Video AutoPlay Modal");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("⚠️ Unexpected error: " + e.getMessage());
		}
	}

	@Test(description = "TC-27 | Verify that the popup allows vertical scrolling if the content exceeds visible area.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User opens the details-modal for each tool and checks vertical scroll support")
	public void TC27_verifyModalVerticalScroll_AllCards() {
		System.out.println("TC27_verifyModalVerticalScroll_AllCards");
		Allure.description("""
				Test Objective: For **each** of the 13 tool-cards should allow vertical scrolling to view all content.
				Per-card Steps:
				1) Click the 'التفاصيل' button
				2) Ensure a modal appears
				3) If content is long → check for vertical scroll
				4) If content is short → skip scroll check
				5) Close the modal and continue
				Expected Result: Popup should allow vertical scrolling to view all content.
				  """);

		try {
			/*
			 * ── ❶ الانتقال إلى قسم أدوات المنصة | Scroll to "Tools" section
			 * ───────────────
			 */
			homePage.scrollToToolsSection();
			Allure.step("🔽 Scrolled to 'أدوات المنصة' section");

			/*
			 * ── ❷ جلب كل بطاقات الأدوات | Retrieve all tool cards
			 * ─────────────────────────
			 */
			List<ToolsCardComponent> cards = homePage.getAllToolsCards();
			JavascriptExecutor js = (JavascriptExecutor) driver;

			Allure.step("📦 Tool-cards retrieved: " + cards.size());
			Assert.assertEquals(cards.size(), 13, "❌ يجب أن يكون عدد البطاقات 13 بالضبط");

			/*
			 * ── ❸ التكرار على كل بطاقة أداة | Loop through all tool cards ───────────────
			 */
			for (int i = 0; i < cards.size(); i++) {
				final int cardNo = i + 1;
				ToolsCardComponent card = cards.get(i);

				Allure.step("🧪 Modal scroll test for card #" + cardNo, () -> {
					System.out.println("🧪 Modal test for tool-card #" + cardNo);

					// ⬇️ تمرير للبطاقة والضغط على زر التفاصيل | Scroll to the card and click
					// 'التفاصيل'
					js.executeScript("arguments[0].scrollIntoView({block:'center'});", card.getRootElement());
					js.executeScript("arguments[0].click();", card.getDetailsButtonElement());

					// 🪟 إنشاء كائن نافذة التفاصيل | Initialize the details modal
					ToolDetailsModal modal = new ToolDetailsModal(driver);

					// ✅ تأكيد ظهور المودال | Ensure the modal is visible
					Assert.assertTrue(modal.isVisible(), "❌ المودال لم يظهر (card #" + cardNo + ")");

					// 📏 جلب عنصر المحتوى داخل المودال | Get the modal’s content container
					WebElement content = modal.getDialogContentElement();

					// 📐 حساب ارتفاع المحتوى وارتفاع الإطار | Get scrollHeight and clientHeight
					Long scrollHeight = (Long) js.executeScript("return arguments[0].scrollHeight", content);
					Long clientHeight = (Long) js.executeScript("return arguments[0].clientHeight", content);

					// ℹ️ طباعة القيم في التقرير | Print the heights for debugging
					Allure.step("📏 scrollHeight = " + scrollHeight + " | clientHeight = " + clientHeight);

					// 🔍 تحقق فقط إذا كان المحتوى أطول من الإطار | Only check scroll if content is
					// long
					if (scrollHeight > clientHeight) {
						// ✅ التحقق من وجود تمرير عمودي | Check if scroll is supported
						boolean isScrollable = modal.isVerticallyScrollable();
						Allure.step("📜 Scroll needed: ✅ → Modal scrollable: " + isScrollable);

						Assert.assertTrue(isScrollable,
								"❌ كان يجب أن يكون هناك تمرير عمودي ولكن لم يظهر (card #" + cardNo + ")");
					} else {
						// 🟡 المحتوى قصير، لا حاجة لاختبار التمرير | Content is short, scroll not
						// required
						Allure.step("📜 Scroll not needed: content fits (card #" + cardNo + ")");
					}

					// ❎ إغلاق المودال | Close the modal
					modal.close();
					Allure.step("❎ Modal closed successfully");
				});
			}

			/* ✅ طباعة ملخص نجاح الحالة | Final success message */
			Allure.step("🎉 تم اختبار التمرير بنجاح لجميع البطاقات حسب الحاجة");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - Modal Scroll Check");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - Modal Scroll Check");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("⚠️ Unexpected error: " + e.getMessage());
		}
	}

	@Test(description = "TC-28 | Verify that clicking the close button closes the popup screen.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User opens the details-modal for each tool and closes it using the ❌ button")
	public void TC28_verifyModalCloseButton_AllCards() {
		System.out.println("TC28_verifyModalCloseButton_AllCards");
		Allure.description(
				"""
						Test Objective: For **each** of the 13 tool-cards close button should close the popup screen and return the user to the "ادوات المنصة" section view.
						Per-card Steps:
						1) Click the 'التفاصيل' button
						2) Ensure the modal window appears
						3) Click the 'close' (❌) button
						4) Ensure the modal window disappears
						Expected Result: Clicking the close button should close the popup screen and return the user to the "ادوات المنصة" section view.
								  """);

		try {
			/*
			 * ── ❶ الانتقال إلى قسم أدوات المنصة | Scroll to 'Tools' section
			 * ───────────────
			 */
			homePage.scrollToToolsSection();
			Allure.step("🔽 Scrolled to 'أدوات المنصة' section");

			/*
			 * ── ❷ جلب بطاقات الأدوات | Retrieve all tool cards ───────────────────────────
			 */
			List<ToolsCardComponent> cards = homePage.getAllToolsCards();
			JavascriptExecutor js = (JavascriptExecutor) driver;

			Allure.step("📦 Tool-cards retrieved: " + cards.size());
			Assert.assertEquals(cards.size(), 13, "❌ يجب أن يكون عدد البطاقات 13 بالضبط");

			/*
			 * ── ❸ التكرار على كل بطاقة | Loop through all tool cards ───────────────────
			 */
			for (int i = 0; i < cards.size(); i++) {
				final int cardNo = i + 1;
				ToolsCardComponent card = cards.get(i);

				Allure.step("🧪 Modal close test for tool-card #" + cardNo, () -> {
					System.out.println("🧪 Modal test for tool-card #" + cardNo);

					// ⬇️ تمرير للبطاقة والنقر على "التفاصيل" | Scroll and click 'التفاصيل'
					js.executeScript("arguments[0].scrollIntoView({block:'center'});", card.getRootElement());
					js.executeScript("arguments[0].click();", card.getDetailsButtonElement());

					// 🪟 إنشاء نافذة التفاصيل | Initialize modal window
					ToolDetailsModal modal = new ToolDetailsModal(driver);

					// ✅ تأكيد ظهور النافذة | Confirm modal is visible
					Assert.assertTrue(modal.isVisible(), "❌ المودال لم يظهر (card #" + cardNo + ")");

					// ✅ تأكيد ظهور زر الإغلاق | Confirm close button is visible
					Assert.assertTrue(modal.isCloseVisible(), "❌ زر الإغلاق غير ظاهر (card #" + cardNo + ")");

					// ❎ النقر على زر الإغلاق | Click the ❌ close button
					modal.close();
					Allure.step("❎ Clicked close button");

					// ⛔ التأكد من اختفاء النافذة | Confirm modal is no longer visible
					boolean modalClosed = !modal.isVisible();
					Assert.assertTrue(modalClosed,
							"❌ المودال لم يُغلق بعد الضغط على زر الإغلاق (card #" + cardNo + ")");

					Allure.step("✅ Modal successfully closed (card #" + cardNo + ")");
				});
			}

			Allure.step("🎉 جميع المودالات أُغلقت بنجاح باستخدام زر الإغلاق");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - Modal Close Button");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - Modal Close Button");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("⚠️ Unexpected error: " + e.getMessage());
		}
	}

	@Test(description = "TC-29 | Verify that the 'مزايا المنصة' section is displayed correctly on the home page.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User sees the 'مزايا المنصة' section while scrolling the homepage")
	public void TC29_verifyFeaturesSectionVisibility() {
		System.out.println("TC29_verifyFeaturesSectionVisibility");
		Allure.description(
				"""
						Test Objective: Ensure that the 'مزايا المنصة' (Features) section is visible on the homepage, and that it includes a proper title and description.
						Steps:
						1) Navigate to the homepage
						2) Scroll to the 'مزايا المنصة' section
						3) Verify that the section is visible
						Expected Result: The "مزايا المنصة" section should be visible with the correct section title and description text.
						""");

		try {
			/*
			 * ── ❶ الانتقال إلى قسم مزايا المنصة | Scroll to the 'Features' section ───────
			 */
			homePage.scrollToFeaturesSection(); // ⚠️ Optional: if you have a scroll method
			Allure.step("🔽 Scrolled to 'مزايا المنصة' section");

			/*
			 * ── ❷ التحقق من ظهور القسم | Check if the section is visible ───────────────
			 */
			boolean isVisible = homePage.isFeaturesSectionVisible();

			// ✅ إضافة النتيجة إلى تقرير Allure
			Allure.step("👁️ Features section visible: " + isVisible);

			// ✅ تأكيد الظهور الفعلي باستخدام Assert
			Assert.assertTrue(isVisible, "❌ قسم 'مزايا المنصة' غير ظاهر في الصفحة الرئيسية");

			// ✅ طباعة للتوثيق في الكونسول
			System.out.println("✅ Features section is visible on the homepage");

			Allure.step("🎉 تم التأكد من أن قسم مزايا المنصة ظاهر للمستخدم");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - Features Section Not Visible");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - Features Section Visibility");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("⚠️ Unexpected error: " + e.getMessage());
		}
	}

	@Test(description = "TC-30 | Verify that each 'ميزة' card displays the correct elements: animation, title text. ", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User sees all feature cards with proper visuals and titles")
	public void TC30_verifyFeatureCardsVisualsAndTitles() {
		System.out.println("TC30_verifyFeatureCardsVisualsAndTitles");
		Allure.description("""
				Test Objective: For each 'ميزة' card (4 total), display an animation and a title.
				Steps:
				1) Scroll to the 'مزايا المنصة' section
				2) Verify that the section is visible
				✦ An animation or visual is displayed
				✦ A title text is shown clearly
				Expected Result: Each 'ميزة' card should display an animation and a title.
				""");

		try {
			/* ── ❶ الانتقال إلى قسم المزايا | Scroll to features section ───────────── */
			homePage.scrollToFeaturesSection();
			Allure.step("🔽 Scrolled to 'مزايا المنصة' section");

			/* ── ❷ جلب البطاقات | Get all feature cards ───────────────────────────── */
			List<FeatureCardComponent> cards = homePage.getAllFeatureCards();
			Allure.step("📦 Feature cards retrieved: " + cards.size());
			Assert.assertEquals(cards.size(), 4, "❌ يجب أن يكون عدد البطاقات 4 بالضبط");

			/* ── ❸ التحقق من كل بطاقة | Validate each card ───────────────────────── */
			for (int i = 0; i < cards.size(); i++) {
				final int cardNo = i + 1;
				FeatureCardComponent card = cards.get(i);

				Allure.step("🧪 Checking feature card #" + cardNo, () -> {
					boolean hasAnimation = card.isAnimationVisible();
					String title = card.getTitleText();

					Allure.step("🎬 Animation visible: " + hasAnimation);
					Allure.step("🏷️ Title: " + title);

					Assert.assertTrue(hasAnimation, "❌ لا يوجد أنيميشن في البطاقة #" + cardNo);
					Assert.assertFalse(title.isEmpty(), "❌ عنوان البطاقة فارغ #" + cardNo);
				});
			}

			Allure.step("🎉 جميع بطاقات المزايا تحتوي على أنيميشن وعنوان");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - Feature Cards Visuals");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - Feature Cards Visuals");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("⚠️ Unexpected error: " + e.getMessage());
		}
	}

	@Test(description = "TC-31 | Verify that a hover effect (shaded visual effect) is applied when the mouse pointer hovers over any 'ميزة' card.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.MINOR)
	@Story("User hovers over each feature card and verifies the shadow effect")
	public void TC31_verifyHoverEffectOnEachFeatureCard() {
		System.out.println("TC31_verifyHoverEffectOnEachFeatureCard");
		Allure.description(
				"""
						Test Objective: Ensure that each of the 4 feature cards shows a box-shadow hover effect when the pointer hovers over it.
						Steps:
						  1) Scroll to the 'مزايا المنصة' section.
						  2) For each card:
						  ✦ Read box-shadow BEFORE hover.
						  ✦ Hover on the card.
						  ✦ Read box-shadow AFTER  hover.
						  ✦ Assert the values differ.
						 Expected Result: When the mouse pointer hovers over any "ميزة" card, a shaded visual effect should appear to indicate hover state.
						""");

		try {
			/* 🔽 1. الانتقال إلى قسم المزايا ---------------------------------------- */
			homePage.scrollToFeaturesSection();
			Allure.step("🔽 Scrolled to 'مزايا المنصة' section");

			/* 📦 2. الحصول على كل البطاقات كمكونات ------------------------------ */
			List<FeatureCardComponent> cards = homePage.getAllFeatureCards();
			Assert.assertEquals(cards.size(), 4, "❌ يجب أن يكون عدد بطاقات المزايا 4");

			/* 🖱 3. التكرار على كل بطاقة وتحقيق التأثير -------------------------- */
			for (int i = 0; i < cards.size(); i++) {
				final int cardNo = i + 1;
				FeatureCardComponent card = cards.get(i);

				Allure.step("🧪 Hover test for feature card #" + cardNo, () -> {
					System.out.println("🧪 Hover test for feature card #" + cardNo);

					// قراءة box-shadow قبل التحويم
					String before = card.getBoxShadow();
					Allure.step("🎨 box-shadow BEFORE hover: " + before);

					// تنفيذ التحويم
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});",
							card.getCardRoot());
					new Actions(driver).moveToElement(card.getCardRoot()).perform();
					Allure.step("🖱 Pointer hovered on feature card #" + cardNo);

					// انتظار تغير box-shadow بدلًا من Thread.sleep
					new WebDriverWait(driver, Duration.ofSeconds(2)).until(d -> {
						String newShadow = card.getBoxShadow();
						return !newShadow.equals(before);
					});

					// قراءة box-shadow بعد التحويم
					String after = card.getBoxShadow();
					Allure.step("🎨 box-shadow AFTER  hover: " + after);

					// التأكد من ظهور التأثير
					Assert.assertNotEquals(after, before,
							"❌ لم يظهر تأثير الظل عند التحويم على بطاقة الميزة #" + cardNo);
				});
			}

			Allure.step("✅ Hover shadow verified successfully for all 4 feature cards");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - Feature Cards Hover");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - Feature Cards Hover");
			attachFailureVideo("📹 Video (on Exception)");
			throw new RuntimeException("⚠️ Hover effect test failed: " + e.getMessage());
		}
	}

	@Test(description = "TC-32 | Verify that the 'فلك في ارقام' section is displayed correctly on the home page.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.CRITICAL)
	@Story("User scrolls to the 'Falak in Numbers' section and verifies visibility and content")
	public void TC32_verifyFalakInNumbersSectionVisibility() {
		System.out.println("TC32_verifyFalakInNumbersSectionVisibility");
		Allure.description("""
				Test Objective: Ensure the 'فلك في أرقام' section on the home page is displayed properly with:
				✦ Section title
				✦ Description text
				✦ Static image
				Steps:
				1) Scroll to the 'فلك في أرقام' section.
				2) Verify visibility of the section.
				3) Verify title text is correct.
				4) Verify paragraph description is present.
				5) Verify image appears.
				Expected Result: The "فلك في ارقام" section should be visible and display:
				✦  Section title "فلك في ارقام"
				✦ Description text
				✦ Static image
				""");

		try {
			// 🔽 الانتقال إلى القسم
			((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 1200)");
			Allure.step("🔽 Scrolled to the ‘فلك في أرقام’ section");

			// ✅ التحقق من الظهور الكامل للقسم
			Assert.assertTrue(homePage.isFalakStatsSectionVisible(), "❌ قسم 'فلك في أرقام' غير ظاهر");

			// 🧾 التحقق من عنوان القسم
			String actualTitle = homePage.getFalakStatsTitleText();
			Assert.assertEquals(actualTitle, "فلك في أرقام", "❌ عنوان القسم غير صحيح");
			Allure.step("🏷️ Section title: " + actualTitle);

			// 📄 التحقق من الوصف
			String desc = homePage.getFalakStatsDescriptionText();
			Assert.assertTrue(desc.contains("منصة فلك") && desc.length() > 20, "❌ الوصف غير صحيح أو غير ظاهر");
			Allure.step("📄 Section description: " + desc);

			// 🖼️ التحقق من ظهور الصورة
			Assert.assertTrue(homePage.isFalakStatsImageVisible(), "❌ صورة القسم غير ظاهرة");
			Allure.step("🖼️ Statistics image is visible: " + homePage.getFalakStatsImageSrc());

			Allure.step("✅ ‘فلك في أرقام’ section is displayed successfully");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - Falak Stats Section");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - Falak Stats Section");
			throw new RuntimeException("⚠️ Unexpected error in stats section test: " + e.getMessage());
		}
	}

	@Test(description = "TC-33 | Verify that the static image in the 'فلك في ارقام' section is displayed correctly without distortion or broken image.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User scrolls to the Falak Stats section and checks the static image integrity")
	public void TC33_verifyFalakStatsImageIsDisplayedCorrectly() {
		System.out.println("TC33_verifyFalakStatsImageIsDisplayedCorrectly");
		Allure.description(
				"""
						Test Objective: Ensure the static image in the 'فلك في أرقام' section:
						✦ Is visible on the page
						✦ Loads from a valid source (src)
						✦ Is not broken or distorted
						Steps:
						1) Scroll to the section
						2) Check visibility
						3) Check src attribute is valid
						4) Ensure no broken image icon or size = 0
						Expected Result: The static image should be displayed clearly without any distortion or broken image symbol.
						""");

		try {
			// 🔽 الانتقال إلى القسم
			((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 1200)");
			Allure.step("🔽 Scrolled to the ‘فلك في أرقام’ section");

			// 🖼️ التحقق من ظهور الصورة
			Assert.assertTrue(homePage.isFalakStatsImageVisible(), "❌ صورة قسم 'فلك في أرقام' غير ظاهرة");

			// 🔗 التحقق من صحة الرابط
			String imgSrc = homePage.getFalakStatsImageSrc();
			Assert.assertNotNull(imgSrc, "❌ رابط الصورة غير موجود (null)");
			Assert.assertFalse(imgSrc.isEmpty(), "❌ رابط الصورة فارغ");
			Allure.step("🔗 Image source: " + imgSrc);

			// 📏 التحقق من أبعاد الصورة (للتأكد أنها غير مكسورة)
			WebElement imgElement = driver
					.findElement(By.xpath("//section[.//h2[normalize-space()='فلك في أرقام']]//img"));
			int imgWidth = imgElement.getSize().getWidth();
			int imgHeight = imgElement.getSize().getHeight();

			Allure.step("📏 Image dimensions: Width = " + imgWidth + ", Height = " + imgHeight);
			Assert.assertTrue(imgWidth > 50 && imgHeight > 50, "❌ الصورة مكسورة أو مشوهة (الأبعاد غير طبيعية)");

			Allure.step("✅ Falak Stats image is displayed correctly");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - Falak Image");
			attachFailureVideo("📹 Video (on AssertionError)");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - Falak Image");
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException("⚠️ Unexpected error in image test: " + e.getMessage());
		}
	}

	@Test(description = "TC-34 | Verify that clicking the 'Top of Page' button scrolls the page up to the top of the home page.", retryAnalyzer = RetryAnalyzer.class)
	@Severity(SeverityLevel.NORMAL)
	@Story("User scrolls down and clicks the Top of Page button to return to the top")
	public void TC34_verifyScrollToTopButtonFunctionality() {
		System.out.println("TC34_verifyScrollToTopButtonFunctionality");
		Allure.description(
				"""
						Test Objective: Ensure the 'Back-to-Top' button scrolls the user smoothly to the top of the page.
						Steps:
						1) Scroll far down to make the button appear
						2)Check button is visible
						3) Click it
						4) Confirm scroll to top by checking scroll position or element visibility
						Expected Result: The page should scroll smoothly to the top of the home page (i.e., the user should be returned to the top of the page after clicking the "Top of Page" button).
						""");

		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;

			// 🔽 Scroll down to make button appear
			js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
			// إتاحة وقت لظهور الزر
			new WebDriverWait(driver, Duration.ofSeconds(5))
					.until(ExpectedConditions.visibilityOf(homePage.getBackToTopButtonElement()));
			Allure.step("🔽 Scrolled to bottom of the page");

			// 👁️ Check button is visible
			Assert.assertTrue(homePage.isBackToTopButtonVisible(), "❌ زر العودة للأعلى غير ظاهر");

			// 🔼 Click the button
			homePage.clickBackToTopButton();
			Allure.step("🔼 Clicked the Back-to-Top button");

			// ⏳ Wait for smooth scroll
			new WebDriverWait(driver, Duration.ofSeconds(5))
					.until(ExpectedConditions.visibilityOf(homePage.getHeaderElement()));

			// 📍 تأكيد الوصول إلى الأعلى عبر عنصر ظاهر في البداية (مثلاً العنوان الرئيسي
			// للصفحة)
			boolean isAtTop = homePage.isHeaderVisible();
			Assert.assertTrue(isAtTop, "❌ لم يتم الرجوع لأعلى الصفحة بعد الضغط على الزر");

			Allure.step("✅ Page scrolled to the top successfully");

		} catch (AssertionError ae) {
			attachFullPageScreenshot("Assertion Failure - Scroll To Top");
			throw ae;

		} catch (Exception e) {
			attachFullPageScreenshot("Unexpected Exception - Scroll To Top");
			throw new RuntimeException("⚠️ Scroll-to-top test failed: " + e.getMessage());
		}
	}

	/**
	 * Shared method to test navigation to the right section.
	 * 
	 * دالة مشتركة لاختبار صحة التنقل للقسم الصحيح.
	 */
	@Step("Verify '{cardName}' button redirects correctly")
	private void runSectionTest(String cardName, Runnable clickAction, Supplier<Boolean> isVisibleCheck) {
		Allure.description(String.format("""
				Test Objective: verify the '%s' Card navigates to the section correctly
				Step:
				1) Navigate to the website URL. (handled in BaseTest).
				2) Click on '%s' Card.
				3) Observe the page scrolling behavior.
				Expected Result: '%s' card should scroll the page to the '%s' section.
				The '%s' section should be fully visible and all content should be displayed correctly.
				""", cardName, cardName, cardName, cardName, cardName));
		try {
			Allure.step("click on" + cardName + " Card", () -> clickAction.run());
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> isVisibleCheck.get());

			Allure.step("Section '" + cardName + "' is visible in the viewport");

		} catch (Exception e) {
			// If unexpected exception, attach screenshot and rethrow
			// في حالة حدوث خطأ غير متوقع، يتم التوثيق بلقطة شاشة
			attachFullPageScreenshot("Failed to scroll or detect section - - " + cardName);
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException(e);
		}
	}

	/**
	 * Shared method to test navigation and URL redirection.
	 * 
	 * دالة مشتركة لاختبار التنقل والتأكد من صحة الرابط.
	 */
	@Step("Verify '{buttonName}' button redirects correctly")
	private void runNavTest(String buttonName, Runnable clickAction, String expectedUrl) {
		Allure.description(String.format("""
				Test Objective: Verify the '%s' button navigates correctly.
				Steps:
				1) Navigate to base URL (handled in BaseTest).
				2) Click on '%s' button.
				3) Wait for the page to load.
				Expected Result: Current URL matches '%s'
				""", buttonName, buttonName, expectedUrl));

		try {
			// Step 1: Click the button
			// الخطوة 1: تنفيذ النقر على الزر المحدد
			Allure.step("Click on '" + buttonName + "' button", () -> clickAction.run());

			// Step 2: Wait until URL changes to expected (footer as indicator)
			// الخطوة 2: انتظار تحميل الصفحة بناءً على تغير الرابط
			new WebDriverWait(driver, Duration.ofSeconds(10))
					.until(ignored -> homePage.getCurrentURL().contains(expectedUrl));

			// Step 3: Get the current URL and attach it to the report
			// الخطوة 3: الحصول على الرابط الحالي وإضافته للتقرير
			String actualUrl = homePage.getCurrentURL();
			Allure.step("Capture current URL", () -> Allure.attachment("Actual URL", actualUrl));
			Allure.step("🔗 Final page URL: " + actualUrl); // ✅ تم التوثيق هنا فقط

			// Step 4: Verify URL match (corrected order)
			// الخطوة 4: التحقق من تطابق الرابط المتوقع مع الرابط الفعلي
			Assert.assertEquals(actualUrl, expectedUrl, "الرابط الحالي لا يطابق الرابط المتوقع");

		} catch (AssertionError ae) {
			// If assertion fails, attach screenshot and throw error
			// في حالة فشل المطابقة، يتم التقاط صورة وإظهار الخطأ
			attachFullPageScreenshot("Assertion Failure - " + buttonName);
			attachFailureVideo("📹 Video (on failure)");
			throw ae;

		} catch (Exception e) {
			// If unexpected exception, attach screenshot and rethrow
			// في حالة حدوث خطأ غير متوقع، يتم التوثيق بلقطة شاشة
			attachFullPageScreenshot("Exception occurred - " + buttonName);
			attachFailureVideo("📹 Video (on exception)");
			throw new RuntimeException(e);
		}
	}
}
