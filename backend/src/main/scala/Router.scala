
import java.util.UUID

import Implicits._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import data._
import mongo.ClientMongo

import scala.concurrent.Future


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
    } ~ path("groups") {
      complete(mongoClient.getGroups)
    }
  } ~ post {
    path("add_student") {
      parameters('name, 'surname, 'sex, 'groups.as[Int].*) { (name, surname, sex, groups) =>
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
        complete(response.recover {
          case th: Throwable => th.getMessage
        })
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
        complete((for {
          departments <- Future.sequence(departments.map(departmentName => mongoClient.getDepartmentByName(departmentName).map(_.fold[Department](
            throw new Exception(s"No such department $departmentName. Teacher wasn't added"))(department => department))))
          departmentsNames = departments.toSeq.map(_.name)
          teacher = Teacher(UUID.randomUUID().toString, name, departmentsNames)
          response <- mongoClient.addTeacher(teacher)
        } yield response).recover {
          case th: Throwable => th.getMessage
        })
      }
    } ~ path("add_group") {
      parameters('number.as[Int], 'faculty, 'department) { (number, facultyName, departmentName) =>
        complete((for {
          faculty <- mongoClient.getFacultyByName(facultyName).map(faculty => if (faculty.isEmpty)
            throw new Exception(s"No such faculty $facultyName")
          else faculty.get)
          department <- mongoClient.getDepartmentByName(departmentName).map(_.fold[Department](
            throw new Exception(s"No such department $departmentName. Group wasn't added"))(department => department))
          group = Group(number, faculty.name, department.name)
          response <- mongoClient.addGroup(group)
        } yield response).recover {
          case th: Throwable => th.getMessage
        })
      }
    } ~ path("add_course_teacher") {
      parameters('courseId, 'teacherId) { (courseId, teacherId) =>
        complete ((for {
          course <- mongoClient.getCourseById(courseId).map(_.fold[Course](
            throw new Exception(s"No course with id=$courseId. Course teacher wasn't added"))(course => course)
          )
          teacher <- mongoClient.getTeacherById(teacherId).map(_.fold[Teacher](
            throw new Exception(s"No teacher with id=$teacherId. Course teacher wasn't added"))(teacher => teacher))
          courseTeacher = CourseTeacher(course.id, teacher.id)
          response <- mongoClient.addCourseTeacher(courseTeacher)
        } yield response).recover {
          case th: Throwable => th.getMessage
        })
      }
    }
  }

}

