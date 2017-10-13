package com.libertymutual.goforcode.schoolmanagementsystem.api;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.libertymutual.goforcode.schoolmanagementsystem.models.Assignment;
import com.libertymutual.goforcode.schoolmanagementsystem.models.AssignmentDto;
import com.libertymutual.goforcode.schoolmanagementsystem.models.CreateAssignmentModel;
import com.libertymutual.goforcode.schoolmanagementsystem.models.Student;
import com.libertymutual.goforcode.schoolmanagementsystem.models.Teacher;
import com.libertymutual.goforcode.schoolmanagementsystem.repositories.AssignmentRepository;
import com.libertymutual.goforcode.schoolmanagementsystem.repositories.StudentRepository;
import com.libertymutual.goforcode.schoolmanagementsystem.repositories.TeacherRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/assignments")
@Api(description = "Use this to get all, create, delete, and update assignments.")
public class AssignmentApiController {

	private AssignmentRepository assignmentRepo;
	private StudentRepository studentRepo;
	private TeacherRepository teacherRepo;

	public AssignmentApiController(AssignmentRepository assignmentRepo, StudentRepository studentRepo,
			TeacherRepository teacherRepo) {
		this.assignmentRepo = assignmentRepo;
		this.studentRepo = studentRepo;
		this.teacherRepo = teacherRepo;

	}

	@ApiOperation(value = "Get a list of all of the assignments.")
	@GetMapping("")
	public List<Assignment> getAll() {
		return assignmentRepo.findAll();
	}

	@ApiOperation(value = "Get a specific assignment by id.")
	@GetMapping("{id}")
	public AssignmentDto getOne(@PathVariable long id) {
		try {
			Assignment assignment = assignmentRepo.findOne(id);
			return new AssignmentDto(assignment);
		} catch (EmptyResultDataAccessException erdae) {
			return null;
		}
	}

	@ApiOperation(value = "Creates a new assignment, and associate it to all students under the teacher.")
	@PostMapping("")
	public AssignmentDto createAndAssociateToStudents(@RequestBody CreateAssignmentModel assignment) {

		// find all students associated with teacher
		List<Student> students;
		Teacher teacher;
		Assignment newAssignment = new Assignment(assignment.getName(), assignment.getDescription(),
				assignment.getDueDate(), assignment.getComment());
		try {
			teacher = teacherRepo.findOne(assignment.getTeacherId());
			students = studentRepo.findByTeacher(teacher);
			newAssignment.setStudents(students);
			assignmentRepo.save(newAssignment);
			return new AssignmentDto(newAssignment);

		} catch (EmptyResultDataAccessException erdae) {
			return null;
		}

	}

	@ApiOperation(value = "Delete an assignment.")
	@DeleteMapping("{id}")
	public AssignmentDto delete(@PathVariable long id) {
		try {
			Assignment assignment = assignmentRepo.findOne(id);
			assignmentRepo.delete(id);
			return new AssignmentDto(assignment);
		} catch (EmptyResultDataAccessException erdae) {
			return null;
		}
	}

	@ApiOperation(value = "Update an assignment.")
	@PutMapping("{id}")
	public AssignmentDto update(@RequestBody Assignment assignment, @PathVariable long id) {
		assignment.setId(id);
		assignmentRepo.save(assignment);
		return new AssignmentDto(assignment);
	}

	@ApiOperation(value = "Get a list of students assigned to a particular assignment.")
	@GetMapping("{id}")
	public List<Student> getAllStudentsByAssignment(@PathVariable long id) {
		try {
			Assignment individualAssignment = assignmentRepo.findOne(id);
			List<Student> studentList = individualAssignment.getStudents();
			return studentList;
		} catch (EmptyResultDataAccessException erdae) {
			return null;
		}
	}

}
