/*
Programmer: Logan Willans
Description: Program simulates university enrollment application. Implemented with GUI using JavaFX and saves data to remote MySQL server.
 */

package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import java.util.Properties;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Main extends Application 
{
	// Declare variable objects
	Stage window;
	Scene mainPage, addStudentPage, studentDetailsPage, 
	searchStudentPage, addCoursePage, courseDetailsPage,
	searchCoursePage, addEnrollmentPage, searchEnrollmentPage, searchInstructorPage,
	searchDepartmentPage, manageGradesPage, reportPage, addInstructorPage, 
	instructorDetailsPage, addDepartmentPage, departmentDetailsPage;
	
	// State array
	String[] stateArray = {"AL", "AK", "AZ", "AR", "CA",
			"CO", "CT", "DC", "DE", "FL", "GA",
			"HI", "ID", "IL", "IN", "IA",
			"KS", "KY", "LA", "ME", "MD",
			"MA", "MI", "MN", "MS", "MO",
			"MT", "NE", "NV", "NH", "NJ",
			"NM", "NY", "NC", "ND", "OH",
			"OK", "OR", "PA", "RI", "SC",
			"SD", "TN", "TX", "UT", "VT",
			"VA", "WA", "WV", "WI", "WY"};
	
	// Semester array
	String[] semesterArray = {"Fall", "Winter", "Spring", "Summer"};
	
	// Grade array
	String[] gradeArray = {"A", "B", "C", "D", "F"};
	
	public static Connection getConnection() throws Exception
	{
		try
		{
			Properties props = new Properties();
			props.load(new FileInputStream("sql/app.config.txt"));
			String driver = "com.mysql.cj.jdbc.Driver";
			String dburl = props.getProperty("dburl");
			String username = props.getProperty("user");
			String password = props.getProperty("password");
			Class.forName(driver);
			Connection myConn = DriverManager.getConnection(dburl, username, password);
			return myConn;
		} catch(Exception e)
		{
			System.out.println(e);
			return null;
		}
	}
	
	public static void main(String[] args) 
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		window = primaryStage;
		try 
		{
			// Add icon to primary stage
			Image javaFxIcon = new Image(new FileInputStream("images/JavaFxIcon.png"));
			primaryStage.getIcons().add(javaFxIcon);
			
			// Establish connection with MySQL database
			Connection myConn = getConnection();
																																					
			// Create linked lists and array list for enrollments
			ArrayList<Enrollment> enrollments = new ArrayList<Enrollment>();  																				
			LinkedList<Student> students = new LinkedList<Student>();
			LinkedList<Course> courses = new LinkedList<Course>();
			LinkedList<Instructor> instructors = new LinkedList<Instructor>();
			LinkedList<Department> departments = new LinkedList<Department>();
			
			// Populate student linkedList with existing data
			if (tableHasData("students"))
			{
				try
				{
					Statement myStmt = myConn.createStatement();
					ResultSet myRs = myStmt.executeQuery("SELECT * FROM universityenrollment.students");
					while(myRs.next())
					{
						students.add(new Student(Integer.parseInt(myRs.getString("studentId")), myRs.getString("Name"), myRs.getString("address"), myRs.getString("city"), myRs.getString("state")));
					}	 
				} catch(Exception e)
				{
					System.out.println(e);
				} 
			}
	
			// Populate course linkedlist with existing data
			if (tableHasData("courses"))
			{
				try
				{
					Statement myStmt = myConn.createStatement();
					ResultSet myRs = myStmt.executeQuery("SELECT * FROM universityenrollment.courses");
					while(myRs.next())
					{
						courses.add(new Course(Integer.parseInt(myRs.getString("courseId")), 
								myRs.getString("courseName"), 
								myRs.getString("instructor"), 
								myRs.getString("department"),
								Integer.parseInt(myRs.getString("courseNumber")),
								Integer.parseInt(myRs.getString("instructorId")),
								Integer.parseInt(myRs.getString("departmentId"))));
					}	 
				} catch(Exception e)
				{
					System.out.println(e);
				} 
			}
			
			// Populate enrollment array with existing data
			if (tableHasData("enrollments"))
			{
				try
				{
					Statement myStmt = myConn.createStatement();
					ResultSet myRs = myStmt.executeQuery("SELECT * FROM universityenrollment.enrollments");
					while(myRs.next())
					{
						enrollments.add(new Enrollment(Integer.parseInt(myRs.getString("studentId")), 
								Integer.parseInt(myRs.getString("courseId")), 
								Integer.parseInt(myRs.getString("year")), 
								myRs.getString("semester"),
								myRs.getString("grade")
								));
					}	 
				} catch(Exception e)
				{
					System.out.println(e);
				} 
			}
			
			// Populate instructor linkedlist with existing data
			if (tableHasData("instructors"))
			{
				try
				{
					Statement myStmt = myConn.createStatement();
					ResultSet myRs = myStmt.executeQuery("SELECT * FROM universityenrollment.instructors");
					while(myRs.next())
					{
						instructors.add(new Instructor(Integer.parseInt(myRs.getString("instructorId")), myRs.getString("name"),  myRs.getString("department"), Integer.parseInt(myRs.getString("departmentId"))));
					}	 
				} catch(Exception e)
				{
					System.out.println(e);
				} 
			}
			
			// Populate department linkedlist with existing data
			if (tableHasData("departments"))
			{
				try
				{
					Statement myStmt = myConn.createStatement();
					ResultSet myRs = myStmt.executeQuery("SELECT * FROM universityenrollment.departments");
					while(myRs.next())
					{
						departments.add(new Department(Integer.parseInt(myRs.getString("departmentId")), myRs.getString("name")));
					}	 
				} catch(Exception e)
				{
					System.out.println(e);
				} 
			}

			// RedX Protocol
			window.setOnCloseRequest(e->closeProgram());
			
			// -------------------------------------------------------------------------------- Main Page ------------------------------------------------------------------------------------------------------
		
			// addMenu
			Menu addMenu = new Menu("Add");
			
			// add items to add menu
			// addStudent
			MenuItem addStudent = new MenuItem("Student");
			addStudent.setOnAction(e->
			{
				window.setScene(addStudentPage);
			});
			
			// addCourse
			MenuItem addCourse = new MenuItem("Course");
			addCourse.setOnAction(e->
			{
				window.setScene(addCoursePage);
			});
			
			// addEnrollment
			MenuItem addEnrollment = new MenuItem("Enrollment");
			addEnrollment.setOnAction(e->
			{
				window.setScene(addEnrollmentPage);
			});
			
			//addInstructor
			MenuItem addInstructor = new MenuItem("Instructor");
			addInstructor.setOnAction(e->
			{
				window.setScene(addInstructorPage);
			});
			
			//addDepartment
			MenuItem addDepartment = new MenuItem("Department");
			addDepartment.setOnAction(e->
			{
				window.setScene(addDepartmentPage);
			});
			
			// Adding all items to top menu
			addMenu.getItems().addAll(addStudent, addCourse, addEnrollment, addInstructor, addDepartment);
		
			// searchMenu
			Menu searchMenu = new Menu("Edit");
	
			// add items to search menu
			// searchStudent
			MenuItem searchStudent = new MenuItem("Student");
			searchStudent.setOnAction(e->
			{
				window.setScene(searchStudentPage);
			});
			
			// searchCourse
			MenuItem searchCourse = new MenuItem("Course");
			searchCourse.setOnAction(e->
			{
				window.setScene(searchCoursePage);
			});
			
			// searchEnrollment
			MenuItem searchEnrollment = new MenuItem("Enrollment");
			searchEnrollment.setOnAction(e->
			{
				window.setScene(searchEnrollmentPage);
			});
			
			// searchInstructor
			MenuItem searchInstructor = new MenuItem("Instructor");
			searchInstructor.setOnAction(e->
			{
				window.setScene(searchInstructorPage);
			});
			
			// searchInstructor
			MenuItem searchDepartment = new MenuItem("Department");
			searchDepartment.setOnAction(e->
			{
				window.setScene(searchDepartmentPage);
			});
			
			searchMenu.getItems().addAll(searchStudent, searchCourse, searchEnrollment, searchInstructor, searchDepartment);
	
			// gradeManagementMenu
			Menu gradeManagementMenu = new Menu("Grade Management");
	
			// add items to grade management menu
			MenuItem viewEditGrades = new MenuItem("Edit Grades");
			viewEditGrades.setOnAction(e->
			{
				window.setScene(manageGradesPage);
			});
			gradeManagementMenu.getItems().addAll(viewEditGrades);

			// reportMenu
			Menu reportMenu = new Menu("Report");
			
			// add items to report menu
			MenuItem createReport = new MenuItem("Create Report");
			createReport.setOnAction(e->
			{
				window.setScene(reportPage);
			});
			reportMenu.getItems().addAll(createReport);

			// Instantiate menu bar and add menus
			MenuBar menuBar = new MenuBar();
			menuBar.getMenus().addAll(addMenu, searchMenu, gradeManagementMenu, reportMenu);

			// Page formatting
			BorderPane mainPageBP = new BorderPane();
			mainPageBP.setTop(menuBar);
			mainPage = new Scene(mainPageBP, 750, 750);
			
			// Adding university image
			//Creating an image 
			Image stanfordImage = new Image(new FileInputStream("images/Standford.png")); 
		     
		    //Setting the image view 
			ImageView mainUniversityView = new ImageView(stanfordImage);
		     
			//Setting the position of the image 
			mainUniversityView.setX(50); 
			mainUniversityView.setY(25); 
		    
		    mainPageBP.setCenter(mainUniversityView);
			
			//--------------------------------------------------------------------------------- addStudent Page -----------------------------------------------------------------------------------------
			
			// format addStudentGrid
			GridPane addStudentGrid = new GridPane();
			addStudentGrid.setPadding(new Insets(10,10,10,10));
			addStudentGrid.setVgap(8);
			addStudentGrid.setVgap(10);
			
			// Create objects for addStudentPage
			
			// ADD A NEW STUDENT LABEL
			Label addANewStudentLabel = new Label("ADD A NEW STUDENT");
			addStudentGrid.setConstraints(addANewStudentLabel,3,2);
			
			// Name Label and Enter Name Text Box
			Label nameLabel = new Label("Enter Student Name:  ");
			addStudentGrid.setConstraints(nameLabel, 3,3);
			TextField nameInput = new TextField();
			addStudentGrid.setConstraints(nameInput, 4,3);
			
			// Address Label and Enter Address Text Box
			Label addressLabel = new Label("Enter Student Address:  ");
			addStudentGrid.setConstraints(addressLabel, 3,4);
			TextField addressInput = new TextField();
			addStudentGrid.setConstraints(addressInput, 4,4);
			
			// City Label and Enter City Text Box
			Label cityLabel = new Label("Enter Student City:  ");
			addStudentGrid.setConstraints(cityLabel, 3,5);
			TextField cityInput = new TextField();
			addStudentGrid.setConstraints(cityInput, 4,5);

			// State Label and Enter State Choice Box
			Label stateLabel = new Label("Select Student State:  ");
			addStudentGrid.setConstraints(stateLabel, 3,6);
			ChoiceBox<String> stateChoiceBox = new ChoiceBox<>();
			stateChoiceBox.getItems().addAll(stateArray);
			addStudentGrid.setConstraints(stateChoiceBox, 4, 6);
			
			// Blank space label
			Label addStudentBlankSpaceLabel = new Label("Error: Blank space. Please enter all information. ");
			addStudentGrid.setConstraints(addStudentBlankSpaceLabel, 3,7);
			addStudentBlankSpaceLabel.setVisible(false);
			
			// Cancel button
			Button cancelButton = new Button("Cancel");
			cancelButton.setOnAction(e->
			{
				addStudentBlankSpaceLabel.setVisible(false);
				nameInput.clear();
				addressInput.clear();
				cityInput.clear();
				stateChoiceBox.getSelectionModel().clearSelection();
				window.setScene(mainPage);
			});
			addStudentGrid.setConstraints(cancelButton, 4,8);
			
			// Submit button
			Button submitButton = new Button("Submit");
			submitButton.setOnAction(e->
			{
				int ID = 1;
				boolean duplicate = false;
				boolean blankSpace = false;
				addStudentBlankSpaceLabel.setVisible(false);
				
				// Create temporary student object and check for duplicate ID
				Student temp = new Student(ID);
				duplicate = students.duplicate(temp);
								
				while (duplicate == true)
				{
					ID += 1;
					temp.setStudentId(ID);
					duplicate = students.duplicate(temp);
				}
				
				if(nameInput.getText().isEmpty() || addressInput.getText().isEmpty() || cityInput.getText().isEmpty() || getChoice(stateChoiceBox) == null)
				{
					blankSpace = true;
				}
				
				if(blankSpace == false)
				{
					// Generate student object
					students.add(new Student(ID, nameInput.getText(), addressInput.getText(), cityInput.getText(), getChoice(stateChoiceBox)));
					
					try 
					{
						createStudentRecord(ID, nameInput.getText(), addressInput.getText(), cityInput.getText(), getChoice(stateChoiceBox));
																																								
					} catch(Exception e1)
					{
						System.out.println("Something went wrong");
					}
					nameInput.clear(); 
					addressInput.clear(); 
					cityInput.clear(); 
					stateChoiceBox.getSelectionModel().clearSelection();
					addStudentBlankSpaceLabel.setVisible(false);
					window.setScene(studentDetailsPage);
				} else
				{
					addStudentBlankSpaceLabel.setVisible(true);
				}
			});
			addStudentGrid.setConstraints(submitButton, 3,8);
			
			// Add all objects to Grid Pane
			addStudentGrid.getChildren().addAll(addANewStudentLabel, nameLabel, nameInput, 
					addressLabel, addressInput, cityLabel, 
					cityInput, stateLabel, stateChoiceBox, 
					submitButton, cancelButton, addStudentBlankSpaceLabel);
		
			// Create addStudentPage Scene
			addStudentPage = new Scene(addStudentGrid, 750, 750);
			
			//---------------------------------------------------------------------------  studentDetails Page ------------------------------------------------------------------------------------------------------------------------------------------------------
			
			// format addStudentDetailsGrid
			GridPane studentDetailsGrid = new GridPane();
			studentDetailsGrid.setPadding(new Insets(10,10,10,10));
			studentDetailsGrid.setVgap(8);
			studentDetailsGrid.setVgap(10);
	
			// Student Successfully Added Label
			Label successfullyAddedLabel = new Label("STUDENT SUCCESSFULLY ADDED");
			studentDetailsGrid.setConstraints(successfullyAddedLabel, 0,0);

			// Label for Newly Added Student's ID Number
			Label newlyAddedStudentIDNumberLabel = new Label("");
			studentDetailsGrid.setConstraints(newlyAddedStudentIDNumberLabel, 1,1);
			
			// Button for Newly Added Student's ID Number
			Button generateStudentIDButton = new Button("Generate Newly Added Student's ID Number");
			generateStudentIDButton.setOnAction(e->
			{
				Student mostRecentlyAddedStudent = new Student(-1);
				mostRecentlyAddedStudent = students.getLast();
				newlyAddedStudentIDNumberLabel.setText("  The ID of the student you just added is " + mostRecentlyAddedStudent.getStudentId());
				
			});
			studentDetailsGrid.setConstraints(generateStudentIDButton, 0,1);
			
			// Add New Student Button
			Button addNewStudentButton = new Button("Add Another Student");
			addNewStudentButton.setOnAction(e->
			{
				newlyAddedStudentIDNumberLabel.setText("");
				window.setScene(addStudentPage);
			});
			studentDetailsGrid.setConstraints(addNewStudentButton, 0,2);
	
			// Return to main page button
			Button mainPageButton = new Button("Return to Main Page");
			mainPageButton.setOnAction(e->
			{
				newlyAddedStudentIDNumberLabel.setText("");
				window.setScene(mainPage);
			});
			studentDetailsGrid.setConstraints(mainPageButton, 1,2);
	
			// Add all objects to Grid Pane
			studentDetailsGrid.getChildren().addAll(successfullyAddedLabel, generateStudentIDButton, mainPageButton, addNewStudentButton, newlyAddedStudentIDNumberLabel);
			
			// Create studentDetails Scene
			studentDetailsPage = new Scene(studentDetailsGrid, 750, 750);	
		
			// -------------------------------------------------------------------------------------- addCourse Page ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
			// format addCourseGrid
			GridPane addCourseGrid = new GridPane();
			addCourseGrid.setPadding(new Insets(10,10,10,10));
			addCourseGrid.setVgap(8);
			addCourseGrid.setVgap(10);
			
			// Create objects for addCoursePage
			
			// ADD A NEW COURSE LABEL
			Label addNewCourseLabel = new Label("ADD A NEW COURSE");
			addCourseGrid.setConstraints(addNewCourseLabel,0,0);
			
			// Course Name Label and Enter Course Name Text Box
			Label courseNameLabel = new Label("Enter Course Name:  ");
			addCourseGrid.setConstraints(courseNameLabel, 0,1);
			TextField courseNameInput = new TextField();
			addCourseGrid.setConstraints(courseNameInput, 1,1);
		
			// Department drop down menu
			Label departmentLabel = new Label("Select Department:  ");
			addCourseGrid.setConstraints(departmentLabel, 0,2);  
			ChoiceBox<String> departmentChoiceBox = new ChoiceBox<>();
			addCourseGrid.setConstraints(departmentChoiceBox, 1,2);
			
			departmentChoiceBox.setOnMouseClicked(e->
			{
				// Clear departmentChoiceBox from previous click
				departmentChoiceBox.getItems().clear();
				
				boolean departmentListIsEmpty = departments.isEmpty();
				Department tempDepartment = new Department(-1);
				ArrayList<String> departmentNames = new ArrayList();
				
				// Get names of all department and add to choice box
				if(departmentListIsEmpty)
				{
					departmentChoiceBox.getItems().add("Please first add department(s)");
				} else
				{
					tempDepartment = departments.getFirst();
					for(int i = 0; i < departments.size(); i ++)
					{
						departmentNames.add(tempDepartment.getName());
						tempDepartment = departments.getNext(tempDepartment);
					}
					
					for(String i : departmentNames)
					{
						departmentChoiceBox.getItems().add(i);
					}
				}
			});
			
			// Instructor drop down menu
			Label instructorLabel = new Label("Select Instructor:  ");
			addCourseGrid.setConstraints(instructorLabel, 0,3);  
			ChoiceBox<String> instructorChoiceBox = new ChoiceBox<>();
			addCourseGrid.setConstraints(instructorChoiceBox, 1,3);  
			
			instructorChoiceBox.setOnMouseClicked(e->
			{
				// Clear instructorChoiceBox from previous click
				instructorChoiceBox.getItems().clear();
				
				boolean instructorListIsEmpty = instructors.isEmpty();
				Instructor tempInstructor = new Instructor(-1);
				ArrayList<String> instructorNames = new ArrayList();
				
				// Get names of all instructors and add to choice box
				if(instructorListIsEmpty || getChoice(departmentChoiceBox) == null)
				{
					instructorChoiceBox.getItems().add("Error. Please first add instructor(s) or select a department.");
				} else
				{
					String selectedDepartment = cleanString(getChoice(departmentChoiceBox));
					tempInstructor = instructors.getFirst();
					for(int i = 0; i < instructors.size(); i ++)
					{
						String tempInstructorDepartment = cleanString(tempInstructor.getDepartment());
						if(tempInstructorDepartment.equals(selectedDepartment))
						{
							instructorNames.add(tempInstructor.getName());
						}
						tempInstructor = instructors.getNext(tempInstructor);
					}
					
					for (String i : instructorNames)
					{
						instructorChoiceBox.getItems().add(i);
						
					}
				}
			});
	
			// Course Number Label and Enter Course Number Text Box
			Label courseNumberLabel = new Label("Enter Course Number:  ");
			addCourseGrid.setConstraints(courseNumberLabel, 0,4);
			TextField courseNumberInput = new TextField();
			addCourseGrid.setConstraints(courseNumberInput, 1,4);
			
			// Course blank space label
			Label addCourseBlankSpaceLabel = new Label("Error: Blank space. Please enter all information. ");
			addCourseGrid.setConstraints(addCourseBlankSpaceLabel, 0,6);
			addCourseBlankSpaceLabel.setVisible(false);
			
			// Add Course Cancel Button to Add Course Page
			Button courseCancelButton = new Button("Cancel");
			courseCancelButton.setOnAction(e->
			{
				courseNameInput.clear();
				instructorChoiceBox.getSelectionModel().clearSelection();
				departmentChoiceBox.getSelectionModel().clearSelection();
				courseNumberInput.clear();
				addCourseBlankSpaceLabel.setVisible(false);
				window.setScene(mainPage);
			});
			addCourseGrid.setConstraints(courseCancelButton, 1,5);
			
			// Course Submit button
			Button courseSubmitButton = new Button("Submit");
			courseSubmitButton.setOnAction(e->
			{
				int courseID = 1;
				boolean duplicate = false;
				boolean blankSpace = false;
				addStudentBlankSpaceLabel.setVisible(false);
				
				// Create temporary course object and check for duplicate ID
				Course temp = new Course(courseID);
				duplicate = courses.duplicate(temp);
				
				while (duplicate == true)
				{
					courseID += 1;
					temp.setCourseId(courseID);
					duplicate = courses.duplicate(temp);
				}
								
				if(courseNameInput.getText().isEmpty() || (getChoice(instructorChoiceBox) == null) 
						|| (getChoice(instructorChoiceBox) == "Error. Please first add instructor(s) or select a department.") ||
						(getChoice(departmentChoiceBox) == null) || courseNumberInput.getText().isEmpty())
				{
					blankSpace = true;
				}
				
				if(blankSpace == false)
				{
					
					// Generate course object, but first we need to get instructorId and departmentId
					int instructorId = -1;
					Instructor tempInstructor = instructors.getFirst();
					while(tempInstructor != null)
					{
						if(tempInstructor.getName() == getChoice(instructorChoiceBox))
						{
							instructorId = tempInstructor.getInstructorId();
						}
						tempInstructor = instructors.getNext(tempInstructor);
					}
						
					int departmentId = -1;
					Department tempDepartment = departments.getFirst();
					while(tempDepartment != null)
					{
						if(tempDepartment.getName() == getChoice(departmentChoiceBox))
						{
							departmentId = tempDepartment.getDepartmentId();
						}
						tempDepartment = departments.getNext(tempDepartment);
					}
					
					courses.add(new Course(courseID, 
							courseNameInput.getText(), 
							getChoice(instructorChoiceBox), 
							getChoice(departmentChoiceBox), 
							Integer.parseInt(courseNumberInput.getText()),
							instructorId,
							departmentId));
					try 
					{
						createCourseRecord(courseID, 
								courseNameInput.getText(), 
								getChoice(instructorChoiceBox), 
								getChoice(departmentChoiceBox), 
								Integer.parseInt(courseNumberInput.getText()),
								instructorId,
								departmentId);
					} catch(Exception e1)
					{
						System.out.println("Something went wrong");
					}
					courseNameInput.clear(); 
					instructorChoiceBox.getSelectionModel().clearSelection(); 
					departmentChoiceBox.getSelectionModel().clearSelection(); 
					courseNumberInput.clear();
					
					window.setScene(courseDetailsPage);
				} else
				{
					addCourseBlankSpaceLabel.setVisible(true);
				}
			});		
			addCourseGrid.setConstraints(courseSubmitButton, 0,5);
			
			// Add all objects to Grid Pane
			addCourseGrid.getChildren().addAll(addNewCourseLabel, courseNameLabel, courseNameInput, 
					instructorLabel, departmentLabel, 
					departmentChoiceBox, courseNumberLabel, courseNumberInput, 
					courseCancelButton, courseSubmitButton, addCourseBlankSpaceLabel,
					instructorChoiceBox);
			
			// Create addCoursePage Scene
			addCoursePage = new Scene(addCourseGrid, 750, 750);
		
			// ---------------------------------------------------------------------------------------  courseDetails Page  ------------------------------------------------------------------------------------------------------------------------------------------------------
			
			// format addCourseDetailsGrid
			GridPane courseDetailsGrid = new GridPane();
			courseDetailsGrid.setPadding(new Insets(10,10,10,10));
			courseDetailsGrid.setVgap(8);
			courseDetailsGrid.setVgap(10);
	
			// Course Successfully Added Label
			Label courseSuccessfullyAddedLabel = new Label("COURSE SUCCESSFULLY ADDED");
			courseDetailsGrid.setConstraints(courseSuccessfullyAddedLabel, 0,0);

			// Label for Newly Added course's ID Number
			Label newlyAddedCourseIDNumberLabel = new Label("");
			courseDetailsGrid.setConstraints(newlyAddedCourseIDNumberLabel, 1,1);
			
			// Button for Newly Added Course's ID Number
			Button generateCourseIDButton = new Button("Generate Newly Added Course's ID Number");
			generateCourseIDButton.setOnAction(e->
			{
				Course mostRecentlyAddedCourse = new Course(-1);
				mostRecentlyAddedCourse = courses.getLast();
				newlyAddedCourseIDNumberLabel.setText("  The ID of the course you just added is " + mostRecentlyAddedCourse.getCourseId());
				
			});
			courseDetailsGrid.setConstraints(generateCourseIDButton, 0,1);
	
			// Add New course Button
			Button addNewCourseButton = new Button("Add Another Course");
			addNewCourseButton.setOnAction(e->
			{
				newlyAddedCourseIDNumberLabel.setText("");
				window.setScene(addCoursePage);
			});
			courseDetailsGrid.setConstraints(addNewCourseButton, 0,2);
	
			// Return to main page button
			Button mainPageButton2 = new Button("Return to Main Page");
			mainPageButton2.setOnAction(e->
			{
				newlyAddedCourseIDNumberLabel.setText("");
				window.setScene(mainPage);
			});
			studentDetailsGrid.setConstraints(mainPageButton2, 1,2);
	
			// Add all objects to Grid Pane
			courseDetailsGrid.getChildren().addAll(courseSuccessfullyAddedLabel, generateCourseIDButton, mainPageButton2, addNewCourseButton, newlyAddedCourseIDNumberLabel);
			
			// Create studentDetails Scene
			courseDetailsPage = new Scene(courseDetailsGrid, 750, 750);
			
			// ------------------------------------------------------------------------------------------- addEnrollmentPage ----------------------------------------------------------------------------------------------------------------------------
			
			// format addEnrollmentGrid
			GridPane addEnrollmentGrid = new GridPane();
			addEnrollmentGrid.setPadding(new Insets(10,10,10,10));
			addEnrollmentGrid.setVgap(8);
			addEnrollmentGrid.setVgap(10);

			// Create objects for addEnrollmentPage
			// ADD A NEW ENROLLMENT LABEL
			Label addNewEnrollmentLabel = new Label("ADD A NEW ENROLLMENT");
			addEnrollmentGrid.setConstraints(addNewEnrollmentLabel,0,0);
			
			// Student ID Label and Enter Student ID Text Field
			Label enrollmentStudentIDLabel = new Label("Enter Student ID:  ");
			addEnrollmentGrid.setConstraints(enrollmentStudentIDLabel, 0,1);
			TextField enrollmentStudentIDInput = new TextField();
			addEnrollmentGrid.setConstraints(enrollmentStudentIDInput, 1,1);
			
			// Course ID Label and Enter Course ID Text Field
			Label enrollmentCourseIDLabel = new Label("Select a Course:  ");
			addEnrollmentGrid.setConstraints(enrollmentCourseIDLabel, 0,2);
			ChoiceBox<String> addEnrollmentCourseChoiceBox = new ChoiceBox<>();
			addEnrollmentGrid.setConstraints(addEnrollmentCourseChoiceBox, 1, 2);
			addEnrollmentCourseChoiceBox.setOnMouseClicked(e->
			{
				// Clear choice box from previous click
				addEnrollmentCourseChoiceBox.getItems().clear();
				
				boolean courseListIsEmpty = courses.isEmpty();
				Course tempCourse = new Course(-1);
				ArrayList<String> courseNames = new ArrayList();
				
				if(courseListIsEmpty)
				{
					addEnrollmentCourseChoiceBox.getItems().add("Please first add course(s)");
				} else
				{
					tempCourse = courses.getFirst();
					for(int i = 0; i < courses.size(); i++)
					{
						courseNames.add(tempCourse.getCourseName());
						tempCourse = courses.getNext(tempCourse);
					}
					
					for (String i: courseNames)
					{
						addEnrollmentCourseChoiceBox.getItems().add(i);
					}
				}
			});
			
			// No match course ID or student ID label
			Label addEnrollmentNoMatchLabel = new Label("");
			addEnrollmentGrid.setConstraints(addEnrollmentNoMatchLabel, 2,2);
			
			// Year Label and Enter Year Text Field
			Label yearLabel = new Label("Enter Year:  ");
			addEnrollmentGrid.setConstraints(yearLabel, 0,3);
			TextField yearInput = new TextField();
			addEnrollmentGrid.setConstraints(yearInput, 1,3);

			// Semester Label and Select Semester Choice Box
			Label semesterLabel = new Label("Select Semester:  ");
			addEnrollmentGrid.setConstraints(semesterLabel, 0,4);
			ChoiceBox<String> semesterChoiceBox = new ChoiceBox<>();
			semesterChoiceBox.getItems().addAll(semesterArray);
			addEnrollmentGrid.setConstraints(semesterChoiceBox, 1, 4);

			// Grade Label and Enter Grade Choice Box
			Label gradeLabel = new Label("Select Grade:  ");
			addEnrollmentGrid.setConstraints(gradeLabel, 0,5);
			ChoiceBox<String> gradeChoiceBox = new ChoiceBox<>();
			gradeChoiceBox.getItems().addAll(gradeArray);
			addEnrollmentGrid.setConstraints(gradeChoiceBox, 1, 5);
			
			// Blank spaces error label
			Label addEnrollmentNoBlankSpacesErrorLabel = new Label("Error: Please input all information. No blank spaces. ");
			addEnrollmentGrid.setConstraints(addEnrollmentNoBlankSpacesErrorLabel, 0,7);
			addEnrollmentNoBlankSpacesErrorLabel.setVisible(false);
			
			// Cancel and Submit Buttons
			Button enrollCancelButton = new Button("Cancel");
			enrollCancelButton.setOnAction(e->
			{
				addEnrollmentNoMatchLabel.setText("");
				enrollmentStudentIDInput.clear();
				addEnrollmentCourseChoiceBox.getSelectionModel().clearSelection();
				yearInput.clear();
				semesterChoiceBox.getSelectionModel().clearSelection();
				gradeChoiceBox.getSelectionModel().clearSelection();
				addEnrollmentNoBlankSpacesErrorLabel.setVisible(false);
				window.setScene(mainPage);
			});
			addEnrollmentGrid.setConstraints(enrollCancelButton, 1,6);
			
			Button enrollSubmitButton = new Button("Submit");
			enrollSubmitButton.setOnAction(e->
			{
				boolean validStudentId = false;
				boolean doesNotAlreadyExist = true;
				boolean noBlankSpaces = true;
				
				// Check for blank spaces
				if(enrollmentStudentIDInput.getText().isEmpty() 
						|| getChoice(addEnrollmentCourseChoiceBox) == null 
						|| yearInput.getText().isEmpty() 
						|| (getChoice(semesterChoiceBox) == null) 
						|| (getChoice(gradeChoiceBox) == null))
				{
					addEnrollmentNoBlankSpacesErrorLabel.setVisible(true);
					noBlankSpaces = false;
				}
				
				if(noBlankSpaces)
				{
					String selectedCourse = cleanString(getChoice(addEnrollmentCourseChoiceBox));
					Course tempCourse = courses.getFirst();
					String tempCourseName = null;
					int courseId = -1;
					for(int i = 0; i < courses.size(); i++)
					{	
						tempCourseName = cleanString(tempCourse.getCourseName());
						if(tempCourseName.equals(selectedCourse))
						{
							courseId = tempCourse.getCourseId();
						}
						tempCourse = courses.getNext(tempCourse);
					}
					
					int studentId = Integer.parseInt(enrollmentStudentIDInput.getText()); 
					int year = Integer.parseInt(yearInput.getText());
					
					// Validate student ID by using temporary student object
					Student temp = new Student(studentId);
					validStudentId = students.duplicate(temp);
				
					// Check if enrollment already exists
					for (Enrollment e3 : enrollments)
					{
						if (e3.getStudentId() == studentId && e3.getCourseId() == courseId)
						{
							doesNotAlreadyExist = false;
						}
					}
					
					if(validStudentId && doesNotAlreadyExist)
					{
						
						// Generate enrollment object
						enrollments.add(new Enrollment(studentId, courseId, year, getChoice(semesterChoiceBox), getChoice(gradeChoiceBox)));
						try 
						{
							createEnrollmentRecord(studentId, courseId, year, getChoice(semesterChoiceBox), getChoice(gradeChoiceBox));
						} catch(Exception e1)
						{
							System.out.println("Something went wrong");
						}
						enrollmentStudentIDInput.clear();
						addEnrollmentCourseChoiceBox.getSelectionModel().clearSelection();
						yearInput.clear();
						semesterChoiceBox.getSelectionModel().clearSelection();
						gradeChoiceBox.getSelectionModel().clearSelection();
						addEnrollmentNoMatchLabel.setText("");
						addEnrollmentNoBlankSpacesErrorLabel.setVisible(false);
						noBlankSpaces = true;
						window.setScene(mainPage);
					} else
					{
						addEnrollmentNoMatchLabel.setText("  Student ID or Course ID is Incorrect or Enrollment already exists.");
						validStudentId = false;
						doesNotAlreadyExist = true;
						addEnrollmentNoBlankSpacesErrorLabel.setVisible(false);
						noBlankSpaces = true;
					}
				}
			});
			
			addEnrollmentGrid.setConstraints(enrollSubmitButton, 0,6);

		
			// Add all objects to Grid Pane
			addEnrollmentGrid.getChildren().addAll(addNewEnrollmentLabel, enrollmentStudentIDLabel, enrollmentStudentIDInput, 
					enrollmentCourseIDLabel, addEnrollmentCourseChoiceBox, yearLabel, 
					yearInput, semesterLabel, semesterChoiceBox, 
					gradeLabel, gradeChoiceBox, enrollCancelButton, 
					enrollSubmitButton, addEnrollmentNoMatchLabel, addEnrollmentNoBlankSpacesErrorLabel);
			
			// Create addEnrollmentPage Scene
			addEnrollmentPage = new Scene(addEnrollmentGrid, 750, 750);
			
			// -------------------------------------------------------------------------- Add Instructor Page ------------------------------------------------------------------------------------------

			// format addInstructorGrid
			GridPane addInstructorGrid = new GridPane();
			addInstructorGrid.setPadding(new Insets(10,10,10,10));
			addInstructorGrid.setVgap(8);
			addInstructorGrid.setVgap(10);
			
			// Create objects for addInstructorPage
			
			// ADD A NEW INSTRUCTOR LABEL
			Label addANewInstructorLabel = new Label("ADD A NEW INSTRUCTOR");
			addInstructorGrid.setConstraints(addANewInstructorLabel,3,2);
			
			// Name Label and Enter Name Text Box
			Label instructorNameLabel = new Label("Enter Instructor Name:  ");
			addInstructorGrid.setConstraints(instructorNameLabel, 3,3);
			TextField instructorNameInput1 = new TextField();
			addInstructorGrid.setConstraints(instructorNameInput1, 4,3);
			
			// Department Label and Enter Department Choice Box
			Label instructorDepartmentLabel = new Label("Select Instructor Department:  ");
			addInstructorGrid.setConstraints(instructorDepartmentLabel, 3,4);
			ChoiceBox<String> departmentChoiceBox1 = new ChoiceBox<>();
			addInstructorGrid.setConstraints(departmentChoiceBox1, 4,4);
			departmentChoiceBox1.setOnMouseClicked(e->
			{
				// Clear departmentChoiceBox from previous click
				departmentChoiceBox1.getItems().clear();
				
				boolean departmentListIsEmpty = departments.isEmpty();
				Department tempDepartment = new Department(-1);
				ArrayList<String> departmentNames = new ArrayList();
				
				// Get names of all departments and add to choice box
				if(departmentListIsEmpty)
				{
					departmentChoiceBox1.getItems().add("Please first add department(s)");
				} else
				{
					tempDepartment = departments.getFirst();
					for(int i = 0; i < departments.size(); i ++)
					{
						departmentNames.add(tempDepartment.getName());
						tempDepartment = departments.getNext(tempDepartment);
					}
					
					for(String i : departmentNames)
					{
						departmentChoiceBox1.getItems().add(i);
					}
				}
			});
			
			// Blank space label
			Label addInstructorBlankSpaceLabel = new Label("Error: Blank space. Please enter all information. ");
			addInstructorGrid.setConstraints(addInstructorBlankSpaceLabel, 3,7);
			addInstructorBlankSpaceLabel.setVisible(false);
			
			// Cancel button
			Button instructorCancelButton = new Button("Cancel");
			instructorCancelButton.setOnAction(e->
			{
				addInstructorBlankSpaceLabel.setVisible(false);
				instructorNameInput1.clear();
				departmentChoiceBox1.getSelectionModel().clearSelection();
				window.setScene(mainPage);
			});
			addInstructorGrid.setConstraints(instructorCancelButton, 4,8);
			
			// Instructor Submit Button
			Button instructorSubmitButton = new Button("Submit");
			instructorSubmitButton .setOnAction(e->
			{
				int ID = 1;
				boolean duplicate = false;
				boolean blankSpace = false;
				addInstructorBlankSpaceLabel.setVisible(false);
				
				// Create temporary instructor object and check for duplicate ID
				Instructor temp = new Instructor(ID);
				duplicate = instructors.duplicate(temp);
								
				while (duplicate == true)
				{
					ID += 1;
					temp.setInstructorId(ID);
					duplicate = instructors.duplicate(temp);
				}
				
				if(instructorNameInput1.getText().isEmpty() || getChoice(departmentChoiceBox1) == "Please first add department(s)" || getChoice(departmentChoiceBox1) == null)
				{
					blankSpace = true;
				}
				
				if(blankSpace == false)
				{
					// Generate instructor object - first we need to obtain the departmentId by using whatever department the user selected
					int departmentId = -1;
					Department tempDepartment = departments.getFirst();
					while(tempDepartment != null)
					{
						if(tempDepartment.getName() == getChoice(departmentChoiceBox1))
						{
							departmentId = tempDepartment.getDepartmentId();
						}
						tempDepartment = departments.getNext(tempDepartment);
					}
					
					instructors.add(new Instructor(ID, instructorNameInput1.getText(), getChoice(departmentChoiceBox1), departmentId));
					
					try 
					{
						createInstructorRecord(ID, instructorNameInput1.getText(), getChoice(departmentChoiceBox1), departmentId);
					} catch(Exception e1)
					{
						System.out.println("Something went wrong");
					}
					instructorNameInput1.clear(); 
					departmentChoiceBox1.getSelectionModel().clearSelection();
					addInstructorBlankSpaceLabel.setVisible(false);
					window.setScene(instructorDetailsPage);
				} else
				{
					addInstructorBlankSpaceLabel.setVisible(true);
				}
			});
			addInstructorGrid.setConstraints(instructorSubmitButton, 3,8);
					
			// Add all objects to Grid Pane
			addInstructorGrid.getChildren().addAll(addANewInstructorLabel, instructorNameLabel, instructorNameInput1, 
					instructorDepartmentLabel, departmentChoiceBox1, instructorSubmitButton, 
					instructorCancelButton, addInstructorBlankSpaceLabel);
			
			// Create addInstructorPage Scene
			addInstructorPage = new Scene(addInstructorGrid, 750, 750);
			
			// ------------------------------------------------------------------------------ Instructor Details Page -------------------------------------------------------------------------------------

			// format instructorDetailsGrid
			GridPane instructorDetailsGrid = new GridPane();
			instructorDetailsGrid.setPadding(new Insets(10,10,10,10));
			instructorDetailsGrid.setVgap(8);
			instructorDetailsGrid.setVgap(10);
	
			// Instructor Successfully Added Label
			Label instructorSuccessfullyAddedLabel = new Label("INSTRUCTOR SUCCESSFULLY ADDED");
			instructorDetailsGrid.setConstraints(instructorSuccessfullyAddedLabel, 0,0);

			// Label for Newly Added Instructor's ID Number
			Label newlyAddedInstructorIDNumberLabel = new Label("");
			instructorDetailsGrid.setConstraints(newlyAddedInstructorIDNumberLabel, 1,1);
			
			// Button for Newly Added Instructor's ID Number
			Button generateInstructorIDButton = new Button("Generate Newly Added Instructor's ID Number");
			generateInstructorIDButton.setOnAction(e->
			{
				Instructor mostRecentlyAddedInstructor = new Instructor(-1);
				mostRecentlyAddedInstructor = instructors.getLast();
				newlyAddedInstructorIDNumberLabel.setText("  The ID of the instructor you just added is " + mostRecentlyAddedInstructor.getInstructorId());
				
			});
			instructorDetailsGrid.setConstraints(generateInstructorIDButton, 0,1);
			
			// Add New Instructor Button
			Button addNewInstructorButton = new Button("Add Another Instructor");
			addNewInstructorButton.setOnAction(e->
			{
				newlyAddedInstructorIDNumberLabel.setText("");
				window.setScene(addInstructorPage);
			});
			instructorDetailsGrid.setConstraints(addNewInstructorButton, 0,2);
	
			// Return to main page button
			Button instructorDetailsMainPageButton = new Button("Return to Main Page");
			instructorDetailsMainPageButton.setOnAction(e->
			{
				newlyAddedInstructorIDNumberLabel.setText("");
				window.setScene(mainPage);
			});
			instructorDetailsGrid.setConstraints(instructorDetailsMainPageButton, 1,2);
	
			// Add all objects to Grid Pane
			instructorDetailsGrid.getChildren().addAll(instructorSuccessfullyAddedLabel, generateInstructorIDButton, instructorDetailsMainPageButton, addNewInstructorButton, newlyAddedInstructorIDNumberLabel);
			
			// Create instructorDetails Scene
			instructorDetailsPage = new Scene(instructorDetailsGrid, 750, 750);	
		
			// --------------------------------------------------------------------------  Add Department Page ------------------------------------------------------------------------------------------

			// format addDepartmentGrid
			GridPane addDepartmentGrid = new GridPane();
			addDepartmentGrid.setPadding(new Insets(10,10,10,10));
			addDepartmentGrid.setVgap(8);
			addDepartmentGrid.setVgap(10);
			
			// Create objects for addDepartmentPage
			
			// ADD A NEW DEPARTMENT LABEL
			Label addANewDepartmentLabel = new Label("ADD A NEW DEPARTMENT");
			addDepartmentGrid.setConstraints(addANewDepartmentLabel,3,2);
			
			// Name Label and Enter Name Text Box
			Label departmentNameLabel = new Label("Enter Department Name:  ");
			addDepartmentGrid.setConstraints(departmentNameLabel, 3,3);
			TextField departmentNameInput1 = new TextField();
			addDepartmentGrid.setConstraints(departmentNameInput1, 4,3);
			
			// Blank space label
			Label addDepartmentBlankSpaceLabel = new Label("Error: Blank space. Please enter all information. ");
			addDepartmentGrid.setConstraints(addDepartmentBlankSpaceLabel, 3,4);
			addDepartmentBlankSpaceLabel.setVisible(false);
			
			// Cancel button
			Button departmentCancelButton = new Button("Cancel");
			departmentCancelButton.setOnAction(e->
			{
				addDepartmentBlankSpaceLabel.setVisible(false);
				departmentNameInput1.clear();
				window.setScene(mainPage);
			});
			addDepartmentGrid.setConstraints(departmentCancelButton, 4,5);
			
			// Department Submit Button
			Button departmentSubmitButton = new Button("Submit");
			departmentSubmitButton .setOnAction(e->
			{
				int ID = 1;
				boolean duplicate = false;
				boolean blankSpace = false;
				addDepartmentBlankSpaceLabel.setVisible(false);
				
				// Create temporary department object and check for duplicate ID
				Department temp = new Department(ID);
				duplicate = departments.duplicate(temp);
								
				while (duplicate == true)
				{
					ID += 1;
					temp.setDepartmentId(ID);
					duplicate = departments.duplicate(temp);
				}
				
				if(departmentNameInput1.getText().isEmpty())
				{
					blankSpace = true;
				}
				
				if(blankSpace == false)
				{
					// Generate department object
					departments.add(new Department(ID, departmentNameInput1.getText())); 
					
					try 
					{
						createDepartmentRecord(ID, departmentNameInput1.getText());																		
					} catch(Exception e1)
					{
						System.out.println("Something went wrong");
					}
					departmentNameInput1.clear(); 
					addDepartmentBlankSpaceLabel.setVisible(false);
					window.setScene(departmentDetailsPage);
				} else
				{
					addDepartmentBlankSpaceLabel.setVisible(true);
				}
			});
			addDepartmentGrid.setConstraints(departmentSubmitButton, 3,5);
					
			// Add all objects to Grid Pane
			addDepartmentGrid.getChildren().addAll(addANewDepartmentLabel, departmentNameLabel, departmentNameInput1, 
					departmentSubmitButton, departmentCancelButton, addDepartmentBlankSpaceLabel);
	
			// Create addDepartmentPage Scene
			addDepartmentPage = new Scene(addDepartmentGrid, 750, 750);
			
			// ------------------------------------------------------------------------------ departmentDetails Page -------------------------------------------------------------------------------------
			
			// format departmentDetailsGrid
			GridPane departmentDetailsGrid = new GridPane();
			departmentDetailsGrid.setPadding(new Insets(10,10,10,10));
			departmentDetailsGrid.setVgap(8);
			departmentDetailsGrid.setVgap(10);
	
			// Department Successfully Added Label
			Label departmentSuccessfullyAddedLabel = new Label("DEPARTMENT SUCCESSFULLY ADDED");
			departmentDetailsGrid.setConstraints(departmentSuccessfullyAddedLabel, 0,0);

			// Label for Newly Added Department's ID Number
			Label newlyAddedDepartmentIDNumberLabel = new Label("");
			departmentDetailsGrid.setConstraints(newlyAddedDepartmentIDNumberLabel, 1,1);
			
			// Button for Newly Added Department's ID Number
			Button generateDepartmentIDButton = new Button("Generate Newly Added Department's ID Number");
			generateDepartmentIDButton.setOnAction(e->
			{
				Department mostRecentlyAddedDepartment = new Department(-1);
				mostRecentlyAddedDepartment = departments.getLast();
				newlyAddedDepartmentIDNumberLabel.setText("  The ID of the department you just added is " + mostRecentlyAddedDepartment.getDepartmentId());
				
			});
			departmentDetailsGrid.setConstraints(generateDepartmentIDButton, 0,1);
			
			// Add New Department Button
			Button addNewDepartmentButton = new Button("Add Another Department");
			addNewDepartmentButton.setOnAction(e->
			{
				newlyAddedDepartmentIDNumberLabel.setText("");
				window.setScene(addDepartmentPage);
			});
			departmentDetailsGrid.setConstraints(addNewDepartmentButton, 0,2);
	
			// Return to main page button
			Button departmentDetailsMainPageButton = new Button("Return to Main Page");
			departmentDetailsMainPageButton.setOnAction(e->
			{
				newlyAddedDepartmentIDNumberLabel.setText("");
				window.setScene(mainPage);
			});
			departmentDetailsGrid.setConstraints(departmentDetailsMainPageButton, 1,2);
	
			// Add all objects to Grid Pane
			departmentDetailsGrid.getChildren().addAll(departmentSuccessfullyAddedLabel, generateDepartmentIDButton, departmentDetailsMainPageButton, addNewDepartmentButton, newlyAddedDepartmentIDNumberLabel);
			
			// Create departmentDetails Scene
			departmentDetailsPage = new Scene(departmentDetailsGrid, 750, 750);	
			
			//------------------------------------------------------------------------------------------ Edit Student Page ----------------------------------------------------------------------------------------------------------------------------
			
			// format searchStudentGrid
			GridPane searchStudentGrid = new GridPane();
			searchStudentGrid.setPadding(new Insets(10,10,10,10));
			searchStudentGrid.setVgap(8);
			searchStudentGrid.setVgap(10);
			
			// Create objects for searchStudentPage
			// Enter Student ID Label and Text Field
			Label searchStudentID = new Label("Enter Student ID:  ");
			searchStudentGrid.setConstraints(searchStudentID, 0,0);
			TextField searchStudentIDInput = new TextField();
			searchStudentGrid.setConstraints(searchStudentIDInput, 1,0);
			
			// Search student no match label
			Label searchStudentNoMatchLabel = new Label("");
			searchStudentGrid.setConstraints(searchStudentNoMatchLabel, 2,0);
			
			// Student name label and text field
			Label studentName = new Label("Student Name:  ");
			searchStudentGrid.setConstraints(studentName, 0, 2);
			TextField studentNameInput = new TextField();
			searchStudentGrid.setConstraints(studentNameInput, 1,2);
			
			// Student address label and text field
			Label studentAddress = new Label("Student Address:  ");
			searchStudentGrid.setConstraints(studentAddress, 0, 3);
			TextField studentAddressInput = new TextField();
			searchStudentGrid.setConstraints(studentAddressInput, 1,3);

			// Student city label and text field
			Label studentCity = new Label("Student City:  ");
			searchStudentGrid.setConstraints(studentCity, 0, 4);
			TextField studentCityInput = new TextField();
			searchStudentGrid.setConstraints(studentCityInput, 1,4);

			// Search student state selection
			Label studentStateLabel = new Label("Select State:  ");
			searchStudentGrid.setConstraints(studentStateLabel , 0,5);  
			ChoiceBox<String> searchStudentStateChoiceBox = new ChoiceBox<>();
			searchStudentStateChoiceBox.getItems().addAll(stateArray);
			searchStudentGrid.setConstraints(searchStudentStateChoiceBox, 1,5);
			
			// Search student error label
			Label searchStudentNoBlankSpacesErrorLabel = new Label("  Error: No blank spaces permitted. Input all information.");
			searchStudentGrid.setConstraints(searchStudentNoBlankSpacesErrorLabel, 2,5);
			searchStudentNoBlankSpacesErrorLabel.setVisible(false);
			
			// Update button
			Button updateButton2 = new Button("Update");
			updateButton2.setOnAction(e->
			{
				// Make sure all appropriate information is entered
				boolean blankSpaces = false;
				Student tempValidateIDStudent = new Student(-1);
				if(getChoice(searchStudentStateChoiceBox) == null 
						|| studentCityInput.getText().isEmpty() 
						|| studentAddressInput.getText().isEmpty() 
						|| studentNameInput.getText().isEmpty() 
						|| searchStudentIDInput.getText().isEmpty())
				{
					searchStudentNoBlankSpacesErrorLabel.setVisible(true);
					blankSpaces = true;
				} else
				{
					// User must have input information, therefore insert the ID they selected into the temp student
					tempValidateIDStudent.setStudentId(Integer.parseInt(searchStudentIDInput.getText()));
				}
				
				// Validate the ID
				boolean validId = students.duplicate(tempValidateIDStudent);
				
				if(blankSpaces == false && validId)
				{
					searchStudentNoBlankSpacesErrorLabel.setVisible(false);
					int indexForRemoval = 0;
					int tempStudentID = Integer.parseInt(searchStudentIDInput.getText());
					
					// Create temporary student object to find index of the student that needs to be removed
					Student temp = new Student(tempStudentID);
					indexForRemoval = students.getIndex(temp);
					
					// Remove student from both the linkedlist and database
					deleteStudentRecord(tempStudentID);
					students.remove(indexForRemoval); 
					
					// Add new student to linkedlist and database
					students.add(new Student(tempStudentID, 
							studentNameInput.getText(), 
							studentAddressInput.getText(), 
							studentCityInput.getText(), 
							getChoice(searchStudentStateChoiceBox)));
					
					try 
					{
						createStudentRecord(tempStudentID, 
						studentNameInput.getText(), 
						studentAddressInput.getText(), 
						studentCityInput.getText(), 
						getChoice(searchStudentStateChoiceBox));
						
					} catch(Exception z)
					{
						System.out.println(z);
					}
				
					searchStudentIDInput.setText("");
					studentNameInput.setText("");
					studentAddressInput.setText("");
					studentCityInput.setText("");
					searchStudentStateChoiceBox.getSelectionModel().clearSelection();
					
				} else
				{
					searchStudentNoBlankSpacesErrorLabel.setVisible(true);
				}
					
			});
			searchStudentGrid.setConstraints(updateButton2, 0,6);
		
			// Submit Button
			Button searchStudentSubmitButton = new Button("Submit");
			searchStudentSubmitButton.setOnAction(e->
			{
				studentNameInput.setText("");
				studentAddressInput.setText("");
				studentCityInput.setText("");
				searchStudentStateChoiceBox.getSelectionModel().clearSelection();
				searchStudentNoBlankSpacesErrorLabel.setVisible(false);
				boolean searchIDExists = true;
				boolean studentListIsEmpty = students.isEmpty();
				
				if(searchStudentIDInput.getText().isEmpty())
				{
					searchStudentNoBlankSpacesErrorLabel.setText("  Error: No blank spaces permitted. Input all information.");
					searchStudentNoBlankSpacesErrorLabel.setVisible(true);
					searchIDExists = false;
				} else if (studentListIsEmpty)
				{
					searchStudentNoBlankSpacesErrorLabel.setText("  Error. Student List is Empty.");
					searchStudentNoBlankSpacesErrorLabel.setVisible(true);
				}
				
				if(searchIDExists && studentListIsEmpty != true)
				{
					searchStudentNoBlankSpacesErrorLabel.setVisible(false);
					int searchId = Integer.parseInt(searchStudentIDInput.getText());
					studentNameInput.setText("");
					studentAddressInput.setText("");
					studentCityInput.setText("");
					searchStudentStateChoiceBox.getSelectionModel().clearSelection();
	
					// Create a temporary student object for comparison purposes
					Student temp = new Student(searchId);
					
					// Returns true if ID user entered exists
					boolean validID = students.duplicate(temp);
					
					// Returns index for the ID of the student that the user entered
					int index = students.getIndex(temp);
					
					// temp now holds all desired data
					temp = students.getElement(index);
					studentNameInput.setText(temp.getName());
					studentAddressInput.setText(temp.getAddress());
					studentCityInput.setText(temp.getCity());
					searchStudentStateChoiceBox.setValue(temp.getState());																											
					searchStudentNoMatchLabel.setText("");
				
					if (validID == false)
					{
						studentNameInput.setText("");
						studentAddressInput.setText("");
						studentCityInput.setText("");
						searchStudentStateChoiceBox.getSelectionModel().clearSelection();
						searchStudentNoMatchLabel.setText("  No Match");
					}
					
					validID = true;
		
				} else
				{
					searchStudentNoBlankSpacesErrorLabel.setVisible(true);
				}
	
			});
			searchStudentGrid.setConstraints(searchStudentSubmitButton, 0,1);
			
			// Cancel button
			Button searchStudentCancelButton = new Button("Cancel");
			searchStudentCancelButton.setOnAction(e->
			{
				searchStudentNoBlankSpacesErrorLabel.setVisible(false);
				searchStudentIDInput.clear();
				studentNameInput.clear();
				studentAddressInput.clear();
				studentCityInput.clear();
				searchStudentStateChoiceBox.getSelectionModel().clearSelection();
				studentNameInput.setText("");
				studentAddressInput.setText("");
				studentCityInput.setText("");
				searchStudentNoMatchLabel.setText("");
				window.setScene(mainPage);
				
			});
			searchStudentGrid.setConstraints(searchStudentCancelButton, 1,1);
			
			// Add all objects to Grid Pane
			searchStudentGrid.getChildren().addAll(searchStudentID, searchStudentIDInput, searchStudentSubmitButton, 
					searchStudentCancelButton, studentName, studentNameInput, 
					studentAddress, studentAddressInput, studentCity, 
					studentCityInput, studentStateLabel, searchStudentStateChoiceBox,
					updateButton2, searchStudentNoMatchLabel, searchStudentNoBlankSpacesErrorLabel);
			
			// Create addEnrollmentPage Scene
			searchStudentPage = new Scene(searchStudentGrid, 750, 750);

			// --------------------------------------------------------------------------------------- Edit Course Page ----------------------------------------------------------------------------------------------------------------------------
			
			// format searchCourseGrid
			GridPane searchCourseGrid = new GridPane();
			searchCourseGrid.setPadding(new Insets(10,10,10,10));
			searchCourseGrid.setVgap(8);
			searchCourseGrid.setVgap(10);
			
			// Select course drop down menu
			Label selectCourseLabel = new Label("Select Course:  ");
			searchCourseGrid.setConstraints(selectCourseLabel, 0,0);  
			ChoiceBox<String> selectCourseChoiceBox = new ChoiceBox<>();
			searchCourseGrid.setConstraints(selectCourseChoiceBox, 1,0);
			selectCourseChoiceBox.setOnMouseClicked(e->
			{
				// Clear selectCourseChoiceBox from previous click
				selectCourseChoiceBox.getItems().clear();
				
				boolean courseListIsEmpty = courses.isEmpty();
				Course tempCourse = new Course(-1);
				ArrayList<String> courseNames = new ArrayList();
				
				// Get names of all courses and add to choice box
				if(courseListIsEmpty)
				{
					selectCourseChoiceBox.getItems().add("Please first add course(s)");
				} else
				{
					tempCourse = courses.getFirst();
					for(int i = 0; i < courses.size(); i ++)
					{
						courseNames.add(tempCourse.getCourseName());
						tempCourse = courses.getNext(tempCourse);
					}
					
					for(String i : courseNames)
					{
						selectCourseChoiceBox.getItems().add(i);
					}
				}
			});
			
			// Search course no match label
			Label searchCourseNoMatchLabel = new Label("");
			searchCourseGrid.setConstraints(searchCourseNoMatchLabel, 2,0);
			
			// Course name label and text field
			Label courseName = new Label("Course Name:  ");
			searchCourseGrid.setConstraints(courseName, 0, 2);
			TextField courseNameInput1 = new TextField();
			searchCourseGrid.setConstraints(courseNameInput1, 1,2);
		
			// Department label and text field
			Label departmentName = new Label("Department:  ");
			searchCourseGrid.setConstraints(departmentName, 0, 3); 
			ChoiceBox<String> searchCourseDepartmentChoiceBox = new ChoiceBox<>();
			searchCourseGrid.setConstraints(searchCourseDepartmentChoiceBox, 1, 3);
			
			// Instructor label and text field
			Label instructorName = new Label("Instructor:  ");
			searchCourseGrid.setConstraints(instructorName, 0, 4); 
			ChoiceBox<String> searchCourseInstructorChoiceBox = new ChoiceBox<>();
			searchCourseGrid.setConstraints(searchCourseInstructorChoiceBox, 1,4);
			searchCourseInstructorChoiceBox.setOnMouseClicked(e->
			{
				//clear from previous clicks
				searchCourseInstructorChoiceBox.getItems().clear();
				
				boolean instructorListIsEmpty = instructors.isEmpty();
				Instructor tempInstructor = new Instructor(-1);
				ArrayList<String> instructorNames = new ArrayList();
				
				// Get names of all instructors and add to choice box
				if(instructorListIsEmpty || getChoice(searchCourseDepartmentChoiceBox) == null
						|| getChoice(searchCourseDepartmentChoiceBox) == "Please first create course(s)")
				{
					searchCourseInstructorChoiceBox.getItems().add("Error. Please first select a department or add course(s)");
				} else
				{
					String selectedDepartment = cleanString(getChoice(searchCourseDepartmentChoiceBox));
					tempInstructor = instructors.getFirst();
					for(int i = 0; i < instructors.size(); i ++)
					{
						String tempInstructorDepartment = cleanString(tempInstructor.getDepartment());
						if(tempInstructorDepartment.equals(selectedDepartment))
						{
							instructorNames.add(tempInstructor.getName());
						}
						tempInstructor = instructors.getNext(tempInstructor);
					}
					
					for (String i : instructorNames)
					{
						searchCourseInstructorChoiceBox.getItems().add(i);
						
					}
				}
			});
			
			// Had to move this below instructor choice box event handler
			searchCourseDepartmentChoiceBox.setOnMouseClicked(e->
			{
				// clear any existing choice for both choice boxes
				searchCourseDepartmentChoiceBox.getItems().clear();
				searchCourseInstructorChoiceBox.getItems().clear();
				searchCourseInstructorChoiceBox.getSelectionModel().clearSelection();
				searchCourseInstructorChoiceBox.setValue(null);
				
				boolean departmentListIsEmpty = departments.isEmpty();
				Department tempDepartment = new Department(-1);
				ArrayList<String> departmentNames = new ArrayList();
				
				// Get names of all departments and add to choice box
				if (departmentListIsEmpty)
				{
					searchCourseDepartmentChoiceBox.getItems().add("Please first create course(s)");
				} else
				{
					tempDepartment = departments.getFirst();
					for(int i = 0; i < departments.size(); i ++)
					{
						departmentNames.add(tempDepartment.getName());
						tempDepartment = departments.getNext(tempDepartment);
					}
					
					for(String i : departmentNames)
					{
						searchCourseDepartmentChoiceBox.getItems().add(i);
					}
				}
			});
			
			// Course Number label and text field
			Label courseNumber = new Label("Course Number:  ");
			searchCourseGrid.setConstraints(courseNumber, 0, 5);
			TextField courseNumberInput1 = new TextField();
			searchCourseGrid.setConstraints(courseNumberInput1, 1,5);
			
			// Search course no blank spaces error label
			Label searchCourseNoBlankSpacesErrorLabel = new Label("  Error: No blank spaces permitted. Input all information.");
			searchCourseGrid.setConstraints(searchCourseNoBlankSpacesErrorLabel, 2,5);
			searchCourseNoBlankSpacesErrorLabel.setVisible(false);
		
			// Update button
			Button updateButton3 = new Button("Update");
			updateButton3.setOnAction(e->
			{
				boolean noBlankSpaces = true;
				boolean courseListIsEmpty = courses.isEmpty();
				
				if (courseNumberInput1.getText().isEmpty() 
						|| getChoice(searchCourseDepartmentChoiceBox) == null 
						|| getChoice(searchCourseInstructorChoiceBox) == null 
						|| courseNameInput1.getText().isEmpty()
						|| getChoice(selectCourseChoiceBox) == null
						|| getChoice(selectCourseChoiceBox) == "Please first add course(s)"
						)
					
				{
					searchCourseNoBlankSpacesErrorLabel.setVisible(true);
					noBlankSpaces = false;
				}
				
				if(noBlankSpaces && courseListIsEmpty != true)
				{
					searchCourseNoBlankSpacesErrorLabel.setVisible(false);
					
					// Create temporary course object with same ID as the one the user selected
					Course temp = courses.getFirst();
					int courseId = 0;
					
					while(temp != null)
					{
						if(cleanString(temp.getCourseName()).equals(cleanString(getChoice(selectCourseChoiceBox))))
						{
							courseId = temp.getCourseId();
						}
						
						temp = courses.getNext(temp);
					}
							
					// We need to find these values
					int instructorId = 0;
					int departmentId = 0;
					int indexForRemoval = 0;
					
					Course firstCourse = courses.getFirst();
					while(firstCourse != null)
					{
						// We've found the course in the linked list with the same ID as the one the user entered
						if(firstCourse.getCourseId() == courseId)
						{
							indexForRemoval = courses.getIndex(firstCourse);
							instructorId = firstCourse.getInstructorId();
							departmentId = firstCourse.getDepartmentId();
						}
						// Set first course equal to the next course in line
						firstCourse = courses.getNext(firstCourse);
					}
					
					// Remove course from linked list with index, not courseId
					courses.remove(indexForRemoval);
					
					// Update linked list with replacement course object
					courses.add(new Course(courseId, 
							courseNameInput1.getText(), 
							getChoice(searchCourseInstructorChoiceBox), 
							getChoice(searchCourseDepartmentChoiceBox), 
							Integer.parseInt(courseNumberInput1.getText()),
							instructorId,
							departmentId));
					
					
					// Update database with replacement course object
					try
					{
						int newInstructorId = 0;
						int newDepartmentId = 0;
						
						Instructor tempInstructor = instructors.getFirst();
						Department tempDepartment = departments.getFirst();
						
						while(tempInstructor != null)
						{
							if(tempInstructor.getName() == getChoice(searchCourseInstructorChoiceBox))
							{
								newInstructorId = tempInstructor.getInstructorId();
							}
							tempInstructor = instructors.getNext(tempInstructor);
						}
						
						while(tempDepartment != null)
						{
							if(tempDepartment.getName() == getChoice(searchCourseDepartmentChoiceBox))
							{
								newDepartmentId = tempDepartment.getDepartmentId();
							}
							tempDepartment = departments.getNext(tempDepartment);
						}
						
						updateCourseRecord(courseId, 
								courseNameInput1.getText(), 
								getChoice(searchCourseInstructorChoiceBox), 
								getChoice(searchCourseDepartmentChoiceBox), 
								Integer.parseInt(courseNumberInput1.getText()));
		
					} catch(Exception e2)
					{
						System.out.println(e2);
					}
					
					// Everything has been updated. Let's clean up the user interface.
					selectCourseChoiceBox.getSelectionModel().clearSelection();
					courseNameInput1.setText("");
					searchCourseInstructorChoiceBox.getSelectionModel().clearSelection();
					searchCourseInstructorChoiceBox.setValue("");
					searchCourseDepartmentChoiceBox.getSelectionModel().clearSelection();
					searchCourseDepartmentChoiceBox.setValue("");
					courseNumberInput1.setText("");
				} else
				{
					searchCourseNoBlankSpacesErrorLabel.setVisible(true);
				}
			});
			searchCourseGrid.setConstraints(updateButton3, 0,6);
		
			// Submit Button
			Button searchCourseSubmitButton = new Button("Confirm");
			searchCourseSubmitButton.setOnAction(e->
			{
				courseNameInput1.setText("");
				searchCourseInstructorChoiceBox.getSelectionModel().clearSelection();
				searchCourseDepartmentChoiceBox.getSelectionModel().clearSelection();
				courseNumberInput1.setText("");
				searchCourseNoMatchLabel.setVisible(false);
				searchCourseNoBlankSpacesErrorLabel.setVisible(false);
				boolean searchIDExists = true;
				boolean courseListIsEmpty = courses.isEmpty();
				
				if(getChoice(selectCourseChoiceBox) == null)
				{
					searchCourseNoBlankSpacesErrorLabel.setText("  Error: No blank spaces permitted. Input all information.");
					searchCourseNoBlankSpacesErrorLabel.setVisible(true);
					searchIDExists = false;
				} else if (courseListIsEmpty)
				{
					searchCourseNoBlankSpacesErrorLabel.setText("  Error. Course List is Empty.");
					searchCourseNoBlankSpacesErrorLabel.setVisible(true);
				}
				
				if(searchIDExists && courseListIsEmpty != true)
				{
					searchCourseNoBlankSpacesErrorLabel.setVisible(false);
					// Create temporary course object with same ID as the one the user selected
					Course tempy = courses.getFirst();
					int courseId = 0;
					
					while(tempy != null)
					{
						if(cleanString(tempy.getCourseName()).equals(cleanString(getChoice(selectCourseChoiceBox))))
						{
							courseId = tempy.getCourseId();
						}
						
						tempy = courses.getNext(tempy);
					}
					courseNameInput1.setText("");
					searchCourseInstructorChoiceBox.getSelectionModel().clearSelection();
					searchCourseDepartmentChoiceBox.getSelectionModel().clearSelection();
					courseNumberInput1.setText("");
	
					// Create a temporary course object for comparison purposes
					Course temp = new Course(courseId);
					
					// Returns true if ID user entered exists
					boolean validID = courses.duplicate(temp);
					
					// Returns index for the ID of the course that the user entered
					int index = courses.getIndex(temp);
					
					// Temp now holds all desired data
					temp = courses.getElement(index);
					courseNameInput1.setText(temp.getCourseName());
					searchCourseInstructorChoiceBox.setValue(temp.getInstructor());
					searchCourseDepartmentChoiceBox.setValue(temp.getDepartment());
					courseNumberInput1.setText(String.valueOf(temp.getCourseNumber())); 
					searchCourseNoMatchLabel.setText("");
				
					if (validID == false)
					{
						courseNameInput1.setText("");
						searchCourseInstructorChoiceBox.getSelectionModel().clearSelection();
						searchCourseDepartmentChoiceBox.getSelectionModel().clearSelection();
						searchCourseInstructorChoiceBox.setValue(null);
						searchCourseDepartmentChoiceBox.setValue(null);
						courseNumberInput1.setText("");
						searchCourseNoMatchLabel.setVisible(true);
						searchCourseNoMatchLabel.setText("  No Match");
					}
					
					validID = true;
		
				} else
				{
					searchCourseNoBlankSpacesErrorLabel.setVisible(true);
				}
				
			});
			searchCourseGrid.setConstraints(searchCourseSubmitButton, 0,1);
			
			// Cancel button
			Button searchCourseCancelButton = new Button("Cancel");
			searchCourseCancelButton.setOnAction(e->
			{
				searchCourseNoBlankSpacesErrorLabel.setVisible(false);
				selectCourseChoiceBox.getSelectionModel().clearSelection();
				courseNameInput1.clear();
				courseNumberInput1.clear();
				courseNameInput1.setText("");
				searchCourseInstructorChoiceBox.getSelectionModel().clearSelection();
				searchCourseDepartmentChoiceBox.getSelectionModel().clearSelection();
				searchCourseInstructorChoiceBox.setValue(null);
				searchCourseDepartmentChoiceBox.setValue(null);
				courseNumberInput1.setText("");
				searchCourseNoMatchLabel.setText("");
				window.setScene(mainPage);
			});
			searchCourseGrid.setConstraints(searchCourseCancelButton, 1,1);
			
			// Add all objects to Grid Pane
			searchCourseGrid.getChildren().addAll(selectCourseLabel, selectCourseChoiceBox, searchCourseSubmitButton, 
					searchCourseCancelButton, courseName, courseNameInput1, 
					instructorName, searchCourseInstructorChoiceBox, departmentName, 
					searchCourseDepartmentChoiceBox, courseNumber, courseNumberInput1,
					updateButton3, searchCourseNoMatchLabel, searchCourseNoBlankSpacesErrorLabel);
			
			// Create addEnrollmentPage Scene
			searchCoursePage = new Scene(searchCourseGrid, 750, 750);

			// ------------------------------------------------------------------------------------------ Edit Enrollment Page ----------------------------------------------------------------------------------------------------------------------------
			
			// format searchEnrollmentGrid
			GridPane searchEnrollmentGrid = new GridPane();
			searchEnrollmentGrid.setPadding(new Insets(10,10,10,10));
			searchEnrollmentGrid.setVgap(8);
			searchEnrollmentGrid.setVgap(10);

			// Create objects for searchEnrollmentPage
			// Enter Student ID Label and Text Field
			Label searchEnrollmentStudentID = new Label("Enter Student ID:  ");
			searchEnrollmentGrid.setConstraints(searchEnrollmentStudentID, 0,0);
			TextField searchEnrollmentStudentIDInput = new TextField();
			searchEnrollmentGrid.setConstraints(searchEnrollmentStudentIDInput, 1,0);

			// Enter Course ID Label and Text Field
			Label searchEnrollmentCourseID = new Label("Enter Course ID:  ");
			searchEnrollmentGrid.setConstraints(searchEnrollmentCourseID, 0,1);
			TextField searchEnrollmentCourseIDInput = new TextField();
			searchEnrollmentGrid.setConstraints(searchEnrollmentCourseIDInput, 1,1);
			
			// Search course no match label
			Label searchEnrollmentErrorLabel = new Label("");
			searchEnrollmentGrid.setConstraints(searchEnrollmentErrorLabel, 2,1);
			
			// Year label and text field
			Label searchEnrollmentYearLabel = new Label("Year:  ");
			searchEnrollmentGrid.setConstraints(searchEnrollmentYearLabel, 0, 3);
			TextField searchEnrollmentYearInput = new TextField();
			searchEnrollmentGrid.setConstraints(searchEnrollmentYearInput, 1,3);
			
			// Semester label and text field
			Label searchEnrollmentSemesterLabel = new Label("Semester:  ");
			searchEnrollmentGrid.setConstraints(searchEnrollmentSemesterLabel, 0, 4);
			TextField searchEnrollmentSemesterInput = new TextField();
			searchEnrollmentGrid.setConstraints(searchEnrollmentSemesterInput, 1,4);

			// Grade Label and Enter Grade Choice Box
			Label searchEnrollmentSelectGradeLabel = new Label("Select Grade:  ");
			searchEnrollmentGrid.setConstraints(searchEnrollmentSelectGradeLabel, 0,5);
			ChoiceBox<String> searchEnrollmentSelectGradeChoiceBox = new ChoiceBox<>();
			searchEnrollmentSelectGradeChoiceBox.getItems().addAll(gradeArray);
			searchEnrollmentGrid.setConstraints(searchEnrollmentSelectGradeChoiceBox, 1, 5);
			
			// Update button
			Button updateButton4 = new Button("Update");
			updateButton4.setOnAction(e->
			{
				if(searchEnrollmentStudentID.getText().isEmpty()
						|| searchEnrollmentCourseIDInput.getText().isEmpty()
						|| searchEnrollmentYearInput.getText().isEmpty()
						|| getChoice(searchEnrollmentSelectGradeChoiceBox) == null
						|| searchEnrollmentSemesterInput.getText().isEmpty())
				{
					searchEnrollmentErrorLabel.setText("  Error: Input all information");
				} else
				{
					int tempStudentID = Integer.parseInt(searchEnrollmentStudentIDInput.getText());
					int tempCourseID = Integer.parseInt(searchEnrollmentCourseIDInput.getText());
					int tempYear = Integer.parseInt(searchEnrollmentYearInput.getText());
					String tempGrade = (getChoice(searchEnrollmentSelectGradeChoiceBox));
					String tempSemester = (searchEnrollmentSemesterInput.getText());
					int indexForRemoval = 0;
					for(Enrollment e4 : enrollments)
					{
						if(tempStudentID == e4.getStudentId() && tempCourseID == e4.getCourseId())
						{
							indexForRemoval = enrollments.indexOf(e4);
						}
					}
						
					enrollments.remove(indexForRemoval); 
					enrollments.add(new Enrollment(tempStudentID, tempCourseID, 
							tempYear, tempSemester, tempGrade));
					searchEnrollmentStudentIDInput.setText("");
					searchEnrollmentCourseIDInput.setText("");
					searchEnrollmentYearInput.setText("");
					searchEnrollmentSemesterInput.setText("");
					searchEnrollmentSelectGradeChoiceBox.getSelectionModel().clearSelection();
					
					// Update enrollments file
					try
					{
						updateEnrollmentRecord(tempStudentID, tempCourseID, 
								tempYear, tempSemester, tempGrade);
					} catch(Exception e2)
					{
						System.out.println("Something went wrong");
					}	
				}
			});
			searchCourseGrid.setConstraints(updateButton4, 0, 6);
		
			// Submit Button
			Button searchEnrollmentSubmitButton = new Button("Submit");
			searchEnrollmentSubmitButton.setOnAction(e->
			{
				searchEnrollmentErrorLabel.setText("");
				int searchStudentId = Integer.parseInt(searchEnrollmentStudentIDInput.getText());
				int searchCourseId = Integer.parseInt(searchEnrollmentCourseIDInput.getText());
				searchEnrollmentYearInput.setText("");
				searchEnrollmentSemesterInput.setText("");
				searchEnrollmentSelectGradeChoiceBox.getSelectionModel().clearSelection();
				boolean bothValidIDs = false;
				
				for (Enrollment e1 : enrollments)
				{
					if(e1.getStudentId() == searchStudentId && e1.getCourseId() == searchCourseId)
					{
						bothValidIDs = true;
						searchEnrollmentStudentIDInput.setText(String.valueOf(e1.getStudentId())); 
						searchEnrollmentCourseIDInput.setText(String.valueOf(e1.getCourseId()));
						searchEnrollmentYearInput.setText(String.valueOf(e1.getYear()));
						searchEnrollmentSemesterInput.setText(e1.getSemester());
						searchEnrollmentSelectGradeChoiceBox.setValue(e1.getGrade());
					}
				}
			
				if(bothValidIDs == false)
				{
					searchEnrollmentErrorLabel.setText("  No Match");
				}
				
				bothValidIDs = false;
	
			});
			searchEnrollmentGrid.setConstraints(searchEnrollmentSubmitButton, 0,2);
			
			// Cancel button
			Button searchEnrollmentCancelButton = new Button("Cancel");
			searchEnrollmentCancelButton.setOnAction(e->
			{
				searchEnrollmentStudentIDInput.clear();
				searchEnrollmentCourseIDInput.clear();
				searchEnrollmentYearInput.clear();
				searchEnrollmentSemesterInput.clear();
				searchEnrollmentSelectGradeChoiceBox.getSelectionModel().clearSelection();
				searchEnrollmentErrorLabel.setVisible(false);
				window.setScene(mainPage);
			});
			searchEnrollmentGrid.setConstraints(searchEnrollmentCancelButton, 1,2);
			
			// Add all objects to Grid Pane
			searchEnrollmentGrid.getChildren().addAll(searchEnrollmentStudentID, searchEnrollmentStudentIDInput, searchEnrollmentCourseID, 
					searchEnrollmentCourseIDInput, searchEnrollmentSubmitButton, searchEnrollmentCancelButton, 
					searchEnrollmentYearLabel, searchEnrollmentYearInput, searchEnrollmentSemesterLabel, 
					searchEnrollmentSemesterInput, searchEnrollmentSelectGradeLabel, searchEnrollmentSelectGradeChoiceBox, 
					updateButton4, searchEnrollmentErrorLabel);
			
			// Create searchEnrollmentPage Scene
			searchEnrollmentPage = new Scene(searchEnrollmentGrid, 750, 750);

			// -------------------------------------------------------------------------------------------------- searchInstructorPage ----------------------------------------------------------------------------
			
			// format searchInstructorGrid
			GridPane searchInstructorGrid = new GridPane();
			searchInstructorGrid.setPadding(new Insets(10,10,10,10));
			searchInstructorGrid.setVgap(8);
			searchInstructorGrid.setVgap(10);
			
			// Create objects for searchInstructorPage
			
			// Select instructor label
			Label selectInstructorLabel = new Label("SELECT AN INSTRUCTOR   ");
			searchInstructorGrid.setConstraints(selectInstructorLabel,0,0);
			// Select Instructor drop down menu
			ChoiceBox<String> selectInstructorChoiceBox = new ChoiceBox<>();
			searchInstructorGrid.setConstraints(selectInstructorChoiceBox, 1,0);
		
			// Results Label
			Label resultsLabel = new Label("");
			searchInstructorGrid.setConstraints(resultsLabel, 0,6);
			resultsLabel.setVisible(false);
			
			selectInstructorChoiceBox.setOnMouseClicked(e->
			{
				// Make results label invisible in case of multiple iterations
				resultsLabel.setVisible(false);
				
				// Clear instructorChoiceBox from previous click
				selectInstructorChoiceBox.getItems().clear();
				
				boolean instructorListIsEmpty = instructors.isEmpty();
				Instructor tempInstructor = new Instructor(-1);
				ArrayList<String> instructorNames = new ArrayList();
				
				// Get names of all instructors and add to choice box
				if(instructorListIsEmpty)
				{
					selectInstructorChoiceBox.getItems().add("Error. Please first add instructor(s).");
				} else
				{
					tempInstructor = instructors.getFirst();
					for(int i = 0; i < instructors.size(); i ++)
					{
						instructorNames.add(tempInstructor.getName());
						tempInstructor = instructors.getNext(tempInstructor);
					}
					
					for (String i : instructorNames)
					{
						selectInstructorChoiceBox.getItems().add(i);
						
					}
				}
			});
			
			// New name and new name Text Box
			Label changeNameLabel = new Label("Change Name To:  ");
			searchInstructorGrid.setConstraints(changeNameLabel, 0,1);
			TextField changeNameInput = new TextField();
			searchInstructorGrid.setConstraints(changeNameInput, 1,1);
		
			// Edit instructor Cancel Button
			Button searchInstructorCancelButton = new Button("Exit");
			searchInstructorCancelButton.setOnAction(e->
			{
				selectInstructorChoiceBox.getSelectionModel().clearSelection();
				changeNameInput.clear();
				resultsLabel.setText("");
				resultsLabel.setVisible(false);
				window.setScene(mainPage);
			});
			searchInstructorGrid.setConstraints(searchInstructorCancelButton, 1,5);
			
			// Search Instructor Submit button
			Button searchInstructorSubmitButton = new Button("Submit");
			searchInstructorSubmitButton.setOnAction(e->
			{
				if(changeNameInput.getText().isEmpty() || getChoice(selectInstructorChoiceBox) == null ||
						getChoice(selectInstructorChoiceBox) == "Error. Please first add instructor(s).")
				{
					resultsLabel.setText("Error. Please make selections and enter new value for name.");
					resultsLabel.setVisible(true);
				} else
				{
					String selectedInstructor = cleanString(getChoice(selectInstructorChoiceBox));
					int instructorIdForDeletion = 0;
					
					//create temporary instructor object for iterating
					Instructor tempInstructor = instructors.getFirst();
					while(tempInstructor != null)
					{
						if(tempInstructor.getName() == getChoice(selectInstructorChoiceBox))
						{
							instructorIdForDeletion = tempInstructor.getInstructorId();
						}
						tempInstructor = instructors.getNext(tempInstructor);
					}
					
					//Re-define temporary instructor object for iterating again
					tempInstructor = instructors.getFirst();
					for(int i = 0; i < instructors.size(); i ++)
					{
						if(cleanString(tempInstructor.getName()).equals(selectedInstructor))
						{
							tempInstructor.setName(changeNameInput.getText());
							try
							{
								updateInstructorName(instructorIdForDeletion, changeNameInput.getText());
							} catch(Exception q)
							{
								System.out.println(q);
							}
							resultsLabel.setText("Instructor Name Changed Successfully");
							resultsLabel.setVisible(true);
						}
						tempInstructor = instructors.getNext(tempInstructor);
					}
																																				
					// boolean to make sure courses list has data
					boolean courseListIsEmpty = courses.isEmpty();
					
					if(courseListIsEmpty == false)
					{
						// temp course object for iterative purposes
						Course tempCourse = courses.getFirst();
				
						for(int i = 0; i < courses.size(); i++)
						{
							
							if(cleanString(tempCourse.getInstructor()).equals(selectedInstructor))
							{
								tempCourse.setInstructor(changeNameInput.getText());
																																								
							}
							tempCourse = courses.getNext(tempCourse);
						}	
					}
				}
				selectInstructorChoiceBox.getSelectionModel().clearSelection();
				changeNameInput.clear();
			});		
			searchInstructorGrid.setConstraints(searchInstructorSubmitButton, 0,5);
			
			// Add all objects to Grid Pane
			searchInstructorGrid.getChildren().addAll(selectInstructorLabel, selectInstructorChoiceBox, searchInstructorCancelButton, searchInstructorSubmitButton,
					changeNameLabel, changeNameInput, resultsLabel);
			
			// Create searchInstructor Scene
			searchInstructorPage = new Scene(searchInstructorGrid, 750, 750);
			
			// ---------------------------------------------------------------------------------------- searchDepartment Page -----------------------------------------------------------------------
			
			// format searchDepartmentGrid
			GridPane searchDepartmentGrid = new GridPane();
			searchDepartmentGrid.setPadding(new Insets(10,10,10,10));
			searchDepartmentGrid.setVgap(8);
			searchDepartmentGrid.setVgap(10);
			
			// Create objects for searchDepartmentPage
			
			// Select department label
			Label selectDepartmentLabel = new Label("SELECT A DEPARTMENT   ");
			searchDepartmentGrid.setConstraints(selectDepartmentLabel,0,0);
			// Select Department drop down menu
			ChoiceBox<String> selectDepartmentChoiceBox = new ChoiceBox<>();
			searchDepartmentGrid.setConstraints(selectDepartmentChoiceBox, 1,0);
		
			// Results Label
			Label searchDepartmentResultsLabel = new Label("");
			searchDepartmentGrid.setConstraints(searchDepartmentResultsLabel, 0,6);
			searchDepartmentResultsLabel.setVisible(false);
			
			selectDepartmentChoiceBox.setOnMouseClicked(e->
			{
				// Make searchDepartmentResults label invisible in case of multiple iterations
				searchDepartmentResultsLabel.setVisible(false);
				
				// Clear selectDepartmentChoiceBox from previous click
				selectDepartmentChoiceBox.getItems().clear();
				
				boolean departmentListIsEmpty = departments.isEmpty();
				Department tempDepartment = new Department(-1);
				ArrayList<String> departmentNames = new ArrayList();
				
				// Get names of all department and add to choice box
				if(departmentListIsEmpty)
				{
					selectDepartmentChoiceBox.getItems().add("Error. Please first add department(s).");
				} else
				{
					tempDepartment = departments.getFirst();
					for(int i = 0; i < departments.size(); i ++)
					{
						departmentNames.add(tempDepartment.getName());
						tempDepartment = departments.getNext(tempDepartment);
					}
					
					for (String i : departmentNames)
					{
						selectDepartmentChoiceBox.getItems().add(i);
						
					}
				}
			});
			
			// New name and new name Text Box
			Label changeDepartmentNameLabel = new Label("Change Name To:  ");
			searchDepartmentGrid.setConstraints(changeDepartmentNameLabel, 0,1);
			TextField changeDepartmentNameInput = new TextField();
			searchDepartmentGrid.setConstraints(changeDepartmentNameInput, 1,1);
		
			// Edit department Cancel Button
			Button searchDepartmentCancelButton = new Button("Exit");
			searchDepartmentCancelButton.setOnAction(e->
			{
				selectDepartmentChoiceBox.getSelectionModel().clearSelection();
				changeDepartmentNameInput.clear();
				searchDepartmentResultsLabel.setText("");
				searchDepartmentResultsLabel.setVisible(false);
				window.setScene(mainPage);
			});
			searchDepartmentGrid.setConstraints(searchDepartmentCancelButton, 1,5);
			
			// Search Department Submit button
			Button searchDepartmentSubmitButton = new Button("Submit");
			searchDepartmentSubmitButton.setOnAction(e->
			{
				if(changeDepartmentNameInput.getText().isEmpty() || getChoice(selectDepartmentChoiceBox) == null ||
						getChoice(selectDepartmentChoiceBox) == "Error. Please first add department(s).")
				{
					searchDepartmentResultsLabel.setText("Error. Please make selections and enter new value for name.");
					searchDepartmentResultsLabel.setVisible(true);
				} else
				{
					String selectedDepartment = cleanString(getChoice(selectDepartmentChoiceBox));
					int departmentIdForDeletion = 0;
					
					//create temporary department object for iterating
					Department tempDepartment = departments.getFirst();
					while(tempDepartment != null)
					{
						if(tempDepartment.getName() == getChoice(selectDepartmentChoiceBox))
						{
							departmentIdForDeletion = tempDepartment.getDepartmentId();
						}
						tempDepartment = departments.getNext(tempDepartment);
					}
					
					//Re-define temporary department object for iterating again
					tempDepartment = departments.getFirst();
					for(int i = 0; i < departments.size(); i ++)
					{
						if(cleanString(tempDepartment.getName()).equals(selectedDepartment))
						{
							tempDepartment.setName(changeDepartmentNameInput.getText());
							try
							{
								updateDepartmentName(departmentIdForDeletion, changeDepartmentNameInput.getText());
							} catch(Exception q)
							{
								System.out.println(q);
							}
							searchDepartmentResultsLabel.setText("Department Name Changed Successfully");
							searchDepartmentResultsLabel.setVisible(true);
						}
						tempDepartment = departments.getNext(tempDepartment);
					}
																																				
					// boolean to make sure courses list has data
					boolean courseListIsEmpty = courses.isEmpty();
					
					if(courseListIsEmpty == false)
					{
						// temp course object for iterative purposes
						Course tempCourse = courses.getFirst();
				
						for(int i = 0; i < courses.size(); i++)
						{
							
							if(cleanString(tempCourse.getDepartment()).equals(selectedDepartment))
							{
								tempCourse.setDepartment(changeDepartmentNameInput.getText());
																																								
							}
							tempCourse = courses.getNext(tempCourse);
						}	
					}
				}
				selectDepartmentChoiceBox.getSelectionModel().clearSelection();
				changeDepartmentNameInput.clear();
			});		
			searchDepartmentGrid.setConstraints(searchDepartmentSubmitButton, 0,5);
			
			// Add all objects to Grid Pane
			searchDepartmentGrid.getChildren().addAll(selectDepartmentLabel, selectDepartmentChoiceBox, searchDepartmentCancelButton, searchDepartmentSubmitButton,
					changeDepartmentNameLabel, changeDepartmentNameInput, searchDepartmentResultsLabel);
			
			// Create searchDepartment Scene
			searchDepartmentPage = new Scene(searchDepartmentGrid, 750, 750);
			
			// ---------------------------------------------------------------------------------------- gradeManagement Page -----------------------------------------------------------------------
			
			// format manageGradesGrid
			GridPane manageGradesGrid = new GridPane();
			manageGradesGrid.setPadding(new Insets(10,10,10,10));
			manageGradesGrid.setVgap(8);
			manageGradesGrid.setVgap(10);
			
			// Search student ID label and text field
			Label manageGradesSearchStudentIDLabel = new Label("Enter Student ID:  ");
			manageGradesGrid.setConstraints(manageGradesSearchStudentIDLabel, 0,0);
			TextField manageGradesStudentIDInput = new TextField();
			manageGradesGrid.setConstraints(manageGradesStudentIDInput, 1,0);
			
			// Get grades no blank spaces error label
			Label getGradesNoBlankSpacesErrorLabel = new Label("  Error: No blank spaces permitted. Input all information.");
			manageGradesGrid.setConstraints(getGradesNoBlankSpacesErrorLabel, 2,0);
			getGradesNoBlankSpacesErrorLabel.setVisible(false);
			
			// Must be integer error label
			Label mustBeIntegerErrorLabel = new Label("  Error: Must enter integer.");
			manageGradesGrid.setConstraints(mustBeIntegerErrorLabel, 2,0);
			mustBeIntegerErrorLabel.setVisible(false);
			
			// Get grades No Match label
			Label getGradesErrorLabel = new Label("  No Match.");
			manageGradesGrid.setConstraints(getGradesErrorLabel, 2,0);
			getGradesErrorLabel.setVisible(false);
			
			// Display student grades label
			Label displayStudentGradesLabel = new Label("");
			manageGradesGrid.setConstraints(displayStudentGradesLabel, 0,3);
			
			// Enter Course ID label and text field
			Label manageGradesEnterCourseIDLabel = new Label("Enter Course ID:  ");
			manageGradesGrid.setConstraints(manageGradesEnterCourseIDLabel, 0,4);
			TextField manageGradesEnterCourseIDInput = new TextField();
			manageGradesGrid.setConstraints(manageGradesEnterCourseIDInput, 1,4);
			
			// Select New Grade and Select New Grade Choice Box
			Label selectNewGradeLabel = new Label("Select Grade:  ");
			manageGradesGrid.setConstraints(selectNewGradeLabel, 0,5);
			ChoiceBox<String> selectNewGradeChoiceBox = new ChoiceBox<>();
			selectNewGradeChoiceBox.getItems().addAll(gradeArray);
			manageGradesGrid.setConstraints(selectNewGradeChoiceBox, 1, 5);
		
			// Edit Grade Button
			Button editGradeButton = new Button("Edit Grade");
			manageGradesGrid.setConstraints(editGradeButton, 0,6);
			editGradeButton.setOnAction(e->
			{
				boolean blankSpaces = false;
				
				if(manageGradesEnterCourseIDInput.getText().isEmpty()
						|| manageGradesStudentIDInput.getText().isEmpty()
						|| getChoice(selectNewGradeChoiceBox) == null)
				{
					blankSpaces = true;
					getGradesErrorLabel.setText("  Error: Blank Spaces");
					getGradesErrorLabel.setVisible(true);
				} else
				{
					getGradesNoBlankSpacesErrorLabel.setVisible(false);
					mustBeIntegerErrorLabel.setVisible(false);
					getGradesErrorLabel.setVisible(false);
					int tempCourseID = Integer.parseInt(manageGradesEnterCourseIDInput.getText());
					int tempStudentID = Integer.parseInt(manageGradesStudentIDInput.getText());
					String tempGrade = (getChoice(selectNewGradeChoiceBox));
					int indexForRemoval = 0;
					int tempYear = 0;
					String tempSemester = "";
					boolean valid = false;
					
					for(Enrollment e7: enrollments)
					{
						if(tempCourseID == e7.getCourseId() && tempStudentID == e7.getStudentId())
						{
							valid = true;
							indexForRemoval = enrollments.indexOf(e7);
							tempYear = e7.getYear();
							tempSemester = e7.getSemester();
						}
					}
					
					if(valid)
					{
						enrollments.remove(indexForRemoval); 
						enrollments.add(new Enrollment(tempStudentID, tempCourseID, 
								tempYear, tempSemester, tempGrade));
						// Create file output stream
						try
						{
							updateEnrollmentRecord(tempStudentID, tempCourseID, 
								tempYear, tempSemester, tempGrade);
						} catch(Exception e2)
						{
							System.out.println("Something went wrong");
						}	
						displayStudentGradesLabel.setText("");
						manageGradesStudentIDInput.clear();
						selectNewGradeChoiceBox.getSelectionModel().clearSelection();
						manageGradesEnterCourseIDInput.clear();
					}
			
				}
			});
			
			// Get grades Button
			Button getGradesButton = new Button("Get Grades");
			manageGradesGrid.setConstraints(getGradesButton, 0,1);
			getGradesButton.setOnAction(e->
			{
				boolean noBlankSpaces = true;
				boolean isInt = true;
				boolean noMatch = false;
				boolean studentListIsEmpty = students.isEmpty();
				getGradesErrorLabel.setVisible(false);
				displayStudentGradesLabel.setText("");
				if(manageGradesStudentIDInput.getText().isEmpty())
				{
					mustBeIntegerErrorLabel.setVisible(false);
					getGradesNoBlankSpacesErrorLabel.setVisible(true);
					noBlankSpaces = false;
				}
				
				if(isInt(manageGradesStudentIDInput) == false && manageGradesStudentIDInput.getText().isEmpty() != true)
				{
					isInt = false;
					getGradesNoBlankSpacesErrorLabel.setVisible(false);
					mustBeIntegerErrorLabel.setVisible(true);
				}
				
				if(noBlankSpaces && isInt && (studentListIsEmpty == false))
				{
					getGradesNoBlankSpacesErrorLabel.setVisible(false);
					mustBeIntegerErrorLabel.setVisible(false);
					int tempStudentID = Integer.parseInt(manageGradesStudentIDInput.getText());
					int tempCourseID = 0;
					String tempStudentName = "";
					String tempCourseName = "";
					ArrayList<Grade> grades = new ArrayList<Grade>();
					
					// Generate temporary student object
					Student temp = new Student(tempStudentID);
					
					// Returns if student ID is valid
					boolean validStudentID = students.duplicate(temp);
					
					// Get index of desired element
					int index = students.getIndex(temp);
					
					// Get name of desired student
					temp = students.getElement(index);
					tempStudentName = temp.getName();
		
					if(validStudentID)
					{
						for(Enrollment e6 : enrollments)
						{
							if(tempStudentID == e6.getStudentId())
							{
								tempCourseID = e6.getCourseId();
								
								// Generate temporary course object
								Course tempCourse = new Course(tempCourseID);
								
								// Get index of desired course
								int courseIndex = courses.getIndex(tempCourse);
								tempCourse = courses.getElement(courseIndex);
								tempCourseName = tempCourse.getCourseName();
								
								grades.add(new Grade(tempStudentID, tempCourseID,  tempStudentName,  tempCourseName));
								String existingText = displayStudentGradesLabel.getText();
								displayStudentGradesLabel.setText(existingText + tempStudentName + " has a " + e6.getGrade() + " in " + tempCourseName 
										+ " \n(Course ID: " + e6.getCourseId() + ", Year: " + e6.getYear() +", Semester: " + e6.getSemester()+")\n");
							}
						}
						if (grades.isEmpty() == true)
						{
							displayStudentGradesLabel.setText("Student does not have any enrollments.");
						}
						grades.clear();
					} else
					{
						noMatch = true;
						getGradesErrorLabel.setVisible(true);
					}
				}
			});

			// Cancel button
			Button manageGradesGridCancelButton = new Button("Cancel");
			manageGradesGrid.setConstraints(manageGradesGridCancelButton, 1,1);
			manageGradesGridCancelButton.setOnAction(e->
			{
				displayStudentGradesLabel.setText("");
				manageGradesStudentIDInput.clear();
				getGradesNoBlankSpacesErrorLabel.setVisible(false);
				mustBeIntegerErrorLabel.setVisible(false);
				getGradesErrorLabel.setVisible(false);
				window.setScene(mainPage);
			});
			
			// Add all objects to Grid Pane
			manageGradesGrid.getChildren().addAll(manageGradesSearchStudentIDLabel, manageGradesStudentIDInput, getGradesButton, 
					manageGradesGridCancelButton, getGradesNoBlankSpacesErrorLabel, displayStudentGradesLabel,
					mustBeIntegerErrorLabel, getGradesErrorLabel, editGradeButton,
					manageGradesEnterCourseIDLabel, manageGradesEnterCourseIDInput, selectNewGradeLabel,
					selectNewGradeChoiceBox);
			
			// Create manageGradesPage Scene
			manageGradesPage = new Scene(manageGradesGrid, 950, 950);
		
			//--------------------------------------------------------------------------------------------- reportPage ----------------------------------------------------------------------------------------------------------------------------
			
			// format manageGradesGrid
			GridPane reportGrid = new GridPane();
			reportGrid.setPadding(new Insets(10,10,10,10));
			reportGrid.setVgap(8);
			reportGrid.setVgap(10);
			
			// Report Label
			Label reportLabel = new Label();
			reportGrid.setConstraints(reportLabel, 0,1);
		
			// Generate report button
			Button generateReportButton = new Button("Generate Report");
			reportGrid.setConstraints(generateReportButton, 0,0);
			generateReportButton.setOnAction(e->
			{
				reportLabel.setText("");
				
				// Add data to report label
				if (enrollments.isEmpty() == true)
				{
					reportLabel.setText("Error.  No data to generate report.  Please first create enrollments.");
				} else
				{
					int studentId = 0;
					int courseId = 0;
					String reportStudentName = "ERROR";
					String reportCourseName = "ERROR";
					for(Enrollment e8: enrollments)
					{
						studentId = e8.getStudentId();
						courseId = e8.getCourseId();
						Student tempStudent = students.getFirst();
						Course tempCourse = courses.getFirst();
						
						
						for (int i = 0; i < students.size(); i++)
						{
							if(tempStudent.getStudentId() == studentId)
							{
								reportStudentName = tempStudent.getName();
							}
							
							tempStudent = students.getNext(tempStudent);
						}
						
						for (int i = 0; i < courses.size(); i++)
						{
							if(tempCourse.getCourseId() == courseId)
							{
								reportCourseName = tempCourse.getCourseName();
							}
							
							tempCourse = courses.getNext(tempCourse);
							
						}
						
						String existingText = reportLabel.getText();
						reportLabel.setText(existingText + reportStudentName + 
								" has a " + e8.getGrade() + " in " + reportCourseName + "\n");
					}
				}
			});

			// Return to Main Page Button
			Button returnToMainPageButton = new Button("Return to Main Page");
			reportGrid.setConstraints(returnToMainPageButton, 0,2);
			returnToMainPageButton.setOnAction(e->
			{
				reportLabel.setText("");
				window.setScene(mainPage);
			});
			
			// Add all objects to Grid Pane
			reportGrid.getChildren().addAll(reportLabel, returnToMainPageButton ,generateReportButton);
			
			// Create reportPage Scene
			reportPage = new Scene(reportGrid, 950, 950);
	
			// --------------------------------------------------------------------------------- INITIAL BOOTUP ---------------------------------------------------------------------------------------------------
			
			window.setScene(mainPage);
			window.setTitle("Student Database Management System");
			window.show();

		} catch(Exception e) 
		{
			e.printStackTrace();
		}
	}

	// ---------------------------------------------------------------------------------------- FUNCTION DEFINITIONS ----------------------------------------------------------------------------------------------------
	
	
	// ---------------------------------------------------------------------------------------- DATABASE FUNCTIONS ------------------------------------------------------------------------------------------------
	
	public static void deleteStudentRecord(int studentId)
	{
		try
		{
			Connection myConn = getConnection();
			Statement myStmt = myConn.createStatement();
			ResultSet myRs = myStmt.executeQuery("SELECT Name, address, city, state FROM universityenrollment.students WHERE studentId = " + studentId);
			Boolean studentRecordExists = myRs.next();
			if(studentRecordExists)
			{
				PreparedStatement deleteStudentRecordStmt = myConn.prepareStatement("DELETE FROM universityenrollment.students WHERE studentId = " + studentId);
				deleteStudentRecordStmt.executeUpdate();
			} 
		} catch(Exception e)
		{
			System.out.println(e);
		} 
	}
	
	
	public static void deleteCourseRecord(int courseId)
	{
		try
		{
			Connection myConn = getConnection();
			Statement myStmt = myConn.createStatement();
			ResultSet myRs = myStmt.executeQuery("SELECT * FROM universityenrollment.courses WHERE courseId = " + courseId);
			Boolean courseRecordExists = myRs.next();
			if(courseRecordExists)
			{
				PreparedStatement deleteCourseRecordStmt = myConn.prepareStatement("DELETE FROM universityenrollment.courses WHERE courseId = " + courseId);
				deleteCourseRecordStmt.executeUpdate();
			}
		} catch(Exception e)
		{
			System.out.println(e);
		} 
	}
	
	
	public static void deleteInstructorRecord(int instructorId)
	{
		try
		{
			Connection myConn = getConnection();
			Statement myStmt = myConn.createStatement();
			ResultSet myRs = myStmt.executeQuery("SELECT * FROM universityenrollment.instructors WHERE instructorId = " + instructorId);
			Boolean instructorRecordExists = myRs.next();
			if(instructorRecordExists)
			{
				PreparedStatement deleteInstructorRecordStmt = myConn.prepareStatement("DELETE FROM universityenrollment.instructors WHERE instructorId = " + instructorId);
				deleteInstructorRecordStmt.executeUpdate();
			} 
		} catch(Exception e)
		{
			System.out.println(e);
		} 
	}
	
	
	public static void createStudentRecord(int studentId, String name, String address, String city, String state) throws Exception
	{
		
		try
		{
			Connection con = getConnection();
			PreparedStatement createRowStmt = con.prepareStatement("INSERT INTO universityenrollment.students(studentId, Name, address, city, state) VALUES(" + studentId + ",'" + name + "','" + address + "','" + city + "','" + state + "')");
			createRowStmt.executeUpdate();
			
		} catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	
	public static void updateInstructorName(int instructorId, String newName) throws Exception
	{
		
		try
		{
			Connection con = getConnection();
			PreparedStatement updateNameStmt = con.prepareStatement("UPDATE universityenrollment.instructors SET name =" +"'" + newName +"'" + " WHERE instructorId =" + instructorId);
			updateNameStmt.executeUpdate();
			PreparedStatement updateCoursesAsWellStmt = con.prepareStatement("UPDATE universityenrollment.courses SET instructor =" +"'" + newName +"'" + " WHERE instructorId =" + instructorId);
			updateCoursesAsWellStmt.executeUpdate();
			
		} catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	
	public static void updateDepartmentName(int departmentId, String newName) throws Exception
	{
		
		try
		{
			Connection con = getConnection();
			PreparedStatement updateNameStmt = con.prepareStatement("UPDATE universityenrollment.departments SET name =" +"'" + newName +"'" + " WHERE departmentId =" + departmentId);
			updateNameStmt.executeUpdate();
			PreparedStatement updateCoursesAsWellStmt = con.prepareStatement("UPDATE universityenrollment.courses SET department =" +"'" + newName +"'" + " WHERE departmentId =" + departmentId);
			updateCoursesAsWellStmt.executeUpdate();
			PreparedStatement updateInstructorsAsWellStmt = con.prepareStatement("UPDATE universityenrollment.instructors SET department =" +"'" + newName +"'" + " WHERE departmentId =" + departmentId);
			updateInstructorsAsWellStmt.executeUpdate();
			
		} catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	
	public static void updateEnrollmentRecord(int studentId, int courseId, int year, String semester, String grade) throws Exception
	{
		try
		{
			Connection con = getConnection();
			PreparedStatement updateYearStmt = con.prepareStatement("UPDATE universityenrollment.enrollments SET year =" + year + " WHERE StudentId = "+ studentId +" AND courseId = "+courseId);
			updateYearStmt.executeUpdate();
			PreparedStatement updateSemesterStmt = con.prepareStatement("UPDATE universityenrollment.enrollments SET semester ='" + semester + "' WHERE StudentId = "+ studentId +" AND courseId = "+ courseId);
			updateSemesterStmt.executeUpdate();
			PreparedStatement updateGradeStmt = con.prepareStatement("UPDATE universityenrollment.enrollments SET grade ='" + grade + "' WHERE StudentId = "+ studentId +" AND courseId = "+ courseId);
			updateGradeStmt.executeUpdate();
			
		} catch(Exception e)
		{
			System.out.println(e);
		}
	}

	public static void updateCourseRecord(int courseId, String courseName, String instructor, String department, int courseNumber) throws Exception
	{
		try
		{
			Connection con = getConnection();
			
			PreparedStatement updateCourseNameStmt = con.prepareStatement("UPDATE universityenrollment.courses SET courseName ='" + courseName + "' WHERE courseId = "+ courseId);
			updateCourseNameStmt.executeUpdate();
			
			PreparedStatement updateInstructorNameStmt = con.prepareStatement("UPDATE universityenrollment.courses SET instructor ='" + instructor + "' WHERE courseId = "+ courseId);
			updateInstructorNameStmt.executeUpdate();
			
			PreparedStatement updateDepartmentNameStmt = con.prepareStatement("UPDATE universityenrollment.courses SET department ='" + department + "' WHERE courseId = "+ courseId);
			updateDepartmentNameStmt.executeUpdate();
			
			PreparedStatement updateCourseNumberStmt = con.prepareStatement("UPDATE universityenrollment.courses SET courseNumber =" + courseNumber + " WHERE courseId = "+ courseId);
			updateCourseNumberStmt.executeUpdate();
		
		} catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	
	public static void createDepartmentRecord(int departmentId, String name) throws Exception
	{
		
		try
		{
			Connection myConn = getConnection();
			CallableStatement myStmt = myConn.prepareCall("{CALL universityenrollment.create_dept(?, ?)}");
			myStmt.setInt(1, departmentId);
			myStmt.setString(2, name);
			myStmt.execute();
		} catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	
	public static void createInstructorRecord(int instructorId, String name, String department, int departmentId) throws Exception
	{
		try
		{
			Connection con = getConnection();
			PreparedStatement createRowStmt = con.prepareStatement("INSERT INTO universityenrollment.instructors(instructorId, name, department, departmentId) VALUES(" + instructorId + ",'" + name + "','" + department + "'," + departmentId + ")");
			createRowStmt.executeUpdate();
		} catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	
	public static void createCourseRecord(int courseId, String courseName, String instructor, String department, int courseNumber, int instructorId, int departmentId) throws Exception
	{
		
		try
		{
			Connection con = getConnection();
			PreparedStatement createRowStmt = con.prepareStatement("INSERT INTO universityenrollment.courses(courseId, courseName, instructor, department, courseNumber, instructorId, departmentId) VALUES(" + courseId + ",'" + courseName + "','" + instructor + "','" + department + "'," + courseNumber + "," + instructorId + "," + departmentId + ")");
			createRowStmt.executeUpdate();
			
		} catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	
	public static void createEnrollmentRecord(int studentId, int courseId, int year, String semester, String grade) throws Exception
	{
		
		try
		{
			Connection con = getConnection();
			PreparedStatement createRowStmt = con.prepareStatement("INSERT INTO universityenrollment.enrollments(studentId, courseId, year, semester, grade) VALUES(" + studentId + "," + courseId + "," + year + ",'" + semester + "','" + grade + "')");
			createRowStmt.executeUpdate();
			
		} catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	
	public static Boolean tableHasData(String tableName)
	{
		try
		{
			Connection myConn = getConnection();
			Statement myStmt = myConn.createStatement();
			ResultSet myRs = myStmt.executeQuery("SELECT * FROM universityenrollment." +tableName);
			return myRs.next();
		} catch(Exception e)
		{
			System.out.println(e);
			return false;
		}
	}
	
	// ------------------------------------------------------------------------------------- END DATABASE FUNCTIONS -------------------------------------------------------------------------------------------
	
	// Clean string function
	private String cleanString(String providedString)
	{
		return providedString.toLowerCase().replaceAll(" ", "");
	}
	
	// Close program function
	private void closeProgram() 
	{
		window.close();
	}
	
	// Valid int function
	private boolean isInt(TextField input)
	{
		try 
		{
			int number = Integer.parseInt(input.getText());
			return true;
		} catch(NumberFormatException e)
		{
			return false;
		}
	}
	
	// Write to file function. Performs on bootup of program.
	static <E> void writeToFile(File file, ArrayList<E> arrayList) throws IOException, ClassNotFoundException
	{
		FileInputStream fi = new FileInputStream(file);
		ObjectInputStream input = new ObjectInputStream(fi);
		try
		{
			while (true)
			{
				E e = (E)input.readObject();
				arrayList.add(e);
			}
		} catch (EOFException ex){}
		input.close();
	}
	
	// Update file function
	static <E> void updateFile(File file, ArrayList<E> arrayList) throws IOException, ClassNotFoundException
	{
		try 
		{
			// Create file output stream
			FileOutputStream fo = new FileOutputStream(file);
			ObjectOutputStream output = new ObjectOutputStream(fo);

			// Serialize
			for(E e : arrayList) 
			{
				output.writeObject(e);
			}
			output.close();
			fo.close();
		} catch(Exception e1)
		{
			System.out.println("Something went wrong");
		}
	
	}
	
	// Get choice from choicebox function
	private String getChoice(ChoiceBox<String> choiceBox)
	{
		String choice = choiceBox.getValue();
		return choice;
	}
}

//Class Definitions
class LinkedList<T extends Comparable<T>>
{
	// LinkedList fields (first & last)
	private Node first = null;
	private Node last = null;

	private class Node
	{
		T data;
		Node next;
		public Node(T data) 
		{
			this.data = data;
		}
	}  // Node class closing bracket
	
	// LinkedListConstructor
	public LinkedList() 
	{
		this.first = null;
		this.last = null;
	}  //LinkedList constructor closing bracket

	// isEmpty method
	public boolean isEmpty()
	{
		return first == null;
	}

	// Returns list size
	public int size()
	{
		int count = 0;
		Node p = first;
		while(p != null)
		{
			count ++;
			p = p.next;
		}
		return count;
	}
	
	// Adds element to end of the list
	public void add (T data)
	{
		if (isEmpty())
		{
			first = new Node(data);
			last = first;
		}
		else
		{
			last.next = new Node(data);
			last = last.next;
		}
	}

	 // To string method
	 public String toString()
	 {
		StringBuilder strBuilder = new StringBuilder();
		Node n = first;
		while(n != null)
		{
			strBuilder.append(n.data + "\n");
			n = n.next;
		}
		return strBuilder.toString();
	}
	
	 // alphabetize/sort method
	 public void sort()
	 {
		 Node current = first;
		 Node index = null;
		 
		 T temp;
		 
		 // Does nothing if list is empty
		 if (first == null)
		 {
			 return;
		 } else
		 {
			 while(current != null)
			 {
				 // Node index will point to current's next node
				 index = current.next;
				 
				 while (index != null)
				 {
					 // If current node's data is greater than index's, swap the data between them
					 if(current.data.compareTo(index.data) == 1)
					 {
						temp = current.data;
						current.data = index.data;
						index.data = temp;
					 }
					 index = index.next; 
				 }
				 current = current.next; 
			 }
		 } 
	 }
	 
	 // Remove node using index
	 public String remove(int index)
	 {
		 if (index < 0 || index >= size())
		 {
			 String message = String.valueOf(index);
			 throw new IndexOutOfBoundsException(message);
		 }
		 
		 String element;
		 if(index == 0)
		 {
			 element = first.data.toString();
			 first = first.next;
			 if (first == null)
			 {
				 last = null;
			 }
		 } else
		 {
			 Node pred = first;
			 
			 for (int k = 1; k <= index -1; k++)
			 {
				 pred = pred.next;
			 }
			 
			 element = pred.next.data.toString();
			 
			 pred.next = pred.next.next;
			 
			 if(pred.next == null)
			 {
				 last = pred;
			 }
		 }
		 return element; 
	 }
	 
	 // Add node into specific position
	 void add(int index, T data)
	 {
		if (index > size()) 
		{
			System.out.println("Index is out of bounds");
	         return;
	    }
		if (index == 0) 
		{
			Node currentNode = first;

	        first = new Node(data);;
	        first.next = currentNode;
	        return;
	     }
		
		 Node um = new Node(data);
	     Node temp = first;
	     Node prev = new Node(null);

	     while (index > 0)
	     {
	         prev = temp;
	         temp = temp.next;
	         index--;
	     }
	     prev.next = um;
	     prev.next.next = temp;
	 } // Add method closing bracket
	 
	 // Get element by using index
	 public T getElement(int index)
	 {
		 if (index < 0 || index >= size())
		 {
			 String message = String.valueOf(index);
			 throw new IndexOutOfBoundsException(message);
		 }
		 
		 T element;
		 if(index == 0)
		 {
			 element = first.data;
		 } else
		 {
			 Node current = first;
			 for (int k = 1; k <= index; k++)
			 {
				 current = current.next;
			 }
			 
			 element = current.data;
		 }
		 return element;
	 }
	 
	 // Update file method
	public void updateFile(File file) // throws IOException, ClassNotFoundException
	{
		try 
		{
			// Create file output stream
			FileOutputStream fo = new FileOutputStream(file);
			ObjectOutputStream output = new ObjectOutputStream(fo);
			
			Node n = first;
			while(n != null)
			{
				output.writeObject(n.data);
				n = n.next;
			}
			
			output.close();
			fo.close();
		} catch(Exception e1)
		{
			System.out.println("Something went wrong");
		}
	}
	
	// Update linkedList method
	public void updateList(File file) throws IOException, ClassNotFoundException
	{
		FileInputStream fi = new FileInputStream(file);
		ObjectInputStream input = new ObjectInputStream(fi);
		try
		{
			while (true)
			{
				T e = (T)input.readObject();
				this.add(e);
			}
		} catch (EOFException ex){}
		input.close();
	}
	
	// Get index with ID
	public int getIndex(T data)
	{	 
		 
		int count = 0;
		int index = 0;
		Node p = first;
		Node fakeNode = new Node(data);
		while(p != null)
		{
			if (p.data.compareTo(fakeNode.data) == 0)
			{
				index = count;
			}
			p = p.next;
			count++;
		}
		 return index; 
	}
	
	// Check for duplicate
	public boolean duplicate(T data)
	{
		boolean duplicate = false;
		Node p = first;
		Node fakeNode = new Node(data);
		
		while(p != null)
		{
			if (p.data.compareTo(fakeNode.data) == 0)
			{
				duplicate = true;
			}
			p = p.next;
		}
		return duplicate;
	}
	
	// Returns last element
	public T getLast()
	{
		Node prev = first;
		
		if(prev.next != null)
		{
			Node currentNode = prev.next;
			
			while(currentNode != null)
			{
				currentNode = currentNode.next;
				prev = prev.next;
			}
			
			return prev.data;
		} else
			return prev.data;
	}
	
	// Returns first element
	public T getFirst()
	{
		Node node = first;
		return node.data;
	}
	
	// Returns next element
	public T getNext(T data)
	{
		Node prev = first;
		
		while(prev.data != data && prev != null)
		{
			prev = prev.next;
		}
		
		if(prev.data == data && prev.next != null)
		{
			return prev.next.data;
		} else if (prev.data == data && prev.next == null)
		{
			return null;
		} else
			return null;
	}
	
}  // LinkedList class closing bracket

//Student Definition
class Student implements Serializable, Comparable<Student>
{
	private int studentId;
	private String Name;
	private String address;
	private String city;
	private String state;
		
	public Student(int studentId, String name, String address, String city, String state)
	{
		this.studentId = studentId;
		this.Name = name;
		this.address = address;
		this.city = city;
		this.state = state;
	}
	
	public Student(int studentId)
	{
		this(studentId, null, null, null, null);
	}
	
	public int getStudentId()
	{
		return this.studentId;
	}
	
	public int setStudentId(int studentId)
	{
		return (this.studentId = studentId);
	}
	
	public String getName()
	{
		return this.Name;
	}
	
	public String setName(String newName) 
	{
		return (this.Name = newName);
	}
	
	public String getAddress()
	{
		return this.address;
	}
	
	public String setAddress(String newAddress) 
	{
		return (this.address = newAddress);
	}
	
	public String getCity()
	{
		return this.city;
	}
	
	public String setCity(String newCity) 
	{
		return (this.city = newCity);
	}
	
	public String getState()
	{
		return this.state;
	}
	
	public String setState(String newState) 
	{
		return (this.state = newState);
	}
	
	public String toString() 
	{
		return studentId + " " + Name + " " + address + " " + city + " " + state;
	}
	
	@Override
	public int compareTo(Student other) 
	{
		if (this.studentId == other.studentId)
		{
			return 0;
		} else if(this.studentId > other.studentId)
		{
			return 1; // larger student ID
		} else
		{
			return -1; // smaller student ID
		}
	}
}  // Student class closing bracket

//Course definition
class Course implements Serializable, Comparable<Course>
{

	private int courseId;
	private String courseName;
	private String instructor;
	private String department;
	private int courseNumber;
	private int instructorId;
	private int departmentId;
	
	public Course(int courseId, String courseName, String instructor, String department, int courseNumber, int instructorId, int departmentId)
	{
		this.courseId = courseId;
		this.courseName = courseName;
		this.instructor = instructor;
		this.department = department;
		this.courseNumber = courseNumber;
		this.instructorId = instructorId;
		this.departmentId = departmentId;
	}
	
	public Course(int courseId)
	{
		this(courseId, null, null, null, 0, 0, 0);
	}
	
	public int getCourseId()
	{
		return this.courseId;
	}
	
	public int setCourseId(int newCourseId) 
	{
		return (this.courseId = newCourseId);
	}
	
	public String getCourseName()
	{
		return this.courseName;
	}
	
	public String setCourseName(String newCourseName) 
	{
		return (this.courseName = newCourseName);
	}
	
	public String getInstructor()
	{
		return this.instructor;
	}
	
	public String setInstructor(String newInstructor) 
	{
		return (this.instructor = newInstructor);
	}
	
	public String getDepartment()
	{
		return this.department;
	}
	
	public String setDepartment(String newDepartment) 
	{
		return (this.department = newDepartment);
	}
	
	public int getCourseNumber()
	{
		return this.courseNumber;
	}
	
	public int setCourseNumber(int newCourseNumber) 
	{
		return (this.courseNumber = newCourseNumber);
	}
	
	public int getInstructorId()
	{
		return this.instructorId;
	}
	
	public int setInstructorId(int newInstructorId) 
	{
		return (this.instructorId = newInstructorId);
	}
	
	public int getDepartmentId()
	{
		return this.departmentId;
	}
	
	public int setDepartmentId(int newDepartmentId) 
	{
		return (this.departmentId = newDepartmentId);
	}
	
	public String toString() 
	{
		return courseId + " " + courseName + " " + instructor + " " + department + " " + courseNumber;
	}
	
	@Override
	public int compareTo(Course other) 
	{
		if (this.courseId == other.courseId)
		{
			return 0;
		} else if(this.courseId > other.courseId)
		{
			return 1; // larger course ID
		} else
		{
			return -1; // smaller course ID
		}
	}
}


//Enrollment Definition
class Enrollment implements Serializable
{
	private int studentId;
	private int courseId;
	private int year;
	private String semester;
	private String grade;
	
	Enrollment(int studentId, int courseId, int year, String semester, String grade)
	{
		this.studentId = studentId;
		this.courseId = courseId;
		this.year = year;
		this.semester = semester;
		this.grade = grade;
	}
	
	public int getStudentId()
	{
		return this.studentId;
	}
	
	public int setStudentId(int newStudentId) 
	{
		return (this.studentId = newStudentId);
	}
	
	public int getCourseId()
	{
		return this.courseId;
	}
	
	public int setCourseId(int newCourseId) 
	{
		return (this.courseId = newCourseId);
	}
	
	public int getYear()
	{
		return this.year;
	}
	
	public int setYear(int newYear) 
	{
		return (this.year = newYear);
	}
	
	public String getSemester()
	{
		return this.semester;
	}
	
	public String setSemester(String newSemester) 
	{
		return (this.semester = newSemester);
	}
	
	public String getGrade()
	{
		return this.grade;
	}
	
	public String setGrade(String newGrade) 
	{
		return (this.grade = newGrade);
	}
	
	public String toString() 
	{
		return studentId + " " + courseId + " " + year + " " + semester + " " + grade;
	}
}

// Grade definition
class Grade
{
	private int studentId;
	private int courseId;
	private String studentName;
	private String courseName;

	Grade(int studentId, int courseId, String studentName, String courseName)
	{
		this.studentId = studentId;
		this.courseId = courseId;
		this.studentName = studentName;
		this.courseName = courseName;
	}
	
	public int getStudentId()
	{
		return this.studentId;
	}
	
	public int setStudentId(int newStudentId) 
	{
		return (this.studentId = newStudentId);
	}
	
	public int getCourseId()
	{
		return this.courseId;
	}
	
	public int setCourseId(int newCourseId) 
	{
		return (this.courseId = newCourseId);
	}
	
	public String getStudentName()
	{
		return this.studentName;
	}
	
	public String setStudentName(String newStudentName) 
	{
		return (this.studentName = newStudentName);
	}
	
	public String getCourseName()
	{
		return this.courseName;
	}
	
	public String setCourseName(String newCourseName) 
	{
		return (this.courseName = newCourseName);
	}
}

// Instructor class definition
class Instructor implements Serializable, Comparable<Instructor>
{
	private int instructorId;
	private String name;
	private String department;
	private int departmentId;
		
	public Instructor(int instructorId, String name, String department, int departmentId)
	{
		this.instructorId = instructorId;
		this.name = name;
		this.department = department;
		this.departmentId = departmentId;
	}
	
	public Instructor(int instructorId)
	{
		this(instructorId, null, null, -1);
	}
	
	public int getInstructorId() 
	{
		return instructorId;
	}

	public void setInstructorId(int instructorId) 
	{
		this.instructorId = instructorId;
	}
	
	public int getDeparmentId() 
	{
		return departmentId;
	}

	public void setDepartmentId(int departmentId) 
	{
		this.departmentId = departmentId;
	}
	
	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getDepartment() 
	{
		return department;
	}

	public void setDepartment(String department) 
	{
		this.department = department;
	}
	
	@Override
	public String toString() 
	{
		return "Instructor [instructorId=" + instructorId + ", name=" + name + ", department=" + department + "]";
	}

	@Override
	public int compareTo(Instructor other) 
	{
		if (this.instructorId == other.instructorId)
		{
			return 0;
		} else if(this.instructorId > other.instructorId)
		{
			return 1; // larger instructor ID
		} else
		{
			return -1; // smaller instructor ID
		}
	}
}  // Instructor class closing bracket


//Department class definition
class Department implements Serializable, Comparable<Department>
{
	private int departmentId;
	private String name;
		
	public Department(int departmentId, String name)
	{
		this.departmentId = departmentId;
		this.name = name;
	}
	
	public Department(int departmentId)
	{
		this(departmentId, null);
	}
	
	public int getDepartmentId() 
	{
		return departmentId;
	}

	public void setDepartmentId(int departmentId) 
	{
		this.departmentId = departmentId;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}
	
	@Override
	public String toString() 
	{
		return "Department [departmentId=" + departmentId + ", name=" + name + "]";
	}

	@Override
	public int compareTo(Department other) 
	{
		if (this.departmentId == other.departmentId)
		{
			return 0;
		} else if(this.departmentId > other.departmentId)
		{
			return 1; // larger department ID
		} else
		{
			return -1; // smaller department ID
		}
	}
}  // Department class closing bracket