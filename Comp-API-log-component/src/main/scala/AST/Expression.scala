package AST
import ConcurrentImpl.isAssociativeCheck
//import ConcurrentImpl.parallelMap

import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Success, Failure}

sealed trait Expression[A] {

  def eval: Eval[List[A]]

  def map[B](f: A => B): Expression[B] = MapExpr(this, f)

  def fold(init: A)(f: (A, A) => A): Expression[A] = FoldExpr(this, f, init)

  def reduce(f: (A, A) => A)(implicit num: Numeric[A]): Expression[A] = ReduceExpr(this, f)

  def flatmap[B](f : A => Expression[B]) : Expression[B] = FlatMapExpr(this,f)

  def zip[B](that: Expression[B]): Expression[(A, B)] = ZipExpr(this, that)

  def unzip: Nothing = ???

  def sum(implicit num: Numeric[A]): Expression[A] = ReduceExpr(this, num.plus)

  def tthen[B](that: Expression[B]): Expression[B] = ThenExpr(this, that)

  def filter(predicate: A => Boolean): Expression[A] = FilterExpr(this, predicate)

  def project[B](f: A => B): Expression[B] = ProjectExpr(this, f)

  def join[B, C](other: Expression[B])(joinFunc: (A, B) => C): Expression[C] = JoinValues(this, other, joinFunc)

  def flatten[B](): Expression[B] = Flatten(this.asInstanceOf[Expression[List[B]]])

  def every(functions: List[(A, A) => A]): Expression[A] = EveryExpr(this, functions)

  def ifThenElse(condition: Expression[Boolean], thenExpr: Expression[A], elseExpr: Expression[A]): Expression[A] = IfExpr(condition, thenExpr, elseExpr)

}

object Expression {
  def value[A](values: A*): Expression[A] = Value(values.toList)
}


case class Value[A](value: List[A]) extends Expression[A] {
  def eval: Eval[List[A]] = Eval.later{
    println(value)
    value
  } // Непосредственное значение
}

case class MapExpr[A, B](exp: Expression[A], f: A => B) extends Expression[B] {
  def eval: Eval[List[B]] = {
    // Получаем значения из выражения
    val originalList = exp.eval.value
    // Создаем список Future для обработки каждого элемента
    val futures: Seq[Future[B]] = originalList.map(value => Future(f(value)))

    // Оборачиваем результаты в Eval
    Eval.later {
      // Ждем завершения всех Future и собираем результаты
      val results: Future[List[B]] = Future.sequence(futures).map(_.toList)
      Await.result(results,1.second)
    }
  }
}

case class FoldExpr[A](exp : Expression[A],f : (A,A) => A,zero : A) extends Expression[A] {
  def eval: Eval[List[A]] = {
    val originalList = exp.eval.value
    val isAssociative: Boolean = {isAssociativeCheck(originalList,f)}
    if (isAssociative) {
      val futures: Seq[Future[A]] = originalList.grouped(100).map { group =>
        Future {
          group.fold(zero)(f)
        }
      }.toSeq

      val resultFuture: Future[List[A]] = Future.sequence(List(Future.sequence(futures).map(_.reduce(f))))

      Eval.later {
        Await.result(resultFuture, 1.second)
      }
    } else {
      // Последовательная редукция
      Eval.later {
        List(originalList.fold(zero)(f))
      }
    }
  }
}

case class FlatMapExpr[A, B](exp: Expression[A], f: A => Expression[B]) extends Expression[B] {
  def eval: Eval[List[B]] = {
    val originalList = exp.eval.value
    val futures: Seq[Future[List[B]]] = originalList.map { value =>
      Future {
        f(value).eval.value
      }
    }
    Eval.later {
      val results: Future[List[List[B]]] = Future.sequence(futures).map(_.toList)
      // Flatten the list of lists after waiting for all futures to complete
      Await.result(results, 1.second).flatten
    }
  }
}

case class ZipExpr[A, B](exp1: Expression[A], exp2: Expression[B]) extends Expression[(A, B)] {
  def eval: Eval[List[(A, B)]] = for {
    list1 <- exp1.eval
    list2 <- exp2.eval
  } yield {
    //println("zipping")
    list1.zip(list2)
  } // Сочетаем
}

case class ReduceExpr[A](exp: Expression[A], f: (A, A) => A) extends Expression[A] {
  def eval: Eval[List[A]] = {
    val originalList = exp.eval.value
    val isAssociative : Boolean= {isAssociativeCheck(originalList,f)}
    if (isAssociative) {
      val futures: Seq[Future[A]] = originalList.grouped(100).map { group =>
        Future {
          group.reduce(f)
        }
      }.toSeq
      val resultFuture: Future[List[A]] = Future.sequence(List(Future.sequence(futures).map(_.reduce(f))))
      Eval.later {
        Await.result(resultFuture, 1.second)
      }
    } else {
      Eval.later {
        List(originalList.reduce(f))
      }
    }
  }
}

case class ThenExpr[A, B](first: Expression[A], second: Expression[B]) extends Expression[B] {
  def eval: Eval[List[B]] = for {
    _ <- first.eval // Выполняем первое выражение
    result <- second.eval // Затем выполняем второе выражение
  } yield result
}

case class FilterExpr[A](exp: Expression[A], predicate: A => Boolean) extends Expression[A] {
  def eval: Eval[List[A]] = for {
    expression <- exp.eval
  } yield expression.filter(predicate) // Фильтруем элементы по предикату
}

case class ProjectExpr[A, B](exp: Expression[A], f: A => B) extends Expression[B] {
  def eval: Eval[List[B]] = for {
    expression <- exp.eval
  } yield expression.map(f) // Применяем функцию к каждому элементу списка
}

case class JoinValues[A, B, C](left: Expression[A], right: Expression[B], joinFunc: (A, B) => C) extends Expression[C] {
  def eval: Eval[List[C]] = for {
    leftValues <- left.eval
    rightValues <- right.eval
  } yield for {
    l <- leftValues
    r <- rightValues
  } yield joinFunc(l, r)
}

case class Flatten[A](expression: Expression[List[A]]) extends Expression[A] {
  def eval: Eval[List[A]] = for {
    nestedList <- expression.eval
  } yield nestedList.flatten
}


case class EveryExpr[A](exp: Expression[A], functions: List[(A, A) => A]) extends Expression[A] {
  def eval: Eval[List[A]] = for {
    elements <- exp.eval
  } yield {
    functions.map { func =>
      elements match {
        case a :: b :: _ => func(a, b)
        case _ => throw new IllegalArgumentException("Список должен содержать как минимум два элемента.")
      }
    }
  }
}

case class IfExpr[A](condition: Expression[Boolean], thenExpr: Expression[A], elseExpr: Expression[A]) extends Expression[A] {
  def eval: Eval[List[A]] = for {
    cond <- condition.eval // Оцениваем условие
    result <- if (cond.head) thenExpr.eval else elseExpr.eval // Выбираем выражение в зависимости от условия
  } yield result
}


object ASTExample {
  def main(args: Array[String]): Unit = {
    // Создаем AST для выражения: Expression.value(1, 2, 3).map(x => x * x).reduce(_ + _)
//    val expression: Expression[Int] = Expression.value(1, 2, 3)
//      .map(x => x * x) // Применяем функцию x => x * x
//      .reduce(_ + _) // Суммируем результаты с использованием reduce
//    // Выполняем вычисление
//    println("пока только создали")
//    Thread.sleep(500)
//    val result: List[Int] = expression.eval.value // Извлекаем значение из Eval
//    println(s"Результат вычисления: ${result.headOption.getOrElse(0)}") // Ожидаемый результат: 14 (1*1 + 2*2 + 3*3)
//    val expression1: Expression[Int] = Expression.value(1, 2, 3)
//      .flatmap(x => Expression.value(x, x * 2)) // Применяем flatMap, чтобы создать новые значения
//
//    // Выполняем вычисление
//    println("пока только создали")
//    Thread.sleep(500)
//    val result1: List[Int] = expression1.eval.value // Извлекаем значение из Eval
//    println(s"Результат вычисления: ${result1.mkString(", ")}") // Ожидаемый результат: 1, 2, 2, 4, 3, 6

    val expression2: Expression[Int] = Expression.value(1,2,3,4).fold(0)((x,y) => x+y)// Суммируем все элементы

    // Выполняем вычисление
    println("Ожидание вычисления")
    val result2: List[Int] = expression2.eval.value // Получаем Eval[Int]
    println(s"Результат редукции: ${result2.mkString(",")}")
  }
}

