package com.wavesplatform

import org.scalacheck.{Arbitrary, Gen}
import scodec.bits.ByteVector
import scorex.transaction.smart.lang.Terms._

trait ScriptGen {

  private def CONST_INTgen: Gen[Term] = Gen.choose(Int.MinValue, Int.MaxValue).map(CONST_INT)

  private def HEIGHTgen: Gen[Term] = Gen.const(HEIGHT)

  private def SUMgen(gas: Int): Gen[Term] = for {
    i1 <- INTGen((gas - 2) / 2)
    i2 <- INTGen((gas - 2) / 2)
  } yield SUM(i1, i2)

  private def INTGen(gas: Int): Gen[Term] = if (gas > 0) Gen.oneOf(HEIGHTgen, CONST_INTgen, SUMgen(gas - 1), IF_INTgen(gas - 1)) else Gen.oneOf(HEIGHTgen, CONST_INTgen)

  private def GEgen(gas: Int): Gen[Term] = for {
    i1 <- INTGen((gas - 2) / 2)
    i2 <- INTGen((gas - 2) / 2)
  } yield GE(i1, i2)


  private def GTgen(gas: Int): Gen[GT] = for {
    i1 <- INTGen((gas - 2) / 2)
    i2 <- INTGen((gas - 2) / 2)
  } yield GT(i1, i2)

  private def EQ_INTgen(gas: Int): Gen[EQ_INT] = for {
    i1 <- INTGen((gas - 2) / 2)
    i2 <- INTGen((gas - 2) / 2)
  } yield EQ_INT(i1, i2)

  private def ANDgen(gas: Int): Gen[AND] = for {
    i1 <- BOOLgen((gas - 2) / 2)
    i2 <- BOOLgen((gas - 2) / 2)
  } yield AND(i1, i2)


  private def ORgen(gas: Int): Gen[OR] = for {
    i1 <- BOOLgen((gas - 2) / 2)
    i2 <- BOOLgen((gas - 2) / 2)
  } yield OR(i1, i2)

  private def SIG_VERIFYgen: Gen[SIG_VERIFY] = for {
    x <- CONST_BYTEVECTORgen
    y <- CONST_BYTEVECTORgen
    z <- CONST_BYTEVECTORgen
  } yield SIG_VERIFY(x, y, z)

  def BOOLgen(gas: Int): Gen[Term] = if (gas > 0) Gen.oneOf(GEgen(gas - 1), GTgen(gas - 1), EQ_INTgen(gas - 1), ANDgen(gas - 1), ORgen(gas - 1), IF_BOOLgen(gas - 1), SIG_VERIFYgen) else SIG_VERIFYgen

  private  def CONST_BYTEVECTORgen: Gen[CONST_BYTEVECTOR] = Gen.choose(0, 100).flatMap(l => Gen.containerOfN[Array, Byte](l, Arbitrary.arbitrary[Byte])).map(bs => CONST_BYTEVECTOR(ByteVector(bs)))

  private def IF_BOOLgen(gas: Int): Gen[Term] = for {
    cnd <- BOOLgen((gas - 3) / 3)
    t <- BOOLgen((gas - 3) / 3)
    f <- BOOLgen((gas - 3) / 3)
  } yield IF(cnd, t, f)

  private def IF_INTgen(gas: Int): Gen[Term] = for {
    cnd <- BOOLgen((gas - 3) / 3)
    t <- INTGen((gas - 3) / 3)
    f <- INTGen((gas - 3) / 3)
  } yield IF(cnd, t, f)

}
