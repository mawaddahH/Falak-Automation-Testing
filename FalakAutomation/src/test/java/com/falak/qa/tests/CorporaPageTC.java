package com.falak.qa.tests;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.falak.qa.base.BaseTest;
import com.falak.qa.enums.CorporaName;
import com.falak.qa.enums.ToolsName;
import com.falak.qa.pages.corpora.CorporaOverviewPage;
import com.falak.qa.utils.CorporaToolUrlBuilder;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@Test
public class CorporaPageTC extends BaseTest {

	@Severity(SeverityLevel.CRITICAL)
	@Story("User clicks on a specific tool from corpora overview page")
	@Description("This test verifies that the user can access the 'الكلمات السابقة واللاحقة' tool from the overview page of the مجمع اللغة العربية المعاصرة corpus.")
	public void TC01_openWordsBeforeAfterTool() {

		// 🔧 إعداد المتغيرات
		CorporaName selectedCorpora = CorporaName.MAJMAA;
		ToolsName selectedTool = ToolsName.WORDS_BEFORE_AFTER;

		// 🔗 فتح صفحة نظرة عامة على المدونة
		String overviewUrl = CorporaToolUrlBuilder.buildCorporaOverviewUrl(selectedCorpora);
		driver.get(overviewUrl);

		// 🧭 إنشاء كائن الصفحة
		CorporaOverviewPage corporaPage = new CorporaOverviewPage(driver);

		// 🎯 التأكد أن الزر الخاص بالأداة موجود
		By toolLocator = corporaPage.getToolCardLocator(selectedTool);
		Assert.assertTrue(driver.findElements(toolLocator).size() > 0,
				"❌ الزر الخاص بالأداة غير موجود في هذه المدونة: " + selectedTool.getArabicName());

		// 🖱️ تنفيذ النقر
		corporaPage.clickOnToolCard(selectedTool);

		// ⏳ انتظر حتى يتغير الرابط ويحتوي على path الخاص بالأداة المطلوبة
		new WebDriverWait(driver, Duration.ofSeconds(10))
				.until(driver -> driver.getCurrentUrl().contains("/" + selectedTool.getPathSegment() + "/"));

		// 🌐 بعد الانتقال، نحصل على الرابط الحالي
		String currentUrl = driver.getCurrentUrl();
		String normalizedUrl = currentUrl.split("\\?")[0];

		// ✅ تحقق من مسار الأداة
		Assert.assertTrue(normalizedUrl.contains("/" + selectedTool.getPathSegment() + "/"),
				"❌ الرابط لا يحتوي على اسم الأداة المتوقع: " + selectedTool.getPathSegment() + "\nالرابط الحالي: "
						+ currentUrl);
		
		// ✅ التحقق من عنوان الصفحة
		String actualCorporaTitle = corporaPage.getTitleText();
		String expectedCorporaTitle = selectedTool.getArabicName();

		Assert.assertEquals(actualCorporaTitle, expectedCorporaTitle,
		    "❌ اسم المدونة الظاهر لا يطابق المدونة المختارة: " + expectedCorporaTitle);

		// ✅ تحقق من وجود UUID
		Pattern uuidPattern = Pattern.compile("[a-f0-9\\-]{36}");
		Matcher matcher = uuidPattern.matcher(normalizedUrl);
		Assert.assertTrue(matcher.find(), "❌ لم يتم العثور على UUID داخل الرابط!\nالرابط الحالي: " + currentUrl);

	}

}
