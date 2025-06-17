package fn10.bedrockr.windows.utils;

import java.util.Random;

public class Greetings { // no, the class is not welcoming you, it is for those greetings that appear 
    public static class Greeting {
        
        public String Text;
        public int Size;

        public Greeting(String Text, int Size) {
            this.Text = Text;
            this.Size = Size;
        }
        
    }
    protected static Greeting[] GREETINGS = {
        new Greeting("Welcome back!",25),
        new Greeting("Welcome to BR, how may I serve you today?",15),
        new Greeting("Could I interest you in some text-based programming?",14),
        new Greeting("Yo.",25),
        new Greeting("bugrockR",25),
        new Greeting("This isnt MCreator...",20),
    };

    

    public static Greeting GetGreeting() {
        var mx = GREETINGS.length;
        var rnd = new Random();

        return GREETINGS[rnd.nextInt(0, mx)];
    }

}
