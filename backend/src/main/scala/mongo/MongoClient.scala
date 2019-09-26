package mongo

import data.Student
import org.mongodb.scala._
import tethys._
import tethys.derivation.semiauto._
import tethys.jackson._

import scala.concurrent.{ExecutionContext, Future}

trait ClientMongo {
  def addStudent(student: Student): Future[String]

  def getStudents: Future[Seq[String]]
}

class ClientMongoImpl(implicit ec: ExecutionContext) extends ClientMongo {
  import ClientMongoImpl._

  private val mongoClient: MongoClient = MongoClient("mongodb://localhost")

  private val database = mongoClient.getDatabase("test2")

  val collection: MongoCollection[Document] = database.getCollection("test")

  override def addStudent(student: Student): Future[String] = {
    collection.insertOne(Document(student.asJson)).toFuture().recover {
      case e: Throwable => println("Error: " + e.getMessage)
    }.map(_ => s"Student was added succesfully")
  }

  override def getStudents: Future[Seq[String]] = {
    collection.find().toFuture().map(seq => seq.map(_.toJson()))
  }
}

object ClientMongoImpl {
  implicit val studentWriter: JsonObjectWriter[Student] = jsonWriter[Student]
  implicit val studentReader: JsonReader[Student] = jsonReader[Student]
}