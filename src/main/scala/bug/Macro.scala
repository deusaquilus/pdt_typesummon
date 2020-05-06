package bug

import scala.quoted._
import scala.quoted.matching._

trait Decoder[RowType, T] {
  def decode(row:RowType): T
}

case class Row1(row: String)

trait Context {
  type RowType
  implicit def stringDecoder: Decoder[RowType, String]
  def serve[T](decoder: Decoder[RowType, T]): T
}

class MyContext extends Context {
  type RowType = Row1
  implicit def stringDecoder: Decoder[Row1, String] =
    new Decoder[Row1, String] {
      def decode(row: Row1): String = stringDecoder.decode(row)
    }
  val internalRow = Row1("hello")
  def serve[T](decoder: Decoder[Row1, T]): T = decoder.decode(internalRow)
}

object Macro {

  inline def serveDecoder[T](context: Context): T = ${ serveDecoderImpl[T]('context) }
  def serveDecoderImpl[T: Type](context: Expr[Context])(using qctx: QuoteContext): Expr[T] = {
    import qctx.tasty._
    val tpe = '[Decoder[$context.RowType, T]]
    val decoderExpr = 
      Expr.summon(using tpe) match {
        case Some(decoder) => decoder
        case None => qctx.throwError(s"Cannot find decoder for: ${tpe.show}")
      }
    
    '{ $context.serve[T]($decoderExpr) }
  }
}
