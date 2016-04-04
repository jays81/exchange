package cs.service


import cs.{Direction, ExecutionResult, OpenInterest}
import org.scalatest.{FlatSpec, Matchers}


class ExchangeServiceSpec extends FlatSpec with ExchangeServiceFixture with Matchers {

  "ExchangeService" should "add an order and should not have been executed" in new ExchangeServiceFixture  {

    val order = "SELL 1000 VOD.L @ 100.2"
    val executed = exchangeService.addOrder(order, "User1")

    executed should be (Some(ExecutionResult(orderId = 1,
      executed = false,
      orderDirection = Direction.sell,
      executionPrice = 100.2)))
  }

  it should "not add an order if the order is not properly formatted" in new ExchangeServiceFixture  {

    val order = "hello 1000VOD.L @ 100.2"
    val executed = exchangeService.addOrder(order, "User1")

    executed should be (None)
  }

  it should "add an order and should have been executed" in new ExchangeServiceFixture {

    val order1 = "SELL 1000 VOD.L @ 100.2"
    val order2 = "BUY 1000 VOD.L @ 100.2"

    val executed1 = exchangeService.addOrder(order1, "User1")
    //first add should not have been executed
    executed1 should be (Some(ExecutionResult(orderId = 1,
      executed = false,
      orderDirection = Direction.sell,
      executionPrice = 100.2)))

    val executed2 = exchangeService.addOrder(order2, "User2")
    //second add should have been executed
    executed2 should be (Some(ExecutionResult(orderId = 2,
      matchedOrderId = 1,
      executed = true,
      orderDirection = Direction.buy,
      matchDirection = Direction.sell,
      matchPrice = 100.2,
      executionPrice = 100.2000)))
  }


  it should "not execute the sell order as the sell price is greater than the buy price" in new ExchangeServiceFixture {

    val order1 = "BUY 1000 VOD.L @ 99"
    val order2 = "BUY 1000 VOD.L @ 101"
    val order3 = "SELL 500 VOD.L @ 102"

    val executed1 = exchangeService.addOrder(order1, "User1")
    //first add should not have been executed
    executed1 should be (Some(ExecutionResult(orderId = 1,
      executed = false,
      orderDirection =  Direction.buy,
      executionPrice = 99)))

    val executed2 = exchangeService.addOrder(order2, "User1")
    //second add should not have been executed
    executed2 should be (Some(ExecutionResult(orderId = 2,
      executed = false,
      orderDirection = Direction.buy,
      executionPrice = 101)))

    val executed3 = exchangeService.addOrder(order3, "User3")
    //third add should not have been executed
    executed3 should be (Some(ExecutionResult(orderId = 3,
      executed = false,
      orderDirection = Direction.sell,
      executionPrice = 102)))
  }

  it should "new buy order should match an existing sell order" in new ExchangeServiceFixture {

    val order1 = "BUY 1000 VOD.L @ 99"
    val order2 = "BUY 1000 VOD.L @ 101"
    val order3 = "SELL 500 VOD.L @ 102"
    val order4 = "BUY 500 VOD.L @ 103"

    val executed1 = exchangeService.addOrder(order1, "User1")
    //first add should not have been executed
    executed1 should be (Some(ExecutionResult(orderId = 1,
      executed = false,
      orderDirection = Direction.buy,
      executionPrice = 99)))

    val executed2 = exchangeService.addOrder(order2, "User1")
    //second add should not have been executed
    executed2 should be (Some(ExecutionResult(orderId = 2,
      executed = false,
      orderDirection = Direction.buy,
      executionPrice = 101)))

    val executed3 = exchangeService.addOrder(order3, "User2")
    //third add should not have been executed
    executed3 should be (Some(ExecutionResult(orderId = 3,
      executed = false,
      orderDirection = Direction.sell,
      executionPrice = 102)))

    val executed4 = exchangeService.addOrder(order4, "User1")
    //4th add should have been executed
    executed4 should be (Some(ExecutionResult(orderId = 4,
      matchedOrderId = 3,
      executed = true,
      orderDirection = Direction.buy,
      matchDirection = Direction.sell,
      matchPrice = 102,
      executionPrice = 103)))

  }

  it should "match the highest price if there are multiple matching orders for a new sell order" in new ExchangeServiceFixture {

    val order1 = "BUY 1000 VOD.L @ 99"
    val order2 = "BUY 1000 VOD.L @ 101"
    val order3 = "SELL 500 VOD.L @ 102"
    val order4 = "BUY 500 VOD.L @ 103"
    val order5 = "SELL 1000 VOD.L @ 98"

    val executed1 = exchangeService.addOrder(order1, "User1")
    //first add should not have been executed
    executed1 should be (Some(ExecutionResult(orderId = 1,
      executed = false,
      orderDirection = Direction.buy,
      executionPrice = 99)))

    val executed2 = exchangeService.addOrder(order2, "User1")
    //second add should not have been executed
    executed2 should be (Some(ExecutionResult(orderId = 2,
      executed = false,
      orderDirection = Direction.buy,
      executionPrice = 101)))

    val executed3 = exchangeService.addOrder(order3, "User2")
    //third add should not have been executed
    executed3 should be (Some(ExecutionResult(orderId = 3,
      executed = false,
      orderDirection = Direction.sell,
      executionPrice = 102)))

    val executed4 = exchangeService.addOrder(order4, "User1")
    //4th add should have been executed
    executed4 should be (Some(ExecutionResult(orderId = 4,
      matchedOrderId = 3,
      executed = true,
      orderDirection = Direction.buy,
      matchDirection = Direction.sell,
      matchPrice = 102,
      executionPrice = 103)))

    val executed5 = exchangeService.addOrder(order5, "user2")
    //5th add should have been executed
    executed5 should be (Some(ExecutionResult(orderId = 5,
      matchedOrderId = 2,
      executed = true,
      orderDirection = Direction.sell,
      matchDirection = Direction.buy,
      matchPrice = 101,
      executionPrice = 98)))

  }


  it should "match the lowest price if there are multiple matching orders for a new buy order" in new ExchangeServiceFixture {


    val order1 = "SELL 1000 VOD.L @ 97"
    val order2 = "SELL 1000 VOD.L @ 102"
    val order3 = "SELL 1000 VOD.L @ 101"
    val order4 = "BUY 1000 VOD.L @ 103"

    val executed1 = exchangeService.addOrder(order1, "User1")
    //first add should not have been executed
    executed1 should be (Some(ExecutionResult(orderId = 1,
      executed = false,
      orderDirection = Direction.sell,
      executionPrice = 97)))

    val executed2 = exchangeService.addOrder(order2, "User1")
    //second add should not have been executed
    executed2 should be (Some(ExecutionResult(orderId = 2,
      executed = false,
      orderDirection = Direction.sell,
      executionPrice = 102)))

    val executed3 = exchangeService.addOrder(order3, "User2")
    //third add should not have been executed
    executed3 should be (Some(ExecutionResult(orderId = 3,
      executed = false,
      orderDirection = Direction.sell,
      executionPrice = 101)))

    val executed4 = exchangeService.addOrder(order4, "User1")
    //4th add should have been executed
    executed4 should be (Some(ExecutionResult(orderId = 4,
      matchedOrderId = 1,
      executed = true,
      orderDirection = Direction.buy,
      matchDirection = Direction.sell,
      matchPrice = 97,
      executionPrice = 103)))

  }



  it should "match the earlist lowest best price if there are multiple matching orders for a new buy order" in new ExchangeServiceFixture {


    val order1 = "SELL 1000 VOD.L @ 97"
    val order2 = "SELL 1000 VOD.L @ 102"
    val order3 = "SELL 1000 VOD.L @ 101"
    val order4 = "SELL 1000 VOD.L @ 97"
    val order5 = "BUY 1000 VOD.L @ 103"

    val executed1 = exchangeService.addOrder(order1, "User1")
    //first add should not have been executed
    executed1 should be (Some(ExecutionResult(orderId = 1,
      executed = false,
      orderDirection = Direction.sell,
      executionPrice = 97)))

    val executed2 = exchangeService.addOrder(order2, "User1")
    //second add should not have been executed
    executed2 should be (Some(ExecutionResult(orderId = 2,
      executed = false,
      orderDirection = Direction.sell,
      executionPrice = 102)))

    val executed3 = exchangeService.addOrder(order3, "User2")
    //third add should not have been executed
    executed3 should be (Some(ExecutionResult(orderId = 3,
      executed = false,
      orderDirection = Direction.sell,
      executionPrice = 101)))

    val executed4 = exchangeService.addOrder(order4, "User2")
    //third add should not have been executed
    executed4 should be (Some(ExecutionResult(orderId = 4,
      executed = false,
      orderDirection = Direction.sell,
      executionPrice = 97)))

    val executed5 = exchangeService.addOrder(order5, "User1")
    //4th add should have been executed
    executed5 should be (Some(ExecutionResult(orderId = 5,
      matchedOrderId = 1,
      executed = true,
      orderDirection = Direction.buy,
      matchDirection = Direction.sell,
      matchPrice = 97,
      executionPrice = 103)))

  }

  it should "provide open interest for a given RIC and direction" in new ExchangeServiceFixture {
    val order1 = "BUY 1000 VOD.L @ 99"
    val order2 = "BUY 1000 VOD.L @ 101"
    val order3 = "SELL 500 VOD.L @ 102"
    val order4 = "BUY 500 VOD.L @ 103"
    val order5 = "SELL 1000 VOD.L @ 98"

    val executed1 = exchangeService.addOrder(order1, "User1")
    //first add should not have been executed
    executed1 should be (Some(ExecutionResult(orderId = 1,
      executed = false,
      orderDirection = Direction.buy,
      executionPrice = 99)))

    //check open interest for buy and sell after 1st add
    val openInterestBuy1 = exchangeService.getOpenInterest("VOD.L", Direction.buy)
    openInterestBuy1 should be (Seq(OpenInterest(1000, 99)))
    val openInterestSell1 = exchangeService.getOpenInterest("VOD.L", Direction.sell)
    openInterestSell1 should be (Seq())

    val executed2 = exchangeService.addOrder(order2, "User1")
    //second add should not have been executed
    executed2 should be (Some(ExecutionResult(orderId = 2,
      executed = false,
      orderDirection = Direction.buy,
      executionPrice = 101)))

    //check open interest for buy and sell after 2nd add
    val openInterest2 = exchangeService.getOpenInterest("VOD.L", Direction.buy)
    openInterest2 should be (Seq(OpenInterest(1000, 101),
      OpenInterest(1000, 99)))
    val openInterestSell2 = exchangeService.getOpenInterest("VOD.L", Direction.sell)
    openInterestSell2 should be (Seq())


    val executed3 = exchangeService.addOrder(order3, "User2")
    //third add should not have been executed
    executed3 should be (Some(ExecutionResult(orderId = 3,
      executed = false,
      orderDirection = Direction.sell,
      executionPrice = 102)))

    //check open interest for buy and sell after 3rd add
    val openInterest3 = exchangeService.getOpenInterest("VOD.L", Direction.buy)
    openInterest3 should be (Seq(OpenInterest(1000, 101),
      OpenInterest(1000, 99)))
    val openInterestSell3 = exchangeService.getOpenInterest("VOD.L", Direction.sell)
        openInterestSell3 should be (Seq(OpenInterest(500, 102)))



    val executed4 = exchangeService.addOrder(order4, "User1")
    //4th add should have been executed
    executed4 should be (Some(ExecutionResult(orderId = 4,
      matchedOrderId = 3,
      executed = true,
      orderDirection = Direction.buy,
      matchDirection = Direction.sell,
      matchPrice = 102,
      executionPrice = 103)))

    //check open interest for buy and sell after 4th add
    val openInterest4 = exchangeService.getOpenInterest("VOD.L", Direction.buy)
    openInterest3 should be (Seq(OpenInterest(1000, 101),
      OpenInterest(1000, 99)))
    val openInterestSell4 = exchangeService.getOpenInterest("VOD.L", Direction.sell)
    openInterestSell4 should be (Seq())

    val executed5 = exchangeService.addOrder(order5, "user2")
    //5th add should have been executed
    executed5 should be (Some(ExecutionResult(orderId = 5,
      matchedOrderId = 2,
      executed = true,
      orderDirection = Direction.sell,
      matchDirection = Direction.buy,
      matchPrice = 101,
      executionPrice = 98)))

    //check open interest for buy and sell after 5th add
    val openInterest5 = exchangeService.getOpenInterest("VOD.L", Direction.buy)
    openInterest5 should be (Seq(OpenInterest(1000, 99)))
    val openInterestSell5 = exchangeService.getOpenInterest("VOD.L", Direction.sell)
    openInterestSell5 should be (Seq())
  }

  it should "provide average execution price for a given RIC" in new ExchangeServiceFixture {
    val order1 = "SELL 1000 VOD.L @ 100.2"
    val order2 = "BUY 1000 VOD.L @ 100.2"
    val order3 = "BUY 1000 VOD.L @ 99"
    val order4 = "BUY 1000 VOD.L @ 101"
    val order5 = "SELL 500 VOD.L @ 102"
    val order6 = "BUY 500 VOD.L @ 103"
    val order7 = "SELL 1000 VOD.L @ 98"



    val executed1 = exchangeService.addOrder(order1, "User1")
    val averageExcecutionPrice1 = exchangeService.getAverageExecutionPrice("VOD.L")
    averageExcecutionPrice1 should be (None)

    val executed2 = exchangeService.addOrder(order2, "User2")
    val averageExcecutionPrice2 = exchangeService.getAverageExecutionPrice("VOD.L")
    averageExcecutionPrice2 should be (Some(100.2000))

    val executed3 = exchangeService.addOrder(order3, "User1")
    val averageExcecutionPrice3 = exchangeService.getAverageExecutionPrice("VOD.L")
    averageExcecutionPrice3 should be (Some(100.2000))

    val executed4 = exchangeService.addOrder(order4, "User1")
    val averageExcecutionPrice4 = exchangeService.getAverageExecutionPrice("VOD.L")
    averageExcecutionPrice4 should be (Some(100.2000))

    val executed5 = exchangeService.addOrder(order5, "User2")
    val averageExcecutionPrice5 = exchangeService.getAverageExecutionPrice("VOD.L")
    averageExcecutionPrice5 should be (Some(100.2000))

    val executed6 = exchangeService.addOrder(order6, "User1")
    val averageExcecutionPrice6 = exchangeService.getAverageExecutionPrice("VOD.L")
    averageExcecutionPrice6 should be (Some(101.1333))

    val executed7 = exchangeService.addOrder(order7, "User2")

    val averageExcecutionPrice7 = exchangeService.getAverageExecutionPrice("VOD.L")
    averageExcecutionPrice7 should be (Some(99.8800))

  }


  it should "provide executed quantity for a given RIC and user" in new ExchangeServiceFixture {
    val order1 = "SELL 1000 VOD.L @ 100.2"
    val order2 = "BUY 1000 VOD.L @ 100.2"
    val order3 = "BUY 1000 VOD.L @ 99"
    val order4 = "BUY 1000 VOD.L @ 101"
    val order5 = "SELL 500 VOD.L @ 102"
    val order6 = "BUY 500 VOD.L @ 103"
    val order7 = "SELL 1000 VOD.L @ 98"



    val executed1 = exchangeService.addOrder(order1, "User1")
    val executedQty1User1 = exchangeService.getExecutedQuantity("VOD.L", "User1")
    executedQty1User1 should be (Some(0))

    val executedQty1User2 = exchangeService.getExecutedQuantity("VOD.L", "User2")
    executedQty1User2 should be (Some(0))

    val executed2 = exchangeService.addOrder(order2, "User2")
    val executedQty2User1 = exchangeService.getExecutedQuantity("VOD.L", "User1")
    executedQty2User1 should be (Some(-1000))

    val executedQty2User2 = exchangeService.getExecutedQuantity("VOD.L", "User2")
    executedQty2User2 should be (Some(1000))


    val executed3 = exchangeService.addOrder(order3, "User1")
    val executedQty3User1 = exchangeService.getExecutedQuantity("VOD.L", "User1")
    executedQty3User1 should be (Some(-1000))

    val executedQty3User2 = exchangeService.getExecutedQuantity("VOD.L", "User2")
    executedQty3User2 should be (Some(1000))

    val executed4 = exchangeService.addOrder(order4, "User1")
    val executedQty4User1 = exchangeService.getExecutedQuantity("VOD.L", "User1")
    executedQty4User1 should be (Some(-1000))

    val executedQty4User2 = exchangeService.getExecutedQuantity("VOD.L", "User2")
    executedQty4User2 should be (Some(1000))

    val executed5 = exchangeService.addOrder(order5, "User2")
    val executedQty5User1 = exchangeService.getExecutedQuantity("VOD.L", "User1")
    executedQty5User1 should be (Some(-1000))

    val executedQty5User2 = exchangeService.getExecutedQuantity("VOD.L", "User2")
    executedQty5User2 should be (Some(1000))

    val executed6 = exchangeService.addOrder(order6, "User1")
    val executedQty6User1 = exchangeService.getExecutedQuantity("VOD.L", "User1")
    executedQty6User1 should be (Some(-500))

    val executedQty6User2 = exchangeService.getExecutedQuantity("VOD.L", "User2")
    executedQty6User2 should be (Some(500))

    val executed7 = exchangeService.addOrder(order7, "User2")
    val executedQty7User1 = exchangeService.getExecutedQuantity("VOD.L", "User1")
    executedQty7User1 should be (Some(500))

    val executedQty7User2 = exchangeService.getExecutedQuantity("VOD.L", "User2")
    executedQty7User2 should be (Some(-500))


  }

}
