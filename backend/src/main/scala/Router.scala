
import java.util.UUID

import Implicits._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.headers._
import data._
import mongo.ClientMongo

import scala.concurrent.Future


object Router extends Json4sSupport {

  private def withAccessControlAllowOrigin(response: ToResponseMarshallable) =
    respondWithHeader(`Access-Control-Allow-Origin`.*) {
      complete(response)
    }

  def routes(mongoClient: ClientMongo): Route = get {
    path("students") {
      withAccessControlAllowOrigin(mongoClient.getStudents)
    } ~ path("courses") {
      withAccessControlAllowOrigin(mongoClient.getCourses)
    } ~ path("departments") {
      withAccessControlAllowOrigin(mongoClient.getDepartments)
    } ~ path("faculties") {
      withAccessControlAllowOrigin(mongoClient.getFaculties)
    } ~ path("groups") {
      withAccessControlAllowOrigin(mongoClient.getGroups)
    } ~ path("semesters") {
      withAccessControlAllowOrigin(mongoClient.getSemesters)
    } ~ path("statistic" / "faculty_marks") {
      parameter("faculty") { faculty =>
        withAccessControlAllowOrigin(mongoClient.facultyMarks(faculty))
      }
    } ~ path("statistic" / "semester_marks") {
      parameter("year".as[Int], "period") { (year, period) =>
        withAccessControlAllowOrigin(mongoClient.semesterMarks(year, period))
      }
    } ~ path("statistic" / "groups_average") {
      parameter("faculty") { faculty =>
        withAccessControlAllowOrigin(mongoClient.groupsAverage(faculty))
      }
    } ~ path("statistic" / "faculty_average") {
      withAccessControlAllowOrigin(mongoClient.facultiesAverage)
    }
  } ~ post {
    path("add_student") {
      parameters('name, 'surname, 'sex.as[Boolean], 'group.as[Int].*) { (name, surname, sex, groups) =>
        val response = for {
          groups <- Future.sequence(groups.map(groupNumber => {
            mongoClient.getGroupByNumber(groupNumber).map(group => if (group.isEmpty)
              throw new Exception(s"No such group $groupNumber. Student wasn't added")
            else group.get)
          }).toSeq)
          groupNumbers = groups.map(_.number)
          student = Student(UUID.randomUUID().toString, name, surname, sex, groupNumbers)
          response <- mongoClient.addStudent(student)
        } yield response
        withAccessControlAllowOrigin(response)
      }
    } ~ path("add_course") {
      parameter('name) { name =>
        val course = Course(name)
        withAccessControlAllowOrigin(mongoClient.addCourse(course))
      }
    } ~ path("add_department") {
      parameter('name) { name =>
        val department = Department(name)
        withAccessControlAllowOrigin(mongoClient.addDepartment(department))
      }
    } ~ path("add_faculty") {
      parameter('name) { name =>
        val faculty = Faculty(name)
        withAccessControlAllowOrigin(mongoClient.addFaculty(faculty))
      }
    } ~ path("add_group") {
      parameters('number.as[Int], 'faculty, 'department) { (number, facultyName, departmentName) =>
        withAccessControlAllowOrigin(for {
          faculty <- mongoClient.getFacultyByName(facultyName).map(faculty => if (faculty.isEmpty)
            throw new Exception(s"No such faculty $facultyName")
          else faculty.get)
          department <- mongoClient.getDepartmentByName(departmentName).map(_.fold[Department](
            throw new Exception(s"No such department $departmentName. Group wasn't added"))(department => department))
          group = Group(number, faculty.name, department.name)
          response <- mongoClient.addGroup(group)
        } yield response)
      }
    } ~ path("add_semester") {
      parameters('year.as[Int], 'period) { (year, period) =>
        val semester = Semester(year, period)
        withAccessControlAllowOrigin(mongoClient.addSemester(semester))
      }
    } ~ path("add_mark") {
      parameter('studentId, 'course, 'mark.as[Int], 'year.as[Int], 'period) { (studentId, course, markValue, year, period) =>
        withAccessControlAllowOrigin(for {
          semester <- mongoClient.getSemester(year, period).map(_.fold[Semester](
            throw new Exception(s"No such semester. Mark wasn't added"))(semester => semester))
          course <- mongoClient.getCourseByName(course).map(_.fold[Course](
            throw new Exception(s"No course $course. Mark wasn't added"))(course => course)
          )
          _ = if (markValue < 1 || markValue > 5) throw new Exception("Mark isn't possible. Please use marks in range from 1 to 5") else ()
          mark = Mark(markValue, semester, course)
          response <- mongoClient.addMark(studentId, mark)
        } yield response)
      }
    }
  }
}
