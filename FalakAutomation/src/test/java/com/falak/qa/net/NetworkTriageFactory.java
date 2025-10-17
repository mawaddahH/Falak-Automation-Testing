package com.falak.qa.net;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.HasDevTools;
import com.browserup.bup.BrowserUpProxy;

import io.qameta.allure.Allure;

public class NetworkTriageFactory {
	/**
	 * 🛠️ إنشاء كائن NetworkTriage مناسب اعتمادًا على قدرات المتصفح والبروكسي
	 *
	 * 🛠️ Creates a suitable NetworkTriage instance depending on WebDriver
	 * capabilities and proxy availability.
	 *
	 * 📌 منطق الاختيار: - إذا كان WebDriver يدعم DevTools → استخدم
	 * {@link CdpNetworkTriage} - إذا كان Proxy متوفرًا → استخدم
	 * {@link ProxyNetworkTriage} - غير ذلك → استخدم {@link NoopNetworkTriage} كبديل
	 * فارغ (safe fallback).
	 *
	 * @param driver     كائن WebDriver المستخدم | The active WebDriver instance
	 * @param proxyIfAny كائن البروكسي إن وُجد | BrowserUpProxy if provided
	 * @return NetworkTriage مناسب للاستخدام | The chosen NetworkTriage
	 *         implementation
	 */
	public static NetworkTriage create(WebDriver driver, BrowserUpProxy proxyIfAny) {
		try {
			if (driver instanceof HasDevTools) {
				Allure.step("🌐 Using CDP-based NetworkTriage (CdpNetworkTriage)");
				return new CdpNetworkTriage(driver);
			} else if (proxyIfAny != null) {
				Allure.step("🌐 Using Proxy-based NetworkTriage (ProxyNetworkTriage)");
				return new ProxyNetworkTriage(proxyIfAny);
			} else {
				Allure.step("ℹ️ Falling back to NoopNetworkTriage (no monitoring)");
				return new NoopNetworkTriage();
			}
		} catch (Throwable t) {
			Allure.step("❌ Failed to initialize NetworkTriage, falling back to Noop: " + t.getMessage());
			return new NoopNetworkTriage();
		}
	}

}
