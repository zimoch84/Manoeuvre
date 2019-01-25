/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.IOException;
import java.util.ArrayList;
import manouvre.commands.CommandQueue;
import manouvre.gui.CreateRoomWindow;
import manouvre.gui.GameWindow;
import manouvre.gui.LoginWindow;
import manouvre.network.client.QueueClient;
import manouvre.network.core.User;
import manouvre.network.server.UnoptimizedDeepCopy;
import org.apache.log4j.PropertyConfigurator;
/**
 *
 * @author Piotr
 */
public class Maneuvre {  //Fake! this is temporary just to start game quick

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        
          PropertyConfigurator.configure( Maneuvre.class.getResourceAsStream("log4j.xml") );
          Player player1 = new Player (new User("Piotr"))      ;
          Player player2 = new Player (new User("Bartek"))      ;
          player1.setNation(Player.Nation.AU);
          player2.setNation(Player.Nation.FR);
          
          ArrayList<Player> players = new ArrayList<Player>();
          players.add(player1);
          players.add(player2);
          Game game = new Game(players);
          
          System.out.println("manouvre.game.Maneuvre.main(): "+ game.toString());
          
          QueueClient fakeClient = new QueueClient();
          CommandQueue cmdQueueHost = new CommandQueue(game,  fakeClient.hostClient );
          
          GameWindow clientGameHost = new GameWindow( game ,  CreateRoomWindow.AS_HOST, cmdQueueHost );
          
          Game game2 = (Game) UnoptimizedDeepCopy.copy (game);
          CommandQueue cmdQueueGuest = new CommandQueue(game2,  fakeClient.guestClient );
          GameWindow clientGameGuest = new GameWindow( game2 , CreateRoomWindow.AS_GUEST , cmdQueueGuest);
          
          fakeClient.cmdHost = cmdQueueHost;
          fakeClient.cmdQuest = cmdQueueGuest;
          try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
          
          clientGameHost.setVisible(true);
           
          clientGameGuest.setVisible(true);
         
          
    }
}
