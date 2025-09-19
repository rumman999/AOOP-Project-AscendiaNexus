package com.example.aoop_project.games.chess.game;

import com.example.aoop_project.games.chess.figures.*;
import com.example.aoop_project.games.chess.figures.*;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class CustomizerController extends ChessBoardController{

    public static CustomizerModel cModel = new CustomizerModel();
    static CustomizerView cView = new CustomizerView();
    private static Game game;
    private static ChessPiece prev = null;

    public CustomizerController(){
        game = new Game(cModel);
        cModel = new CustomizerModel();
        cView = new CustomizerView();
        cView.addEventHandler(MouseEvent.MOUSE_CLICKED, new MouseHandler());
    }

    public CustomizerView getCView() {
        return cView;
    }
    public CustomizerModel getCModel() {
        return cModel;
    }
    public Game getGame(){
        return game;
    }


    public static class MouseHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent e) {
            int row = (int) e.getY() / ChessBoardView.SQUARE_PANEL_SIZE;
            int column = (int) e.getX() / ChessBoardView.SQUARE_PANEL_SIZE;
            ChessPiece piece = cModel.findPiece(row, column);

                    if (e.getButton() == MouseButton.PRIMARY) {
                        /* LEFT CLICK, WHITE PIECE */
                        createPieceOnBoard(ChessPiece.PieceColor.WHITE,piece,row,column);
                    }
                    else if(e.getButton() == MouseButton.SECONDARY){
                        /* RIGHT CLICK, WHITE PIECE */
                        createPieceOnBoard(ChessPiece.PieceColor.BLACK,piece,row,column);
                    }

        }
    }

    public static void createPieceOnBoard(ChessPiece.PieceColor color, ChessPiece piece, int row, int column){
        if (piece != null) {
            if(piece.isPawn()){
                cModel.deleteChessPiece(row, column);
                piece = new Knight(color,row, column);
            }
            else if(piece.isKnight()){
                cModel.deleteChessPiece(row, column);
                piece = new Bishop(color,row, column);
            }
            else if(piece.isBishop()){
                cModel.deleteChessPiece(row, column);
                piece = new Rook(color,row, column);
            }
            else if(piece.isRook()){
                cModel.deleteChessPiece(row, column);
                piece = new Queen(color,row, column);
            }
            else if(piece.isQueen()){
                cModel.deleteChessPiece(row, column);
                cView.deleteChessPiece(row,column);
                return;
            }
            else if(piece.isKing()){
                return;
            }
            else{
                piece = new Pawn(color,row, column);
            }
        }
        else{
            piece = new Pawn(color,row, column);
        }
        cView.deleteChessPiece(row,column);
        cModel.addChessPiece(piece);
        cView.addChessPiece(piece);
    }






}
