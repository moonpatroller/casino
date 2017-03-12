import util._

trait CardSource {
    def draw(): Byte
}

object Deck
{
    def apply(): Deck = {
        val cards = Array.tabulate(52) (i => i.toByte)
        val rand = new util.Random()
        Deck(cards, rand)
    }

    def apply(cards: Byte*) = PreshuffledDeck(cards.toIterator)
}

case class Deck(cards: Array[Byte], rand: Random) extends CardSource
{
    var numLeft = cards.length
    def isEmpty() = numLeft <= 0
    def draw(): Byte = {
        val index = rand.nextInt(numLeft)
        val temp = cards(index)
        cards(index) = cards(numLeft - 1)
        cards(numLeft - 1) = temp
        numLeft -= 1
        temp
    }
}

case class PreshuffledDeck(cards: Iterator[Byte]) extends CardSource
{
    def draw(): Byte = cards.next
}
