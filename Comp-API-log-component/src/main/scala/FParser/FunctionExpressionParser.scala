package FParser

import AST.{Expression, MapExpr, ReduceExpr, Value}
import scala.util.parsing.combinator.RegexParsers
import scala.util.{Failure, Success, Try}

object ExpressionParser extends RegexParsers {

  // Определяем базовые парсеры для чисел и идентификаторов
  def number: Parser[Int] = """\d+""".r ^^ {
    _.toInt
  }

  def identifier: Parser[String] = """[a-zA-Z_][a-zA-Z0-9_]*""".r

  // Парсер для списка чисел
  def numberList: Parser[List[Int]] = "(" ~> repsep(number, ",") <~ ")"

  // Парсер для выражения value
  def valueExpr: Parser[Expression[Int]] = "value" ~> numberList ^^ { numbers =>
    Expression.value(numbers: _*)
  }

  // Парсер для выражения map
  def mapExpr: Parser[Expression[Int]] = "map" ~> "(" ~> expression ~ ("," ~> identifier) <~ ")" ^^ {
    case exp ~ function =>
      val func = function match {
        case "square" => (x: Int) => x * x
        case _ => throw new IllegalArgumentException(s"Неизвестная функция: $function")
      }
      exp.map(func)
  }

  // Парсер для выражения reduce
  def reduceExpr: Parser[Expression[Int]] = "reduce" ~> "(" ~> expression ~ ("," ~> identifier) <~ ")" ^^ {
    case exp ~ function =>
      val func = function match {
        case "sumfunc" => (a: Int, b: Int) => a + b
        case _ => throw new IllegalArgumentException(s"Неизвестная функция: $function")
      }
      exp.reduce(func)
  }

  // Парсер для выражения fold
  def foldExpr: Parser[Expression[Int]] = "fold" ~> "(" ~> expression ~ ("," ~> number) ~ ("," ~> identifier) <~ ")" ^^ {
    case exp ~ zero ~ function =>
      val func = function match {
        case "sumfunc" => (a: Int, b: Int) => a + b
        case _ => throw new IllegalArgumentException(s"Неизвестная функция: $function")
      }
      exp.fold(zero)(func)
  }

  // Парсер для выражения flatMap
  def flatMapExpr: Parser[Expression[Int]] = "flatMap" ~> "(" ~> expression ~ ("," ~> identifier) <~ ")" ^^ {
    case exp ~ function =>
      val func = function match {
        case "double" => (x: Int) => Expression.value(x, x * 2)
        case _ => throw new IllegalArgumentException(s"Неизвестная функция: $function")
      }
      exp.flatmap(func)
  }

  // Общий парсер для выражений
  def expression: Parser[Expression[Int]] = valueExpr | foldExpr | reduceExpr | mapExpr | flatMapExpr
  
  // Метод для парсинга строки
  def parseExpression(input: String): Expression[Int] = {
    parseAll(expression, input) match {
      case Success(result, _) => result
      case Failure(msg, _) => throw new IllegalArgumentException(s"Ошибка парсинга: $msg")
      case Error(msg, _) => throw new IllegalArgumentException(s"Ошибка парсинга: $msg")
    }
  }
}

object FunctionExpressionParser {
  def main(args: Array[String]): Unit = {
    val input = "map(reduce(map(map(value(1,2,3,4,5,6,7,8),square),square),sumfunc),square)"
    val expression = ExpressionParser.parseExpression(input)

    // Выполняем вычисление
    val input1 = "map(fold(map(value(1,2,3,4,5,6,7),square),150,sumfunc),square)"
    val input2 = "map(fold(map(value(1,2,3),square),140,sumfunc),square)"
    val expression1 = ExpressionParser.parseExpression(input1)
    val expression2 = ExpressionParser.parseExpression(input2)
    
    val result: List[Int] = expression.eval.value // Извлекаем значение из Eval
    val res1 = expression1.eval.value
    val res2 = expression2.eval.value

    println(s"I NEED IT even more:  $res2")
    println(s"I NEED IT:  $res1")
    println(s"Результат вычисления: ${result}") // Ожидаемый результат
  }
}



