// By HJC 2021-11-18
// Meeting Scheduler for two calendars
//
// example input:
val calOne = listOf(Pair("9:00", "10:30"), Pair("12:00", "13:00"), Pair("16:00", "18:00"))
val bounds1 = Pair("9:00", "20:00")
val calTwo = listOf(Pair("10:00", "11:30"), Pair("12:30", "14:30"), Pair("14:30", "15:00"), Pair("16:00", "17:00"))
val bounds2 = Pair("10:00", "18:30")
const val duration = 30
//
// output should be [["11:30", "12:00"], ["15:00", "16:00"], ["18:00", "18:30"]]
//
fun main() {
    println("Person 1: bounds $bounds1\n        + meetings $calOne")
    println("Person 2: bounds $bounds2\n        + meetings $calTwo")

    val combinedCal = listOf(
        listOf(Pair("0:00", min0Max1(1, bounds1.first, bounds2.first))),
        (calOne + calTwo).sortedBy { it.first.split(':').first().toInt() * 60 + it.first.split(':').last().toInt() },
        listOf(Pair(min0Max1(0, bounds1.second, bounds2.second), "24:00"))
    ).flatten()
//    println("combinedCal:\n$combinedCal\n")

    val avails = simplify(combinedCal)
    println("\nAvailable blocks of time for a $duration-minute meeting:\n  $avails")
}
//
fun min0Max1(option: Int, here: String, there: String): String{
    val times = mutableListOf<Int>()
    listOf(here, there).forEach { times.add(it.split(':').first().toInt() * 60 + it.split(':').last().toInt()) }
    return if (times[option] == times.minOrNull()) here else there
}
//
fun simplify(combinedCal: List<Pair<String, String>>): List<Pair<String, String>> {
    var tempEnd = combinedCal.first().second
    val avBlocks = mutableListOf<Pair<String, String>>()
    for (comp in 1..combinedCal.lastIndex) {
        val nextStart = combinedCal[comp]		// still in string
        if (canMeet(tempEnd, nextStart.first)) avBlocks.add(Pair(tempEnd, nextStart.first))
//        println("tempEnd: $tempEnd, comp: $comp, combinedCal[comp]: ${combinedCal[comp]}, abBlocks: $avBlocks")
        tempEnd = min0Max1(1, tempEnd, nextStart.second)
    }
    return avBlocks.toList()
}
//
fun canMeet(end: String, start: String): Boolean {
    return (((start.split(':').first().toInt() * 60 + start.split(':').last().toInt())
            - (end.split(':').first().toInt() * 60 + end.split(':').last().toInt()))
            >= duration)
}
//
//
////[ [a,b], [c,d], ...]
// c - b >= duration			->	add [b, c] to avBlocks;
// 									replace [a,b], [c,d] with [a,d]	->	replace [a,b] with [a,maxOf(b,d)], then delete [c,d]
// b > d						->	replace [a,b], [c,d] with [a,b] ->	replace [a,b] with [a,maxOf(b,d)], then delete [c,d]
// else b in (c-duration+1)..d	->	replace [a,b], [c,d] with [a,d]	->	replace [a,b] with [a,maxOf(b,d)], then delete [c,d]
//
//
//
//
