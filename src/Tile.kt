data class Tile(
    val p: Piece?,
    val r: Int,
    val c: Int
) {
    init {
        require(inBounds(r, c))
    }

    fun inBounds(r: Int, c: Int): Boolean =
        r in 0..7 && c in 0..7
}