package com.jason.core.continuous

import com.jason.core.{BackfillerPlugin, SliceProvider}
import com.jason.core.SliceProvider

import scala.collection.immutable.Stream

class ContinuousSlice()

/** continuous backfill also know as real-time backfill,
  * most of times we want our program could monitor market data in real-time
  * and catch the changes immediately, that user can reactive base on the environment wave,
  * for business perspective this is necessary.
  */
abstract class BackfillerContinuousPlugin[In](cmdLine: BackfillerContinuousArgs) extends BackfillerPlugin[In, BackfillerContinuousArgs, ContinuousSlice](cmdLine){
  def sliceProvider = new ContinuousSliceProvider()
  override def isContinuous: Boolean = true
}

class ContinuousSliceProvider extends SliceProvider[BackfillerContinuousArgs, ContinuousSlice]{
  def slice(args: BackfillerContinuousArgs): Stream[ContinuousSlice] =
    Stream.continually(new ContinuousSlice)
}

