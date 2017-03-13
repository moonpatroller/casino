object BlackjackHand
{
    def apply(cards: Byte*): BlackjackHand = new BlackjackHand(cards.toList)
}

case class BlackjackHand(cards: List[Byte]) {
    def isBlackjack(): Boolean = cards.length == 2 && getScore() == 21
    def isBusted(): Boolean = getScore() > 21

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

    def hitTil17(deck: CardSource): BlackjackHand = {
        if (getScore() > 16) this
        else addCard(deck.draw()).hitTil17(deck)
    }

}

object Status {
    def apply(deck: CardSource): Status = apply(deck, BlackjackHand(deck.draw(), deck.draw()), BlackjackHand(deck.draw(), deck.draw()))

    def apply(deck: CardSource, playerHand: BlackjackHand, dealerHand: BlackjackHand): Status = {
        if (playerHand.isBlackjack()) {
            if (dealerHand.isBlackjack()) {
                Push(playerHand, dealerHand)
            } else {
                PlayerBlackjack(playerHand, dealerHand)
            }
        } else if (playerHand.isBusted()) {
            PlayerBusted(playerHand, dealerHand)
        } else {
            PlayerTurn(deck, playerHand, dealerHand)
        }
    }

    def dealerTurn(deck: CardSource, playerHand: BlackjackHand, dealerHand: BlackjackHand): Status = {
        val finalDealerHand = dealerHand.hitTil17(deck)
        if (finalDealerHand.isBusted()) {
            DealerBusted(playerHand, finalDealerHand)
        } else if (finalDealerHand.getScore() == playerHand.getScore()) {
            Push(playerHand, finalDealerHand)
        } else {
            GameOver(playerHand, finalDealerHand)
        }
    }
}

sealed trait Status {
    def getResult(): String = ""
}

case class PlayerTurn(deck: CardSource, playerHand: BlackjackHand, dealerHand: BlackjackHand) extends Status {

    def hit():   Status = Status(deck, playerHand.addCard(deck.draw()), dealerHand)

    def stand(): Status = Status.dealerTurn(deck, playerHand, dealerHand)
}

case class PlayerBlackjack(playerHand: BlackjackHand, dealerHand: BlackjackHand) extends Status {
    override def getResult(): String = "blackjack"
}

case class PlayerBusted(playerHand: BlackjackHand, dealerHand: BlackjackHand) extends Status {
    override def getResult(): String = "player busted"
}

case class DealerBusted(playerHand: BlackjackHand, dealerHand: BlackjackHand) extends Status {
    override def getResult(): String = "dealer busted"
}

case class Push(playerHand: BlackjackHand, dealerHand: BlackjackHand) extends Status {
    override def getResult(): String = "push"
}

case class GameOver(playerHand: BlackjackHand, dealerHand: BlackjackHand) extends Status {
    override def getResult(): String = {
        if (dealerHand.getScore() > playerHand.getScore()) {
            "dealer wins"
        } else {
            "player wins"
        }
    }
}
