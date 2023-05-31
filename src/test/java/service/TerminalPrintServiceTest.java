package service;

import static org.junit.jupiter.api.Assertions.*;

import dto.HolidayDto;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import repository.HolidayRepository;

class TerminalPrintServiceTest {

  @Test
  @DisplayName(value = "Print table test")
  public void printTableTest()
      throws ParserConfigurationException, IOException, ClassNotFoundException, SAXException {
    // given
    String key = "922T%2FggEzFLCf3DO224yyCR0geVmU6X1KwM%2FDq%2Bp%2FcW6UueOtxRERU7eEJgiOnIyv5MvbcsOJd6HeRdYZwO1Hw%3D%3D";
    String year = "2023";
    String month = "05";
    HolidayRepository holidayRepository = new HolidayRepository(key);


    // when & Then
    TerminalPrintService.printHolidayDtoTable(holidayRepository.getHoliday(year, month));
  }

}