package cs.service

import cs.{Direction, Order}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by jay on 02/04/16.
  */
class OrderParserSpec extends FlatSpec with Matchers {

  "OrderParse" should "parse a valid sell order" in {
    val orderToParse = "SELL 1000 VOD.L @ 100.2"
    val orderParser = new OrderParser

    val parsedOrder = orderParser.parseOrder(Some(orderToParse), "User1", 1)

    parsedOrder should be (Some(Order(1, Direction.sell, "VOD.L", 1000, 100.2, "User1")))
  }

  it should "parse a valid buy order" in {
    val orderToParse = "BUY 1000 VOD.L @ 99"
    val orderParser = new OrderParser

    val parsedOrder = orderParser.parseOrder(Some(orderToParse), "User1", 2)

    parsedOrder should be (Some(Order(2, Direction.buy, "VOD.L", 1000, 99, "User1")))
  }

  it should "not parse an invalid order" in {
    val orderToParse = "hello 1000 VOD.L @ 100.2"
    val orderParser = new OrderParser

    val parsedOrder = orderParser.parseOrder(Some(orderToParse), "User1", 3)
    parsedOrder should be (None)
  }


}
