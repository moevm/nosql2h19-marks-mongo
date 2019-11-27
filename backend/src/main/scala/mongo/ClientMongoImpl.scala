package mongo

import data._
import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import tethys._
import tethys.derivation.semiauto._
import tethys.jackson._

import scala.concurrent.{ExecutionContext, Future}

class ClientMongoImpl(implicit ec: ExecutionContext) extends ClientMongo {

  import ClientMongoImpl._

  private val mongoClient: MongoClient = MongoClient("mongodb://localhost")

  private val database = mongoClient.getDatabase("test2")

  val students: MongoCollection[Document] = database.getCollection("students")
  val courses: MongoCollection[Document] = database.getCollection("courses")
  val faculties: MongoCollection[Document] = database.getCollection("faculties")
  val departments: MongoCollection[Document] = database.getCollection("departments")
  val groups: MongoCollection[Document] = database.getCollection("groups")
  val semesters: MongoCollection[Document] = database.getCollection("semesters")

  override def addStudent(student: Student): Future[String] = {
    students.insertOne(Document(student.asJson)).toFuture().map(_ => s"Student was added successfully")
  }

  override def getStudents: Future[Seq[Option[Student]]] = {
    students.find().toFuture().map(_.map(stud => stud.toJson().jsonAs[Student] match {
      case Right(student) => Some(student)
      case _ => None
    }))
  }

  override def addCourse(course: Course): Future[String] = {
    courses.insertOne(Document(course.asJson)).toFuture().map(_ => s"Course was added succesfully")
  }

  override def getCourses: Future[Seq[Option[Course]]] = {
    courses.find().toFuture().map(_.map(_.toJson().jsonAs[Course] match {
      case Right(course) => Some(course)
      case _ => None
    }))
  }

  override def getCourseByName(courseName: String): Future[Option[Course]] = {
    courses.find(equal("name", courseName)).first().toFutureOption().map(_.flatMap(_.toJson().jsonAs[Course] match {
      case Right(course) => Some(course)
      case _ => None
    }))
  }

  override def addFaculty(faculty: Faculty): Future[String] = {
    faculties.insertOne(Document(faculty.asJson)).toFuture().map(_ => s"Faculty was added succesfully")
  }

  override def getFaculties: Future[Seq[Option[Faculty]]] = {
    faculties.find().toFuture().map(_.map(_.toJson().jsonAs[Faculty] match {
      case Right(faculty) => Some(faculty)
      case _ => None
    }))
  }

  override def getFacultyByName(facultyName: String): Future[Option[Faculty]] = {
    faculties.find(equal("name", facultyName)).first().toFutureOption().map(_.flatMap(_.toJson().jsonAs[Faculty] match {
      case Right(faculty) => Some(faculty)
      case _ => None
    }))
  }

  override def addDepartment(department: Department): Future[String] = {
    departments.insertOne(Document(department.asJson)).toFuture().map(_ => s"Department was added succesfully")
  }

  override def getDepartments: Future[Seq[Option[Department]]] = {
    departments.find().toFuture().map(_.map(_.toJson().jsonAs[Department] match {
      case Right(department) => Some(department)
      case _ => None
    }))
  }

  override def getDepartmentByName(departmentName: String): Future[Option[Department]] = {
    departments.find(equal("name", departmentName)).first().toFutureOption().map(_.flatMap(_.toJson().jsonAs[Department] match {
      case Right(department) => Some(department)
      case _ => None
    }))
  }

  override def addGroup(group: Group): Future[String] = {
    groups.insertOne(Document(group.asJson)).toFuture().map(_ => s"Group was added succesfully")
  }

  override def getGroups: Future[Seq[Option[Group]]] = {
    groups.find().toFuture().map(_.map(_.toJson().jsonAs[Group] match {
      case Right(group) => Some(group)
      case _ => None
    }))
  }

  override def getGroupByNumber(number: Int): Future[Option[Group]] = {
    groups.find(equal("number", number)).first().toFutureOption().map(_.flatMap(_.toJson().jsonAs[Group] match {
      case Right(group) => Some(group)
      case _ => None
    }))
  }

  override def addSemester(semester: Semester): Future[String] = {
    semesters.insertOne(Document(semester.asJson)).toFuture().map(_ => s"Semester was added succesfully")
  }

  override def getSemesters: Future[Seq[Option[Semester]]] = {
    semesters.find().toFuture().map(_.map(_.toJson().jsonAs[Semester] match {
      case Right(semester) => Some(semester)
      case _ => None
    }))
  }

  override def getSemester(year: Int, period: String): Future[Option[Semester]] = {
    semesters.find(and(equal("year", year), equal("period", period))).first().toFuture().map {
      case null => None
      case sem => sem.toJson().jsonAs[Semester] match {
        case Right(semester) => Some(semester)
        case _ => None
      }
    }
  }

  override def addMark(studentId: String, mark: Mark): Future[String] = {
    students.updateOne(equal("id", studentId), addToSet("marks", Document(mark.asJson))).toFuture().map(_ =>
      s"Mark was added successfully")
  }

  override def facultyMarks(facultyName: String): Future[Set[FacultyMarkStatistic]] = for {
    groups <- facultyGroups(facultyName)
    students <- Future.sequence(groups.map(group => groupStudents(group.number))).map(_.flatten.toSet)
    marks = students.flatMap(_.marks.map(_.mark))
    statistic = marks.map(mark => {
      val boysCount = students.filter(_.sex).foldLeft(0)((acc, student) => acc + student.marks.count(_.mark == mark))
      val girlsCount = students.filterNot(_.sex).foldLeft(0)((acc, student) => acc + student.marks.count(_.mark == mark))
      FacultyMarkStatistic(mark, boysCount, girlsCount, boysCount + girlsCount)
    })
  } yield statistic

  override def semesterMarks(year: Int, period: String): Future[Set[FacultyMarkStatistic]] = for {
    students <- getStudents.map(_.flatten)
    marks = students.flatMap(_.marks.map(_.mark)).toSet
    statistic = marks.map(mark =>{
      val boysCount = students.filter(_.sex).foldLeft(0)((acc, student) =>
        acc + student.marks.count(mrk => mrk.mark == mark && mrk.semester.year == year && mrk.semester.period == period))
      val girlsCount = students.filterNot(_.sex).foldLeft(0)((acc, student) =>
        acc + student.marks.count(mrk => mrk.mark == mark && mrk.semester.year == year && mrk.semester.period == period))
      FacultyMarkStatistic(mark, boysCount, girlsCount, boysCount + girlsCount)
    })
  } yield statistic

  private def facultyGroups(facultyName: String): Future[Seq[Group]] = {
    groups.find(equal("nameFaculty", facultyName)).toFuture().map(_.flatMap(_.toJson().jsonAs[Group] match {
      case Right(group) => Some(group)
      case _ => None
    }))
  }
  private def groupStudents(groupNumber: Int): Future[Seq[Student]] = for {
    allStudents <- getStudents.map(_.flatten)
    groupStudents = allStudents.filter(_.groups.contains(groupNumber))
  } yield groupStudents

  private def facultyAverage(facultyName: String): Future[Double] = for {
    groups <- facultyGroups(facultyName)
    students <- Future.sequence(groups.map(group => groupStudents(group.number))).map(_.flatten.toSet)
    (sum, n) = students.foldLeft((0, 0))((acc, student) => (acc._1 + student.marks.foldLeft(0)(_ + _.mark), acc._2 + student.marks.length))
  } yield sum.toDouble / n

  override def facultiesAverage: Future[Seq[FacultyAverage]] = for {
    faculties <- getFaculties.map(_.flatten)
    averages <- Future.sequence(faculties.map(faculty => facultyAverage(faculty.name).map(FacultyAverage(faculty.name, _))))
  } yield averages

  def groupsAverage(facultyName: String): Future[Seq[GroupAverage]] = for {
    groups <- facultyGroups(facultyName).map(_.map(_.number))
    students <- getStudents.map(_.flatten)
    groupsAverage = groups.map(group => {
      val groupStudents = students.filter(_.groups.contains(group))
      val (sum, n) = groupStudents.foldLeft((0, 0))((acc, student) => (acc._1 + student.marks.foldLeft(0)(_ + _.mark), acc._2 + student.marks.length))
      GroupAverage(group, sum.toDouble / n)
    })
  } yield groupsAverage
}

object ClientMongoImpl {
  implicit val semesterWriter: JsonObjectWriter[Semester] = jsonWriter[Semester]
  implicit val semesterReader: JsonReader[Semester] = jsonReader[Semester]

  implicit val markWriter: JsonObjectWriter[Mark] = jsonWriter[Mark]
  implicit val markReader: JsonReader[Mark] = jsonReader[Mark]

  implicit val studentWriter: JsonObjectWriter[Student] = jsonWriter[Student]
  implicit val studentReader: JsonReader[Student] = jsonReader[Student]

  implicit val courseWriter: JsonObjectWriter[Course] = jsonWriter[Course]
  implicit val courseReader: JsonReader[Course] = jsonReader[Course]

  implicit val facultyWriter: JsonObjectWriter[Faculty] = jsonWriter[Faculty]
  implicit val facultyReader: JsonReader[Faculty] = jsonReader[Faculty]

  implicit val departmentWriter: JsonObjectWriter[Department] = jsonWriter[Department]
  implicit val departmentReader: JsonReader[Department] = jsonReader[Department]

  implicit val groupWriter: JsonObjectWriter[Group] = jsonWriter[Group]
  implicit val groupReader: JsonReader[Group] = jsonReader[Group]
}