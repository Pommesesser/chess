fun initialGameState(): GameState {
    val board = Array(8) { arrayOfNulls<Piece>(8) }

    for (c in 0..7) {
        board[1][c] = Piece(PieceType.PAWN, PieceColor.BLACK)
        board[6][c] = Piece(PieceType.PAWN, PieceColor.WHITE)
    }

    board[0][0] = Piece(PieceType.ROOK, PieceColor.BLACK)
    board[0][7] = Piece(PieceType.ROOK, PieceColor.BLACK)
    board[7][0] = Piece(PieceType.ROOK, PieceColor.WHITE)
    board[7][7] = Piece(PieceType.ROOK, PieceColor.WHITE)

    board[0][1] = Piece(PieceType.KNIGHT, PieceColor.BLACK)
    board[0][6] = Piece(PieceType.KNIGHT, PieceColor.BLACK)
    board[7][1] = Piece(PieceType.KNIGHT, PieceColor.WHITE)
    board[7][6] = Piece(PieceType.KNIGHT, PieceColor.WHITE)

    board[0][2] = Piece(PieceType.BISHOP, PieceColor.BLACK)
    board[0][5] = Piece(PieceType.BISHOP, PieceColor.BLACK)
    board[7][2] = Piece(PieceType.BISHOP, PieceColor.WHITE)
    board[7][5] = Piece(PieceType.BISHOP, PieceColor.WHITE)

    board[0][3] = Piece(PieceType.QUEEN, PieceColor.BLACK)
    board[7][3] = Piece(PieceType.QUEEN, PieceColor.WHITE)

    board[0][4] = Piece(PieceType.KING, PieceColor.BLACK)
    board[7][4] = Piece(PieceType.KING, PieceColor.WHITE)

    return GameState(board)
}

fun testGameState(): GameState {
    val board = Array(8) { arrayOfNulls<Piece>(8) }

    for (i in 0..7) {
        board[i][i] = Piece(PieceType.ROOK, PieceColor.BLACK)
        board[i][7 - i] = Piece(PieceType.ROOK, PieceColor.WHITE)
    }

    board[5][4] = Piece(PieceType.ROOK, PieceColor.WHITE)
    board[2][3] = Piece(PieceType.ROOK, PieceColor.BLACK)
    board[3][2] = Piece(PieceType.ROOK, PieceColor.BLACK)
    board[4][5] = Piece(PieceType.ROOK, PieceColor.WHITE)

    return GameState(board)
}

class GameState(val data: Array<Array<Piece?>>) {
    fun legalMoves(from: Pair<Int, Int>): List<Pair<Int, Int>> {
        val piece = data[from.first][from.second] ?: return emptyList()
        return when (piece.type) {
            PieceType.PAWN -> legalPawnMoves(from)
            PieceType.ROOK -> legalRookMoves(from)
            PieceType.KNIGHT -> legalKnightMoves(from)
            PieceType.BISHOP -> legalBishopMoves(from)
            PieceType.QUEEN -> legalQueenMoves(from)
            PieceType.KING -> legalKingMoves(from)
        }
    }

    fun legalPawnMoves(from: Pair<Int, Int>): List<Pair<Int, Int>> {
        val piece = data[from.first][from.second]!!

        val rawCoordinates = if (piece.color == PieceColor.WHITE) {
            if (from.first == 6)
                arrayOf(Pair(from.first - 1, from.second), Pair(from.first - 2, from.second))
            else
                arrayOf(Pair(from.first - 1, from.second))
        } else {
            if (from.first == 1)
                arrayOf(Pair(from.first + 1, from.second), Pair(from.first + 2, from.second))
            else
                arrayOf(Pair(from.first + 1, from.second))
        }

        val inBoundsCoordinates = rawCoordinates.filter { it.first in (0..7) && it.second in (0..7) }

        val legalMoves = mutableListOf<Pair<Int, Int>>()
        for (c in inBoundsCoordinates) {
            if (data[c.first][c.second] != null)
                break

            legalMoves.add(c)
        }

        val captureOffsets = if (piece.color == PieceColor.WHITE) listOf(-1 to -1, -1 to 1)
        else listOf(1 to -1, 1 to 1)

        for ((dr, dc) in captureOffsets) {
            val r = from.first + dr
            val c = from.second + dc
            if (r in 0..7 && c in 0..7 && data[r][c]?.color == opponentColor(piece.color)) {
                legalMoves.add(Pair(r, c))
            }
        }

        return legalMoves
    }

    fun legalRookMoves(from: Pair<Int, Int>): List<Pair<Int, Int>> {
        val piece = data[from.first][from.second]!!
        val legalMoves = mutableListOf<Pair<Int, Int>>()

        for (r in from.first + 1..7) {
            if (data[r][from.second] == null) {
                legalMoves.add(Pair(r, from.second))
            } else if (data[r][from.second]!!.color == opponentColor(piece.color)) {
                legalMoves.add(Pair(r, from.second))
                break
            } else break
        }

        for (r in from.first - 1 downTo 0) {
            if (data[r][from.second] == null) {
                legalMoves.add(Pair(r, from.second))
            } else if (data[r][from.second]!!.color == opponentColor(piece.color)) {
                legalMoves.add(Pair(r, from.second))
                break
            } else break
        }

        for (c in from.second + 1..7) {
            if (data[from.first][c] == null) {
                legalMoves.add(Pair(from.first, c))
            } else if (data[from.first][c]!!.color == opponentColor(piece.color)) {
                legalMoves.add(Pair(from.first, c))
                break
            } else break
        }

        for (c in from.second - 1 downTo 0) {
            if (data[from.first][c] == null) {
                legalMoves.add(Pair(from.first, c))
            } else if (data[from.first][c]!!.color == opponentColor(piece.color)) {
                legalMoves.add(Pair(from.first, c))
                break
            } else break
        }

        return legalMoves
    }

    fun legalKnightMoves(from: Pair<Int, Int>): List<Pair<Int, Int>> {
        TODO()
    }

    fun legalBishopMoves(from: Pair<Int, Int>): List<Pair<Int, Int>> {
        TODO()
    }

    fun legalQueenMoves(from: Pair<Int, Int>): List<Pair<Int, Int>> {
        TODO()
    }

    fun legalKingMoves(from: Pair<Int, Int>): List<Pair<Int, Int>> {
        TODO()
    }

    fun opponentColor(color: PieceColor) = if (color == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
}
