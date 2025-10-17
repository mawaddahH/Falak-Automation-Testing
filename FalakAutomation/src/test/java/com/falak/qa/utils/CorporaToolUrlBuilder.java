package com.falak.qa.utils;

import com.falak.qa.config.EnvironmentConfigLoader;
import com.falak.qa.enums.CorporaName;
import com.falak.qa.enums.ToolsName;

public class CorporaToolUrlBuilder {
    /**
     * يبني الرابط الكامل لصفحة "النبذة" الخاصة بمدونة معينة.
     * Builds the full URL of the overview (description) page for a specific corpora.
     *
     * @param corpora اسم المدونة (من نوع CorporaName enum) The target corpora (from CorporaName enum)
     * @return الرابط الكامل لصفحة المدونة The full URL of the corpora overview page
     */
    public static String buildCorporaOverviewUrl(CorporaName corpora) {
        // 🔗 الحصول على الرابط الأساسي من ملف الإعدادات
        String corporaBaseUrl = EnvironmentConfigLoader.getUrl("corporaUrl");

        // 🧩 إضافة UUID الخاصة بالمدونة لبناء الرابط الكامل
        return corporaBaseUrl + "/" + corpora.getUuid();
    }

    /**
     * يبني الرابط الكامل لأداة معينة ضمن مدونة معينة.
     * Builds the full URL for a specific tool under a given corpora.
     *
     * @param corpora اسم المدونة (من نوع CorporaName enum) The target corpora (from CorporaName enum)
     * @param tool اسم الأداة (من نوع ToolsName enum) The tool to open (from ToolsName enum)
     * @return الرابط الكامل للأداة داخل المدونة The full URL of the tool under the given corpora
     */
    public static String buildToolUrl(CorporaName corpora, ToolsName tool) {
        // 🔗 الحصول على الرابط الأساسي من ملف الإعدادات
        String corporaBaseUrl = EnvironmentConfigLoader.getUrl("corporaUrl");

        // 🧼 حذف "/corpora" من الرابط لتكوين روابط الأدوات بشكل صحيح
        String baseUrl = corporaBaseUrl.replace("/corpora", "");

        // 🧩 بناء رابط الأداة باستخدام segment و UUID
        return baseUrl + "/" + tool.getPathSegment() + "/" + corpora.getUuid();
    }

    /**
     * يستخرج UUID الخاص بالأداة من رابط الأداة.
     * Extracts the tool UUID from the tool URL.
     *
     * @param url الرابط الكامل للأداة The full URL of the tool
     * @return UUID الخاص بالأداة The tool's UUID
     */
    public static String extractToolUuidFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL is null or empty.");
        }

        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }

    
    
}
