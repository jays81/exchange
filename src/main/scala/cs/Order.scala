package cs

import cs.Direction.Direction


case class Order(id:Int, direction:Direction, ric:String, quantity:Int, price:BigDecimal, user:String, executed: Boolean = false) {

}
