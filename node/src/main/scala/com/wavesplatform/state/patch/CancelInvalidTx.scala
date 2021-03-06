package com.wavesplatform.state.patch

import com.wavesplatform.account.{Address, AddressScheme}
import com.wavesplatform.common.utils.EitherExt2
import com.wavesplatform.state.{Diff, LeaseBalance, Portfolio}

object CancelInvalidTx extends DiffPatchFactory {
  val height: Int = AddressScheme.current.chainId.toChar match {
    case 'L' => 450000
    case _   => 0
  }

  def apply(): Diff = {
    val addr1 = Address.fromString("3JcQHUDAzkprtFHX8DskQCys3upBYhPeJQq").explicitGet()
    val addr2 = Address.fromString("3JbHxyVNbEEJXMDuuR9kPeTmXn5BCDBSQp4").explicitGet()
    val addr3 = Address.fromString("3JqAYiRnuiJxdMVmdTUsxuTV39LXHR5JWXk").explicitGet()
    val addr4 = Address.fromString("3JtJ5Kf3XuAUiMhtDQasSPdpaG6X5WLa8cE").explicitGet()
    val addr5 = Address.fromString("3JwSzCsBpTZtqxkfdj3KuZgZJ33BcTBnqQr").explicitGet()

    val bal1 = 120000004131663L
    val bal2 = 50000003846140L

    val pfs = Map(
      addr1 -> Portfolio(-bal1, LeaseBalance(0L, 0L), Map.empty),
      addr2 -> Portfolio(-bal2, LeaseBalance(0L, 0L), Map.empty),
      addr3 -> Portfolio(bal1 + bal2, LeaseBalance(0L, 0L), Map.empty),
      addr4 -> Portfolio(110000000L),
      addr5 -> Portfolio(108000000L)
    )

    val diff = Diff.empty.copy(portfolios = pfs)
    diff
  }
}

object CancelInvalidTx2 extends DiffPatchFactory {
  val height: Int = AddressScheme.current.chainId.toChar match {
    case 'L' => 457100
    case _   => 0
  }

  def apply(): Diff = {
    val addr1 = Address.fromString("3JreB3JjQJgFfuz6ntFt76eTDUyv7hxdzMk").explicitGet()
    val pfs = Map(
      addr1 -> Portfolio(9593994L)
    )

    val diff = Diff.empty.copy(portfolios = pfs)
    diff
  }
}
