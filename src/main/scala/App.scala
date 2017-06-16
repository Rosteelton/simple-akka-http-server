import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.{Encoder, Json}
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.Future
import scala.io.StdIn

object App extends App {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()

  implicit val executionContext = system.dispatcher


  val advices: Map[String, String] =
    Map("evotor.intent.action.receipt.sell.OPENED" -> "Открыл чек значит? Не забудь закрыть!",
      "evotor.intent.action.receipt.sell.CLEARED" -> "Зачем ты очистил чек?",
      "evotor.intent.action.receipt.sell.POSITION_ADDED" -> "Отличный выбор!")


  def resolveEvent(eventName: String): Future[ResultDTO] =
    Future.successful(ResultDTO(advices.get(eventName)))


  implicit val encoder: Encoder[ResultDTO] = {
    Encoder.instance { a =>
      a.value match {
        case Some(value) =>
          Json.obj(
            "success" -> Json.True,
            "advice" -> value.asJson
          )
        case None =>
          Json.obj(
            "success" -> Json.False,
            "reason" -> "Не знаю такого ивента".asJson
          )
      }
    }
  }

  final case class ResultDTO(value: Option[String])

  final case class GetAdviceForEventDTO(eventType: String)

  val route =
    path("api" / "v1" / "getAdviceForEvent") {
      post {
        entity(as[GetAdviceForEventDTO]) { dto =>
          complete(resolveEvent(dto.eventType))
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "10.10.10.154", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
