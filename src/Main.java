//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //Show welcome screen and get player names
        String[] players = WelcomeWindow.show();

        //Start the actual checker game with those names
        SimpleChessBoard.launchGame(players[0],players[1]);
    }
}