package com.cookseys.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDbUtil {

	private DataSource dataSource;

	public StudentDbUtil(DataSource theDataSource) {
		dataSource = theDataSource;
	}

	public List<Student> getStudents() throws Exception {

		List<Student> students = new ArrayList<>();

		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;

		try {
			myConn = dataSource.getConnection();

			String sql = "select * from student order by last_name";

			myStmt = myConn.createStatement();

			myRs = myStmt.executeQuery(sql);

			while (myRs.next()) {

				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");

				// create new student object
				Student tempStudent = new Student(id, firstName, lastName, email);

				// add it to the list of students
				students.add(tempStudent);
			}

			return students;
		} finally {
			// close JDBC objects
			close(myConn, myStmt, myRs);
		}
	}

	private void close(Connection myConn, Statement myStmt, ResultSet myRs) {

		try {
			if (myRs != null) {
				myRs.close();
			}

			if (myStmt != null) {
				myStmt.close();
			}

			if (myConn != null) {
				myConn.close(); // doesn't really close it ... just puts back in connection pool
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public void addStudent(Student newStudent) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			myConn = dataSource.getConnection();

			String sql = "insert into student " + "(first_name, last_name, email) " + "values (?, ?, ?)";

			myStmt = myConn.prepareStatement(sql);

			myStmt.setString(1, newStudent.getFirstName());
			myStmt.setString(2, newStudent.getLastName());
			myStmt.setString(3, newStudent.getEmail());

			myStmt.execute();
		} finally {
			close(myConn, myStmt, null);
		}

	}

	public Student getStudent(String theStudentId) throws Exception {

		Student theStudent = null;

		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		int studentId;

		try {
			studentId = Integer.parseInt(theStudentId);

			myConn = dataSource.getConnection();

			String sql = "select * from student where id=?";

			myStmt = myConn.prepareStatement(sql);

			myStmt.setInt(1, studentId);

			myRs = myStmt.executeQuery();

			if (myRs.next()) {
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");

				theStudent = new Student(studentId, firstName, lastName, email);
			} else {
				throw new Exception("Could not find student id: " + studentId);
			}
			return theStudent;
		} finally {
			close(myConn, myStmt, myRs);
		}

	}

	public void updateStudent(Student student) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			myConn = dataSource.getConnection();

			String sql = "update student " + "set first_name=?, last_name=?, email=? " + "where id=?";

			myStmt = myConn.prepareStatement(sql);

			myStmt.setString(1, student.getFirstName());
			myStmt.setString(2, student.getLastName());
			myStmt.setString(3, student.getEmail());
			myStmt.setInt(4, student.getId());

			myStmt.execute();

		} finally {
			close(myConn, myStmt, null);
		}
	}

	public void deleteStudent(String studentId) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			int theStudentId = Integer.parseInt(studentId);
			
			myConn = dataSource.getConnection();
			
			String sql = "delete from student where id=?";
			
			myStmt = myConn.prepareStatement(sql);
			
			myStmt.setInt(1, theStudentId);
			
			myStmt.execute();
		} finally {
			close(myConn, myStmt, null);
		}
	}
}