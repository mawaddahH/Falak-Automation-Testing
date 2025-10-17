package com.falak.qa.utils;

import java.io.File;

public interface VideoRecorder {
	void start(String testName) throws Exception;

	File stop() throws Exception;

	void attachToAllure(String title, boolean deleteAfter);
}
