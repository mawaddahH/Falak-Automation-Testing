package com.falak.qa.net;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;

import org.openqa.selenium.devtools.v136.network.Network;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class CdpNetworkTriage implements NetworkTriage {
	private final DevTools devTools;
	private final List<TriageEvent> events = Collections.synchronizedList(new ArrayList<>());
	private volatile List<Pattern> filters = List.of();
	private boolean started = false;

	/**
	 * 🛠️ المُنشئ: يربط WebDriver مع DevTools
	 * 
	 * 🛠️ Constructor: Initializes connection between WebDriver and Chrome DevTools
	 * Protocol (CDP)
	 *
	 * @param driver كائن WebDriver المستخدم | The WebDriver instance
	 */
	public CdpNetworkTriage(WebDriver driver) {
		try {
			this.devTools = ((HasDevTools) driver).getDevTools();
			Allure.step("✅ DevTools session prepared successfully");
		} catch (Exception e) {
			String msg = "❌ Failed to initialize DevTools from WebDriver";
			Allure.attachment("CdpNetworkTriage Constructor Error", msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * ▶️ بدء الاستماع لأحداث الشبكة عبر CDP
	 *
	 * ▶️ Starts listening for network events using Chrome DevTools Protocol. Adds
	 * listeners for response errors (status >= 400) and loading failures.
	 *
	 * 📌 الهدف: جمع الأحداث (Triage Events) الخاصة بفشل التحميل أو أخطاء الاستجابة.
	 */
	@Override
	@Step("▶️ Start CDP network monitoring")
	public void start() {
		try {
			if (started)
				return;

			// إنشاء جلسة جديدة وتفعيل مراقبة الشبكة
			devTools.createSession();
			devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
			Allure.step("✅ CDP Network session enabled");

			// مراقبة أخطاء الاستجابة (status >= 400)
			devTools.addListener(Network.responseReceived(), evt -> {
				var r = evt.getResponse();
				long len = (r.getEncodedDataLength() == null) ? 0L : r.getEncodedDataLength().longValue();
				if (matches(r.getUrl()) && r.getStatus().intValue() >= 400) {
					events.add(TriageEvent.responseError(r.getUrl(), evt.getRequestId().toString(),
							r.getStatus().intValue(), len, Instant.now()));
					Allure.step("🚨 Response error detected: " + r.getUrl() + " | Status: " + r.getStatus());
				}
			});

			// مراقبة فشل التحميل
			devTools.addListener(Network.loadingFailed(), evt -> {
				String id = evt.getRequestId().toString(); // URL قد لا يكون متاحًا
				if (matches(id)) {
					events.add(TriageEvent.loadFailed(id, evt.getType().toString(), evt.getErrorText(), Instant.now()));
					Allure.step("⚠️ Loading failed detected: " + evt.getErrorText());
				}
			});

			started = true;
			Allure.step("✅ CDP network monitoring started");

		} catch (Exception e) {
			String msg = "❌ Failed to start CDP network monitoring";
			Allure.attachment("CdpNetworkTriage Start Error", msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * ⏹️ إيقاف مراقبة الشبكة عبر CDP
	 *
	 * ⏹️ Stops monitoring network events by disabling the CDP Network domain.
	 */
	@Override
	@Step("⏹️ Stop CDP network monitoring")
	public void stop() {
		try {
			if (!started)
				return;

			// تعطيل مراقبة الشبكة
			devTools.send(Network.disable());
			Allure.step("✅ CDP Network session disabled");

		} catch (Exception e) {
			Allure.step("⚠️ Failed to disable CDP Network monitoring gracefully: " + e.getMessage());
		} finally {
			started = false;
		}
	}

	/**
	 * 🧹 تفريغ أحداث المراقبة الحالية
	 *
	 * 🧹 Clears all currently collected triage events from memory.
	 *
	 * 📌 يُستخدم لبدء جلسة مراقبة جديدة دون بقايا من جلسة سابقة.
	 */
	@Override
	@Step("🧹 Clear collected triage events")
	public void clear() {
		events.clear();
		Allure.step("✅ Triage events cleared");
	}

	/**
	 * 🎯 تفعيل الفلاتر الخاصة بعناوين/مسارات الشبكة (Regex)
	 *
	 * 🎯 Arms network monitoring with optional URL regex filters. إذا لم يتم تمرير
	 * أي فلاتر → ستتم مطابقة جميع الطلبات.
	 *
	 * @param urlRegex أنماط Regex لتصفية عناوين الطلبات | Optional URL regex
	 *                 filters
	 */
	@Override
	@Step("🎯 Arm triage with URL filters")
	public void arm(String... urlRegex) {
		if (urlRegex == null || urlRegex.length == 0) {
			this.filters = List.of(); // لا فلترة = كل شيء
			Allure.step("ℹ️ No URL filters provided. Monitoring ALL requests.");
		} else {
			List<Pattern> ps = new ArrayList<>();
			for (String r : urlRegex) {
				ps.add(Pattern.compile(r));
			}
			this.filters = ps;
			Allure.step("✅ Armed with " + urlRegex.length + " URL regex filter(s)");
		}
	}

	/**
	 * 🔎 التحقق مما إذا كان العنوان يطابق أي فلتر مفعل
	 *
	 * 🔎 Checks whether the given URL matches any of the active regex filters. إذا
	 * لم تكن هناك فلاتر مفعّلة → تُعتبر المطابقة صحيحة للجميع.
	 *
	 * @param url العنوان المراد اختباره | URL to test
	 * @return true إذا كان يطابق أي فلتر، أو إذا لم توجد فلاتر | true if matches or
	 *         if no filters
	 */
	@Step("🔎 Match URL against active filters")
	private boolean matches(String url) {
		if (filters.isEmpty()) {
			// لا توجد فلاتر = قبول جميع العناوين
			return true;
		}
		for (Pattern p : filters) {
			if (p.matcher(url).find()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 🚨 الحصول على أول خطأ مُسجَّل (إن وُجد)
	 *
	 * 🚨 Retrieves the first recorded triage error event (if available).
	 *
	 * 📌 يُستخدم لمراجعة أول مشكلة شبكة تم التقاطها أثناء الجلسة.
	 *
	 * @return أول حدث خطأ داخل Optional | The first error event wrapped in Optional
	 */
	@Override
	@Step("🚨 Retrieve first triage error event")
	public Optional<TriageEvent> firstError() {
		synchronized (events) {
			Optional<TriageEvent> first = events.stream().findFirst();
			if (first.isPresent()) {
				Allure.step("✅ First error event found: " + first.get().toString());
			} else {
				Allure.step("ℹ️ No error events recorded");
			}
			return first;
		}
	}

	/**
	 * 📋 استرجاع جميع أخطاء الشبكة المُسجَّلة
	 *
	 * 📋 Retrieves all collected triage error events.
	 *
	 * 📌 يُفيد في تتبع كامل الأخطاء (مثل فشل استجابات HTTP أو أخطاء تحميل).
	 *
	 * @return قائمة غير قابلة للتعديل من جميع أحداث الخطأ | Immutable list of all
	 *         error events
	 */
	@Override
	@Step("📋 Retrieve all collected triage error events")
	public List<TriageEvent> errors() {
		synchronized (events) {
			List<TriageEvent> allErrors = List.copyOf(events);
			Allure.step("📊 Total error events retrieved: " + allErrors.size());
			return allErrors;
		}
	}

}
