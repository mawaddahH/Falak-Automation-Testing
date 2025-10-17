package com.falak.qa.models.ngrams;

public class NGramResult {
	private String word;
	private int count;

	/**
	 * 📝 يُرجع قيمة الكلمة المخزنة في هذا الكائن.
	 *
	 * 🔹 يُستخدم هذا الميثود عادة عند التعامل مع النتائج المسترجعة من API أو قاعدة
	 * بيانات حيث نحتاج إلى قراءة الكلمة المرتبطة بعدد تكراراتها.
	 *
	 * 📝 Returns the value of the word stored in this object. Commonly used when
	 * processing results fetched from APIs or databases, to retrieve the word
	 * associated with its occurrence count.
	 *
	 * @return الكلمة الحالية | The current word value
	 */
	public String getWord() {
		return word;
	}

	/**
	 * 📝 يضبط قيمة الكلمة المخزنة في هذا الكائن.
	 *
	 * 🔹 يُستخدم لتحديث أو تعيين الكلمة قبل تخزينها أو استخدامها في الاختبار.
	 *
	 * 📝 Sets the value of the word in this object. Useful for updating or
	 * assigning the word before persisting or using it in tests.
	 *
	 * @param word الكلمة الجديدة | The new word value
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * 🔢 يُرجع عدد التكرارات المرتبط بالكلمة.
	 *
	 * 🔹 يُستخدم للتحقق من عدد مرات ظهور الكلمة في النصوص أو الجداول.
	 *
	 * 🔢 Returns the count associated with the word. Typically used to verify the
	 * frequency of word occurrences in texts or tables.
	 *
	 * @return عدد التكرارات | The occurrence count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * 🔢 يضبط عدد التكرارات المرتبط بالكلمة.
	 *
	 * 🔹 يُستخدم لتحديث أو تعيين القيمة العددية الخاصة بتكرار الكلمة في النتائج.
	 *
	 * 🔢 Sets the count associated with the word. Useful for updating or assigning
	 * the frequency of the word in the results.
	 *
	 * @param count العدد الجديد للتكرار | The new occurrence count
	 */
	public void setCount(int count) {
		this.count = count;
	}

}
