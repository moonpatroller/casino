import java.net._
import java.io._
import java.util.stream._

object Http {

    case class Response(sock: Socket) {
        val out = new PrintStream(sock.getOutputStream(), true, "UTF-8")
        def print(s: String): Unit = out.print(s)
        def end(): Unit = {
            out.flush()
            out.close()
            sock.close()
        }
    }

    case class Request(sock: Socket) {
        val br = new BufferedReader(new InputStreamReader(sock.getInputStream()))
    	val buf = new Array[Char](4096)
    	val numRead = br.read(buf, 0, buf.length)
    	val requestData = new String(buf, 0, numRead)
    	println("requestData: " + requestData)
        val path = requestData.split("\r\n")(0).split("\\s+")(1)
        val body = requestData.substring(requestData.indexOf("\r\n\r\n") + 4)
    }

    var handlers = List.empty[(String, (Request, Response) => Unit)]

    def handle(path: String, handler: (Request, Response) => Unit) {
        handlers = (path, handler) :: handlers
    }

    def listenAndServe(port: Int): Unit = {
        val ss = new ServerSocket(port);
        while (true) {
            val sock = ss.accept();

            val req = Request(sock)
            val path = req.path 

            handlers.find((x: (String, (Request, Response) => Unit)) => path.startsWith(x._1)).foreach(_._2.apply(req, Response(sock)))
        }
    }
}

object Server
{
    def main(args: Array[String]): Unit = {

        val highLow = new HighLowMoves()

        Http.handle("/highlow/", (request, response) => {
        	println(request.body)
            response.print(highLow.handleRequest(request.body))
            response.end()
        })

        val blackjack = new BlackjackMoves()

        Http.handle("/blackjack/", (request, response) => {
        	println(request.body)
            response.print(blackjack.handleRequest(request.body))
            response.end()
        })

        Http.listenAndServe(8080)
    }
}
