package com.cst438.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.CourseDTOG;

public class RegistrationServiceREST extends RegistrationService {

	
	RestTemplate restTemplate = new RestTemplate();
	
	@Value("${registration.url}") 
	String registration_url;
	
	public RegistrationServiceREST() {
		System.out.println("REST registration service ");
	}
	
	@Override
	public void sendFinalGrades(int course_id , CourseDTOG courseDTO) { 
		
		String url = registration_url + "/grades/{course_id}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CourseDTOG> requestEntity = new HttpEntity<>(courseDTO, headers);

        try {
            restTemplate.put(url, requestEntity, course_id);
            System.out.println("Final grades sent to Registration backend for course_id: " + course_id);
        } catch (RestClientException e) {
            System.out.println("Failed to send final grades to Registration backend for course_id: " + course_id);
            e.printStackTrace();
        }
		
	}
}
