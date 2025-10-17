package com.falak.qa.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;

import org.openqa.selenium.devtools.v136.browser.Browser;
import org.openqa.selenium.devtools.v136.browser.model.DownloadProgress;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class DownloadsCdpHelper {

	/**
	 * ⬇️ يفعّل إشعارات التحميل لمتصفح Chrome/Edge ويضبط مسار التحميل
	 *
	 * ⬇️ Arms Chrome/Edge download notifications and configures the download
	 * directory.
	 *
	 * <p>
	 * ✅ المزايا | Features:
	 * <ul>
	 * <li>تفعيل إشعاري {@code downloadWillBegin} و {@code downloadProgress} عبر
	 * DevTools</li>
	 * <li>تعيين مجلد التحميل إلى {@code downloadDir}</li>
	 * <li>تحديث {@code completedFlag} تلقائياً عند اكتمال/إلغاء التحميل</li>
	 * <li>إرجاع {@code Runnable} اختياري لتعطيل السلوك لاحقاً</li>
	 * </ul>
	 * </p>
	 *
	 * @param driver        كائن WebDriver (يجب أن يدعم DevTools) | WebDriver
	 *                      instance (must support DevTools)
	 * @param downloadDir   مجلد التحميل | Target directory for downloads
	 * @param completedFlag علم يُضبط عند اكتمال/إلغاء التحميل | Flag set when
	 *                      download completes/cancels
	 * @return {@code Optional<Runnable>} لتعطيل السلوك لاحقاً، أو
	 *         {@code Optional.empty()} إن لم يُدعم DevTools
	 * @throws RuntimeException إذا فشل تهيئة جلسة DevTools أو ضبط سلوك التحميل
	 */
	@Step("⬇️ Arm Chrome/Edge download notifications and configure download directory")
	public static Optional<Runnable> armChromeDownloadDone(WebDriver driver, Path downloadDir,
			AtomicBoolean completedFlag) {
		try {
			if (!(driver instanceof HasDevTools)) {
				Allure.step("⚠️ Driver does not support DevTools. Skipping download listeners setup.");
				return Optional.empty();
			}

			DevTools devTools = ((HasDevTools) driver).getDevTools();
			devTools.createSession();
			Allure.step("🧩 DevTools session created");

			// مهم: eventsEnabled=true حتى نحصل على downloadProgress/downloadWillBegin
			devTools.send(Browser.setDownloadBehavior(Browser.SetDownloadBehaviorBehavior.ALLOW, // بدل ALLOWANDNAME
					Optional.empty(), Optional.ofNullable(downloadDir == null ? null : downloadDir.toString()),
					Optional.of(true) // eventsEnabled
			));
			Allure.step("📂 Download behavior set to ALLOW with eventsEnabled=true, dir="
					+ (downloadDir == null ? "(browser default)" : downloadDir));

			// إشعار بداية التحميل
			devTools.addListener(Browser.downloadWillBegin(), e -> {
				String file = e.getSuggestedFilename();
				String url = e.getUrl();
				System.out.println("⬇️ Download will begin: name=" + file + " | url=" + url);
				Allure.step("⬇️ Download will begin → file: " + file + " | url: " + url);
			});

			// إشعار التقدم/الاكتمال
			devTools.addListener(Browser.downloadProgress(), e -> {
				DownloadProgress.State st = e.getState();
				if (st == DownloadProgress.State.COMPLETED || st == DownloadProgress.State.CANCELED) { // اعتبر الملغي
																										// نهاية أيضاً
																										// إن رغبت
					completedFlag.set(true);
					System.out.println("✅ Download " + st + " (guid=" + e.getGuid() + ")");
					Allure.step("✅ Download state: " + st + " | guid=" + e.getGuid());
				}
			});

			// دالة إرجاع لتعطيل السلوك لاحقًا (اختياري)
			return Optional.of(() -> {
				try {
					devTools.send(Browser.setDownloadBehavior(Browser.SetDownloadBehaviorBehavior.DEFAULT,
							Optional.empty(), Optional.empty(), Optional.of(false)));
					Allure.step("🛑 Download behavior reset to DEFAULT (events disabled)");
				} catch (Exception ignore) {
					System.out.println("⚠️ Failed to reset download behavior to DEFAULT: " + ignore.getMessage());
				}
			});

		} catch (Exception e) {
			Allure.step("❌ Failed to arm Chrome/Edge download notifications: " + e.getMessage());
			throw new RuntimeException("❌ Failed to arm Chrome/Edge download notifications", e);
		}
	}

}
