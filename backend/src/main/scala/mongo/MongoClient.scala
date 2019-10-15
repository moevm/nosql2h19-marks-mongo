package mongo

import data.Student
import org.mongodb.scala._
import tethys._
import tethys.derivation.semiauto._
import tethys.jackson._

import scala.concurrent.{ExecutionContext, Future}

trait ClientMongo {
  def addStudent(student: Student): Future[String]

  def getStudents: Future[Seq[Option[Student]]]
}

class ClientMongoImpl(implicit ec: ExecutionContext) extends ClientMongo {
  import ClientMongoImpl._

  private val mongoClient: MongoClient = MongoClient("mongodb://localhost")

  private val database = mongoClient.getDatabase("test2")

  val collection: MongoCollection[Document] = database.getCollection("test")

  override def addStudent(student: Student): Future[String] = {
    collection.insertOne(Document(student.asJson)).toFuture().recover {
      case e: Throwable => s"Error: ${e.getMessage}"
    }.map(_ => s"Student was added succesfully")
  }

  override def getStudents: Future[Seq[Option[Student]]] = {
    collection.find().toFuture().map(seq => seq.map(bsonStudent => bsonStudent.toJson().jsonAs[Student] match {
      case Right(student) => Some(student)
      case _ => None
    }))
  }
}

object ClientMongoImpl {
  implicit val studentWriter: JsonObjectWriter[Student] = jsonWriter[Student]
  implicit val studentReader: JsonReader[Student] = jsonReader[Student]
}