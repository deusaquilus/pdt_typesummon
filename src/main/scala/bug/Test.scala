package bug

import Macro._

class MyContext extends Context {
  type RowType = Row1
  implicit def stringDecoder: Decoder[Row1, String] =
    new Decoder[Row1, String] {
      def decode(row: Row1): String = stringDecoder.decode(row)
    }
  val internalRow = Row1("hello")
  def serve[T](decoder: Decoder[Row1, T]): T = decoder.decode(internalRow)
}

@main def testMacro() = {
  val ctx = new MyContext()
  import ctx._
  println( serveDecoder[String](ctx) )
}