package example

import cats.effect._
import cats.syntax.all._

import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.dsl.io._
import org.http4s.server.blaze._
import org.http4s.implicits._
import org.http4s.server.Router

import scala.concurrent.ExecutionContext.global

import sttp.tapir._
import sttp.tapir.server.http4s._

object Hello extends IOApp {
  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  def countLogic(s: String): IO[Either[Unit, Int]] =
    IO.pure(s.length().asRight[Unit])
  val countEP: Endpoint[String, Unit, Int, Any] =
    endpoint.in(stringBody).out(plainBody[Int])
  val countRoutes: HttpRoutes[IO] =
    Http4sServerInterpreter.toRoutes(countEP)(countLogic _)
  val countService: HttpApp[IO] = countRoutes orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(countService)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
