package bug

import scala.quoted._
import scala.quoted.matching._

trait Decoder[RowType, T] {
  def decode(row:RowType): T
}

trait Context {
  type RowType
  implicit def stringDecoder: Decoder[RowType, String]
  def serve[T](decoder: Decoder[RowType, T]): T
}

object Macro {

  inline def serveDecoder[T](context: Context): T = ${ serveDecoderImpl('context) }
  def serveDecoderImpl[T: Type, U: Type, C <: Context { type RowType = U }: Type](context: Expr[C])(using qctx: QuoteContext): Expr[T] = {
    import qctx.tasty._

    val tpe = '[Decoder[U, T]]
    val decoderExpr = 
      Expr.summon(using tpe) match {
        case Some(decoder) => decoder
        case None => qctx.throwError(s"Cannot find decoder for: ${tpe.show}")
      }
    
    '{ $context.serve[T]($decoderExpr) }
  }
}
