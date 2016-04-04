package cs.service

import java.util.concurrent.atomic.AtomicInteger

/**
  * Created by jay on 04/04/16.
  */
class SimpleIdGenerator extends IdGenerator {

  private val generator = new AtomicInteger

  override def generateId: Int = generator.incrementAndGet()
}
