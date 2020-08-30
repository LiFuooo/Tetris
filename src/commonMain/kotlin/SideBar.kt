//import java.io.File
//Next Bar
var nextBlockItem = generateANewBlock()



// stack Rows
fun countStackRows(boardOfCells: BoardOfCells): Int {
    val board = boardOfCells.convertToBooleanBoard()
    var count = 0
    (0 .. board.height).forEach {
        if(board.chunked(newBoardNCol)[it].all{false} ){
            count += 1
        }
    }
    return count
}

data class HighScoreEntry(val name : String, val score : Int)
// Score
fun score(scoreIn: Int, NComplete: Int): Int {
    return scoreIn + when(NComplete){
        1 -> 1
        2 -> 4
        3 -> 9
        4 -> 16
        else -> 0
    }
}



// pause
// Begin
// Stop



fun highestScore(sCoreIn: Int): String {

    val fileName = "src/resources/highestScore.txt"
    val scoreString = mutableListOf<Int>()
    scoreString.add(sCoreIn)
    val maxScore = scoreString.max()
//    File(fileName).printWriter().use { out ->
//
//        out.println("First line")
//        out.println("Second line")
//    }

    return maxScore.toString()
}