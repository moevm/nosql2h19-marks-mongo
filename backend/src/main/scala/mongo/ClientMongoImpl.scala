package mongo

import data.{Course, CourseTeacher, Department, Faculty, Mark, Semester, Student, Teacher}
import org.mongodb.scala._
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
  val teachers: MongoCollection[Document] = database.getCollection("teachers")
  val courseTeachers: MongoCollection[Document] = database.getCollection("course_teachers")

  override def addStudent(student: Student): Future[String] = {
    students.insertOne(Document(student.asJson)).toFuture().recover {
      case e: Throwable => s"Error: ${e.getMessage}"
    }.map(_ => s"Student was added succesfully")
  }

  override def getStudents: Future[Seq[Option[Student]]] = {
    students.find().toFuture().map(_.map(_.toJson().jsonAs[Student] match {
      case Right(student) => Some(student)
      case _ => None
    }))
  }

  override def addCourse(course: Course): Future[String] = {
    courses.insertOne(Document(course.asJson)).toFuture().recover {
      case e: Throwable => s"Error: ${e.getMessage}"
    }.map(_ => s"Course was added succesfully")
  }

  override def getCourses: Future[Seq[Option[Course]]] = {
    courses.find().toFuture().map(_.map(_.toJson().jsonAs[Course] match {
      case Right(course) => Some(course)
      case _ => None
    }))
  }

  override def addFaculty(faculty: Faculty): Future[String] = {
    faculties.insertOne(Document(faculty.asJson)).toFuture().recover {
      case e: Throwable => s"Error: ${e.getMessage}"
    }.map(_ => s"Faculty was added succesfully")
  }

  override def getFaculties: Future[Seq[Option[Faculty]]] = {
    faculties.find().toFuture().map(_.map(_.toJson().jsonAs[Faculty] match {
      case Right(faculty) => Some(faculty)
      case _ => None
    }))
  }

  override def addDepartment(department: Department): Future[String] = {
    departments.insertOne(Document(department.asJson)).toFuture().recover {
      case e: Throwable => s"Error: ${e.getMessage}"
    }.map(_ => s"Department was added succesfully")
  }

  override def getDepartments: Future[Seq[Option[Department]]] = {
    departments.find().toFuture().map(_.map(_.toJson().jsonAs[Department] match {
      case Right(department) => Some(department)
      case _ => None
    }))
  }

  override def addTeacher(teacher: Teacher): Future[String] = {
    teachers.insertOne(Document(teacher.asJson)).toFuture().recover {
      case e: Throwable => s"Error: ${e.getMessage}"
    }.map(_ => s"Teacher was added succesfully")
  }

  override def getTeachers: Future[Seq[Option[Teacher]]] = {
    teachers.find().toFuture().map(_.map(_.toJson().jsonAs[Teacher] match {
      case Right(teacher) => Some(teacher)
      case _ => None
    }))
  }

  override def addCourseTeacher(courseTeacher: CourseTeacher): Future[String] = {
    courseTeachers.insertOne(Document(courseTeacher.asJson)).toFuture().recover {
      case e: Throwable => s"Error: ${e.getMessage}"
    }.map(_ => s"Course teacher was added succesfully")
  }

  override def getCourseTeachers: Future[Seq[Option[CourseTeacher]]] = {
    courseTeachers.find().toFuture().map(_.map(_.toJson().jsonAs[CourseTeacher] match {
      case Right(courseTeacher) => Some(courseTeacher)
      case _ => None
    }))
  }
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

  implicit val teacherWriter: JsonObjectWriter[Teacher] = jsonWriter[Teacher]
  implicit val teacherReader: JsonReader[Teacher] = jsonReader[Teacher]

  implicit val courseTeacherWriter: JsonObjectWriter[CourseTeacher] = jsonWriter[CourseTeacher]
  implicit val courseTeacherReader: JsonReader[CourseTeacher] = jsonReader[CourseTeacher]
}