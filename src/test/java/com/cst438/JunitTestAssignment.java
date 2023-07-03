package com.cst438;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cst438.controllers.AssignmentController;
import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentListDTO;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.services.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cst438.domain.AssignmentRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ContextConfiguration(classes = { AssignmentController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class JunitTestAssignment {

	@MockBean
	AssignmentRepository assignmentRepository;

	@MockBean
	AssignmentGradeRepository assignmentGradeRepository;

	@MockBean
	CourseRepository courseRepository; // must have this to keep Spring test happy

	@MockBean
	RegistrationService registrationService; // must have this to keep Spring test happy

	@Autowired
	private MockMvc mvc;

	@Test
	public void testAddAssignment() throws Exception {
		MockHttpServletResponse response;
		int courseId = 1;

		AssignmentListDTO.AssignmentDTO assignmentDTO = new AssignmentListDTO.AssignmentDTO();
		assignmentDTO.assignmentName = "Assignment 1";
		assignmentDTO.dueDate = "2023-06-30";

		Course course = new Course();
		course.setCourse_id(courseId);

		given(courseRepository.findById(courseId)).willReturn(Optional.of(course));
		given(assignmentRepository.save(any())).willReturn(new Assignment());

		response = mvc
				.perform(MockMvcRequestBuilders.post("/course/{course_id}/assignments", courseId)
						.content(asJsonString(assignmentDTO)).contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		assertEquals(200, response.getStatus());
		verify(courseRepository, times(1)).findById(courseId);
		verify(assignmentRepository, times(1)).save(any());
	}

	@Test
	public void testUpdateAssignmentName() throws Exception {
		MockHttpServletResponse response;
		int courseId = 1;
		int assignmentId = 1;
		String newName = "Updated Assignment Name";

		Course course = new Course();
		course.setCourse_id(courseId);

		Assignment assignment = new Assignment();
		assignment.setId(assignmentId);
		assignment.setName("Assignment 1");

		given(courseRepository.findById(courseId)).willReturn(Optional.of(course));
		given(assignmentRepository.findById(assignmentId)).willReturn(Optional.of(assignment));
		given(assignmentRepository.save(any())).willReturn(assignment);

		Map<String, Object> payload = new HashMap<>();
		payload.put("assignmentName", newName);

		response = mvc
				.perform(MockMvcRequestBuilders
						.put("/course/{course_id}/assignments/{assignment_id}", courseId, assignmentId)
						.content(asJsonString(payload)).contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		assertEquals(200, response.getStatus());
		verify(courseRepository, times(1)).findById(courseId);
		verify(assignmentRepository, times(1)).findById(assignmentId);
		verify(assignmentRepository, times(1)).save(any());
		assertEquals(newName, assignment.getName());
	}

	@Test
	public void testDeleteAssignment() throws Exception {
		MockHttpServletResponse response;
		int courseId = 1;
		int assignmentId = 1;

		Course course = new Course();
		course.setCourse_id(courseId);

		Assignment assignment = new Assignment();
		assignment.setId(assignmentId);

		given(courseRepository.findById(courseId)).willReturn(Optional.of(course));
		given(assignmentRepository.findById(assignmentId)).willReturn(Optional.of(assignment));
		given(assignmentGradeRepository.existsByAssignment(assignment)).willReturn(false);

		response = mvc.perform(MockMvcRequestBuilders.delete("/course/{course_id}/assignments/{assignment_id}",
				courseId, assignmentId)).andReturn().getResponse();

		assertEquals(200, response.getStatus());
		verify(courseRepository, times(1)).findById(courseId);
		verify(assignmentRepository, times(1)).findById(assignmentId);
		verify(assignmentGradeRepository, times(1)).existsByAssignment(assignment);
		verify(assignmentRepository, times(1)).delete(assignment);
	}

	private static String asJsonString(final Object obj) {
		try {

			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
