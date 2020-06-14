import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class Ftp {
    private Socket connectionSocket;
    private PrintStream outputStream;
    private BufferedReader inputStream;
    private BufferedInputStream reader;

    private Socket pasvSocket;
    private BufferedWriter writer, writerData;
    public static boolean logIn = false;

    private String rep;
    private long restartPoint = 0L;

    /****établir une connexion avec le serveur FTP **/
    public boolean connect(String host, int port)throws UnknownHostException, IOException
    {
        /*création de la socket de connexion, qui va permettre l'echange de donnée avec le serveur*/
        connectionSocket = new Socket(host, port);
        /*pour envoyer les données au serveur */
        outputStream = new PrintStream(connectionSocket.getOutputStream());
        /*pour lire la réponse du serveur*/
        inputStream = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

        reader = new BufferedInputStream(connectionSocket.getInputStream());
        writer = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
        /****tester si la réponse du serveur est positif sinon on annulée la connexion**/
        if ( Integer.parseInt(getFullServerReply().substring(0, 3))!=220){
            disconnect();
            return false;
        }
        return true;
    }

    /**** login sur le serveur ftp avec un userneme et un password **/
    public boolean login(String username, String password)throws IOException
    {
        /*Envoyer le nom du user et le passeword au serveur FTP*/
        int response = executeCommand("USER " + username);
        if (response!=331) {
            setRep("Erreur de connexion avec le compte utilisateur : \n" + response);
            return false;
        }
        response = executeCommand("PASS " + password);
        if(response!=230) {
            logIn =false;
            return false;
        }
        else {
            logIn =true;
            return true;
        }
    }

    /****Annuler la demande de connexion**/
    public void disconnect()
    {
        if (outputStream != null) {
            try {
                if (logIn) { logout(); };
                outputStream.close();
                inputStream.close();
                connectionSocket.close();
            } catch (IOException e) {}

            outputStream = null;
            inputStream = null;
            connectionSocket = null;
        }
    }

    /****Se déconnecte de l'host sur lequel on est actuellement connecté**/
    public boolean logout()throws IOException
    {
        /*envoyer la commande qui au serveur FTP qui permet de se deconnecter de ce dernier*/
        int response = executeCommand("Logout");
        if(response==221) {
            logIn =false;
            return false;
        }else{
            logIn =true;
            return true;
        }
    }

   /****Exécute une commande sur le FTP**/
    public int executeCommand(String command)throws IOException
    {
        outputStream.println(command);
        return  Integer.parseInt(getFullServerReply().substring(0, 3));
    }

    /****retourne la dèrnière ligne complete de réponse du serveur**/
    private String getFullServerReply() throws IOException
    {
        String reply;
        do {
            reply = inputStream.readLine();
            debugPrint(reply);
            setRep(reply);
        } while(!(Character.isDigit(reply.charAt(0)) &&
                Character.isDigit(reply.charAt(1)) &&
                Character.isDigit(reply.charAt(2)) &&
                reply.charAt(3) == ' '));

        return reply;
    }

    private void debugPrint(String message)
    {
        System.out.println(message);
        setRep(message);
    }

    private void setRep(String rep) {
        this.rep=rep;
    }

    public String getRep() {
        return rep;
    }

    private void addRep(String s) {
        this.rep+=" \n "+s;
    }

    /****affiche le chemin courrant sur le FTP**/
    public String getCurrentDirectory()throws IOException
    {
        String response = getExecutionResponse("PWD");

        return response;
    }
    public String getExecutionResponse(String command)throws IOException
    {
        System.out.println(command);
        outputStream.println(command);
        return getFullServerReply();
    }

    /****Aller dans la répertoire choisi**/

    public boolean Directory(String directory)throws IOException
    {
        int response = executeCommand("cwd " + directory);
        if(response >= 200 && response < 300) return true;
        else return false;
    }

    /****affiche le contenu d'un répertoire du serveur*/
    public String list() throws IOException{

        typeASCII();
        openPasv();
        createDataSocket();
        executeCommand("LIST");
        return readData();
    }

    private String readData() throws IOException{

        String response = "";
        byte[] b = new byte[1024];
        int stream;

        while((stream = reader.read(b)) != -1){
            response += new String(b, 0, stream);
        }
        setRep(response);
        return response;
    }

    private void createDataSocket() throws IOException{

        reader = new BufferedInputStream(pasvSocket.getInputStream());
        writerData = new BufferedWriter(new OutputStreamWriter(pasvSocket.getOutputStream()));
    }
    /****entre en mode passif*/
    private boolean openPasv() throws IOException
    {
        String tmp = getExecutionResponse("PASV");
        String pasv = excludeCode(tmp);
        //On récupère l'IP et le PORT pour la connection
        pasv = pasv.substring(pasv.indexOf("(")+1,pasv.indexOf(")"));
        String[] splitedPasv = pasv.split(",");
        int port1 = Integer.parseInt(splitedPasv[4]);
        int port2 = Integer.parseInt(splitedPasv[5]);

        /*Le port est un entier de type int mais cet entier est découpé en deux
        la première partie correspond aux 4 premiers bits de l'octet et la deuxième au 4 derniers
        Il faut donc multiplier le premier nombre par 256 et l'additionner au second
        pour avoir le numéro de ports défini par le serveur*/
        int port = (port1*256)+port2;

        //L'adresse IP est séparée par des virgules
        //on les remplace donc par des points...
        String ip = splitedPasv[0]+"."+splitedPasv[1]+"."+splitedPasv[2]+"."+splitedPasv[3];

        pasvSocket = new Socket(ip,port);
        int response=Integer.parseInt(tmp.substring(0,3));
        if(response >= 200 && response < 300) return true;
        else return false;

    }
    private String excludeCode(String response)
    {
        if (response.length() < 5) return response;
        return response.substring(4);
    }

    /****passe en mode de communication ASCII*/
    public void typeASCII() throws IOException{
        executeCommand("TYPE ASCII");

    }

    /****le client demande a récuperer un fichier qui est sur le serveur*/
    public boolean retreiveFile( String fileName)throws IOException
    {
        /*On ouvre le fichier en local*/
        RandomAccessFile outfile = new RandomAccessFile(fileName, "rw");

        /* Converti le RandomAccessFile en un OutputStream*/
        FileOutputStream filestrm = new FileOutputStream(outfile.getFD());
        boolean success = executeDataCommand("RETR "+fileName, filestrm);

        outfile.close();
        return success;
    }

    public boolean executeDataCommand(String command, OutputStream out) throws IOException
    {
        if (!setupDataPasv(command)) return false;
        InputStream inputStrm = pasvSocket.getInputStream();
        sizeData(inputStrm,out);
        inputStrm.close();
        pasvSocket.close();
        int response =  Integer.parseInt(getFullServerReply().substring(0, 3));
        if(response >= 200 && response < 300)
            return true;
        else return false;
    }
    private void sizeData(InputStream in, OutputStream out) throws IOException
    {
        byte b[] = new byte[4096];
        int donné;
        /*Stock les donnés dans un fichier*/
        while ((donné = in.read(b)) > 0)
        {
            out.write(b, 0, donné);
        }
    }
    private boolean setupDataPasv(String command) throws IOException
    {
        if (!openPasv()) return false;
        /*Lance le mode binaire pour la récéption des donnés*/
        outputStream.println("TYPE i");
        int response= Integer.parseInt(getFullServerReply().substring(0, 3));
        if (!(response >= 200 && response < 300))
        {
            debugPrint("Could not set transfer type");
            return false;
        }

        // Si l'on a un point de restart
        if (restartPoint != 0) {
            outputStream.println("rest " + restartPoint);
            restartPoint = 0;
            // TODO: Interpret server response here
            Integer.parseInt(getFullServerReply().substring(0, 3));
        }
        System.out.println(command);
        outputStream.println(command);

        int response2 = Integer.parseInt(getFullServerReply().substring(0, 3));
        if(response2 >= 100 && response2 < 200)
            return true;
        else return false;
    }

    /****le client demande a déposer un fichier sur le serveur*/
    public void storeFile(File fileName)throws IOException
    {
        executeCommand("TYPE ASCII");
        openPasv();

        writerData = new BufferedWriter(new OutputStreamWriter(pasvSocket.getOutputStream()));
        reader = new BufferedInputStream(pasvSocket.getInputStream());

        executeCommand("STOR "+fileName.getName());
        System.out.println(fileName.getName());

        /*la taille d'un fichier transféré*/

       byte[] bytes = new byte[ 16*1024];

        InputStream in = new FileInputStream(fileName);


        OutputStream outputStream = pasvSocket.getOutputStream();

        int size;
        while ((size = in.read(bytes)) > 0)
        {
            outputStream.write(bytes, 0, size);
        }
        pasvSocket.close();
        outputStream.close();
    }

    /**** Supression d'un fichier sur le serveur*/
    public boolean deleteFile(String fileName)throws IOException
    {
        int response = executeCommand("dele " + fileName);
        if(response==250)
        {
            addRep("File deleted ==> " +fileName);
            return true;
        }else
            return false;
    }

    /****Affichage du contenu d'un répértoire du serveur**/
    public String listFiles() throws IOException
    {
        return listFiles("");
    }
    public String listFiles(String params) throws IOException
    {
        StringBuffer files = new StringBuffer();
        StringBuffer dirs = new StringBuffer();
        if (!getAndParseDirList(params, files, dirs))
        {
            debugPrint("Error getting file list");
        }

        return files.toString();
    }

    private boolean getAndParseDirList(String params, StringBuffer files, StringBuffer directory)throws IOException
    {
        /*  initialiser à 0 ls variables de retoure*/
        files.setLength(0);
        directory.setLength(0);

        /*Namelist command */
        String shortList = processFileListCommand("NLST " + params);
        /*list command*/
        String longList = processFileListCommand("LIST " + params);

        /* On couper ls lignes*/
        StringTokenizer sList = new StringTokenizer(shortList, "\n");
        StringTokenizer lList = new StringTokenizer(longList, "\n");

        /*Ls deux lists ont le méme nombres ds lignes*/
        while ((sList.hasMoreTokens()) && (lList.hasMoreTokens())) {
            String sString = sList.nextToken();
            String lString = lList.nextToken();

            if (lString.length() > 0) {
                if (lString.startsWith("d")) {addRep("directory : "+sString);
                } else if (lString.startsWith("-")) {
                    addRep("file :"+sString);
                }
            }
        }

        if (files.length() > 0)  {  files.setLength(files.length()  ); }
        if (directory.length() > 0)  {  directory.setLength(directory.length() ); }

        return true;
    }

    private String processFileListCommand(String command)throws IOException
    {
        StringBuffer answer = new StringBuffer();
        String answerString;

        boolean success = executeDataCommand(command, answer);
        if (!success)
        {
            return "";
        }
        answerString = answer.toString();
        if(answer.length() > 0)
        {
            return answerString.substring(0, answer.length() - 1);
        }
        else
        {
            return answerString;
        }
    }
    public boolean executeDataCommand(String command, StringBuffer sb)throws IOException
    {
        if (!setupDataPasv(command)) return false;
        InputStream in = pasvSocket.getInputStream();
        sizeData(in,sb);
        in.close();
        pasvSocket.close();
        int response= Integer.parseInt(getFullServerReply().substring(0, 3));
        if(response >= 200 && response < 300) return true;
        else return false;
    }
    private void sizeData(InputStream in, StringBuffer sb) throws IOException
    {
        byte b[] = new byte[4096];
        int donné;

        /*Stock les données dans un buffer*/
        while ((donné = in.read(b)) > 0)
        {
            sb.append(new String(b, 0, donné));
        }
    }

    /****new file**/
    public  boolean addFile(String fileName)throws IOException
    {
        openPasv();
        createDataSocket();

       int response =  executeCommand("STOR "+fileName);
        if (response==150)
        {
            addRep("File created==> "+fileName);
            return true;
        }else
        pasvSocket.close();
        inputStream.close();
        outputStream.close();
        Integer.parseInt(getFullServerReply().substring(0, 3));
        return false;
    }

    /****new folder**/
    public  boolean addFolder(String fileName) throws  IOException
    {
       int response= executeCommand("MKD "+fileName);
        if (response==257)
        {
            addRep("Folder created==> "+fileName);
            return true;
        }else
            return false;
    }


    /****Rename**/
    public void rename(String oldName ,String newname)throws  IOException
    {
        executeCommand("RNFR "+oldName);
     // JOptionPane pane = new JOptionPane();
      executeCommand("RNTO "+newname);


    }


}
