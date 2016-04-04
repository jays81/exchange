package cs.service


import java.math.MathContext

import cs.{Direction, Order}



/**
  * parses and creates an Order from an order message
  * assumes that the order to parse has been validated and the delimiter is a space
  */
class OrderParser {


  private val orderFields = Seq(OrderParserConstants.DIRECTION, OrderParserConstants.UNITS, OrderParserConstants.RIC, OrderParserConstants.PRICE)

  def parseOrder(orderToParse: Option[String], user:String, id:Int): Option[Order] = {
    orderToParse.flatMap(order => doParse(order, user, id))
  }

  private def doParse(orderToParse: String, user: String, id:Int): Option[Order] = {
    val splitOrderToParse = orderToParse.split(" ")
    val filteredOrderValues = splitOrderToParse.filter(orderField => orderField != "@")
    val mappedValues = orderFields.zip(filteredOrderValues).toMap
    try {
      Some(Order(id, Direction.withName(mappedValues(OrderParserConstants.DIRECTION).toLowerCase),
        mappedValues(OrderParserConstants.RIC),
        mappedValues(OrderParserConstants.UNITS).toInt,
        BigDecimal(mappedValues(OrderParserConstants.PRICE), new MathContext(4)),
        user))
    } catch{
      case ex: Exception => None
    }

  }
}

object OrderParserConstants {

  val DIRECTION = "DIRECTION"
  val UNITS = "UNITS"
  val RIC = "RIC"
  val PRICE = "PRICE"

}