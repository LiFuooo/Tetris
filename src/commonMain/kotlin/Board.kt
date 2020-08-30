import com.soywiz.kds.Array2

//val newBoardNRow: Int = 20
//val newBoardNCol: Int = 10

val newBoard = createEmptyBoard()


class BoardCollisionHelper(private val boardOfCells: BoardOfCells) {
    private val board get() = boardOfCells.convertToBooleanBoard()
    private val boardWidth = board.width
    private val boardHeight = board.height


    fun isBlockHitBottom(blockItem: BlockItem, y: Double): Boolean {
        return (y + blockItem.shape.height) > boardHeight
    }


    fun isBlockOverlapping(blockItem: BlockItem, xIn: Double, yIn: Double): Boolean {
        val notEmptyBoardPositions = board.getPositionsWithValue(true)
        val notEmptyBlockPositions = blockItem.positionsNotEmpty()
        var notEmptyBlockPositions1 = notEmptyBlockPositions.map { (x, y) -> Pair<Int, Int>((x + xIn).toInt(), (y + yIn).toInt()) }

        return notEmptyBoardPositions.intersect(notEmptyBlockPositions1.asIterable()).isNotEmpty()
    }

    private fun isCompleteRow(row: List<Boolean>, index: Int) = if (row.all { it }) index else null

    fun boardCompleteChecker(): List<Int> {
        return board.chunked(boardWidth).mapIndexedNotNull { index, list ->
            isCompleteRow(list, index)
        }
    }

    fun completeRowsN(): Int {
        return boardCompleteChecker().size
    }

    fun clearCompleteRows() {
        val completeRows = boardCompleteChecker()
        val cloneBoardOfCells = boardOfCells.boardOfCells
        val notCompleteRows = cloneBoardOfCells.chunked(boardWidth).filterIndexed { index, list -> index !in completeRows }
        val newRows = (completeRows.indices).map { createNewRowOfCells() }
        println(newRows)
        println(notCompleteRows)
        boardOfCells.boardOfCells = Array2(newRows + notCompleteRows)
    }

    private fun printBoardChunk(boardChunkIn: List<List<Cell>>) {
        (boardChunkIn.indices).forEach {
            var listOfValue = boardChunkIn[it].map { cell -> cell.value }
        }
    }

    fun clearWholeBoard() {
        (0 until boardWidth).forEach{x ->
            (0 until boardHeight).forEach { y->
                boardOfCells.boardOfCells[x,y] = createEmptyCell()
            }

        }
    }

    private fun createNewRowOfCells(): List<Cell> {
        return (0 until newBoardNCol).map { _ ->
            createEmptyCell()
        }

    }


    private fun inside(x: Int, y: Int): Boolean =
            x >= 0 &&
                    y >= 0 &&
                    x <= boardWidth &&
                    y <= boardHeight

    fun isBlockInsideBoard(blockItem: BlockItem): Boolean {
        val blockHeight = blockItem.shape.height
        val blockWidth = blockItem.shape.width
        val x = (blockItem.location.x).toInt()
        val y = (blockItem.location.y).toInt()

        return inside(x, y) &&
                inside(x + blockWidth, y) &&
                inside(x, y + blockHeight) &&
                inside(x + blockWidth, y + blockHeight)
    }


    fun countStacks(): Int {
        var emptyRowList = mutableListOf<Int>()
        var boardChunk = board.chunked(boardWidth)

        (boardChunk.indices).forEach {
            if (boardChunk[it].all { it -> !it }) {
                emptyRowList.add(it)
            }
        }
        return boardHeight - emptyRowList.size
    }

}


fun createEmptyBoard(): Array2<Boolean> {
    return Array2<Boolean>(width = newBoardNCol, height = newBoardNRow, fill = false)
}


fun isGameOver(boardOfCells: BoardOfCells): Boolean {
    var board = boardOfCells.convertToBooleanBoard()
    var rowZero = mutableListOf<Boolean>()
    (0..newBoardNCol).forEach {
        rowZero.add(board[it, 0])
    }

    return rowZero.any { true }
}


