package com.falak.qa.net;

import java.time.Instant;

public final class TriageEvent {

	public enum Kind {
		RESPONSE_ERROR, LOAD_FAILED
	}

	public final Kind kind;
	public final String url; // قد يكون URL أو requestId إذا لم يتوفر URL
	public final String requestIdOrType; // requestId في response أو type في loadFailed
	public final Integer status; // قد تكون null في loadFailed
	public final String errorText; // رسالة الخطأ إن وجدت
	public final long encodedLength; // طول البيانات المستلمة (إن توفر)
	public final Instant time;

	/**
	 * 🧱 مُنشئ حدث (TriageEvent)
	 *
	 * 	🔹 يُستخدم لتمثيل حدث شبكة تم التقاطه (خطأ استجابة، فشل تحميل، ...). 🔹 يضمّ
	 * معلومات مثل نوع الحدث، الرابط/المعرّف، كود الحالة، نصّ الخطأ، حجم البيانات،
	 * والوقت.
	 *
	 * 🧱 Triage event constructor.
	 *
	 * 🔹 Represents a captured network event (response error, load failure, ...).
	 * 🔹 Holds details like kind, URL/request id, status code, error text, encoded
	 * length, and timestamp.
	 *
	 * @param kind            نوع الحدث | The kind of triage event
	 * @param url             الرابط أو المعرّف المعروض | The URL (or identifier)
	 *                        associated with the event
	 * @param requestIdOrType معرّف الطلب أو نوع الفشل | Request ID or failure type
	 * @param status          كود الحالة (قد يكون null في حالات الفشل قبل الاستجابة)
	 *                        | HTTP status code (nullable on pre-response failures)
	 * @param errorText       نص الخطأ (إن وجد) | Error message (if any)
	 * @param encodedLength   حجم البيانات المرمّزة | Encoded data length
	 * @param time            الطابع الزمني للحدث | Event timestamp
	 */
	public TriageEvent(Kind kind, String url, String requestIdOrType, Integer status, String errorText,
			long encodedLength, Instant time) {
		this.kind = kind;
		this.url = url;
		this.requestIdOrType = requestIdOrType;
		this.status = status;
		this.errorText = errorText;
		this.encodedLength = encodedLength;
		this.time = time;
	}

	/**
	 * 🏭 مصنع مختصر لحدث "خطأ استجابة" (status ≥ 400)
	 *
	 * 🏭 Convenience factory for a "response error" event (status ≥ 400).
	 *
	 * @param url       رابط الطلب الذي أعاد الخطأ | The request URL that returned
	 *                  an error
	 * @param requestId معرّف الطلب (من CDP/Proxy) | Request ID (from CDP/Proxy)
	 * @param status    كود الحالة (HTTP) | HTTP status code
	 * @param len       حجم البيانات المرمّزة | Encoded data length
	 * @param t         الطابع الزمني للحدث | Event timestamp
	 * @return كائن TriageEvent مهيأ كخطأ استجابة | A TriageEvent representing a
	 *         response error
	 */
	public static TriageEvent responseError(String url, String requestId, int status, long len, Instant t) {
		return new TriageEvent(Kind.RESPONSE_ERROR, url, requestId, status, null, len, t);
	}

	/**
	 * 🏭 مصنع مختصر لحدث "فشل تحميل" (قبل وصول الاستجابة)
	 *
	 * 🏭 Convenience factory for a "loading failed" event (pre-response failure).
	 *
	 * @param urlOrReqId الرابط أو معرّف الطلب (إذا كان الرابط غير متاح) | URL or
	 *                   request ID (when URL not available)
	 * @param type       نوع الفشل (مثلاً: Network/Other) | Failure type (e.g.,
	 *                   Network/Other)
	 * @param err        نصّ الخطأ إن وُجد | Error text if available
	 * @param t          الطابع الزمني للحدث | Event timestamp
	 * @return كائن TriageEvent مهيأ كفشل تحميل | A TriageEvent representing a load
	 *         failure
	 */
	public static TriageEvent loadFailed(String urlOrReqId, String type, String err, Instant t) {
		return new TriageEvent(Kind.LOAD_FAILED, urlOrReqId, type, null, err, 0L, t);
	}

	/**
	 * 🧾 تمثيل نصّي مُختصر للحدث — مفيد للتصحيح والسجلات
	 *
	 * 🧾 Concise string representation for debugging and logs.
	 *
	 * @return سلسلة تحتوي النوع، الرابط/المعرّف، الكود، الخطأ، الحجم، والوقت | A
	 *         compact line with kind, url/id, status, error, length, and time
	 */
	@Override
	public String toString() {
		return kind + " url=" + url + " type/reqId=" + requestIdOrType + " status=" + status + " err=" + errorText
				+ " len=" + encodedLength + " at=" + time;
	}

}
