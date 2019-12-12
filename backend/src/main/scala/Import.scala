import Implicits._
import akka.http.scaladsl.model.Multipart
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.Directives._
import data.{Course, Department, Faculty, Group, Semester, Student}
import mongo.ClientMongo
import mongo.ClientMongoImpl._
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.Document
import tethys.{JsonReader, JsonWriter}

import scala.concurrent.Future

object Import {

  def routes(mongoClient: ClientMongo) = {
    path("import" / "students") {
      importJson[Student](mongoClient, students)
    } ~ path("import" / "groups") {
      importJson[Group](mongoClient, groups)
    } ~ path("import" / "departments") {
      importJson[Department](mongoClient, departments)
    } ~ path("import" / "faculties") {
      importJson[Faculty](mongoClient, faculties)
    } ~ path("import" / "courses") {
      importJson[Course](mongoClient, courses)
    } ~ path("import" / "semesters") {
      importJson[Semester](mongoClient, semesters)
    }
  }

  private def importJson[Entity](mongoClient: ClientMongo, mongoCollection: MongoCollection[Document])
                                (implicit writer: JsonWriter[Entity], reader: JsonReader[Entity]) = {
    entity(as[Multipart.FormData]) { (formdata: Multipart.FormData) =>

      val result = formdata.parts.mapAsync(1) { p ⇒

        p.entity.dataBytes.runFold("")(_ + _.utf8String).map {
          case (str) ⇒
            mongoClient.importJson[Entity](mongoCollection, str).map(p.getFilename() + " – " + _)
        }
      }.runFold(Future.successful(Seq.empty[String]))((res, str) => for {
        a <- res
        b <- str
      } yield a :+ b).flatMap(_.map(_.mkString(", ")))

      respondWithHeader(`Access-Control-Allow-Origin`.*) {
        complete {
          result
        }
      }
    }
  }
}
