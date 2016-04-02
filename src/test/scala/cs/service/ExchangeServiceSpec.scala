package cs.service

import cs.Direction._
import cs.{Direction, Order}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by jay on 02/04/16.
  */
class ExchangeServiceSpec extends FlatSpec with Matchers {

  "ExchangeService" should "add an order" in {
    val exchangeService = new ExchangeService


    val order = Order(Direction.sell, "VOD.L", 1000, 100.2, "User1")
    exchangeService.addOrder(order)
  }

}
