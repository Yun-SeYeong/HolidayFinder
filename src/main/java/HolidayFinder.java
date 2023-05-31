import dto.MaxCountHolidayDto;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import repository.HolidayRepository;
import service.TerminalPrintService;

public class HolidayFinder {

  private static final BufferedReader bufferedReader = new BufferedReader(
      new InputStreamReader(System.in));
  private static final String OPEN_API_KEY = "922T%2FggEzFLCf3DO224yyCR0geVmU6X1KwM%2FDq%2Bp%2FcW6UueOtxRERU7eEJgiOnIyv5MvbcsOJd6HeRdYZwO1Hw%3D%3D";

  public static void main(String[] args)
      throws IOException, ParserConfigurationException, ClassNotFoundException, SAXException {
    HolidayRepository holidayRepository = new HolidayRepository(OPEN_API_KEY);

    // 프로그램이 종료될 때 까지 반복한다.
    while (true) {
      // 화면에 Logo를 출력한다.
      TerminalPrintService.printLogo();

      // 선택된 메뉴가 종료인지 확인하고 종료한다.
      int menuNum = selectMenu();
      if (menuNum == 5) {
        return;
      }

      // 오늘 날짜의 연도와 월을 조회한다.
      LocalDateTime now = LocalDateTime.now();
      String year = String.format("%04d", now.getYear());
      String month = String.format("%02d", now.getMonthValue());

      // 메뉴에 따라 기능을 처리한다.
      switch (menuNum) {
        case 1:
          int option = selectOption();
          TerminalPrintService.resetCursor();
          // 옵션에 따라 이번달 혹은 올해 데이터를 조회한다.
          if (option == 1) {
            TerminalPrintService.printHolidayDtoTable(holidayRepository.getHoliday(year, month));
          } else {
            TerminalPrintService.printHolidayDtoTable(holidayRepository.getHolidayByYear(year));
          }
          break;
        case 2:
          // 올해 가장 휴일이 많은 달을 조회한다.
          MaxCountHolidayDto maxHoliday = holidayRepository.getMaxCountHolidayByYear(year);
          TerminalPrintService.printHolidayDtoTable(maxHoliday.getHolidayDtos());
          System.out.println("가장 많은 달은 " + maxHoliday.getMaxYear() + "월 입니다.");
          break;
        case 3:
          // 올해 월별 휴일 수를 차트로 보여준다.
          TerminalPrintService.printHolidayDtoChart(holidayRepository.getHolidayMapByYear(year));
          break;
        case 4:
          // 캐시파일 삭제
          holidayRepository.deleteCache();
          System.out.println("캐시가 삭제 되었습니다.");
          break;
      }
      System.out.println("돌아가려면 Enter를 입력해주세요.");
      bufferedReader.readLine();
    }

  }

  /**
   * 메뉴선택 화면을 출력하고 정상적으로 메뉴가 출력될 때까지 문자를 입력받는다.
   */
  private static int selectMenu() throws IOException {
    String line;
    do {
      TerminalPrintService.resetCursor();
      TerminalPrintService.printMenus();
      line = bufferedReader.readLine();
    } while (!line.matches("^[1-5]$"));
    return Integer.parseInt(line);
  }

  /**
   * 옵션선택 화면을 출력하고 정상적으로 메뉴가 출력될 때까지 문자를 입력받는다.
   */
  private static int selectOption() throws IOException {
    String line;
    do {
      TerminalPrintService.resetCursor();
      TerminalPrintService.printOptions();
      line = bufferedReader.readLine();
    } while (!line.matches("^[1-2]$"));
    return Integer.parseInt(line);
  }
}
