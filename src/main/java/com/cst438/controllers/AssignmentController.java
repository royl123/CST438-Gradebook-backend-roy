package com.cst438.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentListDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
public class AssignmentController {

	private final CourseRepository courseRepository;
	private final AssignmentRepository assignmentRepository;
	private final AssignmentGradeRepository assignmentGradeRepository;

	public AssignmentController(CourseRepository courseRepository, AssignmentRepository assignmentRepository,
			AssignmentGradeRepository assignmentGradeRepository) {
		this.courseRepository = courseRepository;
		this.assignmentRepository = assignmentRepository;
		this.assignmentGradeRepository = assignmentGradeRepository;
	}

	@PostMapping("/course/{course_id}/assignments")
	@Transactional
	public void addAssignment(@PathVariable("course_id") int courseId,
			@RequestBody AssignmentListDTO.AssignmentDTO assignmentDTO) {
		// Check if the course exists
		Course course = courseRepository.findById(courseId).orElse(null);
		if (course == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found.");
		}

		// Create a new assignment
		Assignment assignment = new Assignment();
		assignment.setName(assignmentDTO.assignmentName);

		// Convert the dueDate to a java.sql.Date object
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date dueDate;
		try {
			dueDate = dateFormat.parse(assignmentDTO.dueDate);
			java.sql.Date sqlDueDate = new java.sql.Date(dueDate.getTime());
			assignment.setDueDate(sqlDueDate);
		} catch (ParseException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid due date format.");
		}

		assignment.setCourse(course);

		// Save the assignment in the database
		assignmentRepository.save(assignment);
	}

	@PutMapping("/course/{course_id}/assignments/{assignment_id}")
	@Transactional
	public void updateAssignmentName(@PathVariable("course_id") int courseId,
			@PathVariable("assignment_id") int assignmentId, @RequestBody Map<String, Object> payload) {
		// Check if the course exists
		Course course = courseRepository.findById(courseId).orElse(null);
		if (course == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found.");
		}

		// Check if the assignment exists
		Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
		if (assignment == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found.");
		}

		String newName = (String) payload.get("assignmentName");

		// Update the assignment name
		assignment.setName(newName);

		// Save the updated assignment in the database
		assignmentRepository.save(assignment);
	}

	@DeleteMapping("/course/{course_id}/assignments/{assignment_id}")
	@Transactional
	public void deleteAssignment(@PathVariable("course_id") int courseId,
			@PathVariable("assignment_id") int assignmentId) {
		// Check if the course exists
		Course course = courseRepository.findById(courseId).orElse(null);
		if (course == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found.");
		}

		// Check if the assignment exists
		Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
		if (assignment == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found.");
		}

		// Check if there are any grades for the assignment
		boolean hasGrades = assignmentGradeRepository.existsByAssignment(assignment);
		if (hasGrades) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete assignment with existing grades.");
		}

		assignmentRepository.delete(assignment);
	}
}
