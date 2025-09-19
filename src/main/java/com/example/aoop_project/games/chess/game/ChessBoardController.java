package com.example.aoop_project.games.chess.game;

import com.example.aoop_project.games.chess.figures.King;
import com.example.aoop_project.games.chess.figures.ChessPiece;
import com.example.aoop_project.games.chess.figures.ChessPiece.PieceColor;
import com.example.aoop_project.games.chess.figures.Sequence;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import static com.example.aoop_project.games.chess.game.CastleHelper.CastleType.TYPE_LONG;
import static com.example.aoop_project.games.chess.game.CastleHelper.CastleType.TYPE_SHORT;
import static com.example.aoop_project.games.chess.game.ChessBoardController.State.*;


public class ChessBoardController {

    private static ChessBoardView view;
    private static ChessBoardModel board;
    private static Game game;

    private static ChessPiece lastFigure = null;

    public enum State {READY, DRAG, SELECTED}
    private static State state;

    private static boolean moveSuccess;

    public ChessBoardController() {
        view = new ChessBoardView();
        view.addEventHandler(MouseEvent.ANY, new MouseHandler());
        board = new ChessBoardModel();
        game = new Game(board);
        state = READY;
    }


    public ChessBoardController(String darkSquareColor, String lightSquareColor) {
        view = new ChessBoardView(darkSquareColor, lightSquareColor);
        view.addEventHandler(MouseEvent.ANY, new MouseHandler());
        board = new ChessBoardModel();
        game = new Game(board);
        state = READY;
    }
    public static class MouseHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent e) {
            int row = (int) e.getY() / ChessBoardView.SQUARE_PANEL_SIZE;
            int column = (int) e.getX() / ChessBoardView.SQUARE_PANEL_SIZE;
            if(row > ChessBoardModel.NUM_ROWS || row < 0 ||
                column > ChessBoardModel.NUM_COLUMNS || column < 0)
                return;
            ChessPiece piece = board.findPiece(row, column);
            switch(state){
                case READY:
                if (e.getEventType() == MouseEvent.MOUSE_PRESSED || e.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    if(!moveSuccess)
                        view.unColorizeAll();
                if (lastFigure != null) {

                    moveSuccess = initiateMoveChessPiece(lastFigure, row, column);
                    if(moveSuccess){
                        view.colorizeSequence(row,column, "#d4f1fd");
                    }

                    view.colorizeKingIfChecked(PieceColor.WHITE, board);
                    view.colorizeKingIfChecked(PieceColor.BLACK, board);
                }

                if ((game.getColorInPlay() == piece.getPieceColor()
                        && board.findPiece(row, column) == piece)) {

                    view.colorizeSequences(board, piece);
                    view.colorizeCastleSequences(board, piece, column);


                }

                lastFigure = piece;

            }
                else if (e.getEventType() == MouseEvent.MOUSE_RELEASED)
                {
                    view.removeAllGhostPieces();
                    if (lastFigure != null ) {
                        initiateMoveChessPiece(lastFigure, row, column);
                        view.colorizeKingIfChecked(PieceColor.WHITE, board);
                        view.colorizeKingIfChecked(PieceColor.BLACK, board);
                    }

                }
                    if(e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                        lastFigure.setX(e.getX());
                        lastFigure.setY(e.getY());
                        state = DRAG;
                    }

            break;

                case DRAG:
                    if (e.getEventType() == MouseEvent.MOUSE_DRAGGED)
                    {
                        if (piece == null) return;

                        if (lastFigure != null) {
                            try {
                                initiateGhostChessPiece(lastFigure, row, column);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                    else if (e.getEventType() == MouseEvent.MOUSE_RELEASED)
                    {
                        view.removeAllGhostPieces();
                        if(!moveSuccess)
                            view.unColorizeAll();
                        if (lastFigure != null ) {

                            moveSuccess = initiateMoveChessPiece(lastFigure, row, column);
                            if(moveSuccess){
                                view.colorizeSequence(row,column, "#d4f1fd");
                            }

                            view.colorizeKingIfChecked(PieceColor.WHITE, board);
                            view.colorizeKingIfChecked(PieceColor.BLACK, board);
                            state = READY;
                        }

                        lastFigure = piece;

                    }
                    break;
            }
        }
    }
    public ChessBoardView getView() {
        return view;
    }
    public ChessBoardModel getModel(){
        return board;
    }

    public Game getGame(){
        return game;
    }

    public void setGame(Game newGame){
        game = newGame;
    }

    public void setChessBoardModel(ChessBoardModel newModel){
        board = newModel;
    }

    public static void initiateGhostChessPiece(ChessPiece piece, int newRow, int newColumn) throws IOException {
        view.removeAllGhostPieces();
        if (board.canMoveTo(piece, newRow, newColumn)
                && game.getColorInPlay() == piece.getPieceColor()) {


                view.displayGhostChessPiece(piece, newRow, newColumn);
            }
    }
    private static void moveInAllComponents(ChessPiece piece, int newRow, int newColumn){
        view.moveChessPiece(piece, newRow, newColumn);
        board.moveChessPiece(piece, newRow, newColumn);
        game.initiateMove(piece, newRow, newColumn);
    }

    public static boolean initiateMoveChessPiece(ChessPiece piece, int newRow, int newColumn) {
        if (board.canMoveTo(piece, newRow, newColumn)
            && game.getColorInPlay() == piece.getPieceColor()) {
            if(board.sequenceIsCheckInducing(new Sequence(newRow,newColumn,piece))){
                return false;
            }
            if(board.findPiece(newRow,newColumn) != null && board.findPiece(newRow,newColumn).isKing()){
                return false;
            }
            if (CastleHelper.sequenceIsCastle(board, piece, newRow, newColumn)) {
                King kingPiece = (King) piece;
                int rookColumn = CastleHelper.sequenceIsShortCastle(board, kingPiece, newRow, newColumn) ? 7 : 0;
                if(CastleHelper.sequenceIsShortCastle(board, (King)piece, newRow, newColumn)){
                    if(board.sequenceIsCheckInducing(
                            new Sequence(newRow, CastleHelper.getKingsAdjacentCastlePathSquareColumn(
                                    TYPE_SHORT,
                                    piece.getColumn()),
                                    piece))){
                        return false;
                    }
                }
                else if(CastleHelper.sequenceIsLongCastle(board, (King)piece, newRow, newColumn)){
                    if(board.sequenceIsCheckInducing(
                            new Sequence(newRow,CastleHelper.getKingsAdjacentCastlePathSquareColumn(
                                    TYPE_LONG,
                                    piece.getColumn()),
                                    piece))){
                        return false;
                    }
                }
                ChessPiece rook = board.findPiece(kingPiece.getRow(), rookColumn);
                castle(kingPiece, rook, newRow, newColumn);

                return true;
            }
            else {
                moveInAllComponents(piece,newRow,newColumn);
                return true;
            }
        }
        return false;
    }

    public static void castle(ChessPiece king, ChessPiece rook, int newRow, int newColumn) {
        moveInAllComponents(king,newRow,newColumn);
        boolean rookIsOnTheLeft = rook.getColumn() == 0;
        if(rookIsOnTheLeft){
            view.moveChessPiece(rook, newRow, newColumn + 1);
            board.moveChessPiece(rook, newRow, newColumn + 1);
        }
        else{
            view.moveChessPiece(rook, newRow, newColumn - 1);
            board.moveChessPiece(rook, newRow, newColumn - 1);
        }
    }

}
