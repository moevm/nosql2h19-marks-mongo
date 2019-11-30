package mongo

import data._
import org.mongodb.scala._
import org.mongodb.scala.model.Accumulators.{avg, sum}
import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Updates.addToSet
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

  override def facultyMarks(facultyName: String): Future[Set[FacultyMarkStatistic]] =
    students.aggregate(Seq(
      lookup("groups", "group", "number", "group_full"),
      filter(equal("group_full.nameFaculty", facultyName)),
      unwind("$marks"),
      group(Map("mark" -> "$marks.mark", "sex"->"$sex"), sum("count", 1)),
      project(fields(computed("mark", "$_id.mark"), computed("sex", "$_id.sex"), include("count")))
    )).toFuture().map(a => {
      println(a)
      a
    }).map(_.flatMap(_.toJson().jsonAs[MarkCount] match {
      case Right(markCount) => Some(markCount)
      case _ => None
    }).toSet).map(marksCountToFacultyMarkStatistic)

  override def semesterMarks(year: Int, period: String): Future[Set[FacultyMarkStatistic]] =
    students.aggregate(Seq(
      filter(and(equal("marks.semester.year", year), equal("marks.semester.period", period))),
      unwind("$marks"),
      group(Map("mark" -> "$marks.mark", "sex" -> "$sex"), sum("count", 1)),
      project(fields(computed("mark", "$_id.mark"), computed("sex", "$_id.sex"), include("count")))
    )).toFuture().map(a => {
      println(a)
      a
    }).map(_.flatMap(_.toJson().jsonAs[MarkCount] match {
      case Right(markCount) => Some(markCount)
      case _ => None
    }).toSet).map(marksCountToFacultyMarkStatistic)


  private def marksCountToFacultyMarkStatistic(marksCount: Set[MarkCount]) = {
    marksCount.map(markCount => FacultyMarkStatistic(
      mark = markCount.mark,
      boys = marksCount.find(mark => mark.mark == markCount.mark && mark.sex).map(_.count).getOrElse(0),
      girls = marksCount.find(mark => mark.mark == markCount.mark && !mark.sex).map(_.count).getOrElse(0),
      total = 0
    ))
  }

  override def facultyGroups(facultyName: String): Future[Seq[Group]] = {
    groups.find(equal("nameFaculty", facultyName)).toFuture().map(_.flatMap(_.toJson().jsonAs[Group] match {
      case Right(group) => Some(group)
      case _ => None
    }))
  }

  override def groupStudents(groupNumber: Int): Future[Seq[Student]] =
    students.find(equal("groups", groupNumber)).toFuture().map(_.flatMap(_.toJson().jsonAs[Student] match {
      case Right(student) => Some(student)
      case _ => None
    }))

  override def facultiesAverage: Future[Seq[FacultyAverage]] =
    students.aggregate(Seq(
      lookup("groups", "group", "number", "group_full"),
      unwind("$marks"),
      unwind("$group_full"),
      group("$group_full.nameFaculty", avg("average", "$marks.mark")),
      project(fields(computed("faculty", "$_id"), include("average")))
    )).toFuture().map(_.flatMap(_.toJson().jsonAs[FacultyAverage] match {
      case Right(facultyAverage) => Some(facultyAverage)
      case _ => None
    }))

  def groupsAverage(facultyName: String): Future[Seq[GroupAverage]] =
    students.aggregate(Seq(
      unwind("$marks"),
      group("$group", avg("average", "$marks.mark")),
      project(fields(computed("group", "$_id"), include("average")))
    )).toFuture().map(_.flatMap(_.toJson().jsonAs[GroupAverage] match {
      case Right(groupAverage) => Some(groupAverage)
      case _ => None
    }))
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

  implicit val groupAverageReader: JsonReader[GroupAverage] = jsonReader[GroupAverage]
  implicit val groupAverageWriter: JsonObjectWriter[GroupAverage] = jsonWriter[GroupAverage]

  implicit val facultyAverageReader: JsonReader[FacultyAverage] = jsonReader[FacultyAverage]
  implicit val facultyAverageWriter: JsonObjectWriter[FacultyAverage] = jsonWriter[FacultyAverage]

  implicit val markCountReader: JsonReader[MarkCount] = jsonReader[MarkCount]

  implicit val facultyMarkStatisticReader: JsonReader[FacultyMarkStatistic] = jsonReader[FacultyMarkStatistic]
  implicit val facultyMarkStatisticWriter: JsonObjectWriter[FacultyMarkStatistic] = jsonWriter[FacultyMarkStatistic]
}