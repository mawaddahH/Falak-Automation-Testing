package com.falak.qa.config;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.browserup.bup.BrowserUpProxy;
import com.browserup.bup.BrowserUpProxyServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

//	✅ DriverFactory هذا الكلاس مسؤول عن إنشاء وتكوين المتصفح (WebDriver) بناءً على إعدادات المشروع.
// This class handles initializing the WebDriver instance according to project settings.
public class DriverFactory {

	// 🧠 كائن المتصفح المستخدم في الاختبارات
	// WebDriver instance used in the test
	public WebDriver driver;

	// ⚙️ كائن الإعدادات العامة (مأخوذة من ملف config.properties)
	// Global properties object loaded from config.properties
	public Properties prop;

	private BrowserUpProxy proxy; // null لغير Firefox
	private String browserName; // نخزّن اسم المتصفح

	private Path downloadsDir;

	/**
	 * 🌐 تهيئة المتصفح استنادًا إلى الإعدادات المعرّفة في ملف config.properties
	 *
	 * 🔹 هذه الدالة تُستخدم لتهيئة متصفح الاختبار (Chrome, Firefox, Edge) بناءً على
	 * القيمة المحددة في ملف الإعدادات. 🔹 يتم إعداد مجلد التنزيلات، وضبط الخصائص
	 * (مثل التحميلات و البروكسي)، وتكبير النافذة لتحسين استقرار الاختبارات.
	 *
	 * 🌐 Initializes the WebDriver instance based on the "browser" setting in
	 * config.properties. Sets up downloads folder, proxy (if needed), and maximizes
	 * the browser window.
	 *
	 * @return WebDriver كائن جاهز لاستخدامه في التصفح والاختبارات | WebDriver ready
	 *         for use
	 * @throws RuntimeException إذا فشلت عملية التهيئة لأي سبب | If browser
	 *                          initialization fails
	 *
	 *                          📌 الهدف: توفير WebDriver موحّد ومجهز بجميع
	 *                          الإعدادات المطلوبة لبدء الاختبارات.
	 */
	@Step("🌐 Initialize WebDriver based on configuration")
	public WebDriver initDriver() {
		try {
			// 🧩 1. تحميل الإعدادات من ملف config.properties
			prop = ConfigReader.initProperties();

			// 🌐 2. جلب نوع المتصفح من الإعدادات
			this.browserName = prop.getProperty("browser").trim().toLowerCase();
			String browserName = this.browserName;
			Allure.step("🌐 المتصفح المختار: " + browserName);

			// 📂 3. إنشاء مجلد التنزيلات داخل المشروع
			downloadsDir = Path.of(System.getProperty("user.dir"), "downloads");
			try {
				Files.createDirectories(downloadsDir);
			} catch (IOException ignored) {
			}

			// 🧪 4. تهيئة WebDriver بناءً على نوع المتصفح
			switch (browserName) {
			case "chrome":
				ChromeOptions opt = new ChromeOptions();
				Map<String, Object> prefs = new HashMap<>();
				prefs.put("download.default_directory", downloadsDir.toString());
				prefs.put("download.prompt_for_download", false);
				prefs.put("download.directory_upgrade", true);
				prefs.put("safebrowsing.enabled", true);
				opt.setExperimentalOption("prefs", prefs);
				driver = new ChromeDriver(opt);
				break;

			case "firefox":
				proxy = new BrowserUpProxyServer();
				proxy.start(0); // اختيار منفذ تلقائي
				String hostPort = "127.0.0.1:" + proxy.getPort();

				Proxy seleniumProxy = new Proxy();
				seleniumProxy.setHttpProxy(hostPort);
				seleniumProxy.setSslProxy(hostPort);

				org.openqa.selenium.firefox.FirefoxOptions ff = new org.openqa.selenium.firefox.FirefoxOptions();
				ff.setProxy(seleniumProxy);

				ff.addPreference("browser.download.dir", downloadsDir.toString());
				ff.addPreference("browser.download.folderList", 2);
				ff.addPreference("browser.helperApps.neverAsk.saveToDisk",
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel,text/csv,application/octet-stream");
				ff.addPreference("pdfjs.disabled", true);

				driver = new FirefoxDriver(ff);
				break;

			case "edge":
				EdgeOptions optt = new EdgeOptions();
				Map<String, Object> prefss = new HashMap<>();
				prefss.put("download.default_directory", downloadsDir.toString());
				prefss.put("download.prompt_for_download", false);
				optt.setExperimentalOption("prefs", prefss);
				driver = new EdgeDriver(optt);
				break;

			default:
				Allure.step("⚠️ متصفح غير معروف، سيتم استخدام Chrome كخيار افتراضي");
				driver = new ChromeDriver();
			}

			// ⏳ 5. ضبط وقت الانتظار الضمني
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

			// 🖥️ 6. تكبير النافذة لتحسين الاستقرار
			driver.manage().window().maximize();

		} catch (Exception e) {
			Allure.step("❌ فشل تهيئة المتصفح: " + e.getMessage());
			throw new RuntimeException("⚠️ حدث خطأ أثناء تهيئة المتصفح: " + e.getMessage());
		}

		// ✅ 7. إعادة WebDriver المجهز
		return driver;
	}

	/**
	 * 🌐 إرجاع كائن البروكسي الحالي (إن تم تهيئته)
	 *
	 * 🌐 Returns the currently configured BrowserUp proxy instance.
	 *
	 * @return كائن BrowserUpProxy أو null إذا لم يتم تهيئته | BrowserUpProxy
	 *         instance or null
	 */
	@Step("🌐 Get BrowserUp Proxy instance")
	public BrowserUpProxy getProxy() {
		return proxy;
	}

	/**
	 * 🌐 إرجاع اسم المتصفح المستخدم حاليًا
	 *
	 * 🌐 Returns the name of the currently initialized browser.
	 *
	 * @return اسم المتصفح | Name of the browser (chrome, firefox, edge, etc.)
	 */
	@Step("🌐 Get browser name")
	public String getBrowserName() {
		return browserName;
	}

	/**
	 * 🌐 إرجاع كائن WebDriver الحالي
	 *
	 * 🌐 Returns the current WebDriver instance.
	 *
	 * @return WebDriver المستخدم في الجلسة الحالية | Current WebDriver instance
	 */
	@Step("🌐 Get current WebDriver instance")
	public WebDriver getDriver() {
		return driver;
	}

	/**
	 * 📂 إرجاع مسار مجلد التنزيلات المستخدم في الاختبارات
	 *
	 * 📂 Returns the path of the downloads directory used in tests.
	 *
	 * @return مسار مجلد التنزيلات | Path to the downloads directory
	 */
	@Step("📂 Get downloads directory path")
	public Path getDownloadsDir() {
		return downloadsDir;
	}

}
