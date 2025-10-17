package com.falak.qa.net;

import java.util.List;
import java.util.Optional;

public class NoopNetworkTriage implements NetworkTriage {
	@Override
	/**
	 * 🚫 بدء الرصد (لا يقوم بأي شيء في وضع Noop)
	 *
	 * 🚫 Starts the network monitoring (no-op in this implementation).
	 *
	 * 📌 هذه الدالة موجودة للتماشي مع الواجهة فقط ولا تُفعّل أي مراقبة. 📌 This
	 * method exists for interface compliance; no monitoring is activated.
	 */
	public void start() {
		io.qameta.allure.Allure.step("ℹ️ NoopNetworkTriage.start() — no monitoring enabled");
	}

	@Override
	/**
	 * 🛑 إيقاف الرصد (لا يقوم بأي شيء في وضع Noop)
	 *
	 * 🛑 Stops the monitoring (no-op in this implementation).
	 *
	 * 📌 لا توجد موارد لوقفها في وضع Noop. 📌 There are no resources to stop in the
	 * noop implementation.
	 */
	public void stop() {
		io.qameta.allure.Allure.step("ℹ️ NoopNetworkTriage.stop() — nothing to stop");
	}

	@Override
	/**
	 * 🧹 مسح الأحداث الملتقطة (لا توجد أحداث في وضع Noop)
	 *
	 * 🧹 Clears captured events (there are none in noop mode).
	 *
	 * 📌 لا يتم الاحتفاظ بأي أحداث هنا؛ الاستدعاء لا يغيّر شيئًا. 📌 No events are
	 * stored; calling this has no effect.
	 */
	public void clear() {
		io.qameta.allure.Allure.step("ℹ️ NoopNetworkTriage.clear() — nothing to clear");
	}

	@Override
	/**
	 * 🎯 تهيئة فلاتر الروابط (يتم تجاهلها في وضع Noop)
	 *
	 * 🎯 Arms URL filters (ignored in noop implementation).
	 *
	 * @param urlRegex واحد أو أكثر من أنماط الـ Regex لتصفية الطلبات (سيتم تجاهلها)
	 *                 One or more regex patterns to filter requests (ignored).
	 */
	public void arm(String... urlRegex) {
		io.qameta.allure.Allure.step("ℹ️ NoopNetworkTriage.arm() — filters are ignored");
	}

	@Override
	/**
	 * ❌ جلب أول خطأ مُسجّل (دائمًا فارغ في وضع Noop)
	 *
	 * ❌ Returns the first recorded error (always empty in noop mode).
	 *
	 * @return Optional.empty() دائمًا | Always Optional.empty()
	 */
	public Optional<TriageEvent> firstError() {
		return Optional.empty();
	}

	@Override
	/**
	 * 📄 جلب قائمة كل الأخطاء المُسجّلة (دائمًا قائمة فارغة في وضع Noop)
	 *
	 * 📄 Returns all recorded errors (always an empty list in noop mode).
	 *
	 * @return قائمة غير قابلة للتعديل لكنها فارغة | An unmodifiable empty list
	 */
	public List<TriageEvent> errors() {
		return java.util.Collections.emptyList();
	}

}
