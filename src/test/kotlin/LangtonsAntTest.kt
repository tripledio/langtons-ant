import Direction.*
import SquareContent.*
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.damo.aspen.Test
import java.util.*

class LangtonsAntTest : Test({
    describe("An ant"){
        test("With no ticks the grid remains unchanged"){
            var grid = createGrid("""
                    ██████████
                    ██████████
                    ██████████
                    ██████████
                    ██████████
                    █████←████
                    ██████████
                    ██████████
                    ██████████
                    ██████████""")

            assertThat(represent(grid), equalTo(represent("""
                    ██████████
                    ██████████
                    ██████████
                    ██████████
                    ██████████
                    █████←████
                    ██████████
                    ██████████
                    ██████████
                    ██████████""")))
        }

        test("Turns right when on a black tile, moves forward, leaves it white"){
            var grid = createGrid("""
                    ██████████
                    ██████████
                    ██████████
                    ██████████
                    ██████████
                    █████←████
                    ██████████
                    ██████████
                    ██████████
                    ██████████""", BLACK)

            grid.tick()

            assertThat(represent(grid), equalTo(represent("""
                    ██████████
                    ██████████
                    ██████████
                    ██████████
                    █████↑████
                    █████□████
                    ██████████
                    ██████████
                    ██████████
                    ██████████""")))
        }

        test("Turns left when on a white tile, moves forward, leaves it black"){
            var grid = createGrid("""
                    ██████████
                    ██████████
                    ██████████
                    ██████████
                    ██████████
                    █████←████
                    ██████████
                    ██████████
                    ██████████
                    ██████████""", WHITE)

            grid.tick()

            assertThat(represent(grid), equalTo(represent("""██████████
                    ██████████
                    ██████████
                    ██████████
                    ██████████
                    ██████████
                    █████↓████
                    ██████████
                    ██████████
                    ██████████""")))
        }
    }
})

fun represent(input: String): String
    = withNewLines(input.filter { c -> !c.isWhitespace() }.map { c-> c.toString() }.joinToString(""), 10)

fun represent(grid: Grid): String = withNewLines(grid.squares.map { s -> when (s.content){
    BLACK -> '█'
    WHITE -> '□'
    ANT -> when (s.direction){
        NORTH -> '↑'
        WEST -> '←'
        EAST -> '→'
        SOUTH -> '↓'
        else -> ""
    }
    else -> ""
} }.joinToString(""), 10)

fun withNewLines(str: String, size: Int): String
    = IntRange(0, (str.length / size) - 1).map { i -> str.substring(i * size, (i+1) * size) + "\n" }.joinToString("")


fun createGrid(state: String, antSquare: SquareContent = BLACK): Grid =
    Grid(ArrayList(state
            .filter { c -> !c.isWhitespace() }
            .map { c -> when (c){
                    '↑' -> Square(ANT, NORTH, antSquare)
                    '←' -> Square(ANT, WEST, antSquare)
                    '→' -> Square(ANT, EAST, antSquare)
                    '↓' -> Square(ANT, SOUTH, antSquare)
                    '█' -> Square(BLACK)
                    '□' -> Square(WHITE)
                    else -> TODO()
                 }
            }))

class Grid(var squares: MutableList<Square>) {

    fun tick() {
        val ant = findAnt()
        val location = squares.indexOf(ant)

        flipSquare(ant, location)
        moveToNewLocation(ant, location)
    }

    private fun findAnt(): Square = squares.find { t->t.content == ANT }!!

    private fun flipSquare(ant: Square, location: Int) {
        squares[location] = Square(ant.originalContent.flippedColor())
    }

    private fun moveToNewLocation(ant: Square, location: Int) {
        val newDirection = ant.determineNewDirection()
        squares[newAntPosition(location, newDirection)] = Square(ANT, newDirection)
    }

    private fun newAntPosition(location: Int, direction: Direction) = location + direction.moves
}

data class Square(val content: SquareContent, val direction: Direction = Direction.NONE, val originalContent: SquareContent = content) {
    fun determineNewDirection(): Direction = direction.determineNewDirection(originalContent)
}

enum class SquareContent {
    WHITE {
        override fun flippedColor(): SquareContent = BLACK
    },
    BLACK {
        override fun flippedColor(): SquareContent  = WHITE
    },
    ANT {
        override fun flippedColor(): SquareContent = ANT
    };

    abstract fun flippedColor(): SquareContent
}

enum class Direction(val moves: Int) {
    NORTH(-10) {
        override fun determineNewDirection(content: SquareContent): Direction =
            when (content) {
                BLACK -> WEST
                else -> EAST
            }
    },
    EAST(1) {
        override fun determineNewDirection(content: SquareContent): Direction =
            when (content){
                BLACK -> SOUTH
                else -> NORTH
            }
    },
    SOUTH(10) {
        override fun determineNewDirection(content: SquareContent): Direction =
            when (content){
                BLACK -> EAST
                else -> WEST
            }
    },
    WEST(-1) {
        override fun determineNewDirection(content: SquareContent): Direction =
            when (content){
                BLACK -> NORTH
                else -> SOUTH
            }
    },
    NONE(0) {
        override fun determineNewDirection(content: SquareContent): Direction = NONE
    };

    abstract fun determineNewDirection(content: SquareContent): Direction
}
