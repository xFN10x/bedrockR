package fn10.bedrockr.utils;

import java.util.Random;

public class Greetings { // no, the class is not welcoming you, it is for those greetings that appear 
    public record Greeting(String Text, int Size) { }
    
    protected static Greeting[] GREETINGS = {
        new Greeting("Welcome back!",25),
        new Greeting("Welcome to BR, how may I serve you today?",15),
        new Greeting("Yo.",25),
        new Greeting("bugrockR",25),
        new Greeting("This isnt MCreator...",20),
        new Greeting("Now in alpha!",24),
        new Greeting("bed-rocking you to sleep tonight",24),
        new Greeting("Rock Bedrock!",25),
        new Greeting("bedrockR, my rival",26),
        new Greeting("PSPTools, now out!",26),
        new Greeting("Remember major updates?",26),
            new Greeting("I love when Microsoft breaks things",24),
            new Greeting("I've been waiting so long...",24),
            new Greeting("Now for 26.3! Wait, no- 26.30... wait 1.26.30? HELP",23),
            new Greeting("Now for 26.60!",25),
            new Greeting("Hackclub!",25),

            // i said, "the next 5 messages sent to this channel will be added to bedrockR as welcome texts (basically mc splash texts"
            //but they didn't read the message... 
            new Greeting("I think @trkl is very handsome -camiel",22),
            new Greeting("wanna become a philantropist? donate to poorer? (for instance me??) -Lix",22),
            new Greeting("the goat -jerry",22),
            new Greeting("okay bro -Lix",22),
            new Greeting("wow how could you say that -camiel",22),
            
    };

    

    public static Greeting GetGreeting() {
        var mx = GREETINGS.length;
        var rnd = new Random();

        return GREETINGS[rnd.nextInt(0, mx)];
    }

}
