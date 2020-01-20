package com.liferay.demo.hh.service;

import java.io.IOException;
import java.util.List;

import com.liferay.demo.hh.model.Area;
import com.liferay.demo.hh.model.Vacancy;
import com.liferay.demo.hh.util.IntegerResult;

public interface HhService {
	public List<Vacancy> getVacancies(String area, String specialization, String page, String perPage, IntegerResult outFound) throws InterruptedException, IOException;
	public List<Area> getAreas() throws IOException;
}
