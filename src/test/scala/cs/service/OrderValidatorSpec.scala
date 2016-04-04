package cs.service

import cs.dao.InMemoryExchangeDAO
import cs.{Direction, ExecutionResult, Order}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by jay on 02/04/16.
  */
class OrderValidatorSpec extends FlatSpec with Matchers {

  "OrderValidator" should "validate a valid sell order" in {
    val orderToValidate = "SELL 1000 VOD.L @ 100.2"
    val orderValidator = new OrderValidator

    val result = orderValidator.validateOrder(Some(orderToValidate))
    result should be (true)
  }

  it should "validate a valid buy order" in {
    val orderToValidate = "BUY 1000 VOD.L @ 99"
    val orderValidator = new OrderValidator

    val result = orderValidator.validateOrder(Some(orderToValidate))
    result should be (true)
  }

  it should "not validate an invalid order" in {
    val orderToValidate = "hello 1000 VOPD.L @ 100.2"
    val orderValidator = new OrderValidator

    val result = orderValidator.validateOrder(Some(orderToValidate))
    result should be (false)
  }


}
