import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}
import org.json4s.ParserUtil.ParseException

import java.util.UUID.randomUUID

class BlackjackMoves
{
    val games = collection.mutable.Map[String, Status]()

    implicit val formats = Serialization.formats(NoTypeHints)
    case class Move(id: String, move: String)
    case class Error(id: String, msg: String)
    case class BlackjackResult(id: String, 
        dealerCards: List[String], dealerScore: Int, 
        playerCards: List[String], playerScore: Int, result: String)

    def prettyPrint(card: Byte): String = if (card < 0) "" else Card.rank(card) + Card.suit(card)

    def prettyPrint(hand: BlackjackHand): List[String] = {
        hand.cards.map(prettyPrint(_))
    }

    def prettyPrintFirstCard(hand: BlackjackHand): List[String] = {
        prettyPrint(hand.cards.head) :: hand.cards.tail.map(prettyPrint(_))
    }

    def writeResult(id: String, status: Status): String = {
        status match {
            case PlayerTurn(_, playerCards, dealerCards) => 
                write(BlackjackResult(id, prettyPrintFirstCard(dealerCards), dealerCards.getScore(), prettyPrint(playerCards), playerCards.getScore(), ""))

            case go @ GameOver(playerCards, dealerCards) =>
                write(BlackjackResult(id, prettyPrint(dealerCards), dealerCards.getScore(), prettyPrint(playerCards), playerCards.getScore(), go.getResult()))

            case pb @ PlayerBusted(playerCards, dealerCards) => 
                write(BlackjackResult(id, prettyPrint(dealerCards), dealerCards.getScore(), prettyPrint(playerCards), playerCards.getScore(), pb.getResult()))

            case db @ DealerBusted(playerCards, dealerCards) => 
                write(BlackjackResult(id, prettyPrint(dealerCards), dealerCards.getScore(), prettyPrint(playerCards), playerCards.getScore(), db.getResult()))

            case pbj @ PlayerBlackjack(playerCards, dealerCards) => 
                write(BlackjackResult(id, prettyPrint(dealerCards), dealerCards.getScore(), prettyPrint(playerCards), playerCards.getScore(), pbj.getResult()))

            case pu @ Push(playerCards, dealerCards) => 
                write(BlackjackResult(id, prettyPrint(dealerCards), dealerCards.getScore(), prettyPrint(playerCards), playerCards.getScore(), pu.getResult()))
        }
    }

    def handleRequest(text: String): String = {
        if (text == "newgame") {
            val id = randomUUID().toString()
            val game = Status(Deck())
            games(id) = game
            writeResult(id, game)
        } else {
            try {
                read[Move](text) match {
                    case Move(id, move) =>
                        games.get(id) match {
                            case None => 
                                write(Error(id, "Invalid id"))
                            case Some(game: PlayerTurn) =>
                                move match {
                                    case "hit" => 
                                        games(id) = game.hit()
                                        writeResult(id, games(id))
                                    case "stand" => 
                                        games(id) = game.stand()
                                        writeResult(id, games(id))
                                    case _ => // not a valid guess
                                        write(Error(id, "Invalid move: '" + move + "'"))
                                }
                            case Some(game: Status) =>
                                write(Error(id, "Can't hit or stand."))
                        }
                }
            } catch {
                case pe: ParseException => 
                    println(pe)
                    pe.printStackTrace()
                    write(Error("", "Invalid request"))
            }
        }
    }
}
