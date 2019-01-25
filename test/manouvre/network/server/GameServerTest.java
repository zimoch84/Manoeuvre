/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.server;

import java.util.ArrayList;
import manouvre.network.core.Message;
import manouvre.network.core.MessageFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Piotr
 */
public class GameServerTest {
    
    public GameServerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getMessageFactory method, of class GameServer.
     */
    @Test
    public void testGetMessageFactory() {
        System.out.println("getMessageFactory");
        GameServer instance = null;
        MessageFactory expResult = null;
        MessageFactory result = instance.getMessageFactory();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of run method, of class GameServer.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        GameServer instance = null;
        instance.run();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of start method, of class GameServer.
     */
    @Test
    public void testStart() {
        System.out.println("start");
        GameServer instance = null;
        instance.start();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of stop method, of class GameServer.
     */
    @Test
    public void testStop() {
        System.out.println("stop");
        GameServer instance = null;
        instance.stop();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findClient method, of class GameServer.
     */
    @Test
    public void testFindClient() {
        System.out.println("findClient");
        int ID = 0;
        GameServer instance = null;
        int expResult = 0;
        int result = instance.findClient(ID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of Announce method, of class GameServer.
     */
    @Test
    public void testAnnounce_3args() {
        System.out.println("Announce");
        String type = "";
        String sender = "";
        String content = "";
        GameServer instance = null;
        instance.announce(type, sender, content);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of announce method, of class GameServer.
     */
    @Test
    public void testAnnounce_Message() {
        System.out.println("announce");
        Message inMessage = null;
        GameServer instance = null;
        instance.announce(inMessage);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of announceInRoom method, of class GameServer.
     */
    @Test
    public void testAnnounceInRoom() {
        System.out.println("announceInRoom");
        GameRoom room = null;
        Message msg = null;
        GameServer instance = null;
        instance.announceInRoom(room, msg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of SendUserList method, of class GameServer.
     */
    @Test
    public void testSendUserList() {
        System.out.println("SendUserList");
        String toWhom = "";
        GameServer instance = null;
        instance. (toWhom);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findUserThread method, of class GameServer.
     */
    @Test
    public void testFindUserThread() {
        System.out.println("findUserThread");
        String usr = "";
        GameServer instance = null;
        ClientServerThread expResult = null;
        ClientServerThread result = instance.findUserThread(usr);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRooms method, of class GameServer.
     */
    @Test
    public void testGetRooms() {
        System.out.println("getRooms");
        GameServer instance = null;
        ArrayList<GameRoom> expResult = null;
        ArrayList<GameRoom> result = instance.getRooms();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class GameServer.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        int ID = 0;
        GameServer instance = null;
        instance.remove(ID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createRoom method, of class GameServer.
     */
    @Test
    public void testCreateRoom() {
        System.out.println("createRoom");
        String channelName = "";
        String password = "";
        ClientServerThread thread = null;
        GameServer instance = null;
        instance.createRoom(channelName, password, thread);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of destroyRoom method, of class GameServer.
     */
    @Test
    public void testDestroyRoom() {
        System.out.println("destroyRoom");
        GameRoom channel = null;
        GameServer instance = null;
        boolean expResult = false;
        boolean result = instance.destroyRoom(channel);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRoomByPort method, of class GameServer.
     */
    @Test
    public void testGetRoomByPort() {
        System.out.println("getRoomByPort");
        int socketPort = 0;
        GameServer instance = null;
        GameRoom expResult = null;
        GameRoom result = instance.getRoomByPort(socketPort);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isRoomExistsOnServer method, of class GameServer.
     */
    @Test
    public void testIsRoomExistsOnServer() {
        System.out.println("isRoomExistsOnServer");
        GameRoom ingameroom = null;
        GameServer instance = null;
        boolean expResult = false;
        boolean result = instance.isRoomExistsOnServer(ingameroom);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStackTrace method, of class GameServer.
     */
    @Test
    public void testGetStackTrace() {
        System.out.println("getStackTrace");
        Throwable throwable = null;
        String expResult = "";
        String result = GameServer.getStackTrace(throwable);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of notifyAbout method, of class GameServer.
     */
    @Test
    public void testNotifyAbout() {
        System.out.println("notifyAbout");
        String logLevel = "";
        String text = "";
        GameServer instance = null;
        instance.notifyAbout(logLevel, text);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of authorize method, of class GameServer.
     */
    @Test
    public void testAuthorize() {
        System.out.println("authorize");
        String user = "";
        String password = "";
        GameServer instance = null;
        boolean expResult = false;
        boolean result = instance.authorize(user, password);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
