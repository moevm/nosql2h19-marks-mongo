
import java.util.UUID

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import data._
import mongo.ClientMongo


object Router extends Json4sSupport {
  def routes(mongoClient: ClientMongo): Route = get {
    path("students") {
      complete(mongoClient.getStudents)
    } ~ path("courses") {
      complete(mongoClient.getCourses)
    } ~ path("course_teachers") {
      complete(mongoClient.getCourseTeachers)
    } ~ path("departments") {
      complete(mongoClient.getDepartments)
    } ~ path("faculties") {
      complete(mongoClient.getFaculties)
    } ~ path("teachers") {
      complete(mongoClient.getTeachers)
    }
  } ~ post {
    path("add_student") {
      parameters('name, 'surname, 'sex, 'groups.as[Int].*) { (name, surname, sex, groups) =>
        val student = Student(UUID.randomUUID().toString, name, surname, sex, groups.toSeq)
        complete(mongoClient.addStudent(student))
      }
    } ~ path("add_course") {
      parameter('name) { name =>
        val course = Course(UUID.randomUUID().toString, name)
        complete(mongoClient.addCourse(course))
      }
    } ~ path("add_department") {
      parameter('name) { name =>
        val department = Department(name)
        complete(mongoClient.addDepartment(department))
      }
    } ~ path("add_faculty") {
      parameter('name) { name =>
        val faculty = Faculty(name)
        complete(mongoClient.addFaculty(faculty))
      }
    } ~ path("add_teacher") {
      parameters('name, 'departments.*) { (name, departments) =>
        val teacher = Teacher(UUID.randomUUID().toString, name, departments.toSeq)
        complete(mongoClient.addTeacher(teacher))
      }
    }
  }
}
