//import com.soywiz.kds.iterators.fastForEach
//import com.soywiz.klock.hr.hrMilliseconds
//import com.soywiz.klock.milliseconds
//import com.soywiz.korev.Key
//import com.soywiz.korev.KeyEvent
//import com.soywiz.korge.*
//import com.soywiz.korge.input.Input
//import com.soywiz.korge.view.*
//import com.soywiz.korim.color.Colors
//import com.soywiz.korim.format.readBitmap
//import com.soywiz.korio.file.std.resourcesVfs
//import com.soywiz.korma.geom.vector.rect
//import com.soywiz.korma.geom.vector.roundRect
//
//var cellSize = 0.0
//val newBoardNRow: Int = 20
//val newBoardNCol: Int = 10
//var boardWidth = 0.0
//var boardHeight = 0.0
//var leftIndent = 0.0
//
//
//var topIndent = 50.0
//suspend fun main() = Korge(width = 600, height = 800, bgcolor = Colors.DARKSEAGREEN) {
//// width, height here is original pop up window size, can be adjusted by dragging
////	color is the background color of this whole window
//
//    val input = views.input
//    boardWidth = views.virtualWidth * 0.6
//    cellSize = boardWidth / (newBoardNCol + 1)
//    boardHeight = 50.0 + newBoardNRow * cellSize
//    leftIndent = views.virtualWidth * 0.05
//
//
//    topIndent = 50.0
//
//
//    val boardField = roundRect(
//            width = boardWidth,
//            height = boardHeight,
//            rx = 5.0,
//            color = Colors.BEIGE,
//            autoScaling = true) {
//        position(leftIndent, topIndent)
//    }
//
//    addChild(boardField)
//
//    renderNextBlock()
//    renderSide()
//
//
//    var isThisBlockDone = false
//    var newBlockItem = generateANewBlock()
//    var bgBoardOfCells = newBoardOfCells
//    val boardHelper = BoardCollisionHelper(bgBoardOfCells)
//
//
//
//    val boardOfCellsGraph = graphics {
//        position(leftIndent, topIndent)
//        drawBoardOfCells(bgBoardOfCells, cellSize)
//    }
//
//    var currentBlock = createBlock(newBlockItem, input)
//    var currentBlockItem = newBlockItem
//    var gravityCounter = 0.0
//    var gravityRate = 3
//    var gravityThreshold = 60.0
//
//    addHrUpdater { dt ->
//        val scale = dt / 16.6666.hrMilliseconds
//        gravityCounter += gravityRate * scale
//        if (gravityCounter > gravityThreshold) {
//            gravityCounter = 0.0
//            val (x, y) = currentBlockItem.location
//            if (!boardHelper.isBlockHitBottom(currentBlockItem, y + 1) &&
//                    !boardHelper.isBlockOverlapping(currentBlockItem, x, y + 1)
//            ) {
//                currentBlockItem.location.y += 1 }
//            else {
//                //WE need to place block and create a new one
//                //paint old block onto cells board of cells
//                boardOfCellsGraph.clear()
//                bgBoardOfCells = placeBlockItemOnBoardOfCells(currentBlockItem, bgBoardOfCells)
//                boardOfCellsGraph.drawBoardOfCells(bgBoardOfCells, cellSize)
//                currentBlock.removeFromParent()
//                currentBlockItem = generateANewBlock()
//                currentBlock = createBlock(currentBlockItem, input)
//            }
//        }
//    }
//
//}
//
//
//
//private fun Stage.createBlock(newBlockItem: BlockItem, input: Input): Graphics {
//    var counter = 0.0
//    val blockItemGraph = graphics {
//        position(newBlockItem.location)
//        drawShape(newBlockItem, cellSize)
//    }
//    blockItemGraph.addHrUpdater { dt ->
//        val scale = dt / 16.6666.hrMilliseconds
//        val rotationIndex = newBlockItem.rotationIndex
//        fun holdKeyMove(key: Key) {
//            if (input.keys.pressing(key)) {
//                if (input.keys.justPressed(key)) {
//                    counter = 0.0
//                }
//                //can collect the "scale" as time. We can expect the counter to reach 60 every second (+- a few)
//                counter += scale
//                //I'm waiting for 5 updates before triggering button release
//                if (counter > 5) input.keys.triggerKeyEvent(KeyEvent(key = key))
//            }
//
//        }
//        holdKeyMove(Key.LEFT)
//        holdKeyMove(Key.RIGHT)
//        holdKeyMove(Key.DOWN)
//
//
//        fun pressKeyMoveBlock(key: Key) {
//            when (key) {
//                Key.LEFT -> if (input.keys.justPressed(key)) {
//                    if (newBlockItem.location.x - 1 >= 0) {
//                        newBlockItem.location.x -= 1
//                    } else {
//                        newBlockItem.location.x
//                    }
//                }
//                Key.RIGHT -> if (input.keys.justPressed(key)) {
//                    if (newBlockItem.location.x + 1 + newBlockItem.shape.width <= newBoardNCol) {
//                        newBlockItem.location.x += 1
//                    } else {
//                        newBlockItem.location.x
//                    }
//                }
//                Key.DOWN -> if (input.keys.justPressed(key)) {
//                    if (newBlockItem.location.y + 1 + newBlockItem.shape.height <= newBoardNRow) {
//                        newBlockItem.location.y += 1
//                    } else {
//                        newBlockItem.location.y
//                    }
//                }
//                Key.Q -> if (input.keys.justPressed(key) && canBlockRotation(newBlockItem)) newBlockItem.rotateCounterClockwise()
//                Key.E -> if (input.keys.justPressed(key) && canBlockRotation(newBlockItem)) newBlockItem.rotateClockwise()
//                else -> newBlockItem.location.x -= 0
//            }
//            if (rotationIndex != newBlockItem.rotationIndex) {
//                clear()
//                drawShape(newBlockItem, cellSize)
//            }
//        }
////
//        pressKeyMoveBlock(Key.LEFT)
//        pressKeyMoveBlock(Key.RIGHT)
//        pressKeyMoveBlock(Key.DOWN)
//        pressKeyMoveBlock(Key.Q)
//        pressKeyMoveBlock(Key.E)
//
//        position(leftIndent + newBlockItem.location.x * (1 + cellSize), topIndent + newBlockItem.location.y * (1 + cellSize))
//
//    }
//    return blockItemGraph
//}
//
//private suspend fun Stage.renderSide() {
//    val spriteMap = resourcesVfs["spritePikachu2.png"].readBitmap()
//    val explosionAnimation = SpriteAnimation(
//            spriteMap = spriteMap,
//            spriteWidth = 111,
//            spriteHeight = 119,
//            marginTop = 0,
////			marginLeft = (views.virtualWidth * 0.3).toInt(),
//            marginLeft = 0,
//            columns = 6,
//            rows = 1,
//            offsetBetweenColumns = 6,
//            offsetBetweenRows = 0
//    )
//
//    val explosion = sprite(explosionAnimation) {
//        position(views.virtualWidth * 0.72, 50 + cellSize * 7)
//    }
//    explosion.playAnimationLooped(spriteDisplayTime = 300.milliseconds)
//
//
//    val stackRowsBlockField = roundRect(
//            cellSize * 4 + 2,
//            cellSize * 2,
//            5.0,
//            5.0,
//            Colors.FLORALWHITE) {
//        position(views.virtualWidth * 0.7, 50 + cellSize * 12)
//    }
//
//    text("STACKROWS", cellSize * 0.5, Colors.BLUE) {
//        centerXOn(stackRowsBlockField)
//        centerYBetween(50 + cellSize * 12, 50 + cellSize * 13)
//    }
//
//    val scoreBlockField = roundRect(
//            cellSize * 4 + 2,
//            cellSize * 2,
//            5.0,
//            5.0,
//            Colors.FLORALWHITE) {
//        position(views.virtualWidth * 0.7, 50 + cellSize * 15)
//    }
//
//    text("SCORE", cellSize * 0.5, Colors.BLUE) {
//        centerXOn(scoreBlockField)
//        centerYBetween(50 + cellSize * 15, 50 + cellSize * 16)
//    }
//
//
//    val pauseBlockField = roundRect(
//            cellSize * 2,
//            cellSize,
//            20.0,
//            20.0,
//            Colors.FLORALWHITE) {
//        position(views.virtualWidth * 0.7, 50 + cellSize * 18)
//    }
//
//    text("PAUSE", cellSize * 0.4, Colors.BLUE) {
//        centerXOn(pauseBlockField)
//        centerYBetween(50 + cellSize * 18, 50 + cellSize * 19)
//    }
//
//    val stopBlockField = roundRect(
//            cellSize * 1.8,
//            cellSize,
//            20.0,
//            20.0,
//            Colors.FLORALWHITE) {
//        position(views.virtualWidth * 0.82, 50 + cellSize * 18)
//    }
//
//    text("STOP", cellSize * 0.4, Colors.BLUE) {
//        centerXOn(stopBlockField)
//        centerYBetween(50 + cellSize * 18, 50 + cellSize * 19)
//    }
//
//
//    val startBlockField = roundRect(
//            cellSize * 3,
//            cellSize,
//            20.0,
//            20.0,
//            Colors.FLORALWHITE) {
//        position(views.virtualWidth * 0.73, 50 + cellSize * 20)
//    }
//    text("START", cellSize * 0.5, Colors.BLUE) {
//        centerXOn(startBlockField)
//        centerYBetween(50 + cellSize * 20, 50 + cellSize * 21)
//    }
//}
//
//private fun Stage.renderNextBlock() {
//    val nextBlockWordField = roundRect(
//            cellSize * 4 + 2,
//            cellSize,
//            10.0,
//            10.0,
//            Colors.FLORALWHITE) {
//        position(views.virtualWidth * 0.7, 50.0)
//    }
//
//
//    val nextBlockField = roundRect(
//            cellSize * 4 + 2,
//            cellSize * 4,
//            5.0,
//            5.0,
//            Colors.FLORALWHITE) {
//        position(views.virtualWidth * 0.7, 50 + cellSize)
//    }
//
//    text("NEXT", cellSize * 0.5, Colors.BLUE) {
//        centerXOn(nextBlockField)
//        centerYBetween(50.0, 50 + cellSize)
//    }
//
//    graphics {
//        position(views.virtualWidth * 0.7, 50.0)
//        fill(Colors.LIGHTSTEELBLUE) {
//            for (i in 0 until 4) {
//                for (j in 0 until 4) {
//                    roundRect((1 + cellSize) * i, cellSize + (1 + cellSize) * j, cellSize, cellSize, 2.0, 2.0)
//                }
//            }
//
//        }
//    }
//}
//
//
////
////			var boardOfBoolean = bgBoardOfCells.convertToBooleanBoard()
////			var blockItemOnBoard = BlockItemOnBoard(newBlockItem, bgBoardOfCells)
//////
////			isThisBlockDone = blockItemOnBoard.isBlockHitBottom() ||
////					blockItemOnBoard.isBlockHitStack() ||
////					blockItemOnBoard.isBlockInsideBoard()
////			println("isThisBlockDone2 = $isThisBlockDone")
////
////			if (isThisBlockDone) {
////				placeBlockItemOnBoardOfCells(newBlockItem, bgBoardOfCells)
//////				generateANewBlock()
////			}
////		}
////	}
//
////	Block AREA END -------------------------------------
//
//
//fun canBlockRotation(blockItem: BlockItem): Boolean {
//    return (blockItem.location.x + blockItem.shape.height <= newBoardNCol) && (blockItem.location.y + blockItem.shape.width <= newBoardNRow)
//}
//
//
//private fun Graphics.drawShape(Shape: BlockItem, cellSize: Double) {
//    fill(Shape.color) {
//        Shape.shape.getPositionsWithValue(true).fastForEach { (x, y) ->
//            rect(10 + (1 + cellSize) * x, 15 + (1 + cellSize) * y, cellSize, cellSize)
//        }
//    }
//}
//
//
//private fun Graphics.drawBoardOfCells(boardOfCells: BoardOfCells, cellSize: Double) {
//
//    (0 until boardOfCells.height).forEach { y ->
//        (0 until boardOfCells.width).forEach { x ->
//            fill(boardOfCells.boardOfCells[x, y].col) {
//                rect(10 + (1 + cellSize) * x, 15 + (1 + cellSize) * y, cellSize, cellSize)
//            }
//        }
//    }
//}
//
//
////fun Graphics.drawBlockOfCells(blockOfCells: BlockOfCells, cellSize: Double) {
////	for (y in 0 until blockOfCells.height) {
////		for (x in 0 until blockOfCells.width) {
////			fill(blockOfCells.getCellColor(x,y)) {
////				rect(10 + (1 + cellSize) * x, 15 + (1+ cellSize) * y, cellSize, cellSize)
////			}
////		}
////	}
////}
////
//
//
////
////fun isThisBlockDone(newBlockItem:BlockItem, bgBoardOfCells: BoardOfCells): Boolean {
////	var blockItemOnBoard = BlockItemOnBoard(newBlockItem, bgBoardOfCells)
////	var boardOfBoolean = bgBoardOfCells.convertToBooleanBoard()
////	var isThisBlockDone = false
////
////	println("isThisBlockDone2 = $isThisBlockDone")
////
////	isThisBlockDone = blockItemOnBoard.isBlockHitBottom() ||
////			blockItemOnBoard.isBlockHitStack(blockItemOnBoard.blockItem) ||
////			blockItemOnBoard.isBlockInsideBoard()
////
////	return isThisBlockDone
////
////}
//
//
//
//
