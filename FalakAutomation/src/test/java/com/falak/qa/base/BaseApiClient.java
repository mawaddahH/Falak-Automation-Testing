package com.falak.qa.base;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public abstract class BaseApiClient {

	// 🔗 الرابط الأساسي للمنصة
	protected static final String BASE_URL = "https://falak.ksaa.gov.sa";

	// 🧭 كل أداة ترث وتنفذ هذه الميثود لتحديد رابطها
	protected abstract String getToolEndpoint();

	// 🧮 كل أداة تحدد الـ Parameters الخاصة بها
	protected abstract Map<String, Object> getQueryParams();

	/**
	 * ✅ إرسال طلب GET مع تهيئة تلقائية
	 *
	 * 🔹 تُستخدم هذه الدالة لإرسال طلب GET إلى واجهة API الخاصة بالأداة مع جميع
	 * الإعدادات المشتركة (مثل: Base URI, Headers, Query Params).
	 *
	 * This method sends a GET request to the tool's API endpoint with all
	 * preconfigured settings (Base URI, headers, and query parameters).
	 *
	 * @return Response كائن يحتوي على الاستجابة من السيرفر The response object
	 *         returned by the server.
	 *
	 *         📌 مفيد عند اختبار استرجاع البيانات بدون الحاجة لإعادة كتابة منطق
	 *         الإعداد في كل مرة.
	 */
	@Step("Send GET request to tool endpoint with preconfigured settings")
	public Response sendGetRequest() {
		return buildRequest().get();
	}

	/**
	 * 📩 إرسال طلب POST (مع جسم بيانات)
	 *
	 * 🔹 تُستخدم هذه الدالة لإرسال طلب POST إلى واجهة API الخاصة بالأداة، مع
	 * إمكانية تمرير جسم (Body) للطلب مثل JSON أو أي كائن.
	 *
	 * This method sends a POST request to the tool's API endpoint, allowing a
	 * request body (e.g., JSON object) to be included.
	 *
	 * @param body جسم الطلب المراد إرساله (عادةً بيانات بصيغة JSON) The request
	 *             body (usually JSON) to be sent with the POST call.
	 *
	 * @return Response كائن يحتوي على الاستجابة من السيرفر The response object
	 *         returned by the server.
	 *
	 *         📌 مفيد عند اختبار العمليات التي تتطلب إرسال بيانات مثل: إنشاء كيان
	 *         جديد (Create Entity).
	 */
	@Step("Send POST request to tool endpoint with body")
	public Response sendPostRequest(Object body) {
		return buildRequest().body(body).post();
	}

	/**
	 * 🧱 إعداد الطلب المشترك (Request Builder)
	 *
	 * 🔹 هذه الدالة مسؤولة عن تهيئة الطلبات (GET/POST) مع جميع الإعدادات الموحدة: -
	 * Base URI (رابط الأساس) - Base Path (مسار الأداة) - Query Parameters
	 * (المحددات/الفلاتر) - Headers (الرؤوس الافتراضية) - Content-Type (نوع
	 * البيانات: JSON)
	 *
	 * This private method builds and returns a preconfigured request specification
	 * with: - Base URI - Base Path (tool endpoint) - Query parameters - Default
	 * headers - Content type as JSON
	 *
	 * @return RequestSpecification كائن جاهز لإرسال طلب HTTP A preconfigured
	 *         RequestSpecification ready for execution.
	 *
	 *         📌 الهدف هو تقليل التكرار وتوحيد الإعدادات لجميع الطلبات في مكان واحد
	 *         (Single Point of Configuration).
	 */
	@Step("Build base request specification with common settings")
	private RequestSpecification buildRequest() {
		return RestAssured.given().baseUri(BASE_URL).basePath(getToolEndpoint()).queryParams(getQueryParams())
				.headers(getDefaultHeaders()).contentType("application/json");
	}

	/**
	 * 🧾 الرؤوس الافتراضية لطلبات الـ API
	 *
	 * 🔹 تعيد خريطة تحتوي على الترويسة الأساسية: - Accept: application/json
	 *
	 * Returns the default HTTP headers used for API requests: - Accept:
	 * application/json
	 *
	 * @return Map<String,String> خريطة بالرؤوس الافتراضية A map of default headers
	 *         to send with each request.
	 *
	 *         📌 الهدف: توحيد الرؤوس الافتراضية في مكان واحد وتفادي التكرار.
	 */
	@Step("Build default HTTP headers (Accept: application/json)")
	protected Map<String, String> getDefaultHeaders() {
		return Map.of("Accept", "application/json");
	}

	/**
	 * 🧪 التحقق من كود حالة الاستجابة (Status Code)
	 *
	 * 🔹 تتحقق من أن كود الحالة يطابق القيمة المتوقعة؛ إذا لم يطابق، ترمي
	 * AssertionError
	 *
	 * Validates that the HTTP status code of the response equals the expected
	 * value. Throws AssertionError if it does not match.
	 *
	 * @param response     كائن الاستجابة من RestAssured The HTTP response to
	 *                     validate.
	 * @param expectedCode الكود المتوقع (مثل 200 أو 400 أو 500) The expected HTTP
	 *                     status code.
	 *
	 * @throws AssertionError عند عدم التطابق بين الكود الفعلي والمتوقع If the
	 *                        actual status code differs from the expected one.
	 *
	 *                        📌 مفيد لتثبيت التوقعات القياسية قبل التحقق من محتوى
	 *                        الاستجابة.
	 */
	@Step("Validate HTTP status code equals {expectedCode}")
	public void validateStatusCode(Response response, int expectedCode) {
		if (response.statusCode() != expectedCode) {
			throw new AssertionError("Expected " + expectedCode + " but got " + response.statusCode());
		}
	}

	/**
	 * 📦 تحويل جسم الاستجابة إلى كائن (POJO)
	 *
	 * 🔹 يقوم بتحويل/تفريغ (deserialize) جسم الاستجابة إلى كلاس الهدف المحدد.
	 *
	 * Parses (deserializes) the response body into the specified POJO type.
	 *
	 * @param response     كائن الاستجابة الذي سيتم تحويله The HTTP response whose
	 *                     body will be parsed.
	 * @param responseType نوع الكلاس الهدف لعملية التحويل (مثلاً MyDto.class)
	 *                     Target class type for deserialization (e.g.,
	 *                     MyDto.class).
	 * @param <T>          النوع العام للكائن المعاد Generic type of the returned
	 *                     object.
	 *
	 * @return T كائن من النوع المطلوب بعد التحويل A POJO instance of the requested
	 *         type.
	 *
	 *         📌 تأكد أن الـ POJO مزوّد بحقول/مُحولات متوافقة مع JSON القادم.
	 */
	@Step("Parse response body into POJO")
	public <T> T parseResponseAs(Response response, Class<T> responseType) {
		return response.as(responseType);
	}
}
