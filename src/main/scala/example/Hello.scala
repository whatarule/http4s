package example

import cats.effect._
import cats.syntax.all._
import cats.data._

import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.dsl.io._
import org.http4s.server.blaze._
import org.http4s.implicits._
import org.http4s.server.Router

//import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext.global

import sttp.tapir._
import sttp.tapir.server.http4s._
import cats.kernel.Semigroup

object Hello extends IOApp {
  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  //implicit val timer: Timer[IO] = IO.timer(global)

  def hello(name: String): IO[Either[Unit, String]] =
    IO.pure(s"hello, $name!".asRight[Unit])
  val helloEP: Endpoint[String, Unit, String, Any] =
    endpoint.get.in("hello" / path[String]("name")).out(stringBody)

  def count(s: String): IO[Either[Unit, Int]] =
    IO.pure(s.length().asRight[Unit])
  val countEP: Endpoint[String, Unit, Int, Any] =
    endpoint.in(stringBody).out(plainBody[Int])

  implicit val routesSemigroup: Semigroup[HttpRoutes[IO]] = _ combineK _
  val httpApp: HttpApp[IO] =
    NonEmptyList.of(helloEP toRoutes hello, countEP toRoutes count).reduce orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
