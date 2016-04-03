package cs

import cs.Direction.Direction

/**
 * Created by jay on 03/04/16.
 */
case class ExecutionResult(executed:Boolean, orderDirection: Direction, matchDirection:Direction = Direction.none, matchPrice:BigDecimal = 0, executionPrice:BigDecimal) {

}
