package com.hoge.controller;

import com.hoge.api.SampleAPI;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ViewController {
	
	@GetMapping("/")
	public String homeGet(Model model, HttpServletRequest request) {
		try {
			model.addAttribute("video", new SampleAPI().getSearchList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "view.html";
	}
}