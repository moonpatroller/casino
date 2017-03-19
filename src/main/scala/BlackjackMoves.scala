import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}
import org.json4s.ParserUtil.ParseException

import java.util.concurrent.{DelayQueue, TimeUnit}
import java.util.UUID.randomUUID

class BlackjackMoves
{
    val games = collection.mutable.Map[String, Status]()

    implicit val formats = Serialization.formats(NoTypeHints)
    case class Move(id: String, move: String)
    case class Error(id: String, msg: String)
    case class BlackjackResult(id: String, 
        dealerCards: List[String], dealerScore: Int, 
        playerCards: List[String], playerScore: Int, 
        result: String, nextMoves: List[String])

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
                write(BlackjackResult(id, prettyPrintFirstCard(dealerCards), dealerCards.getScore(), prettyPrint(playerCards), playerCards.getScore(), "", List("hit", "stand", "display")))

            case go @ GameOver(playerCards, dealerCards) =>
                write(BlackjackResult(id, prettyPrint(dealerCards), dealerCards.getScore(), prettyPrint(playerCards), playerCards.getScore(), go.getResult(), Nil))

            case pb @ PlayerBusted(playerCards, dealerCards) => 
                write(BlackjackResult(id, prettyPrint(dealerCards), dealerCards.getScore(), prettyPrint(playerCards), playerCards.getScore(), pb.getResult(), Nil))

            case db @ DealerBusted(playerCards, dealerCards) => 
                write(BlackjackResult(id, prettyPrint(dealerCards), dealerCards.getScore(), prettyPrint(playerCards), playerCards.getScore(), db.getResult(), Nil))

            case pbj @ PlayerBlackjack(playerCards, dealerCards) => 
                write(BlackjackResult(id, prettyPrint(dealerCards), dealerCards.getScore(), prettyPrint(playerCards), playerCards.getScore(), pbj.getResult(), Nil))

            case pu @ Push(playerCards, dealerCards) => 
                write(BlackjackResult(id, prettyPrint(dealerCards), dealerCards.getScore(), prettyPrint(playerCards), playerCards.getScore(), pu.getResult(), Nil))
        }
    }

    def removeOldGames(): Unit = {
        for ((k, v) <- games) {
            v match {
                case _: PlayerTurn =>
                case _ => games.remove(k)
            }
        }
    }

    def handleRequest(text: String): String = {
        removeOldGames()
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
                                    case "display" => 
                                        writeResult(id, games(id))
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
