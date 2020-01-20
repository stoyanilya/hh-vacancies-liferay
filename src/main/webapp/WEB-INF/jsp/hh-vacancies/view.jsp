<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%@include file="init.jsp"%>

This is the <b>hh-vacancies</b> portlet.<br />

<form:select path="area">
  <form:options items="${areaList}" itemLabel="name" itemValue="id"/>
</form:select>

<table id="vacancies-datatable" class="table table-striped table-bordered">
  <thead>
    <tr>
      <th>Вакансии</th>
    </tr>
  </thead>
  <tbody>
  </tbody>
</table>

<script>
jQuery(document).ready(function() {
  jQuery('#vacancies-datatable').dataTable({
      bAutoWidth : true,
      bProcessing : true,
      bFilter : false,
      bSort: false,
      sPaginationType: "full_numbers",
      bServerSide: true,
      sAjaxSource :  "<portlet:resourceURL id='getVacancies'/>",
      aoColumns : [
    	  {
    		  mData : "row"
    		}
    	]
  });
});
</script>