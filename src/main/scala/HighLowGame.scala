class HighLowGame
{
    var numLeft = 26
    def gameOver() = numLeft <= 0

    val deck = Deck()
    var lastCard: Byte = deck.draw()
    def drawCard(): Byte = {
        lastCard = deck.draw()
        lastCard
    }

    def numericRank(b: Byte): Int = if (b % 13 == 0) 13 else b % 13
    def compareTo(c1: Byte, c2: Byte): Int = numericRank(c1) - numericRank(c2)
    def guessHelper(guessHigher: Boolean, c1: Byte, c2: Byte): String = {
        compareTo(c1, c2) match {
            case 0 => "tie"
            case n if n < 0 => if (!guessHigher) "lose" else "win"
            case n if n > 0 => if (!guessHigher) "win" else "lose"
        }
    }
    def guess(guessHigher: Boolean, c1: Byte, c2: Byte): String = {
        if (gameOver()) "gameover"
        else guessHelper(guessHigher, c1, c2)
    }
    def guess(guessHigher: Boolean): String = {
        guessHelper(guessHigher, lastCard, drawCard())
    }
}
