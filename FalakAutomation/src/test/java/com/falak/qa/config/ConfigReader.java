package com.falak.qa.config;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

//	✅ ConfigReader: المسؤول عن قراءة إعدادات المشروع من ملف config.properties
// This class loads and provides access to configuration settings from a properties file
public class ConfigReader {

	//	🧠 كائن لتخزين الإعدادات على شكل (مفتاح = قيمة)
	// This object holds the key-value pairs from the properties file
	private static Properties prop;

    /**
     * ✅ تحميل جميع الإعدادات من ملف config.properties
     *
     * 🔹 هذه الدالة تُستخدم لتهيئة ملف الخصائص (Properties) وتحميل جميع القيم 
     *    المعرّفة فيه مثل: البيئة، المتصفح، وغيرها من الإعدادات الخاصة بالاختبارات.
     *
     * ✅ Loads all configuration properties from the `config.properties` file.
     *    Useful for initializing environment, browser type, and other test settings.
     *
     * @return كائن Properties يحتوي على جميع الإعدادات | Properties object containing the loaded settings
     * @throws RuntimeException إذا فشل تحميل الملف أو لم يتم العثور عليه | If the file cannot be loaded or found
     *
     * 📌 الهدف: تهيئة بيئة الاختبار من ملف إعدادات خارجي بدلًا من تثبيت القيم في الكود.
     */
    @Step("📥 Load configuration from config.properties")
    public static Properties initProperties() {
        prop = new Properties(); // 🧱 إنشاء كائن جديد من Properties

        try {
            // 📂 تحديد مسار ملف الإعدادات داخل المشروع
            FileInputStream ip = new FileInputStream("src/test/resources/config.properties");

            // 📥 تحميل البيانات من الملف إلى الكائن prop
            prop.load(ip);

            // 📝 توثيق نجاح تحميل الإعدادات في تقرير Allure
            Allure.step("📥 تم تحميل ملف الإعدادات بنجاح من config.properties");

        } catch (IOException e) {
            // ❌ في حال وجود مشكلة في فتح الملف أو قراءته
            String errorMsg = "❌ فشل تحميل ملف الإعدادات: " + e.getMessage();

            // 🔍 تسجيل المشكلة في الكونسول وتقرير Allure
            System.out.println(errorMsg);
            Allure.step(errorMsg);

            // 🚫 إيقاف التنفيذ إذا لم يتم العثور على الملف
            throw new RuntimeException("⚠️ لا يمكن تحميل ملف الإعدادات، تحقق من المسار أو وجود الملف.");
        }

        // 🔄 إعادة الكائن المحمّل
        return prop;
    }


    /**
     * 🔑 جلب قيمة إعداد معينة باستخدام اسم المفتاح
     *
     * 🔹 تُستخدم هذه الدالة عند الحاجة لقراءة إعداد معيّن من ملف الخصائص، 
     *    مثل (browser = chrome) أو (environment = dev).
     *
     * 🔑 Retrieves the value of a property by its key from the loaded Properties object.
     *    Useful for accessing specific config values (e.g., browser type, environment).
     *
     * @param key اسم المفتاح المطلوب | Key of the property to retrieve
     * @return القيمة المقابلة للمفتاح، أو null إذا لم توجد | Value of the property, or null if not found
     *
     * 📌 الهدف: تمكين استدعاء الإعدادات بسهولة بدلًا من إعادة كتابة القيم يدويًا.
     */
    @Step("🔑 Get property value for key: {key}")
    public String getProperty(String key) {
        return prop.getProperty(key);
    }

}
