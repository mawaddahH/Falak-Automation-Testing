package com.falak.qa.base;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

//	✅ BasePage: الكلاس الأساسي الذي يحتوي على دوال مشتركة بين جميع الصفحات
// This class provides shared methods for interacting with web pages.
public class BasePage {

	// 🧠 WebDriver يمثل المتصفح - نستخدمه للتفاعل مع العناصر
	// WebDriver used to control the browser.
	protected WebDriver driver;

	// ⏳ WebDriverWait للانتظار حتى ظهور أو تفاعل العناصر
	// WebDriverWait for waiting on elements.
	protected WebDriverWait wait;

	// 🔧 المُنشئ: يستقبل الـ driver ويُنشئ وقت الانتظار العام
	// Constructor initializes WebDriver and default wait time.
	public BasePage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	}

	// ==================== Actions with Elements ====================

	/**
	 * 🖱️ ينقر على عنصر بعد الانتظار لكونه قابلاً للنقر
	 *
	 * 🔹 يقوم بالتمرير إلى العنصر أولاً لضمان ظهوره داخل إطار الرؤية، ثم ينتظر
	 * قابليته للنقر ويجري النقرة الفعلية. في حال انتهاء المهلة بدون نجاح → يرمي
	 * RuntimeException.
	 *
	 * 🖱️ Clicks on an element after waiting for it to be clickable. It scrolls the
	 * element into view before clicking to avoid visibility issues.
	 *
	 * @param locator محدد العنصر | Locator of the element to click
	 * @throws RuntimeException عند فشل الانتظار أو النقر | Throws on
	 *                          timeout/failure
	 */
	@Step("Click on element after waiting: {locator}")
	public void waitAndClick(By locator) {
		try {
			WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
			scrollToElement(element);
			element.click();
			Allure.step("✅ Clicked on element: " + locator);
		} catch (TimeoutException e) {
			String message = "❌ فشل النقر على العنصر (انتهت المهلة): " + locator;
			Allure.attachment("خطأ في BasePage - waitAndClick", message);
			throw new RuntimeException(message, e);
		} catch (Exception e) {
			String message = "❌ فشل النقر على العنصر: " + locator + " — " + e.getMessage();
			Allure.attachment("خطأ في BasePage - waitAndClick", message);
			throw new RuntimeException(message, e);
		}
	}

	/**
	 * 👁️ ينتظر ظهور العنصر ثم يُرجعه
	 *
	 * 🔹 مفيد قبل أي تفاعل مع عنصر غير ظاهر فوراً (مثل حقول داخل تبويب/مودال). إذا
	 * لم يظهر العنصر خلال المهلة → يرمي RuntimeException.
	 *
	 * 👁️ Waits for an element to become visible, then returns it. Useful before
	 * interacting with elements that are not immediately visible.
	 *
	 * @param locator محدد العنصر | Locator of the element to wait for
	 * @return عنصر الويب الظاهر | The visible WebElement
	 * @throws RuntimeException عند فشل الانتظار | Throws on timeout
	 */
	@Step("Wait for visibility of element: {locator}")
	public WebElement waitForElement(By locator) {
		try {
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
			return element;
		} catch (TimeoutException e) {
			String message = "❌ فشل الانتظار لظهور العنصر (انتهت المهلة): " + locator;
			Allure.attachment("خطأ في BasePage - waitForElement", message);
			throw new RuntimeException(message, e);
		} catch (Exception e) {
			String message = "❌ فشل الانتظار لظهور العنصر: " + locator + " — " + e.getMessage();
			Allure.attachment("خطأ في BasePage - waitForElement", message);
			throw new RuntimeException(message, e);
		}
	}

	/**
	 * 👁️ ينتظر ظهور جميع العناصر المطابقة للمحدد ثم يُرجعها
	 *
	 * 🔹 يُستخدم للتحقق من أن كل العناصر أصبحت جاهزة للتفاعل (مثل القوائم أو
	 * الصفوف). إذا لم تظهر خلال المهلة → يرمي RuntimeException.
	 *
	 * 👁️ Waits for all elements matching the locator to become visible, then
	 * returns them.
	 *
	 * @param locator محدد العناصر | Locator of the elements to wait for
	 * @return قائمة عناصر الويب الظاهرة | List of visible WebElements
	 * @throws RuntimeException إذا انتهت المهلة بدون ظهور العناصر | Throws
	 *                          RuntimeException on timeout
	 */
	@Step("Wait for visibility of all elements: {locator}")
	public List<WebElement> waitForElements(By locator) {
		try {
			List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
			Allure.step("👀 All elements are visible: " + locator + " (Total: " + elements.size() + ")");
			return elements;
		} catch (TimeoutException e) {
			String message = "❌ فشل الانتظار لظهور العناصر: " + locator;
			Allure.attachment("خطأ في BasePage - waitForElements", message);
			throw new RuntimeException(message);
		}
	}

	/**
	 * 🧭 التمرير إلى عنصر باستخدام JavaScript
	 *
	 * 🔹 يُستخدم عندما يكون العنصر خارج إطار الرؤية (Viewport)، لضمان ظهوره قبل
	 * التفاعل معه.
	 *
	 * 🧭 Scrolls to the specified element using JavaScript. Useful when the element
	 * is outside the current viewport.
	 *
	 * @param element العنصر المستهدف | The WebElement to scroll to
	 */
	@Step("Scroll to element using JavaScript")
	public void scrollToElement(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
		Allure.step("🧭 Scrolled to element: " + element);
	}

	/**
	 * 🖱️ ينقر على عنصر باستخدام JavaScript عند فشل النقر التقليدي
	 *
	 * 🔹 مفيد للعناصر التي تتأثر بـ CSS أو مغطاة بعناصر أخرى، حيث أن
	 * `element.click()` قد لا يعمل بشكل صحيح.
	 *
	 * 🖱️ Clicks an element using JavaScript when standard click fails. Useful for
	 * dynamic or non-standard elements.
	 *
	 * @param locator محدد العنصر | Locator of the element to click
	 * @throws RuntimeException إذا فشل العثور على العنصر أو النقر عليه
	 */
	@Step("Click on element using JavaScript: {locator}")
	public void jsClick(By locator) {
		try {
			WebElement element = waitForElement(locator);
			scrollToElement(element);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
			Allure.step("✅ Clicked on element using JavaScript: " + locator);
		} catch (Exception e) {
			String message = "❌ فشل النقر باستخدام JavaScript على العنصر: " + locator;
			Allure.attachment("خطأ في BasePage", message);
			throw new RuntimeException(message);
		}
	}

	/**
	 * 🏷️ يُرجع عنوان الصفحة الحالي
	 *
	 * 🔹 يُستخدم عادة للتحقق من أن التنقل بين الصفحات تم بشكل صحيح، أو للتأكد من أن
	 * العنوان المعروض يطابق المتوقع في الاختبار.
	 *
	 * 🏷️ Returns the current page title. Useful for validating navigation
	 * correctness or expected title display.
	 *
	 * @return عنوان الصفحة الحالي | The title of the current web page
	 */
	@Step("Get current page title")
	public String getPageTitle() {
		String title = driver.getTitle();
		Allure.step("📄 Current page title: " + title);
		return title;
	}

	/**
	 * 🔗 يُرجع الرابط (URL) الحالي للصفحة المفتوحة
	 *
	 * 🔹 مفيد للتحقق من أن المستخدم وُجّه إلى الرابط الصحيح بعد عمليات مثل تسجيل
	 * الدخول، النقر على الروابط، أو إعادة التوجيه.
	 *
	 * 🔗 Returns the current URL of the loaded web page. Useful for verifying
	 * navigation correctness after actions like login.
	 *
	 * @return الرابط الحالي | The current URL as a String
	 */
	@Step("Get current page URL")
	public String getCurrentURL() {
		String currentUrl = driver.getCurrentUrl();
		Allure.step("🔗 Current page URL: " + currentUrl);
		return currentUrl;
	}

	/**
	 * 👁️‍🗨️ يتحقق مما إذا كان عنصر معين ظاهرًا في الصفحة
	 *
	 * 🔹 دالة "آمنة" لا ترمي استثناء عند الفشل، وإنما تعيد false. مفيدة للتحقق
	 * الشرطي من ظهور عناصر اختيارية.
	 *
	 * 👁️‍🗨️ Checks whether the specified element is visible on the page. Safe
	 * method: returns false instead of throwing exception if not found.
	 *
	 * @param locator محدد العنصر | Locator of the element to check
	 * @return true إذا كان العنصر ظاهرًا | true if visible, false إذا لم يكن ظاهرًا
	 *         أو لم يتم العثور عليه | false otherwise
	 */
	@Step("Check if element is visible: {locator}")
	public boolean isElementVisible(By locator) {
		try {
			boolean visible = waitForElement(locator).isDisplayed();
			return visible;
		} catch (Exception e) {
			String message = "❌ لم يتم العثور على العنصر أو لم يكن ظاهرًا: " + locator;
			Allure.attachment("تحقق من ظهور عنصر", message);
			return false;
		}
	}

	/**
	 * ⌨️ يكتب نصًا داخل حقل إدخال بعد التأكد من ظهوره
	 *
	 * 🔹 يُستخدم عادة لإدخال بيانات النموذج أو البحث. يقوم أولاً بمسح أي نص موجود
	 * ثم يكتب النص الجديد.
	 *
	 * ⌨️ Types text into an input field after ensuring it's visible. Clears
	 * existing text before typing the new one.
	 *
	 * @param locator محدد الحقل | Locator of the input field
	 * @param text    النص المطلوب إدخاله | The text to type
	 * @throws RuntimeException إذا فشل العثور على الحقل أو الكتابة فيه Throws
	 *                          RuntimeException if typing fails
	 */
	@Step("Type text into element: {locator} with value: {text}")
	public void type(By locator, String text) {
		try {
			WebElement element = waitForElement(locator);
			element.clear();
			element.sendKeys(text);
		} catch (Exception e) {
			String message = "❌ فشل إدخال النص في الحقل: " + locator;
			Allure.attachment("خطأ في الكتابة", message);
			throw new RuntimeException(message);
		}
	}

	/**
	 * 📝 يُرجع النص الظاهر داخل عنصر معيّن
	 *
	 * 🔹 تُستخدم هذه الدالة في اختبارات التحقق من الرسائل أو البيانات المعروضة، مثل
	 * رسائل الخطأ أو النصوص في الجداول والأزرار.
	 *
	 * 📝 Returns the visible text from a specific element. Useful for assertions of
	 * displayed data or error messages.
	 *
	 * @param locator محدد العنصر | Locator of the element
	 * @return النص داخل العنصر | The text inside the element
	 * @throws RuntimeException إذا فشل جلب النص من العنصر Throws RuntimeException
	 *                          if text retrieval fails
	 *
	 *                          📌 الهدف: التأكد من أن النصوص المطلوبة ظاهرة وصحيحة
	 *                          أثناء الاختبار.
	 */
	@Step("Get text from element: {locator}")
	public String getText(By locator) {
		try {
			String text = waitForElement(locator).getText();
			Allure.step("📥 Retrieved text from element: " + locator + " => " + text);
			return text;
		} catch (Exception e) {
			String message = "❌ فشل جلب النص من العنصر: " + locator;
			Allure.attachment("خطأ في قراءة النص", message);
			throw new RuntimeException(message);
		}
	}

	/**
	 * 📌 يُرجع قيمة خاصية (Attribute) من عنصر HTML معيّن
	 *
	 * 🔹 مفيد للتحقق من خصائص مثل: - href (رابط) - value (قيمة الإدخال) - class
	 * (الستايلات) وغيرها.
	 *
	 * 📌 Returns the value of a specified attribute from an element. Useful for
	 * verifying attributes like href, value, class, etc.
	 *
	 * @param locator   محدد العنصر | Locator of the element
	 * @param attribute اسم الخاصية المطلوب جلبها | Name of the attribute
	 * @return قيمة الخاصية | The value of the specified attribute
	 * @throws RuntimeException إذا فشل جلب الخاصية Throws RuntimeException if
	 *                          attribute retrieval fails
	 *
	 *                          📌 الهدف: التحقق من خصائص العناصر للتأكد من سلوكها
	 *                          الصحيح.
	 */
	@Step("Get attribute from element: {locator}, attribute: {attribute}")
	public String getAttribute(By locator, String attribute) {
		try {
			String value = waitForElement(locator).getAttribute(attribute);
			Allure.step("📌 Retrieved attribute '" + attribute + "' from element: " + locator + " => " + value);
			return value;
		} catch (Exception e) {
			String message = "❌ فشل جلب الخاصية '" + attribute + "' من العنصر: " + locator;
			Allure.attachment("خطأ في قراءة خاصية", message);
			throw new RuntimeException(message);
		}
	}

	/**
	 * 🔄 يُبدّل التركيز إلى إطار (iframe) داخل الصفحة
	 *
	 * 🔹 ضروري قبل التفاعل مع العناصر الموجودة داخل iframe.
	 *
	 * 🔄 Switches the WebDriver focus to a specific iframe within the page.
	 * Required before interacting with elements inside iframes.
	 *
	 * @param locator محدد الإطار (iframe) | Locator of the iframe element
	 * @throws RuntimeException إذا فشل العثور على الإطار أو التبديل إليه Throws
	 *                          RuntimeException if switching fails
	 *
	 *                          📌 الهدف: ضمان إمكانية التفاعل مع عناصر داخل iframe
	 *                          أثناء الاختبار.
	 */
	@Step("Switch to iframe: {locator}")
	public void switchToFrame(By locator) {
		try {
			WebElement frame = waitForElement(locator);
			driver.switchTo().frame(frame);
			Allure.step("🔄 Switched to iframe: " + locator);
		} catch (Exception e) {
			String message = "❌ فشل التبديل إلى الإطار (iframe): " + locator;
			Allure.attachment("خطأ في switchToFrame", message);
			throw new RuntimeException(message);
		}
	}

	/**
	 * 🔙 يُعيد التركيز إلى محتوى الصفحة الرئيسي بعد الخروج من iframe
	 *
	 * 🔹 هذه الدالة تُستخدم بعد الانتهاء من التفاعل مع أي iframe للرجوع إلى السياق
	 * الرئيسي للصفحة.
	 *
	 * 🔙 Switches the WebDriver focus back to the main page content after exiting
	 * an iframe.
	 *
	 * 📌 الهدف: تمكين التفاعل مع عناصر الصفحة الرئيسية بعد العمل داخل إطار فرعي.
	 */
	@Step("Switch back to default content")
	public void switchToDefaultContent() {
		driver.switchTo().defaultContent();
	}

	/**
	 * 🔢 يُعدّ عدد العناصر المطابقة لمحدد معيّن (Locator)
	 *
	 * 🔹 تُستخدم هذه الدالة للتحقق من عدد العناصر التي يُرجعها محدد (مثل XPath أو
	 * CSS). مفيدة للتحقق من القوائم، الجداول، أو تكرار عناصر معيّنة.
	 *
	 * 🔢 Counts the number of elements found using the given locator. Useful for
	 * verifying lists, tables, or repeated elements.
	 *
	 * @param locator محدد العناصر (مثل By.xpath أو By.cssSelector) Locator of the
	 *                elements to be counted.
	 * @return عدد العناصر المطابقة | The number of matching elements
	 * @throws RuntimeException إذا حدث خطأ أثناء محاولة العد Throws
	 *                          RuntimeException if counting fails.
	 *
	 *                          📌 الهدف: التأكد من وجود عدد متوقع من العناصر أثناء
	 *                          الاختبار.
	 */
	@Step("Count number of elements matching locator: {locator}")
	public int countElements(By locator) {
		try {
			int count = driver.findElements(locator).size();
			Allure.step("📊 عدد العناصر باستخدام المحدد " + locator + " = " + count);
			return count;
		} catch (Exception e) {
			Allure.step("❌ خطأ أثناء عد العناصر: " + locator);
			throw new RuntimeException("Count failed: " + e.getMessage());
		}
	}

	/**
	 * ⏳ انتظار حتى تكتمل جاهزية الصفحة للتفاعل الكامل
	 *
	 * 🔹 هذه الدالة تتحقق من: 1. تحميل كامل HTML (readyState = complete). 2. تطابق
	 * الرابط مع الجزء المتوقع (path segment). 3. استدعاء `waitForPageElements()`
	 * للتحقق من العناصر العامة. 4. التأكد من جاهزية العنصر الرئيسي (إن تم تمريره).
	 * 5. انتظار استجابة الـ API الأساسي للأداة.
	 *
	 * ⏳ Waits for full page readiness, including: 1. Complete HTML load. 2. Current
	 * URL contains the expected path segment. 3. Running `waitForPageElements()`
	 * for general checks. 4. Ensuring the main element (if supplied) is visible and
	 * ready. 5. Waiting for the common tool-loading API to finish.
	 *
	 * @param expectedPathSegment الجزء المتوقع في رابط الصفحة Expected URL path
	 *                            segment.
	 * @param mainElementSupplier دالة تُعيد العنصر الرئيسي (يمكن أن تكون null)
	 *                            Supplier for the main element (nullable).
	 * @throws RuntimeException إذا لم تجهز الصفحة أو العناصر كما هو متوقع Throws
	 *                          RuntimeException if readiness checks fail.
	 *
	 *                          📌 الهدف: ضمان أن الصفحة بالكامل جاهزة (UI + API)
	 *                          قبل التفاعل في الاختبار.
	 */
	@Step("⏳ Wait for full page readiness (HTML + Element + API)")
	public void waitForPageReady(String expectedPathSegment, Supplier<WebElement> mainElementSupplier) {
		try {
			// ✅ 1. التأكد من تحميل الصفحة HTML بالكامل
			wait.until(driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState")
					.equals("complete"));
			System.out.println("📥 Loaded HTML content");

			// ✅ 2. التأكد من أن الرابط يحتوي على الجزء المتوقع
			wait.until(driver -> driver.getCurrentUrl().contains(expectedPathSegment));
			System.out.println("📥 URL contains expected path segment: " + expectedPathSegment);

			// 🔄 3. تنفيذ أي انتظار مخصص حسب نوع الصفحة
			waitForPageElements();
			System.out.println("📥 Ran waitForPageElements()");

			// 🎯 4. انتظار العنصر الرئيسي إن وُجد
			if (mainElementSupplier != null) {
				System.out.println("🔍 Waiting for main element to be visible...");

				boolean elementReady = wait.until(driver -> {
					try {
						WebElement el = mainElementSupplier.get();
						boolean displayed = el.isDisplayed();
						System.out.println("✅ Element found and displayed: " + displayed);
						return el.isDisplayed() && el.isEnabled() && el.getAttribute("value").trim().isEmpty()
								&& displayed;
					} catch (Exception e) {
						System.out.println("⚠️ Element not ready yet: " + e.getMessage());
						return false;
					}
				});

				if (!elementReady) {
					throw new RuntimeException("❌ Main element is not ready for interaction.");
				}
			} else {
				System.out.println("ℹ️ No main element specified, skipping element wait");
			}

			// 🌐 5. انتظار انتهاء جميع طلبات الـ API
			waitForToolLoadingApi();

			// 🟢 6. تسجيل النجاح
			Allure.step("📥 Page fully ready for interaction");
			System.out.println("📥 Page fully ready for interaction ✅");

		} catch (Exception e) {
			// 🧠 طباعة الرابط + لقطة شاشة عند الفشل
			String currentUrl = driver.getCurrentUrl();
			System.out.println("❌ Failure while waiting for page: " + currentUrl);
			throw new RuntimeException("❌ Failed to wait for full page readiness", e);
		}
	}

	/**
	 * ⏳ انتظار مخصص لعناصر أساسية في الصفحة - يمكن إعادة تعريفه في الصفحات الفرعية
	 * Optional page-specific element wait (override this in specific Page Objects)
	 */
	public void waitForPageElements() {
		// 🚫 لا شيء افتراضيًا - يُعاد تعريفه عند الحاجة
	}

	/**
	 * ✅ انتظار جاهزية الصفحة للتفاعل (العناصر + تحميل API)
	 *
	 * 🔹 هذه الدالة تنتظر حتى تصبح الصفحة جاهزة للتفاعل عبر: 1. استدعاء دالة انتظار
	 * العناصر الأساسية (إن وُجدت). 2. انتظار استجابة الـ API العام للأدوات.
	 *
	 * Waits for the page to be ready for interaction by: 1. Executing an optional
	 * elements waiter runnable (if provided). 2. Ensuring the common tool-loading
	 * API has finished.
	 *
	 * @param elementsWaiter ميثود اختياري لانتظار عناصر معينة (يمكن تمريره null)
	 *                       Optional runnable to wait for specific elements
	 *                       (nullable).
	 *
	 *                       📌 الهدف: ضمان أن الصفحة والـ API جاهزة قبل أي تفاعل مع
	 *                       المستخدم.
	 */
	@Step("Wait for page to be fully ready (elements + API)")
	public void waitForPageReady(Runnable elementsWaiter) {
		// ⏳ 1. إذا فيه ميثود انتظار عناصر، شغّله
		if (elementsWaiter != null) {
			elementsWaiter.run(); // مثل: () -> homePage.waitForPageElements()
		}

		// ⏳ 2. ثم ننتظر انتهاء جميع طلبات الشبكة (API)
		waitForToolLoadingApi();
	}

	/**
	 * ⏳ انتظار حتى يتم تحميل الـ API العام للأدوات
	 *
	 * 🔹 هذه الدالة تستدعي الـ API الرئيسي للتحقق من جاهزية النظام قبل التفاعل.
	 *
	 * Waits until the common tool-loading API has completed successfully.
	 *
	 * @throws RuntimeException إذا لم يرجع الـ API حالة 200 أو 304 Throws
	 *                          RuntimeException if the API response code is not 200
	 *                          or 304.
	 *
	 *                          📌 الهدف: منع بدء الاختبار قبل أن تصبح البيانات
	 *                          الأساسية للأدوات جاهزة.
	 */
	@Step("Wait for tool-loading API to complete")
	public void waitForToolLoadingApi() {
		Response response = RestAssured.given().relaxedHTTPSValidation().when()
				.get("https://falak.ksaa.gov.sa/api/new-public/corpus");

		if (response.getStatusCode() != 200 && response.getStatusCode() != 304) {
			throw new RuntimeException("❌ Tool loading API not completed (Status: " + response.getStatusCode() + ")");
		}

		System.out.println("✅ Tool loading API responded: " + response.getStatusCode());
		Allure.step("✅ Tool loading API responded: " + response.getStatusCode());
	}

	/**
	 * 🔍 يسجل خصائص العنصر للعرض عند فشل التفاعل
	 *
	 * 🔹 تسجل هذه الدالة أهم خصائص عنصر (WebElement) مثل: العرض، التفعيل، الموقع،
	 * نوع التاغ، أسماء الكلاسات، وإذا كان محجوبًا بعنصر آخر.
	 *
	 * Logs important attributes of a WebElement for debugging purposes, including:
	 * visibility, enabled state, location, tag name, class names, and whether it is
	 * visually obstructed by another element.
	 *
	 * @param element عنصر الويب المراد فحصه وتسجيل خصائصه The WebElement to inspect
	 *                and log details for.
	 *
	 *                📌 الهدف: تسهيل اكتشاف سبب فشل التفاعل مع العناصر (مثل
	 *                الأيقونات) أثناء الاختبار.
	 */
	@Step("Log details of a WebElement for debugging")
	public void logIconDetails(WebElement element) {
		try {
			// ✅ تسجيل الخصائص الأساسية
			System.out.println("DISPLAYED: " + element.isDisplayed()); // هل الأيقونة مرئية؟
			System.out.println("ENABLED: " + element.isEnabled()); // هل يمكن التفاعل معها؟
			System.out.println("LOCATION: " + element.getLocation()); // موقعها في الصفحة
			System.out.println("TAG: " + element.getTagName()); // نوع العنصر (tag)
			System.out.println("CLASS: " + element.getAttribute("class")); // أسماء الكلاسات

			// ⚠️ التحقق مما إذا كانت الأيقونة محجوبة بعنصر آخر
			Boolean isBlocked = (Boolean) ((JavascriptExecutor) driver).executeScript("""
					    const el = arguments[0];
					    const rect = el.getBoundingClientRect();
					    const elAtPoint = document.elementFromPoint(rect.left + 5, rect.top + 5);
					    return el !== elAtPoint && !el.contains(elAtPoint);
					""", element);

			System.out.println("⚠️ Is icon blocked visually? " + isBlocked);

		} catch (Exception e) {
			// ⚠️ تسجيل أي خطأ يحدث أثناء محاولة جمع التفاصيل
			System.out.println("⚠️ Failed to log icon details: " + e.getMessage());
		}
	}

}
