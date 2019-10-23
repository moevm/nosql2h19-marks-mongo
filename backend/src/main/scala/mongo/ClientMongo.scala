package mongo

import data.{Course, CourseTeacher, Department, Faculty, Student, Teacher}

import scala.concurrent.Future

trait ClientMongo {
  def addStudent(student: Student): Future[String]

  def getStudents: Future[Seq[Option[Student]]]

  def addCourse(course: Course): Future[String]

  def getCourses: Future[Seq[Option[Course]]]

  def addFaculty(faculty: Faculty): Future[String]

  def getFaculties: Future[Seq[Option[Faculty]]]

  def addDepartment(department: Department): Future[String]

  def getDepartments: Future[Seq[Option[Department]]]

  def addTeacher(teacher: Teacher): Future[String]

  def getTeachers: Future[Seq[Option[Teacher]]]

  def addCourseTeacher(courseTeacher: CourseTeacher): Future[String]

  def getCourseTeachers: Future[Seq[Option[CourseTeacher]]]
}
