package bug

import Macro._

@main def testMacro() = {
  val ctx = new MyContext()
  import ctx._
  println( serveDecoder[String](ctx) )
}