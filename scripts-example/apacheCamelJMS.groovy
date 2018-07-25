@Grab('org.apache.camel:camel-core:2.22.0')
@Grab('org.apache.camel:camel-slack:2.22.0')
@Grab('org.apache.camel:camel-groovy:2.22.0')
@Grab('org.apache.camel:camel-jms:2.22.0')
@Grab('org.apache.camel:camel-csv:2.22.0')
@Grab('org.apache.activemq:activemq-camel:5.15.4')
@Grab('org.slf4j:slf4j-simple:1.7.25')
import org.apache.camel.*
import org.apache.camel.impl.*
import org.apache.camel.builder.*
import org.apache.camel.component.slack.*
import org.apache.activemq.camel.component.*
import org.apache.camel.model.dataformat.*
import org.apache.camel.util.jndi.JndiContext
import java.util.regex.Matcher;
import java.util.regex.Pattern;

def camelContext = new DefaultCamelContext()

camelContext.addRoutes(new RouteBuilder() {
    def void configure() {
        CsvDataFormat csv = new CsvDataFormat();
        csv.setDelimiter(";");
        //from("activemq:bme280")
	from("timer:timi?period=1000")
	  .transform()
	  .simple('AAA')
          .unmarshal(csv)
          .setHeader("time").groovy("new java.text.SimpleDateFormat('yyyyMMddHHmmss').format(new Date(Long.valueOf(body[0][1])*1000L))")
          .log(ColorCodes.parseColors('Sensor: :blue,n:${body[0][0]}[RC] chipId: :green,n:${body[0][10]}[RC] time: ${in.header.time} t: :cyan,n:${body[0][2]} °C[RC]'))
    }
})

def activeMQ = ActiveMQComponent.activeMQComponent(System.getenv('ACTIVE_MQ_URL'))
activeMQ.setUserName(System.getenv('ACTIVE_MQ_USERNAME'))
activeMQ.setPassword(System.getenv('ACTIVE_MQ_PASSWORD'))
camelContext.addComponent('activemq', activeMQ)
camelContext.start()

addShutdownHook{ camelContext.stop() }
synchronized(this){ this.wait() }

public class ColorCodes {

    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30;40;1m";
    public static final String RED = "\u001B[31;40;1m";
    public static final String GREEN = "\u001B[32;40;1m";
    public static final String YELLOW = "\u001B[33;40;1m";
    public static final String BLUE = "\u001B[34;40;1m";
    public static final String PURPLE = "\u001B[35;40;1m";
    public static final String CYAN = "\u001B[36;40;1m";
    public static final String WHITE = "\u001B[37;40;1m";

    /**
     * Parses a string with ANSI color codes based on the input
     * @param input the input string
     * @return the parsed ANSI string
     */
    public static String parseColors(String input){
        String ret = input;
        Pattern regexChecker = Pattern.compile(":\\S+,\\S+:");
        Matcher regexMatcher = regexChecker.matcher(input);
        while(regexMatcher.find()){
            if(regexMatcher.group().length() != 0){
                String sub = regexMatcher.group().trim();
                sub = sub.replace(":", "");
                String[] colors = sub.split(",");

                ret = (colors[1].equalsIgnoreCase("N")) ?
                        ret.replace(
                            regexMatcher.group().trim(),
                            "\u001B[3" + getColorID(colors[0]) + ";1m"
                        )
                      :
                        ret.replace(
                            regexMatcher.group().trim(),
                            "\u001B[3" + getColorID(colors[0]) +
                            ";4" + getColorID(colors[1]) + ";1m"
                        );

                ret = ret.replace("[RC]", ColorCodes.RESET);
            }
        }
        ret = ret + ColorCodes.RESET; return ret;
    }

    /**
     * Internal function for getting a colors value
     * @param color The color as test
     * @return The colors integral value
     */
    private static int getColorID(String color){
        if(color.equalsIgnoreCase("BLACK")){
            return 0;
        }else if(color.equalsIgnoreCase("RED")){
            return 1;
        }else if(color.equalsIgnoreCase("GREEN")){
            return 2;
        }else if(color.equalsIgnoreCase("YELLOW")){
            return 3;
        }else if(color.equalsIgnoreCase("BLUE")){
            return 4;
        }else if(color.equalsIgnoreCase("MAGENTA")){
            return 5;
        }else if(color.equalsIgnoreCase("CYAN")){
            return 6;
        }else if(color.equalsIgnoreCase("WHITE")){
            return 7;
        }

        return 7;
    }


}
