package com.falak.qa.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;

import java.io.File;
import java.io.IOException;

//	✅ EnvironmentConfigLoader كلاس مخصص لتحميل إعدادات الروابط الخاصة بالبيئة من
//  ملف JSON This class loads URLs and settings from the config-environment.json file

public class EnvironmentConfigLoader {

	//	🧠 يحتوي على بيانات البيئة النشطة (dev، staging، prod)
	// Holds the currently loaded environment block
	private static JsonNode environmentData;

	// ========== تحميل إعدادات البيئة ==========

    /**
     * ✅ تحميل إعدادات البيئة من ملف JSON
     *
     * 🔹 تُستخدم هذه الدالة لتحميل بيانات البيئة (dev, staging, prod ...) من ملف 
     *    config-environment.json وحفظها في الذاكرة لاستخدامها لاحقًا في الاختبارات.  
     * 🔹 تساعد على الفصل بين الإعدادات الخاصة بكل بيئة تشغيل وضمان مرونة التنقل بينها.
     *
     * ✅ Load environment settings from a JSON file.
     * Reads the config-environment.json and loads the settings of the selected 
     * environment (dev, staging, prod, etc.) into memory for later use.
     *
     * @param environment اسم البيئة المطلوبة (مثل dev أو staging أو prod)  
     *                    The target environment name (e.g., dev, staging, prod)
     * @throws RuntimeException إذا لم يتم العثور على البيئة أو حدث خطأ في تحميل الملف  
     *                          If the environment is missing or file reading fails
     *
     * 📌 الهدف: ضمان تحميل إعدادات صحيحة ومطابقة للبيئة قبل بدء أي اختبار.
     */
    @Step("🔧 Load environment configuration from JSON for environment: {environment}")
    public static void loadConfig(String environment) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            File file = new File("src/test/resources/config-environment.json");
            JsonNode rootNode = mapper.readTree(file);

            environmentData = rootNode.get(environment);

            if (environmentData == null) {
                throw new RuntimeException("🚫 البيئة غير موجودة: " + environment);
            }

            Allure.step("🔧 Loaded configuration for environment: " + environment);

        } catch (IOException e) {
            Allure.step("❌ Failed to load environment config: " + e.getMessage());
            throw new RuntimeException("❌ فشل تحميل ملف البيئة: " + e.getMessage());
        }
    }


	// ========== جلب روابط البيئة ==========

    /**
     * ✅ جلب الرابط الكامل حسب المفتاح المحدد
     *
     * 🔹 تُستخدم هذه الدالة لاستخراج رابط (URL) محدد من الإعدادات المحمّلة للبيئة، 
     *    مثل baseUrl أو corporaUrl.  
     * 🔹 إذا لم يكن المفتاح موجودًا، يتم إرجاع خطأ.
     *
     * ✅ Get the full URL based on the given key.
     * Retrieves a URL (e.g., baseUrl, corporaUrl) from the loaded environment settings.
     *
     * @param key اسم المفتاح (مثل baseUrl أو corporaUrl) | Key name of the URL
     * @return رابط الصفحة المطلوبة | The corresponding URL as a String
     * @throws RuntimeException إذا لم يتم العثور على المفتاح أو كان غير صالح  
     *                          If the key is missing or invalid
     *
     * 📌 الهدف: تسهيل استدعاء الروابط الصحيحة للتنقل أو API requests أثناء الاختبارات.
     */
    @Step("🌐 Retrieve URL for key: {key}")
    public static String getUrl(String key) {
        try {
            String url = environmentData.get(key).get("url").asText();
            Allure.step("🌐 Retrieved URL for [" + key + "]: " + url);
            return url;
        } catch (Exception e) {
            Allure.step("🚫 Failed to retrieve URL for [" + key + "]: " + e.getMessage());
            throw new RuntimeException("⚠️ المفتاح غير موجود أو غير صالح: " + key);
        }
    }


	// ========== جلب وصف الرابط ==========

    /**
     * ✅ جلب وصف الرابط (اختياري للاستخدام في التقارير أو التوثيق)
     *
     * 🔹 تُستخدم هذه الدالة للحصول على وصف النصي للرابط المخزن في ملف البيئة،  
     *    مثل "الرابط الأساسي للتطبيق" أو "رابط مدونة النصوص".  
     * 🔹 تُفيد في التوثيق والتقارير (Allure أو السجلات).
     *
     * ✅ Get the description of the URL (optional).
     * Returns a textual description of the URL for reporting/logging purposes.
     *
     * @param key اسم المفتاح المطلوب | The key name
     * @return وصف الرابط | The URL description (or default message if not available)
     *
     * 📌 الهدف: تحسين توثيق التقارير من خلال إظهار وصف الرابط المستخدم.
     */
    @Step("📑 Retrieve description for key: {key}")
    public static String getDescription(String key) {
        try {
            return environmentData.get(key).get("description").asText();
        } catch (Exception e) {
            return "لا يوجد وصف متاح لهذا المفتاح [" + key + "]";
        }
    }

}
