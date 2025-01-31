// By HJC 2021-11-21
// 5x5 Futoshiki (Inequality/Unequal Puzzle)
//      only works when solution exists... for now...
//
const val puzzle = """
			|0-0>0-0-0|
            |- - - - -|
            |0-0-0-0-0|
            |v v ^ - -|
            |0-0>0-0-0|
            |- - - ^ -|
            |4-0-0-0-0|
            |^ - - - v|
            |0-0-0-0-0|
            """
//
//fun isEven(num: Int) = num % 2 == 0
val num = mutableListOf<Int>()
val sig = mutableMapOf<Int, MutableSet<Int>>()
var ind = 0
var dir = 1
var signFlag = false
var tries = 0
//
fun main() {
    println(puzzle)
    stringify(puzzle)
    println("num: $num")    // num: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    println("sig: $sig\n")    // sig: {1=[2], 5=[10], 6=[11], 12=[7], 11=[12], 18=[13], 20=[15], 19=[24]}

    val sol = num.toMutableList()
    while (ind < sol.count()) solve(sol, ind, dir)
    val finalList = sol.toList()
    printBoard(finalList)
}
//
fun stringify(input: String) {
    fun setOrAddSign(big: Int, small: Int) {
        if (sig[big].isNullOrEmpty()) sig[big] = mutableSetOf(small)    else sig[big]!!.add(small)
    }
    val temp = input.trimMargin().split("|\n")
//    println("temp: $temp")		// [0-0>0-0-0, - - - - -, 0-0-0-0-0, v v ^ - -, 0-0>0-0-0, - - - ^ -, 4-0-0-0-0, ^ - - - v, 0-0-0-0-0]

    for (i in 0..8) {
        jloop@ for (j in 0..8) {
            when (val want = temp[i][j].digitToIntOrNull() ?: temp[i][j]) {
                ' ', '-' -> continue@jloop
                is Int -> num.add(want)
                is Char -> {
                    val (big, small) = when (want) {	// key is always greater than value
                        '>' -> Pair(num.lastIndex, num.count())
                        '<' -> Pair(num.count(), num.lastIndex)
                        'v' -> Pair(num.count() + j/2 - 5, num.count() + j/2)
                        '^' -> Pair(num.count() + j/2, num.count() + j/2 - 5)
                        else -> throw Exception("Invalid Inequality Symbol (e.g: uppercase V should be lowercase v)")
                    }
                    setOrAddSign(big, small)
                }
            }
        }
    }
}
//
fun solve(sol: MutableList<Int>, tempind: Int, tempdir: Int) {
    ind = tempind
    dir = tempdir
    while (tryAgain(ind, sol) || dir == -1) {
        when {
            sol[ind] != 0 && sol[ind] == num[ind]
                      -> { println("skipping fixed number"); ind += dir }

            dir == -1 -> if (ind % 5 == 4 || ind >= 20 || sol[ind] == 5) {
                                   sol[ind] = 0;  ind += dir
                          } else { sol[ind] += 1; dir = 1
                                   println("incremented and U-turn")
                          }
            dir == 1  -> when {
                             ind % 5 == 4 && sol[ind] == 0 -> { sol[ind] = 15 - sol.slice((ind - 4)..ind).sum() }
                             ind >= 20 && sol[ind] == 0    -> { sol[ind] = 15 - sol.slice((ind % 5)..ind step 5).sum() }
                             sol[ind] == 5 || (ind % 5 == 4 || ind >= 20)
                                                           -> { sol[ind] = 0; dir = -1
                                                                println("reset to 0 and backtracking"); report(sol)
                                                                break }
                             else                          -> { sol[ind] += 1; println("incremented") }
                         }
        }
        report(sol)
    }
    ind += dir
    println("  next index")
// 			need conditions for completion/no solution
}
//
fun report(sol: MutableList<Int>) {
    val arrow = if (dir == 1) '→' else '←'
    println("\nind: $ind, dir: $arrow, sol: $sol")
}
//
// num: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0]
// sig: {1=[2], 5=[10], 6=[11], 12=[7], 11=[12], 18=[13], 20=[15], 19=[24]}
//
fun tryAgain(ind: Int, sol: MutableList<Int>): Boolean {
    val noDup = sol.slice((0 until ind).filter { it % 5 == ind % 5 || it / 5 == ind / 5 })
    println("trying [${sol[ind]}] against $noDup")
    tries++
    //
    val signPairs = mutableSetOf<Pair<Int, Int>>()
    if (ind > 4) signPairs.addAll(setOf(Pair(ind, ind - 5), Pair(ind - 5, ind)))
    if (ind % 5 > 0) signPairs.addAll(setOf(Pair(ind, ind - 1), Pair(ind - 1, ind)))
    signPairs.reversed().forEach { (k, v) -> if (sig[k]?.contains(v) != true) signPairs -= Pair(k,v) }
    //
    signFlag = signPairs.any { (G, s) -> sol[G] < sol[s] }.also { if (it) println("  →  inequality disagreement") }
    return noDup.contains(sol[ind]) || sol[ind] == 0 || signFlag
}
//
fun printBoard(finalList: List<Int>) {
    println("\n[$tries tries]  →  Complete!\n")

    var output = puzzle
    val outputIter = finalList.listIterator()
    puzzle.forEachIndexed { charNum, it ->
        if (it.isDigit()) output = output.replaceRange(charNum, charNum+1, outputIter.next().toString())
    }
    println(output)
}
//
// Solution:
//	13254
//	35142
//	24315
//	41523
//	52431
//
//  Final output:
//  [2493 tries]  →  Complete!
//
//
//			  |1-3>2-5-4|
//            |- - - - -|
//            |3-5-1-4-2|
//            |v v ^ - -|
//            |2-4>3-1-5|
//            |- - - ^ -|
//            |4-1-5-2-3|
//            |^ - - - v|
//            |5-2-4-3-1|
//
//
//
//
//