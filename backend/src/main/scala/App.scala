import Implicits._
import mongo.ClientMongoImpl
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object App {
  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load("application")
    val serverHost = config.getString("server_host")
    val serverPort = config.getInt("server_port")

    val mongoClient = new ClientMongoImpl()
    val bindingFuture = Http().bindAndHandle(Router.routes(mongoClient), serverHost, serverPort)
    println(s"Server online at $serverHost:$serverPort")

    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
