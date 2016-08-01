package main.scala.utils


object RetryLogic {


  // TODO  has to implement in the future
  def retry[In, Out](arg: In, f: In => Out, tryTime: Int = 5): Out = {
    f(arg)
  }

  def tryOnce[Out](f: => Out): Out = {
    f
  }

}
