package mongo

import data.{Course, Department, Faculty, FacultyAverage, FacultyMarkStatistic, Group, GroupAverage, Mark, Semester, Student}

import scala.concurrent.Future

trait ClientMongo {
  def addStudent(student: Student): Future[String]

  def getStudents: Future[Seq[Option[Student]]]

  def addCourse(course: Course): Future[String]

  def getCourses: Future[Seq[Option[Course]]]

  def getCourseByName(courseName: String): Future[Option[Course]]

  def addFaculty(faculty: Faculty): Future[String]

  def getFacultyByName(facultyName: String): Future[Option[Faculty]]

  def getFaculties: Future[Seq[Option[Faculty]]]

  def addDepartment(department: Department): Future[String]

  def getDepartments: Future[Seq[Option[Department]]]

  def getDepartmentByName(departmentName: String): Future[Option[Department]]

  def addGroup(group: Group): Future[String]

  def getGroups: Future[Seq[Option[Group]]]

  def getGroupByNumber(number: Int): Future[Option[Group]]

  def addMark(studentId: String, mark: Mark): Future[String]

  def addSemester(semester: Semester): Future[String]

  def getSemesters: Future[Seq[Option[Semester]]]

  def getSemester(year: Int, period: String): Future[Option[Semester]]

  def facultyMarks(facultyName: String): Future[Set[FacultyMarkStatistic]]

  def semesterMarks(year: Int, period: String): Future[Set[FacultyMarkStatistic]]

  def facultiesAverage: Future[Seq[FacultyAverage]]

  def groupsAverage(facultyName: String): Future[Seq[GroupAverage]]
}
