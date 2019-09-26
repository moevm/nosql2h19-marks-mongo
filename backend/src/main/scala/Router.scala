
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import data.Student
import mongo.ClientMongo
import tethys._
import tethys.jackson._
import mongo.ClientMongoImpl._


object Router extends Json4sSupport {
  def routes(mongoClient: ClientMongo): Route = get {
    path("students") {
      complete(mongoClient.getStudents)
    }
  } ~ post {
    path("addStudent") {
      parameter('student) { _.jsonAs[Student] match {
          case Right(student) => complete(mongoClient.addStudent(student))
          case _ => complete("Error during parsing student parameter")
        }
      }
    }
  }
}
