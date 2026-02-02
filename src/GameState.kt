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

    board[0][4] = Piece(PieceType.KING, PieceColor.BLACK)
    board[1][2] = Piece(PieceType.ROOK, PieceColor.BLACK)
    board[2][5] = Piece(PieceType.KNIGHT, PieceColor.BLACK)
    board[3][1] = Piece(PieceType.PAWN, PieceColor.BLACK)
    board[3][3] = Piece(PieceType.QUEEN, PieceColor.BLACK)

    board[5][4] = Piece(PieceType.QUEEN, PieceColor.WHITE)
    board[7][4] = Piece(PieceType.KING, PieceColor.WHITE)
    board[6][5] = Piece(PieceType.ROOK, PieceColor.WHITE)
    board[5][2] = Piece(PieceType.BISHOP, PieceColor.WHITE)
    board[4][6] = Piece(PieceType.PAWN, PieceColor.WHITE)
    board[5][7] = Piece(PieceType.KNIGHT, PieceColor.WHITE)

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
            if (r in 0..7 && c in 0..7 && data[r][c]?.color == piece.color) {
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
            } else if (data[r][from.second]!!.color != piece.color) {
                legalMoves.add(Pair(r, from.second))
                break
            } else break
        }

        for (r in from.first - 1 downTo 0) {
            if (data[r][from.second] == null) {
                legalMoves.add(Pair(r, from.second))
            } else if (data[r][from.second]!!.color != piece.color) {
                legalMoves.add(Pair(r, from.second))
                break
            } else break
        }

        for (c in from.second + 1..7) {
            if (data[from.first][c] == null) {
                legalMoves.add(Pair(from.first, c))
            } else if (data[from.first][c]!!.color != piece.color) {
                legalMoves.add(Pair(from.first, c))
                break
            } else break
        }

        for (c in from.second - 1 downTo 0) {
            if (data[from.first][c] == null) {
                legalMoves.add(Pair(from.first, c))
            } else if (data[from.first][c]!!.color != piece.color) {
                legalMoves.add(Pair(from.first, c))
                break
            } else break
        }

        return legalMoves
    }

    fun legalKnightMoves(from: Pair<Int, Int>): List<Pair<Int, Int>> {
        val piece = data[from.first][from.second]!!

        return listOf(
            from.first + 2 to from.second + 1,
            from.first + 2 to from.second - 1,
            from.first - 2 to from.second + 1,
            from.first - 2 to from.second - 1,
            from.first + 1 to from.second + 2,
            from.first + 1 to from.second - 2,
            from.first - 1 to from.second + 2,
            from.first - 1 to from.second - 2
        )
            .filter { it.first in (0..7) && it.second in (0..7) }
            .filter {
                val target = data[it.first][it.second]
                if (target == null)
                    true
                else if (target.color != piece.color)
                    true
                else
                    false
            }
    }

    fun legalBishopMoves(from: Pair<Int, Int>): List<Pair<Int, Int>> {
        val piece = data[from.first][from.second]!!

        val legalMoves = mutableListOf<Pair<Int, Int>>()
        for (i in 1..7) {
            val rawTargetCoordinate = from.first - i to from.second - i
            if (rawTargetCoordinate.first !in (0..7) || rawTargetCoordinate.second !in (0..7))
                break

            val target = data[rawTargetCoordinate.first][rawTargetCoordinate.second]
            if (target == null) {
                legalMoves.add(rawTargetCoordinate)
            } else if (target.color != piece.color) {
                legalMoves.add(rawTargetCoordinate)
                break
            } else break
        }

        for (i in 1..7) {
            val rawTargetCoordinate = from.first + i to from.second - i
            if (rawTargetCoordinate.first !in (0..7) || rawTargetCoordinate.second !in (0..7))
                break

            val target = data[rawTargetCoordinate.first][rawTargetCoordinate.second]
            if (target == null) {
                legalMoves.add(rawTargetCoordinate)
            } else if (target.color != piece.color) {
                legalMoves.add(rawTargetCoordinate)
                break
            } else break
        }

        for (i in 1..7) {
            val rawTargetCoordinate = from.first + i to from.second + i
            if (rawTargetCoordinate.first !in (0..7) || rawTargetCoordinate.second !in (0..7))
                break

            val target = data[rawTargetCoordinate.first][rawTargetCoordinate.second]
            if (target == null) {
                legalMoves.add(rawTargetCoordinate)
            } else if (target.color != piece.color) {
                legalMoves.add(rawTargetCoordinate)
                break
            } else break
        }

        for (i in 1..7) {
            val rawTargetCoordinate = from.first - i to from.second + i
            if (rawTargetCoordinate.first !in (0..7) || rawTargetCoordinate.second !in (0..7))
                break

            val target = data[rawTargetCoordinate.first][rawTargetCoordinate.second]
            if (target == null) {
                legalMoves.add(rawTargetCoordinate)
            } else if (target.color != piece.color) {
                legalMoves.add(rawTargetCoordinate)
                break
            } else break
        }

        return legalMoves
    }

    fun legalQueenMoves(from: Pair<Int, Int>): List<Pair<Int, Int>> {
        return legalRookMoves(from) + legalBishopMoves(from)
    }

    fun legalKingMoves(from: Pair<Int, Int>): List<Pair<Int, Int>> {
        val piece = data[from.first][from.second]!!

        return listOf(
            from.first - 1 to from.second - 1,
            from.first - 1 to from.second,
            from.first - 1 to from.second + 1,
            from.first to from.second - 1,
            from.first to from.second,
            from.first to from.second + 1,
            from.first + 1 to from.second - 1,
            from.first + 1 to from.second,
            from.first + 1 to from.second + 1,
        )
            .filter { it.first in (0..7) && it.second in (0..7) }
            .filter {
                val target = data[it.first][it.second]
                if (target == null)
                    true
                else if (target.color != piece.color)
                    true
                else
                    false
            }
    }
}