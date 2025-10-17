package com.falak.qa.base;

import com.falak.qa.config.ConfigReader;
import com.falak.qa.config.DriverFactory;
import com.falak.qa.config.EnvironmentConfigLoader;
import com.falak.qa.net.NetworkTriage;
import com.falak.qa.net.NetworkTriageFactory;
import com.falak.qa.pages.home.HomePage;
import com.falak.qa.utils.JcodecVideoRecorder;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.time.Duration;
import java.util.function.BooleanSupplier;

import javax.imageio.ImageIO;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

//	✅ BaseTest: الكلاس الأساسي لجميع ملفات الاختبار
// This class handles test setup and teardown for all test classes.
public class BaseTest {

	// 🧠 المتغير الرئيسي للتعامل مع المتصفح
	// WebDriver is the main interface to control the browser
	protected WebDriver driver;

	// 🛠️ مصنع لإنشاء المتصفح حسب نوعه (Chrome, Firefox, etc.)
	// DriverFactory initializes the chosen browser
	protected DriverFactory driverFactory;

	// 🏠 الكائن الخاص بالصفحة الرئيسية لفلك
	// HomePage represents the main landing page of the application
	protected HomePage homePage;

	// 🔗 رابط الصفحة الذي سيتم فتحه عند تشغيل الاختبار
	// This URL is loaded before each test
	protected String url;

	// 👇 طبقة مراقبة الشبكة (CDP/Proxy) موحّدة
	protected NetworkTriage triage;

	protected JcodecVideoRecorder videoRecorder;

	private boolean videoStopped = false; // منع الإيقاف مرتين

	/**
	 * 🔧 يُنفّذ جميع خطوات الإعداد قبل كل حالة اختبار
	 *
	 * 🔹 تقوم هذه الدالة بتهيئة بيئة التشغيل بشكل كامل: - قراءة ملف الإعدادات
	 * (config.properties) وتحديد البيئة الحالية (dev, staging...). - تحميل إعدادات
	 * البيئة من ملف JSON (مثل روابط الـ baseUrl). - إنشاء متصفح جديد باستخدام
	 * DriverFactory. - فتح الرابط الأساسي وتأكيد نجاح التحميل. - إنشاء الصفحة
	 * الرئيسية HomePage للتفاعل معها. - تفعيل مراقبة الشبكة (Network Triage)
	 * لالتقاط طلبات/أخطاء الـ API. - بدء تسجيل فيديو باستخدام JCodecVideoRecorder
	 * لتوثيق الاختبار بصريًا.
	 *
	 * 🔧 This method runs before every test to prepare the execution environment: -
	 * Reads config.properties and determines the active environment. - Loads
	 * environment settings from JSON (e.g., baseUrl). - Initializes the browser
	 * using DriverFactory. - Opens the base URL and verifies it loads successfully.
	 * - Creates the HomePage object for navigation and interaction. - Starts
	 * Network Triage for capturing network/API activity. - Starts video recording
	 * via JCodecVideoRecorder to document the test.
	 *
	 * @param method كائن Method يعكس اسم حالة الاختبار الحالية | Method object
	 *               representing the running test
	 * @throws RuntimeException إذا فشل أي جزء من عملية التهيئة | Throws
	 *                          RuntimeException if setup fails
	 *
	 *                          📌 الهدف: ضمان بيئة اختبار موحّدة ونظيفة قبل كل حالة
	 *                          اختبار، مع تسجيل فيديو لسهولة تحليل الأعطال.
	 */
	@BeforeMethod(alwaysRun = true)
	@Step("🔧 إعداد البيئة قبل تشغيل الاختبار")
	public void setUp(Method method) {
		try {
			// 📥 قراءة ملف الإعدادات العامة مثل نوع البيئة (dev, staging, etc.)
			Allure.step("📥 قراءة ملف الإعدادات config.properties");
			String environment = ConfigReader.initProperties().getProperty("environment");

			// 🌍 تحميل إعدادات البيئة من ملف JSON (يحتوي على روابط البيئة)
			Allure.step("🌍 تحميل إعدادات البيئة: " + environment);
			EnvironmentConfigLoader.loadConfig(environment);

			// 🔗 جلب الرابط الصحيح من ملف البيئة
			url = EnvironmentConfigLoader.getUrl("baseUrl");
			Allure.step("🔗 رابط البيئة: " + url);

			// 🚀 إنشاء المتصفح باستخدام DriverFactory
			driverFactory = new DriverFactory();
			driver = driverFactory.initDriver();
			Allure.step("🚀 المتصفح جاهز للعمل");

			// 🌐 فتح الرابط
			driver.get(url);
			Allure.step("✅ تم فتح الرابط بنجاح");

			// 🏠 إنشاء كائن الصفحة الرئيسية
			homePage = new HomePage(driver);

			// ✅ تفعيل Triage المناسب (CDP للكروم/إيدج، Proxy للفايرفوكس، No-op احتياطي)
			triage = NetworkTriageFactory.create(driver, driverFactory.getProxy());
			triage.start(); // يبدأ الاستماع لأحداث الشبكة
			Allure.step("🕸️ Network Triage started");

			// 🎥 بدء تسجيل الفيديو
			try {
				videoRecorder = new JcodecVideoRecorder(10);
				videoRecorder.start(method.getName());
//				Allure.step("🎥 Video recording started: " + method.getName());
				videoStopped = false;
			} catch (Exception ve) {
				Allure.step("⚠️ Failed to start video recording: " + ve.getMessage());
				videoRecorder = null;
			}

		} catch (Exception e) {
			// ❌ في حال حدوث خطأ في التهيئة
			Allure.step("❌ حدث خطأ أثناء التهيئة: " + e.getMessage());
			throw new RuntimeException("❌ فشل الإعداد قبل الاختبار: " + e.getMessage());
		}
	}

	/**
	 * 🧹 يُنفّذ جميع خطوات التنظيف بعد كل حالة اختبار
	 *
	 * 🔹 تقوم هذه الدالة بضمان إغلاق بيئة الاختبار بشكل آمن ومرتب: - إيقاف تسجيل
	 * الفيديو وإرفاقه في تقرير Allure في حال فشل الاختبار. - إيقاف مراقبة الشبكة
	 * (Network Triage) لضمان عدم بقاء أي جلسات مفتوحة. - إغلاق المتصفح بعد الانتظار
	 * لفترة قصيرة، لتفادي إنهاء العمليات الجارية.
	 *
	 * 🧹 This method runs after each test to ensure proper cleanup: - Stops the
	 * video recorder and attaches the recording to Allure on failure. - Stops the
	 * network triage to release any ongoing listeners or proxies. - Closes the
	 * browser after a short delay, ensuring stability of teardown.
	 *
	 * @param result كائن ITestResult يصف نتيجة حالة الاختبار (نجاح/فشل/تخطي) |
	 *               ITestResult describing the outcome of the executed test
	 * @throws RuntimeException إذا حدث خطأ أثناء الإغلاق أو التنظيف | Throws
	 *                          RuntimeException if cleanup or shutdown fails
	 *
	 *                          📌 الهدف: ضمان إغلاق الموارد (المتصفح + مراقبة
	 *                          الشبكة + الفيديو) بعد كل اختبار، لتفادي أي تسرب
	 *                          موارد أو تعارض بين الاختبارات التالية.
	 */
	@AfterMethod(alwaysRun = true)
	@Step("🧹 تنظيف البيئة بعد الاختبار")
	public void tearDown(ITestResult result) {
		try {
			// 🎥 إيقاف تسجيل الفيديو وإرفاقه عند الفشل
			safeStopAndAttachVideoOnFailure(result);

			// 🛑 إيقاف مراقبة الشبكة
			if (triage != null) {
				try {
					triage.stop();
				} catch (Exception ignore) {
				}
				Allure.step("🕸️ Network Triage stopped");
			}

			Allure.step("🛑 إغلاق المتصفح بعد الاختبار");

			// ⏳ انتظار قصير لضمان استقرار العمليات قبل الإغلاق
			Thread.sleep(500);

			// ✅ إغلاق المتصفح إذا كان ما يزال نشطًا
			if (driver != null) {
				driver.quit();
				Allure.step("✅ تم إغلاق المتصفح بنجاح");
			}

		} catch (InterruptedException e) {
			Allure.step("⚠️ فشل أثناء الانتظار قبل الإغلاق: " + e.getMessage());
			e.printStackTrace();

		} catch (Exception e) {
			Allure.step("🚫 فشل إغلاق المتصفح: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * ⛑️ يوقف تسجيل الفيديو إن لم يكن موقوفًا مسبقًا ويُرفقه في تقرير Allure عند
	 * فشل الاختبار.
	 *
	 * 🔹 تُستخدم هذه الدالة كطبقة أمان لإغلاق الـ Video Recorder بعد انتهاء
	 * الاختبار. - إذا كان الاختبار فشل (ITestResult.FAILURE) → يتم إرفاق الفيديو في
	 * تقرير Allure. - إذا نجح الاختبار → يتم حذف الفيديو لتقليل الحجم (إلا إذا أردت
	 * الاحتفاظ به).
	 *
	 * ⛑️ Stops the video recording if not already stopped and attaches the video to
	 * the Allure report in case of test failure. - On failure → attaches the
	 * recorded MP4 to the Allure report. - On success → deletes the file to avoid
	 * clutter (optional).
	 *
	 * @param result كائن ITestResult الذي يصف نتيجة الاختبار (نجاح/فشل/تخطي) |
	 *               ITestResult describing the outcome of the executed test
	 * @throws RuntimeException إذا حدث خطأ أثناء إيقاف التسجيل أو إرفاق الفيديو |
	 *                          Throws RuntimeException if stopping or attaching
	 *                          fails
	 *
	 *                          📌 الهدف: ضمان إغلاق الفيديو وتوثيقه عند الفشل
	 *                          لتسهيل تحليل الأخطاء بعد التنفيذ.
	 */
	private void safeStopAndAttachVideoOnFailure(ITestResult result) {
		if (videoRecorder == null || videoStopped)
			return;
		try {
			File video = videoRecorder.stopAndGetFile();
			videoStopped = true;
			if (result != null && result.getStatus() == ITestResult.FAILURE && video != null && video.length() > 0) {
				try (FileInputStream fis = new FileInputStream(video)) {
					// Allure.addAttachment("📹 Failure video - " + result.getName(), "video/mp4",
					// fis, ".mp4");
				}
			}
			// 🗑️ في حالة النجاح يمكن حذف الفيديو لتقليل حجم المشروع
			else if (video != null)
				Files.deleteIfExists(video.toPath());
		} catch (Exception ve) {
			// Allure.step("⚠️ Failed to stop/attach video: " + ve.getMessage());
		}
	}

	/**
	 * 🌐 يُرجع كائن مراقبة الشبكة (NetworkTriage) المستخدم في الاختبار.
	 *
	 * 🔹 يسمح هذا الكائن بالتقاط وتحليل طلبات الشبكة (API calls) أثناء تشغيل
	 * الاختبارات. يمكن الاستفادة منه للتحقق من الاستجابات أو اكتشاف أخطاء الشبكة.
	 *
	 * 🌐 Returns the active NetworkTriage instance used during the test. Useful for
	 * monitoring and validating API calls, errors, or requests.
	 *
	 * @return كائن NetworkTriage الحالي | The current active NetworkTriage instance
	 *
	 *         📌 الهدف: تسهيل الوصول إلى طبقة مراقبة الشبكة ضمن حالات الاختبار أو
	 *         صفحات الـ POM.
	 */
	public NetworkTriage getTriage() {
		return triage;
	}

	/**
	 * 📊 أنواع النتائج المحتملة عند الانتظار في الاختبار.
	 *
	 * 🔹 يحدد هذا التعداد نوع النتيجة بعد انتظار تحميل البيانات أو واجهة المستخدم:
	 * - TABLE → تم العثور على جدول نتائج. - NO_DATA → لم يتم العثور على بيانات
	 * (رسالة "لا توجد نتائج"). - TRIAGE_ERROR → تم التقاط خطأ من مراقبة الشبكة
	 * (Network Triage).
	 *
	 * 📊 Enum describing the possible outcome kinds when waiting during test
	 * execution: - TABLE → Table of results was displayed. - NO_DATA → No results
	 * were returned. - TRIAGE_ERROR → Network error captured by triage layer.
	 *
	 * 📌 الهدف: توفير قيم واضحة يمكن الاعتماد عليها عند تقييم نتائج الانتظار في
	 * الاختبارات.
	 */
	protected enum OutcomeKind {
		TABLE, NO_DATA, TRIAGE_ERROR
	}

	/**
	 * 📦 نتيجة الانتظار الموحّدة لحالات الاختبار.
	 *
	 * 🔹 تحتوي هذه الفئة على نوع النتيجة (OutcomeKind) مع إمكانية حمل تفاصيل خطأ
	 * الشبكة (TriageEvent). تساعد في توحيد طريقة التعامل مع النتائج سواء كانت
	 * نجاحًا، لا بيانات، أو خطأ شبكة.
	 *
	 * 📦 Unified result object representing the outcome of waiting logic in tests.
	 * Encapsulates both the outcome kind and an optional network error
	 * (TriageEvent).
	 *
	 * @field kind نوع النتيجة (TABLE/NO_DATA/TRIAGE_ERROR) | OutcomeKind of the
	 *        result
	 * @field triageError خطأ الشبكة الملتقط (إن وُجد) | Optional TriageEvent with
	 *        network error details
	 *
	 *        📌 الهدف: تسهيل تمرير نتائج الانتظار (مع الأخطاء إن وُجدت) بين
	 *        الميثودات دون الحاجة إلى استخدام أكثر من متغير.
	 */
	protected static class OutcomeResult {
		public final OutcomeKind kind;
		public final Optional<com.falak.qa.net.TriageEvent> triageError;

		public OutcomeResult(OutcomeKind kind, Optional<com.falak.qa.net.TriageEvent> triageError) {
			this.kind = kind;
			this.triageError = triageError;
		}
	}

	/**
	 * ⏳ ينتظر نتيجة واجهة المستخدم/الـ API مع مراقبة أخطاء الشبكة، ويُعيد نتيجة
	 * موحّدة.
	 *
	 * 🔹 آلية الانتظار تعمل بالتوازي على ثلاثة مسارات: 1) مراقبة أخطاء الشبكة عبر
	 * NetworkTriage (أولوية أعلى – تُلتقط سريعًا). 2) التحقّق من ظهور جدول النتائج
	 * (TABLE). 3) التحقّق من ظهور رسالة "لا توجد بيانات" (NO_DATA).
	 *
	 * ⏳ Waits for outcome by polling UI + network: 1) Checks triage for any network
	 * error (highest priority). 2) Checks if the result table is visible (TABLE).
	 * 3) Checks if "no data" message is visible (NO_DATA).
	 *
	 * @param tableVisible  دالة تُعيد true عند ظهور الجدول | Supplier that returns
	 *                      true when the table is visible
	 * @param noDataVisible دالة تُعيد true عند ظهور رسالة لا توجد بيانات | Supplier
	 *                      that returns true when "no data" is visible
	 * @param timeout       أقصى مدة انتظار | Maximum wait duration
	 * @return OutcomeResult نتيجة موحّدة تحدد هل الجدول ظهر أو لا توجد بيانات أو
	 *         التقطنا خطأ شبكة | Unified outcome
	 * @throws org.openqa.selenium.TimeoutException إذا انتهت المهلة دون تحقق أي
	 *                                              نتيجة | If timeout elapsed with
	 *                                              no outcome
	 *
	 *                                              📌 الهدف: توحيد منطق الانتظار في
	 *                                              مكان واحد، وإرجاع نتيجة واضحة
	 *                                              (TABLE/NO_DATA/TRIAGE_ERROR)
	 *                                              لتبسيط اختباراتك.
	 */
	@Step("⏳ Wait for outcome (table/no-data/triage-error) with timeout: {timeout}")
	protected OutcomeResult waitForOutcomeWithTriage(BooleanSupplier tableVisible, BooleanSupplier noDataVisible,
			Duration timeout) {
		WebDriverWait wait = new WebDriverWait(driver, timeout);
		wait.pollingEvery(Duration.ofMillis(200)).ignoring(NoSuchElementException.class)
				.ignoring(StaleElementReferenceException.class).ignoring(ElementNotInteractableException.class);

		// نستخدم until لإرجاع OutcomeResult
		return wait.until(d -> {
			System.out.println("⏳ Polling... table / no-data / triage-error");

			// 1) triage أولاً (أسرع التقاطًا للأخطاء)
			try {
				var triageErr = getTriage().firstError();
				if (triageErr.isPresent()) {
					var e = triageErr.get();
					Allure.step("🚨 Triage error: status=" + e.status + " url=" + e.url);
					System.out.println("🚨 Triage error detected: " + e);
					return new OutcomeResult(OutcomeKind.TRIAGE_ERROR, Optional.of(e));
				}
			} catch (Exception ex) {
				System.out.println("🔸 triage check failed: " + ex.getMessage());
			}

			// 2) الجدول؟
			try {
				if (tableVisible.getAsBoolean()) {
					return new OutcomeResult(OutcomeKind.TABLE, Optional.empty());
				}
			} catch (Exception ignore) {
			}

			// 3) لا توجد بيانات؟
			try {
				if (noDataVisible.getAsBoolean()) {
					return new OutcomeResult(OutcomeKind.NO_DATA, Optional.empty());
				}
			} catch (Exception ignore) {
			}

			// لا شيء بعد
			return null; // يواصل الانتظار
		});
	}

	/**
	 * 📸 يلتقط لقطة شاشة كاملة للصفحة ويرفقها في تقرير Allure.
	 *
	 * 🔹 يستخدم AShot باستراتيجية viewportRetina لالتقاط Scroll مع دقة عالية،
	 * ويحولها إلى PNG ثم يرفقها كمرفق داخل التقرير.
	 *
	 * 📸 Captures a full-page screenshot and attaches it to the Allure report. Uses
	 * AShot (viewportRetina) for high-DPI, scrollable screenshots.
	 *
	 * @param title العنوان الظاهر في Allure للمرفق | Attachment title in Allure
	 * @throws RuntimeException (ضمنيًا) إذا فشل الالتقاط/الترميز سيُسجّل في الـ
	 *                          console | Failures are logged
	 *
	 *                          📌 الهدف: توثيق حالة الصفحة بصريًا عند نقاط الاهتمام
	 *                          أو عند وقوع الأخطاء.
	 */
	@Step("📸 Attach full-page screenshot: {title}")
	protected void attachFullPageScreenshot(String title) {
		try {
			Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportRetina(100, 0, 0, 2))
					.takeScreenshot(driver);

			BufferedImage image = screenshot.getImage();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, "PNG", os);
			Allure.addAttachment(title, new ByteArrayInputStream(os.toByteArray()));
		} catch (IOException e) {
			System.out.println("⚠️ Failed to capture full page screenshot: " + e.getMessage());
		}
	}

	/**
	 * 🎥 يوقف التسجيل (إن لم يكن متوقفًا) ثم يرفق فيديو آخر تشغيل في Allure.
	 *
	 * 🔹 مخصّصة للاستدعاء من داخل حالة الاختبار (داخل catch مثلاً) عندما تفضّل
	 * إرفاق الفيديو مباشرة هناك بدل الاعتماد على @AfterMethod. 🔹 إذا لم يتوفر
	 * فيديو أو كان حجمه صفر → يتجاهل بصمت.
	 *
	 * 🎥 Stops recording (if still running) and attaches the last recorded video to
	 * the Allure report. Intended for on-demand use inside the test body.
	 *
	 * @param title العنوان الظاهر في Allure للمرفق | Attachment title in Allure
	 * @throws RuntimeException (ضمنيًا) إذا فشل إيقاف/إرفاق الفيديو سيُسجّل في الـ
	 *                          console | Failures are logged
	 *
	 *                          📌 الهدف: تمكينك من إرفاق الفيديو من داخل test body
	 *                          (مثلاً عند AssertionError) بنفس أسلوب لقطة الشاشة.
	 */
	@Step("🎥 Attach failure video (if present): {title}")
	protected void attachFailureVideo(String title) {
		try {
			if (videoRecorder == null)
				return;
			// لو ما توقف بعد، وقّفه الآن حتى يكتمل الملف
			if (!videoStopped) {
				videoRecorder.stopAndGetFile();
				videoStopped = true;
			}
			File video = videoRecorder.getOutputFile();
			if (video != null && video.exists() && video.length() > 0) {
				try (FileInputStream fis = new FileInputStream(video)) {
					Allure.addAttachment(title, "video/mp4", fis, ".mp4");
				}
			} else {
				System.out.println("⚠️ No video file recorded or file is empty.");
			}
		} catch (Exception e) {
			System.out.println("⚠️ Failed to attach video: " + e.getMessage());
		}
	}

}
