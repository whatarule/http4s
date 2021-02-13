package example

import cats.effect._
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
//import scala.concurrent.ExecutionContext.Implicits.global

import cats.syntax.all._
import org.http4s.server.blaze._
import org.http4s.implicits._
import org.http4s.server.Router

import scala.concurrent.ExecutionContext.global

object Hello extends IOApp {
  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  //implicit val timer: Timer[IO] = IO.timer(global)

  val helloWorldService = HttpRoutes.of[IO] { case GET -> Root / "hello" / name =>
    Ok(s"hello, $name!")
  }

  val services = helloWorldService
  val httpApp = Router("/" -> helloWorldService, "/api" -> services).orNotFound
  val serverBuilder = BlazeServerBuilder[IO](global).bindHttp(8080, "localhost").withHttpApp(httpApp)

  //val fiber = serverBuilder.resource.use(_ => IO.never).start.unsafeRunSync()
  //fiber.cancel.unsafeRunAsync(e => ())

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
