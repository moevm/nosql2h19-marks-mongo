
import java.util.UUID

import Implicits._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import data._
import mongo.{ClientMongo, ClientMongoImpl}


object Router extends Json4sSupport {

  private val exceptionHandler = ExceptionHandler {
    case th: Throwable =>
      respondWithHeader(`Access-Control-Allow-Origin`.*) {
        complete(HttpResponse(StatusCodes.InternalServerError, entity = th.getMessage))
      }
  }

  private def withAccessControlAllowOrigin(response: ToResponseMarshallable) =
    respondWithHeader(`Access-Control-Allow-Origin`.*) {
      complete(response)
    }

  private def toFileResponse(filename: String, fileData: String): HttpResponse = {
    val httpEntity = HttpEntity(MediaTypes.`application/json`, fileData)
    val httpHeader = `Content-Disposition`(ContentDispositionTypes.attachment, Map("filename" -> filename))
    HttpResponse(entity = httpEntity, headers = List(httpHeader))
  }

  def routes(mongoClient: ClientMongo): Route = handleExceptions(exceptionHandler) {
    get {
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
      } ~ path("statistic" / "course_marks"){
        parameter("course"){ course =>
          withAccessControlAllowOrigin(mongoClient.courseMarks(course))
        }
      }~ path("statistic" / "groups_average") {
        parameter("faculty") { faculty =>
          withAccessControlAllowOrigin(mongoClient.groupsAverage(faculty))
        }
      } ~ path("statistic" / "faculty_average") {
        withAccessControlAllowOrigin(mongoClient.facultiesAverage)
      } ~ path("group_students") {
        parameter("group".as[Int])(groupNumber =>
          withAccessControlAllowOrigin(mongoClient.groupStudents(groupNumber))
        )
      }~ path("faculty_groups") {
        parameter("faculty")(faculty =>
          withAccessControlAllowOrigin(mongoClient.facultyGroups(faculty))
        )
      } ~ path("export" / "students") {
        withAccessControlAllowOrigin(mongoClient.exportJson(ClientMongoImpl.students).map(fileData => {
          toFileResponse("students.json", fileData)
        }
        ))
      } ~ path("export" / "groups") {
        withAccessControlAllowOrigin(mongoClient.exportJson(ClientMongoImpl.groups).map(fileData => {
          toFileResponse("groups.json", fileData)
        }
        ))
      } ~ path("export" / "semesters") {
        withAccessControlAllowOrigin(mongoClient.exportJson(ClientMongoImpl.semesters).map(fileData => {
          toFileResponse("semesters.json", fileData)
        }
        ))
      } ~ path("export" / "courses") {
        withAccessControlAllowOrigin(mongoClient.exportJson(ClientMongoImpl.courses).map(fileData => {
          toFileResponse("courses.json", fileData)
        }
        ))
      } ~ path("export" / "faculties") {
        withAccessControlAllowOrigin(mongoClient.exportJson(ClientMongoImpl.faculties).map(fileData => {
          toFileResponse("faculties.json", fileData)
        }
        ))
      } ~ path("export" / "departments") {
        withAccessControlAllowOrigin(mongoClient.exportJson(ClientMongoImpl.departments).map(fileData => {
          toFileResponse("departments.json", fileData)
        }
        ))
      }
    } ~ post {
      path("add_student") {
        parameters('name, 'surname, 'sex.as[Boolean], 'group.as[Int]) { (name, surname, sex, group) =>
          withAccessControlAllowOrigin {
            for {
              group <- mongoClient.getGroupByNumber(group).map(group => if (group.isEmpty)
                throw new Exception(s"No such group $group. Student wasn't added")
              else group.get)
              student = Student(UUID.randomUUID().toString, name, surname, sex, group.number)
              response <- mongoClient.addStudent(student)
            } yield response
          }
        }
    } ~ path("add_course") {
      parameter('name) { name =>
        withAccessControlAllowOrigin(mongoClient.addCourse(Course(name)))
      }
    } ~ path("add_department") {
      parameter('name) { name =>
        withAccessControlAllowOrigin(mongoClient.addDepartment(Department(name)))
      }
    } ~ path("add_faculty") {
      parameter('name) { name =>
        withAccessControlAllowOrigin(mongoClient.addFaculty(Faculty(name)))
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
        withAccessControlAllowOrigin(mongoClient.addSemester(Semester(year, period)))
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

}
