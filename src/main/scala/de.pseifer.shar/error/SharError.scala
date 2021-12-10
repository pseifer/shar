package de.pseifer.shar.error

/** An alias for Either[SharError, T].
  */
type SharTry[T] = Either[SharError, T]

/** SharError -+- InternalError
  * |
  * +- TypeError
  * |
  * +- Warning
  * |
  * +- SPARQLError
  * |
  * +- ParseError
  * |
  * +- IOError
  *
  * extends Exception
  */
abstract class SharError(val prefix: String, val msg: String):

  /** The final error message, composing the Shar prefix, shared prefix and
    * specific message.
    */
  final def show: String = SharError.format(prefix, msg)

  override def toString: String = show

object SharError:

  /** Common formatting for all Shar errors.
    */
  protected def format(prefix: String, msg: String): String =
    s"[Shar] $prefix: $msg"

  /** Transform a Seq[SharTry[T]] to a SharTry[Seq[T]], selecting the first
    * error found via 'find', if any failures exist.
    */
  def getFirst[T](lst: Seq[SharTry[T]]): SharTry[Seq[T]] =
    lst.find(_.isLeft) match
      // Nothing can be 'left'
      case None => Right(lst.map(_.toOption.get))
      // This one is 'left'
      case Some(err) => Left(err.swap.toOption.get)

/** IO Error (can occur in REPL/etc.).
  */
class IOError(msg: String) extends SharError("IO Error", msg)

/** Shar-internal Errors
  */
class InternalError(msg: String = "") extends SharError("Internal Error", msg)

/** Features that are not yet implemented.
  */
class NotYetImplementedError(feature: String)
    extends SharError("not yet implemented: ", feature)
