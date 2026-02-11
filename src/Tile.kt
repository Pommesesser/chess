data class Tile(
    val p: Piece?,
    val r: Int,
    val c: Int
) {
    init {
        if (!inBounds(r, c))
            throw IllegalArgumentException()
    }
}