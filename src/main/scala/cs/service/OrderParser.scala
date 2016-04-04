package cs.service


import cs.{Direction, Order}



/**
  * parses and creates an Order from an order message
  * assumes that the order to parse has been validated and the delimiter is a space
  */
class OrderParser {



  private val orderFields = Seq(OrderParserConstants.DIRECTION, OrderParserConstants.UNITS, OrderParserConstants.RIC, OrderParserConstants.PRICE)

  def parseOrder(orderToParse: Option[String], user:String): Option[Order] = {
    orderToParse.flatMap(order => doParse(order, user))
  }

  private def doParse(orderToParse: String, user: String): Option[Order] = {
    val splitOrderToParse = orderToParse.split(" ")
    splitOrderToParse.foreach(s => println(s))


    val filteredOrderValues = splitOrderToParse.filter(orderField => orderField != "@")
    val mappedValues = orderFields.zip(filteredOrderValues).toMap
    println(mappedValues)

    try {
      Some(Order(Direction.withName(mappedValues(OrderParserConstants.DIRECTION).toLowerCase),
        mappedValues(OrderParserConstants.RIC),
        mappedValues(OrderParserConstants.UNITS).toInt,
        BigDecimal(mappedValues(OrderParserConstants.PRICE)),
        user))
    } catch{
      case ex: Exception => {
        None
      }
    }

  }
}

object OrderParserConstants {

  val DIRECTION = "DIRECTION"
  val UNITS = "UNITS"
  val RIC = "RIC"
  val PRICE = "PRICE"

}