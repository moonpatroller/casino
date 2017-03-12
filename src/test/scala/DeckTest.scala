import org.scalatest._

class DeckSpec extends FlatSpec with Matchers {

  "A Deck" should "draw up to 52 cards" in {
    val d = Deck()
    var cards = Set[Byte]()
    for (i <- 0 until 52) {
        cards = cards + d.draw()
    }
    cards.size should be (52)
    for (i <- 0 until 52) {
        cards(i.toByte) should be (true)
    }
  }

  it should "throw IllegalArgumentException if a 53rd card is drawn" in {
    val d = Deck()
    for (i <- 0 until 52) d.draw()

    a [IllegalArgumentException] should be thrownBy {
      d.draw()
    } 
  }
}