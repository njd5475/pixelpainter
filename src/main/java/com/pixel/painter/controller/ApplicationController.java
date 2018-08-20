package com.pixel.painter.controller;

import java.util.HashMap;
import java.util.Map;

public class ApplicationController {

	private static ApplicationController		instance;

	private final Map<String, ImageController>	images;

	private ApplicationController() {
		images = new HashMap<String, ImageController>();
	}

	public static ApplicationController getInstance() {
		if (instance == null) {
			instance = new ApplicationController();
		}

		return instance;
	}
}
