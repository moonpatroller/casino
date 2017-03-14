import org.scalatest._

class BlackjackSpec extends FlatSpec with Matchers {

    val aces = 0 until 4 map (x => (x * 13).toByte)
    val tenCards = (9 until 13) flatMap (v => (0 until 4) map (m => (v + 13 * m).toByte))
    val numberCards = (1 until 9) flatMap (v => (0 until 4) map (m => (v + 13 * m).toByte))

    "A BlackjackHand" should "score 21" in {
        for (ace <- aces;
             tenCard <- tenCards) {
            BlackjackHand(ace, tenCard).getScore() should be (21)
            BlackjackHand(tenCard, ace).getScore() should be (21)
        }
    }
    "A BlackjackHand" should "not score 21" in {
        for (ace <- aces;
             numberCard <- numberCards) {
            BlackjackHand(ace, numberCard).getScore() should be ((numberCard % 13) + 12)
            BlackjackHand(numberCard, ace).getScore() should be ((numberCard % 13) + 12)
        }
    }

    "A BlackjackHand" should "have an ace scored as 1" in {
        for (ace <- aces;
             numberCard1 <- numberCards;
             numberCard2 <- numberCards
             if (numberCard1 % 13) + 1 + (numberCard2 % 13) + 1 > 10) {
            BlackjackHand(List(ace, numberCard1, numberCard2)).getScore() should be ((numberCard1 % 13) + 1 + (numberCard2 % 13) + 1 + 1)
        }
    }

    "A BlackjackGames" should "show blackjack correctly" in {
        for (ace <- aces;
             tenCard <- tenCards) {
            val d = Deck(ace, tenCard, 1.toByte, 2.toByte)
            Status(d) should be (PlayerBlackjack(BlackjackHand(ace, tenCard), BlackjackHand(1.toByte, 2.toByte)))
        }
    }

    "A BlackjackGames" should "bust correctly" in {
        for (numberCard <- numberCards;
             tenCard1 <- tenCards;
             tenCard2 <- tenCards) {
            // val d = Deck(numberCard, tenCard1, 1.toByte, tenCard2, 2.toByte)
            // Status(d).hit() should be (GameOver(BlackjackHand(2.toByte, 1.toByte), BlackjackHand(tenCard2, numberCard, tenCard1)))

            PlayerTurn(Deck(tenCard2, 2.toByte), BlackjackHand(numberCard, tenCard1), BlackjackHand(1.toByte, 2.toByte)).hit() should be (PlayerBusted(BlackjackHand(tenCard2, numberCard, tenCard1), BlackjackHand(1.toByte, 2.toByte)))
        }
    }

    "A BlackjackGames" should "have dealer hit" in {

        val playerHand = BlackjackHand(2.toByte, 16.toByte)

        for (c1 <- 0 until 13;
             c2 <- 0 until 13;
             c3 <- 0 until 13;
             dealerHand1 = BlackjackHand(c1.toByte, c2.toByte);
             dealerHand2 = BlackjackHand(c1.toByte, c2.toByte, c3.toByte);
             score1 = dealerHand1.getScore();
             score2 = dealerHand1.getScore()
             if score1 < 17 && score2 <= 21 && score2 >= 17) {

            PlayerTurn(Deck(c2.toByte, 9.toByte), playerHand, BlackjackHand(c1.toByte)).stand() should be (GameOver(playerHand, BlackjackHand(c3.toByte, c2.toByte, c1.toByte)))
        }
    }

    "A BlackjackGames" should "have dealer bust" in {

        val playerHand = BlackjackHand(2.toByte, 16.toByte)

        for (c1 <- 0 until 13;
             c2 <- 0 until 13;
             c3 <- 0 until 13;
             dealerHand1 = BlackjackHand(c1.toByte, c2.toByte);
             dealerHand2 = BlackjackHand(c1.toByte, c2.toByte, c3.toByte);
             score1 = dealerHand1.getScore();
             score2 = dealerHand1.getScore()
             if score1 < 17 && score2 > 21) {

            PlayerTurn(Deck(c2.toByte, 9.toByte), playerHand, BlackjackHand(c1.toByte)).stand() should be (DealerBusted(playerHand, BlackjackHand(c3.toByte, c2.toByte, c1.toByte)))
        }
    }

    "A BlackjackGames" should "have dealer stand" in {

        val playerHand = BlackjackHand(2.toByte, 16.toByte)

        for (c1 <- 1 until 13;
             c2 <- 1 until 13;
             dealerHand = BlackjackHand(c1.toByte, c2.toByte);
             score = dealerHand.getScore()
             if score > 16) {

            PlayerTurn(Deck(c2.toByte), playerHand, BlackjackHand(c1.toByte)).stand() should be (GameOver(playerHand, BlackjackHand(c2.toByte, c1.toByte)))
        }
    }

}
