package com.falak.qa.models.ngrams;

import java.util.List;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;

public class NGramsResponse {
	private List<NGramResult> result;
	private int totalRecords;

	/**
	 * 📦 إرجاع قائمة النتائج (NGramResult)
	 * 
	 * 📦 Returns the list of NGram results
	 *
	 * @return قائمة النتائج | List of NGramResult objects
	 */
	@Step("📦 Get NGram results list")
	public List<NGramResult> getResult() {
		try {
			return result;
		} catch (Exception e) {
			String msg = "❌ Failed to retrieve NGram results list";
			Allure.attachment("Get Result Error", msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * 📦 تعيين قائمة النتائج (NGramResult)
	 * 
	 * 📦 Sets the list of NGram results
	 *
	 * @param result قائمة النتائج المراد تعيينها | The list of NGramResult to set
	 */
	@Step("📦 Set NGram results list")
	public void setResult(List<NGramResult> result) {
		try {
			this.result = result;
			Allure.step("✅ NGram results list set successfully (size: " + (result != null ? result.size() : 0) + ")");
		} catch (Exception e) {
			String msg = "❌ Failed to set NGram results list";
			Allure.attachment("Set Result Error", msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * 🔢 جلب إجمالي عدد السجلات
	 * 
	 * 🔢 Returns the total number of records
	 *
	 * @return إجمالي السجلات | The total number of records
	 */
	@Step("🔢 Get total number of records")
	public int getTotalRecords() {
		try {
			return totalRecords;
		} catch (Exception e) {
			String msg = "❌ Failed to retrieve total records";
			Allure.attachment("Get Total Records Error", msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * 🔢 تعيين إجمالي عدد السجلات
	 * 
	 * 🔢 Sets the total number of records
	 *
	 * @param totalRecords العدد المراد تعيينه | The total record count to set
	 */
	@Step("🔢 Set total number of records: {0}")
	public void setTotalRecords(int totalRecords) {
		try {
			this.totalRecords = totalRecords;
			Allure.step("✅ Total records set to: " + totalRecords);
		} catch (Exception e) {
			String msg = "❌ Failed to set total records";
			Allure.attachment("Set Total Records Error", msg);
			throw new RuntimeException(msg, e);
		}
	}

}
