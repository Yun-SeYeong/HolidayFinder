package service;

import dto.HolidayDto;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TerminalPrintService {

  /**
   * 로고 출력
   */
  public static void printLogo() {
    System.out.print("\033[H\033[2J");
    System.out.println("\n   / / / /___  / (_)___/ /___ ___  __   / ____(_)___  ____/ /__  _____\n"
        + "  / /_/ / __ \\/ / / __  / __ `/ / / /  / /_  / / __ \\/ __  / _ \\/ ___/\n"
        + " / __  / /_/ / / / /_/ / /_/ / /_/ /  / __/ / / / / / /_/ /  __/ /\n"
        + "/_/ /_/\\____/_/_/\\__,_/\\__,_/\\__, /  /_/   /_/_/ /_/\\__,_/\\___/_/\n"
        + "                                                               (v1.0)");
    System.out.flush();
  }

  /**
   * 메뉴 출력
   */
  public static void printMenus() {
    System.out.println(" 메뉴를 선택해주세요.");
    System.out.println(" 1. 공휴일 조회하기");
    System.out.println(" 2. 올해 공휴일이 가장 많은 달 조회하기");
    System.out.println(" 3. 공휴일 통계 조회");
    System.out.printf(" 4. 캐시 삭제 (%.2fKB)\n", (new File("cache")).length() / 1000f);
    System.out.println(" 5. 종료");
    System.out.print(" > ");
  }

  /**
   * 옵션 출력
   */
  public static void printOptions() {
    System.out.println("옵션을 선택해주세요.");
    System.out.println("1. 이번달");
    System.out.println("2. 올해");
    System.out.print("> ");
  }

  /**
   * 커서 리셋
   */
  public static void resetCursor() {
    System.out.print("\033[7;0H\033[0J");
    System.out.flush();
  }

  /**
   * 공휴일 정보 표로 출력
   * @param holidayDtoList 조회할 공휴일 리스트
   */
  public static void printHolidayDtoTable(List<HolidayDto> holidayDtoList) {
    System.out.print("\033[0;0H\033[2J");
    System.out.println(
        "┌──────────────────────────────┬──────────────────────────────┬──────────────────────────────┐"
    );
    System.out.printf("│%-" + getColumnSize("날짜", 30) + "s│%-" + getColumnSize(
        "유형", 30) + "s│%-" + getColumnSize("휴일명", 30)
        + "s│\n", "날짜", "휴무", "휴일명");

    holidayDtoList.forEach(holidayDto -> {
      System.out.println(
          "├──────────────────────────────┼──────────────────────────────┼──────────────────────────────┤"
      );
      String isHoliday = holidayDto.getHoliday() ? "Y" : "N";
      String locDate = LocalDate.parse(holidayDto.getLocDate(),
          DateTimeFormatter.ofPattern("yyyyMMdd")).toString();

      System.out.printf("│%-" + getColumnSize(locDate, 30)
          + "s│%-" + getColumnSize(isHoliday, 30)
          + "s│%-" + getColumnSize(holidayDto.getDateName(), 30)
          + "s│\n", locDate, isHoliday, holidayDto.getDateName());
    });

    System.out.println(
        "└──────────────────────────────┴──────────────────────────────┴──────────────────────────────┘"
    );

  }

  /**
   * 월별 공휴일 정보 차트출력
   * @param holidayMap 조회할 공휴일 정보
   */
  public static void printHolidayDtoChart(Map<String, List<HolidayDto>> holidayMap) {
    System.out.print("\033[H\033[2J");
    System.out.println(
        "┌───────────────┬─────┬────────────────────────────────────────────────────────────┐"
    );
    System.out.printf("│%-" + getColumnSize("날짜", 15) + "s│%-"
        + getColumnSize("개수", 5) + "s│%-"
        + getColumnSize("차트", 60) + "s│\n", "날짜", "개수", "차트");

    holidayMap
        .entrySet()
        .stream().sorted(Entry.comparingByKey())
        .forEach(entry -> {
          System.out.println(
              "├───────────────┼─────┼────────────────────────────────────────────────────────────┤"
          );
          String bar = String.join("", Collections.nCopies(entry.getValue().size() * 10, "■"));
          String locDate = entry.getKey().substring(0, 4) + "년 "
              + entry.getKey().substring(4) + "월";

          System.out.printf("│%-" + getColumnSize(locDate, 15)
              + "s│%-" + getColumnSize(String.valueOf(entry.getValue().size()), 5)
              + "s│%-" + getColumnSize(bar, 60) + "s│\n", locDate, entry.getValue().size(), bar);
        });

    System.out.println(
        "└───────────────┴─────┴────────────────────────────────────────────────────────────┘"
    );

  }

  /**
   * max에서 한글 수 만큼 뺀 길이 계산
   * @param kor 문자
   * @param max 최대 문자열
   * @return max에서 한글 수 만큼 뺀 길이
   */
  private static int getColumnSize(String kor, int max) {
    int cnt = max;
    for (int i = 0; i < kor.length(); i++) {
      if ((kor.charAt(i) >= '가' && kor.charAt(i) <= '힣')) {
        cnt--;
      }
    }
    return cnt;
  }
}
