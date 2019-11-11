import Implicits._
import mongo.ClientMongoImpl
import akka.http.scaladsl.Http

import scala.io.StdIn

object App {
  def main(args: Array[String]): Unit = {

    val serverHost = "localhost"
    val serverPort = 8080

    val mongoClient = new ClientMongoImpl()
    val bindingFuture = Http().bindAndHandle(Router.routes(mongoClient), serverHost, serverPort)
    println(s"Server online at $serverHost:$serverPort")

    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
