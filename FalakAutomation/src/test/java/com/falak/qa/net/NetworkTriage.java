package com.falak.qa.net;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;

public interface NetworkTriage extends AutoCloseable {
	void start(); // تفعيل الالتقاط (CDP/Proxy)

	void stop(); // إيقاف الالتقاط

	void clear(); // مسح أي أحداث قديمة

	void arm(String... urlRegex); // فلترة على مسارات مهمة (اختياري)

	Optional<TriageEvent> firstError(); // أول خطأ 4xx/5xx أو فشل تحميل

	List<TriageEvent> errors(); // كل الأخطاء التي تم التقاطها

	/**
	 * 💾 محاولة حفظ ملف HAR إذا كانت الأداة تدعم ذلك
	 *
	 * 💾 Attempts to save HAR (HTTP Archive) file if supported by the triage tool.
	 *
	 * 📌 يُستخدم لتوثيق جميع طلبات واستجابات الشبكة أثناء الاختبار.
	 *
	 * @param out المسار المستهدف لحفظ الملف | The output path where HAR should be
	 *            saved
	 * @throws IOException في حال حدوث مشكلة بالكتابة | If writing to file fails
	 */
	@Step("💾 Save HAR file if supported")
	default void saveHarIfSupported(Path out) throws IOException {
		// ⚠️ الافتراضي: لا يوجد دعم لحفظ HAR
		Allure.step("ℹ️ saveHarIfSupported not implemented for this triage tool.");
	}

	/**
	 * 🛑 إغلاق موارد Triage الحالية وإيقاف المراقبة
	 *
	 * 🛑 Closes the current triage session and stops any active listeners.
	 *
	 * 📌 يُستخدم في نهاية الاختبار لضمان تحرير جميع الموارد المرتبطة بالشبكة.
	 */
	@Override
	@Step("🛑 Close triage and release resources")
	default void close() {
		try {
			stop();
			Allure.step("✅ Triage stopped and resources released successfully.");
		} catch (Exception e) {
			Allure.step("⚠️ Failed to close triage properly: " + e.getMessage());
			throw new RuntimeException("❌ Error while closing triage", e);
		}
	}

}
