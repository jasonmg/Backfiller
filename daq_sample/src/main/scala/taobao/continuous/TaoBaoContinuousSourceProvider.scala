package main.scala.taobao.continuous

import main.scala.core.SourceProvider
import main.scala.core.continuous.ContinuousSlice
import main.scala.taobao.model.TaoBaoEntity
import main.scala.taobao.model.Sex._
import scala.util.Random

class TaoBaoContinuousSourceProvider extends SourceProvider[TaoBaoEntity, ContinuousSlice]{
  import TaoBaoContinuousSourceProvider._

  private val r = Random

  def load(sourceArg: ContinuousSlice): Seq[TaoBaoEntity] = {
    val id = r.nextLong()
    val age = r.nextInt(100)
    val sex = if(r.nextBoolean()) Male else Female
    val marriage = r.nextBoolean()
    val name = s"${lastNameSeq.apply(r.nextInt(lastNameSize))} ${firstNameSeq.apply(r.nextInt(firstNameSize))}"
    val country = countrySeq.apply(r.nextInt(countrySize))

    Seq(TaoBaoEntity(id,name,age,sex,marriage,address,country))
  }
}

object TaoBaoContinuousSourceProvider{
   val countrySeq = Seq("China","USA","UK","japan","China-TaiWan","China-HK")
   val countrySize = countrySeq.size
   val lastNameSeq = Seq("Ji","Wang","Zhang","Tian","Li","Zhao","Lou","Chen","Yan","Jia","Cai","Tan")
   val lastNameSize = lastNameSeq.size
   val firstNameSeq = Seq("MingTian","XiWang","Kevin","Ron","Tao","Dan","ShaoHua","Bao","Xing","YiFan","MingXing","MingMing","BingChen","GouDan")
   val firstNameSize = firstNameSeq.size

  val address = "N/A"
}
