import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}

import java.util.UUID.randomUUID

class HighLowMoves
{
    val games = collection.mutable.Map[String, HighLowGame]()

    implicit val formats = Serialization.formats(NoTypeHints)
    case class NewGame()
    case class Guess(id: String, guess: String)
    case class Error(id: String, msg: String)
    case class HighLowResult(id: String, lastCard: String, currentCard: String, lastGuess: String, result: String)

    def prettyPrint(card: Byte): String = if (card < 0) "" else Card.rank(card) + Card.suit(card)

    def handleRequest(text: String): String = {
        if (text == "newgame") {
            val id = randomUUID().toString()
            val game = new HighLowGame()
            games(id) = game
            write(HighLowResult(id, "", prettyPrint(game.currentCard), "", ""))
        } else {
            try {
                read[Guess](text) match {
                    case Guess(id, guess) =>
                        games.get(id) match {
                            case None => 
                                write(Error(id, "Invalid id"))
                            case Some(game: HighLowGame) =>
                                guess match {
                                    case "higher" | "lower" => 
                                        val result = game.guess(guess == "higher")
                                        write(HighLowResult(id, prettyPrint(game.lastCard), prettyPrint(game.currentCard), guess, result))
                                    case _ => // not a valid guess
                                        write(Error(id, "Invalid guess: '" + guess + "'"))
                                }
                        }
                }
            } catch {
                case pe: Exception => 
                    println(pe)
                    pe.printStackTrace()
                    write(Error("", "Invalid request"))
            }
        }
    }

}
