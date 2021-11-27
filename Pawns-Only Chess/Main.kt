//// Pawns-Only Chess by HJC 2021-11-15
//
package chess
//
enum class Player(val color: String, var eerum: String, var pieces: String, var passingTrail: String) {
    One("white", "", "a2b2c2d2e2f2g2h2", ""),
    Two("black", "", "a7b7c7d7e7f7g7h7", "");
    companion object {
        fun daum() = when (turn) { One -> Two; Two -> One }
        fun update() {
            enemy.pieces = enemy.pieces.replace(over[there]!!, "")
            turn.pieces = turn.pieces.replace(over[here]!!, over[there]!!)
            enemy.passingTrail = ""
            turn = enemy
            enemy = daum()
        }
    }
}
var turn = Player.One
var enemy = Player.daum()
const val here = "here"
const val there = "there"
val over = mutableMapOf<String, String>()     // (here to "", there to "")
const val ep = "ep"         // 1 square forward
const val ride = "ride"     // 2 squares forward
const val rafe = "rafe"     // diagonal attack
const val agger = "agger"   // enPassant capture
val st = mutableMapOf<String, Boolean>()    // (ep to false, ride to false, rafe to false, agger to false)
var exitFlag = false
var reports = false
// ===================================================================================================================
fun main() {
    println(" Pawns-Only Chess")
/* debug mode...*/
    val sim = false  /* true for debugging */ .also { if (it) preset() }
/*...to here */
    println("First Player's name:").also { Player.One.eerum = Player.One.color.takeIf {sim} ?: readLine()!! }
    println("Second Player's name:").also { Player.Two.eerum = Player.Two.color.takeIf {sim} ?: readLine()!! }
    printBoard()

    while (!exitFlag) play()

    println("Bye!")
}
// ===================================================================================================================
fun printBoard() {
    val rankLine = "  +" + "---+".repeat(8)
    val fileLine = " | "
    for (x in 8 downTo 1) {
        print("$rankLine\n$x$fileLine")
        for (y in 'a'..'h') {
            when {
                "$y$x" in Player.One.pieces -> 'W'
                "$y$x" in Player.Two.pieces -> 'B'
                else -> " "
            }.also { print("$it$fileLine") }
        }
        println()
    }
    println("$rankLine\n    " + ('a'..'h').joinToString("   "))
}
//
fun play(): Boolean {
    println("${turn.eerum}'s turn:")
    val move = readLine()!!.lowercase()
    if (move.matches(Regex("([a-h][1-8]){2}")) || move == "exit") { // case 0: -> else invalid input
        over.putAll(setOf(here to move.substring(0..1), there to move.substring(2..3)))
        interpretation()
        when {
            move == "exit" -> exitFlag = true    // case 1: exit
            over[here]!! !in turn.pieces -> println("No ${turn.color} pawn at ${over[here]}") // case 2
            st.values.all { !it } -> println("Invalid Input")   // case 3: correctly found piece, but invalid move
            else -> goodMove()          // case 4: valid move; update turn/Board
        }
    } else println("Invalid Input")
    return exitFlag
}
//
fun interpretation() {
    val start = over[here]!!; val stop = over[there]!!
    val dRank: Int = (stop[1] - start[1]).takeIf { turn.ordinal == 0 } ?: -(stop[1] - start[1])
    val dFile: Int = (stop[0] - start[0]).takeIf { it > 0 } ?: -(stop[0] - start[0])
    st[ep] = dRank == 1 && dFile == 0 && stop !in enemy.pieces
    st[ride] = dRank == 2 && dFile == 0 && stop !in enemy.pieces && start[1].digitToInt() == 2 + 5 * turn.ordinal
    st[rafe] = dRank == 1 && dFile == 1 && stop in enemy.pieces
    st[agger] = dRank == 1 && dFile == 1 && stop !in enemy.pieces && stop == enemy.passingTrail
}
//
fun goodMove() {
    val x = over[there]!![0];  val y = over[there]!![1]
    val neighbors = Regex("[${x - 1}|${x + 1}][$y]")
    when {
        st[ride]!! && enemy.pieces.contains(neighbors)
                                          -> turn.passingTrail = "$x${3 + 3 * turn.ordinal}"
        over[there] == enemy.passingTrail -> enemy.pieces = enemy.pieces.replace("$x${4 + enemy.ordinal}", "")
    }
    Player.update()
    printBoard()
/* For debugging... */
    if (reports) printReport()
/* ...to here */
    when {
        enemy.pieces.contains(Regex("[1|8]")) || turn.pieces.isEmpty()
                    -> { println("${enemy.color} wins!"); exitFlag = true }
        stalemate() -> { println("Stalemate!");           exitFlag = true }
    }
    st.replaceAll { _, _ -> false }     // resets st
}
//
fun stalemate(): Boolean {
    val vect = if (turn == Player.One) 1 else -1
    var staleFlag = true
    for (my in turn.pieces.chunked(2)) {
        val (ahhp, wehn, oreun) =
            listOf(
                listOf( my[0], my[1] + vect ).joinToString(""),
                listOf( my[0] + vect, my[1] + vect ).joinToString(""),
                listOf( my[0] - vect, my[1] + vect ).joinToString(""),
            )
        staleFlag = staleFlag && ahhp in enemy.pieces &&
                (wehn !in enemy.pieces || wehn in turn.pieces || my[0] + vect !in 'a'..'h') &&
                (oreun !in enemy.pieces || oreun in turn.pieces || my[0] - vect !in 'a'..'h')
    }
    return staleFlag
}
// debugging functions + variables

fun preset() {
    val choice: Int
    println(
        """Preload starting conditions & Print reports for..?
            | [1] enPassant
            | [2] Stalemate Draw
            | [3] Promotion Win
            | [4] Capture Win
            | [5] Only Print Mode""".trimMargin()
    ).also { choice = readLine()!!.toInt() }
    reports = true
    when (choice) {
        1 -> { Player.One.pieces = "a2b5c2d5e2f5g2h5"; Player.Two.pieces = "a7b4c7d4e7f4g7h4" }
        2 -> { Player.One.pieces = "a4c4e2h4"; Player.Two.pieces = "a5c5e5h5" }
        3 -> { Player.One.pieces = "a7b7c7d7e7f6g5h4"; Player.Two.pieces = "a5b4c3d2e2f2g2h2" }
        4 -> { Player.One.pieces = "e2"; Player.Two.pieces = "d5" }
        5 -> {}
        else -> { println("Invalid Choice; restart program"); exitFlag = true }
    }
}
fun printReport() {
    val accent: (String, String) -> String = { str, aim ->
            str.chunked(2).joinToString(" ").replace(aim, "{${aim.uppercase()}}") }
    val summary: (Player) -> String = { P ->
            P.eerum + ": " + accent(P.pieces, over[there]!!) + " ; passingTrail: " + P.passingTrail }
    println(summary(Player.One))
    println(summary(Player.Two))
    print(" Move = ${over[here]} -> st" + st.filterValues { it }.keys.joinToString(""))
    println(" -> {${over[there]!!.uppercase()}}")
}

/* test inputs
for enPassant:
e2e4e7e5d2d4e5d4c2c4d4e3d4c3b2c3a7a5e4e5d7d5a2a4f7f5e5d6e5f6g7f6g2g4d5d4g4g5d4c3g5f6c3c2f6f7c2c1
for stalemate:
a2a4a7a5b2b4b7b5...etc

*/
// current end
// ===================================================================================================================
/*
val critList = listOf(  dRank,
        if (dFile == -1) 1 else dFile,
        if (over[there].toString() in enemy.pieces) 1 else 0,
        when { over[here].toString()[1].digitToInt() == 2 + 5 * turn.ordinal -> 1
            over[there] == enemy.passingTrail -> -1
            else -> 0
        }
    )
    st[ep] = critList.containsAll(listOf(1, 0, 0, 0))
    st[ride] = critList.containsAll(listOf(2, 0, 0, 1))
    st[rafe] = critList.containsAll(listOf(1, 1, 1, 0))
    st[agger] = critList.containsAll(listOf(1, 1, 0, -1))

        if (over[there].toString() !in turn.pieces) {
            when (over[there].toString() in enemy.pieces) {
                true -> st[rafe] = (dRank == 1 && dFile in setOf(-1, 1))
                false -> when (dRank) {
                    2 -> st[ride] = (dFile == 0 && over[here].toString()[1].digitToInt() == 2 + 5 * turn.ordinal)
                    1 -> when (dFile) {
                        -1, 1 -> st[agger] = over[there] == enemy.passingTrail
                        0 -> st[ep] = true
                    }
                }
            }
        }

//fun names(): List<String> {
//    println("First Player's name:")
//    val p1 = readLine()!!
//    println("Second Player's name:")
//    val p2 = readLine()!!
//    return listOf(p1, p2)
//}
	// Part of Method 2
// class Piece(val side: String) {
//     var fyle: String = "a"
//     var rank: Int = 7
// }
// ---------------------------------------------------
	// Part of Method 3
// class space() {
//     var occupied =
// }
// ---------------------------------------------------------------------------------
    // Method 3 = create 2D board as matrix with spacenames as values
	// 				with values containing ternary enum class??
//
// ------------------------------------------------------------------------------------
	// Method 2 = create class of piece and each piece is an instance (object),
	// 				with rank/fyle as mutable properties.
	// 				Black/White will each be list of those objects.
//     var b1 = Piece("B")
//     var b2 = Piece("B")
//     val black = mutableListOf(b1, b2)
//     val white = Piece("W")
// 	black[1].fyle = "b"
//     println("${black[0].fyle}, ${black[0].rank}, ${black[1].fyle}, ${black[1].rank}")
//     println("${white.fyle}, ${white.rank}")
// }
// 	--------------------------------------------------------------------
	// 	Method 1 = create map of rank/file for each piece,
	// 				list of pieces are mapped to Black/White.
	// 				End result becomes Map of List of Map
// 	var pieces = mutableMapOf(
//         "B" to mutableListOf(mutableMapOf("file" to "a", "rank" to 7),
//                              mutableMapOf("file" to "b", "rank" to 7),
//                              mutableMapOf("file" to "c", "rank" to 7),
//                              mutableMapOf("file" to "d", "rank" to 7),
//                              mutableMapOf("file" to "e", "rank" to 7),
//                              mutableMapOf("file" to "f", "rank" to 7),
//                              mutableMapOf("file" to "g", "rank" to 7),
//                              mutableMapOf("file" to "h", "rank" to 7),
//                             ),
//         "W" to mutableListOf(mutableMapOf("file" to "a", "rank" to 2),
//                              mutableMapOf("file" to "b", "rank" to 2),
//                              mutableMapOf("file" to "c", "rank" to 2),
//                              mutableMapOf("file" to "d", "rank" to 2),
//                              mutableMapOf("file" to "e", "rank" to 2),
//                              mutableMapOf("file" to "f", "rank" to 2),
//                              mutableMapOf("file" to "g", "rank" to 2),
//                              mutableMapOf("file" to "h", "rank" to 2),
//                             )
//     )
//     println(pieces)
// }
 */