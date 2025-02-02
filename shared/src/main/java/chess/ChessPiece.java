package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        switch (pieceType) {
            case KING:
                addKingMoves(board, myPosition, moves);
                break;
            case QUEEN:
                addQueenMoves(board, myPosition, moves);
                break;
            case BISHOP:
                addBishopMoves(board, myPosition, moves);
                break;
            case KNIGHT:
                addKnightMoves(board, myPosition, moves);
                break;
            case ROOK:
                addRookMoves(board, myPosition, moves);
                break;
            case PAWN:
                addPawnMoves(board, myPosition, moves);
                break;


        }

        return moves;
    }
    private void addKingMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int[][] directions = {{1,0}, {0,1}, {-1,0}, {0,-1}, {1,1}, {1,-1}, {-1,1}, {-1,-1}};
        for (int[] direction : directions) {
            addMoveIfValid(board, myPosition, direction[0], direction[1], moves, false);
        }
    }
    private void addQueenMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        addRookMoves(board, myPosition, moves);
        addBishopMoves(board, myPosition, moves);
    }
    private void addBishopMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int[][] directions = {{1,1}, {1,-1}, {-1,1}, {-1,-1}};
        for (int[] direction : directions) {
            addMoveIfValid(board, myPosition, direction[0], direction[1], moves, true);
        }
    }
    private void addKnightMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int[][] jumps = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
        for (int[] jump : jumps) {
            addMoveIfValid(board, position, jump[0], jump[1], moves, false);
        }
    }
    private void addRookMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        for (int[] dir : directions) {
            addMoveIfValid(board, position, dir[0], dir[1], moves, true);
        }
    }
    private void addPawnMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;

        //Promotion move
        if (pieceColor == ChessGame.TeamColor.WHITE && position.getRow() == 7) {
            addPromotionMoves(board, position, moves);
            return;
        } else if (pieceColor == ChessGame.TeamColor.BLACK && position.getRow() == 2) {
            addPromotionMoves(board, position, moves);
            return;
        }

        // Forward move
        addMoveIfValid(board, position, direction, 0, moves, false);
        if (position.getRow() == 2 && pieceColor == ChessGame.TeamColor.WHITE){
            if (moves.size() == 1){
                addMoveIfValid(board, position, direction * 2, 0, moves, false);
            }
        }
        if (position.getRow() == 7 && pieceColor == ChessGame.TeamColor.BLACK){
            if (moves.size() == 1){
                addMoveIfValid(board, position, direction * 2, 0, moves, false);
            }
        }
        //Capture move
        addMoveIfValid(board, position, direction, 1, moves, false, true);
        addMoveIfValid(board, position, direction, -1, moves, false, true);
    }

    private void addPromotionMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int promotionRow = position.getRow() + direction;

        ChessPosition promotionPosition = new ChessPosition(promotionRow, position.getColumn());
        ChessPiece pieceAtTarget = board.getPiece(promotionPosition);
        if (pieceAtTarget == null) {
            moves.add(new ChessMove(position, promotionPosition, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(position, promotionPosition, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(position, promotionPosition, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(position, promotionPosition, ChessPiece.PieceType.KNIGHT));
        }

        ChessPosition promotionCapturePosition1 = new ChessPosition(promotionRow, position.getColumn() + 1);
        pieceAtTarget = board.getPiece(promotionCapturePosition1);
        if (pieceAtTarget != null && pieceAtTarget.getTeamColor() != this.pieceColor) {
            moves.add(new ChessMove(position, promotionCapturePosition1, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(position, promotionCapturePosition1, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(position, promotionCapturePosition1, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(position, promotionCapturePosition1, ChessPiece.PieceType.KNIGHT));
        }

        ChessPosition promotionCapturePosition2 = new ChessPosition(promotionRow, position.getColumn() - 1);
        pieceAtTarget = board.getPiece(promotionCapturePosition2);
        if (pieceAtTarget != null && pieceAtTarget.getTeamColor() != this.pieceColor) {
            moves.add(new ChessMove(position, promotionCapturePosition2, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(position, promotionCapturePosition2, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(position, promotionCapturePosition2, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(position, promotionCapturePosition2, ChessPiece.PieceType.KNIGHT));
        }
    }

    private void addMoveIfValid(ChessBoard board, ChessPosition position, int rowOffset, int colOffset,
                                Collection<ChessMove> moves, boolean repeat) {
        addMoveIfValid(board, position, rowOffset, colOffset, moves, repeat, false);
    }

    private void addMoveIfValid(ChessBoard board, ChessPosition position, int rowOffset, int colOffset,
                                Collection<ChessMove> moves, boolean repeat, boolean captureOnly) {
        int currentRow = position.getRow();
        int currentCol = position.getColumn();

        while (true) {
            currentRow += rowOffset;
            currentCol += colOffset;

            if (!isWithinBounds(currentRow, currentCol)) {
                break;
            }


            ChessPosition newPosition = new ChessPosition(currentRow, currentCol);
            ChessPiece pieceAtTarget = board.getPiece(newPosition);

            if (pieceAtTarget == null) {
                if (!captureOnly) {
                    moves.add(new ChessMove(position, newPosition, null));
                }
            } else {
                if (pieceAtTarget.getTeamColor() != this.pieceColor) {
                    boolean type = board.getPiece(position).getPieceType() == ChessPiece.PieceType.PAWN;
                    if (board.getPiece(position).getPieceType() == ChessPiece.PieceType.PAWN){
                        if (colOffset != 0){
                            moves.add(new ChessMove(position, newPosition, null));
                        }
                    }
                    else {
                        moves.add(new ChessMove(position, newPosition, null));
                    }
                }
                break;
            }

            if (!repeat) {
                break;
            }
        }
    }
    private boolean isWithinBounds(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    @Override
    public boolean equals(Object o) {
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, pieceType);
    }


}
