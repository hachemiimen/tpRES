import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class main
{
    public static void main(String [] args) throws IOException
    {
        JFrame f=new JFrame("Client FTP");
        f.setBounds(100,80,500,400);

        JPanel panel = new JPanel();
        panel.setBackground(Color.white);

       JLabel l =new JLabel();
       l.setBounds(10,40,200,200);
       Icon img = new ImageIcon("C:/Users/C/IdeaProjects/TpRES/logo.jpg" );
       l.setIcon(img);

        JLabel client=new JLabel("Username :");
        client.setBounds(230,120, 100, 20);
        client.setForeground(Color.black);
        Icon image1 = new ImageIcon( "C:/Users/C/IdeaProjects/TpRES/users.png" );
        client.setIcon( image1 );

        JTextField t1=new JTextField();
        t1.setBounds(320,120,100,20);

        JLabel mdp=new JLabel("Password :");
        mdp.setBounds(230,170, 100, 20);
        mdp.setForeground(Color.black);
        Icon image2 = new ImageIcon( "C:/Users/C/IdeaProjects/TpRES/pass.png" );
        mdp.setIcon( image2 );

        JPasswordField t2=new JPasswordField();
        t2.setBounds(320,170,100,20);

        JLabel l1=new JLabel();
        l1.setBounds(20,100,300,20);

        JButton b1=new JButton("Sign in");
        b1.setBounds(290, 240, 100, 30);
        b1.setBackground(Color.getHSBColor(270,100,10));

        f.add(l);
        f.add(client);
        f.add(t1);
        f.add(mdp);
        f.add(t2);
        f.add(l1);
        f.add(b1);
        f.add(panel);
        b1.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                // TODO Auto-generated method stub
                Ftp clientFtp = new Ftp ();
                try
                {
                    clientFtp.connect("127.0.0.1", 21);
                    clientFtp.login(t1.getText(), t2.getText());
                    l1.setText(clientFtp.getRep());

                    if(clientFtp.logIn)
                    {
                        f.hide();
                        JFrame f2 = new JFrame("Services :");
                        f2.setSize(800, 650);

                        JPanel panel2 = new JPanel();
                        panel2.setBackground(Color.BLACK);

                        JLabel l2 = new JLabel("File Transfert Protocol FTP");
                        l2.setBounds(300,30,300,40);
                        l2.setForeground(Color.white);
                        Icon image3 = new ImageIcon( "C:/Users/C/IdeaProjects/TpRES/ftp.png" );

                        JTextArea l1 = new JTextArea();
                        l1.setBounds(280, 90, 380, 320);

                        JScrollPane jsp = new JScrollPane(l1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                        jsp.setBounds(220, 90, 380, 320);

                        JLabel label1 = new JLabel("Retreive File: ");
                        label1.setBounds(10,120,200,20);
                        label1.setForeground(Color.white);
                        JButton b1=new JButton("Retreive");
                        b1.setBounds(100, 120, 100, 20);
                        b1.setBackground(Color.gray);
                        b1.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // TODO Auto-generated method stub

                                JFileChooser jfc=new JFileChooser();

                                try {
                                    jfc.setCurrentDirectory(new java.io.File("C:\\fichierRES"));
                                    jfc.setDialogTitle("Retreive File");
                                    jfc.showOpenDialog(null);
                                    File file=jfc.getSelectedFile();
                                    String filename=file.getName();
                                    if(filename!=null) {
                                        clientFtp.retreiveFile(filename);
                                        l1.append(clientFtp.getRep());
                                        l1.append("\n");
                                    }

                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }
                        });

                        JLabel label2 = new JLabel("Store File: ");
                        label2.setBounds(10,160,200,20);
                        label2.setForeground(Color.white);
                        JButton b2=new JButton("Store");
                        b2.setBounds(100, 160, 100, 20);
                        b2.setBackground(Color.gray);
                        b2.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // TODO Auto-generated method stub
                                JFileChooser jfc=new JFileChooser();
                                try {
                                    jfc.setCurrentDirectory(new java.io.File("C:\\dossier"));
                                    jfc.setDialogTitle("Store File");
                                    jfc.showOpenDialog(null);
                                    File file=jfc.getSelectedFile();
                                    String filename=file.getName();
                                    if(filename!=null) {
                                        clientFtp.storeFile(file);
                                        l1.append(clientFtp.getRep());
                                        l1.append("\n");
                                    }
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }
                        });

                        JLabel label3 = new JLabel("Delete File: ");
                        label3.setBounds(10,200,200,20);
                        label3.setForeground(Color.white);
                        JButton b3=new JButton("Delete");
                        b3.setBounds(100, 200, 100, 20);
                        b3.setBackground(Color.gray);
                        b3.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // TODO Auto-generated method stub
                                JFileChooser jfc=new JFileChooser();

                                try {
                                    jfc.setCurrentDirectory(new java.io.File("C:\\fichierRES"));
                                    jfc.setDialogTitle("FTP Server");
                                    jfc.showOpenDialog(null);
                                    File file=jfc.getSelectedFile();
                                    String filename=file.getName();
                                    if(filename!=null) {
                                        clientFtp.deleteFile(filename);
                                        l1.append(clientFtp.getRep());
                                        l1.append("\n");
                                    }
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }
                        });

                        JLabel label9 = new JLabel("Rename File: ");
                        label9.setBounds(10,240,200,20);
                        label9.setForeground(Color.white);
                        JButton b9=new JButton("Rename");
                        b9.setBounds(100, 240, 100, 20);
                        b9.setBackground(Color.gray);
                        b9.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String filename=JOptionPane.showInputDialog("Rename file");
                                try {
                                    String s =l1.getSelectedText();
                                    System.out.println(""+s);
                                    clientFtp.rename(s,filename);
                                    l1.append(clientFtp.getRep());
                                    l1.append("\n");
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                            }
                        });

                        JLabel label8 = new JLabel("Add file ");
                        label8.setBounds(10,280,200,20);
                        label8.setForeground(Color.white);
                        JButton b8=new JButton("Add");
                        b8.setBounds(100, 280, 100, 20);
                        b8.setBackground(Color.gray);
                        b8.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String filename=JOptionPane.showInputDialog("Enter new file");
                                try {
                                    clientFtp.addFile(filename);
                                    l1.append(clientFtp.getRep());
                                    l1.append("\n");
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });

                        JLabel label10 = new JLabel("Add folder ");
                        label10.setBounds(10,320,200,20);
                        label10.setForeground(Color.white);
                        JButton b11=new JButton("Add");
                        b11.setBounds(100, 320, 100, 20);
                        b11.setBackground(Color.gray);
                        b11.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String filename=JOptionPane.showInputDialog("Enter new folder");
                                try {
                                    clientFtp.addFolder(filename);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                                l1.append(clientFtp.getRep());
                                l1.append("\n");

                            }
                        });

                        JLabel label4 = new JLabel("List: ");
                        label4.setBounds(10,360,200,20);
                        label4.setForeground(Color.white);
                        JButton b4=new JButton("List");
                        b4.setBounds(100, 360, 100, 20);
                        b4.setBackground(Color.gray);
                        b4.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // TODO Auto-generated method stub

                                try {
                                    clientFtp.listFiles();
                                    l1.append(clientFtp.getRep());
                                    l1.append("\n");
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }
                        });


                        JLabel label5 = new JLabel("Current path");
                        label5.setBounds(230,440,100,20);
                        label5.setForeground(Color.white);
                        JButton b5=new JButton("PWD");
                        b5.setBounds(230, 470, 80, 20);
                        b5.setBackground(Color.gray);
                        b5.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    l1.append("Current directory :"+clientFtp.getCurrentDirectory());
                                    l1.append("\n");
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }
                        });

                        JLabel label6 = new JLabel("Choose a directory");
                        label6.setBounds(340,440,100,20);
                        label6.setForeground(Color.white);
                        JButton b6=new JButton("CWD");
                        b6.setBounds(340, 470, 100, 20);
                        b6.setBackground(Color.gray);
                        b6.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // TODO Auto-generated method stub

                                try {
                                    String option=JOptionPane.showInputDialog("Choose a directory:");
                                    if(option!=null) {
                                        clientFtp.Directory(option);
                                        l1.append(clientFtp.getRep());
                                        l1.append("\n");
                                    }
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }

                            }
                        });

                        JLabel label7 = new JLabel("List");
                        label7.setBounds(510,440,100,20);
                        label7.setForeground(Color.white);
                        JButton b7=new JButton("ls -al");
                        b7.setBounds(470, 470, 100, 20);
                        b7.setBackground(Color.gray);
                        b7.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // TODO Auto-generated method stub

                                try {
                                    clientFtp.list();
                                    l1.append(clientFtp.getRep());
                                    l1.append("\n");
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }
                        });

                        JButton b10=new JButton("logout");
                        b10.setBounds(340, 520, 100, 20);
                        b10.setBackground(Color.gray);
                        b10.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // TODO Auto-generated method stub

                                try {
                                    clientFtp.logout();
                                    l1.append(clientFtp.getRep());
                                    l1.append("\n");
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                                f2.hide();
                            }
                        });

                        f2.add(l2);
                        l2.setIcon( image3 );
                        f2.add(jsp);
                        f2.add(b1);
                        f2.add(b2);
                        f2.add(b3);
                        f2.add(b4);
                        f2.add(b5);
                        f2.add(b6);
                        f2.add(b7);
                        f2.add(b8);
                        f2.add(b9);
                        f2.add(b10);
                        f2.add(b11);
                        f2.add(label1);
                        f2.add(label2);
                        f2.add(label3);
                        f2.add(label4);
                        f2.add(label5);
                        f2.add(label6);
                        f2.add(label7);
                        f2.add(label8);
                        f2.add(label9);
                        f2.add(label10);



                        f2.add(panel2);

                        f2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        f2.setVisible(true);
                    }
                    }catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }});



        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
       /* Ftp tpFTP=new Ftp("127.0.0.1",21,"imen");
        tpFTP.connect();

        Scanner scanner= new Scanner(System.in);
        String reponse = scanner.nextLine();

        if (reponse.equals("PWD"))
        {
            tpFTP.pwd();
        }

        else
        if (reponse.equals("CWD"))
        {
            System.out.println("-Saisissez le nom du répertoire : ");
            String dir = scanner.nextLine();
            System.out.println(tpFTP.cwd(dir));
        }
        else
            if (reponse.equals("LIST"))
            {
                String list = tpFTP.list();
                System.out.println(list);
            }
            else
                if (reponse.equals("QUIT"))
                {
                    tpFTP.quit();
                    System.out.println("La session est déconnecter");
                }*/
    }
}
