package com.falak.qa.pages.home;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.qameta.allure.Step;

public class FeatureCardComponent {

	private final WebElement root;

	/* عنوان الميزة (العربي) */
	private final By titleLocator = By.xpath(
			"//section[contains(@class,'section-features')]//div[contains(@class,'surface-card')]//h3[contains(@class,'label')]");

	/* وصف الميزة */
	private final By descriptionLocator = By.xpath(
			"//section[contains(@class,'section-features')]//div[contains(@class,'surface-card')]//p[contains(@class,'text-500')]");

	/* فيديو العرض للميزة */
	private final By videoLocator = By
			.xpath("//section[contains(@class,'section-features')]//div[contains(@class,'surface-card')]//video");

	//	🧩 مُحدد الأنيميشن (عادة يكون Lottie أو صورة متحركة) | Animation visual
	/* بطاقة تحتوي على كلاسي الأنيميشن */
	private final By animationLocator = By.xpath(
			"//section[contains(@class,'section-features')]//div[contains(@class,'surface-card') and contains(@class,'animate__animated') and contains(@class,'animate__fadeInDown')]");

	/**
	 * 🔧 Constructor - المُنشئ
	 * 
	 * @param root العنصر الجذري للبطاقة | The root element of the feature card
	 */
	public FeatureCardComponent(WebElement root) {
		this.root = root;
	}

	/**
	 * 🎬 هل يوجد أنيميشن ظاهر؟ | Is animation visible?
	 */
	@Step("Check if animation is visible in feature card")
	public boolean isAnimationVisible() {
		try {
			return root.findElement(animationLocator).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 🎬 هل فيديو الأنيميشن ظاهر؟ | Checks if the video animation is visible
	 */
	@Step("Check if feature animation (video) is visible")
	public boolean isVideoVisible() {
		try {
			return root.findElement(videoLocator).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 🏷️ جلب عنوان الميزة | Gets the title text of the feature
	 */
	@Step("Get the feature title text")
	public String getTitleText() {
		try {
			return root.findElement(titleLocator).getText().trim();
		} catch (Exception e) {
			return "N/A";
		}
	}

	/**
	 * 📝 جلب وصف الميزة | Gets the description text of the feature
	 */
	@Step("Get the feature description text")
	public String getDescriptionText() {
		try {
			return root.findElement(descriptionLocator).getText().trim();
		} catch (Exception e) {
			return "N/A";
		}
	}
	
	
	/**
	 * 🌱 يُرجع العنصر الجذري للبطاقة (مفيد لاختبارات CSS أو Hover)
	 * Returns the root WebElement of the card (useful for CSS or hover tests).
	 */
	public WebElement getCardRoot() {
		return this.root;
	}

	/**
	 * 🌫️ يُرجع قيمة خاصية box-shadow الخاصة بالبطاقة
	 * Retrieves the current value of the card's box-shadow CSS property.
	 */
	public String getBoxShadow() {
		return root.getCssValue("box-shadow");
	}

}
