package main.scala.utils

object StrUtil {

  /** if right = true then padding char in the right side
    * e.g.  padding(123,5) result is: 12300
    * and vice verse
    */
  def padding(s: String, width: Int = 3, wth: Char = '0', right: Boolean = false): String = {
    val padding = if(right) s"%-${width}s" else s"%${width}s"
    padding.format(s).replace(' ', wth)
  }

  /** recursively call splitAt
    * 123456789 -> Seq(123,456,789)
    */
  def recurSplitAt(s: String, at: Int = 3): Seq[String] ={
    val (s1,s2) = s.splitAt(at)
    if(s2.length > at) s1 +: recurSplitAt(s2) else Seq(s1,s2)
  }

  // human readable format, e.g. 1123452 turn to 1,223,452
  def readableFormat(s: String): String ={
    if(s.length <= 3) s
    else {
      val s1 = if(s.length % 3 == 0 ) s
      else {
        val width = (s.length / 3) * 3 + 3
        padding(s, width)
      }
      val res = recurSplitAt(s1)
      res map {_.toInt} mkString(",")
    }
  }

}
