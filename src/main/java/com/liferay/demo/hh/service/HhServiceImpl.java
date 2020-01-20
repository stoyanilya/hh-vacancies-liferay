package com.liferay.demo.hh.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.demo.hh.model.Area;
import com.liferay.demo.hh.model.Vacancy;
import com.liferay.demo.hh.util.IntegerResult;

@Service
public class HhServiceImpl implements HhService{
  final String URL_HH_RU = "https://api.hh.ru/";
  final String DELAULT_AREA = "Россия";
  final String DELAULT_SPECIALIZATION = "Информационные технологии, интернет, телеком";

  private String getSpecializationIdByName(String specializationName) throws IOException {
    final String uri = URL_HH_RU + "specializations";
    String id = "";

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.add("User-Agent", "LifeRay-test");

    ResponseEntity<String> response = (new RestTemplate())
      .exchange(uri, HttpMethod.GET, new HttpEntity<Object>("parameters", headers), String.class);
    if(response.getBody()==null || !response.getStatusCode().equals(HttpStatus.OK)){
      throw new RuntimeException("Не удалось получить список специализациq");
    }
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(response.getBody());
    Iterator<JsonNode> itr = root.elements();
    while (itr.hasNext()){
      JsonNode node = itr.next();
      if(node.path("name").asText().equalsIgnoreCase(specializationName)){
        id = node.path("id").asText();
        break;
      }
    }
    if(id.isEmpty()){
      throw new RuntimeException("Не удалось получить идентификатор специализации:"+specializationName);
    }
    return id;
  }
  
  private String getAreaIdByName(String areaName) throws IOException {
    String id = "";
    for(Area area: getAreas()){
    	if(area.getName().equalsIgnoreCase(areaName)){
    		id = area.getId();
    	}
    }
    if(id.isEmpty()){
      throw new RuntimeException("Не удалось получить идентификатор "+areaName);
    }
    return id;
  }
  
  public List<Area> getAreas() throws IOException{
	final String uri = URL_HH_RU + "areas/countries";

	HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.add("User-Agent", "LifeRay-test");

    ResponseEntity<String> response = (new RestTemplate())
      .exchange(uri, HttpMethod.GET, new HttpEntity<Object>("parameters", headers), String.class);
    if(response.getBody()==null || !response.getStatusCode().equals(HttpStatus.OK)){
      throw new RuntimeException("Не удалось получить список стран");
    }
    List<Area> areas = new LinkedList<Area>();
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(response.getBody());
    Iterator<JsonNode> itr = root.elements();
    while (itr.hasNext()){
      JsonNode node = itr.next();
      Area area = new Area();
      area.setId(node.path("id").asText());
      area.setName(node.path("name").asText());
      areas.add(area);
    }
    return areas;
  }
  
  public List<Vacancy> getVacancies(String area, String specialization, String page, String perPage, IntegerResult outFound) throws InterruptedException, IOException {
	final String EMPTY_VALUE = "";
	//see https://github.com/hhru/api/blob/master/docs/vacancies.md
	//глубина возвращаемых результатов не может быть больше 2000
	final int MAX_COUNT_RECORDS = 2000;
	List<Vacancy> vacancies = new ArrayList<Vacancy>();
	
    StringBuilder uri = new StringBuilder(URL_HH_RU)
      .append("vacancies?area=")
      .append(area == null || area.isEmpty() ? getAreaIdByName(DELAULT_AREA) : area)
      .append("&specialization=")
      .append(specialization == null || specialization.isEmpty() ? getSpecializationIdByName(DELAULT_SPECIALIZATION) : specialization)
      .append("&page=")
      .append(page)
      .append("&per_page=")
      .append(perPage);
    
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.add("User-Agent", "LifeRay-test");
    HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

    ResponseEntity<String> response = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, String.class);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(response.getBody());
    Iterator<JsonNode> itr = root.path("items").iterator();
    while (itr.hasNext()){
      JsonNode node = itr.next(),
        tmpNode;
      Vacancy vacancy = new Vacancy();
      vacancy.setId(node.path("id").asText());
      vacancy.setName((tmpNode = node.path("name")) == null ? EMPTY_VALUE : tmpNode.asText());
      vacancy.setPublishedAt((tmpNode = node.path("published_at")) == null ? EMPTY_VALUE : tmpNode.asText());
      tmpNode = node.path("salary");
      if(!tmpNode.isNull()){
        String from = (tmpNode = node.path("from")) == null ? "" : tmpNode.asText(),
          to = (tmpNode = node.path("to")) == null ? "" : tmpNode.asText();
        if(!from.isEmpty() && !to.isEmpty()) {
          from = from + " до " + to;
        }else{
          from = (from.isEmpty() ? to : from);
        }
        vacancy.setSalary(from);
      }
      tmpNode = node.path("department");
      if(!tmpNode.isNull()){
        vacancy.setDepartmentName((tmpNode = node.path("name")) == null ? EMPTY_VALUE : tmpNode.asText());
      }
      vacancies.add(vacancy);
    }
    
    outFound.setValue(Integer.parseInt(root.path("found").asText()));
    if(outFound.getValue() > MAX_COUNT_RECORDS){
    	outFound.setValue(MAX_COUNT_RECORDS);
    }
    return vacancies;
  }
}