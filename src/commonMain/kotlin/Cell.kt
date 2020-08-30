import com.soywiz.kds.Array2
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korma.geom.Point


class Cell(var col: RGBA, var value:Boolean)

val emptyCellColor = Colors.LIGHTSTEELBLUE
fun createEmptyCell(): Cell = Cell(col = Colors.LIGHTSTEELBLUE, value = false)


fun blockItemToBlockCells(blockItem: BlockItem): BlockOfCells {
//    var rotationIndex = blockItem.rotationIndex
//    val shapeTransformations = blockItem.shapeTransformations
    val location = blockItem.location
    val shape = blockItem.shape
    val shapeColor = blockItem.color
    val shapeWidth = shape.width
    val shapeHeight = shape.height
    var outputBlock = newBlockOfCells(shapeWidth,shapeHeight, createEmptyCell())

    outputBlock.location = location

    (0 until shapeWidth).forEach { x ->
        (0 until shapeHeight).forEach { y ->
            if(shape[x,y]){
                outputBlock.setCellFill(x,y,true)
                outputBlock.setCellColor(x,y,shapeColor)

            } else{
                outputBlock.setCellFill(x,y,false)
                outputBlock.setCellColor(x,y,emptyCellColor)
            }
        }
    }


    return outputBlock
}




class BlockOfCells(var blockOfCells: Array2<Cell>){
    var location = Point()
    var width = blockOfCells.width
    var height = blockOfCells.height

    fun getCellFill( x: Int, y: Int): Boolean = blockOfCells[x, y].value
    fun getCellColor(x: Int, y: Int): RGBA  = blockOfCells[x, y].col
    fun setCellFill(x: Int, y: Int, value: Boolean): Unit = run { blockOfCells[x, y].value = value }
    fun setCellColor(x: Int, y: Int, color: RGBA): Unit = run { blockOfCells[x, y].col = color }

}

fun newBlockOfCells(width: Int, height: Int, data: Cell): BlockOfCells {
    return BlockOfCells(Array2<Cell>(width,height,data))
}


class BoardOfCells(var boardOfCells: Array2<Cell>) {
    private val boardIn get() = boardOfCells
    val width:Int = boardIn.width
    val height:Int = boardIn.height

    fun getCellFill(x: Int, y: Int): Boolean  =  boardIn[x, y].value
    fun getCellColor(x: Int, y: Int): RGBA  = boardIn[x, y].col

    fun setCellFill(x: Int, y: Int, value: Boolean): Unit = run { boardIn[x, y].value = value }
    fun setCellColor(x: Int, y: Int, color: RGBA): Unit = run { boardIn[x, y].col = color }



    fun convertToBooleanBoard(): Array2<Boolean> {
        val width:Int = boardOfCells.width
        val height:Int = boardOfCells.height
        var boardValue = Array2<Boolean>(width = width, height = height, fill = false)
        (0 until height).forEach { y ->
            (0 until width).forEach { x ->
                boardValue[x,y] = boardOfCells[x,y].value
            }
        }
        return boardValue

    }
     }





fun createBoardOfCells( width: Int, height:Int ): BoardOfCells {
    return BoardOfCells(Array2<Cell>(width = newBoardNCol,height = newBoardNRow){createEmptyCell()})
}

val newBoardOfCells = createBoardOfCells(width = newBoardNCol,height = newBoardNRow)
//var newBoardOfCells = BoardOfCells(newBoardOfCells1)
//val newBoardOfCells1: Array2<Cell> = Array2<Cell>(width = newBoardNCol,height = newBoardNRow){getEmptyCell()}

fun placeBlockItemOnBoardOfCells(blockItem:BlockItem, backgroundBoard: BoardOfCells): BoardOfCells {
    val blockLocationX = (blockItem.location.x).toInt()
    val blockLocationY = (blockItem.location.y).toInt()
    val notEmptyBlockPositions = blockItem.positionsNotEmpty()
    val outBoardOfCells = newBoardOfCells



    notEmptyBlockPositions.forEach {
        outBoardOfCells.boardOfCells[blockLocationX + it.first, blockLocationY + it.second].value = true
        outBoardOfCells.boardOfCells[blockLocationX + it.first, blockLocationY + it.second].col = blockItem.color
    }


    return outBoardOfCells
}

