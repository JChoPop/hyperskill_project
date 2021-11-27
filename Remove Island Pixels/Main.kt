// By HJC
//remove island pixels
//
val input = listOf( listOf(1, 0, 0, 0, 0, 0),
                    listOf(0, 1, 0, 1, 1, 1),
                    listOf(0, 0, 0, 0, 1, 0),
                    listOf(1, 1, 0, 0, 1, 0),
                    listOf(1, 0, 0, 0, 0, 0),
                    listOf(1, 0, 0, 0, 1, 1))
//
val conus = mutableMapOf<Int, MutableSet<Int>>()
var exitFlag = false
//
fun main() {
    for (i in 0..input.lastIndex) {
        for (j in 0..input[i].lastIndex) {
            if ((i in 1..(input.lastIndex-1)) && (j in 1..(input[i].lastIndex-1))) continue
            search(i, j)
        }
    }
    println("conus: $conus")
    println("output:")
    val output = mutableListOf<MutableList<Int>>()
    var value: Int
    for (i in 0..input.lastIndex) {
        output.add(mutableListOf<Int>())
        if (conus.contains(i)) {
            for (j in 0..input[i].lastIndex) {
                if (conus[i]!!.contains(j)) value = 1 else value = 0
                output[i].add(value)
            }
        } else { output[i].add(0) }
        println("   ${output[i]}")
    }
}
//
fun search(i: Int, j: Int) {
    if (i in 0..input.lastIndex && j in 0..input[i].lastIndex && input[i][j] == 1) {
        when {
            conus[i].isNullOrEmpty() -> { conus[i] = mutableSetOf(j); exitFlag = false }
            conus[i]!!.contains(j) -> exitFlag = true
            else -> { conus[i]!!.add(j); exitFlag = false }
        }
        if (!exitFlag) {
            search(i, j-1)
            search(i-1, j)
            search(i, j+1)
            search(i+1, j)
        }
    }
}