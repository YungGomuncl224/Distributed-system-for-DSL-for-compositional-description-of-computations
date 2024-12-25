package ConcurrentImpl

import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.*
import scala.util.{Failure, Success}

implicit val ec: ExecutionContext = ExecutionContext.global

def isAssociativeCheck[A](values: List[A], f: (A, A) => A): Boolean = {
  val left = f(f(values.head, values(1)), values(2))
  val right = f(values.head, f(values(1), values(2)))
  left == right
}

@main
def some() : Unit =
  object Solution {
    def findingUsersActiveMinutes(logs: Array[Array[Int]], k: Int): Array[Int] = {

      val result = Array.fill(k)(0)
      var c = 0
      result.map(
        x => {
          c += 1
          logs
            .groupBy(log => log(0))
            .map { case (id, logs) => id -> logs.map(_(1)).distinct.length }
            .values.count(x => x == c)
        }
      )
      val userActiveMinutes = logs
        .groupBy(log => log(0))
        .map { case (id, logs) => id -> logs.map(_(1)).toSet.size }
        .values.foreach { uam =>
          if (uam > 0) result(uam - 1) += 1
        }
      val mama: String = "abc"
      val s = mama.contains("")

      result

    }

  }
