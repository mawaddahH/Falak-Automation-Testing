package com.falak.qa.enums;

/**
 * 🛠️ أنواع الأدوات المتوفرة في منصة فلك | Tool Types in Falak Platform
 */
public enum ToolsName {
	CONCORDANCER("الكشاف السياقي", "concordancer"), WORDS_FREQUENCY_LISTS("قوائم الشيوع", "words-frequency-lists"),
	NGRAMS("التتابعات اللَّفظيَّة", "ngrams"), WORDS_BEFORE_AFTER("الكلمات السابقة واللاحقة", "words-before-after"),
	PREFIXES_SUFFIXES("السوابق واللواحق", "prefixes-and-suffixes"), COLLOCATION("التصاحب اللفظي", "collocation"),
	FREQUENCY_DISTRIBUTION("توزيع التكرار", "frequency-distribution"), EXAMPLES_SEARCH("البحث عن أمثلة", "search"),
	KEYWORDS_EXTRACTION("استخلاص المصطلحات", "keywords"), STATISTICS("الإحصائيات", "statistics");

	private final String arabicName; // الاسم العربي الظاهر
	private final String pathSegment; // اسم المسار في الرابط URL

	/**
	 * 🏷️ المُهيّئ (Constructor) الخاص بالقيمة التعدادية `ToolsName`
	 *
	 * 🔹 يُستخدم لإنشاء كائن جديد من نوع `ToolsName` مع قيمتين أساسيتين: -
	 * `arabicName` : الاسم العربي للأداة (يظهر في الـ UI أو التقارير). -
	 * `pathSegment` : جزء الرابط (URL segment) الذي يُستخدم للوصول إلى الأداة عبر
	 * المتصفح أو الـ API.
	 *
	 * 🏷️ Constructor for the `ToolsName` enum. Initializes an enum constant with:
	 * - `arabicName` : the Arabic display name of the tool. - `pathSegment` : the
	 * corresponding URL segment used for routing or API calls.
	 *
	 * @param arabicName  الاسم العربي للأداة | The Arabic display name
	 * @param pathSegment جزء الرابط الخاص بالأداة | The path segment (URL part)
	 *
	 *                    📌 الهدف: ربط كل أداة باسم عربي ظاهر ومعرّف مسار URL
	 *                    لاستخدامه في التوجيه أو الاختبارات.
	 */
	ToolsName(String arabicName, String pathSegment) {
		this.arabicName = arabicName;
		this.pathSegment = pathSegment;
	}

	/**
	 * 📝 يُرجع الاسم العربي للأداة
	 *
	 * 🔹 يُستخدم عادةً في واجهة المستخدم أو تقارير Allure لعرض أسماء الأدوات باللغة
	 * العربية.
	 *
	 * 📝 Returns the Arabic display name of the tool. Useful for showing tool names
	 * in the UI or test reports.
	 *
	 * @return الاسم العربي للأداة | The Arabic display name
	 */
	public String getArabicName() {
		return arabicName;
	}

	/**
	 * 🔗 يُرجع جزء الرابط (URL segment) الخاص بالأداة
	 *
	 * 🔹 يُستخدم عند تكوين روابط الأدوات أو استدعاءات الـ API للتنقل مباشرة إلى
	 * صفحة الأداة.
	 *
	 * 🔗 Returns the URL path segment of the tool. Typically required when
	 * constructing tool URLs or API endpoints.
	 *
	 * @return جزء الرابط للأداة | The URL path segment
	 */
	public String getPathSegment() {
		return pathSegment;
	}

}
