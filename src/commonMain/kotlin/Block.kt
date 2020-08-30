import com.soywiz.kds.Array2
import com.soywiz.korev.GameButton
import com.soywiz.korev.Key
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korma.geom.Point
import kotlin.random.Random


val newBlockLocationX = 3
val newBlockLocationY = 0
val newBlockPoint = Point()

enum class BlockType(val shapes: List<Array2<Boolean>>) {
    I(listOf(
            shape("####"),
            shape("""
				#
				#
				#
				#
			""".trimIndent())
    )),
    O(listOf(shape("""
        ##
        ##
    """.trimIndent()))),
    T(listOf(shape("""
        .#.
        ###
    """.trimIndent()),
            shape("""
        #.
        ##
        #.
    """.trimIndent()),
            shape("""
        ###
        .#.
    """.trimIndent()),
            shape("""
                .#
                ##
                .#
            """.trimIndent())
    )),
    Z(listOf(
            shape("""
                ##.
                .##
            """.trimIndent()),
            shape("""
        .#
        ##
        #.
    """.trimIndent())
    )),

    S(listOf(
            shape("""
                .##
                ##.
            """.trimIndent()),
            shape("""
        #.
        ##
        .#
    """.trimIndent())
    )),

    J(listOf(shape("""
        .#
        .#
        ##
    """.trimIndent()),
            shape("""
        #..
        ###
    """.trimIndent()),
            shape("""
        ##
        #.
        #.
    """.trimIndent()),
            shape("""
        ###
        ..#
    """.trimIndent())
    )),
    L(listOf(shape("""
        #.
        #.
        ##
    """.trimIndent()),
            shape("""
        ###
        #..
    """.trimIndent()),
            shape("""
        ##
        .#
        .#
    """.trimIndent()),
            shape("""
        ..#
        ###
    """.trimIndent())))
}
data class Dimension(val width : Int, val height: Int)

typealias TetrisBlock = Array2<Boolean>

fun shape(shape: String): Array2<Boolean> {
    return Array2.Companion.invoke(shape) { char, x, y -> char == '#' }
}

data class BlockItem(var rotationIndex: Int,
                     val shapeTransformations: List<Array2<Boolean>>,
                     val location: Point,
                     val color: RGBA) {

    val shape get() = shapeTransformations[rotationIndex]
    fun rotateClockwise() {
        rotationIndex++
        if (rotationIndex >= shapeTransformations.size)
            rotationIndex -= shapeTransformations.size
    }

    fun rotateCounterClockwise() {
        rotationIndex--
        if (rotationIndex < 0)
            rotationIndex += shapeTransformations.size
    }

    fun positionsNotEmpty(): List<Pair<Int, Int>> {
       return shape.getPositionsWithValue(true)
    }

}

fun generateANewBlock(): BlockItem {
    val newBlockIndexType =Random.nextInt(7)
    val newBlockLocation = Point(x = newBlockLocationX, y =newBlockLocationY)
    val blockShape = when(newBlockIndexType) {
        0 -> BlockType.I;
        1 -> BlockType.O;
        2 -> BlockType.T;
        3 -> BlockType.S;
        4 -> BlockType.Z;
        5 -> BlockType.J;
        6 -> BlockType.L;
        else -> BlockType.I;
    }
    val color = blockColor(blockShape)
    return BlockItem(0, blockShape.shapes, newBlockLocation,color);
}

fun blockColor(shapeTransformations: BlockType): RGBA {
   return  when(shapeTransformations) {
       BlockType.I -> Colors.LIGHTPINK;
       BlockType.O -> Colors.TOMATO;
       BlockType.T -> Colors.CORNFLOWERBLUE;
       BlockType.S -> Colors.VIOLET;
       BlockType.Z -> Colors.YELLOW;
       BlockType.J -> Colors.LIGHTSEAGREEN;
       BlockType.L -> Colors.PEACHPUFF;
   }
}

//fun moveBlockItem(Key:String){
//
//    if (input.keys.justPressed(GameButton.LEFT)) {
//        if(newBlock.location.x -1 >= 0){newBlock.location.x -= 1}
//        else{newBlock.location.x}}
//
//    if (input.keys.justPressed(Key.RIGHT)) {
//        if(newBlock.location.x +1 + newBlock.shape.width <= newBoardNCol){newBlock.location.x += 1}
//        else{newBlock.location.x}}
//
//    if (input.keys.justPressed(Key.DOWN)) {
//        if(newBlock.location.y +1 + newBlock.shape.height <= newBoardNRow){newBlock.location.y += 1}
//        else{newBlock.location.y}}
//
//    if (input.keys.justPressed(Key.Q)) newBlock.rotateCounterClockwise()
//    if (input.keys.justPressed(Key.E)) newBlock.rotateClockwise()
//}

