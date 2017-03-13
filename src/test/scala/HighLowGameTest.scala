import org.scalatest._

class AnotherSpec extends FlatSpec with Matchers {

  "A HighLowGame" should "draw a different card" in {
    val g = new HighLowGame()
    g.lastCard should not be (g.drawCard())
  }

  "A HighLowGame" should "rank aces as 13" in {
    val g = new HighLowGame()
    g.numericRank(0.toByte) should be (13)
  }

  "A HighLowGame" should "compare 4 higher than 3" in {
    val g = new HighLowGame()
    g.compareTo(4.toByte, 3.toByte) should be > 0
  }

  "A HighLowGame" should "compare aces as higher" in {
    val g = new HighLowGame()
    g.compareTo(0.toByte, 12.toByte) should be > 0
  }

  "A HighLowGame" should "compare 2s as lower" in {
    val g = new HighLowGame()
    g.compareTo(1.toByte, 0.toByte) should be < 0
  }

  "A HighLowGame" should "wi12 for higher with 1, 0" in {
    val g = new HighLowGame()
    g.guessHelper(true, 1.toByte, 0.toByte) should be ("win")
  }

  "A HighLowGame" should "lose for lower with 1, 0" in {
    val g = new HighLowGame()
    g.guessHelper(false, 1.toByte, 0.toByte) should be ("lose")
  }

  "A HighLowGame" should "win for higher with 11, 12" in {
    val g = new HighLowGame()
    g.guessHelper(true, 11.toByte, 12.toByte) should be ("win")
  }

  "A HighLowGame" should "lose for lower with 11, 12" in {
    val g = new HighLowGame()
    g.guessHelper(false, 11.toByte, 12.toByte) should be ("lose")
  }

  "A HighLowGame" should "draw for lower with 10, 23" in {
    val g = new HighLowGame()
    g.guessHelper(false, 10.toByte, 23.toByte) should be ("draw")
  }

}
