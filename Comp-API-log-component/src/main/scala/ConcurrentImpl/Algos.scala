package ConcurrentImpl

import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Success, Failure}

implicit val ec: ExecutionContext = ExecutionContext.global

def isAssociativeCheck[A](values: List[A], f: (A, A) => A): Boolean = {
  val left = f(f(values.head, values(1)), values(2))
  val right = f(values.head, f(values(1), values(2)))
  left == right
}
 
