/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.demo.hh;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.demo.hh.model.Area;
import com.liferay.demo.hh.model.Vacancy;
import com.liferay.demo.hh.service.HhService;
import com.liferay.demo.hh.service.HhServiceImpl;
import com.liferay.demo.hh.util.IntegerResult;
import com.liferay.portal.kernel.util.ReleaseInfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

@Controller("portletViewController")
@RequestMapping("VIEW")
public class PortletViewController {
	@Autowired
	HhService hhService;
	
	@RenderMapping
	public String question(Model model) {
		model.addAttribute("releaseInfo", ReleaseInfo.getReleaseInfo());
		model.addAttribute("area", "");
		return "hh-vacancies/view";
	}

	@ModelAttribute("areaList")
    public List<Area> getAreaList() throws IOException {
      return hhService.getAreas();
    }

	@ResourceMapping("getVacancies")
	public void getVacancies(
			@RequestParam("iDisplayLength") String iDisplayLength,
			@RequestParam("iDisplayStart") String iDisplayStart,
			ResourceRequest request, ResourceResponse response
	) throws IOException, InterruptedException 
	{
		IntegerResult totalFound = new IntegerResult();
		int page = Integer.parseInt(iDisplayStart) / Integer.parseInt(iDisplayLength);
		List<Vacancy> vacancies= hhService.getVacancies("1", "1", String.valueOf(page), iDisplayLength, totalFound);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("iTotalRecords",String.valueOf(totalFound.getValue()));
		map.put("iTotalDisplayRecords", String.valueOf(totalFound.getValue()));
		//map.put("aaData", vacancies);
		
		List<Map<String,String>> viewRows = new ArrayList<Map<String,String>>();
		for(Vacancy vacancy: vacancies){
			String html =
			        "<div class=\"hh-vacancy\">" +
			          "  <div class=\"hh-vacancy-row\">" +
			          "    <div class=\"hh-vacancy-row-item hh-vacancy-name\">1</div>" +
			          "    <div class=\"hh-vacancy-row-item hh-vacancy-salary\">2</div>" +
			          "  </div>" +
			          "  <div class=\"hh-vacancy-row\">" +
			          "    <div class=\"hh-vacancy-row-item hh-vacancy-dept\">3</div>" +
			          "  </div>" +
			          "  <div class=\"hh-vacancy-row\">" +
			          "    <div class=\"hh-vacancy-row-item hh-vacancy-date\">4</div>" +
			          "  </div>" +
			          "</div>";
			viewRows.add(Collections.singletonMap("row", 
			  html
			   .replace("1", vacancy.getName())
			   .replace("2", vacancy.getSalary())
			   .replace("3", vacancy.getDepartmentName())
			   .replace("4", vacancy.getPublishedAt())));
		}
		map.put("aaData", viewRows);
		ObjectMapper mapper = new ObjectMapper();
	    mapper.writeValue(response.getWriter(), map);
	    //System.out.println("****  ->"+"  "+iDisplayLength +" "+iDisplayStart+"  "+String.valueOf(page));
	}
}