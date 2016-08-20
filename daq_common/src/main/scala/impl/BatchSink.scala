package main.scala.impl

import main.scala.core.SinkProvider
import main.scala.model.EntityCollection
import main.scala.utils.Log
import scala.collection.mutable.ListBuffer
import main.scala.utils.TimeUtil._

class BatchSink(sinkProvider: SinkProvider, batchSize: Int, status: SinkStatus) extends Log {

  var cacheSize = 0
  val batchEle = new ListBuffer[EntityCollection]

  // synchronized because there have multiple sink routee running in the pool.
  def insert(ele: EntityCollection): Unit = synchronized {
    batchEle += ele
    cacheSize += ele.entities.size
    if (cacheSize >= batchSize)
      flush()
  }

  def flush(): Unit = synchronized {
    if (batchEle.nonEmpty) {
      val (time, _) = timer {
        val batched = EntityCollection.reduce(batchEle)

        sinkProvider.insert(batched)
        batchEle.clear()
        cacheSize = 0

        status.recordInsert(batched.size)
        log.info(s"Flush ${batched.size} EntityCollection, at thread: ${Thread.currentThread().getName}")
      }
      status.recordFlush(time)
    } else {
      log.warn(s"no batched object need to flush")
      status.recordFlush(0)
    }
  }

}

trait SinkStatus {
  def recordInsert(num: Int): Unit

  def recordFlush(time: Long): Unit
}
