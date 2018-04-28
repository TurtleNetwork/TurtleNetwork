package scorex.transaction

import com.google.common.primitives.Bytes
import com.wavesplatform.crypto
import com.wavesplatform.state.ByteStr
import monix.eval.Coeval
import scorex.account._
import scorex.crypto.signatures.Curve25519.SignatureLength

import scala.util.{Failure, Success, Try}

case class CreateAliasTransactionV1 private (sender: PublicKeyAccount, alias: Alias, fee: Long, timestamp: Long, signature: ByteStr)
    extends CreateAliasTransaction
    with SignedTransaction {

  override def version: Byte              = 1
  override val builder: TransactionParser = CreateAliasTransactionV1

  override val id: Coeval[AssetId] =
    Coeval.evalOnce(ByteStr(crypto.fastHash(builder.typeId +: alias.bytes.arr)))

  override val bodyBytes: Coeval[Array[Byte]] =
    baseBytes.map(base => Bytes.concat(Array(builder.typeId), base))

  override val bytes: Coeval[Array[Byte]] =
    bodyBytes.map(body => Bytes.concat(body, signature.arr))
}

object CreateAliasTransactionV1 extends TransactionParserFor[CreateAliasTransactionV1] with TransactionParser.HardcodedVersion1 {

  override val typeId: Byte = CreateAliasTransaction.typeId

  override protected def parseTail(version: Byte, bytes: Array[Byte]): Try[TransactionT] =
    Try {
      for {
        (sender, alias, fee, timestamp, end) <- CreateAliasTransaction.parseBase(0, bytes)
        signature = ByteStr(bytes.slice(end, end + SignatureLength))
        tx <- CreateAliasTransactionV1
          .create(sender, alias, fee, timestamp, signature)
          .fold(left => Failure(new Exception(left.toString)), right => Success(right))
      } yield tx
    }.flatten

  def create(sender: PublicKeyAccount, alias: Alias, fee: Long, timestamp: Long, signature: ByteStr): Either[ValidationError, TransactionT] =
    if (fee <= 0) {
      Left(ValidationError.InsufficientFee())
    } else {
      Right(CreateAliasTransactionV1(sender, alias, fee, timestamp, signature))
    }

  def create(sender: PrivateKeyAccount, alias: Alias, fee: Long, timestamp: Long): Either[ValidationError, TransactionT] = {
    create(sender, alias, fee, timestamp, ByteStr.empty).right.map { unsigned =>
      unsigned.copy(signature = ByteStr(crypto.sign(sender, unsigned.bodyBytes())))
    }
  }
}
