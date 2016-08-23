package manouvre.network.client;

import java.io.*;
import java.net.*;
import manouvre.gui.ClientUI;

public class SocketClient implements Runnable{
    
    public int port;
    public String serverAddr;
    public Socket socket;
    public ClientUI ui;
    public ObjectInputStream In;
    public ObjectOutputStream Out;
    
    
    public SocketClient(ClientUI frame) throws IOException{
        ui = frame; 
        //this.serverAddr = "zimoch.insomnia247.nl"; 
        this.serverAddr = "localhost"; 
        this.port = 5002;
        socket = new Socket(InetAddress.getByName(serverAddr), port);
            
        Out = new ObjectOutputStream(socket.getOutputStream());
        Out.flush();
        In = new ObjectInputStream(socket.getInputStream());
        
       
    }

    @Override
    public void run() {
        boolean keepRunning = true;
        while(keepRunning){
            try {
                Message msg = (Message) In.readObject();
                System.out.println("Incoming : "+msg.toString());
                
                if(msg.type.equals("message")){
                    if(msg.recipient.equals(ui.getUserName())){
                        ui.chatTextArea.append("["+msg.sender +" > Me] : " + msg.content + "\n");
                    }
                    else{
                        ui.chatTextArea.append("["+ msg.sender +" > "+ msg.recipient +"] : " + msg.content + "\n");
                    }
                                           
               
                }
//                else if(msg.type.equals("login")){
//                    if(msg.content.equals("TRUE")){
//                        ui.jButton2.setEnabled(false); ui.jButton3.setEnabled(false);                        
//                        ui.jButton4.setEnabled(true); ui.jButton5.setEnabled(true);
//                        ui.chatTextArea.append("[SERVER > Me] : Login Successful\n");
//                        ui.jTextField3.setEnabled(false); ui.jPasswordField1.setEnabled(false);
//                    }
//                    else{
//                        ui.chatTextArea.append("[SERVER > Me] : Login Failed\n");
//                    }
//                }
//                else if(msg.type.equals("test")){
//                    ui.jButton1.setEnabled(false);
//                    ui.jButton2.setEnabled(true); ui.jButton3.setEnabled(true);
//                    ui.jTextField3.setEnabled(true); ui.jPasswordField1.setEnabled(true);
//                    ui.jTextField1.setEditable(false); ui.jTextField2.setEditable(false);
//                    ui.jButton7.setEnabled(true);
//                }
//                else if(msg.type.equals("newuser")){
//                    if(!msg.content.equals(ui.username)){
//                        boolean exists = false;
//                        for(int i = 0; i < ui.model.getSize(); i++){
//                            if(ui.model.getElementAt(i).equals(msg.content)){
//                                exists = true; break;
//                            }
//                        }
//                        if(!exists){ ui.model.addElement(msg.content); }
//                    }
//                }
//                else if(msg.type.equals("signup")){
//                    if(msg.content.equals("TRUE")){
//                        ui.jButton2.setEnabled(false); ui.jButton3.setEnabled(false);
//                        ui.jButton4.setEnabled(true); ui.jButton5.setEnabled(true);
//                        ui.chatTextArea.append("[SERVER > Me] : Singup Successful\n");
//                    }
//                    else{
//                        ui.chatTextArea.append("[SERVER > Me] : Signup Failed\n");
//                    }
//               }
//                else if(msg.type.equals("signout")){
//                    if(msg.content.equals(ui.username)){
//                        ui.chatTextArea.append("["+ msg.sender +" > Me] : Bye\n");
//                        ui.jButton1.setEnabled(true); ui.jButton4.setEnabled(false); 
//                        ui.jTextField1.setEditable(true); ui.jTextField2.setEditable(true);
//                        
//                        for(int i = 1; i < ui.model.size(); i++){
//                            ui.model.removeElementAt(i);
//                        }
//                        
//                        ui.clientThread.stop();
//                    }
//                    else{
//                        ui.model.removeElement(msg.content);
//                        ui.chatTextArea.append("["+ msg.sender +" > All] : "+ msg.content +" has signed out\n");
//                    }
//                }
                
                
                else{
                    ui.chatTextArea.append("[SERVER > Me] : Unknown message type\n");
                }
            }
            catch(Exception ex) {
                keepRunning = false;
                ui.chatTextArea.append("[Application > Me] : Connection Failure \n" + ex.getMessage());
//                ui.jButton1.setEnabled(true); 
//                ui.jTextField1.setEditable(true); 
//                ui.jTextField2.setEditable(true);
//                ui.jButton4.setEnabled(false); 
//                ui.jButton5.setEnabled(false); 
//                ui.jButton5.setEnabled(false);
                
//                for(int i = 1; i < ui.model.size(); i++){
//                    ui.model.removeElementAt(i);
//                }
                
                ui.clientThread.stop();
                
                System.out.println("Exception SocketClient run()");
                ex.printStackTrace();
            }
        }
    }
    
    public void send(Message msg){
        try {
            Out.writeObject(msg);
            Out.flush();
            System.out.println("Outgoing : "+msg.toString());
       } 
        catch (IOException ex) {
            System.out.println("Exception SocketClient send()");
        }
    }
    
    public void closeThread(Thread t){
        t = null;
    }
}
