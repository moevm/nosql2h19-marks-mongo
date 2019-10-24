package mongo

import data.{Course, CourseTeacher, Department, Faculty, Group, Student, Teacher}

import scala.concurrent.Future

trait ClientMongo {
  def addStudent(student: Student): Future[String]

  def getStudents: Future[Seq[Option[Student]]]

  def addCourse(course: Course): Future[String]

  def getCourses: Future[Seq[Option[Course]]]

  def getCourseById(courseId: String): Future[Option[Course]]

  def addFaculty(faculty: Faculty): Future[String]

  def getFacultyByName(facultyName: String): Future[Option[Faculty]]

  def getFaculties: Future[Seq[Option[Faculty]]]

  def addDepartment(department: Department): Future[String]

  def getDepartments: Future[Seq[Option[Department]]]

  def getDepartmentByName(departmentName: String): Future[Option[Department]]

  def addTeacher(teacher: Teacher): Future[String]

  def getTeachers: Future[Seq[Option[Teacher]]]

  def getTeacherById(id: String): Future[Option[Teacher]]

  def addCourseTeacher(courseTeacher: CourseTeacher): Future[String]

  def getCourseTeachers: Future[Seq[Option[CourseTeacher]]]

  def addGroup(group: Group): Future[String]

  def getGroups: Future[Seq[Option[Group]]]

  def getGroupByNumber(number: Int): Future[Option[Group]]
}
