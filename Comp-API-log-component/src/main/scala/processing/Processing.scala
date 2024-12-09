package processing

import FParser.ExpressionParser


def process(record : String): String = {
  val code : String = extractBeforeColon(record)
  val input : String = extractAfterColon(record)
  val expression = ExpressionParser.parseExpression(input)
  val result: List[Int] = expression.eval.value
  code.+(":").+(result)
}


def extractAfterColon(input: String): String = {
  val parts = input.split(":")
  if (parts.length > 1) {
    parts(1).trim 
  } else {
    "" 
  }
}

def extractBeforeColon(input: String): String = {
  val parts = input.split(":")
  if (parts.length > 1) {
    parts(0).trim
  } else {
    ""
  }
}



