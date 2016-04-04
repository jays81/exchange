package cs.service

import java.util.concurrent.atomic.AtomicInteger


class SimpleIdGenerator extends IdGenerator {

  private val generator = new AtomicInteger

  override def generateId: Int = generator.incrementAndGet()
}
