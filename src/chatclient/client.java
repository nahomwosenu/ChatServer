
package chatclient;

import java.net.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

import javax.imageio.ImageIO;
public class client {
  private JFrame frame;
  private JLabel display;
  private JTextField messageField;
  private JTextArea messageArea;
  private JToggleButton disconnect;
  private JButton button;
  private Socket connection;
  private Object message;
  private ObjectInputStream input;
  private ObjectOutputStream output;
  public void buildGui(){
      frame=new JFrame("Client side");
      messageField=new JTextField(20);
      messageField.addActionListener(
              new ActionListener(){
                      public void actionPerformed(ActionEvent e){
                       sendMessage(e.getActionCommand());
                      }
              }
      );
      button=new JButton("Select File");
      button.addActionListener(
              new ActionListener(){
                  public void actionPerformed(ActionEvent e){
                      JFileChooser d=new JFileChooser();
                      int rt=d.showOpenDialog(null);
                      if(rt==JFileChooser.APPROVE_OPTION)
                      sendMessage(d.getSelectedFile());
                  }
              }
      );
    disconnect=new JToggleButton("Connect");
    disconnect.addActionListener(
            new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    connector c=new connector();
                    Thread t=new Thread(c);
                    t.start();
                    if(disconnect.isSelected())
                        disconnect.setText("Disconnect");
                    else disconnect.setText("Connect");
                }
            }
                
    );
    JPanel panel=new JPanel(new BorderLayout());
    panel.add(messageField,BorderLayout.WEST);
    panel.add(button,BorderLayout.CENTER);
    JPanel footer=new JPanel(new BorderLayout());
    footer.add(disconnect,BorderLayout.WEST);
    footer.add(button,BorderLayout.CENTER);
    messageArea=new JTextArea();
    frame.getContentPane().add(footer,BorderLayout.SOUTH);
    frame.getContentPane().add(panel,BorderLayout.NORTH);
    frame.getContentPane().add(new JScrollPane(messageArea),BorderLayout.CENTER);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(500,400);
  }
  public void sendMessage(Object message){
     try{
       output=new ObjectOutputStream(connection.getOutputStream());
       output.writeObject(message);
       output.flush();
      }
      catch(Exception e){
          e.printStackTrace();
      }
  }
  public void connect(){
      try{
          connection=new Socket("127.0.0.1",2020);
          reciever r=new reciever();
          Thread t=new Thread(r);
          t.start();
      }catch(IOException e){
          e.printStackTrace();
      }
  }
  class connector implements Runnable{
      public void run(){
          connect();
      }
  }
  class reciever implements Runnable{
      public void run(){
          try{
              do{
                  input=new ObjectInputStream(connection.getInputStream());
                  message=input.readObject();
                  showMessage(message);
              }while(input!=null);
          }catch(SocketException e){
              showMessage("Connection is Lost");
              messageField.setEditable(false);
              disconnect.setSelected(false);
              disconnect.setText("Retry");
          }
          catch(Exception e){
              e.printStackTrace();
          }
      }
  }
  public void showMessage(Object message){
      if(message instanceof String){
       if(((String)message).compareToIgnoreCase("close")==0)
           frame.dispose();
       else
          messageArea.append("\n"+(String)message);
      }
      else if(message instanceof Image){
          try{
          BufferedImage img=ImageIO.read((File)message);
          display=new JLabel(new ImageIcon(img));
          frame.getContentPane().add(display,BorderLayout.EAST);
          }catch(IOException e){
              e.printStackTrace();
          }
          
      }
      else messageArea.append("\nUnknown type of message");
  }

    public static void main(String[] args) {
        client c=new client();
        c.buildGui();
    }
    
}
