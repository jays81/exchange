package cs.service

import cs.Direction._
import cs.dao.InMemoryExchangeDAO
import cs.{ExecutionResult, Direction, Order}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by jay on 02/04/16.
  */
class ExchangeServiceSpec extends FlatSpec with Matchers {

  "ExchangeService" should "add an order and should not have been executed" in {
    val exchangeService = new ExchangeService(new InMemoryExchangeDAO)


    val order = Order(1, Direction.sell, "VOD.L", 1000, 100.2, "User1")
    val executed = exchangeService.addOrder(order)

    executed should be (ExecutionResult(executed = false,
      orderDirection = Direction.sell,
      executionPrice = 100.2))
  }

  it should "add an order and should have been executed" in {
    val exchangeService = new ExchangeService(new InMemoryExchangeDAO)


    val order1 = Order(1,Direction.sell, "VOD.L", 1000, 100.2, "User1")
    val order2 = Order(2, Direction.buy, "VOD.L", 1000, 100.2, "User2")
    val executed1 = exchangeService.addOrder(order1)
    //first add should not have been executed
    executed1 should be (ExecutionResult(executed = false,
      orderDirection = Direction.sell,
      executionPrice = 100.2))

    val executed2 = exchangeService.addOrder(order2)
    //second add should have been executed
    executed2 should be (ExecutionResult(executed = true,
      orderDirection = Direction.buy,
      matchDirection = Direction.sell,
      matchPrice = 100.2,
      executionPrice = 100.2000))
  }


  it should "not execute the sell order as the sell price is greater than the buy price" in {
    val exchangeService = new ExchangeService(new InMemoryExchangeDAO)


    val order1 = Order(1, Direction.buy, "VOD.L", 1000, 99, "User1")
    val order2 = Order(2, Direction.buy, "VOD.L", 1000, 101, "User1")
    val order3 = Order(3, Direction.sell, "VOD.L", 1000, 102, "User2")
    val executed1 = exchangeService.addOrder(order1)
    //first add should not have been executed
    executed1 should be (ExecutionResult(executed = false,
      orderDirection =  Direction.buy,
      executionPrice = 99))

    val executed2 = exchangeService.addOrder(order2)
    //second add should not have been executed
    executed2 should be (ExecutionResult(executed = false,
      orderDirection = Direction.buy,
      executionPrice = 101))

    val executed3 = exchangeService.addOrder(order3)
    //third add should not have been executed
    executed3 should be (ExecutionResult(executed = false,
      orderDirection = Direction.sell,
      executionPrice = 102))
  }

  it should "new buy order should match an existing sell order" in {
    val exchangeService = new ExchangeService(new InMemoryExchangeDAO)


    val order1 = Order(1, Direction.buy, "VOD.L", 1000, 99, "User1")
    val order2 = Order(2, Direction.buy, "VOD.L", 1000, 101, "User1")
    val order3 = Order(3, Direction.sell, "VOD.L", 1000, 102, "User2")
    val order4 = Order(4, Direction.buy, "VOD.L", 1000, 103, "User1")
    val executed1 = exchangeService.addOrder(order1)
    //first add should not have been executed
    executed1 should be (ExecutionResult(executed = false,
      orderDirection = Direction.buy,
      executionPrice = 99))

    val executed2 = exchangeService.addOrder(order2)
    //second add should not have been executed
    executed2 should be (ExecutionResult(executed = false,
      orderDirection = Direction.buy,
      executionPrice = 101))

    val executed3 = exchangeService.addOrder(order3)
    //third add should not have been executed
    executed3 should be (ExecutionResult(executed = false,
      orderDirection = Direction.sell,
      executionPrice = 102))

    val executed4 = exchangeService.addOrder(order4)
    //4th add should have been executed
    executed4 should be (ExecutionResult(executed = true,
      orderDirection = Direction.buy,
      matchDirection = Direction.sell,
      matchPrice = 102,
      executionPrice = 103))

  }

  it should "match the highest price if there are multiple matching orders for a new sell order" in {
    val exchangeService = new ExchangeService(new InMemoryExchangeDAO)


    val order1 = Order(1, Direction.buy, "VOD.L", 1000, 99, "User1")
    val order2 = Order(2, Direction.buy, "VOD.L", 1000, 101, "User1")
    val order3 = Order(3, Direction.sell, "VOD.L", 1000, 102, "User2")
    val order4 = Order(4, Direction.buy, "VOD.L", 1000, 103, "User1")
    val order5 = Order(5, Direction.sell, "VOD.L", 1000, 98, "User2")
    val executed1 = exchangeService.addOrder(order1)
    //first add should not have been executed
    executed1 should be (ExecutionResult(executed = false,
      orderDirection = Direction.buy,
      executionPrice = 99))

    val executed2 = exchangeService.addOrder(order2)
    //second add should not have been executed
    executed2 should be (ExecutionResult(executed = false,
      orderDirection = Direction.buy,
      executionPrice = 101))

    val executed3 = exchangeService.addOrder(order3)
    //third add should not have been executed
    executed3 should be (ExecutionResult(executed = false,
      orderDirection = Direction.sell,
      executionPrice = 102))

    val executed4 = exchangeService.addOrder(order4)
    //4th add should have been executed
    executed4 should be (ExecutionResult(executed = true,
      orderDirection = Direction.buy,
      matchDirection = Direction.sell,
      matchPrice = 102,
      executionPrice = 103))

    val executed5 = exchangeService.addOrder(order5)
    //5th add should have been executed
    executed5 should be (ExecutionResult(executed = true,
      orderDirection = Direction.sell,
      matchDirection = Direction.buy,
      matchPrice = 101,
      executionPrice = 98))

  }

}
