import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

public class WeatherForeCast {
   public static void main(String[] args) throws JsonProcessingException {
       int noOfDayReport = 5;
        String forecastUrl = "https://api.weather.gov/points/";
        String foreCastGridDataPath = "/properties/forecastGridData";
        String dailyWeatherReportPath = "/properties/temperature/values";

        //System.out.println("URL : " + forecastUrl + args[0] + ',' + args[1]);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(forecastUrl + args[0] + ',' + args[1], String.class);
        //System.out.println(response.getBody());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        JsonNode foreCastGridDataNode = root.at(foreCastGridDataPath);
        //System.out.println("JSON PAth : " + foreCastGridDataNode);

        response = restTemplate.getForEntity(foreCastGridDataNode.asText(), String.class);
        //System.out.println(response.getBody());

        JsonNode dateWiseValuesNode = mapper.readTree(response.getBody());
        JsonNode dailyReportNode = dateWiseValuesNode.at(dailyWeatherReportPath);

        //System.out.println(dailyReportNode.asText());
        if (dailyReportNode.isArray())
        {
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime after5Days = today.plusDays(noOfDayReport + 1);
            for(JsonNode n : dailyReportNode) {
                String tempDateStr = n.at("/validTime").asText();
                int indexOfDirectionality = tempDateStr.indexOf("+");
                LocalDateTime tempLocalDateTime = LocalDateTime.parse(tempDateStr.replace(tempDateStr.substring(indexOfDirectionality), ""));
                if(after5Days.getDayOfYear() == tempLocalDateTime.getDayOfYear() &&
                        after5Days.getMonthValue() == tempLocalDateTime.getMonthValue() &&
                        after5Days.getYear() == tempLocalDateTime.getYear()) break;
                System.out.println("Date & Time : " + tempDateStr + " -> Temperature : " + n.at("/value").asText());
            }
        }
    }
}
