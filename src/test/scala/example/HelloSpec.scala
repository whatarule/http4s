import org.scalatest.funsuite._
import util.chaining._

import sttp.tapir._
import sttp.tapir.server.http4s._
import scala.concurrent.ExecutionContext.global

import app._

class HelloSpec extends AnyFunSuite {

  test("dsl") {
    import cats.effect._, cats.syntax.all._
    import org.http4s._, org.http4s.dsl.io._, org.http4s.implicits._
    implicit val timer: Timer[IO] = IO.timer(global)

    val service = HttpRoutes.of[IO] { case _ => IO(Response(Status.Ok)) }
    val getRoot = Request[IO](Method.GET, uri"/")
    val io = service.orNotFound.run(getRoot)
    val response = io.unsafeRunSync(); response pipe println
    import fs2.text

    val okIO = Ok()
    val ok = okIO.unsafeRunSync(); ok pipe println
    HttpRoutes.of[IO] { case _ => Ok() }.orNotFound.run(getRoot).unsafeRunSync() pipe println
    HttpRoutes.of[IO] { case _ => NoContent() }.orNotFound.run(getRoot).unsafeRunSync() pipe println

    Ok("Ok response.").unsafeRunSync().headers pipe println
    import org.http4s.headers.`Cache-Control`
    import org.http4s.CacheDirective.`no-cache`
    import cats.data.NonEmptyList
    Ok("Ok response.", `Cache-Control`(NonEmptyList(`no-cache`(), Nil))).unsafeRunSync().headers pipe println
    Ok("Ok response.", Header("X-Auth-Token", "Value")).unsafeRunSync().headers pipe println

    (helloEP toRoutes hello).orNotFound
      .run(Request[IO](GET, uri"hello/bbb"))
      .unsafeRunSync()
      .body
      .through(text.utf8Decode)
      .compile
      .toList
      .unsafeRunSync pipe println
    httpApp.run(Request[IO](GET, uri"hello/aaa")).unsafeRunSync()
  }
}
