package com.falak.qa.models.ngrams;

import com.falak.qa.base.BaseApiClient;

import io.qameta.allure.Allure;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NGramsApiClient extends BaseApiClient {

	private final String toolId;
	private final Map<String, Object> queryParams;

	/**
	 * 🏗️ مُنشئ كائن NGramsApiClient
	 *
	 * 🔹 يُستخدم لتهيئة الكائن بالمعرّف الخاص بالأداة (toolId) مع
	 * الفلاتر/الباراميترز المطلوبة للاتصال بالـ API. هذا يضمن أن جميع الاستدعاءات
	 * اللاحقة ستكون مخصّصة للأداة المطلوبة.
	 *
	 * 🏗️ Constructor for the NGramsApiClient.
	 *
	 * 🔹 Initializes the client with the tool’s unique ID (toolId) and the query
	 * parameters needed for API requests. Ensures all subsequent calls are tied to
	 * the correct tool.
	 *
	 * @param toolId      معرّف الأداة (UUID أو String خاص بالأداة) | The unique
	 *                    tool ID
	 * @param queryParams خريطة الفلاتر والباراميترز المطلوب تمريرها | A map of
	 *                    filters and query parameters
	 */
	public NGramsApiClient(String toolId, Map<String, Object> queryParams) {
		this.toolId = toolId;
		this.queryParams = queryParams;
	}

	/**
	 * 🌐 تحديد نهاية رابط API الخاص بأداة N-Gram
	 *
	 * 🔹 يُستخدم هذا الميثود لتجميع الرابط الأساسي الذي سيتم الاتصال به عبر الـ
	 * API. الرابط يُبنى بشكل ديناميكي باستخدام toolId.
	 *
	 * 🌐 Defines the specific API endpoint for the N-Gram tool.
	 *
	 * 🔹 Dynamically constructs the endpoint path using the provided toolId,
	 * ensuring requests target the correct resource.
	 *
	 * @return نهاية الرابط المطلوب | The API endpoint path string
	 */
	@Override
	protected String getToolEndpoint() {
		return "/api/tools/ngram/" + toolId;
	}

	/**
	 * 📦 تمرير الفلاتر (الباراميترز) الخاصة بالبحث إلى API
	 *
	 * 🔹 يوفّر هذا الميثود جميع الباراميترز المطلوبة في شكل خريطة. غالبًا تشمل:
	 * الكلمات، الحد الأقصى، الصفحة، أو أي فلتر آخر مدعوم.
	 *
	 * 📦 Supplies the query parameters for the API request.
	 *
	 * 🔹 Returns all required filters in a map structure. Commonly includes:
	 * keyword, page number, limits, or other supported filters.
	 *
	 * @return خريطة الباراميترز المطلوبة | A map of query parameters
	 */
	@Override
	protected Map<String, Object> getQueryParams() {
		return queryParams;
	}

	/**
	 * 📥 جلب نتائج أداة الـ N-Grams ككائن Java (POJO)
	 *
	 * 🔹 يُرسل هذا الميثود طلب GET إلى خادم الـ API، ويتحقق من نجاح الاستجابة (كود
	 * 200)، ثم يُحوّل البيانات الراجعة من JSON إلى كائن Java من نوع
	 * {@link NGramsResponse}.
	 *
	 * 📥 Fetches the N-Grams tool results as a Java POJO.
	 *
	 * 🔹 Sends an HTTP GET request to the API, validates that the response status
	 * code is 200 (OK), and converts the JSON response body into a
	 * {@link NGramsResponse} object.
	 *
	 * @return كائن NGramsResponse يحتوي على النتائج | An {@link NGramsResponse}
	 *         object containing the list of results returned from the API
	 *
	 * @throws RuntimeException إذا فشل الطلب أو لم يكن كود الاستجابة 200 Throws
	 *                          RuntimeException if the request fails or the
	 *                          response code is not 200
	 *
	 *                          📌 الهدف: تبسيط عملية استدعاء API وإرجاع البيانات
	 *                          بشكل جاهز للاستخدام في الاختبارات.
	 */
	public NGramsResponse getResultsAsPojo() {
		// 📡 إرسال طلب GET إلى الخادم | Send a GET request to the server
		Response response = sendGetRequest();

		// 🛡️ التحقق من نجاح الاستجابة | Validate HTTP status code
		validateStatusCode(response, 200);

		// 🔄 تحويل JSON إلى كائن Java | Parse response into POJO
		return parseResponseAs(response, NGramsResponse.class);
	}

	/**
	 * 📡 جلب جميع نتائج أداة N-Gram من خلال API (صفحة تلو الأخرى)
	 *
	 * 🔹 يُستخدم هذا الميثود لاسترجاع **كل النتائج** الخاصة بأداة N-Gram عبر
	 * استدعاءات متتابعة للـ API مع دعم تقسيم النتائج إلى صفحات (Pagination). يقوم
	 * بالتكرار حتى نفاد الصفحات أو عند كشف تكرار النتائج.
	 *
	 * 📡 Fetch all N-Gram tool results from the API (page by page).
	 *
	 * 🔹 This method retrieves **all results** for the N-Gram tool by making
	 * multiple paginated API calls. It continues until no further results are
	 * available or duplication is detected.
	 *
	 * @param toolId        المعرّف الفريد للأداة | The unique identifier of the
	 *                      tool
	 * @param initialParams الفلاتر المستخدمة في البحث | The initial filter
	 *                      parameters
	 * @return قائمة بجميع النتائج المجمعة | A list of all collected N-Gram results
	 *
	 * @throws RuntimeException في حال تجاوز الحد الأقصى للصفحات أو استجابة API غير
	 *                          طبيعية Throws RuntimeException if maximum safe
	 *                          iterations are exceeded or the API response is
	 *                          abnormal
	 *
	 *                          📌 الهدف: ضمان جمع النتائج كاملة بشكل موثوق عبر جميع
	 *                          الصفحات بدون فقدان أو تكرار.
	 */
	public static List<NGramResult> getAllResults(String toolId, NGramsFilterParams initialParams) {
		// 📦 قائمة لتخزين جميع النتائج | List to store all fetched results
		List<NGramResult> allResults = new ArrayList<>();

		// 🧠 متغير لتتبع أول كلمة بكل صفحة | Track first word per page to detect
		// duplicates
		String lastFirstWord = null;

		// 🔢 بداية الترقيم الصفحي (0-based) | Page index (starts from 0)
		int page = 0;

		// 🎯 تحديد الحد الأقصى لكل صفحة | Max number of items per page
		int limit = initialParams.getLimit() > 0 ? initialParams.getLimit() : 1000;

		// 🛡️ حماية من الحلقات اللانهائية | Safety cap for infinite loop
		int maxSafeIterations = 10000;

		// 🔁 التكرار عبر الصفحات | Loop through pages
		while (true) {
			// 🧭 تحديث رقم الصفحة في الفلاتر | Update page number
			initialParams.setPageNumber(page);

			// 🧪 تحويل الفلاتر إلى خريطة لإرسالها | Convert filters into map
			Map<String, Object> currentParams = initialParams.toMap();
			currentParams.put("limit", limit);

			// 📤 إنشاء عميل API | Build API client for current request
			NGramsApiClient client = new NGramsApiClient(toolId, currentParams);

			// 📥 جلب النتائج وتحويلها إلى كائن Java | Fetch and parse response
			NGramsResponse response = client.getResultsAsPojo();
			List<NGramResult> resultPage = response.getResult();

			// 🛑 إذا فارغة → انتهينا | Stop if page is empty
			if (resultPage == null || resultPage.isEmpty()) {
				break;
			}

			// 🧐 التقاط أول كلمة للمقارنة | Capture first word for duplication check
			String firstWord = resultPage.get(0).getWord();

			// 🛑 وقف عند التكرار | Stop if duplicate detected
			if (firstWord.equals(lastFirstWord)) {
				System.out.println("⚠️ Duplicate first word detected: " + firstWord + ". Stopping.");
				Allure.step("⚠️ Duplicate first word detected: " + firstWord + ". Stopping.");
				break;
			}

			// ✅ أضف النتائج للقائمة | Add results to master list
			allResults.addAll(resultPage);
			lastFirstWord = firstWord;

			// 🧾 سجل معلومات الصفحة | Log page details
			System.out.println("📄 Fetched page: " + page + " | 🔤 First word: " + firstWord);
			Allure.step("📄 Fetched page: " + page + " | 🔤 First word: " + firstWord);

			// ✅ إذا أقل من الحد → انتهى | Stop if last page
			if (resultPage.size() < limit) {
				break;
			}

			// ⏭️ انتقل للصفحة التالية | Go to next page
			page++;

			// 🛑 حماية من الدوران | Prevent unsafe loops
			if (page >= maxSafeIterations) {
				throw new RuntimeException(
						"🚨 Too many pages fetched. Possible infinite loop or unfiltered API response.");
			}
		}

		// 🧮 سجل العدد الكلي | Log total results
		System.out.println("📦 Total N-Gram results fetched: " + allResults.size());
		Allure.step("📦 Total N-Gram results fetched: " + allResults.size());

		// 📤 إرجاع النتائج | Return final list
		return allResults;
	}

}
