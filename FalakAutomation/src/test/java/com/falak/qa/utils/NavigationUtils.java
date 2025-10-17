package com.falak.qa.utils;

import com.falak.qa.config.EnvironmentConfigLoader;
import com.falak.qa.enums.CorporaName;
import com.falak.qa.enums.ToolsName;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;

public class NavigationUtils {
	/**
	 * 🌐 يبني الرابط الكامل لأداة معينة ضمن مجموعة النصوص المحددة 🌐 Builds the
	 * full tool URL for a given corpora and tool
	 *
	 * @param corpora مجموعة النصوص الهدف (The target corpora)
	 * @param tool    الأداة المراد الوصول إليها (The target tool)
	 * @return الرابط الكامل للوصول إلى الأداة (The constructed tool URL as String)
	 */
	@Step("🌐 Build tool URL for corpora: {corpora} and tool: {tool}")
	public static String buildToolUrl(CorporaName corpora, ToolsName tool) {
		try {
			// 🧩 تركيب الرابط باستخدام القيم المُحددة
			// Format the URL using baseUrl, tool path, and corpora UUID
			String url = String.format("%s%s/%s", EnvironmentConfigLoader.getUrl("baseUrl"), tool.getPathSegment(),
					corpora.getUuid());

			// 📝 تسجيل الرابط في Allure والتصحيح
			Allure.step("🔗 Built tool URL: " + url);
			System.out.println("🔗 Built tool URL: " + url);

			return url;
		} catch (Exception e) {
			// ⚠️ في حالة حدوث خطأ أثناء بناء الرابط
			// Handle any error during URL construction
			throw new RuntimeException("❌ Failed to build tool URL", e);
		}
	}

}
