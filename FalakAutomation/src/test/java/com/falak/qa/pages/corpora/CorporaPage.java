package com.falak.qa.pages.corpora;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.falak.qa.base.BasePage;

import io.qameta.allure.Step;

public class CorporaPage extends BasePage {

	private final String corporaUrl; // 🌍 رابط المدونات– يُعاد عند الطلب
	// The base URL of the home page, useful for navigation and validations.

	private By corporaCards = By.cssSelector("div.item-details");

	// 📦 المُنشئ (Constructor)
	// يُستخدم لتمرير الـ WebDriver إلى الكلاس الأب BasePage.
	public CorporaPage(WebDriver driver) {
		super(driver);
		this.corporaUrl = com.falak.qa.config.EnvironmentConfigLoader.getUrl("corporaUrl");
	}

	/**
	 * 🌐 إرجاع رابط صفحة المدونات
	 * 
	 * Returns the base URL of the website as defined in the environment
	 * configuration.
	 *
	 * @return String - رابط المدونات (Corpora URL of the application)
	 */
	@Step("Get base URL")
	public String getCorporaUrl() {
		return this.corporaUrl;
	}

	/**
	 * ✅ التحقّق من تحميل صفحة المدونات
	 * 
	 * Verifies that the Corpora page has successfully loaded by checking the title.
	 *
	 * @return true إذا كان عنوان الصفحة يحتوي على "الرئيسية"، false في حال وجود خطأ
	 *         true if the page title contains "الرئيسية"; false otherwise
	 */
	@Step("Verify Corpora Page loaded")
	public boolean isHomePageLoaded() {
		try {
			return getPageTitle().contains("الرئيسية");
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 👁️ التحقّق من ظهور قسم "المدونات"
	 * 
	 * Checks whether the "Corpora" section is visible to the user on the Corpora
	 * Page.
	 *
	 * @return true إذا كان القسم ظاهرًا على الصفحة، false إذا لم يكن ظاهرًا أو حدث
	 *         خطأ true if the "Features" section is visible; false otherwise
	 */
	@Step("Is ‘Corpora’ section visible")
	public boolean isCorporaVisible() {
		try {
			return isElementVisible(corporaCards);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 🔢 الحصول على عدد بطاقة المدونات
	 * 
	 * Returns the number of visible Corpora cards.
	 *
	 * @return عدد البطاقات الظاهرة، أو -1 إذا لم يتمكن من جلب العدد Number of
	 *         Corpora cards shown; returns -1 if unable to count
	 */
	@Step("Get count of Corpora cards")
	public int getCorporaCardsCount() {
		try {
			return countElements(corporaCards);
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * 🔄 جلب كل بطاقات المدونات Retrieves all corpora cards and wraps them in
	 * {@link CorporaCardComponent} objects.
	 *
	 * @return قائمة كائنات {@link CorporaCardComponent} لتسهيل التفاعل معها
	 * @throws RuntimeException في حال فشل تحميل البطاقات
	 */
	@Step("📥 Get all corpora card components from the corpora page")
	public List<CorporaCardComponent> getAllCorporaCards() {
		try {
			waitForElement(corporaCards);
			List<WebElement> cards = driver.findElements(corporaCards);
			List<CorporaCardComponent> cardComponents = new ArrayList<>();
			for (WebElement card : cards) {
				cardComponents.add(new CorporaCardComponent(card, driver));
			}
			return cardComponents;
		} catch (Exception e) {
			throw new RuntimeException("❌ Failed to retrieve corpora card components", e);
		}
	}

	/**
	 * 🔍 جلب بطاقة مدونة محددة حسب الاسم (من الـ Enum)
	 *
	 * Retrieves a specific Corpora card by matching its title text to the provided
	 * enum value.
	 *
	 * @param targetCorpora اسم المدونة المطلوبة (from enum CorporaName)
	 * @return كائن {@link CorporaCardComponent} إذا تم العثور عليها، null إذا لم
	 *         توجد
	 */
	@Step("🔍 Get corpora card by corpus name: {0}")
	public CorporaCardComponent getCorporaCardComponentByName(String targetCorpora) {
		List<CorporaCardComponent> allCards = getAllCorporaCards();
		for (CorporaCardComponent card : allCards) {
			String cardTitle = card.getTitle();
			if (cardTitle != null && cardTitle.trim().equalsIgnoreCase(targetCorpora)) {
				return card;
			}
		}
		return null;
	}

}
