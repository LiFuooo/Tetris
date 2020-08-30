import com.soywiz.kds.iterators.fastForEach
import com.soywiz.klock.hr.hrMilliseconds
import com.soywiz.klock.milliseconds
import com.soywiz.korev.Key
import com.soywiz.korev.KeyEvent
import com.soywiz.korge.*
import com.soywiz.korge.input.Input
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.rect
import com.soywiz.korma.geom.vector.roundRect


var cellSize = 0.0
val newBoardNRow: Int = 20
val newBoardNCol: Int = 10
var boardWidth = 0.0
var boardHeight = 0.0
var leftIndent = 0.0
var isGamePause = false
var isGameOver = false
var startNewGame = false

var topIndent = 50.0
suspend fun main() = Korge(width = 600, height = 800, bgcolor = Colors.DARKSEAGREEN) {


    val input = views.input
    boardWidth = views.virtualWidth * 0.6
    cellSize = boardWidth / (newBoardNCol + 1)
    boardHeight = 50.0 + newBoardNRow * cellSize
    leftIndent = views.virtualWidth * 0.05


    topIndent = 50.0


    val boardField = roundRect(
            width = boardWidth,
            height = boardHeight,
            rx = 5.0,
            color = Colors.BEIGE,
            autoScaling = true) {
        position(leftIndent, topIndent)
    }

    addChild(boardField)
    renderNextBlock()
    renderSide()


    var isThisBlockDone = false
    var newBlockItem = generateANewBlock()
    var bgBoardOfCells = newBoardOfCells
    var boardHelper = BoardCollisionHelper(bgBoardOfCells)
    var rotationIndex = newBlockItem.rotationIndex






    var boardOfCellsGraph = graphics {
        position(leftIndent, topIndent)
        drawBoardOfCells(bgBoardOfCells, cellSize)
    }

    var nextBlockItemGraph = graphics {
        position( views.virtualWidth * 0.7-10,   cellSize+50.0-15)
        drawShape(nextBlockItem, cellSize)
    }

    var currentBlock = createBlock(newBlockItem, input)


    var currentBlockItem = newBlockItem
    var gravityCounter = 0.0
    var gravityRate = 3
    var gravityThreshold = 60.0
    var gameScore = 0
    var stackCount = 0


//    SCORE Display
    var scoreDisplay = text(gameScore.toString(),cellSize * 0.5, Colors.BLUE) {
        centerXBetween(views.virtualWidth * 0.75, views.virtualWidth * 0.85)
        centerYBetween(65 + cellSize * 15, 65 + cellSize * 17)
        color = Colors.DARKGREEN
    }


// count stack rows
    var stackCountDisplay = text(stackCount.toString(), cellSize * 0.5, Colors.BLUE) {
        centerXBetween(views.virtualWidth * 0.75, views.virtualWidth * 0.85)
        centerYBetween(50 + cellSize * 13, 50 + cellSize * 14)
        color = Colors.RED
    }


    // highest score
    var highestScoreDisplay = text(highestScore(gameScore).toString(), cellSize * 0.5, Colors.BLUE) {
        centerXBetween(views.virtualWidth * 0.75, views.virtualWidth * 0.85)
        centerYBetween(50 + cellSize * 19, 50 + cellSize * 20)
        color = Colors.BLACK
    }



    addHrUpdater { dt ->
        if(startNewGame){
            boardOfCellsGraph.clear()
            currentBlock.removeFromParent()
            boardHelper.clearWholeBoard()
            bgBoardOfCells = newBoardOfCells
            boardOfCellsGraph.drawBoardOfCells(bgBoardOfCells, cellSize)
            currentBlock = createBlock(currentBlockItem, input)
            stackCount = boardHelper.countStacks()
            stackCountDisplay.text = stackCount.toString()

            gameScore = 0
            scoreDisplay.text = gameScore.toString()


            isGamePause = false
            isGameOver = false
            startNewGame = false

        }


        val scale = dt / 16.6666.hrMilliseconds
        gravityCounter += gravityRate * scale
        if (gravityCounter > gravityThreshold && !isGamePause) {
            gravityCounter = 0.0
            val (x, y) = currentBlockItem.location
            if (!boardHelper.isBlockHitBottom(currentBlockItem, y + 1) &&
                    !boardHelper.isBlockOverlapping(currentBlockItem, x, y + 1)
            ) {
                currentBlockItem.location.y += 1 }
            else {
                //WE need to place block and create a new one
                //paint old block onto cells board of cells
                boardOfCellsGraph.clear()
                bgBoardOfCells = placeBlockItemOnBoardOfCells(currentBlockItem, bgBoardOfCells)
                boardOfCellsGraph.drawBoardOfCells(bgBoardOfCells, cellSize)


                currentBlock.removeFromParent()
                nextBlockItemGraph.removeFromParent()
                currentBlockItem = nextBlockItem
                nextBlockItem = generateANewBlock()
                currentBlock = createBlock(currentBlockItem, input)

                nextBlockItemGraph = graphics {
                    position( views.virtualWidth * 0.7-10,   cellSize+50.0-15)
                    drawShape(nextBlockItem, cellSize)
                }


                stackCount = boardHelper.countStacks()
                stackCountDisplay.text = stackCount.toString()


//            get complete rows list
                val completeRows = boardHelper.boardCompleteChecker()
                gameScore = score(gameScore,boardHelper.completeRowsN())

                if(completeRows.isNotEmpty()) {
                    boardOfCellsGraph.clear()
                    boardHelper.clearCompleteRows()
                    boardOfCellsGraph.drawBoardOfCells(bgBoardOfCells, cellSize)
                    scoreDisplay.text = gameScore.toString()

                }

//                if game Over
                if(stackCount >= newBoardNRow)  isGameOver = true

                if(isGameOver){
                    isGamePause = true
                    highestScoreDisplay.text = highestScore(gameScore)
                    showGameOver()
                    println("startNewGame1 = $startNewGame")
                }

            }
        }
    }

}


private fun Stage.createBlock(newBlockItem: BlockItem, input: Input): Graphics {
    var counter = 0.0
    var rotationIndex = newBlockItem.rotationIndex
    val blockItemGraph = graphics {
        position(leftIndent + newBlockItem.location.x * (1 + cellSize), topIndent + newBlockItem.location.y * (1 + cellSize))
        drawShape(newBlockItem, cellSize)
    }







    var bgBoardOfCells = newBoardOfCells
    val boardHelper = BoardCollisionHelper(bgBoardOfCells)

    blockItemGraph.addHrUpdater { dt ->
        val scale = dt / 16.6666.hrMilliseconds

        fun holdKeyMove(key: Key) {
            if (input.keys.pressing(key) && !isGamePause) {
                if (input.keys.justPressed(key)) {
                    counter = 0.0
                }
                //can collect the "scale" as time. We can expect the counter to reach 60 every second (+- a few)
                counter += scale
                //I'm waiting for 5 updates before triggering button release
                if (counter > 5) input.keys.triggerKeyEvent(KeyEvent(key = key))
            }

        }
        holdKeyMove(Key.LEFT)
        holdKeyMove(Key.RIGHT)
        holdKeyMove(Key.DOWN)




        fun pressKeyMoveBlock(key: Key) {
            when (key) {
                Key.LEFT -> if (input.keys.justPressed(key) && canBlockMoveLeft(newBlockItem, bgBoardOfCells) && !isGamePause)
                    newBlockItem.location.x -= 1


                Key.RIGHT -> if (input.keys.justPressed(key) && canBlockMoveRight(newBlockItem, bgBoardOfCells)&& !isGamePause)
                    newBlockItem.location.x += 1


                Key.DOWN -> if (input.keys.justPressed(key)&& canBlockMoveDown(newBlockItem, bgBoardOfCells)&& !isGamePause)
                    newBlockItem.location.y += 1

                Key.SPACE -> if (input.keys.justPressed(key) && !isGamePause){
                            while(canBlockMoveDown(newBlockItem, bgBoardOfCells)){
                                newBlockItem.location.y += 1
                            }
                        }



                Key.Q -> if (input.keys.justPressed(key) &&
                        canBlockRotateCounterClockwise(newBlockItem, bgBoardOfCells) && !isGamePause) {
                    newBlockItem.rotateCounterClockwise()
                }



                Key.E -> if (input.keys.justPressed(key) &&
                        canBlockRotateClockwise(newBlockItem,bgBoardOfCells) && !isGamePause) {
                    newBlockItem.rotateClockwise()
                }

                else -> newBlockItem.location.x -= 0
            }


            if (rotationIndex != newBlockItem.rotationIndex && !isGamePause) {
                clear()
                drawShape(newBlockItem, cellSize)
                rotationIndex = newBlockItem.rotationIndex
            }
        }
//
        pressKeyMoveBlock(Key.LEFT)
        pressKeyMoveBlock(Key.RIGHT)
        pressKeyMoveBlock(Key.DOWN)
        pressKeyMoveBlock(Key.SPACE)
        pressKeyMoveBlock(Key.Q)
        pressKeyMoveBlock(Key.E)

        position(leftIndent + newBlockItem.location.x * (1 + cellSize), topIndent + newBlockItem.location.y * (1 + cellSize))

    }
    return blockItemGraph
}

private suspend fun Stage.renderSide() {
    val spriteMap = resourcesVfs["spritePikachu2.png"].readBitmap()
    val explosionAnimation = SpriteAnimation(
            spriteMap = spriteMap,
            spriteWidth = 111,
            spriteHeight = 119,
            marginTop = 0,
//			marginLeft = (views.virtualWidth * 0.3).toInt(),
            marginLeft = 0,
            columns = 6,
            rows = 1,
            offsetBetweenColumns = 6,
            offsetBetweenRows = 0
    )

    val explosion = sprite(explosionAnimation) {
        position(views.virtualWidth * 0.72, 50 + cellSize * 7)
    }
    explosion.playAnimationLooped(spriteDisplayTime = 300.milliseconds)


    val stackRowsBlockField = roundRect(
            cellSize * 4 + 2,
            cellSize * 2,
            5.0,
            5.0,
            Colors.FLORALWHITE) {
        position(views.virtualWidth * 0.7, 50 + cellSize * 12)
    }

    text("STACKROWS", cellSize * 0.5, Colors.BLUE) {
        centerXOn(stackRowsBlockField)
        centerYBetween(50 + cellSize * 12, 50 + cellSize * 13)
    }

    val scoreBlockField = roundRect(
            cellSize * 4 + 2,
            cellSize * 2,
            5.0,
            5.0,
            Colors.FLORALWHITE) {
        position(views.virtualWidth * 0.7, 50 + cellSize * 15)
    }

    text("SCORE", cellSize * 0.5, Colors.BLUE) {
        centerXOn(scoreBlockField)
        centerYBetween(50 + cellSize * 15, 50 + cellSize * 16)
    }

    val highestScoreBlockField = roundRect(
            cellSize * 4 + 2,
            cellSize * 2,
            5.0,
            5.0,
            Colors.FLORALWHITE) {
        position(views.virtualWidth * 0.7, 50 + cellSize * 18)
    }

    text("Highest Score", cellSize * 0.35, Colors.BLUE) {
        centerXOn(highestScoreBlockField)
        centerYBetween(50 + cellSize * 18, 50 + cellSize * 19)
    }


    val pauseBlockField = roundRect(
            cellSize * 2,
            cellSize,
            20.0,
            20.0,
            Colors.FLORALWHITE) {
        position(views.virtualWidth * 0.7, 50 + cellSize * 20.5)


    }

    val resumeBlockField = roundRect(
            cellSize * 2.0,
            cellSize,
            20.0,
            20.0,
            Colors.FLORALWHITE) {
        position(views.virtualWidth * 0.82, 50 + cellSize * 20.5)

    }

    text("Pause", cellSize* 0.4 , Colors.BLUE) {
        centerXOn(pauseBlockField)
        centerYBetween(50 + cellSize * 20.5, 50 + cellSize * 21.5)
        onClick {
            isGamePause = true
        }
    }



    text("Resume", cellSize * 0.4, Colors.BLUE) {
        centerXOn(resumeBlockField)
        centerYBetween(50 + cellSize * 20.5, 50 + cellSize * 21.5)
        onClick {
            isGamePause = false

        }

    }
}


private fun Stage.renderNextBlock() {
    val nextBlockWordField = roundRect(
            cellSize * 4 + 2,
            cellSize,
            10.0,
            10.0,
            Colors.FLORALWHITE) {
        position(views.virtualWidth * 0.7, 50.0)
    }


    val nextBlockField = roundRect(
            cellSize * 4 + 2,
            cellSize * 4,
            5.0,
            5.0,
            Colors.FLORALWHITE) {
        position(views.virtualWidth * 0.7, 50 + cellSize)
    }

    text("NEXT", cellSize * 0.5, Colors.BLUE) {
        centerXOn(nextBlockField)
        centerYBetween(50.0, 50 + cellSize)
    }

    graphics {
        position(views.virtualWidth * 0.7, 50.0)
        fill(Colors.LIGHTSTEELBLUE) {
            for (i in 0 until 4) {
                for (j in 0 until 4) {
                    roundRect((1 + cellSize) * i, cellSize + (1 + cellSize) * j, cellSize, cellSize, 2.0, 2.0)
                }
            }

        }
    }
}


fun isMoveLegal(blockItem: BlockItem, boardOfCells: BoardOfCells): Boolean {
    var boardHelper = BoardCollisionHelper(boardOfCells)
    val x  = blockItem.location.x
    val y = blockItem.location.y


    return !boardHelper.isBlockHitBottom(blockItem, y ) &&
            !boardHelper.isBlockOverlapping(blockItem, x, y) &&
            boardHelper.isBlockInsideBoard(blockItem)
}

fun canBlockMoveLeft(blockItem: BlockItem, boardOfCells: BoardOfCells): Boolean {
    var blockItemAfterMove =  BlockItem(
            rotationIndex = blockItem.rotationIndex,
            shapeTransformations = blockItem.shapeTransformations,
            location = Point(blockItem.location.x -1,blockItem.location.y),
            color = blockItem.color)

    return isMoveLegal(blockItemAfterMove,boardOfCells)
}

fun canBlockMoveRight(blockItem: BlockItem, boardOfCells: BoardOfCells): Boolean {
    var blockItemAfterMove =  BlockItem(
            rotationIndex = blockItem.rotationIndex,
            shapeTransformations = blockItem.shapeTransformations,
            location = Point(blockItem.location.x +1,blockItem.location.y),
            color = blockItem.color)
    return isMoveLegal(blockItemAfterMove,boardOfCells)
}

fun canBlockMoveDown(blockItem: BlockItem, boardOfCells: BoardOfCells): Boolean {
    var blockItemAfterMove =  BlockItem(
            rotationIndex = blockItem.rotationIndex,
            shapeTransformations = blockItem.shapeTransformations,
            location = Point(blockItem.location.x,blockItem.location.y + 1),
            color = blockItem.color)

    return isMoveLegal(blockItemAfterMove,boardOfCells)
}


fun canBlockRotateCounterClockwise(blockItem: BlockItem, boardOfCells: BoardOfCells): Boolean {
    var blockItemAfterMove =  BlockItem(
            rotationIndex = blockItem.rotationIndex ,
            shapeTransformations = blockItem.shapeTransformations,
            location = Point(blockItem.location.x, blockItem.location.y),
            color = blockItem.color)

    blockItemAfterMove.rotateCounterClockwise()
//    println("RotationIndexCountClockWise = ${blockItemAfterMove.rotationIndex}")
    return isMoveLegal(blockItemAfterMove,boardOfCells)
}


fun canBlockRotateClockwise(blockItem: BlockItem, boardOfCells: BoardOfCells): Boolean {
    var blockItemAfterMove =  BlockItem(
            rotationIndex = blockItem.rotationIndex ,
            shapeTransformations = blockItem.shapeTransformations,
            location = Point(blockItem.location.x, blockItem.location.y),
            color = blockItem.color)
    blockItemAfterMove.rotateClockwise()
//    println("RotationIndexClockWise = ${blockItemAfterMove.rotationIndex}")
    return isMoveLegal(blockItemAfterMove, boardOfCells)
}



fun isInside(x: Int, y: Int): Boolean = x >= 0 && y >= 0 && x < newBoardNCol && y < newBoardNRow

fun canBlockRotate(blockItem: BlockItem): Boolean {
    return (blockItem.location.x + blockItem.shape.height <= newBoardNCol) && (blockItem.location.y + blockItem.shape.width <= newBoardNRow)
}


private fun Graphics.drawShape(Shape: BlockItem, cellSize: Double) {
    fill(Shape.color) {
        Shape.shape.getPositionsWithValue(true).fastForEach { (x, y) ->
            rect(10 + (1 + cellSize) * x, 15 + (1 + cellSize) * y, cellSize, cellSize)
        }
    }
}


private fun Graphics.drawBoardOfCells(boardOfCells: BoardOfCells, cellSize: Double) {
    (0 until boardOfCells.height).forEach { y ->
        (0 until boardOfCells.width).forEach { x ->
            fill(boardOfCells.boardOfCells[x, y].col) {
                rect(10 + (1 + cellSize) * x, 15 + (1 + cellSize) * y, cellSize, cellSize)
            }
        }
    }
}


fun Stage.showGameOver(){
    val gameOverBanner = roundRect(
            cellSize * 8 ,
            cellSize * 4,
            5.0,
            5.0,
            Colors.FLORALWHITE) {
        position(views.virtualWidth*0.12, 50 + cellSize*8)
    }

    val gameOverText = text("Game Over", cellSize * 0.7, Colors.RED) {
        centerXOn(gameOverBanner)
        centerYBetween( 50+ cellSize*8,  50+ cellSize*10)
    }

    val playAgainButton = roundRect(
            cellSize * 5 ,
            cellSize ,
            5.0,
            5.0,
            Colors.BLUE) {
        position(views.virtualWidth * 0.21, 50 + cellSize * 10)
    }
       val playAgainText =  text("PLAY AGAIN", cellSize * 0.5, Colors.WHITE) {
            centerOn(playAgainButton)
            onClick {
                startNewGame = true
                println("startNewGame = $startNewGame")
                gameOverBanner.removeFromParent()
                gameOverText.removeFromParent()
                playAgainButton.removeFromParent()
                this.removeFromParent()
            }
        }


}







