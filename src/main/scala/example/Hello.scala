package example

import cats.effect._, org.http4s._, org.http4s.dsl.io._, scala.concurrent.ExecutionContext.Implicits.global
import cats.syntax.all._
import org.http4s.server.blaze._
import org.http4s.implicits._
import org.http4s.server.Router

object Hello extends Greeting with App {
  println(greeting)

  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  implicit val timer: Timer[IO] = IO.timer(global)

  val helloWorldService = HttpRoutes.of[IO] { case GET -> Root / "hello" / name =>
    Ok(s"hello, $name")
  }

  val services = helloWorldService
  val httpApp = Router("/" -> helloWorldService, "/api" -> services).orNotFound
  val serverBuilder = BlazeServerBuilder[IO](global).bindHttp(8080, "localhost").withHttpApp(httpApp)

  val fiber = serverBuilder.resource.use(_ => IO.never).start.unsafeRunSync()
  //fiber.cancel.unsafeRunAsync(e => ())
}

trait Greeting {
  lazy val greeting: String = "hello"
}
