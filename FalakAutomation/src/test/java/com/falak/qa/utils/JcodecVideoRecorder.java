package com.falak.qa.utils;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;

import org.jcodec.api.awt.AWTSequenceEncoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 🎥 مسجّل فيديو Pure-Java باستخدام JCodec (بدون FFmpeg) - يلتقط سطح المكتب
 * كصور ويحوّلها إلى MP4 (H.264 baseline). - لا يسجّل الصوت.
 */
public class JcodecVideoRecorder {

	private final int fps;
	private final AtomicBoolean running = new AtomicBoolean(false);
	private Thread worker;
	private File outputFile;

	/**
	 * 🛠️ مُنشئ افتراضي بمعدل 10fps
	 *
	 * 🛠️ Default constructor with 10 fps.
	 */
	public JcodecVideoRecorder() {
		this(10); // افتراضي 10fps
	}

	/**
	 * 🛠️ مُنشئ بمعدل إطارات مخصص
	 *
	 * 🛠️ Constructor with a custom frame rate.
	 *
	 * @param fps معدل الإطارات المطلوب (يُقص إلى 1 كحد أدنى) | Desired FPS (minimum
	 *            clamped to 1)
	 */
	public JcodecVideoRecorder(int fps) {
		this.fps = Math.max(1, fps);
	}

	/**
	 * 📄 يعيد ملف الإخراج (MP4) بعد بدء التسجيل
	 *
	 * 📄 Returns the output MP4 file (after recording has started).
	 *
	 * @return كائن {@link File} يشير إلى الفيديو الناتج | The output video file
	 */
	public File getOutputFile() {
		return outputFile;
	}

	/**
	 * 🧼 تنظيف اسم الملف من المحارف غير المسموح بها
	 *
	 * 🧼 Sanitizes a string to be filesystem-friendly.
	 *
	 * @param s النص الأصلي | Original string
	 * @return نص آمن للاستخدام كجزء من اسم ملف | Safe, filename-friendly string
	 */
	private static String sanitize(String s) {
		return s == null ? "video" : s.replaceAll("[^\\w\\-\\.]+", "_");
	}

	/**
	 * 🎥 ابدأ تسجيل الشاشة إلى ملف MP4 داخل {@code target/videos}
	 *
	 * 🎥 Starts full-screen recording to an MP4 file under {@code target/videos}.
	 *
	 * <p>
	 * 🔸 ينشئ ملفًا باسم: {@code <testName>_yyyyMMdd_HHmmss.mp4} 🔸 يعتمد على
	 * {@link Robot} لالتقاط الشاشة و{@link AWTSequenceEncoder} للترميز. 🔸 يُشغّل
	 * خيطًا للخلفية ويستمر حتى يتم إيقاف {@code running} من الخارج.
	 * </p>
	 *
	 * @param testName اسم الحالة/الاختبار لإدراجه في اسم الملف | Test name used in
	 *                 the filename
	 * @throws Exception عند فشل التهيئة (Robot/Encoder/ملف الإخراج) | If
	 *                   initialization fails
	 */
	@Step("🎥 Start desktop recording for: {testName}")
	public void start(String testName) throws Exception {
		String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File outDir = new File("target/videos");
		if (!outDir.exists())
			outDir.mkdirs();
		outputFile = new File(outDir, sanitize(testName) + "_" + ts + ".mp4");

		Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		Robot robot = new Robot();
		running.set(true);

//		Allure.step("📁 Output file: " + outputFile.getAbsolutePath());
//		Allure.step("⏱️ FPS: " + fps + " | Screen: " + screen.width + "x" + screen.height);

		worker = new Thread(() -> {
			AWTSequenceEncoder enc = null;
			try {
				// ⚠️ لا تستخدم try-with-resources — AWTSequenceEncoder ليس AutoCloseable
				enc = AWTSequenceEncoder.createSequenceEncoder(outputFile, fps);

				long frameDelayMs = 1000L / fps;
				while (running.get()) {
					long t0 = System.nanoTime();
					BufferedImage frame = robot.createScreenCapture(screen);
					enc.encodeImage(frame);
					long spent = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0);
					long sleep = frameDelayMs - spent;
					if (sleep > 0)
						Thread.sleep(sleep);
				}
			} catch (Exception e) {
				e.printStackTrace();
				//Allure.step("❌ Recorder worker error: " + e.getMessage());
			} finally {
				try {
					if (enc != null)
						enc.finish(); // ضروري لكتابة الميتاداتا وإغلاق الملف
					//Allure.step("🛑 Recorder finalized & file closed");
				} catch (Exception ignore) {
				}
			}
		}, "pure-java-video-recorder");

		worker.setDaemon(true);
		worker.start();
		//Allure.step("▶️ Recording thread started");
	}

	/**
	 * 🛑 يوقف التسجيل وينتظر ثبات الملف ثم يعيده
	 *
	 * 🛑 Stops the recording, waits for the MP4 file to finalize, and returns it.
	 *
	 * <p>
	 * ✅ يضبط {@code running=false}، وينتظر خيط التسجيل {@code worker} لمدة وجيزة،
	 * ثم يتحقق من أن الملف النهائي موجود وغير فارغ قبل إرجاعه.
	 * </p>
	 *
	 * @return ملف الفيديو الناتج إن كان صالحًا (غير فارغ) أو {@code null} إن لم
	 *         يتوفر The recorded video file if valid (non-empty), otherwise
	 *         {@code null}
	 */
	@Step("🛑 Stop recorder and return finalized video file")
	public File stopAndGetFile() {
		try {
			running.set(false);
			if (worker != null)
				worker.join(2000); // ⏳ انتظر حتى ينتهي الثريد
			waitForFileFinalize(outputFile); // ⏳ انتظر استقرار حجم الملف
			//Allure.step("🛑 Recorder stopped, file finalized");
		} catch (InterruptedException ignore) {
			Thread.currentThread().interrupt();
			//Allure.step("⚠️ Interrupted while stopping recorder");
		}
		return (outputFile != null && outputFile.exists() && outputFile.length() > 0) ? outputFile : null;
	}

	/**
	 * 📎 يرفق الفيديو (MP4) إلى Allure ثم يحذفه اختياريًا
	 *
	 * 📎 Attaches the recorded MP4 to Allure, then optionally deletes the file.
	 *
	 * <p>
	 * ✅ ينتظر ثبات الملف أولًا لضمان اكتماله. ✅ يستخدم {@link Allure#addAttachment}
	 * لرفع الفيديو كـ {@code video/mp4}.
	 * </p>
	 *
	 * @param title       عنوان المرفق داخل تقرير Allure | Attachment title in
	 *                    Allure
	 * @param deleteAfter إذا {@code true} سيُحذف الملف بعد الإرفاق | Delete file
	 *                    after attaching if {@code true}
	 */
	@Step("📎 Attach video to Allure: {title} (deleteAfter={deleteAfter})")
	public void attachToAllure(String title, boolean deleteAfter) {
		try {
			if (outputFile != null) {
				waitForFileFinalize(outputFile);
				if (outputFile.exists() && outputFile.length() > 0) {
					Allure.addAttachment(title, "video/mp4", new FileInputStream(outputFile), ".mp4");
					//Allure.step("✅ Video attached to Allure: " + outputFile.getName());
				} else {
					//Allure.step("⚠️ Output file not ready or empty");
				}
				if (deleteAfter) {
					try {
						Files.deleteIfExists(outputFile.toPath());
						//Allure.step("🧹 Video file deleted after attaching");
					} catch (Exception ignore) {
						//Allure.step("⚠️ Failed to delete video file after attaching");
					}
				}
			} else {
				Allure.step("ℹ️ No output file to attach");
			}
		} catch (Exception ignore) {
			Allure.step("⚠️ Exception while attaching video to Allure");
		}
	}

	/**
	 * ⏳ ينتظر حتى يستقر حجم الملف (علامة على اكتمال الكتابة) ثم يخرج
	 *
	 * ⏳ Waits until the file size stabilizes (indicating write completion), then
	 * returns.
	 *
	 * <p>
	 * 🔸 يُجرِي فحوصات متتابعة لحجم الملف بفواصل قصيرة. 🔸 يعتبر الملف مستقرًا إذا
	 * تكرر نفس الحجم عدة مرات متتالية.
	 * </p>
	 *
	 * @param f ملف الخرج المراد انتظار استقراره | Output file to wait on
	 * @throws InterruptedException إذا قوطع الانتظار | if the waiting thread is
	 *                              interrupted
	 */
	private static void waitForFileFinalize(File f) throws InterruptedException {
		if (f == null)
			return;
		long last = -1;
		int stable = 0;
		for (int i = 0; i < 30; i++) { // ~3 ثوانٍ (30 * 100ms)
			long len = f.exists() ? f.length() : 0;
			if (len > 0 && len == last) {
				stable++;
				if (stable >= 3)
					break; // 📍 اعتبره مستقرًا بعد 3 قراءات متطابقة
			} else {
				stable = 0;
				last = len;
			}
			Thread.sleep(100);
		}
	}

	/**
	 * 📡 هل المُسجّل يعمل حاليًا؟
	 *
	 * 📡 Is the recorder currently running?
	 *
	 * @return {@code true} إذا كان يلتقط الإطارات الآن | {@code true} if currently
	 *         capturing frames
	 */
	@Step("📡 Check if recorder is running")
	public boolean isRunning() {
		return running.get();
	}

}
