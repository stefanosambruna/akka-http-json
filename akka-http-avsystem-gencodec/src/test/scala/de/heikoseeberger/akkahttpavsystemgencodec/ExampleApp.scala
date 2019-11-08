/*
 * Copyright 2015 Heiko Seeberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.heikoseeberger.akkahttpavsystemgencodec

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.{ Materialzer, Materializer }
import com.avsystem.commons.serialization.GenCodec

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn

object ExampleApp {

  final object Foo {
    implicit val codec: GenCodec[Foo] = GenCodec.materialize[Foo]
  }

  final case class Foo(bar: String)

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val mat    = Materialzer()

    Http().bindAndHandle(route, "127.0.0.1", 8000)

    StdIn.readLine("Hit ENTER to exit")
    Await.ready(system.terminate(), Duration.Inf)
  }

  def route(implicit mat: Materializer) = {
    import Directives._
    import GenCodecSupport._

    pathSingleSlash {
      post {
        entity(as[Foo]) { foo =>
          complete {
            foo
          }
        }
      }
    }
  }
}
