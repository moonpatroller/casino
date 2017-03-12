object BlackjackHand
{
    def apply(cards: Byte*): BlackjackHand = new BlackjackHand(cards.toList)
}

case class BlackjackHand(cards: List[Byte]) {
    def getScore(): Int = {
        def numericRank(b: Byte): Int = {
            val rank = b % 13
            if (rank > 8) 10 else rank + 1
        }

        cards.foldLeft((0, false)) {
            case ((score, seenAce), card) => (score + numericRank(card), seenAce || (card % 13 == 0))
        } match {
            case (score, true) if score + 10 <= 21 => score + 10
            case (score, _) => score
        }
    }
    def addCard(card: Byte): BlackjackHand = BlackjackHand(card :: cards)
}

object Status {
    def apply(deck: CardSource): Status = {
        val playerCards = BlackjackHand(deck.draw(), deck.draw())
        if (playerCards.getScore() == 21) {
            val dealerCards = BlackjackHand(deck.draw(), deck.draw())
            GameOver(dealerCards, playerCards)
        } else {
            PlayerTurn(deck.draw(), playerCards, deck)
        }
    }
}

sealed trait Status {
    def hit(): Status = this
    def stand(): Status = this
}

case class PlayerTurn(dealerCard: Byte, playerCards: BlackjackHand, deck: CardSource) extends Status {
    def hitTil17(cards: BlackjackHand): BlackjackHand = {
        if (cards.getScore() > 16) cards
        else hitTil17(cards.addCard(deck.draw()))
    }

    override def hit(): Status = {
        val newPlayerCards = playerCards.addCard(deck.draw())
        if (newPlayerCards.getScore() < 21) {
            copy(playerCards = newPlayerCards)
        } else if (newPlayerCards.getScore() > 21) {
            GameOver(BlackjackHand(deck.draw(), dealerCard), newPlayerCards)
        } else {
            val dealerCards = hitTil17(BlackjackHand(dealerCard :: Nil))
            GameOver(dealerCards, newPlayerCards)
        }
    }

    override def stand(): Status = {
        val dealerCards = hitTil17(BlackjackHand(dealerCard :: Nil))
        GameOver(dealerCards, playerCards)
    }
}

case class GameOver(dealerCards: BlackjackHand, playerCards: BlackjackHand) extends Status
