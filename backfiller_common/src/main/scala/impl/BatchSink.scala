package main.scala.impl

import com.codahale.metrics.Clock
import main.scala.core.SinkProvider
import main.scala.model.EntityCollection
import main.scala.utils.Log
import scala.collection.mutable.ListBuffer


class BatchSink(sinkProvider: SinkProvider, batchSize: Int, status: SinkStatus) extends Log {

  var cacheSize = 0
  val batchEle = new ListBuffer[EntityCollection]
  val clock = Clock.defaultClock()

//    @NotThreadSafe
  def insert(ele: EntityCollection): Unit = {
    batchEle += ele
    cacheSize += ele.entities.size
    if (cacheSize >= batchSize)
      flush()
  }

//  @NotThreadSafe
  def flush(): Unit = {
    val start = clock.getTick
    if (batchEle.nonEmpty) {
      val batched = EntityCollection.reduce(batchEle)

      sinkProvider.insert(batched)
      batchEle.clear()
      cacheSize = 0

      status.recordInsert(batched.size)
      log.info(s"Flush ${batched.size} EntityCollection into DB/File at thread: ${Thread.currentThread().getName}")
      val time = clock.getTick - start
      status.recordFlush(time)
    } else {
      log.warn(s"non batched object need to flush")
      status.recordFlush(0)
    }
  }

}

trait SinkStatus {
  def recordInsert(num: Int): Unit

  def recordFlush(time: Long): Unit
}
