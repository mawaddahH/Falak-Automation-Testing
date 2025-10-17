package com.falak.qa.config;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.testng.annotations.BeforeSuite;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EnvironmentSetup {

	/**
	 * ✅ تهيئة بيانات Allure قبل بدء أي Suite
	 *
	 * 🔹 تُستخدم هذه الدالة لضبط جميع الملفات الداعمة لتقارير Allure مثل
	 * environment.properties, executor.json, categories.json, history. 🔹 يتم
	 * استدعاؤها مرة واحدة فقط قبل تشغيل جميع الاختبارات (على مستوى Suite).
	 *
	 * ✅ Setup Allure metadata before test suite execution. Ensures that required
	 * Allure metadata files (environment.properties, executor.json,
	 * categories.json, history) are generated/copied properly.
	 *
	 * 📌 الهدف: تجهيز التقارير بحيث تعرض معلومات البيئة، تفاصيل التنفيذ، التصنيفات
	 * (Categories)، وبيانات الـ Trend.
	 */
	@BeforeSuite
	public void setupAllureMetadata() {
		generateEnvironmentProperties();
		generateExecutorJson();
		copyCategoriesJson();
		copyHistoryFromPreviousReport();
	}

	/**
	 * ✅ توليد ملف environment.properties لـ Allure
	 *
	 * 🔹 يقرأ الإعدادات من config.properties + dependency-versions.txt ويضيفها
	 * كخصائص بيئة في تقارير Allure. 🔹 يشمل تفاصيل مثل: نوع المتصفح، البيئة،
	 * النظام، إصدار Java، والمكتبات المستعملة.
	 *
	 * ✅ Generate environment.properties for Allure reports. Reads config.properties
	 * and dependency-versions.txt to populate environment details such as browser,
	 * environment, OS, Java version, and library versions.
	 *
	 * 📌 الهدف: توفير وصف كامل لبيئة التنفيذ داخل تقارير Allure.
	 *
	 * @throws IOException إذا فشل في قراءة الملفات أو حفظ environment.properties If
	 *                     reading/writing environment.properties fails
	 */
	private void generateEnvironmentProperties() {
		try {
			Properties configProps = new Properties();
			InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
			if (input == null)
				throw new FileNotFoundException("❌ config.properties غير موجود في resources.");
			configProps.load(input);

			File resultsDir = new File("allure-results");
			if (!resultsDir.exists())
				resultsDir.mkdirs();

			Properties allureEnv = new Properties();
			allureEnv.setProperty("Browser", configProps.getProperty("browser", "N/A"));
			allureEnv.setProperty("Environment", configProps.getProperty("environment", "N/A"));
			allureEnv.setProperty("Base URL", configProps.getProperty("base.url", "N/A"));
			allureEnv.setProperty("Execution Mode", configProps.getProperty("execution.mode", "N/A"));
			allureEnv.setProperty("Retry Count", configProps.getProperty("retry.count", "0"));
			allureEnv.setProperty("Tester", configProps.getProperty("tester", System.getProperty("user.name")));
			allureEnv.setProperty("OS", System.getProperty("os.name"));
			allureEnv.setProperty("Java Version", System.getProperty("java.version"));

			// 🧠 أسماء ودّية للمكتبات المشهورة
			Map<String, String> knownNames = new HashMap<>();

			// Selenium & TestNG
			knownNames.put("org.seleniumhq.selenium:selenium-java", "Selenium Java");
			knownNames.put("org.testng:testng", "TestNG");
			knownNames.put("io.github.bonigarcia:webdrivermanager", "WebDriverManager");
			knownNames.put("io.qameta.allure:allure-testng", "Allure TestNG");
			knownNames.put("ru.yandex.qatools.ashot:ashot", "Ashot");

			// Logging
			knownNames.put("org.slf4j:slf4j-simple", "SLF4J");
			knownNames.put("org.slf4j:slf4j-api", "SLF4J API");
			knownNames.put("org.slf4j:jcl-over-slf4j", "JCL over SLF4J");

			// Jackson
			knownNames.put("com.fasterxml.jackson.core:jackson-databind", "Jackson Databind");
			knownNames.put("com.fasterxml.jackson.core:jackson-core", "Jackson Core");
			knownNames.put("com.fasterxml.jackson.core:jackson-annotations", "Jackson Annotations");

			// Docker Java
			knownNames.put("com.github.docker-java:docker-java", "Docker Java");
			knownNames.put("com.github.docker-java:docker-java-api", "Docker Java API");
			knownNames.put("com.github.docker-java:docker-java-core", "Docker Java Core");
			knownNames.put("com.github.docker-java:docker-java-transport", "Docker Transport");
			knownNames.put("com.github.docker-java:docker-java-transport-httpclient5", "Docker Transport (HTTP5)");

			// Google Libraries
			knownNames.put("com.google.auto.service:auto-service-annotations", "Google AutoService");
			knownNames.put("com.google.code.gson:gson", "Gson");
			knownNames.put("com.google.errorprone:error_prone_annotations", "ErrorProne Annotations");
			knownNames.put("com.google.guava:guava", "Google Guava");
			knownNames.put("com.google.guava:failureaccess", "Guava FailureAccess");
			knownNames.put("com.google.guava:listenablefuture", "Guava ListenableFuture");
			knownNames.put("com.google.j2objc:j2objc-annotations", "J2ObjC Annotations");

			// Apache Commons
			knownNames.put("commons-codec:commons-codec", "Apache Commons Codec");
			knownNames.put("commons-io:commons-io", "Apache Commons IO");
			knownNames.put("org.apache.commons:commons-exec", "Apache Commons Exec");
			knownNames.put("org.apache.commons:commons-compress", "Apache Commons Compress");
			knownNames.put("org.apache.commons:commons-lang3", "Apache Commons Lang");

			// HttpClient
			knownNames.put("org.apache.httpcomponents.client5:httpclient5", "HttpClient 5");
			knownNames.put("org.apache.httpcomponents.core5:httpcore5", "HttpCore 5");
			knownNames.put("org.apache.httpcomponents.core5:httpcore5-h2", "HttpCore 5 (HTTP/2)");

			// Security
			knownNames.put("org.bouncycastle:bcpkix-jdk18on", "BouncyCastle PKIX");
			knownNames.put("org.bouncycastle:bcprov-jdk18on", "BouncyCastle Provider");
			knownNames.put("org.bouncycastle:bcutil-jdk18on", "BouncyCastle Util");

			// Compression
			knownNames.put("org.brotli:dec", "Brotli Decoder");

			// Misc
			knownNames.put("org.hamcrest:hamcrest-core", "Hamcrest");
			knownNames.put("org.jcommander:jcommander", "JCommander");
			knownNames.put("org.jspecify:jspecify", "JSPECIFY");
			knownNames.put("org.webjars:jquery", "jQuery");
			knownNames.put("SeleniumDemo:SeleniumDemo", "Selenium Demo");

			// ByteBuddy & JNA
			knownNames.put("net.bytebuddy:byte-buddy", "ByteBuddy");
			knownNames.put("net.java.dev.jna:jna", "JNA");

			// OpenTelemetry
			knownNames.put("io.opentelemetry:opentelemetry-api", "OpenTelemetry API");
			knownNames.put("io.opentelemetry:opentelemetry-context", "OpenTelemetry Context");
			knownNames.put("io.opentelemetry:opentelemetry-sdk", "OpenTelemetry SDK");
			knownNames.put("io.opentelemetry:opentelemetry-exporter-logging", "OpenTelemetry Logging Exporter");
			knownNames.put("io.opentelemetry:opentelemetry-sdk-common", "OpenTelemetry SDK Common");
			knownNames.put("io.opentelemetry:opentelemetry-sdk-logs", "OpenTelemetry Logs SDK");
			knownNames.put("io.opentelemetry:opentelemetry-sdk-trace", "OpenTelemetry Trace SDK");
			knownNames.put("io.opentelemetry:opentelemetry-sdk-metrics", "OpenTelemetry Metrics SDK");
			knownNames.put("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure", "OpenTelemetry Autoconfig");
			knownNames.put("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure-spi",
					"OpenTelemetry Autoconfig SPI");

			// ✅ Rest Assured & JSONPath
			knownNames.put("io.rest-assured:rest-assured", "Rest Assured");
			knownNames.put("io.rest-assured:json-path", "JSONPath");

			// ✅ لمنع التكرار
			Set<String> handledGroupIds = new HashSet<>();
			Set<String> addedDisplayNames = new HashSet<>();

			File depFile = new File("target/dependency-versions.txt");
			if (depFile.exists()) {
				try (BufferedReader reader = new BufferedReader(new FileReader(depFile))) {
					String line;
					while ((line = reader.readLine()) != null) {
						if (line.trim().startsWith(" "))
							continue;

						String[] parts = line.trim().split(":");
						if (parts.length >= 4) {
							String groupId = parts[0].trim();
							String artifactId = parts[1].trim();
							String version = parts[3].trim();
							String key = groupId + ":" + artifactId;

							// ✅ الاسم الودي أو الكامل
							String displayName = knownNames.getOrDefault(key, key);

							// ✅ تجاهل إذا أُضيف مسبقًا
							if (addedDisplayNames.contains(displayName))
								continue;

							// ✅ تجاهل إذا تم عرض مكتبة من نفس groupId مسبقًا
							if (handledGroupIds.contains(groupId))
								continue;

							allureEnv.setProperty(displayName, version);
							addedDisplayNames.add(displayName);
							handledGroupIds.add(groupId);
						}
					}
				}
			} else {
				System.err.println("⚠️ لم يتم العثور على dependency-versions.txt. شغّل mvn validate أولاً.");
			}

			FileWriter writer = new FileWriter(new File(resultsDir, "environment.properties"));
			allureEnv.store(writer, "Generated Automatically by EnvironmentAndExecutorSetup");
			writer.close();
			System.out.println("✅ environment.properties تم توليده بنجاح.");

		} catch (IOException e) {
			System.err.println("❌ خطأ في توليد environment.properties: " + e.getMessage());
		}
	}

	/**
	 * ✅ إنشاء ملف executor.json لـ Allure
	 *
	 * 🔹 يُستخدم لتوثيق تفاصيل التنفيذ مثل: نوع Executor (محلي، Jenkins، GitHub
	 * Actions)، اسم المستخدم، اسم البناء (Build)، الروابط، والـ timestamp. 🔹 يساعد
	 * في ربط تقارير Allure بالـ CI/CD أو التشغيل المحلي.
	 *
	 * ✅ Generate executor.json for Allure reports. Captures executor details
	 * (local, Jenkins, GitHub Actions), build name, user, and timestamp for linking
	 * Allure reports to CI/CD environments.
	 *
	 * 📌 الهدف: تسهيل تتبع أصل التقارير وربطها بجلسة التنفيذ أو الـ Pipeline.
	 *
	 * @throws IOException إذا فشل في حفظ الملف If saving executor.json fails
	 */
	private void generateExecutorJson() {
		try {
			File resultsDir = new File("allure-results");
			if (!resultsDir.exists())
				resultsDir.mkdirs();

			Map<String, String> env = System.getenv();
			String executorType = "local";
			String name = System.getProperty("user.name");
			String buildName = "Local Manual Run";
			String buildUrl = "";
			String reportUrl = "";

			if (env.containsKey("JENKINS_HOME")) {
				executorType = "jenkins";
				name = env.getOrDefault("BUILD_USER", "Jenkins");
				buildName = env.getOrDefault("JOB_NAME", "Jenkins Job");
				buildUrl = env.getOrDefault("BUILD_URL", "");
			} else if ("true".equals(env.get("GITHUB_ACTIONS"))) {
				executorType = "github";
				name = env.getOrDefault("GITHUB_ACTOR", "GitHub Actions");
				buildName = env.getOrDefault("GITHUB_WORKFLOW", "GitHub Workflow");
				buildUrl = "https://github.com/" + env.getOrDefault("GITHUB_REPOSITORY", "") + "/actions/runs/"
						+ env.getOrDefault("GITHUB_RUN_ID", "");
			}

			Executor executor = new Executor();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String timestamp = LocalDateTime.now().format(formatter);
			String buildNameWithTime = buildName + " - " + timestamp;

			executor.buildName = buildNameWithTime;
			executor.name = name;
			executor.type = executorType;
			executor.buildName = buildName;
			executor.buildUrl = buildUrl;
			executor.reportUrl = reportUrl;
			executor.buildOrder = (int) (System.currentTimeMillis() / 1000);

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			FileWriter writer = new FileWriter(new File(resultsDir, "executor.json"));
			gson.toJson(executor, writer);
			writer.close();

			System.out.println("✅ executor.json تم توليده.");

		} catch (IOException e) {
			System.err.println("❌ خطأ في توليد executor.json: " + e.getMessage());
		}
	}

	/**
	 * ✅ نموذج بيانات Executor لـ Allure
	 *
	 * 🔹 يمثل البيانات الوصفية الخاصة بالمنفذ (Executor) مثل Jenkins أو GitHub
	 * Actions أو التشغيل المحلي. 🔹 تتم كتابته لاحقًا إلى ملف executor.json.
	 *
	 * ✅ Data model for Allure Executor. Represents executor metadata such as CI/CD
	 * environment, build info, and report URL.
	 */
	class Executor {
		String name; // اسم المنفذ | Executor name
		String type; // نوع البيئة (local, jenkins, github) | Executor type
		String buildName; // اسم الـ Build | Build name
		String buildUrl; // رابط الـ Build | Build URL
		String reportUrl; // رابط التقرير | Report URL
		int buildOrder; // ترتيب التنفيذ | Build order (timestamp-based)
	}

	/**
	 * ✅ نسخ ملف categories.json إلى مجلد allure-results
	 *
	 * 🔹 يُستخدم لتحديد تصنيفات الأخطاء (Categories) مثل: Assertion Error, Network
	 * Error, UI Bug. 🔹 إذا لم يكن موجودًا داخل resources → يتم تخطي العملية.
	 *
	 * ✅ Copy categories.json into allure-results directory. Defines categories of
	 * errors (Assertion Error, Network Error, UI Bug) for Allure reports.
	 *
	 * 📌 الهدف: تحسين وضوح التصنيفات داخل تقارير Allure.
	 *
	 * @throws IOException إذا فشل النسخ If file copying fails
	 */
	private void copyCategoriesJson() {
		try {
			InputStream input = getClass().getClassLoader().getResourceAsStream("categories.json");
			if (input == null) {
				System.err.println("⚠️ لم يتم العثور على categories.json داخل resources.");
				return;
			}

			File resultsDir = new File("allure-results");
			if (!resultsDir.exists())
				resultsDir.mkdirs();

			File targetFile = new File(resultsDir, "categories.json");
			try (OutputStream out = new FileOutputStream(targetFile)) {
				byte[] buffer = new byte[1024];
				int length;
				while ((length = input.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
			}

			System.out.println("✅ categories.json تم نسخه تلقائيًا إلى allure-results.");

		} catch (IOException e) {
			System.err.println("❌ فشل في نسخ categories.json: " + e.getMessage());
		}
	}

	/**
	 * ✅ نسخ بيانات history من التقرير السابق
	 *
	 * 🔹 يقوم بنسخ مجلد allure-report/history إلى allure-results/history إذا كان
	 * موجودًا. 🔹 يُستخدم لإظهار الـ Trend (النجاحات والإخفاقات عبر الإصدارات
	 * السابقة).
	 *
	 * ✅ Copy history data from previous report. Moves allure-report/history to
	 * allure-results/history to preserve historical trends in Allure reports.
	 *
	 * 📌 الهدف: تفعيل خاصية Trend في تقارير Allure (مقارنة نتائج الإصدارات).
	 */
	private void copyHistoryFromPreviousReport() {
		File sourceHistory = new File("allure-report/history"); // مجلد التقرير السابق
		File targetHistory = new File("allure-results/history");

		if (!sourceHistory.exists() || !sourceHistory.isDirectory()) {
			System.out.println(
					"⚠️ لا يوجد تقرير سابق (allure-report/history) لنسخ بيانات Trend منه. سيتم إنشاء التقرير بدون Trend.");
			return; // منع النسخ أو التوليد الفارغ
		}

		// تابع النسخ في حال وجود history
		if (!targetHistory.exists())
			targetHistory.mkdirs();

		for (File file : Objects.requireNonNull(sourceHistory.listFiles())) {
			try {
				File targetFile = new File(targetHistory, file.getName());
				try (InputStream in = new FileInputStream(file); OutputStream out = new FileOutputStream(targetFile)) {
					byte[] buffer = new byte[1024];
					int length;
					while ((length = in.read(buffer)) > 0) {
						out.write(buffer, 0, length);
					}
				}
			} catch (IOException e) {
				System.err.println("⚠️ لم يمكن نسخ: " + file.getName());
			}
		}

		System.out.println("✅ تم نسخ بيانات history بنجاح لتفعيل Trend.");
	}

}
