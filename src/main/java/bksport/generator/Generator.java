package main.java.bksport.generator;

import com.mchange.v2.c3p0.DataSources;
import main.java.bksport.models.Club;
import main.java.bksport.models.Player;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class Generator {
    static org.slf4j.Logger Logger = LoggerFactory.getLogger(Generator.class);
    public static DataSource dataSourceUnpooled = null;
    public static DataSource dataSourcePooled = null;

    public static void main(String[] args) throws IOException, SQLException {
        // Load properties file
        String filename = "database.properties";
        Properties prop = new Properties();
        InputStream input = Generator.class.getClassLoader().getResourceAsStream(filename);
        if(input==null){
            System.out.println("Sorry, unable to find " + filename);
            return;
        }
        else{
            prop.load(input);
        }

        // Init data pool
        dataSourceUnpooled = DataSources.unpooledDataSource(prop.getProperty("development.url"), prop.getProperty("development.username"), prop.getProperty("development.password"));
        dataSourcePooled = DataSources.pooledDataSource(dataSourceUnpooled);
        // Start
        Base.open(dataSourcePooled);
        // player
        List<Player> players = Player.findAll();
        PrintWriter out = new PrintWriter("D:\\players.txt");
        for(Player player: players) {
            out.println(playerGenerator(player));
        }
        out.close();

        //club
        List<Club> clubs = Club.findAll();
        PrintWriter outClub = new PrintWriter("D:\\clubs.txt");
        PrintWriter outStadium = new PrintWriter("D:\\stadiums.txt");
        PrintWriter outManager = new PrintWriter("D:\\managers.txt");
        for(Club club: clubs) {
            outClub.println(clubGenerator(club));
            if(club.get("stadium") != null) outStadium.println(stadiumGenerator(club));
            if(club.get("manager") != null) outManager.println(managerGenerator(club));
        }
        outClub.close();
        outStadium.close();
        outManager.close();
        Base.close();
    }


    static String managerGenerator(Club club){
        return String.format("" +
                "<!-- %s http://bk.sport.owl#%s -->\n" +
                "\n" +
                "    <owl:NamedIndividual rdf:about=\"http://bk.sport.owl#%s\">\n" +
                "        <rdf:type rdf:resource=\"http://bk.sport.owl#FootballManager\"/>\n" +
                "        <managerOf rdf:resource=\"http://bk.sport.owl#%s\"/>\n" +
                "        <protons:generatedBy rdf:resource=\"http://bk.sport.owl\"/>\n" +
                "        <protons:hasAlias>%s</protons:hasAlias>\n" +
                "        <protons:mainLabel>%s</protons:mainLabel>\n" +
                "        <rdfs:label>%s</rdfs:label>\n" +
                "    </owl:NamedIndividual>\n", club.get("id"), convert(club.get("manager")).trim(), convert(club.get("manager")).trim(),
                convert(club.get("common_name")).trim(), club.get("manager"),  club.get("manager"),  club.get("manager"));
    }

    static String stadiumGenerator(Club club){
        return String.format("" +
                "<!-- %s http://bk.sport.owl#%s-stadium -->\n" +
                "\n" +
                "  <owl:NamedIndividual rdf:about=\"http://bk.sport.owl#%s-stadium\">\n" +
                "    <rdf:type rdf:resource=\"http://bk.sport.owl#FootballStadium\"/>\n" +
                "    <rdfs:label xml:lang=\"en\"><![CDATA[%s]]></rdfs:label>\n" +
                "    <protons:mainLabel><![CDATA[%s]]></protons:mainLabel>\n" +
                "    <protons:generatedBy rdf:resource=\"http://bk.sport.owl\"/>\n" +
                "    <isLocatedIn rdf:resource=\"http://bk.sport.owl#%s\"/>\n" +
                "    <homeOf rdf:resource=\"&bksport;%s\"/>\n" +
                "    <buildIn rdf:datatype=\"&xsd;date\"><![CDATA[1923-01-01]]></buildIn>\n" +
                "  </owl:NamedIndividual>\n", club.get("id"), convert(club.get("stadium")).trim(),
                convert(club.get("stadium")).trim(), club.get("stadium"),
                club.get("stadium"), convert(club.get("stadium")).trim(), convert(club.get("common_name")).trim());
    }

    static String clubGenerator(Club club){
        return String.format(
                "  <!-- %s http://bk.sport.owl#%s -->\n" +
                "  <owl:NamedIndividual rdf:about=\"http://bk.sport.owl#%s\">\n" +
                "    <rdfs:label xml:lang=\"en\">%s</rdfs:label>\n" +
                "    <protons:mainLabel>%s</protons:mainLabel>\n" +
                "    <rdf:type rdf:resource=\"&bksport;ClubTeam\"/>\n" +
                "    <protons:generatedBy rdf:resource=\"http://bk.sport.owl\"/>\n" +
                "  </owl:NamedIndividual>\n" +
                "", club.get("id"), convert(club.get("common_name")).trim(),convert(club.get("common_name")).trim(),
                club.get("common_name").toString().trim(),club.get("common_name").toString().trim());
    }

    static String convert(Object obj){
        String str = obj.toString();
        return str.toLowerCase().replaceAll(" ", "-");
    }

    static String playerGenerator(Player player){
        return String.format("" +
                "<!-- %s http://bk.sport.owl#tarique-fosu -->\n" +
                "\n" +
                "  <owl:NamedIndividual rdf:about=\"http://bk.sport.owl#%s\">\n" +
                "    <rdfs:label xml:lang=\"en\">%s</rdfs:label>\n" +
                "    <protons:mainLabel>%s</protons:mainLabel>\n" +
                "    <rdf:type rdf:resource=\"&bksport;%s\"/>\n" +
                "    <protons:generatedBy rdf:resource=\"http://bk.sport.owl\"/>\n" +
                "    <playFor rdf:resource=\"&bksport;%s\"/>\n" +
                "  </owl:NamedIndividual>", player.get("id").toString(),
                convert(player.get("common_name")).trim(), player.get("common_name").toString().trim(), player.get("common_name").toString().trim(),
                player.get("position"), convert(player.get("current_club")).trim());
    }


}
