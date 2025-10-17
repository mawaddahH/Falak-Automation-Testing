package com.falak.qa.pages.corpora;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import com.falak.qa.base.BasePage;
import com.falak.qa.enums.ToolsName;

public class CorporaOverviewPage extends BasePage {

	/* 🔽 قائمة اختيار المدونة (Label داخل p-dropdown) */
	private final By corporaDropdown = By
			.xpath("//app-corpus-switcher//p-dropdown//span[contains(@class,'p-dropdown-label')]");

	/* ℹ️ زر عرض المعلومات (أيقونة info) */
	private final By infoButton = By.xpath("//img[contains(@src,'icon-info') and @ptooltip='التفاصيل']");

	/* 🔍 خانة البحث داخل القائمة المنسدلة */
	private final By dropdownSearchInput = By.xpath("//p-dropdown//input[contains(@class,'p-dropdown-filter')]");

	/* ────────── عنوان المدونة ووصفها ────────── */
	private final By corporaTitle = By.xpath("//div[contains(@class,'surface-50')]//span[normalize-space()]");
	private final By corporaDescription = By
			.xpath("//p[contains(@class,'font-medium') and contains(@class,'mb-3')]/span[contains(@class,'text-500')]");

	/* ───────────ـ البحث داخل المدونة ─────────── */
	private final By searchInput = By.xpath("//input[@placeholder='ابحث في المدونة']");
	private final By searchButton = By.xpath("//a[@label='بحث' and contains(@class,'p-button')]");

	/* ───────────── عنوان قسم الإحصائيات ───────────── */
	private final By statisticsTitle = By
			.xpath("//h3[contains(@class,'sec-title') and normalize-space()='الإحصائيات']");

	/* زر لوحة المفاتيح الافتراضية */
	private final By virtualKeyboardButton = By.xpath("//app-virtual-keyboard//img[@ptooltip]");

	/* ─────────────── إحصاءات عدد النصوص ─────────────── */
	private final By textsIcon = By
			.xpath(".//div[contains(@class,'item-status')]//img[contains(@src,'icon-documents')]");
	private final By textsLabel = By.xpath(
			".//div[contains(@class,'item-status')]//img[contains(@src,'icon-documents')]/following-sibling::p[contains(@class,'text-xs')]");
	private final By textsValue = By.xpath(
			".//div[contains(@class,'item-status')]//img[contains(@src,'icon-documents')]/following-sibling::p[contains(@class,'text-700')]");

	/* ─────────────── إحصاءات عدد الكلمات ─────────────── */
	private final By wordsIcon = By.xpath(
			".//div[contains(@class,'item-status')]//img[contains(@src,'icon-words') and not(contains(@src,'nofreq'))]");
	private final By wordsLabel = By.xpath(
			".//div[contains(@class,'item-status')]//img[contains(@src,'icon-words') and not(contains(@src,'nofreq'))]/following-sibling::p[contains(@class,'text-xs')]");
	private final By wordsValue = By.xpath(
			".//div[contains(@class,'item-status')]//img[contains(@src,'icon-words') and not(contains(@src,'nofreq'))]/following-sibling::p[contains(@class,'text-700')]");

	/* ─────────── الكلمات بدون تكرار ─────────── */
	private final By uniqueIcon = By
			.xpath(".//div[contains(@class,'item-status')]//img[contains(@src,'icon-words-nofreq')]");
	private final By uniqueLabel = By.xpath(
			".//div[contains(@class,'item-status')]//img[contains(@src,'icon-words-nofreq')]/following-sibling::p[contains(@class,'text-xs')]");
	private final By uniqueValue = By.xpath(
			".//div[contains(@class,'item-status')]//img[contains(@src,'icon-words-nofreq')]/following-sibling::p[contains(@class,'text-700')]");

	/* عنوان «الكلمات الأكثر بحثاً» وسحابة الوسوم */
	private final By mostSearchedTitle = By
			.xpath("//h3[contains(@class,'sec-title') and contains(normalize-space(),'الأكثر بحثاً')]");
	private final By tagCloudContainer = By.xpath("//angular-tag-cloud");

	// 📦 Constructor المُنشئ
	public CorporaOverviewPage(WebDriver driver) {
		super(driver);
	}

	/**
	 * 🔍 الحصول على المحدد (Locator) الخاص بزر الأداة في القائمة اليمنى
	 * 
	 * @param tool نوع الأداة (من enum ToolsName)
	 * @return محدد CSS للزر
	 */
	public By getToolCardLocator(ToolsName tool) {
		return By.cssSelector("a[href*='/" + tool.getPathSegment() + "/']");
	}

	/**
	 * 🖱️ الضغط على أداة معينة داخل قائمة أدوات المدونة
	 * 
	 * @param tool نوع الأداة (من enum ToolsName)
	 */
	public void clickOnToolCard(ToolsName tool) {
		try {
			waitAndClick(getToolCardLocator(tool));
		} catch (NoSuchElementException e) {
			throw new RuntimeException("⚠️ لم يتم العثور على الأداة في الصفحة: " + tool.getArabicName(), e);
		}
	}

	/**
	 * 🏷️ الحصول على اسم المدونة المعروضة في الصفحة This method returns the name of
	 * the currently viewed corpus.
	 * 
	 * @return النص الظاهر لاسم المدونة
	 */
	public String getTitleText() {
		try {
			return waitForElement(corporaTitle).getText().trim();
		} catch (NoSuchElementException e) {
			throw new RuntimeException("❌ لم يتم العثور على اسم المدونة داخل الصفحة!", e);
		}
	}

}
