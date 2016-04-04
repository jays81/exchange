package cs

import cs.Direction.Direction


case class ExecutionResult(orderId:Int, matchedOrderId:Int = -1, executed:Boolean, orderDirection: Direction, matchDirection:Direction = Direction.none, matchPrice:BigDecimal = 0, executionPrice:BigDecimal) {

}
