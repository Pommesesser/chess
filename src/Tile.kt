class Tile(
    var p: Piece?,
    val r: Int,
    val c: Int
) {
    init {
        if (!inBounds(r, c))
            throw IllegalArgumentException()
    }
}