package com.falak.qa.models.ngrams;

import java.util.HashMap;
import java.util.Map;

/**
 * 🔍 كائن يمثل جميع فلاتر البحث الممكنة لأداة التتابعات اللفظية (N-Grams)
 */
public class NGramsFilterParams {

    // 🧮 الخصائص الافتراضية
    private int n = 2;
    private int minFreq = 2;
    private int maxFreq = 500000;
    private int pageNumber = 0;
    private int limit = 1000;
    private String excludeRegex = "1";

    private String excludeWords = "";
    private String containWords = "";
    private String startWithWords = "";
    private String endWithWords = "";

    // 🧱 Setters بتصميم Builder Pattern
    public NGramsFilterParams withN(int n) {
        this.n = n;
        return this;
    }

    public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getMinFreq() {
		return minFreq;
	}

	public void setMinFreq(int minFreq) {
		this.minFreq = minFreq;
	}

	public int getMaxFreq() {
		return maxFreq;
	}

	public void setMaxFreq(int maxFreq) {
		this.maxFreq = maxFreq;
	}

	public String getExcludeRegex() {
		return excludeRegex;
	}

	public void setExcludeRegex(String excludeRegex) {
		this.excludeRegex = excludeRegex;
	}

	public String getExcludeWords() {
		return excludeWords;
	}

	public void setExcludeWords(String excludeWords) {
		this.excludeWords = excludeWords;
	}

	public String getContainWords() {
		return containWords;
	}

	public void setContainWords(String containWords) {
		this.containWords = containWords;
	}

	public String getStartWithWords() {
		return startWithWords;
	}

	public void setStartWithWords(String startWithWords) {
		this.startWithWords = startWithWords;
	}

	public String getEndWithWords() {
		return endWithWords;
	}

	public void setEndWithWords(String endWithWords) {
		this.endWithWords = endWithWords;
	}

	public NGramsFilterParams withMinFreq(int minFreq) {
        this.minFreq = minFreq;
        return this;
    }

    public NGramsFilterParams withMaxFreq(int maxFreq) {
        this.maxFreq = maxFreq;
        return this;
    }

    public NGramsFilterParams withLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public NGramsFilterParams withPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public NGramsFilterParams withExcludeWords(String excludeWords) {
        this.excludeWords = excludeWords;
        return this;
    }

    public NGramsFilterParams withContainWords(String containWords) {
        this.containWords = containWords;
        return this;
    }

    public NGramsFilterParams withStartWithWords(String startWithWords) {
        this.startWithWords = startWithWords;
        return this;
    }

    public NGramsFilterParams withEndWithWords(String endWithWords) {
        this.endWithWords = endWithWords;
        return this;
    }

    public NGramsFilterParams withExcludeRegex(String excludeRegex) {
        this.excludeRegex = excludeRegex;
        return this;
    }

    // ✅ تحويل الخصائص إلى Map لتمريرها إلى RestAssured
    public Map<String, Object> toMap() {
        Map<String, Object> params = new HashMap<>();
        params.put("n", n);
        params.put("minFreq", minFreq);
        params.put("maxFreq", maxFreq);
        params.put("limit", limit);
        params.put("pageNumber", pageNumber);
        params.put("excludeWords", excludeWords);
        params.put("containWords", containWords);
        params.put("startWithWords", startWithWords);
        params.put("endWithWords", endWithWords);
        params.put("excludeRegex", excludeRegex);
        return params;
    }
    
 // 📌 Getter and Setter for limit
    public int getLimit() {
        return limit;
    }

    public NGramsFilterParams setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    // 📌 Getter and Setter for pageNumber
    public int getPageNumber() {
        return pageNumber;
    }

    public NGramsFilterParams setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

}
