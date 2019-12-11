import Implicits._
import akka.http.scaladsl.model.Multipart
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.Directives._
import akka.util.ByteString
import data.Student
import mongo.ClientMongo
import mongo.ClientMongoImpl._

object Import {

  def routes(mongoClient: ClientMongo) = {
    path("import" / "students") {
      entity(as[Multipart.FormData]) { (formdata: Multipart.FormData) =>

        val fileNamesFuture = formdata.parts.mapAsync(1) { p ⇒
          println(s"Got part. name: ${p.name} filename: ${p.filename}")

          p.entity.dataBytes.map(a => println(a.utf8String))
          println("Counting size...")
          var lastReport = System.currentTimeMillis()
          var lastSize = 0L

          def receiveChunk(counter: (Long, Long, String), chunk: ByteString): (Long, Long, String) = {
            println(chunk.utf8String)
            val (oldSize, oldChunks, str) = counter
            val newSize = oldSize + chunk.size
            val newChunks = oldChunks + 1

            val now = System.currentTimeMillis()
            if (now > lastReport + 1000) {
              val lastedTotal = now - lastReport
              val bytesSinceLast = newSize - lastSize
              val speedMBPS = bytesSinceLast.toDouble / 1000000 //* bytes per MB  / lastedTotal * 1000  millis per second

              println(f"Already got $newChunks%7d chunks with total size $newSize%11d bytes avg chunksize ${newSize / newChunks}%7d bytes/chunk speed: $speedMBPS%6.2f MB/s")

              lastReport = now
              lastSize = newSize
            }
            (newSize, newChunks, str + chunk.utf8String)
          }

          p.entity.dataBytes.runFold((0L, 0L, ""))(receiveChunk).map {
            case (size, numChunks, str) ⇒
              println(s"Size is $size")
              mongoClient.importJson[Student](students, str).foreach(println)
              (p.name, p.filename, size)
          }
        }.runFold(Seq.empty[(String, Option[String], Long)])(_ :+ _).map(_.mkString(", "))

        respondWithHeader(`Access-Control-Allow-Origin`.*) {
          complete {
            "Ок"
          }
        }
      }
    }
  }
}
