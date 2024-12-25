package Parsing

import AST.Expression
import scala.util.{Try, Success, Failure}

object ArithmeticExpressionParser {

  sealed trait Token

  case class Number(value: Int) extends Token

  case class Plus() extends Token

  case class Minus() extends Token

  case class Times() extends Token

  case class Divide() extends Token

  case class LParen() extends Token

  case class RParen() extends Token

  def tokenize(input: String): List[Token] = {
    val regex = """\s*(\d+|[+*/()-])\s*""".r
    regex.findAllMatchIn(input).toList.map { m =>
      m.group(1) match {
        case num if num.forall(_.isDigit) => Number(num.toInt)
        case "+" => Plus()
        case "-" => Minus()
        case "*" => Times()
        case "/" => Divide()
        case "(" => LParen()
        case ")" => RParen()
      }
    }
  }

  def parse(tokens: List[Token]): Expression[Int] = {
    def parseExpression(tokens: List[Token]): (Expression[Int], List[Token]) = {
      val (term, rest) = parseTerm(tokens)
      rest match {
        case Plus() :: tail =>
          val (nextTerm, nextRest) = parseExpression(tail)
          (term.zip(nextTerm).map { case (a, b) => a + b }, nextRest) // Сложение
        case Minus() :: tail =>
          val (nextTerm, nextRest) = parseExpression(tail)
          (term.zip(nextTerm).map { case (a, b) => a - b }, nextRest) // Вычитание
        case _ => (term, rest)
      }
    }

    def parseTerm(tokens: List[Token]): (Expression[Int], List[Token]) = {
      val (factor, rest) = parseFactor(tokens)
      rest match {
        case Times() :: tail =>
          val (nextFactor, nextRest) = parseTerm(tail)
          (factor.zip(nextFactor).map { case (a, b) => a * b }, nextRest) // Умножение
        case Divide() :: tail =>
          val (nextFactor, nextRest) = parseTerm(tail)
          (factor.zip(nextFactor).map { case (a, b) => a / b }, nextRest) // Деление
        case _ => (factor, rest)
      }
    }

    def parseFactor(tokens: List[Token]): (Expression[Int], List[Token]) = {
      tokens match {
        case Number(value) :: rest => (Expression.value(value), rest)
        case LParen() :: rest =>
          val (expr, afterExpr) = parseExpression(rest)
          afterExpr match {
            case RParen() :: tail => (expr, tail)
            case _ => throw new RuntimeException("Expected closing parenthesis")
          }
        case _ => throw new RuntimeException("Unexpected token")
      }
    }

    parseExpression(tokens)._1
  }

  // Основной метод для парсинга строки
  def parseExpression(input: String): Expression[Int] = {
    val tokens = tokenize(input)
    parse(tokens)
  }

  def main(args: Array[String]): Unit = {
    val input = "(1*2*3+3*4*100/100*100/100*100/100*100/100)*1000+10000*(500-14)/20*200/1"
    val in_1 = "(1)"
    val expression = parseExpression(input)
    println("до этого ничего не должно быть")
    val result = expression.eval.value
    //val ex2  = parseExpression(in_1)
    //val res2 = ex2.eval.value
    //println(s"res2 =  $res2") // 1
    println(s"Результат вычисления: $result") // Ожидаемый результат: 10
  }
}

