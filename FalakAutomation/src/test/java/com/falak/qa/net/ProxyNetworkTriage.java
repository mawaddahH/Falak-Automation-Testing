package com.falak.qa.net;

import com.browserup.bup.BrowserUpProxy;
import com.browserup.harreader.model.HarEntry;

import io.qameta.allure.Allure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProxyNetworkTriage implements NetworkTriage {
	private final BrowserUpProxy proxy;
	private volatile List<Pattern> filters = List.of();
	private List<TriageEvent> latest = new ArrayList<>();

	/**
	 * 🌐 مُنشئ ProxyNetworkTriage باستخدام BrowserUpProxy
	 *
	 * 🌐 Constructor for ProxyNetworkTriage using BrowserUpProxy.
	 *
	 * @param proxy كائن البروكسي المستخدم لاعتراض وتحليل الشبكة | The
	 *              BrowserUpProxy instance used for network interception and
	 *              analysis
	 */
	public ProxyNetworkTriage(BrowserUpProxy proxy) {
		this.proxy = proxy;
	}

	/**
	 * ▶️ بدء الرصد عبر إنشاء HAR جديد
	 *
	 * ▶️ Starts network monitoring by creating a new HAR.
	 *
	 * 📌 هذا يلتقط جميع الطلبات/الاستجابات منذ لحظة البدء. 📌 This captures all
	 * requests/responses from the moment monitoring starts.
	 */
	@Override
	public void start() {
		proxy.newHar("triage"); // يبدأ تسجيل HAR
		Allure.step("▶️ ProxyNetworkTriage started with new HAR session");
	}

	/**
	 * ⏹️ إيقاف الرصد (لا يقوم بعملية فعلية هنا)
	 *
	 * ⏹️ Stops network monitoring (no actual action performed here).
	 *
	 * 📌 تُترك عملية القراءة لاحقًا عند الحاجة. 📌 The HAR is read on-demand, no
	 * stop operation required.
	 */
	@Override
	public void stop() {
		Allure.step("⏹️ ProxyNetworkTriage.stop() — nothing to stop, HAR will be read later");
	}

	/**
	 * 🧹 مسح الأحداث والبدء بجلسة HAR جديدة
	 *
	 * 🧹 Clears captured events and starts a new HAR session.
	 *
	 * 📌 يتم أيضًا تفريغ قائمة الأحداث الأخيرة. 📌 Also clears the latest captured
	 * events list.
	 */
	@Override
	public void clear() {
		proxy.newHar("triage-clear");
		latest.clear();
		Allure.step("🧹 Cleared captured events and reset HAR");
	}

	/**
	 * 🎯 تهيئة الفلاتر باستخدام تعبيرات Regex (لرصد روابط محددة فقط)
	 *
	 * 🎯 Arms the network filters using provided regex patterns (to monitor only
	 * specific URLs).
	 *
	 * @param urlRegex قائمة تعبيرات Regex لتصفية الطلبات | List of regex patterns
	 *                 to filter requests
	 */
	@Override
	public void arm(String... urlRegex) {
		if (urlRegex == null || urlRegex.length == 0) {
			this.filters = List.of(); // لا فلترة = كل شيء
			Allure.step("🎯 No filters applied — capturing all network traffic");
		} else {
			List<Pattern> ps = new ArrayList<>();
			for (String r : urlRegex)
				ps.add(Pattern.compile(r));
			this.filters = ps;
			Allure.step("🎯 Applied network filters: " + Arrays.toString(urlRegex));
		}
	}

	/**
	 * 🚨 استرجاع أول خطأ (إن وُجد) من قائمة الأحداث
	 *
	 * 🚨 Retrieves the first error (if any) from the collected events.
	 *
	 * @return كائن TriageEvent داخل Optional | Optional containing the first
	 *         TriageEvent if present
	 */
	@Override
	public Optional<TriageEvent> firstError() {
		collect();
		Optional<TriageEvent> first = latest.stream().findFirst();
		Allure.step("🚨 First error retrieved: " + (first.isPresent() ? first.get().toString() : "No errors found"));
		return first;
	}

	/**
	 * 📋 استرجاع جميع الأخطاء المجمعة من الشبكة
	 *
	 * 📋 Retrieves all captured errors from the network events.
	 *
	 * @return قائمة غير قابلة للتغيير من TriageEvent | Unmodifiable list of all
	 *         captured TriageEvents
	 */
	@Override
	public List<TriageEvent> errors() {
		collect();
		Allure.step("📋 Retrieved total errors: " + latest.size());
		return List.copyOf(latest);
	}

	/**
	 * 💾 حفظ ملف HAR إن كان مدعومًا
	 *
	 * 💾 Saves the HAR file if supported by the proxy.
	 *
	 * @param out مسار الإخراج حيث سيتم حفظ HAR | Path where the HAR file should be
	 *            saved
	 * @throws IOException إذا فشل الحفظ | If saving the HAR fails
	 */
	@Override
	public void saveHarIfSupported(Path out) throws IOException {
		var har = proxy.getHar();
		if (har != null) {
			Files.createDirectories(out.getParent());
			har.writeTo(out.toFile());
			Allure.step("💾 HAR saved successfully to: " + out.toAbsolutePath());
		} else {
			Allure.step("⚠️ No HAR available to save.");
		}
	}

	/**
	 * 🧮 جمع وتحويل بيانات HAR إلى أحداث TriageEvent
	 *
	 * 🧮 Collects and transforms HAR data into TriageEvent objects.
	 *
	 * 📌 يقوم بفلترة الروابط، ويرصد فقط الاستجابات التي تحمل كود ≥ 400. 📌 Filters
	 * URLs and records only responses with status ≥ 400.
	 */
	private void collect() {
		var har = proxy.getHar();
		if (har == null) {
			latest = List.of();
			Allure.step("⚠️ No HAR available — no events collected.");
			return;
		}

		List<TriageEvent> res = new ArrayList<>();
		for (HarEntry e : har.getLog().getEntries()) {
			String url = e.getRequest().getUrl();
			if (!matches(url))
				continue;

			int status = e.getResponse().getStatus();
			if (status >= 400) {
				res.add(new TriageEvent(TriageEvent.Kind.RESPONSE_ERROR, url, e.getRequest().getMethod().name(), status,
						null, e.getResponse().getBodySize() == null ? 0 : e.getResponse().getBodySize(),
						Instant.ofEpochMilli(e.getStartedDateTime().getTime())));
				Allure.step("🚨 Captured error: " + url + " (status " + status + ")");
			}
		}

		latest = res.stream().sorted(Comparator.comparing((TriageEvent t) -> t.time)).collect(Collectors.toList());
		Allure.step("🧮 Total collected events: " + latest.size());
	}

	/**
	 * 🔍 التحقق مما إذا كان الرابط يطابق أي من الفلاتر المحددة
	 *
	 * 🔍 Checks if the given URL matches any of the defined regex filters.
	 *
	 * @param url الرابط المستهدف | The URL to check
	 * @return true إذا طابق أي فلتر، false خلاف ذلك | true if matches, false
	 *         otherwise
	 */
	private boolean matches(String url) {
		if (filters.isEmpty()) {
			return true; // لا فلاتر = الكل مسموح
		}
		for (Pattern p : filters) {
			if (p.matcher(url).find()) {
				return true;
			}
		}
		return false;
	}

}
