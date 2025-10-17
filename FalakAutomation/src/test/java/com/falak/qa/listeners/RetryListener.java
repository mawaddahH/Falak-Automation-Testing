package com.falak.qa.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;
import com.falak.qa.config.RetryAnalyzer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 🔁 يربط RetryAnalyzer آليًا بجميع الاختبارات. Automatically attaches
 * RetryAnalyzer to every @Test method.
 */
public class RetryListener implements IAnnotationTransformer {

	/**
	 * 🔄 يضبط آلية إعادة المحاولة (RetryAnalyzer) بشكل تلقائي
	 *
	 * 🔹 هذا الميثود يُنفّذ عند تحميل التعليقات التوضيحية (annotations) الخاصة بـ
	 * TestNG. إذا لم يتم تعيين `RetryAnalyzer` يدويًا في الاختبار، يقوم هذا الميثود
	 * بإسناد الكلاس `RetryAnalyzer.class` بشكل افتراضي.
	 *
	 * 🔄 Automatically configures the retry analyzer for TestNG tests. This method
	 * runs during annotation transformation and ensures that every test has a retry
	 * mechanism. If a test method does not explicitly define a `RetryAnalyzer`, it
	 * will automatically be assigned `RetryAnalyzer.class`.
	 *
	 * @param annotation      التعليق التوضيحي للاختبار | The test annotation being
	 *                        transformed
	 * @param testClass       الكلاس الذي يحتوي على الاختبار (قد يكون null) | The
	 *                        test class
	 * @param testConstructor المُنشئ الخاص بالاختبار (قد يكون null) | The test
	 *                        constructor
	 * @param testMethod      الميثود الخاص بالاختبار | The test method
	 *
	 *                        📌 الهدف: ضمان أن جميع الاختبارات لديها آلية إعادة
	 *                        المحاولة حتى لو لم يتم تعيينها يدويًا.
	 */
	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {

		// ✅ إذا لم يتم تعيين RetryAnalyzer يدويًا، أضف RetryAnalyzer.class افتراضيًا
		if (annotation.getRetryAnalyzerClass() == null) {
			annotation.setRetryAnalyzer(RetryAnalyzer.class);
		}
	}

}
