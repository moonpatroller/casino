object Card
{
    def rank(b: Byte): String = {
        b % 13 match {
            case 0 => "A"
            case 10 => "J"
            case 11 => "Q"
            case 12 => "K"
            case n: Int => (n + 1).toString()
        }
    }

    def suit(b: Byte): String = {
        b / 14 match {
            case 0 => "C"
            case 1 => "H"
            case 2 => "S"
            case 3 => "D"
        }
    }
}
