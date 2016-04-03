package cs.service

import cs.Direction._
import cs.{ExecutionResult, Direction, Order}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by jay on 02/04/16.
  */
class ExchangeServiceSpec extends FlatSpec with Matchers {

  "ExchangeService" should "add an order and should not have been executed" in {
    val exchangeService = new ExchangeService


    val order = Order(Direction.sell, "VOD.L", 1000, 100.2, "User1")
    val executed = exchangeService.addOrder(order)

    executed should be (ExecutionResult(false, order))
  }

  it should "add an order and should have been executed" in {
    val exchangeService = new ExchangeService


    val order1 = Order(Direction.sell, "VOD.L", 1000, 100.2, "User1")
    val order2 = Order(Direction.buy, "VOD.L", 1000, 100.2, "User2")
    val executed1 = exchangeService.addOrder(order1)
    //first add should not have been executed
    executed1 should be (ExecutionResult(false, order1))

    val executed2 = exchangeService.addOrder(order2)
    //first add should not have been executed
    executed2 should be (ExecutionResult(true, order2))
  }

}
