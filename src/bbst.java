import edu.ufl.ads.proj.event.EventCounter;

import java.io.*;

public class bbst {
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Invalid input");
            System.out.println("$java bbst file-name");
            System.exit(1);
        }
        String filename = args[0];
        EventCounter em = new EventCounter();
        //Initializing the tree
        String line = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            line = reader.readLine();
            int n = Integer.parseInt(line);
            em.initialize(reader, n);
        } catch (IOException x) {
            System.out.println("File Not Found");
            System.exit(1);
        } catch(NumberFormatException e){
            System.out.println("Failed to parse Number in command: "+ line);
            System.exit(1);
        }
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
            do{
                line = reader.readLine();
                String[] command = line.split(" ");
                if(command.length == 3 && command[0].startsWith("increase")){
                    em.increase(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                } else if(command.length == 3 && command[0].startsWith("reduce")){
                    em.reduce(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                } else if(command.length == 3 && command[0].startsWith("inrange")){
                    em.inrange(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                } else if(command.length == 2 && command[0].startsWith("count")){
                    em.count(Integer.parseInt(command[1]));
                } else if(command.length == 2 && command[0].startsWith("next")){
                    em.next(Integer.parseInt(command[1]));
                } else if(command.length == 2 && command[0].startsWith("previous")){
                    em.previous(Integer.parseInt(command[1]));
                } else if(command.length == 1 && command[0].startsWith("quit")) {
                    System.exit(0);
                } else if(command.length == 1 && command[0].startsWith("verify")) {
                    em.verify();
                }
                else System.out.println("Invalid Command: "+ line);

            }while(true);
        } catch(NumberFormatException e){
            System.out.println("Failed to parse Number in command: "+ line);
        } catch (IOException e){
            System.out.println("Failed to read Input Stream");
        }


    }
}
