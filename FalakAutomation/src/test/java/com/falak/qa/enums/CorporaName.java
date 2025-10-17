package com.falak.qa.enums;

public enum CorporaName {

	MAJMAA("مدونة المجمع للغة العربية المعاصرة", "babe4d42-221a-4fc1-8ce9-03bd3fa92dc1"),
	USAGE_GUIDES("مدونة أدلة الاستخدام", "355943e7-8413-4b88-ba8d-78ef7c873f4d"),
	CULTURAL("المدونة الثقافية", "134ae566-063c-4551-9930-dbae7b83533e"),
	SPORTS("المدونة الرياضية", "1959eefb-8bc8-4c68-9695-42812ae2d379"),
	FOLK_POETRY("مدونة الشعر الشعبي", "4ffb2ae1-0c17-444b-8653-a063fd1f14d5"),
	ENTERTAINMENT("مدونة الترفيه", "40ece28a-c948-41a9-83b7-05c81705e29d"), ARABIC("المدونة العربية", "arabiccorpus"),
	ARABIC_BOOKS("مدونة كتب العربية", "90eccba1-2056-48f4-ab51-79b9768ee7b1"),
	ARABIC_LEARNERS("المدونة اللغوية لمتعلمي اللغة العربية", "b7265808-6dca-4375-be02-cd72f13c1e8e"),
	DIALECT_NOVELS("الروايات العامية", "07661dc5-10bd-48a0-8796-757b2436307f"),
	QURAN("مدونة القرآن الكريم", "e691881d-b532-40ab-9f47-d1308222990b"),
	CAMELS("مدونة الإبل", "9692af45-5565-4ea2-8d8f-a6c08e42734b");
	// LEGAL_SYSTEMS("مدونة الأنظمة", "uuid-13"),
	// BUSINESS("مدونة المال والأعمال", "uuid-14");

	private final String displayName;
	private final String uuid;

	/**
	 * 🏷️ المُهيّئ (Constructor) الخاص بالقيمة التعدادية `CorporaName`
	 *
	 * 🔹 يُستخدم لإنشاء كائن جديد من نوع `CorporaName` مع قيمتين أساسيتين: -
	 * `displayName` : الاسم الظاهر للمدونة (مثلاً: "المجمع"). - `uuid` : المعرّف
	 * الفريد للمدونة.
	 *
	 * 🏷️ Constructor for the `CorporaName` enum. Initializes an enum constant
	 * with: - `displayName` : the user-facing name of the corpus. - `uuid` : the
	 * unique identifier of the corpus.
	 *
	 * @param displayName الاسم الظاهر للمدونة | The human-readable display name
	 * @param uuid        المعرّف الفريد للمدونة | The unique identifier (UUID)
	 *
	 *                    📌 الهدف: ربط كل قيمة تعدادية باسم ظاهر ومعرّف فريد يمكن
	 *                    استخدامه في الـ UI أو الاستدعاءات.
	 */
	CorporaName(String displayName, String uuid) {
		this.displayName = displayName;
		this.uuid = uuid;
	}

	/**
	 * 📝 يُرجع الاسم الظاهر للمدونة
	 *
	 * 🔹 يُستخدم عادةً عند عرض أسماء المدونات في واجهة المستخدم أو في تقارير
	 * الاختبار.
	 *
	 * 📝 Returns the display name of the corpus. Useful for showing corpus names in
	 * the UI or reporting.
	 *
	 * @return الاسم الظاهر للمدونة | The display name of the corpus
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * 🆔 يُرجع المعرّف الفريد (UUID) للمدونة
	 *
	 * 🔹 يُستخدم عند استدعاء واجهات الـ API أو عند التعامل مع قاعدة البيانات
	 * للتعرّف على المدونة بشكل دقيق.
	 *
	 * 🆔 Returns the unique identifier (UUID) of the corpus. Typically required for
	 * API calls or internal references.
	 *
	 * @return المعرّف الفريد | The UUID of the corpus
	 */
	public String getUuid() {
		return uuid;
	}

}
