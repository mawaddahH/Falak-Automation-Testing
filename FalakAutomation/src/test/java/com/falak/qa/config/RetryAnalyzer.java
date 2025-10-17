package com.falak.qa.config;

import io.qameta.allure.Allure;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class RetryAnalyzer implements IRetryAnalyzer {

	private int retryCount = 0;
	private final int maxRetryCount;

	/**
	 * ✨ قائمة الاستثناءات المسموح بإعادة المحاولة عند حدوثها
	 *
	 * 🔹 تحتوي على الأنواع الشائعة من استثناءات Selenium و WebDriver التي قد تحدث
	 * بشكل متقطع (مثل Timeout أو StaleElementReference). 🔹 إذا حدث أي استثناء مدرج
	 * في هذه القائمة، يتم السماح بإعادة محاولة تنفيذ الاختبار.
	 *
	 * ✨ List of retryable exceptions. Defines which exceptions allow retrying the
	 * test execution (e.g., Timeout, StaleElementReference, WebDriver errors).
	 *
	 * 📌 الهدف: التعامل مع الأخطاء العابرة (Flaky Errors) وجعل الاختبارات أكثر
	 * استقرارًا.
	 */
	private static final Class<?>[] RETRYABLE_EXCEPTIONS = { TimeoutException.class,
			ElementNotInteractableException.class, NoSuchElementException.class, StaleElementReferenceException.class,
			WebDriverException.class,

			// fully qualified names for clarity
			org.openqa.selenium.NoSuchElementException.class, org.openqa.selenium.StaleElementReferenceException.class,
			ElementClickInterceptedException.class, org.openqa.selenium.WebDriverException.class,

			RuntimeException.class };

	/**
	 * 🔧 المُهيّئ (Constructor) لآلية إعادة المحاولة
	 *
	 * 🔹 يقوم بقراءة قيمة `retry.count` من ملف `config.properties` لتحديد الحد
	 * الأقصى لعدد مرات إعادة المحاولة المسموح بها. 🔹 في حال كانت القيمة غير رقمية
	 * أو غير صالحة يتم استخدام القيمة الافتراضية (0).
	 *
	 * 🔧 Constructor for RetryAnalyzer. Reads the `retry.count` value from
	 * `config.properties` to define the maximum allowed retry attempts. Falls back
	 * to 0 if the value is invalid.
	 *
	 * 📌 الهدف: ضبط `maxRetryCount` بشكل ديناميكي من الإعدادات.
	 */
	public RetryAnalyzer() {
		String retryCountStr = ConfigReader.initProperties().getProperty("retry.count", "0");
		int parsedCount;
		try {
			parsedCount = Integer.parseInt(retryCountStr);
		} catch (NumberFormatException e) {
			parsedCount = 0;
		}
		this.maxRetryCount = parsedCount;
	}

	/**
	 * 🔁 يحدد ما إذا كان يجب إعادة محاولة تشغيل الاختبار بعد الفشل
	 *
	 * 🔹 يتم التحقق من: 1. أن عدد المحاولات الحالية أقل من الحد الأقصى. 2. أن هناك
	 * استثناء وقع. 3. أن الاستثناء ينتمي إلى قائمة الاستثناءات المسموح إعادة
	 * المحاولة لها.
	 *
	 * 🔁 Determines whether the failed test should be retried. Checks that the
	 * current retry count is below the maximum, an exception occurred, and the
	 * exception is retryable.
	 *
	 * ✨ ميزات إضافية: - تسجيل سبب الفشل في Allure + Console. - تطبيق آلية
	 * "Exponential Backoff" (تأخير يتضاعف مع كل محاولة). - إغلاق المتصفح القديم قبل
	 * إعادة التشغيل لتجنب تداخل الجلسات.
	 *
	 * @param result كائن نتيجة الاختبار الفاشل | The failed test result
	 * @return true إذا كان سيتم إعادة المحاولة، false خلاف ذلك | true if retry is
	 *         allowed, false otherwise
	 */
	@Override
	public boolean retry(ITestResult result) {
		Throwable cause = result.getThrowable();

		if (retryCount < maxRetryCount && cause != null && isRetryableException(cause)) {
			retryCount++;

			String message = String.format("🔁 إعادة المحاولة #%d لحالة الاختبار: %s بسبب الخطأ: %s", retryCount,
					result.getMethod().getMethodName(), cause.getClass().getSimpleName());
			System.out.println(message);
			System.out.println("Current retry count: " + retryCount + " / Max: " + maxRetryCount);
			Allure.step(message);

			// 🔄 Exponential Backoff Delay
			int delayInSeconds = (int) Math.pow(2, retryCount);
			try {
				Allure.step("⏳ الانتظار " + delayInSeconds + " ثواني قبل المحاولة التالية");
				TimeUnit.SECONDS.sleep(delayInSeconds);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			// ✅ إغلاق الجلسة القديمة لتفادي مشاكل في إعادة التشغيل
			try {
				Object testInstance = result.getInstance();
				Field driverField = testInstance.getClass().getDeclaredField("driver");
				driverField.setAccessible(true);
				WebDriver driver = (WebDriver) driverField.get(testInstance);
				if (driver != null) {
					driver.quit();
					Allure.step("🛑 تم إغلاق المتصفح بعد فشل المحاولة السابقة");
				}
			} catch (Exception e) {
				Allure.step("⚠️ لم يتمكن من إغلاق المتصفح قبل إعادة المحاولة: " + e.getMessage());
			}

			return true;
		}

		return false;
	}

	/**
	 * ✨ يتحقق مما إذا كان الاستثناء يسمح بإعادة المحاولة
	 *
	 * 🔹 تتم مطابقة نوع الاستثناء مع قائمة الاستثناءات المسموح بها
	 * (`RETRYABLE_EXCEPTIONS`). 🔹 بالإضافة إلى ذلك، يتم فحص الرسالة النصية
	 * للاستثناء للتأكد من حالات خاصة مثل: `"Expected condition failed"`.
	 *
	 * ✨ Checks if the exception is eligible for retry. - Matches the exception type
	 * against the `RETRYABLE_EXCEPTIONS` list. - Additionally checks for specific
	 * messages like `"Expected condition failed"`.
	 *
	 * @param throwable الاستثناء الذي حدث | The thrown exception
	 * @return true إذا كان يسمح بإعادة المحاولة، false خلاف ذلك | true if
	 *         retryable, false otherwise
	 */
	private boolean isRetryableException(Throwable throwable) {
		boolean isTypeMatch = Arrays.stream(RETRYABLE_EXCEPTIONS)
				.anyMatch(type -> type.isAssignableFrom(throwable.getClass()));

		boolean isMessageMatch = throwable.getMessage() != null
				&& throwable.getMessage().contains("Expected condition failed");

		return isTypeMatch || isMessageMatch;
	}

}
